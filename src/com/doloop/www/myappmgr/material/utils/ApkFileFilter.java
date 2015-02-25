package com.doloop.www.myappmgr.material.utils;

import java.io.File;
import java.io.FilenameFilter;

public class ApkFileFilter implements FilenameFilter {

    @Override
    public boolean accept(File dir, String filename) {
        // TODO Auto-generated method stub
        if (filename.endsWith(".apk")) {
            return true;
        }
        return false;
    }

}
