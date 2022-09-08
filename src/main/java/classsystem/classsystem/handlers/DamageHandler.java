package classsystem.classsystem.handlers;

import classsystem.classsystem.ClassSystem;
import classsystem.classsystem.Party;
import classsystem.classsystem.PartyManager;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class DamageHandler implements Listener, ClassSystem.Variables {
    private final ClassSystem plugin;
    //region Register Events
    public DamageHandler(ClassSystem plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }
    //endregion
    //region Damage Handler
    //region Entity Damage
    @EventHandler(priority = EventPriority.LOW)
    public void onEntityDamage(EntityDamageByEntityEvent e) {

        if (e.getDamager().getPersistentDataContainer().getKeys().contains("fireJump")) {
            e.setCancelled(true);
        }

        if (e.getDamager() instanceof Player) {
            Player damager = (Player) e.getDamager();
            if (!(e.getEntity() instanceof LivingEntity)) return;
            LivingEntity damaged = (LivingEntity) e.getEntity();
            String damagerUUID = damager.getUniqueId().toString();
            if (plugin.getConfig().get(damagerUUID + ".class") == "rogue") {
                float alpha = damaged.getLocation().getYaw();
                float beta = damager.getLocation().getYaw();
                boolean backstab = Math.abs(alpha - beta) % 360 <= 45;
                if (backstab) {
                    damaged.getWorld().spawnParticle(Particle.SMOKE_LARGE, damaged.getLocation(), 10);
                    damaged.damage(e.getFinalDamage());
                }
            }
        }

        if (e.getDamager() instanceof Arrow) {
            Arrow a = (Arrow) e.getDamager();
            if (!(a.getShooter() instanceof Player)) return;
            Player shooter = (Player) a.getShooter();
            String shooterID = shooter.getUniqueId().toString();
            double dmg = plugin.getConfig().getDouble(shooterID + ".dmgMultiplier");
            if (!(e.getEntity() instanceof LivingEntity)) return;
            LivingEntity entity = (LivingEntity) e.getEntity();
            if (e.getEntity().getScoreboardTags().contains("Marked")) {
                dmg *= 1.5;
                entity.removeScoreboardTag("Marked");
                entity.removePotionEffect(PotionEffectType.GLOWING);
            }
            if (!(plugin.getConfig().get(shooterID + ".class").equals("scout"))) return;
            // the D after the 4 is telling java the number is a double not an int
            double damage = e.getDamage()*dmg;
            entity.damage(damage, a);
            a.remove();
            e.setCancelled(true);
        }
    }
    //endregion
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPartyDamage(EntityDamageByEntityEvent e) {
        //region Melee
        PartyManager partyManager = plugin.getPartyInstance();
        Party entityParty = partyManager.findParty(e.getEntity().getUniqueId());
        Party damagerParty = partyManager.findParty(e.getDamager().getUniqueId());
        if (entityParty == null) return;
        if (entityParty.equals(damagerParty)) {
            e.setCancelled(true);
            return;
        }
        //endregion
        //region Ranged
        if (e.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) e.getDamager();
            if (!(projectile.getShooter() instanceof Player)) return;
            UUID shooterUUID = ((Player) projectile.getShooter()).getUniqueId();
            Party shooterParty = partyManager.findParty(shooterUUID);
            if (entityParty.equals(shooterParty)) {
                e.setCancelled(true);
                return;
            }
        }
        //endregion
        //region Entities
        Set<String> scoreboard = e.getDamager().getScoreboardTags();
        if (!(scoreboard.isEmpty())) {
            for (String s : scoreboard) {
                UUID mageUUID = UUID.fromString(s);
                Party mageParty = partyManager.findParty(mageUUID);
                if (entityParty.equals(mageParty)) {
                    e.setCancelled(true);
                    return;
                }
            }
        }
        //endregion
    }




    //region Player Damage
    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player p = (Player) event.getEntity();
        String pUUID = p.getUniqueId().toString();
        String pName = p.getName();
        if (plugin.getConfig().get(pUUID+".class").equals("cleric")) {
            event.setDamage(event.getDamage()*2);
        }
        if (plugin.getConfig().get(pUUID+".class").equals("warrior")) {
            event.setDamage(event.getDamage()/2);
        }
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            if (plugin.getConfig().get(pUUID+".class").equals("warrior")) {
                ClassSystem plugin = ClassSystem.getInstance();
                PartyManager partyManager = plugin.getPartyInstance();
                double dmgModifier = plugin.getConfig().getInt(pUUID + ".dmgMultiplier");
                double rangeModifier = plugin.getConfig().getInt(pUUID + ".rangeMultiplier");
                double kbModifier = plugin.getConfig().getInt(pUUID + ".kbMultiplier");
                double cdModifier = plugin.getConfig().getInt(pUUID + ".cdMultiplier");
                double speed = 1 * kbModifier;
                double kb = ThreadLocalRandom.current().nextDouble(0.3, 0.5) * kbModifier;
                double range = 25 * rangeModifier;
                double damage = 10 * dmgModifier;
                p.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, p.getLocation(), 1);
                p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
                List<Entity> closebyMonsters = p.getNearbyEntities(range, range, range);
                for (Entity closebyMonster : closebyMonsters) {
                    if (partyManager.findParty(p.getUniqueId()) != null) {
                        if (partyManager.findParty(closebyMonster.getUniqueId()) == partyManager.findParty(p.getUniqueId())) continue;
                    }
                    Location eLocation = closebyMonster.getLocation();
                    // .distance is resource intensive, so get it squared (SO SQUARE YOUR RANGE)
                    double distance = eLocation.distanceSquared(p.getLocation());
                    if (distance < range) {
                        // make sure it's a living entity, not an armor stand or something, continue skips the current loop
                        if (!(closebyMonster instanceof LivingEntity)) continue;
                        LivingEntity livingMonster = (LivingEntity) closebyMonster;
                        livingMonster.damage(damage, p);
                        livingMonster.setVelocity(new Vector(0, kb, 0));
                    }
                }
            }
            event.setCancelled(true);
        }
        if (!(godMode.contains(pName))) {
            return;
        } else {
            event.setCancelled(true);
        }
    }
    //endregion
    //endregion
}
