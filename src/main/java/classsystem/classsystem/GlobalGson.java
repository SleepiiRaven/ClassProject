package classsystem.classsystem;

import classsystem.classsystem.adapters.PlayerDataAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GlobalGson {
    private static GsonBuilder BUILDER = new GsonBuilder().registerTypeAdapter(PlayerData.class, new PlayerDataAdapter()).disableHtmlEscaping();
    public static Gson GSON;

    static {
        GSON = BUILDER.setPrettyPrinting().create();
    }
}
