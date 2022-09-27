package classsystem.classsystem.commands;

import classsystem.classsystem.ClassSystem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class EnchantCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String [] args) {
        // If the sender isn't a Player
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can run this command.");
            return true;
        }

        Player player = (Player) sender;
        ItemStack hand = player.getInventory().getItemInMainHand();
        //region Armor Types
        List<Material> armor = new ArrayList<>();
        armor.add(Material.GOLDEN_HELMET);
        armor.add(Material.GOLDEN_CHESTPLATE);
        armor.add(Material.GOLDEN_LEGGINGS);
        armor.add(Material.GOLDEN_BOOTS);
        armor.add(Material.CHAINMAIL_HELMET);
        armor.add(Material.CHAINMAIL_CHESTPLATE);
        armor.add(Material.CHAINMAIL_LEGGINGS);
        armor.add(Material.CHAINMAIL_BOOTS);
        armor.add(Material.IRON_HELMET);
        armor.add(Material.IRON_CHESTPLATE);
        armor.add(Material.IRON_LEGGINGS);
        armor.add(Material.IRON_BOOTS);
        armor.add(Material.DIAMOND_HELMET);
        armor.add(Material.DIAMOND_CHESTPLATE);
        armor.add(Material.DIAMOND_LEGGINGS);
        armor.add(Material.DIAMOND_BOOTS);
        armor.add(Material.NETHERITE_HELMET);
        armor.add(Material.NETHERITE_CHESTPLATE);
        armor.add(Material.NETHERITE_LEGGINGS);
        armor.add(Material.NETHERITE_BOOTS);
        //endregion
        if (!(armor.contains(hand.getType()))) {
            player.sendMessage("You need to hold a piece of armor!");
            return true;
        }
        hand.addEnchantment(ClassSystem.manaReplaceEnchantment, 1);
        ItemMeta meta = hand.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Mana Replace");
        if (meta.hasLore()) {
            for (String l : meta.getLore()) {
                lore.add(l);
            }
        }
        meta.setLore(lore);
        hand.setItemMeta(meta);
        return true;
    }
}
