package classsystem.classsystem.handlers.clickHandler;

import classsystem.classsystem.ClassSystem;
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
        if (p.getInventory().getItemInMainHand().getType().equals(Material.BOW)) {
            if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
                if (p.isSneaking()) {
                    //region Hunter's Mark
                    double range = 32 * rangeModifier;
                    RayTraceResult traceResult = p.getWorld().rayTraceEntities(p.getEyeLocation(), p.getEyeLocation().getDirection(), range, (entity -> {
                        return entity != p;
                    }));
                    if (traceResult == null || !(traceResult.getHitEntity() instanceof LivingEntity)) return;
                    LivingEntity livingEntity = (LivingEntity) traceResult.getHitEntity();
                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 300, 1));
                    livingEntity.addScoreboardTag("Marked");
                    p.playSound(pLocation, Sound.BLOCK_CONDUIT_ACTIVATE, 1, 1);
                    BukkitScheduler scheduler = Bukkit.getScheduler();
                    scheduler.runTaskLater(plugin, () -> {
                        livingEntity.removeScoreboardTag("Marked");
                    }, 300L);
                    //endregion
                } else {
                }
            }
        }
        if (p.getInventory().getItemInMainHand().getType().equals(Material.ARROW)) {
            if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
                if (p.isSneaking()) {

                } else {
                    //region Evade
                    summonCircle(pLocation, 4, Particle.FIREWORKS_SPARK);
                    Vector jumpSpeed = new Vector(4, 3.5, 4).multiply(kbModifier);
                    Vector pLooking = pLocation.getDirection();
                    Vector jumpVelocity = pLooking.multiply(jumpSpeed);
                    p.setVelocity(jumpVelocity);
                    p.playSound(pLocation, Sound.ENTITY_ENDER_DRAGON_FLAP, 1, 1);
                    //endregion
                }
            }
        }
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
