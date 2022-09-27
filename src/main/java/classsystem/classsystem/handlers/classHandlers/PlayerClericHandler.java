package classsystem.classsystem.handlers.classHandlers;

import classsystem.classsystem.*;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class PlayerClericHandler extends PlayerClassTemplate {
    ClassSystem plugin = ClassSystem.getInstance();
    CooldownManager cooldownManager = plugin.getCdInstance();
    PartyManager partyManager = plugin.getPartyInstance();
    @Override
    public void onTrigger(PlayerInteractEvent e) {
        ClassSystem plugin = ClassSystem.getInstance();
        if (e.getHand() != EquipmentSlot.HAND) return;
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
        Location pLocation = p.getLocation();
        double dmgModifier = PlayerData.getPlayerData(pUUID).getDmgModifier();
        double rangeModifier = PlayerData.getPlayerData(pUUID).getRangeModifier();
        double kbModifier = PlayerData.getPlayerData(pUUID).getKbModifier();
        double cdModifier = PlayerData.getPlayerData(pUUID).getCdModifier();
        double hpModifier = PlayerData.getPlayerData(pUUID).getHpModifier();
        int mana = PlayerData.getPlayerData(pUUID).getMana();

        //region CLASS ABILITIES
        if (ItemManager.clericWeapons.contains(p.getInventory().getItemInMainHand().getType())) {
            if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
                double range = 4 * rangeModifier;
                Collection<Entity> entitiesInRange = p.getNearbyEntities(range, range, range);
                if (p.isSneaking()) {
                    int manaCost = 5;
                    if (!manaSystem) {
                        if (!(cooldownManager.isCooldownDone(p.getUniqueId(), "Demeter's Blessing"))) return;
                    } else {
                        if (!(mana < manaCost)) return;
                        PlayerData.getPlayerData(pUUID).setMana(mana - manaCost);
                    }
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
        double rangeModifier = PlayerData.getPlayerData(pUUID).getRangeModifier();
        double kbModifier = PlayerData.getPlayerData(pUUID).getKbModifier();
        double cdModifier = PlayerData.getPlayerData(pUUID).getCdModifier();
        double hpModifier = PlayerData.getPlayerData(pUUID).getHpModifier();
    }
    private void demeterBlessing(Player p, Location pLocation, int hpModifier, double cdModifier, int range, Collection<Entity> entitiesInRange) {
        //region Demeter's Blessing
        long cooldown = (long) (10000 / cdModifier);
        cooldownManager.setCooldownFromNow(p.getUniqueId(), "Demeter's Blessing", cooldown);
        summonCircle(pLocation, range, Particle.HEART);
        if (partyManager.findParty(p.getUniqueId()) != null) {
            for (Entity entity : entitiesInRange) {
                if (!(entity instanceof LivingEntity)) continue;
                LivingEntity ally = (LivingEntity) entity;
                if (!(partyManager.findParty(ally.getUniqueId()) == partyManager.findParty(p.getUniqueId()))) continue;
                ally.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 80, hpModifier));
            }
        }
        p.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 1, hpModifier));
        p.playSound(pLocation, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        //endregion
    }
    private void dionysusIntoxication(Player p, Location pLocation, double dmgModifier, double cdModifier, int range, Collection<Entity> entitiesInRange) {
        //region Dionysus' Intoxication
        long cooldown = (long) (6000 / cdModifier);
        cooldownManager.setCooldownFromNow(p.getUniqueId(), "Dionysus' Intoxication", cooldown);
        for (Entity entity : entitiesInRange) {
            if (!(entity instanceof LivingEntity)) continue;
            if (partyManager.findParty(p.getUniqueId()) != null) {
                if (partyManager.findParty(entity.getUniqueId()) == partyManager.findParty(p.getUniqueId())) continue;
            }
            LivingEntity enemy = (LivingEntity) entity;
            enemy.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, (int)(3 * dmgModifier)));
            enemy.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 200, (int)(-3 * dmgModifier)));
        }
        summonCircle(pLocation, range, Particle.SOUL);
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
        double range = 32 * rangeModifier;
        double healing = 4 * hpModifier;
        RayTraceResult traceResult = p.getWorld().rayTraceEntities(p.getEyeLocation(), p.getEyeLocation().getDirection(), range, (entity -> {
            return entity != p;
        }));
        if (traceResult != null) {
            if (traceResult.getHitEntity() instanceof Player && (partyManager.findParty(traceResult.getHitEntity().getUniqueId()) == partyManager.findParty(p.getUniqueId())) && partyManager.findParty(p.getUniqueId()) != null) {
                Player target = (Player) traceResult.getHitEntity();
                if (target.getHealth() < target.getMaxHealth() && p.getHealth() > healing) {
                    p.setHealth(p.getHealth() - healing);
                    p.getWorld().spawnParticle(Particle.CRIT, pLocation, 10);
                    p.playSound(pLocation, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1);
                }
                target.setHealth(Math.min((target.getHealth() + healing), target.getMaxHealth()));
                target.getWorld().spawnParticle(Particle.HEART, target.getLocation(), 10);
                return;
            } else if (traceResult.getHitEntity() instanceof LivingEntity) {
                LivingEntity enemy = (LivingEntity) traceResult.getHitEntity();
                if (p.getHealth() > healing) {
                    enemy.damage(healing, p);
                    p.setHealth(p.getHealth() - healing);
                    p.getWorld().spawnParticle(Particle.CRIT, pLocation, 10);
                    enemy.getWorld().spawnParticle(Particle.CRIT, enemy.getLocation(), 10);
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