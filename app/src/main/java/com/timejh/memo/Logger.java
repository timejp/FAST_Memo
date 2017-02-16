package com.timejh.memo;

import android.util.Log;

/**
 * Created by tokijh on 2017. 2. 14..
 */

public class Logger {
    private final static boolean DEBUG = true;

    public static void d(String tag, String content) {
        if (DEBUG)
            Log.d(tag, content);
    }

    public static void d(String tag, int content) {
        if (DEBUG)
            Log.d(tag, content + "");
    }
}
