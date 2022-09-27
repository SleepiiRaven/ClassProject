package classsystem.classsystem.handlers.classHandlers;

import classsystem.classsystem.*;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class PlayerScoutHandler extends PlayerClassTemplate {
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
        if (ItemManager.scoutWeapons.contains(p.getInventory().getItemInMainHand().getType())) {
            if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
                if (p.isSneaking()) {
                    if (!(cooldownManager.isCooldownDone(p.getUniqueId(), "Hunter's Mark"))) return;
                    if (hunterMark(plugin, p, pLocation, rangeModifier, cdModifier, cooldownManager)) return;
                } else {
                }
            }
        }
        if (Tag.ITEMS_ARROWS.isTagged(p.getInventory().getItemInMainHand().getType())) {
            if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
                if (p.isSneaking()) {

                } else {
                    if (!(cooldownManager.isCooldownDone(p.getUniqueId(), "Evade"))) return;
                    evade(p, pLocation, kbModifier, cdModifier);
                }
            }
        }
    }

    public void onTriggerSwap(PlayerSwapHandItemsEvent e) {
        ClassSystem plugin = ClassSystem.getInstance();
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
        if (ItemManager.scoutWeapons.contains(p.getInventory().getItemInMainHand().getType())) {
            if (!(cooldownManager.isCooldownDone(p.getUniqueId(), "Camouflage"))) return;
            camouflage(plugin, p, cdModifier, dmgModifier);
            e.setCancelled(true);
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
    private boolean hunterMark(ClassSystem plugin, Player p, Location pLocation, double rangeModifier, double cdModifier, CooldownManager cooldownManager) {
        //region Hunter's Mark
        long cooldown = (long) (5000 / cdModifier);
        double range = 32 * rangeModifier;
        RayTraceResult traceResult = p.getWorld().rayTraceEntities(p.getEyeLocation(), p.getEyeLocation().getDirection(), range, (entity -> {
            return entity != p;
        }));
        if (traceResult == null || !(traceResult.getHitEntity() instanceof LivingEntity)) return true;
        if (partyManager.findParty(p.getUniqueId()) != null) {
            if (partyManager.findParty(traceResult.getHitEntity().getUniqueId()) == partyManager.findParty(p.getUniqueId())) return false;
        }
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
    private void camouflage(ClassSystem plugin, Player p, double cdModifier, double dmgModifier) {
        long cooldown = (long) (15000 / cdModifier);
        long time = (long) (80*dmgModifier);
        cooldownManager.setCooldownFromNow(p.getUniqueId(), "Camouflage", cooldown);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (partyManager.findParty(onlinePlayer.getUniqueId()) != null) {
                if (partyManager.findParty(onlinePlayer.getUniqueId()) == partyManager.findParty(p.getUniqueId())) return;
            }
            onlinePlayer.sendEquipmentChange(p, EquipmentSlot.HEAD, new ItemStack(Material.AIR, 0));
            onlinePlayer.sendEquipmentChange(p, EquipmentSlot.CHEST, new ItemStack(Material.AIR, 0));
            onlinePlayer.sendEquipmentChange(p, EquipmentSlot.LEGS, new ItemStack(Material.AIR, 0));
            onlinePlayer.sendEquipmentChange(p, EquipmentSlot.FEET, new ItemStack(Material.AIR, 0));
            onlinePlayer.sendEquipmentChange(p, EquipmentSlot.HAND, new ItemStack(Material.AIR, 0));
            onlinePlayer.sendEquipmentChange(p, EquipmentSlot.OFF_HAND, new ItemStack(Material.AIR, 0));
        }
        p.setAllowFlight(true);
        p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, (int)time, 1));

        BukkitScheduler scheduler = Bukkit.getScheduler();
        BukkitTask followPlayer = scheduler.runTaskTimer(plugin, () -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.spawnParticle(Particle.TOTEM, p.getLocation(), 25);
            }
        }, 1L, 5L);
        scheduler.runTaskLater(plugin, () -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                followPlayer.cancel();
                if (partyManager.findParty(onlinePlayer.getUniqueId()) != null) {
                    if (partyManager.findParty(onlinePlayer.getUniqueId()) == partyManager.findParty(p.getUniqueId())) return;
                }
                onlinePlayer.sendEquipmentChange(p, EquipmentSlot.HEAD, p.getInventory().getHelmet());
                onlinePlayer.sendEquipmentChange(p, EquipmentSlot.CHEST, p.getInventory().getChestplate());
                onlinePlayer.sendEquipmentChange(p, EquipmentSlot.LEGS, p.getInventory().getLeggings());
                onlinePlayer.sendEquipmentChange(p, EquipmentSlot.FEET, p.getInventory().getBoots());
                onlinePlayer.sendEquipmentChange(p, EquipmentSlot.HAND, p.getInventory().getItemInMainHand());
                onlinePlayer.sendEquipmentChange(p, EquipmentSlot.OFF_HAND, p.getInventory().getItemInOffHand());
            }
            p.setAllowFlight(false);
        }, time);
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
