    package classsystem.classsystem.commands;

    import classsystem.classsystem.ClassSystem;
    import org.bukkit.Bukkit;
    import org.bukkit.ChatColor;
    import org.bukkit.Material;
    import org.bukkit.command.Command;
    import org.bukkit.command.CommandExecutor;
    import org.bukkit.command.CommandSender;
    import org.bukkit.entity.Player;
    import org.bukkit.event.EventHandler;
    import org.bukkit.event.Listener;
    import org.bukkit.event.inventory.InventoryClickEvent;
    import org.bukkit.inventory.Inventory;
    import org.bukkit.inventory.ItemStack;
    import org.bukkit.inventory.meta.ItemMeta;

    import java.io.File;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.Timer;
    import java.util.UUID;

    public class Menu implements CommandExecutor, ClassSystem.Variables, Listener {
        private final ClassSystem plugin;
        public Menu(ClassSystem plugin) {
            this.plugin = plugin;
        }
        private String invName = "Class Selector";
        @EventHandler
        public void onInvClick(InventoryClickEvent event) {
            // WHEN ASKING IF A STRING = ANOTHER STRING USE .equals() MUCH LESS BUGGY
            if (!event.getView().getTitle().equals(invName)) {
                return;
            }

            Player p = (Player) event.getWhoClicked();
            int slot = event.getSlot();
            UUID pUUID = p.getUniqueId();
            String pUUIDString = pUUID.toString();
            String playerClass = "none";
            switch(slot) {
                //region Rogue
                case 11:
                    playerClass = "rogue";
                    p.setMaxHealth(20.0 * plugin.getConfig().getDouble(pUUIDString + ".hpMultiplier"));
                    break;
                    //endregion
                //region Warrior
                case 12:
                    playerClass = "warrior";
                    p.setMaxHealth(20.0 * plugin.getConfig().getDouble(pUUIDString + ".hpMultiplier"));
                    break;
                    //endregion
                //region Mage
                case 13:
                    playerClass = "mage";
                    p.setMaxHealth(20.0 * plugin.getConfig().getDouble(pUUIDString + ".hpMultiplier"));
                    break;
                    //endregion
                //region Scout
                case 14:
                    playerClass = "scout";
                    p.setMaxHealth(20.0 * plugin.getConfig().getDouble(pUUIDString + ".hpMultiplier"));
                    break;
                    //endregion
                //region Cleric
                case 15:
                    playerClass = "cleric";
                    p.setMaxHealth(80.0 * plugin.getConfig().getDouble(pUUIDString + ".hpMultiplier"));
                    break;
                    //endregion
                //region Default
                default:
                    playerClass = "none";
                    p.setMaxHealth(20.0 * plugin.getConfig().getDouble(pUUIDString + ".hpMultiplier"));
                    break;
                    //endregion
            }
            plugin.getConfig().set(pUUIDString + ".class", playerClass);

            plugin.saveConfig();

            p.closeInventory();
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String [] args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players can execute this command.");
                return true;
            }

            Player p = (Player) sender;
            // args: owner of inventory, size (columns by rows)
            Inventory inv = Bukkit.createInventory(p, 9 * 3, invName);
            inv.setItem(11, getItem(new ItemStack(Material.DIAMOND_SWORD), "&6Rogue", "&eClick to become a rogue.", "Rogues deal very high damage,", "but they're only single target."));
            inv.setItem(12, getItem(new ItemStack(Material.DIAMOND_AXE), "&6Warrior", "&eClick to become a warrior.", "Warriors deal moderate damage,", "with a moderate amount of AOE."));
            inv.setItem(13, getItem(new ItemStack(Material.STICK), "&6Mage", "&eClick to become a mage.", "Mages deal high damage and AOE,", "but they aren't tanky."));
            inv.setItem(14, getItem(new ItemStack(Material.BOW), "&6Scout", "&eClick to become a scout.", "Scouts deal moderate damage,", "and they're ranged."));
            inv.setItem(15, getItem(new ItemStack(Material.TOTEM_OF_UNDYING), "&6Cleric", "&eClick to become a cleric.", "Clerics deal very low damage,", "but can heal their allies."));

            p.openInventory(inv);

            return true;
        }
        // THE ... IS CREATING A VAR ARG, WHICH MEANS THE LENGTH OF THE ARRAY IS A VARIABLE
        private ItemStack getItem(ItemStack item, String name, String ... lore) {
            ItemMeta meta = item.getItemMeta();

            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

            List<String> lores = new ArrayList<>();
            for (String s : lore) {
                lores.add(ChatColor.translateAlternateColorCodes('&', s));
            }

            meta.setLore(lores);

            item.setItemMeta(meta);

            return item;
        }
    }
