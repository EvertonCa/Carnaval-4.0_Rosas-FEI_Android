package com.wikitude.samples;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.wikitude.architect.ArchitectView;
import com.wikitude.common.CallStatus;
import com.wikitude.common.permission.PermissionManager;
import com.wikitude.samples.util.PermissionUtil;
import com.wikitude.samples.util.SampleCategory;
import com.wikitude.samples.util.SampleData;
import com.wikitude.samples.util.SampleJsonParser;
import com.wikitude.sdksamples.R;

import java.util.Arrays;
import java.util.List;

public class StartActivity extends AppCompatActivity {

    private static final String sampleDefinitionsPath = "samples/samples.json";

    private final PermissionManager permissionManager = ArchitectView.getPermissionManager();
    private List<SampleCategory> categories;

    private ImageView buttonStart;
    private ImageView rosasLogoView;
    private ImageView feiLogoView;
    private VideoView backgroundVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        setContentView(R.layout.activity_main);
        buttonStart = findViewById(R.id.buttonStart);
        rosasLogoView = findViewById(R.id.rosasLogoView);
        feiLogoView = findViewById(R.id.feiLogoView);

        backgroundVideoView = findViewById(R.id.videoView);
        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.rosas_background);
        backgroundVideoView.setVideoURI(uri);

        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new AccelerateInterpolator());
        fadeIn.setStartOffset(3000);
        fadeIn.setDuration(3000);

        buttonStart.startAnimation(fadeIn);
        rosasLogoView.startAnimation(fadeIn);
        feiLogoView.startAnimation(fadeIn);

        backgroundVideoView.start();

        backgroundVideoView.setOnPreparedListener (new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });

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
    }

    public boolean startAR(View v)
    {
        final SampleData sampleData = categories.get(0).getSamples().get(0);
        final String[] permissions = PermissionUtil.getPermissionsForArFeatures(sampleData.getArFeatures());

        if(!sampleData.getIsDeviceSupporting()) {
            showDeviceMissingFeatures(sampleData.getIsDeviceSupportingError());
        } else {
            permissionManager.checkPermissions(StartActivity.this, permissions, PermissionManager.WIKITUDE_PERMISSION_REQUEST, new PermissionManager.PermissionManagerCallback() {
                @Override
                public void permissionsGranted(int requestCode) {
                    final Intent intent = new Intent(StartActivity.this, sampleData.getActivityClass());
                    intent.putExtra(SimpleArActivity.INTENT_EXTRAS_KEY_SAMPLE, sampleData);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }

                @Override
                public void permissionsDenied(@NonNull String[] deniedPermissions) {
                    Toast.makeText(StartActivity.this, getString(R.string.permissions_denied) + Arrays.toString(deniedPermissions), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void showPermissionRationale(final int requestCode, @NonNull String[] strings) {
                    final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(StartActivity.this);
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
        new AlertDialog.Builder(StartActivity.this)
                .setTitle(R.string.device_missing_features)
                .setMessage(errorMessage)
                .show();
    }
}
