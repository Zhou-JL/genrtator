package com.zhoujl.demo.commom.mapper;

import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 */
public class SQLHelper {
    private static Pattern pattern = Pattern.compile("\\s+FROM\\s+", 2);
    private static Pattern distinct_pattern = Pattern.compile("\\s+DISTINCT\\s+", 2);
    private static Pattern GROUPBY_pattern = Pattern.compile("\\s+GROUP\\s+", 2);
    private static final char UNDERLINE = '_';
    private static Pattern p = Pattern.compile("order\\s*by[\\w|\\W|\\s|\\S]*", 2);

    public SQLHelper() {
    }

    public static String generatorCountSql(String sql) {
        if (!distinct_pattern.matcher(sql).find() && !GROUPBY_pattern.matcher(sql).find()) {
            Matcher matcher = pattern.matcher(sql);
            StringBuffer prefix = new StringBuffer();

            while(matcher.find()) {
                boolean flag = hasEqualBrackets(sql.substring(0, matcher.start()));
                matcher.appendReplacement(prefix, "");
                if (flag) {
                    break;
                }
            }

            StringBuffer result = new StringBuffer();
            result.append("select count(*) from ");
            matcher.appendTail(result);
            String clause = result.toString();
            int lastIndex = clause.toLowerCase().lastIndexOf("order by");
            if (lastIndex > 0) {
                String endClause = clause.substring(lastIndex);
                return endClause.contains("(") ? clause : clause.substring(0, lastIndex);
            } else {
                return clause;
            }
        } else {
            return String.format("select count(*) from (%s) as tmp_count", sql);
        }
    }

    private static Boolean hasEqualBrackets(String index) {
        if (index != null && index.length() != 0) {
            Queue<Object> queue = new LinkedList();
            char[] ch = index.toCharArray();
            char[] var3 = ch;
            int var4 = ch.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                char a = var3[var5];
                if ('(' == a) {
                    queue.add(a);
                } else if (')' == a) {
                    queue.poll();
                }
            }

            return queue.size() == 0;
        } else {
            return true;
        }
    }



}
