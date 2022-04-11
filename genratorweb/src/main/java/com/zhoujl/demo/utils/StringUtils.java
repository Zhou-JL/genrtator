package com.zhoujl.demo.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.util.*;
import java.util.regex.Pattern;


/**
 * @author zjl
 * @company 北京汉唐智创科技有限公司
 * @time 2022-2-14 10.19
 */
public class StringUtils {
    private static final Logger LOG = LoggerFactory.getLogger(StringUtils.class);
    private static final String[] sqlKeys = new String[]{"union", "in", "where", "select", "from", "join", "delete", "insert", "or", "and", "group", "having"};

    public StringUtils() {
    }

    public static String trim(String s) {
        if (isNotEmpty(s)) {
            s = s.trim();
        }
        return s;
    }

    public static String trim(String s, final String code) {
        if (s == null) {
            return null;
        } else {
            s = s.trim();
            if (isNullOrEmpty(code)) {
                return s;
            } else {
                while (s.endsWith(code)) {
                    s = s.substring(0, s.length() - 1).trim();
                }

                return s;
            }
        }
    }

    public static void append(StringBuilder builder, String s, Object... args) {
        builder.append(String.format(s, args));
    }

    public static String getIP() {
        InetAddress ia = null;

        try {
            ia = InetAddress.getLocalHost();
            String localip = ia.getHostAddress();
            return localip;
        } catch (Exception var2) {
            var2.printStackTrace();
            return "127.0.0.1";
        }
    }

    public static String hideMiddleFourPhone(String phone) {
        return phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }

    public static boolean equalsIgnoreCase(String first, String sencord) {
        if (first == null && sencord == null) {
            return true;
        } else {
            return first != null && first.equalsIgnoreCase(sencord);
        }
    }

    public static boolean equalsSuccess(String value) {
        return "SUCCESS".equalsIgnoreCase(value);
    }

    public static boolean equalsFail(String value) {
        return "FAIL".equalsIgnoreCase(value);
    }

    public static boolean isEmpty(CharSequence str) {
        if (str != null && str.length() != 0) {
            for (int var2 = 0; var2 < str.length(); ++var2) {
                char var3 = str.charAt(var2);
                if (var3 != '\n' && var3 != '\f' && var3 != '\r' && var3 != '\t' && var3 != ' ') {
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }

    public static boolean isNotEmpty(CharSequence str) {
        return !isEmpty(str);
    }

    public static boolean isNullOrEmpty(String str) {
        if (str != null && str.length() != 0) {
            if ("null".equalsIgnoreCase(str)) {
                return true;
            } else {
                for (int var2 = 0; var2 < str.length(); ++var2) {
                    char var3 = str.charAt(var2);
                    if (var3 != '\n' && var3 != '\f' && var3 != '\r' && var3 != '\t' && var3 != ' ') {
                        return false;
                    }
                }
                return true;
            }
        } else {
            return true;
        }
    }

    public static boolean isNotNullOrEmpty(String str) {
        return !isNullOrEmpty(str);
    }

    public static String sqlStringFilter(String value) {
        if (value != null && value.length() != 0) {
            value = trim(value);
            String reg = "(?:')|(?:--)|(/\\*(?:.|[\\n\\r])*?\\*/)|(\\b(select|update|and|or|delete|insert|trancate|char|into|substr|ascii|declare|exec|count|master|into|drop|execute)\\b)";
            Pattern sqlPattern = Pattern.compile(reg, 2);
            if (sqlPattern.matcher(value).find()) {
                value = "";
            }

            return value;
        } else {
            return value;
        }
    }

    public static boolean isTrueString(Object value) {
        if (value == null) {
            return false;
        } else if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (value instanceof Integer) {
            return (Integer) value == 1;
        } else if (value instanceof Long) {
            return (Long) value == 1L;
        } else if (value instanceof Short) {
            return (Short) value == 1;
        } else {
            return isTrueString(value.toString());
        }
    }

    public static String getFirstChar(String str) {
        return !isNullOrEmpty(str) && str.length() > 1 ? str.substring(0, 1) : str;
    }

    public static boolean isTrueString(String str) {
        return isNotNullOrEmpty(str) && (str.equals("1") || str.equalsIgnoreCase("true") || str.equalsIgnoreCase("真") || str.equalsIgnoreCase("是") || str.equalsIgnoreCase("对") || str.equalsIgnoreCase("开") || str.equalsIgnoreCase("on") || str.equalsIgnoreCase("open"));
    }

    public static boolean isEmpty(Object[] obj) {
        return null == obj || 0 == obj.length;
    }


    public static boolean isEmpty(List<?> obj) {
        return null == obj || obj.isEmpty();
    }

    public static boolean isEmpty(Map<?, ?> obj) {
        return null == obj || obj.isEmpty();
    }

    public static String getExt(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public static String get32UUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String get36UUID() {
        return UUID.randomUUID().toString();
    }

    public static boolean isNumeric(String str) {
        return isNullOrEmpty(str) ? false : str.matches("\\d*");
    }

    public static int getByteSize(String content) {
        int size = 0;
        if (null != content) {
            try {
                size = content.getBytes("utf-8").length;
            } catch (UnsupportedEncodingException var3) {
                LOG.error(var3.getMessage(), var3);
            }
        }

        return size;
    }

    public static List<String> getInParam(String param) {
        boolean flag = param.contains(",");
        List<String> list = new ArrayList();
        if (flag) {
            list = Arrays.asList(param.split(","));
        } else {
            ((List) list).add(param);
        }

        return (List) list;
    }

    public static boolean hasText(String str) {
        if (!hasLength(str)) {
            return false;
        } else {
            int strLen = str.length();

            for (int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(str.charAt(i))) {
                    return true;
                }
            }

            return false;
        }
    }

    public static boolean hasLength(String str) {
        return str != null && str.length() > 0;
    }

    public static boolean startsWithIgnoreCase(String str, String prefix) {
        if (str != null && prefix != null) {
            if (str.startsWith(prefix)) {
                return true;
            } else if (str.length() < prefix.length()) {
                return false;
            } else {
                String lcStr = str.substring(0, prefix.length()).toLowerCase();
                String lcPrefix = prefix.toLowerCase();
                return lcStr.equals(lcPrefix);
            }
        } else {
            return false;
        }
    }

    public static String clean(String in) {
        String out = in;
        if (in != null) {
            out = in.trim();
            if (out.equals("")) {
                out = null;
            }
        }

        return out;
    }

    public static String padLeft(String oriStr, int len, char alexin) {
        String str = "";
        int strlen = oriStr.length();
        if (strlen < len) {
            for (int i = 0; i < len - strlen; ++i) {
                str = str + alexin;
            }
        }

        str = str + oriStr;
        return str;
    }

    public static String padRight(String oriStr, int len, char alexin) {
        String str = "";
        int strlen = oriStr.length();
        if (strlen < len) {
            for (int i = 0; i < len - strlen; ++i) {
                str = str + alexin;
            }
        }

        str = oriStr + str;
        return str;
    }

    public static boolean contains(final CharSequence seq, final int searchChar) {
        if (isEmpty(seq)) {
            return false;
        } else {
            return CharSequenceUtils.indexOf(seq, searchChar, 0) >= 0;
        }
    }

    public static boolean contains(final CharSequence seq, final CharSequence searchSeq) {
        if (seq != null && searchSeq != null) {
            return CharSequenceUtils.indexOf(seq, searchSeq, 0) >= 0;
        } else {
            return false;
        }
    }

    public static boolean containsIgnoreCase(final CharSequence str, final CharSequence searchStr) {
        if (str != null && searchStr != null) {
            int len = searchStr.length();
            int max = str.length() - len;

            for (int i = 0; i <= max; ++i) {
                if (CharSequenceUtils.regionMatches(str, true, i, searchStr, 0, len)) {
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    public static boolean containsWhitespace(final CharSequence seq) {
        if (isEmpty(seq)) {
            return false;
        } else {
            int strLen = seq.length();

            for (int i = 0; i < strLen; ++i) {
                if (Character.isWhitespace(seq.charAt(i))) {
                    return true;
                }
            }

            return false;
        }
    }

    public static int indexOfAny(final CharSequence cs, final char... searchChars) {
        if (!isEmpty(cs) && searchChars != null && searchChars.length != 0) {
            int csLen = cs.length();
            int csLast = csLen - 1;
            int searchLen = searchChars.length;
            int searchLast = searchLen - 1;

            for (int i = 0; i < csLen; ++i) {
                char ch = cs.charAt(i);

                for (int j = 0; j < searchLen; ++j) {
                    if (searchChars[j] == ch) {
                        if (i >= csLast || j >= searchLast || !Character.isHighSurrogate(ch)) {
                            return i;
                        }

                        if (searchChars[j + 1] == cs.charAt(i + 1)) {
                            return i;
                        }
                    }
                }
            }

            return -1;
        } else {
            return -1;
        }
    }

    public static int indexOfAny(final CharSequence cs, final String searchChars) {
        return !isEmpty(cs) && !isEmpty((CharSequence) searchChars) ? indexOfAny(cs, searchChars.toCharArray()) : -1;
    }

    public static boolean containsAny(final CharSequence cs, final char... searchChars) {
        if (!isEmpty(cs) && searchChars != null && searchChars.length != 0) {
            int csLength = cs.length();
            int searchLength = searchChars.length;
            int csLast = csLength - 1;
            int searchLast = searchLength - 1;

            for (int i = 0; i < csLength; ++i) {
                char ch = cs.charAt(i);

                for (int j = 0; j < searchLength; ++j) {
                    if (searchChars[j] == ch) {
                        if (!Character.isHighSurrogate(ch)) {
                            return true;
                        }

                        if (j == searchLast) {
                            return true;
                        }

                        if (i < csLast && searchChars[j + 1] == cs.charAt(i + 1)) {
                            return true;
                        }
                    }
                }
            }

            return false;
        } else {
            return false;
        }
    }

    public static boolean containsAny(final CharSequence cs, final CharSequence searchChars) {
        return searchChars == null ? false : containsAny(cs, CharSequenceUtils.toCharArray(searchChars));
    }

    public static List<String> parseStringToStringList(String source, String token) {
        if (!isNullOrEmpty(source) && !isNullOrEmpty(token)) {
            List<String> result = new ArrayList();
            String[] units = source.split(token);
            String[] arr$ = units;
            int len$ = units.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                String unit = arr$[i$];
                result.add(unit);
            }

            return result;
        } else {
            return null;
        }
    }



    public static String joinDefault(String... args) {
        return join("$", "$", args);
    }

    public static String join(String joinChar, String nullStr, String... args) {
        if (args != null && args.length != 0) {
            if (args.length == 1) {
                return args[0] == null ? "" : args[0].trim();
            } else {
                if (joinChar == null) {
                    joinChar = "_";
                }

                StringBuilder builder = new StringBuilder();
                builder.append(args[0] == null ? "" : args[0].trim());

                for (int i = 1; i < args.length; ++i) {
                    String item = args[i];
                    if (item != null && !isNullOrEmpty(item)) {
                        builder.append(joinChar).append(item.trim());
                    } else if (nullStr != null) {
                        builder.append(joinChar).append(nullStr);
                    }
                }

                return builder.toString();
            }
        } else {
            return "";
        }
    }

    public static boolean checkSQLInject(String str) {
        String[] inj_stra = new String[]{"script", "mid", "master", "truncate", "insert", "select", "delete", "update", "declare", "iframe", "'", "onreadystatechange", "alert", "atestu", "xss", ";", "'", "\"", "<", ">", "(", ")", "\\", "svg", "confirm", "prompt", "onload", "onmouseover", "onfocus", "onerror"};
        str = str.toLowerCase();

        for (int i = 0; i < inj_stra.length; ++i) {
            if (str.contains(inj_stra[i])) {
                LoggerFactory.getLogger(StringUtils.class).info("传入str=" + str + ",包含特殊字符：" + inj_stra[i]);
                return true;
            }
        }

        return false;
    }



    public static String autoGenericCode(String code) {
        return String.format("%0" + code.length() + "d", Integer.parseInt(code) + 1);
    }



    public static String autoFillCodeByStr(String code, int length) {
        if (isNullOrEmpty(code)) {
            return "";
        } else {
            int zeroNum = length - code.length();
            StringBuffer sb = new StringBuffer();

            for (int i = 0; i < zeroNum; ++i) {
                sb.append(0);
            }

            return sb.append(code).toString();
        }
    }

    public static String substrFixStr(String code, int length, boolean pre) {
        if (isNullOrEmpty(code)) {
            return "";
        } else {
            if (code.length() < length) {
                code = autoFillCodeByStr(code, length);
            }

            return pre ? code.substring(0, length) : code.substring(code.length() - length, code.length());
        }
    }

    public static void main(String[] args) {
        String avg = substrFixStr("E2", 9, false);
        System.out.println("格式化字符串：" + avg);
    }

    static class CharSequenceUtils {
        public CharSequenceUtils() {
        }

        public static CharSequence subSequence(final CharSequence cs, final int start) {
            return cs == null ? null : cs.subSequence(start, cs.length());
        }

        static int indexOf(final CharSequence cs, final int searchChar, int start) {
            if (cs instanceof String) {
                return ((String) cs).indexOf(searchChar, start);
            } else {
                int sz = cs.length();
                if (start < 0) {
                    start = 0;
                }

                for (int i = start; i < sz; ++i) {
                    if (cs.charAt(i) == searchChar) {
                        return i;
                    }
                }

                return -1;
            }
        }

        static int indexOf(final CharSequence cs, final CharSequence searchChar, final int start) {
            return cs.toString().indexOf(searchChar.toString(), start);
        }

        static int lastIndexOf(final CharSequence cs, final int searchChar, int start) {
            if (cs instanceof String) {
                return ((String) cs).lastIndexOf(searchChar, start);
            } else {
                int sz = cs.length();
                if (start < 0) {
                    return -1;
                } else {
                    if (start >= sz) {
                        start = sz - 1;
                    }

                    for (int i = start; i >= 0; --i) {
                        if (cs.charAt(i) == searchChar) {
                            return i;
                        }
                    }

                    return -1;
                }
            }
        }

        static int lastIndexOf(final CharSequence cs, final CharSequence searchChar, final int start) {
            return cs.toString().lastIndexOf(searchChar.toString(), start);
        }

        static char[] toCharArray(final CharSequence cs) {
            if (cs instanceof String) {
                return ((String) cs).toCharArray();
            } else {
                int sz = cs.length();
                char[] array = new char[cs.length()];

                for (int i = 0; i < sz; ++i) {
                    array[i] = cs.charAt(i);
                }

                return array;
            }
        }

        static boolean regionMatches(final CharSequence cs, final boolean ignoreCase, final int thisStart, final CharSequence substring, final int start, final int length) {
            if (cs instanceof String && substring instanceof String) {
                return ((String) cs).regionMatches(ignoreCase, thisStart, (String) substring, start, length);
            } else {
                int index1 = thisStart;
                int index2 = start;
                int var8 = length;

                while (var8-- > 0) {
                    char c1 = cs.charAt(index1++);
                    char c2 = substring.charAt(index2++);
                    if (c1 != c2) {
                        if (!ignoreCase) {
                            return false;
                        }

                        if (Character.toUpperCase(c1) != Character.toUpperCase(c2) && Character.toLowerCase(c1) != Character.toLowerCase(c2)) {
                            return false;
                        }
                    }
                }

                return true;
            }
        }
    }


    /**
     * Byte数组为空判断
     * @param bytes
     * @return boolean
     */
    public static boolean isNull(byte[] bytes) {
        // 根据byte数组长度为0判断
        return bytes == null || bytes.length == 0;
    }

    /**
     * Byte数组不为空判断
     * @param bytes
     * @return boolean
     */
    public static boolean isNotNull(byte[] bytes) {
        return !isNull(bytes);
    }


    /**
     * String为空判断(不允许空格)
     * @param str
     * @return boolean
     */
    public static boolean isBlank(String str) {
        return str == null || "".equals(str.trim());
    }

    /**
     * String不为空判断(不允许空格)
     * @param str
     * @return boolean
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }


    public static String parseString(Object obj)
    {
        if(obj == null)
        {
            return "";
        }
        return obj.toString();
    }

}
