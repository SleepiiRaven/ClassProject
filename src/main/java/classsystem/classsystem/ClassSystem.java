package classsystem.classsystem;

import classsystem.classsystem.commands.Avatar;
import classsystem.classsystem.commands.Fly;
import classsystem.classsystem.commands.God;
import classsystem.classsystem.commands.Menu;
import classsystem.classsystem.handlers.PlaceBreakHandler;
import classsystem.classsystem.handlers.DamageHandler;
import classsystem.classsystem.handlers.PlayerHandler;
import classsystem.classsystem.handlers.clickHandler.PlayerClassListener;
import classsystem.classsystem.handlers.SleepHandler;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.ArrayList;
import java.util.List;

public final class ClassSystem extends JavaPlugin {
    private static ClassSystem instance;

    public static ClassSystem getInstance() {
        return instance;
    }

    //region Variables
    public interface Variables {
        List<String> godMode = new ArrayList<>();
    }
    //endregion
    //region Enable
    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        //region Register Commands
        getCommand("fly").setExecutor(new Fly());
        getCommand("god").setExecutor(new God());
        getCommand("menu").setExecutor(new Menu(this));
        getCommand("avatar").setExecutor(new Avatar());
        this.getServer().getPluginManager().registerEvents(new Menu(this), this);
        //endregion
        //region Register Handlers
        new DamageHandler(this);
        new PlayerHandler(this);
        new PlaceBreakHandler(this);
        new SleepHandler(this);
        new PlayerClassListener();
        this.getServer().getPluginManager().registerEvents(new PlayerClassListener(), this);
        //endregion
    }
    //endregion
    //region Disable
    @Override
    public void onDisable() {
    }
    //endregion
}
