package classsystem.classsystem.handlers;

import classsystem.classsystem.ClassSystem;
import classsystem.classsystem.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.UUID;

public class DeathHandler implements Listener {
    private final ClassSystem plugin;
    public DeathHandler(ClassSystem plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        LivingEntity killer = e.getEntity().getKiller();
        if (!(killer instanceof Player)) return;
        PlayerData killerData = PlayerData.getPlayerData(killer.getUniqueId());
        if (!killerData.getPlayerClass().equals("summoner")) return;
        killerData.setSouls((int) killerData.getSouls() + 1);
        System.out.println(killerData.getSouls());
    }
    public void onPlayerDeath(PlayerDeathEvent e) {
        String name = e.getEntity().getName();
        String deathType;
        String killer;
        Bukkit.broadcastMessage(e.getEntity().getName() + " was killed by " + e.getEntity().getKiller());
    }
}
