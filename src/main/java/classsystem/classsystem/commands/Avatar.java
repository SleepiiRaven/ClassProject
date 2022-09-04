package classsystem.classsystem.commands;

import classsystem.classsystem.ClassSystem;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Avatar implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String [] args) {
        // If the sender isn't a Player
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can run this command.");
            return true;
        }

        Player player = (Player) sender;
        String pUUIDString = player.getUniqueId().toString();

        ClassSystem plugin = ClassSystem.getInstance();
        plugin.getConfig().set(pUUIDString + ".class", "avatar");
        plugin.saveConfig();

        return true;
    }
}
