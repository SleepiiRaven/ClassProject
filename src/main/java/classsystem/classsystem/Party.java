package classsystem.classsystem;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Party {
    private final UUID partyUUID;
    List<UUID> party = new ArrayList<>();
    List<UUID> partyInvites = new ArrayList<>();
    public Party(UUID pUUID) {
        addPlayer(pUUID);
        partyUUID = UUID.randomUUID();
    }
    public void invitePlayer(UUID pUUID) {
        partyInvites.add(pUUID);
    }
    public boolean isInvited(UUID pUUID) {
        return partyInvites.contains(pUUID);
    }
    public void addPlayer(UUID pUUID) {
        party.add(pUUID);
    }
    public void removePlayer(UUID pUUID) {
        party.remove(pUUID);
    }
    public boolean hasPlayer(UUID pUUID) {
        return party.contains(pUUID);
    }
    public List<UUID> listPlayers() {
        return party;
    }
    public UUID getPartyUUID() {
        return partyUUID;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        return partyUUID.equals(((Party) obj).partyUUID);
    }
}