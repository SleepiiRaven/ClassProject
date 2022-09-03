package classsystem.classsystem.handlers;

import classsystem.classsystem.ClassSystem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class PlaceBreakHandler implements Listener {
    // Registering the events.
    public PlaceBreakHandler(ClassSystem plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Types of Priorities
     * Lowest
     * Low
     * Normal
     * High
     * Highest
     * ---
     * Monitor // Everything has already happened, it's just to monitor what's going on, can't change anything //
     **/

    // Set event priority here to low, to do things first
    @EventHandler(priority = EventPriority.LOW)
    // During the BlockPlaceEvent
    public void onBlockPlaced (BlockPlaceEvent event) {
        if (event.getPlayer().hasPermission("classsystem.block.break")) {
            return;
        } else {
            event.getPlayer().sendMessage(ChatColor.RED + "You can't place blocks here.");
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockBroken (BlockBreakEvent event) {
        if (event.getPlayer().hasPermission("classsystem.block.break")) {
            return;
        } else {
            event.getPlayer().sendMessage(ChatColor.RED + "You can't break blocks here.");
            event.setCancelled(true);
        }
    }
}
