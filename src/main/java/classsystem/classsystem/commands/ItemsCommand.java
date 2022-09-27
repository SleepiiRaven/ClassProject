package classsystem.classsystem.commands;

import classsystem.classsystem.ClassSystem;
import classsystem.classsystem.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String [] args) {
        // If the sender isn't a Player
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can run this command.");
            return true;
        }

        Player p = (Player) sender;
        //region WEAPONS
        //region Blade of Everlight
        ItemStack blade = new ItemStack(Material.DIAMOND_SWORD, 1);
        Inventory inv = p.getInventory();
        ItemMeta bladeMeta = blade.getItemMeta();
        // Make sure meta isn't null
        assert bladeMeta != null;
        // Set name
        bladeMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Blade of Everlight");
        // Make a list for the lore
        final List<String> bladeLore = new ArrayList<>();
        bladeLore.add(ChatColor.YELLOW + "This blade shimmers in the light.");
        bladeMeta.setLore(bladeLore);
        // Make a variable damage
        AttributeModifier bladeDamage = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", 20.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        // Add the damage
        bladeMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, bladeDamage);
        // Set the blade's metadata
        blade.setItemMeta(bladeMeta);
        // Add the item to the player's inventory

        //endregion
        // region Axe of Time
        ItemStack axe = new ItemStack(Material.DIAMOND_AXE, 1);
        ItemMeta axeMeta = axe.getItemMeta();
        // Make sure meta isn't null
        assert axeMeta != null;
        // Set name
        axeMeta.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Axe of Time");
        // Make a list for the lore
        final List<String> axeLore = new ArrayList<>();
        axeLore.add(ChatColor.YELLOW + "This axe seems to make a chime noise every five minutes.");
        axeMeta.setLore(axeLore);
        // Make a variable damage
        AttributeModifier axeDamage = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", 20.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        // Add the damage
        axeMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, axeDamage);
        // Set the blade's metadata
        axe.setItemMeta(axeMeta);
        // Add the item to the player's inventory

        //endregion
        //region Oceanic Staff
        ItemStack scythe = new ItemStack(Material.DIAMOND_HOE, 1);
        ItemMeta scytheMeta = scythe.getItemMeta();
        // Make sure meta isn't null
        assert scytheMeta != null;
        // Set name
        scytheMeta.setDisplayName(ChatColor.BLUE + "" + ChatColor.BOLD + "Oceanic Staff");
        // Make a list for the lore
        final List<String> scytheLore = new ArrayList<>();
        scytheLore.add(ChatColor.YELLOW + "This unpolished staff has a bubble on top.");
        scytheMeta.setLore(scytheLore);
        // Make a variable damage
        AttributeModifier scytheDamage = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", 20.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        // Add the damage
        scytheMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, scytheDamage);
        // Set the blade's metadata
        scythe.setItemMeta(scytheMeta);
        //endregion
        //region Staff of the Roots
        ItemStack staff = new ItemStack(Material.STICK, 1);
        ItemMeta staffMeta = staff.getItemMeta();
        // Make sure meta isn't null
        assert staffMeta != null;
        // Set name
        staffMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Staff of the Roots");
        // Make a list for the lore
        final List<String> staffLore = new ArrayList<>();
        staffLore.add(ChatColor.YELLOW + "This hand-made staff harnesses the power of its own roots to cast spells.");
        staffMeta.setLore(staffLore);
        // Make a variable damage
        AttributeModifier staffDamage = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", 20.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        // Add the damage
        staffMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, staffDamage);
        // Set the blade's metadata
        staff.setItemMeta(staffMeta);
        //endregion
        //region Staff of the Roots
        ItemStack bow = new ItemStack(Material.BOW, 1);
        ItemMeta bowMeta = bow.getItemMeta();
        // Make sure meta isn't null
        assert bowMeta != null;
        // Set name
        bowMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Barrage of the Wind");
        // Make a list for the lore
        final List<String> bowLore = new ArrayList<>();
        bowLore.add(ChatColor.YELLOW + "This hand-made staff harnesses the power of its own roots to cast spells.");
        bowMeta.setLore(bowLore);
        // Make a variable damage
        AttributeModifier bowDamage = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", 20.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        // Add the damage
        bowMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, bowDamage);
        // Set the blade's metadata
        bow.setItemMeta(bowMeta);
        //endregion
        inv.addItem(blade);
        inv.addItem(axe);
        inv.addItem(scythe);
        inv.addItem(staff);
        inv.addItem(bow);
        //endregion
        return true;
    }
}
