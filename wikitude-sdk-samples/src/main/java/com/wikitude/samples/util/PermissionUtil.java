package com.wikitude.samples.util;

import com.wikitude.common.devicesupport.Feature;
import java.util.EnumSet;

import android.Manifest;

public class PermissionUtil {

    private PermissionUtil(){}

    public static String[] getPermissionsForArFeatures(EnumSet<Feature> features) {
        return (features.contains(Feature.GEO)) ?
                new String[]{Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION} :
                new String[]{Manifest.permission.CAMERA};
    }
}
