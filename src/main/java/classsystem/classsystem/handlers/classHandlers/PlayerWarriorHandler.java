package classsystem.classsystem.handlers.classHandlers;

import classsystem.classsystem.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class PlayerWarriorHandler extends PlayerClassTemplate {
    ClassSystem plugin = ClassSystem.getInstance();
    PartyManager partyManager = plugin.getPartyInstance();
    CooldownManager cooldownManager = plugin.getCdInstance();
    @Override
    public void onTrigger(PlayerInteractEvent e) {
        if (e.getHand() != EquipmentSlot.HAND) return;
        //region Set Variables
        Player p = e.getPlayer();
        boolean manaSystem = false;
        Enchantment manaReplace = ClassSystem.manaReplaceEnchantment;
        if (p.getInventory().getHelmet() != null) {
            if (p.getInventory().getHelmet().containsEnchantment(manaReplace)) {
                manaSystem = true;
            }
        }
        if (p.getInventory().getChestplate() != null) {
            if (p.getInventory().getChestplate().containsEnchantment(manaReplace)) {
                manaSystem = true;
            }
        }
        if (p.getInventory().getLeggings() != null) {
            if (p.getInventory().getLeggings().containsEnchantment(manaReplace)) {
                manaSystem = true;
            }
        }
        if (p.getInventory().getBoots() != null) {
            if (p.getInventory().getBoots().containsEnchantment(manaReplace)) {
                manaSystem = true;
            }
        }
        UUID pUUID = p.getUniqueId();
        double dmgModifier = PlayerData.getPlayerData(pUUID).getDmgModifier();
        double rangeModifier = PlayerData.getPlayerData(pUUID).getRangeModifier();
        double kbModifier = PlayerData.getPlayerData(pUUID).getKbModifier();
        double cdModifier = PlayerData.getPlayerData(pUUID).getCdModifier();
        //endregion
        if (ItemManager.warriorWeapons.contains(p.getInventory().getItemInMainHand().getType())) {
            if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
                if (p.isSneaking()) {
                    if (!(cooldownManager.isCooldownDone(p.getUniqueId(), "Roar"))) return;
                    roar(p, cdModifier, rangeModifier);
                }
            } if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (p.isSneaking()) {
                    if (!(cooldownManager.isCooldownDone(p.getUniqueId(), "Stomp"))) return;
                    stomp(kbModifier, p, cdModifier);
                } else {
                    if (!(cooldownManager.isCooldownDone(p.getUniqueId(), "Earthquake"))) return;
                    earthquake(p, dmgModifier, cdModifier, rangeModifier);
                }
            }
        }
    }
    public void onTriggerSwap(PlayerSwapHandItemsEvent e) {
        Player p = e.getPlayer();
        boolean manaSystem = false;
        Enchantment manaReplace = ClassSystem.manaReplaceEnchantment;
        if (p.getInventory().getHelmet() != null) {
            if (p.getInventory().getHelmet().containsEnchantment(manaReplace)) {
                manaSystem = true;
            }
        }
        if (p.getInventory().getChestplate() != null) {
            if (p.getInventory().getChestplate().containsEnchantment(manaReplace)) {
                manaSystem = true;
            }
        }
        if (p.getInventory().getLeggings() != null) {
            if (p.getInventory().getLeggings().containsEnchantment(manaReplace)) {
                manaSystem = true;
            }
        }
        if (p.getInventory().getBoots() != null) {
            if (p.getInventory().getBoots().containsEnchantment(manaReplace)) {
                manaSystem = true;
            }
        }
        UUID pUUID = p.getUniqueId();
        double dmgModifier = PlayerData.getPlayerData(pUUID).getDmgModifier();
        double cdModifier = PlayerData.getPlayerData(pUUID).getCdModifier();
        double rangeModifier = PlayerData.getPlayerData(pUUID).getRangeModifier();
        double hpModifier = PlayerData.getPlayerData(pUUID).getHpModifier();
        if (ItemManager.warriorWeapons.contains(p.getInventory().getItemInMainHand().getType())) {
            if (p.isSneaking()) {
                if (!(cooldownManager.isCooldownDone(p.getUniqueId(), "Rage"))) return;
                rage(p, dmgModifier, cdModifier);
            } else {
                if (!(cooldownManager.isCooldownDone(p.getUniqueId(), "Shield"))) return;
                shield(p, cdModifier, rangeModifier, hpModifier);
            }
            e.setCancelled(true);
        }
    }

    public void shield(Player p, double cdModifier, double rangeModifier, double hpModifier) {
        long cooldown = (long) (10000 / cdModifier);
        cooldownManager.setCooldownFromNow(p.getUniqueId(), "Shield", cooldown);
        double range = 4 * rangeModifier;
        Location location = p.getLocation();
        Collection<Entity> entitiesInRange = p.getNearbyEntities(range, range, range);
        if (partyManager.findParty(p.getUniqueId()) != null) {
            for (Entity entity : entitiesInRange) {
                if (!(entity instanceof LivingEntity)) continue;
                LivingEntity ally = (LivingEntity) entity;
                if (!(partyManager.findParty(ally.getUniqueId()) == partyManager.findParty(p.getUniqueId()))) continue;
                ally.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 50, (int)hpModifier));
            }
        }
        p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 50, (int)hpModifier));
        p.playSound(p.getLocation(), Sound.BLOCK_GLASS_BREAK, 1, 1);
        for (int d = 0; d <= 90; d += 1) {
            Location particleLoc = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());
            // Cosine for X
            particleLoc.setX(location.getX() + Math.cos(d) * range);
            // Sine for Z
            particleLoc.setZ(location.getZ() + Math.sin(d) * range);
            location.getWorld().spawnParticle(Particle.GLOW_SQUID_INK, particleLoc, 1);
        }
    }

    public void stomp(double kbModifier, Player p, double cdModifier) {
        double speed = 1 * kbModifier;
        long cooldown = (long) (5000 / cdModifier);
        cooldownManager.setCooldownFromNow(p.getUniqueId(), "Stomp", cooldown);
        //if (p.isOnGround()) {
            p.setVelocity(new Vector(0D, speed, 0D));
            BukkitScheduler scheduler = Bukkit.getScheduler();
            scheduler.runTaskLater(plugin, () -> {
                p.setVelocity(new Vector(0D, -speed, 0D).add(p.getEyeLocation().getDirection()));
            }, 15L);
        /**} else {
            p.setVelocity(new Vector(0D, -speed, 0D).add(p.getEyeLocation().getDirection()));
        }**/
    }

    public void rage(Player p, double dmgModifier, double cdModifier) {
        long cooldown = (long) (10000 / cdModifier);
        cooldownManager.setCooldownFromNow(p.getUniqueId(), "Rage", cooldown);
        p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, (int) dmgModifier));
        p.playSound(p.getLocation(), Sound.ENTITY_GHAST_SCREAM, 1, 0.5f);
    }

    public void earthquake(Player p, double dmgModifier, double cdModifier, double rangeModifier) {
        long cooldown = (long) (5000 / cdModifier);
        cooldownManager.setCooldownFromNow(p.getUniqueId(), "Earthquake", cooldown);
        Location location = p.getLocation();
        double damage = 2*dmgModifier;
        double size = 5 * rangeModifier;
        BlockData data;
        if (p.getLocation().subtract(0, -1, 0).getBlock().getBlockData().getMaterial().isAir()) {
            data = Material.DIRT.createBlockData();
        } else {
            data = p.getLocation().subtract(0, -1, 0).getBlock().getBlockData();
        }
        BukkitScheduler scheduler = Bukkit.getScheduler();
        BukkitTask task = scheduler.runTaskTimer(plugin, () -> {
            List<Entity> closebyMonsters = p.getNearbyEntities(Math.pow(size, 2), Math.pow(size, 2), Math.pow(size, 2));
            for (Entity closebyMonster : closebyMonsters) {
                if (partyManager.findParty(p.getUniqueId()) != null) {
                    if (partyManager.findParty(closebyMonster.getUniqueId()) == partyManager.findParty(p.getUniqueId()))
                        continue;
                }
                Location eLocation = closebyMonster.getLocation();
                // .distance is resource intensive, so get it squared (SO SQUARE YOUR RANGE)
                double distance = eLocation.distanceSquared(location);
                if (distance < Math.pow(size, 2)) {
                    // make sure it's a living entity, not an armor stand or something, continue skips the current loop
                    if (!(closebyMonster instanceof LivingEntity)) continue;
                    LivingEntity livingMonster = (LivingEntity) closebyMonster;
                    livingMonster.damage(damage, p);
                }
            }
            for (int d = 0; d <= 90; d += 1) {
                Location particleLoc = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());
                // Cosine for X
                particleLoc.setX(location.getX() + Math.cos(d) * size);
                // Sine for Z
                particleLoc.setZ(location.getZ() + Math.sin(d) * size);
                location.getWorld().spawnParticle(Particle.BLOCK_DUST, particleLoc, 1, data);
            }
            p.playSound(location, Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR, 1, 1);
        }, 0L, 10L);
        scheduler.runTaskLater(plugin, task::cancel, 50L);
    }

    public void roar(Player p, double cdModifier, double rangeModifier) {
        long cooldown = (long) (5000 / cdModifier);
        cooldownManager.setCooldownFromNow(p.getUniqueId(), "Roar", cooldown);
        Location location = p.getLocation();
        double size = 5 * rangeModifier;
        List<Entity> closebyMonsters = p.getNearbyEntities(size, size, size);
        for (Entity closebyMonster : closebyMonsters) {
            if (!(closebyMonster instanceof Monster)) {
                if (!(closebyMonster instanceof Player)) continue;
                Player closebyPlayer = (Player) closebyMonster;

                Vector direction = getVector(closebyPlayer).subtract(getVector(p)).normalize();
                double x = direction.getX();
                double y = direction.getY();
                double z = direction.getZ();

                // Now change the angle
                Location changed = closebyPlayer.getLocation().clone();
                changed.setYaw(180 - toDegree(Math.atan2(x, z)));
                changed.setPitch(90 - toDegree(Math.acos(y)));
                closebyPlayer.teleport(changed);
                continue;
            }
            Location eLocation = closebyMonster.getLocation();
            // .distance is resource intensive, so get it squared (SO SQUARE YOUR RANGE)
            double distance = eLocation.distanceSquared(location);
            if (distance < Math.pow(size, 2)) {
                // make sure it's a living entity, not an armor stand or something, continue skips the current loop
                Monster livingMonster = (Monster) closebyMonster;
                livingMonster.setTarget(p);
            }
        }
        for (int d = 0; d <= 90; d += 1) {
            Location particleLoc = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());
            // Cosine for X
            particleLoc.setX(location.getX() + Math.cos(d) * size);
            // Sine for Z
            particleLoc.setZ(location.getZ() + Math.sin(d) * size);
            location.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, particleLoc, 1);
        }
        p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
    }

    private Vector getVector(Entity entity) {
        if (entity instanceof Player)
            return ((Player) entity).getEyeLocation().toVector();
        else
            return entity.getLocation().toVector();
    }

    private float toDegree(double angle) {
        return (float) Math.toDegrees(angle);
    }
}
