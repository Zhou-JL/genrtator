package com.zhoujl.demo.utils;

import org.springframework.util.StringUtils;


public class HtUploadUtil {

    public static StringBuilder checkPath(String path) {
        return checkPath(path, (String)null);
    }

    public static StringBuilder checkPath(String path, String appPath) {
        if (StringUtils.isEmpty(path)) {
            return null;
        } else if (path.indexOf("..") > 0) {
            return null;
        } else {
            String[] pathSplit;
            if (path.indexOf("\\") >= 0) {
                pathSplit = path.split("\\\\");
            } else {
                pathSplit = path.split("/");
            }

            boolean first = true;
            StringBuilder sb = new StringBuilder();

            for(int i = 0; i < pathSplit.length; ++i) {
                String foldName = pathSplit[i];
                if (!StringUtils.isEmpty(foldName)) {
                    sb.append("/");
                    sb.append(foldName);
                    if (first && appPath != null) {
                        sb.append("/").append(appPath);
                    }

                    first = false;
                }
            }

            return sb;
        }
    }


}
