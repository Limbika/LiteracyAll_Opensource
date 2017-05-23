package com.literacyall.app.utilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by RAFI on 8/9/2016.
 */
public class RegularExpCheck {
    public static boolean IsMatch(String s) {
        try {
            String regex = "\\b(https?|http)://[-a-zA-Z0-9+&@#/%?=_|!:,.;]*[-a-zA-Z0-9+&@#/%=_|]";

            Pattern patt = Pattern.compile(regex);
            Matcher matcher = patt.matcher(s);
            return matcher.matches();
        } catch (RuntimeException e) {
            return false;
        }
    }
}
