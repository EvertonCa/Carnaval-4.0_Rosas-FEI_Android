package com.wikitude.samples;

import com.wikitude.architect.ArchitectView;
import com.wikitude.common.CallStatus;
import com.wikitude.common.permission.PermissionManager;
import com.wikitude.samples.util.PermissionUtil;
import com.wikitude.samples.util.SampleCategory;
import com.wikitude.samples.util.SampleData;
import com.wikitude.samples.util.SampleJsonParser;
import com.wikitude.sdksamples.R;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

/**
 * The MainActivity is used to display the list of samples and handles the runtime
 * permissions for the sample activities.
 */
public class MainActivity extends AppCompatActivity {

    private static final String sampleDefinitionsPath = "samples/samples.json";

    private final PermissionManager permissionManager = ArchitectView.getPermissionManager();
    private List<SampleCategory> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        final String json = SampleJsonParser.loadStringFromAssets(this, sampleDefinitionsPath);
        categories = SampleJsonParser.getCategoriesFromJsonString(json);

        for (final SampleCategory category : categories) {
            for (final SampleData data : category.getSamples()) {
                CallStatus status = ArchitectView.isDeviceSupporting(this, data.getArFeatures());
                if (status.isSuccess()) {
                    data.isDeviceSupporting(true, "");
                } else {
                    data.isDeviceSupporting(false, status.getError().getMessage());
                }
            }
        }

        Handler handle = new Handler();
        handle.postDelayed(new Runnable() {
            @Override public void run() {
                startAR();
            }
        }, 1000);
    }

    public boolean startAR()
    {
        final SampleData sampleData = categories.get(0).getSamples().get(0);
        final String[] permissions = PermissionUtil.getPermissionsForArFeatures(sampleData.getArFeatures());

        if(!sampleData.getIsDeviceSupporting()) {
            showDeviceMissingFeatures(sampleData.getIsDeviceSupportingError());
        } else {
            permissionManager.checkPermissions(MainActivity.this, permissions, PermissionManager.WIKITUDE_PERMISSION_REQUEST, new PermissionManager.PermissionManagerCallback() {
                @Override
                public void permissionsGranted(int requestCode) {
                    final Intent intent = new Intent(MainActivity.this, sampleData.getActivityClass());
                    intent.putExtra(SimpleArActivity.INTENT_EXTRAS_KEY_SAMPLE, sampleData);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }

                @Override
                public void permissionsDenied(@NonNull String[] deniedPermissions) {
                    Toast.makeText(MainActivity.this, getString(R.string.permissions_denied) + Arrays.toString(deniedPermissions), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void showPermissionRationale(final int requestCode, @NonNull String[] strings) {
                    final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle(R.string.permission_rationale_title);
                    alertBuilder.setMessage(getString(R.string.permission_rationale_text) + Arrays.toString(permissions));
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            permissionManager.positiveRationaleResult(requestCode, permissions);
                        }
                    });

                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                }
            });
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        ArchitectView.getPermissionManager().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void showDeviceMissingFeatures(String errorMessage) {
        new AlertDialog.Builder(MainActivity.this)
            .setTitle(R.string.device_missing_features)
            .setMessage(errorMessage)
            .show();
    }
}
