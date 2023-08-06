package me.sirimperivm.spigot.assets.others;

import java.util.UUID;

@SuppressWarnings("all")
public class Strings {

    public static String generateUUID() {
        String generated = UUID.randomUUID().toString().replace("-", "");
        return generated;
    }
}
