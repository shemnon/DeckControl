package com.github.shemnon.deckcontrol.skin;

/**
 * Created with IntelliJ IDEA.
 * User: shemnon
 * Date: 17 Sep 2012
 * Time: 8:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class SobolSequences {

    public static int grayCode(int value) {
        return  (value >>> 1) ^ value;
    }

    public static double sobel(int n) {
        if (n > 0x40000000) {
            // clamp it to 2 billion or so.
            throw new IllegalArgumentException("Sobel values only calculated to 2 billion");
        }
        // the bit math works out better when not at signed integer length
        // besides, floats only have 24 bits of precision, and this is 30

        // step one, mirror the bits so b(n) = b(32-n)
        int l = 0x20000000;
        int r = 0x1;
        int result = 0;
        int gray = grayCode(n);
        while (l > 0) {
            if ((gray & r) != 0) {
                result |= l;
            }
            l = l >> 1;
            r = r << 1;
        }
        // divide by a large number to make the binary fraction
        return result / 1073741824.0; // 0x40000000 in double
    }
}
