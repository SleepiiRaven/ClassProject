package classsystem.classsystem.handlers.classHandlers;

import classsystem.classsystem.ClassSystem;
import classsystem.classsystem.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import java.util.UUID;

public class PlayerClassListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        UUID pUUID = p.getUniqueId();
        PlayerClass playerClass = PlayerClass.stringToClass(PlayerData.getPlayerData(pUUID).getPlayerClass());
        PlayerClassTemplate playerClassTemplate = playerClass.supplier.get();
        playerClassTemplate.onTrigger(e);
    }

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent e) {
        Player p = e.getPlayer();
        UUID pUUID = p.getUniqueId();
        PlayerClass playerClass = PlayerClass.stringToClass(PlayerData.getPlayerData(pUUID).getPlayerClass());
        PlayerClassTemplate playerClassTemplate = playerClass.supplier.get();
        playerClassTemplate.onTriggerSwap(e);
    }
}