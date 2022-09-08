package classsystem.classsystem.commands;

import classsystem.classsystem.ClassSystem;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GodCommand implements CommandExecutor, ClassSystem.Variables {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String [] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can run this command");
            return true;
        }

        Player p = (Player) sender;
        if (!(godMode.contains(p.getName()))) {
            godMode.add(p.getName());
            sender.sendMessage(ChatColor.GOLD + "GodMode is now on!");
        } else {
            godMode.remove(p.getName());
            sender.sendMessage(ChatColor.GOLD + "God mode has been turned off.");
        }

        return true;
    }
}
