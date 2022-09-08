package classsystem.classsystem;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

public class PartyManager {
    private final Map<UUID, Party> partyMap = new HashMap<>();
    public void invitePlayer(UUID leaderUUID, UUID targetUUID) {
        Party party = partyMap.get(leaderUUID);
        if (party != null) {
            if (party.hasPlayer(targetUUID)) return;
        } else {
            party = new Party(leaderUUID);
            partyMap.put(leaderUUID, party);
        }
        party.invitePlayer(targetUUID);
        Bukkit.getPlayer(targetUUID).sendMessage(ChatColor.GOLD + Bukkit.getPlayer(leaderUUID).getName() + " sent you a party invite! Type /p accept '" + Bukkit.getPlayer(leaderUUID).getName() + "' to accept the request!");
    }
    public boolean acceptRequest(UUID leaderUUID, UUID pUUID) {
        Party party = partyMap.get(leaderUUID);
        if (party == null) return false;
        if (party.isInvited(pUUID)) {
            party.addPlayer(pUUID);
            party.partyInvites.remove(pUUID);
            return true;
        } else return false;
    }
    public boolean removePlayer(Party party, UUID targetUUID) {
        if (party == null) return false;
        party.party.remove(targetUUID);
        if (partyMap.get(targetUUID) != null) {
            partyMap.remove(targetUUID);
            return true;
        }
        return false;
    }
    public Party findParty(UUID pUUID) {
        for (Party value : partyMap.values()) {
            if (value == null) continue;
            if (value.hasPlayer(pUUID)) {
                return value;
            }
        }
        return null;
    }
    public List<Player> listPartyMembers(UUID pUUID) {
        Party party = findParty(pUUID);
        if (party == null) return null;
        List<UUID> partyUUIDS = party.listPlayers();
        if (partyUUIDS==null) return null;
        List<Player> partyMembers = new ArrayList<>();
        for (UUID partyUUID : partyUUIDS) {
            partyMembers.add(Bukkit.getPlayer(partyUUID));
        }
        return partyMembers;
    }
    public UUID getPartyUUID(Party party) {
        return party.getPartyUUID();
    }
}
