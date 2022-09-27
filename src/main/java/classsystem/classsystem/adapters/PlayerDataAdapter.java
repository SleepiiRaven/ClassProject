package classsystem.classsystem.adapters;

import classsystem.classsystem.PlayerData;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.UUID;

public class PlayerDataAdapter implements JsonSerializer<PlayerData>, JsonDeserializer<PlayerData> {
    @Override
    public JsonElement serialize(PlayerData data, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();

        object.addProperty("uuid", data.getUuid().toString());
        object.addProperty("mana", data.getMana());
        object.addProperty("class", data.getPlayerClass());
        object.addProperty("dmgModifier", data.getDmgModifier());
        object.addProperty("cdModifier", data.getCdModifier());
        object.addProperty("hpModifier", data.getHpModifier());
        object.addProperty("rangeModifier", data.getRangeModifier());
        object.addProperty("kbModifier", data.getKbModifier());
        object.addProperty("souls", data.getSouls());
        return object;
    }

    @Override
    public PlayerData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonObject()) {
            JsonObject object = json.getAsJsonObject();

            PlayerData playerDataJSON = new PlayerData(UUID.fromString(object.get("uuid").getAsString()));
            playerDataJSON.setPlayerClass(object.get("class").getAsString());
            playerDataJSON.setMana(object.get("mana").getAsInt());
            playerDataJSON.setDmgModifier(object.get("dmgModifier").getAsDouble());
            playerDataJSON.setCdModifier(object.get("cdModifier").getAsDouble());
            playerDataJSON.setHpModifier(object.get("hpModifier").getAsDouble());
            playerDataJSON.setRangeModifier(object.get("rangeModifier").getAsDouble());
            playerDataJSON.setKbModifier(object.get("kbModifier").getAsDouble());
            playerDataJSON.setSouls(object.get("souls").getAsInt());

            return playerDataJSON;
        }
        return null;
    }
}
