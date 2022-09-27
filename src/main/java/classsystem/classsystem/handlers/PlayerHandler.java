package classsystem.classsystem.handlers;

import classsystem.classsystem.ClassSystem;
import classsystem.classsystem.CooldownManager;
import classsystem.classsystem.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class PlayerHandler implements Listener {
    private final ClassSystem plugin;
    public PlayerHandler(ClassSystem plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        UUID pUUID = p.getUniqueId();
        CooldownManager cooldownManager = plugin.getCdInstance();
        cooldownManager.createContainer(pUUID);
        PlayerData pData = PlayerData.getPlayerData(pUUID);
        pData.save();
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        UUID pUUID = p.getUniqueId();
        CooldownManager cooldownManager = plugin.getCdInstance();
        cooldownManager.removeContainer(pUUID);
        PlayerData playerData = PlayerData.getPlayerData(pUUID);
        playerData.saveAndDelete();
    }
}
