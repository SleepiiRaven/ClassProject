package classsystem.classsystem.handlers.clickHandler;

import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public abstract class PlayerClassTemplate {

    public abstract void onTrigger(PlayerInteractEvent e);
    public abstract void onTriggerSwap(PlayerSwapHandItemsEvent e);
}
