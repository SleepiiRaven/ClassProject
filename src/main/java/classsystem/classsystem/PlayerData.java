package classsystem.classsystem;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData {

    private static final Path STORAGE_FOLDER = ClassSystem.getInstance().getDataFolder().toPath().resolve("PlayerData");
    private static final Map<UUID, PlayerData> cacheMap = new HashMap<>();

    private final Path storage;
    private final UUID uuid;
    private String playerClass = "none";
    private int mana = 100;
    private double dmgModifier = 1;
    private double cdModifier = 1;
    private double kbModifier = 1;
    private double rangeModifier = 1;
    private double hpModifier = 1;
    private int souls = 0;

    @Override
    public String toString() {
        return "PlayerData{" +
                "uuid=" + uuid +
                ", playerClass='" + playerClass + '\'' +
                ", mana=" + mana +
                ", dmgModifier=" + dmgModifier +
                ", cdModifier=" + cdModifier +
                ", kbModifier=" + kbModifier +
                ", rangeModifier=" + rangeModifier +
                ", hpModifier=" + hpModifier +
                ", souls=" + souls +
                '}';
    }
    //region Getters

    public UUID getUuid() {
        return uuid;
    }

    public String getPlayerClass() {
        return playerClass;
    }

    public int getMana() {
        return mana;
    }

    public double getDmgModifier() {
        return dmgModifier;
    }

    public double getCdModifier() {
        return cdModifier;
    }

    public double getKbModifier() {
        return kbModifier;
    }

    public double getRangeModifier() {
        return rangeModifier;
    }

    public double getHpModifier() {
        return hpModifier;
    }

    public double getSouls() {
        return souls;
    }

    //endregion
    //region Setters

    public void setPlayerClass(String playerClass) {
        this.playerClass = playerClass;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public void setDmgModifier(double dmgModifier) {
        this.dmgModifier = dmgModifier;
    }

    public void setCdModifier(double cdModifier) {
        this.cdModifier = cdModifier;
    }

    public void setKbModifier(double kbModifier) {
        this.kbModifier = kbModifier;
    }

    public void setRangeModifier(double rangeModifier) {
        this.rangeModifier = rangeModifier;
    }

    public void setHpModifier(double hpModifier) {
        this.hpModifier = hpModifier;
    }

    public void setSouls(int souls) {
        this.souls = souls;
    }

    //endregion
    public PlayerData(final UUID uuid) {
        this.uuid = uuid;
        this.storage = STORAGE_FOLDER.resolve(uuid.toString() + ".json");
    }

    public static PlayerData getPlayerData(UUID uuid) {
        PlayerData data = cacheMap.getOrDefault(uuid, null);
        if (data == null) {
            Path path = STORAGE_FOLDER.resolve(uuid.toString() + ".json");
            if (!Files.exists(path)) {
                data = new PlayerData(uuid);
                return data;
            }
            try {
                // getting the data
                String json = Files.readString(path);
                data = GlobalGson.GSON.fromJson(json, PlayerData.class);
            } catch (IOException e) {
                // print the error
                e.printStackTrace();
            }
            cacheMap.put(uuid, data);
        }
        return data;
    }

    // says that it could throw an ioexception but dont error it
    public void createJSON() throws IOException {
        if (!Files.exists(this.storage)) {
            if (!Files.exists(this.storage.getParent())) {
                Files.createDirectory(this.storage.getParent());
            }
            Files.createFile(this.storage);
        }
    }

    public static void saveAll() {
        cacheMap.forEach((k, v) -> v.saveAndDelete());
    }

    public void save() {
        try {
            this.createJSON();
            Files.writeString(this.storage, GlobalGson.GSON.toJson(this));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveAndDelete() {
        save();
        cacheMap.remove(this,uuid);
    }
}