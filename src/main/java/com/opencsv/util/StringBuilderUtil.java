package com.opencsv.util;

public class StringBuilderUtil {


    /**
     * Goes through the value of a stringBuilder and replaces all occurrences of the findString with the value of the
     * replaceString.
     * <p>
     * If the replaceString is null or is an empty string then all occurences of findString will be removed from the
     * StringBuilder.
     *
     * @param stringBuilder - original value
     * @param findString    - substring to find
     * @param replaceString
     * @return the StringBuilder object if it is null, the findString is null or empty or the length of the findString
     * is greater than the length of the stringBuilder.
     */
    public static StringBuilder replaceAll(StringBuilder stringBuilder, String findString, String replaceString) {
        if (stringBuilder == null || findString == null || findString.length() == 0 || stringBuilder.length() < findString.length()) {
            return stringBuilder;
        }
        String innerReplaceString = replaceString == null ? "" : replaceString;

        for (int start = stringBuilder.indexOf(findString); start != -1; start = stringBuilder.indexOf(findString, start + innerReplaceString.length())) {
            stringBuilder.replace(start, start + findString.length(), innerReplaceString);
        }
        return stringBuilder;
    }
}
