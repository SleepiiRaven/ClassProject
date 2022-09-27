package classsystem.classsystem;

import classsystem.classsystem.commands.*;
import classsystem.classsystem.enchantments.ManaReplaceEnchantment;
import classsystem.classsystem.handlers.*;
import classsystem.classsystem.handlers.classHandlers.PlayerClassListener;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class ClassSystem extends JavaPlugin {
    private static ClassSystem instance;
    public static ManaReplaceEnchantment manaReplaceEnchantment;
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
    //region Enchantments
    public static void registerEnchantment(Enchantment enchantment) {
        boolean registered = true;
        try {
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
            Enchantment.registerEnchantment(enchantment);
        } catch (Exception e) {
            registered = false;
            e.printStackTrace();
        }
        if (registered) {

        }
    }

    public static void unregisterEnchantment(Enchantment enchant) {
        try {
            Field keyField = Enchantment.class.getDeclaredField("byKey");
            keyField.setAccessible(true);
            @SuppressWarnings("unchecked")
            HashMap<NamespacedKey, Enchantment> byKey = (HashMap<NamespacedKey, Enchantment>) keyField.get(null);
            if (byKey.containsKey(enchant.getKey())) {
                byKey.remove(enchant.getKey());
            }
            Field nameField = Enchantment.class.getDeclaredField("byName");
            nameField.setAccessible(true);
            @SuppressWarnings("unchecked")
            HashMap<String, Enchantment> byName = (HashMap<String, Enchantment>) nameField.get(null);
            if (byName.containsKey(enchant.getName())) {
                byName.remove(enchant.getName());
            }
        } catch (Exception ignored) {}
    }
    //endregion
    //region Enable
    @Override
    public void onEnable() {
        instance = this;
        cdInstance = new CooldownManager();
        partyInstance = new PartyManager();
        manaReplaceEnchantment = new ManaReplaceEnchantment("manaReplace");
        saveDefaultConfig();
        //region Register Commands
        getCommand("fly").setExecutor(new FlyCommand());
        getCommand("god").setExecutor(new GodCommand());
        getCommand("items").setExecutor(new ItemsCommand());
        getCommand("enchantment").setExecutor(new EnchantCommand());
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
        new DeathHandler(this);
        new PlayerClassListener();
        this.getServer().getPluginManager().registerEvents(new PlayerClassListener(), this);
        this.getServer().getPluginManager().registerEvents(new DeathHandler(this), this);

        registerEnchantment(manaReplaceEnchantment);
        //endregion
    }
    //endregion
    //region Disable
    @Override
    public void onDisable() {
        PlayerData.saveAll();
        unregisterEnchantment(manaReplaceEnchantment);
    }
    //endregion
}
