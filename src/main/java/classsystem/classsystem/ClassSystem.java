package classsystem.classsystem;

import classsystem.classsystem.commands.Fly;
import classsystem.classsystem.commands.God;
import classsystem.classsystem.commands.Menu;
import classsystem.classsystem.handlers.PlaceBreakHandler;
import classsystem.classsystem.handlers.PlayerHandler;
import classsystem.classsystem.handlers.PlayerAbilityHandler;
import classsystem.classsystem.handlers.SleepHandler;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.ArrayList;
import java.util.List;

public final class ClassSystem extends JavaPlugin {
    //region Variables
    public interface Variables {
        List<String> godMode = new ArrayList<>();
    }
    //endregion
    //region Enable
    @Override
    public void onEnable() {
        saveDefaultConfig();
        //region Register Commands
        getCommand("fly").setExecutor(new Fly());
        getCommand("god").setExecutor(new God());
        getCommand("menu").setExecutor(new Menu(this));
        this.getServer().getPluginManager().registerEvents(new Menu(this), this);
        //endregion
        //region Register Handlers
        new PlayerHandler(this);
        new PlaceBreakHandler(this);
        new SleepHandler(this);
        new PlayerAbilityHandler(this);
        //endregion
    }
    //endregion
    //region Disable
    @Override
    public void onDisable() {
    }
    //endregion
}
