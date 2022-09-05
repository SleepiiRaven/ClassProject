package classsystem.classsystem.handlers.clickHandler;

import classsystem.classsystem.ClassSystem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerClassListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        ClassSystem plugin = ClassSystem.getInstance();
        Player p = e.getPlayer();
        String pUUID = p.getUniqueId().toString();
        PlayerClass playerClass = PlayerClass.stringToClass(plugin.getConfig().getString(pUUID + ".class"));
        PlayerClassTemplate playerClassTemplate = playerClass.supplier.get();
        playerClassTemplate.onTrigger(e);
    }
}