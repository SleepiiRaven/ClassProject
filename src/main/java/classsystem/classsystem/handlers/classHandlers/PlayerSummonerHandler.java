package classsystem.classsystem.handlers.classHandlers;

import classsystem.classsystem.ClassSystem;
import classsystem.classsystem.CooldownManager;
import classsystem.classsystem.PlayerData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import java.util.UUID;

public class PlayerSummonerHandler extends PlayerClassTemplate {
    ClassSystem plugin = ClassSystem.getInstance();
    CooldownManager cooldownManager = plugin.getCdInstance();
    @Override
    public void onTrigger(PlayerInteractEvent e) {
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
}
