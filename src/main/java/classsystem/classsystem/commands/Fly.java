package classsystem.classsystem.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Fly implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String [] args) {
        // If the sender isn't a Player
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can run this command.");
            return true;
        }

        Player player = (Player) sender;

        player.setAllowFlight(!player.getAllowFlight());

        boolean flight = player.getAllowFlight();
        player.sendMessage(ChatColor.GOLD + "Flight is now " + flight);

        return true;
    }
}
