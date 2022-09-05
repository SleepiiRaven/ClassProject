package classsystem.classsystem.handlers.clickHandler;

import classsystem.classsystem.ClassSystem;
import classsystem.classsystem.CooldownManager;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerSummonerHandler extends PlayerClassTemplate {
    ClassSystem plugin = ClassSystem.getInstance();
    CooldownManager cooldownManager = plugin.getCdInstance();
    @Override
    public void onTrigger(PlayerInteractEvent e) {

    }
}
