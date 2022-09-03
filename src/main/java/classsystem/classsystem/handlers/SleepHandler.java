package classsystem.classsystem.handlers;

import classsystem.classsystem.ClassSystem;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

public class SleepHandler implements Listener {
    public SleepHandler(ClassSystem plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void getInBed(PlayerBedEnterEvent event) {
        World world = event.getBed().getWorld();
        world.setTime(1000);
    }
}
