package classsystem.classsystem;

import org.bukkit.Material;
import org.bukkit.Tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemManager {
    public static final List<Material> clericWeapons = Arrays.asList(
            Material.WOODEN_HOE,
            Material.STONE_HOE,
            Material.GOLDEN_HOE,
            Material.IRON_HOE,
            Material.DIAMOND_HOE,
            Material.NETHERITE_HOE
    );
    public static final List<Material> mageWeapons = Arrays.asList(
            Material.STICK
    );
    public static final List<Material> rogueWeapons = Arrays.asList(
            Material.WOODEN_SWORD,
            Material.STONE_SWORD,
            Material.GOLDEN_SWORD,
            Material.IRON_SWORD,
            Material.DIAMOND_SWORD,
            Material.NETHERITE_SWORD
    );
    public static final List<Material> scoutWeapons = Arrays.asList(
            Material.BOW,
            Material.CROSSBOW,
            Material.TRIDENT
    );
    public static final List<Material> summonerWeapons = Arrays.asList();
    public static final List<Material> warriorWeapons = Arrays.asList(
            Material.WOODEN_AXE,
            Material.STONE_AXE,
            Material.GOLDEN_AXE,
            Material.IRON_AXE,
            Material.DIAMOND_AXE,
            Material.NETHERITE_AXE
    );
}
