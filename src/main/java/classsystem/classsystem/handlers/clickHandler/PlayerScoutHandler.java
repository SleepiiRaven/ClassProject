package classsystem.classsystem.handlers.clickHandler;

import classsystem.classsystem.ClassSystem;
import classsystem.classsystem.CooldownManager;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class PlayerScoutHandler extends PlayerClassTemplate {
    ClassSystem plugin = ClassSystem.getInstance();
    CooldownManager cooldownManager = plugin.getCdInstance();

    @Override
    public void onTrigger(PlayerInteractEvent e) {
        ClassSystem plugin = ClassSystem.getInstance();
        if (e.getHand() != EquipmentSlot.HAND) return;
        Player p = e.getPlayer();
        String pUUID = p.getUniqueId().toString();
        Location pLocation = p.getLocation();
        double dmgModifier = plugin.getConfig().getInt(pUUID + ".dmgMultiplier");
        double rangeModifier = plugin.getConfig().getInt(pUUID + ".rangeMultiplier");
        double kbModifier = plugin.getConfig().getInt(pUUID + ".kbMultiplier");
        double cdModifier = plugin.getConfig().getInt(pUUID + ".cdMultiplier");
        if (p.getInventory().getItemInMainHand().getType().equals(Material.BOW)) {
            if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
                if (p.isSneaking()) {
                    if (!(cooldownManager.isCooldownDone(p.getUniqueId(), "Hunter's Mark"))) return;
                    if (hunterMark(plugin, p, pLocation, rangeModifier, cdModifier, cooldownManager)) return;
                } else {
                }
            }
        }
        if (p.getInventory().getItemInMainHand().getType().equals(Material.ARROW)) {
            if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
                if (p.isSneaking()) {

                } else {
                    if (!(cooldownManager.isCooldownDone(p.getUniqueId(), "Evade"))) return;
                    evade(p, pLocation, kbModifier, cdModifier);
                }
            }
        }
    }

    private void evade(Player p, Location pLocation, double kbModifier, double cdModifier) {
        //region Evade
        long cooldown = (long) (10000 / cdModifier);
        cooldownManager.setCooldownFromNow(p.getUniqueId(), "Evade", cooldown);
        summonCircle(pLocation, 4, Particle.FIREWORKS_SPARK);
        Vector jumpSpeed = new Vector(4, 3.5, 4).multiply(kbModifier);
        Vector pLooking = pLocation.getDirection();
        Vector jumpVelocity = pLooking.multiply(jumpSpeed);
        p.setVelocity(jumpVelocity);
        p.playSound(pLocation, Sound.ENTITY_ENDER_DRAGON_FLAP, 1, 1);
        //endregion
    }
    private static boolean hunterMark(ClassSystem plugin, Player p, Location pLocation, double rangeModifier, double cdModifier, CooldownManager cooldownManager) {
        //region Hunter's Mark
        long cooldown = (long) (5000 / cdModifier);
        double range = 32 * rangeModifier;
        RayTraceResult traceResult = p.getWorld().rayTraceEntities(p.getEyeLocation(), p.getEyeLocation().getDirection(), range, (entity -> {
            return entity != p;
        }));
        if (traceResult == null || !(traceResult.getHitEntity() instanceof LivingEntity)) return true;
        cooldownManager.setCooldownFromNow(p.getUniqueId(), "Hunter's Mark", cooldown);
        LivingEntity livingEntity = (LivingEntity) traceResult.getHitEntity();
        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 300, 1));
        livingEntity.addScoreboardTag("Marked");
        p.playSound(pLocation, Sound.BLOCK_CONDUIT_ACTIVATE, 1, 1);
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskLater(plugin, () -> {
            livingEntity.removeScoreboardTag("Marked");
        }, 300L);
        return false;
        //endregion
    }

    public void summonCircle(Location location, int size, Particle particle) {
        for (int d = 0; d <= 90; d += 1) {
            Location particleLoc = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());
            // Cosine for X
            particleLoc.setX(location.getX() + Math.cos(d) * size);
            // Sine for Z
            particleLoc.setZ(location.getZ() + Math.sin(d) * size);
            location.getWorld().spawnParticle(particle, particleLoc, 1);
        }
    }
}
