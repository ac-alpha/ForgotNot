package in.ashutoshchaubey.forgotnot.utilities;

import android.nfc.Tag;
import android.util.Log;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ashutoshchaubey on 01/08/18.
 */

public class DateTimeInfoUtil {

    private static String TAG = "DateTimeInfoUtil";
    private static String[] timeRegex = new String[]{"[0-9]{1,2} *:?\\.? *[0-9]{0,2} *[AaMmPp]{2}", "[0-9]{1,2} *[oO] *\'? *[CLOCKclock]{5}"};
    private static String[] dateRegex = new String[]{"[0-9]{1,2} *[./-] *[0-9]{1,2} *[./-] *[0-9]{0,4}", "[0-9]{1,2} ?[a-zA-Z]{0,2} *[a-zA-Z]{3,9} *,? *[0-9]{0,4}"};
    private static ArrayList<String> months = new ArrayList<>(Arrays.asList(new DateFormatSymbols().getMonths()));

    public static boolean findDate(String data) {

        Pattern pattern;
        int i;
        for (i = 0; i < 2; i++) {

            pattern = Pattern.compile(dateRegex[i]);
            Matcher matcher = pattern.matcher(data);
            if (matcher.find()) {
                return true;
            }

        }


        return false;

    }

    public static Calendar extractDate(String data) {

        Pattern pattern;
        int matchedInd = -1;
        String matchedDate = "";
        int i;
        for (i = 0; i < 2; i++) {

            pattern = Pattern.compile(dateRegex[i]);
            Matcher matcher = pattern.matcher(data);
            if (matcher.find()) {
                matchedInd = matcher.start();
                matchedDate = matcher.group();
                break;
            }

        }

        Calendar date = Calendar.getInstance();
        matchedDate = matchedDate.replaceAll(",", "");
        matchedDate = matchedDate.replaceAll("rd ", "");
        matchedDate = matchedDate.replaceAll("st ", "");
        matchedDate = matchedDate.replaceAll("nd ", "");
        matchedDate = matchedDate.replaceAll(" ", "");

        Log.i(TAG, matchedDate);
        int j = 0;
        int l = matchedDate.length();
        if (matchedInd != -1) {
            if (i == 0) {
                if ((Character.isDigit(matchedDate.charAt(j)) && Character.isDigit(matchedDate.charAt(j + 1))) && !(matchedDate.charAt(j) == '0')) {
                    date.set(Calendar.DATE, Integer.parseInt(matchedDate.substring(0, 2)));
                    Log.i(TAG, "Date 1");
                    j += 2;
                } else if (matchedDate.charAt(j) == '0') {
                    date.set(Calendar.DATE, Integer.parseInt(Character.toString(matchedDate.charAt(j + 1))));
                    Log.i(TAG, "Date 2");
                    j += 2;
                } else {
                    date.set(Calendar.DATE, Integer.parseInt(Character.toString(matchedDate.charAt(j))));
                    Log.i(TAG, "Date 3");
                    j += 1;
                }
                j += 1;
                if ((Character.isDigit(matchedDate.charAt(j)) && Character.isDigit(matchedDate.charAt(j + 1))) && !(matchedDate.charAt(j) == '0')) {
                    date.set(Calendar.MONTH, Integer.parseInt(matchedDate.substring(0, 2)) - 1);
                    j += 2;
                } else if (matchedDate.charAt(j) == '0') {
                    date.set(Calendar.MONTH, Integer.parseInt(Character.toString(matchedDate.charAt(j + 1))) - 1);
                    j += 2;
                } else {
                    date.set(Calendar.MONTH, Integer.parseInt(Character.toString(matchedDate.charAt(j))) - 1);
                    j += 1;
                }
                j += 1;
                Log.i(TAG, matchedDate.substring(j, l));
                Log.i(TAG, (l - j) + "");
                if ((l - j) == 4) {
                    Log.i(TAG, "YYYY");
                    date.set(Calendar.YEAR, Integer.parseInt(matchedDate.substring(j, l)));
                    Log.i(TAG, date.get(Calendar.YEAR) + "");
                } else if (l - j == 2) {
                    Log.i(TAG, "YY");
                    date.set(Calendar.YEAR, 2000 + Integer.parseInt(matchedDate.substring(j, l)));
                    Log.i(TAG, date.get(Calendar.YEAR) + "");
                }

            } else if (i == 1) {
                if ((Character.isDigit(matchedDate.charAt(j)) && Character.isDigit(matchedDate.charAt(j + 1))) && !(matchedDate.charAt(j) == '0')) {
                    date.set(Calendar.DATE, Integer.parseInt(matchedDate.substring(0, 2)));
                    Log.i(TAG, "Date 1");
                    j += 2;
                } else if (matchedDate.charAt(j) == '0') {
                    date.set(Calendar.DATE, Integer.parseInt(Character.toString(matchedDate.charAt(j + 1))));
                    Log.i(TAG, "Date 2");
                    j += 2;
                } else {
                    date.set(Calendar.DATE, Integer.parseInt(Character.toString(matchedDate.charAt(j))));
                    Log.i(TAG, "Date 3");
                    j += 1;
                }

                if ((j + 3 == l)) {
                    for (String s : months) {
                        if (s.substring(0, 3).toLowerCase().equals(matchedDate.substring(j, l).toLowerCase())) {
                            date.set(Calendar.MONTH, months.indexOf(s) + 1);
                        }
                    }
                    j += 3;
                } else if (Character.isDigit(matchedDate.charAt(j + 3))) {
                    for (String s : months) {
                        if (s.substring(0, 3).toLowerCase().equals(matchedDate.substring(j, l).toLowerCase())) {
                            date.set(Calendar.MONTH, months.indexOf(s) + 1);
                        }
                    }
                    j += 3;
                } else {
                    for (String s : months) {
                        if (s.toLowerCase().equals(matchedDate.toLowerCase())) {
                            date.set(Calendar.MONTH, months.indexOf(s) + 1);
                            j += s.length();
                        }
                    }
                }

                if ((l - j) == 4) {
                    Log.i(TAG, "YYYY");
                    date.set(Calendar.YEAR, Integer.parseInt(matchedDate.substring(j, l)));
                    Log.i(TAG, date.get(Calendar.YEAR) + "");
                } else if (l - j == 2) {
                    Log.i(TAG, "YY");
                    date.set(Calendar.YEAR, 2000 + Integer.parseInt(matchedDate.substring(j, l)));
                    Log.i(TAG, date.get(Calendar.YEAR) + "");
                }

            }
        }

        return date;

    }

    public static Calendar extractTime(String data) {

        Calendar time = Calendar.getInstance();

        Pattern pattern;
        int matchedInd = -1;
        String matchedTime = "";
        int i;
        for (i = 0; i < 2; i++) {

            pattern = Pattern.compile(timeRegex[i]);
            Matcher matcher = pattern.matcher(data);
            if (matcher.find()) {
                matchedInd = matcher.start();
                matchedTime = matcher.group();
                break;
            }

        }

        matchedTime = matchedTime.replaceAll(" ", "");
        int j = 0;
        int l = matchedTime.length();
        Log.i(TAG, matchedTime);
        if (matchedInd != -1) {
            if(i==0){

                if ((Character.isDigit(matchedTime.charAt(j)) && Character.isDigit(matchedTime.charAt(j + 1))) && !(matchedTime.charAt(j) == '0')) {
                    time.set(Calendar.HOUR, Integer.parseInt(matchedTime.substring(j, j+2)));
                    Log.i(TAG, "Date 1");
                    j += 2;
                } else if (matchedTime.charAt(j) == '0') {
                    time.set(Calendar.HOUR, Integer.parseInt(Character.toString(matchedTime.charAt(j + 1))));
                    Log.i(TAG, "Date 2");
                    j += 2;
                } else {
                    time.set(Calendar.HOUR, Integer.parseInt(Character.toString(matchedTime.charAt(j))));
                    Log.i(TAG, "Date 3");
                    j += 1;
                }
                if((matchedTime.charAt(j) == ':') || (matchedTime.charAt(j) == '.')){
                    j+=1;
                }
                if(Character.toLowerCase(matchedTime.charAt(j+1)) == 'm'){
                    time.set(Calendar.MINUTE, 0);
                    if(matchedTime.substring(j,j+2).toLowerCase().equals("am")){
                        time.set(Calendar.AM_PM, Calendar.AM);
                    }else if(matchedTime.substring(j,j+2).toLowerCase().equals("pm")){
                        time.set(Calendar.AM_PM, Calendar.PM);
                    }
                }else {
                    if ((Character.isDigit(matchedTime.charAt(j)) && Character.isDigit(matchedTime.charAt(j + 1))) && !(matchedTime.charAt(j) == '0')) {
                        time.set(Calendar.MINUTE, Integer.parseInt(matchedTime.substring(j, j+2)));
                        j += 2;
                    } else if (matchedTime.charAt(j) == '0') {
                        time.set(Calendar.MINUTE, Integer.parseInt(Character.toString(matchedTime.charAt(j + 1))));
                        j += 2;
                    } else {
                        time.set(Calendar.MINUTE, Integer.parseInt(Character.toString(matchedTime.charAt(j))));
                        j += 1;
                    }
                    if(matchedTime.substring(j,j+2).toLowerCase().equals("am")){
                        time.set(Calendar.AM_PM, Calendar.AM);
                    }else if(matchedTime.substring(j,j+2).toLowerCase().equals("pm")){
                        time.set(Calendar.AM_PM, Calendar.PM);
                    }
                }

            }
        }

        return time;

    }

    public static boolean findTime(String data) {

        Pattern pattern;
        int i;
        for (i = 0; i < 2; i++) {

            pattern = Pattern.compile(timeRegex[i]);
            Matcher matcher = pattern.matcher(data);
            if (matcher.find()) {
                return true;
            }

        }
        return false;

    }

    public static Calendar extractDateTime(String data){

        Calendar result = Calendar.getInstance();

        if(findDate(data)){
            result = extractDate(data);
        }

        if (findTime(data)){
            result.set(Calendar.HOUR, extractTime(data).get(Calendar.HOUR));
            result.set(Calendar.MINUTE, extractTime(data).get(Calendar.MINUTE));
        }

        return result;

    }

}
