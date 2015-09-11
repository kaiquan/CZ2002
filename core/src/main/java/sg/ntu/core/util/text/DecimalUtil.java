package sg.ntu.core.util.text;

import android.location.Location;

import java.text.DecimalFormat;

/**
 * @author khanhnv
 */
public class DecimalUtil {

    /**
     * using WSG84 using the Metric system
     */
    public static float getDistance(double startLati, double startLongi,
                                    double goalLati, double goalLongi) {
        float[] resultArray = new float[99];
        Location.distanceBetween(startLati, startLongi, goalLati, goalLongi,
                resultArray);
        return resultArray[0];
    }

    /**
     * @param meters
     * @return
     */
    public static int showKilometers(double meters) {
        int kilometers = (int) (meters * 0.001);
        return kilometers;
    }

    /**
     * @param meters
     * @return
     */
    public static String conversion2Kilometers(double meters) {
        if (meters % 100 >= 0) {
            int kilometers = (int) (meters * 0.001);
            return kilometers + "km";
        } else {
            return meters + "m";
        }
    }

    /**
     * @param value
     * @param pattern
     * @return
     */
    public static String decimalFormat(int value, String pattern) {
        DecimalFormat formatter = new DecimalFormat(pattern != null ? pattern : "#00.###");
        return formatter.format(value);
    }

    public static int hex2decimal(String string) {
        String digits = "0123456789ABCDEF";
        string = string.toUpperCase();
        int val = 0;
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            int d = digits.indexOf(c);
            val = 16 * val + d;
        }
        return val;
    }

    // precondition: d is a nonnegative integer
    public static String decimal2hex(int d) {
        String digits = "0123456789ABCDEF";
        if (d == 0)
            return "0";
        String hex = "";
        while (d > 0) {
            int digit = d % 16; // rightmost digit
            hex = digits.charAt(digit) + hex; // string concatenation
            d = d / 16;
        }
        return hex;
    }

    /**
     * @param value
     * @param places
     * @return
     */
    public static double round(double value, int places) {
        if (places < 0)
            throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
