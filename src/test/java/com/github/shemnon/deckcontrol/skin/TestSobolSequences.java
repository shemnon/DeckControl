package com.github.shemnon.deckcontrol.skin;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: shemnon
 * Date: 17 Sep 2012
 * Time: 8:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestSobolSequences {

    int first16Gray[] = {0, 1, 3, 2, 6, 7, 5, 4, 12, 13, 15, 14, 10, 11, 9, 8};

    @Test
    public void fourBitGray() {
        for (int i = 0; i < first16Gray.length; i++) {
            assertEquals(first16Gray[i], SobolSequences.grayCode(i));
        }
    }

    double first16Sobol[] = {
            // Decimal, // binary
            0.0,    //0.0
            0.5,    //0.1
            0.75,   //0.11
            0.25,   //0.01
            0.375,  //0.011
            0.875,  //0.111
            0.625,  //0.101
            0.125,  //0.001
            0.1875, //0.0011
            0.6875, //0.1011
            0.9375, //0.1111
            0.4375, //0.0111
            0.3125, //0.0101
            0.8125, //0.1101
            0.5625, //0.1001
            0.0625, //0.0001
    };

    @Test
    public void sixteenSobol() {
        for (int i = 0; i < first16Gray.length; i++) {
            assertEquals(first16Sobol[i], SobolSequences.sobel(i), 0.00001);
        }
    }
}
