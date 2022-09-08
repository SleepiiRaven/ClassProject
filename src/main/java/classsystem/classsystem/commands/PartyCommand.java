package classsystem.classsystem.commands;

import classsystem.classsystem.ClassSystem;
import classsystem.classsystem.Party;
import classsystem.classsystem.PartyManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class PartyCommand implements CommandExecutor {
    PartyManager party;
    public PartyCommand(PartyManager party) {
        this.party = party;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String [] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can execute this command!");
            return true;
        }
        Player p = (Player) sender;
        UUID pUUID = p.getUniqueId();
        if (args.length > 0) {
            String subCommand = args[0];
                switch (subCommand.toLowerCase()) {
                    case "invite":
                        if (args.length > 1) {
                            Player target = isPlayer(args, p);
                            //region Checks
                            if (target == null) {
                                p.sendMessage(ChatColor.RED + "That is not a player! Correct usage: /party invite [Player Name]");
                                return true;
                            }
                            if (target == p) {
                                p.sendMessage(ChatColor.RED + "You can't invite yourself to a party!");
                                return true;
                            }
                            party.invitePlayer(pUUID, target.getUniqueId());
                            p.sendMessage(ChatColor.GREEN + "You invited " + target.getName() + " to your party!");
                            //endregion
                        } else {
                            p.sendMessage(ChatColor.RED + "You must put a player's name after /p invite!");
                        }
                        break;
                    case "remove":
                        if (args.length > 1) {
                            Player targetRemove = isPlayer(args, p);
                            //region Checks
                            if (targetRemove == null) {
                                sender.sendMessage(ChatColor.RED + "That is not a player! Correct usage: /party remove [Player Name]");
                                return true;
                            }
                            if (targetRemove == p) {
                                targetRemove.sendMessage(ChatColor.RED + "You can't remove yourself from a party! Use '/party leave' instead!");
                                return true;
                            }
                            //endregion
                            if (party.removePlayer(party.findParty(pUUID), targetRemove.getUniqueId())) {
                                p.sendMessage(ChatColor.RED + targetRemove.getName() + " was removed from your party.");
                            } else {
                                p.sendMessage(ChatColor.RED + "That player is not in your party.");
                            }
                        } else {
                            p.sendMessage(ChatColor.RED + "You must put a player's name after /p remove!");
                        }
                        break;
                    case "accept":
                        if (args.length > 1) {
                            Player targetAccept = isPlayer(args, p);
                            //region Checks
                            if (targetAccept == null) {
                                p.sendMessage(ChatColor.RED + "That is not a player! Correct usage: /party accept [Player Name]");
                                return true;
                            }
                            if (targetAccept == p) {
                                p.sendMessage(ChatColor.RED + "You can't accept your own party invite!");
                                return true;
                            }
                            //endregion
                            boolean invited = party.acceptRequest(targetAccept.getUniqueId(), pUUID);
                            if (invited) {
                                p.sendMessage(ChatColor.GREEN + "You joined " + targetAccept.getName() + "'s party!");
                                targetAccept.sendMessage(ChatColor.GREEN + p.getName() + " joined your party!");
                            } else {
                                p.sendMessage(ChatColor.RED + "You were not invited to that player's party!");
                            }
                        } else {
                            p.sendMessage(ChatColor.RED + "You must put a player's name after /p accept!");
                        }
                        break;
                    case "leave":
                        party.removePlayer(party.findParty(pUUID), pUUID);
                        p.sendMessage(ChatColor.RED + "You left the party.");
                        break;
                    case "list":
                        List<Player> partyMembers = party.listPartyMembers(pUUID);
                        if (partyMembers == null) {
                            p.sendMessage(ChatColor.RED + "You are not in a party!");
                            return true;
                        }
                        p.sendMessage("Party Members:");
                        for (Player partyMember : partyMembers) {
                            String partyMemberName = partyMember.getName();
                            if (partyMemberName.equals(p.getName())) {
                                p.sendMessage(ChatColor.GOLD + partyMemberName);
                            } else {
                                p.sendMessage(ChatColor.GREEN + partyMemberName);
                            }
                        }
                        break;
                }
        } else {
            return false;
        }
        return true;
    }

    public Player isPlayer(String [] args, Player p) {
        if (args.length > 1) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                return null;
            }
            if (!(target.isOnline())) {
                return null;
            }
            return target;
        }
        return null;
    }
}
