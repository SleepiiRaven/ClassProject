package classsystem.classsystem;

import classsystem.classsystem.commands.*;
import classsystem.classsystem.handlers.PlaceBreakHandler;
import classsystem.classsystem.handlers.DamageHandler;
import classsystem.classsystem.handlers.PlayerHandler;
import classsystem.classsystem.handlers.clickHandler.PlayerClassListener;
import classsystem.classsystem.handlers.SleepHandler;
import org.bukkit.block.data.type.Bed;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.ArrayList;
import java.util.List;

public final class ClassSystem extends JavaPlugin {
    private static ClassSystem instance;
    private PartyManager partyInstance;
    private CooldownManager cdInstance;
    public PartyManager getPartyInstance() { return partyInstance; }
    public CooldownManager getCdInstance() {
        return cdInstance;
    }
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
        cdInstance = new CooldownManager();
        partyInstance =  new PartyManager();
        saveDefaultConfig();
        //region Register Commands
        getCommand("fly").setExecutor(new FlyCommand());
        getCommand("god").setExecutor(new GodCommand());
        getCommand("menu").setExecutor(new MenuCommand(this));
        getCommand("avatar").setExecutor(new AvatarCommand());
        getCommand("party").setExecutor(new PartyCommand(partyInstance));
        this.getServer().getPluginManager().registerEvents(new MenuCommand(this), this);
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
