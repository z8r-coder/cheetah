package utils;

import org.apache.log4j.Logger;

/**
 * @author ruanxin
 * @create 2018-02-10
 * @desc
 */

public class StringUtils {

    private final static Logger logger = Logger.getLogger(Configuration.class);
    /**
     * Example: subString("12345","1","4")=23
     *
     * @param src
     * @param start
     * @param to
     * @return
     */
    public static Integer subStringToInteger(String src, String start, String to) {
        return string2Integer(subString(src, start, to));
    }

    /**
     * Example: subString("abcd","a","c")="b"
     *
     * @param src
     * @param start null while start from index=0
     * @param to null while to index=src.length
     * @return
     */
    public static String subString(String src, String start, String to) {
        int indexFrom = start == null ? 0 : src.indexOf(start);
        int indexTo = to == null ? src.length() : src.indexOf(to);
        if (indexFrom < 0 || indexTo < 0 || indexFrom > indexTo) {
            return null;
        }

        if (null != start) {
            indexFrom += start.length();
        }

        return src.substring(indexFrom, indexTo);

    }

    /**
     * Example: subString("abcdc","a","c",true)="bcd"
     *
     * @param src
     * @param start null while start from index=0
     * @param to null while to index=src.length
     * @param toLast true while to index=src.lastIndexOf(to)
     * @return
     */
    public static String subString(String src, String start, String to, boolean toLast) {
        if(!toLast) {
            return subString(src, start, to);
        }
        int indexFrom = start == null ? 0 : src.indexOf(start);
        int indexTo = to == null ? src.length() : src.lastIndexOf(to);
        if (indexFrom < 0 || indexTo < 0 || indexFrom > indexTo) {
            return null;
        }

        if (null != start) {
            indexFrom += start.length();
        }

        return src.substring(indexFrom, indexTo);

    }
    public static Integer string2Integer(String in) {
        if (in == null) {
            return null;
        }
        in = in.trim();
        if (in.length() == 0) {
            return null;
        }

        try {
            return Integer.parseInt(in);
        } catch (NumberFormatException e) {
            logger.warn("string2Integer error string=" + in, e);
            return null;
        }
    }

    public static boolean equals(String a, String b) {
        if (a == null) {
            return b == null;
        }
        return a.equals(b);
    }

    public static boolean equalsIgnoreCase(String a, String b) {
        if (a == null) {
            return b == null;
        }
        return a.equalsIgnoreCase(b);
    }
    public static int lowerHashCode(String text) {
        if (text == null) {
            return 0;
        }
//        return text.toLowerCase().hashCode();
        int h = 0;
        for (int i = 0; i < text.length(); ++i) {
            char ch = text.charAt(i);
            if (ch >= 'A' && ch <= 'Z') {
                ch = (char) (ch + 32);
            }

            h = 31 * h + ch;
        }
        return h;
    }
    public static boolean isEmpty(CharSequence value) {
        if (value == null || value.length() == 0) {
            return true;
        }

        return false;
    }

    /**
     * 检查charsequence是否只含空格和Null
     * <pre>
     * StringUtils.isBlank(null)      = true
     * StringUtils.isBlank("")        = true
     * StringUtils.isBlank(" ")       = true
     * StringUtils.isBlank("bob")     = false
     * StringUtils.isBlank("  bob  ") = false
     * </pre>
     * @param cs
     * @return
     */
    public static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * 删除字符串中所有字符
     * <pre>
     * StringUtils.deleteWhitespace(null)         = null
     * StringUtils.deleteWhitespace("")           = ""
     * StringUtils.deleteWhitespace("abc")        = "abc"
     * StringUtils.deleteWhitespace("   ab  c  ") = "abc"
     * </pre>
     * @param str
     * @return
     */
    public static String deleteWhiteSpace(final String str) {
        if (isEmpty(str)) {
            return str;
        }
        final int sz = str.length();
        final char[] chs = new char[sz];
        int count = 0;
        for (int i = 0; i < sz;i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                chs[count++] = str.charAt(i);
            }
        }

        if (count == sz) {
            return str;
        }
        return new String(chs,0,count);
    }
    /**
     * 是否是数字
     * @param str
     * @return
     */
    public static boolean isNumber(String str) {
        if (isEmpty(str)) {
            return false;
        }
        char[] chars = str.toCharArray();
        int sz = chars.length;
        boolean hasExp = false;
        boolean hasDecPoint = false;
        boolean allowSigns = false;
        boolean foundDigit = false;
        // deal with any possible sign up front
        int start = (chars[0] == '-') ? 1 : 0;
        if (sz > start + 1) {
            if (chars[start] == '0' && chars[start + 1] == 'x') {
                int i = start + 2;
                if (i == sz) {
                    return false; // str == "0x"
                }
                // checking hex (it can't be anything else)
                for (; i < chars.length; i++) {
                    if ((chars[i] < '0' || chars[i] > '9')
                            && (chars[i] < 'a' || chars[i] > 'f')
                            && (chars[i] < 'A' || chars[i] > 'F')) {
                        return false;
                    }
                }
                return true;
            }
        }
        sz--; // don't want to loop to the last char, check it afterwords
        // for type qualifiers
        int i = start;
        // loop to the next to last char or to the last char if we need another digit to
        // make a valid number (e.g. chars[0..5] = "1234E")
        while (i < sz || (i < sz + 1 && allowSigns && !foundDigit)) {
            if (chars[i] >= '0' && chars[i] <= '9') {
                foundDigit = true;
                allowSigns = false;

            } else if (chars[i] == '.') {
                if (hasDecPoint || hasExp) {
                    // two decimal points or dec in exponent
                    return false;
                }
                hasDecPoint = true;
            } else if (chars[i] == 'e' || chars[i] == 'E') {
                // we've already taken care of hex.
                if (hasExp) {
                    // two E's
                    return false;
                }
                if (!foundDigit) {
                    return false;
                }
                hasExp = true;
                allowSigns = true;
            } else if (chars[i] == '+' || chars[i] == '-') {
                if (!allowSigns) {
                    return false;
                }
                allowSigns = false;
                foundDigit = false; // we need a digit after the E
            } else {
                return false;
            }
            i++;
        }
        if (i < chars.length) {
            if (chars[i] >= '0' && chars[i] <= '9') {
                // no type qualifier, OK
                return true;
            }
            if (chars[i] == 'e' || chars[i] == 'E') {
                // can't have an E at the last byte
                return false;
            }
            if (!allowSigns
                    && (chars[i] == 'd'
                    || chars[i] == 'D'
                    || chars[i] == 'f'
                    || chars[i] == 'F')) {
                return foundDigit;
            }
            if (chars[i] == 'l'
                    || chars[i] == 'L') {
                // not allowing L with an exponent
                return foundDigit && !hasExp;
            }
            // last character is illegal
            return false;
        }
        // allowSigns is true iff the val ends in 'E'
        // found digit it to make sure weird stuff like '.' and '1E-' doesn't pass
        return !allowSigns && foundDigit;
    }
}
