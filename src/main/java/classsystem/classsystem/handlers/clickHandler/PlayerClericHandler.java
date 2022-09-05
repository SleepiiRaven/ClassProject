package classsystem.classsystem.handlers.clickHandler;

import classsystem.classsystem.ClassSystem;
import classsystem.classsystem.CooldownManager;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.Collection;

public class PlayerClericHandler extends PlayerClassTemplate {
    ClassSystem plugin = ClassSystem.getInstance();
    CooldownManager cooldownManager = plugin.getCdInstance();
    @Override
    public void onTrigger(PlayerInteractEvent e) {
        ClassSystem plugin = ClassSystem.getInstance();
        if (e.getHand() != EquipmentSlot.HAND) return;
        Player p = e.getPlayer();
        String pUUID = p.getUniqueId().toString();
        Location pLocation = p.getLocation();
        double hpModifier = plugin.getConfig().getInt(pUUID + ".hpMultiplier");
        double dmgModifier = plugin.getConfig().getInt(pUUID + ".dmgMultiplier");
        double rangeModifier = plugin.getConfig().getInt(pUUID + ".rangeMultiplier");
        double kbModifier = plugin.getConfig().getInt(pUUID + ".kbMultiplier");
        double cdModifier = plugin.getConfig().getInt(pUUID + ".cdMultiplier");

        //region CLASS ABILITIES
        if (p.getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_HOE)) {
            if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
                double range = 4 * rangeModifier;
                Collection<Entity> entitiesInRange = p.getNearbyEntities(range, range, range);
                if (p.isSneaking()) {
                    if (!(cooldownManager.isCooldownDone(p.getUniqueId(), "Demeter's Blessing"))) return;
                    demeterBlessing(p, pLocation, (int) hpModifier, cdModifier, (int) range, entitiesInRange);
                } else {
                    if (!(cooldownManager.isCooldownDone(p.getUniqueId(), "Dionysus' Intoxication"))) return;
                    dionysusIntoxication(p, pLocation, dmgModifier, cdModifier, (int) range, entitiesInRange);
                }
            }
            if (e.getAction() == Action.RIGHT_CLICK_AIR) {
                if (!p.isSneaking()) {
                    if (!(cooldownManager.isCooldownDone(p.getUniqueId(), "Hermes' Leap"))) return;
                    hermesLeap(p, pLocation, kbModifier, cdModifier);
                } else {
                    if (!(cooldownManager.isCooldownDone(p.getUniqueId(), "Aphrodite's Love"))) return;
                    aphroditeLove(p, pLocation, hpModifier, rangeModifier, cdModifier);
                }
            }
        }
        //endregion
    }
    private void demeterBlessing(Player p, Location pLocation, int hpModifier, double cdModifier, int range, Collection<Entity> entitiesInRange) {
        //region Demeter's Blessing
        long cooldown = (long) (10000 / cdModifier);
        cooldownManager.setCooldownFromNow(p.getUniqueId(), "Demeter's Blessing", cooldown);
        summonCircle(pLocation, range, Particle.HEART);
        for (Entity entity : entitiesInRange) {
            if (!(entity instanceof Player)) continue;
            ((Player) entity).addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 80, hpModifier*2));
            p.playSound(pLocation, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        }
        p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20, hpModifier));
        //endregion
    }
    private void dionysusIntoxication(Player p, Location pLocation, double dmgModifier, double cdModifier, int range, Collection<Entity> entitiesInRange) {
        //region Dionysus' Intoxication
        long cooldown = (long) (6000 / cdModifier);
        cooldownManager.setCooldownFromNow(p.getUniqueId(), "Dionysus' Intoxication", cooldown);
        for (Entity entity : entitiesInRange) {
            summonCircle(pLocation, range, Particle.SOUL);
            if (!(entity instanceof Monster)) continue;
            Monster monster = (Monster) entity;
            monster.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, (int)(3 * dmgModifier)));
            monster.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 200, (int)(-3 * dmgModifier)));
        }
        p.playSound(pLocation, Sound.ENTITY_WANDERING_TRADER_DRINK_POTION, 1, 1);
        //endregion
    }
    private void hermesLeap(Player p, Location pLocation, double kbModifier, double cdModifier) {
        //region Hermes' Leap
        long cooldown = (long) (8000 / cdModifier);
        cooldownManager.setCooldownFromNow(p.getUniqueId(), "Hermes' Leap", cooldown);
        summonCircle(pLocation, 4, Particle.CLOUD);
        Vector jumpSpeed = new Vector(3, 2.5, 3).multiply(kbModifier);
        Vector pLooking = pLocation.getDirection();
        Vector jumpVelocity = pLooking.multiply(jumpSpeed);
        p.setVelocity(jumpVelocity);
        p.playSound(pLocation, Sound.ENTITY_SLIME_SQUISH, 1, 1);
        //endregion
    }
    private void aphroditeLove(Player p, Location pLocation, double hpModifier, double rangeModifier, double cdModifier) {
        //region Aphrodite's Love
        long cooldown = (long) (250 / cdModifier);
        cooldownManager.setCooldownFromNow(p.getUniqueId(), "Aphrodite's Love", cooldown);
        double range = 8 * rangeModifier;
        double healing = 4 * hpModifier;
        RayTraceResult traceResult = p.getWorld().rayTraceEntities(p.getEyeLocation(), p.getEyeLocation().getDirection(), range, (entity -> {
            return entity != p;
        }));
        if (traceResult != null) {
            if (traceResult.getHitEntity() instanceof Player) {
                Player target = (Player) traceResult.getHitEntity();
                if (target.getHealth() < target.getMaxHealth() && p.getHealth() > healing) {
                    p.setHealth(p.getHealth() - healing);
                    p.getWorld().spawnParticle(Particle.CRIT, pLocation, 10);
                    p.playSound(pLocation, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1);
                }
                target.setHealth(Math.min((target.getHealth() + healing), target.getMaxHealth()));
                target.getWorld().spawnParticle(Particle.HEART, target.getLocation(), 10);
                return;
            } else if (traceResult.getHitEntity() instanceof Monster) {
                Monster monster = (Monster) traceResult.getHitEntity();
                if (p.getHealth() > healing) {
                    monster.damage(healing, p);
                    p.setHealth(p.getHealth() - healing);
                    p.getWorld().spawnParticle(Particle.CRIT, pLocation, 10);
                    monster.getWorld().spawnParticle(Particle.CRIT, monster.getLocation(), 10);
                    p.playSound(pLocation, Sound.ENTITY_GHAST_DEATH, 1, 0.5f);
                }
            }
        }
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