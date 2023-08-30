package me.sirimperivm.spigot.assets.others;

import me.sirimperivm.spigot.assets.utils.Colors;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SuppressWarnings("all")
public class General {

    public static List<String> lore(List<String> li) {
        List<String> cL = new ArrayList<String>();
        for (String l : li) {
            cL.add(Colors.text(l));
        }
        return cL;
    }

    public static int generateRandomNumber(int min, int max) {
        Random rand = new Random();
        int toReturn = rand.nextInt(min, max);
        return toReturn;
    }
}
