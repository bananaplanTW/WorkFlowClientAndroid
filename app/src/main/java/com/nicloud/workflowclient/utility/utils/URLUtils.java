package com.nicloud.workflowclient.utility.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by daz on 10/9/15.
 */
public class URLUtils {
    public static String buildURLString(String baseURL, String endPoint, HashMap<String, String> queries) {
        String queryString = URLUtils.buildQueryString(queries);
        if (queryString.length() > 0) {
            return baseURL + endPoint + "?" + queryString;
        } else {
            return baseURL + endPoint;
        }
    }

    public static String buildQueryString (HashMap<String, String> queries) {
        String queryString = "";
        if (queries != null) {
            Iterator iter = queries.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                queryString += entry.getKey() + "=" + entry.getValue();
                queryString += "&";
            }
            if (queryString.length() > 0) {
                queryString = queryString.substring(0, queryString.length() - 1);
            }
        }
        return queryString;
    }
}
