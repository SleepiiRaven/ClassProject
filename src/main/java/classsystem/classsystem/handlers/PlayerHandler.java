package classsystem.classsystem.handlers;

import classsystem.classsystem.ClassSystem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        String pUUIDString = pUUID.toString();
        //region Set Config
        if (plugin.getConfig().get(pUUIDString + ".class") == null) {
            plugin.getConfig().set(pUUIDString + ".class", "none");
        }
        if (plugin.getConfig().get(pUUIDString + ".dmgMultiplier") == null) {
            plugin.getConfig().set(pUUIDString + ".dmgMultiplier", 1.0);
        }
        if (plugin.getConfig().get(pUUIDString + ".kbMultiplier") == null) {
            plugin.getConfig().set(pUUIDString + ".kbMultiplier", 1.0);
        }
        if (plugin.getConfig().get(pUUIDString + ".rangeMultiplier") == null) {
            plugin.getConfig().set(pUUIDString + ".rangeMultiplier", 1.0);
        }
        if (plugin.getConfig().get(pUUIDString + ".hpMultiplier") == null) {
            plugin.getConfig().set(pUUIDString + ".hpMultiplier", 1.0);
        }
        plugin.saveConfig();
        ///endregion
        //region Blade of Everlight
        ItemStack blade = new ItemStack(Material.DIAMOND_SWORD, 1);
        Inventory inv = p.getInventory();
        ItemMeta meta = blade.getItemMeta();
        // Make sure meta isn't null
        assert meta != null;
        // Set name
        meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Blade of Everlight");
        // Make a list for the lore
        final List<String> lore = new ArrayList<>();
        lore.add(ChatColor.YELLOW + "This blade shimmers in the light.");
        meta.setLore(lore);
        // Make a variable damage
        AttributeModifier damage = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", 20.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        // Add the damage
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, damage);
        // Set the blade's metadata
        blade.setItemMeta(meta);
        // Add the item to the player's inventory
        inv.addItem(blade);
        //endregion
    }
}
