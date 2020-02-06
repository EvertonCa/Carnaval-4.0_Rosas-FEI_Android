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

        final ScaleAnimation anim1 =  new ScaleAnimation(1.0f, 1.2f, 1.0f, 1.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim1.setDuration(200);     // animation duration in milliseconds
        anim1.setFillAfter(true);    // If fillAfter is true, the transformation that this animation performed will persist when it is finished.

        final ScaleAnimation anim2 =  new ScaleAnimation(1.2f, 1.1f, 1.2f, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim2.setDuration(300);     // animation duration in milliseconds
        anim2.setFillAfter(true);    // If fillAfter is true, the transformation that this animation performed will persist when it is finished.

        final ScaleAnimation anim3 =  new ScaleAnimation(1.1f, 1.2f, 1.1f, 1.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim3.setDuration(100);     // animation duration in milliseconds
        anim3.setFillAfter(true);    // If fillAfter is true, the transformation that this animation performed will persist when it is finished.

        final ScaleAnimation anim4 =  new ScaleAnimation(1.2f, 1.0f, 1.2f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim4.setDuration(300);     // animation duration in milliseconds
        anim4.setFillAfter(true);    // If fillAfter is true, the transformation that this animation performed will persist when it is finished.

        final ScaleAnimation animPause =  new ScaleAnimation(1.0f, 1.0f, 1.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animPause.setDuration(600);     // animation duration in milliseconds
        animPause.setFillAfter(true);    // If fillAfter is true, the transformation that this animation performed will persist when it is finished.

        final ScaleAnimation anim5 =  new ScaleAnimation(1.0f, 1.2f, 1.0f, 1.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim5.setDuration(200);     // animation duration in milliseconds
        anim5.setFillAfter(true);    // If fillAfter is true, the transformation that this animation performed will persist when it is finished.

        final ScaleAnimation anim6 =  new ScaleAnimation(1.2f, 1.1f, 1.2f, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim6.setDuration(300);     // animation duration in milliseconds
        anim6.setFillAfter(true);    // If fillAfter is true, the transformation that this animation performed will persist when it is finished.

        final ScaleAnimation anim7 =  new ScaleAnimation(1.1f, 1.2f, 1.1f, 1.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim7.setDuration(100);     // animation duration in milliseconds
        anim7.setFillAfter(true);    // If fillAfter is true, the transformation that this animation performed will persist when it is finished.

        final ScaleAnimation anim8 =  new ScaleAnimation(1.2f, 1.0f, 1.2f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim8.setDuration(300);     // animation duration in milliseconds
        anim8.setFillAfter(true);    // If fillAfter is true, the transformation that this animation performed will persist when it is finished.

        splashImageView.startAnimation(anim1);

        anim1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                ;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                splashImageView.startAnimation(anim2);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                ;
            }
        });

        anim2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                ;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                splashImageView.startAnimation(anim3);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                ;
            }
        });

        anim3.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                ;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                splashImageView.startAnimation(anim4);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                ;
            }
        });

        anim4.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                ;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                splashImageView.startAnimation(animPause);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                ;
            }
        });

        animPause.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                ;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                splashImageView.startAnimation(anim5);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                ;
            }
        });

        anim5.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                ;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                splashImageView.startAnimation(anim6);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                ;
            }
        });

        anim6.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                ;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                splashImageView.startAnimation(anim7);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                ;
            }
        });

        anim7.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                ;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                splashImageView.startAnimation(anim8);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                ;
            }
        });

        anim8.setAnimationListener(new Animation.AnimationListener() {
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
