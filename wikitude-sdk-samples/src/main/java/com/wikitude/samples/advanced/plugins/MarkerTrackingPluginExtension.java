package com.wikitude.samples.advanced.plugins;

import com.wikitude.architect.ArchitectView;
import com.wikitude.common.ErrorCallback;
import com.wikitude.common.WikitudeError;
import com.wikitude.samples.advanced.ArchitectViewExtension;
import com.wikitude.sdksamples.R;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

/**
 * This Extension is the java counterpart of the 13_PluginsAPI_5_MarkerTracking AR-Experience.
 * It registers a native Plugin which uses camera frames provided by the Wikitude SDK for
 * aruco marker detection and will render the augmentations using the Wikitude positionables api.
 */
public class MarkerTrackingPluginExtension extends ArchitectViewExtension {

    private static final String TAG = "MarkerTrackingPlugin";

    public MarkerTrackingPluginExtension(Activity activity, ArchitectView architectView) {
        super(activity, architectView);
    }

    @Override
    public void onPostCreate() {
        /*
         * Registers the plugin with the name "markertracking".
         * The library containing the native plugin is libwikitudePlugins.so.
         */
        architectView.registerNativePlugins("wikitudePlugins", "markertracking", new ErrorCallback() {
            @Override
            public void onError(@NonNull WikitudeError error) {
                Toast.makeText(activity, R.string.error_loading_plugins, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Plugin failed to load. Reason: " + error.getMessage());
            }
        });
    }
}
