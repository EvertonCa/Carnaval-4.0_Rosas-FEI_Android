package com.wikitude.samples;

import com.wikitude.sdksamples.R;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

/**
 * The MainActivity is used to display the list of samples and handles the runtime
 * permissions for the sample activities.
 */
public class MainActivity extends AppCompatActivity {

    private ImageView splashImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        splashImageView = findViewById(R.id.splashImageView);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        Handler handle = new Handler();
        handle.postDelayed(new Runnable() {
            @Override public void run() {
                animateImage();
            }
        }, 2000);
    }

    public void animateImage()
    {
        // first 0f, 1f mean scaling from X-axis to X-axis, meaning scaling from 0-100%
        // first 0f, 1f mean scaling from Y-axis to Y-axis, meaning scaling from 0-100%
        // The two 0.5f mean animation will start from 50% of X-axis & 50% of Y-axis, i.e. from center

        final ScaleAnimation scale_up =  new ScaleAnimation(0.5f, 3.5f, 0.5f, 3.5f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale_up.setDuration(700);     // animation duration in milliseconds
        scale_up.setFillAfter(true);    // If fillAfter is true, the transformation that this animation performed will persist when it is finished.

        final ScaleAnimation scale_down =  new ScaleAnimation(1f, 0.5f, 1f, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale_down.setDuration(400);     // animation duration in milliseconds
        scale_down.setFillAfter(true);    // If fillAfter is true, the transformation that this animation performed will persist when it is finished.

        final ScaleAnimation scale_down_again =  new ScaleAnimation(3.5f, 0.0f, 3.5f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale_down_again.setDuration(400);     // animation duration in milliseconds
        scale_down_again.setFillAfter(true);    // If fillAfter is true, the transformation that this animation performed will persist when it is finished.

        splashImageView.startAnimation(scale_down);

        scale_down.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                ;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                splashImageView.startAnimation(scale_up);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                ;
            }
        });

        scale_up.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                ;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                splashImageView.startAnimation(scale_down_again);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                ;
            }
        });

        scale_down_again.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                ;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                callMainActivity();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                ;
            }
        });
    }

    public void callMainActivity()
    {
        Intent i = new Intent(MainActivity.this, StartActivity.class);
        MainActivity.this.startActivity(i);
    }


}
