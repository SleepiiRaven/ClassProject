package classsystem.classsystem.handlers;

import classsystem.classsystem.ClassSystem;
import classsystem.classsystem.Party;
import classsystem.classsystem.PartyManager;
import classsystem.classsystem.PlayerData;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class DamageHandler implements Listener, ClassSystem.Variables {
    private final ClassSystem plugin;
    private DecimalFormat formatter = new DecimalFormat("#.##");
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

        if (e.getDamager().getScoreboardTags().contains("fireJump")) {
            e.setCancelled(true);
        }

        if (e.getDamager() instanceof Player) {
            Player damager = (Player) e.getDamager();
            if (!(e.getEntity() instanceof LivingEntity)) return;
            LivingEntity damaged = (LivingEntity) e.getEntity();
            if (PlayerData.getPlayerData(damager.getUniqueId()).getPlayerClass().equals("rogue")) {
                float alpha = damaged.getLocation().getYaw();
                float beta = damager.getLocation().getYaw();
                boolean backstab = Math.abs(alpha - beta) % 360 <= 45;
                if (backstab) {
                    damaged.getWorld().spawnParticle(Particle.SMOKE_LARGE, damaged.getLocation(), 10);
                    damaged.damage(e.getDamage() * 2);
                    e.setCancelled(true);
                }
            }
        }

        if (e.getDamager() instanceof Arrow) {
            Arrow a = (Arrow) e.getDamager();
            if (!(a.getShooter() instanceof Player)) return;
            Player shooter = (Player) a.getShooter();
            UUID shooterID = shooter.getUniqueId();
            double dmg = PlayerData.getPlayerData(shooterID).getDmgModifier();
            if (!(e.getEntity() instanceof LivingEntity)) return;
            LivingEntity entity = (LivingEntity) e.getEntity();
            if (e.getEntity().getScoreboardTags().contains("Marked")) {
                dmg *= 1.5;
                entity.removeScoreboardTag("Marked");
                entity.removePotionEffect(PotionEffectType.GLOWING);
            }
            if (!(PlayerData.getPlayerData(shooter.getUniqueId()).getPlayerClass().equals("scout"))) return;
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
            if (entityParty == null) return;
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




    //region Entity Damage In General
    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        //region Player
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            UUID pUUID = p.getUniqueId();
            String pName = p.getName();
            if (PlayerData.getPlayerData(pUUID).getPlayerClass().equals("cleric")) {
                e.setDamage(e.getDamage() * 2);
            }
            if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                if (PlayerData.getPlayerData(pUUID).getPlayerClass().equals("avatar") || PlayerData.getPlayerData(pUUID).getPlayerClass().equals("warrior")) {
                    ClassSystem plugin = ClassSystem.getInstance();
                    PartyManager partyManager = plugin.getPartyInstance();
                    double dmgModifier = PlayerData.getPlayerData(pUUID).getDmgModifier();
                    double rangeModifier = PlayerData.getPlayerData(pUUID).getRangeModifier();
                    double kbModifier = PlayerData.getPlayerData(pUUID).getKbModifier();
                    double cdModifier = PlayerData.getPlayerData(pUUID).getCdModifier();
                    double hpModifier = PlayerData.getPlayerData(pUUID).getHpModifier();
                    double speed = 1 * kbModifier;
                    double kb = ThreadLocalRandom.current().nextDouble(0.3, 0.5) * kbModifier;
                    double range = 25 * rangeModifier;
                    double damage = (e.getDamage() + 8) * dmgModifier;
                    p.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, p.getLocation(), 1);
                    p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
                    List<Entity> closebyMonsters = p.getNearbyEntities(range, range, range);
                    for (Entity closebyMonster : closebyMonsters) {
                        if (partyManager.findParty(p.getUniqueId()) != null && partyManager.findParty(closebyMonster.getUniqueId()) == partyManager.findParty(p.getUniqueId())) continue;
                        Location eLocation = closebyMonster.getLocation();
                        // .distance is resource intensive, so get it squared (SO SQUARE YOUR RANGE)
                        double distance = eLocation.distanceSquared(p.getLocation());
                        if (!(distance < range)) continue;
                        if (!(closebyMonster instanceof LivingEntity)) continue;
                        LivingEntity livingMonster = (LivingEntity) closebyMonster;
                        livingMonster.damage(damage, p);
                        livingMonster.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 30, 100));
                        livingMonster.setVelocity(new Vector(0, kb, 0));
                    }
                }
                e.setCancelled(true);
            }
            if (!(godMode.contains(pName))) {
                return;
            } else {
                e.setCancelled(true);
            }
        }
        //endregion
        //region Entity
        if (!(e.getEntity() instanceof LivingEntity)) return;
        LivingEntity entity = (LivingEntity) e.getEntity();
        double eDmg = e.getFinalDamage();
        if (eDmg < 0.01) eDmg = 0;
        String entityDamage = formatter.format(eDmg);
        Location loc = entity.getLocation().clone().add(getRandomOffset(), 1 + (getRandomOffset() / 2), getRandomOffset());
        entity.getWorld().spawn(loc, ArmorStand.class, armorStand -> {
            armorStand.setMarker(true);
            armorStand.setVisible(false);
            armorStand.setGravity(false);
            armorStand.setSmall(true);
            armorStand.setCustomNameVisible(true);
            armorStand.setCustomName(ChatColor.RED + entityDamage);
            BukkitScheduler scheduler = Bukkit.getScheduler();
            scheduler.runTaskLater(plugin, armorStand::remove, 20L);
        });
        //endregion
    }
    //endregion
    //endregion

    private double getRandomOffset() {
        double random = Math.random();
        if (Math.random() > 0.5) random *= -1;
        return random;
    }
}
