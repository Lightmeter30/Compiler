package back;

import java.util.regex.Pattern;

public class Tools {
    public static final int N = 32;
    public static final Pattern isDigit = Pattern.compile("\\d*");

    public static boolean  isBeginByNumber(String oprand) {
        return isDigit.matcher(oprand).matches() ||
                oprand.charAt(0) == '+' || oprand.charAt(0) == '-';
    }


    public static boolean isTwoPower(Integer integer) {
        int x = integer;
        return (x & (x - 1)) == 0;
    }
}
