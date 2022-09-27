package classsystem.classsystem.handlers;

import classsystem.classsystem.ClassSystem;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemListener implements Listener {
    private final ClassSystem plugin;
    public ItemListener(ClassSystem plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent e) {
        Map<Enchantment, Integer> enchant = e.getEnchantsToAdd();
        Enchantment manaReplace = ClassSystem.manaReplaceEnchantment;
        if (enchant.containsKey(manaReplace)) {
            ItemMeta meta = e.getItem().getItemMeta();
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Mana Replace");
            if (meta.hasLore()) {
                for (String l : meta.getLore()) {
                    lore.add(l);
                }
            }
            meta.setLore(lore);
            e.getItem().setItemMeta(meta);
        }
    }


}
