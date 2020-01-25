package com.wikitude.samples.advanced;

import com.wikitude.architect.ArchitectJavaScriptInterfaceListener;
import com.wikitude.architect.ArchitectView;
import com.wikitude.common.permission.PermissionManager;
import com.wikitude.sdksamples.R;

import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.widget.Toast;

import java.io.OutputStream;
import java.util.Arrays;

import static com.wikitude.architect.ArchitectView.CaptureScreenCallback.CAPTURE_MODE_CAM_AND_WEBVIEW;

/**
 * This Extension is used for the Gestures and Bonus-CaptureScreen AR-Experience.
 * It is used to store and share a screenshot of the AR-Experienced.
 *
 * For this functionality it implements an ArchitectJavaScriptInterfaceListener.
 */
public class ScreenshotSaverExtension extends ArchitectViewExtension implements ArchitectJavaScriptInterfaceListener {

    /** The PermissionManager can be used to easily check for required permissions. */
    private final PermissionManager permissionManager = ArchitectView.getPermissionManager();

    private final PermissionManager.PermissionManagerCallback permissionManagerCallback = new PermissionManager.PermissionManagerCallback() {
        @Override
        public void permissionsGranted(int requestCode) {
            saveScreenCaptureToExternalStorage(screenCapture);
        }

        @Override
        public void permissionsDenied(@NonNull final String[] deniedPermissions) {
            Toast.makeText(activity, activity.getString(R.string.permissions_denied) + Arrays.toString(deniedPermissions), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void showPermissionRationale(final int requestCode, @NonNull String[] strings) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
            alertBuilder.setCancelable(true);
            alertBuilder.setTitle(R.string.permission_rationale_title);
            alertBuilder.setMessage(activity.getString(R.string.permission_rationale_text) + Manifest.permission.WRITE_EXTERNAL_STORAGE);
            alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    permissionManager.positiveRationaleResult(requestCode, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
                }
            });

            AlertDialog alert = alertBuilder.create();
            alert.show();
        }
    };

    private Bitmap screenCapture;

    public ScreenshotSaverExtension(Activity activity, ArchitectView architectView) {
        super(activity, architectView);
    }


    @Override
    public void onCreate() {
        /*
         * The ArchitectJavaScriptInterfaceListener has to be added to the Architect view after ArchitectView.onCreate.
         * There may be more than one ArchitectJavaScriptInterfaceListener.
         */
        architectView.addArchitectJavaScriptInterfaceListener(this);
    }

    @Override
    public void onDestroy() {
        // The ArchitectJavaScriptInterfaceListener has to be removed from the Architect view before ArchitectView.onDestroy.
        architectView.removeArchitectJavaScriptInterfaceListener(this);
    }

    /**
     * ArchitectJavaScriptInterfaceListener.onJSONObjectReceived is called whenever
     * AR.platform.sendJSONObject is called in the JavaScript code.
     *
     * @param jsonObject jsonObject passed in AR.platform.sendJSONObject
     */
    @Override
    public void onJSONObjectReceived(JSONObject jsonObject) {
        try {
            switch (jsonObject.getString("action")) {
                case "capture_screen":
                    /*
                     * ArchitectView.captureScreen has two different modes:
                     *  - CAPTURE_MODE_CAM_AND_WEBVIEW which will capture the camera and web-view on top of it.
                     *  - CAPTURE_MODE_CAM which will capture ONLY the camera and its content (AR.Drawables).
                     *
                     * onScreenCaptured will be called once the ArchitectView has processed the screen capturing and will
                     * provide a Bitmap containing the screenshot.
                     */
                    architectView.captureScreen(CAPTURE_MODE_CAM_AND_WEBVIEW, new ArchitectView.CaptureScreenCallback() {
                        @Override
                        public void onScreenCaptured(final Bitmap screenCapture) {

                            ScreenshotSaverExtension.this.screenCapture = screenCapture;
                            permissionManager.checkPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123, permissionManagerCallback);
                        }
                    });
                    break;
            }
        } catch (JSONException e) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, R.string.error_parsing_json, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    /**
     * This method will store the screenshot in a file and will create an intent to share it.
     *
     * @param screenCapture Bitmap from onScreenCaptured
     */
    private void saveScreenCaptureToExternalStorage(Bitmap screenCapture) {
        if (screenCapture != null) {
            // 1. Save bitmap to file & compress to jpeg. You may use PNG too
            final ContentResolver resolver = architectView.getContext().getContentResolver();
            try {

                final ContentValues  values = new ContentValues();
                values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
                }

                Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                try (final OutputStream out = resolver.openOutputStream(uri)) {
                    screenCapture.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.flush();
                }

                // 2. create send intent
                final Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/jpg");
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                share.putExtra(Intent.EXTRA_STREAM, uri);


                // 3. launch intent-chooser
                final String chooserTitle = "Share Snaphot";
                activity.startActivity(Intent.createChooser(share, chooserTitle));

            } catch (final Exception e) {
                // should not occur when all permissions are set
                activity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // show toast message in case something went wrong
                        Toast.makeText(activity, activity.getString(R.string.unexpected_error) + e, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }
}
