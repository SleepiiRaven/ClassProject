package classsystem.classsystem.handlers.classhandler;

import java.util.function.Supplier;

public enum PlayerClass {
    MAGE("mage",PlayerMageHandler::new),
    ROUGE("rogue",PlayerRogueHandler::new),
    AVATAR("avatar",PlayerAvatarHandler::new);

    final String name;
    Supplier<PlayerClassTemplate> supplier;

    PlayerClass(String name, Supplier<PlayerClassTemplate> supplier) {
        this.name = name;
        this.supplier = supplier;
    }

    public static PlayerClass stringToClass(String string) {
        for (PlayerClass value : PlayerClass.values()) {
            if (value.name.equalsIgnoreCase(string)) return value;
        }
        return null;
    }
}
