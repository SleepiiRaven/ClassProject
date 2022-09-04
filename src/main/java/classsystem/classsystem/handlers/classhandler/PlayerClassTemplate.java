package classsystem.classsystem.handlers.classhandler;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public abstract class PlayerClassTemplate {

    public abstract void onTrigger(PlayerInteractEvent e);

}
