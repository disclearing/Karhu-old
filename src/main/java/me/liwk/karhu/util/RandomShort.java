package me.liwk.karhu.util;

import java.util.Random;

public class RandomShort extends Random {
    public short nextShort() {
        return (short) next(16); // give me just 16 bits.
    }

    public short nextShort2() {
        return (short) next(8); // give me just 16 bits.
    }
}
