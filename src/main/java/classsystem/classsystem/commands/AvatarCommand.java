package classsystem.classsystem.commands;

import classsystem.classsystem.ClassSystem;
import classsystem.classsystem.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class AvatarCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String [] args) {
        // If the sender isn't a Player
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can run this command.");
            return true;
        }

        Player player = (Player) sender;
        UUID pUUID = player.getUniqueId();

        ClassSystem plugin = ClassSystem.getInstance();
        PlayerData.getPlayerData(pUUID).setPlayerClass("avatar");
        player.setMaxHealth(PlayerData.getPlayerData(pUUID).getHpModifier() * 20);
        player.sendMessage(ChatColor.GOLD + "You are now the Avatar");
        return true;
    }
}
