package nl.hazenebula.terraingeneration;

import java.util.Random;

public class Util {
    private static final Random rand = new Random();

    public static int randInt(int min, int max) {
        if (max == min) {
            return 0;
        }

        return rand.nextInt(max - min) + min;
    }

    public static double randDouble() {
        return rand.nextDouble();
    }
}
