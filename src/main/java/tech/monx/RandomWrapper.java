package tech.monx;

import java.util.Random;

public class RandomWrapper {
    public static final Random random;

    static {
        random = new Random();
    }
}
