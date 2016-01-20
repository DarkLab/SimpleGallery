package com.darklab.simplegallery;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import java.net.URI;

public class GalleryActivity extends AppCompatActivity {
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    private ViewFlipper flipper;

    private int[] picturesId = new int[]{
            R.drawable.elephant,
            R.drawable.home,
            R.drawable.ice_cream,
            R.drawable.lion,
            R.drawable.monkey
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gallery);

        mVisible = true;
        mContentView = findViewById(R.id.fullscreen_content);

        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        Animation fade_in = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation fade_out = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        Animation slide_in = AnimationUtils.loadAnimation(this, R.anim.slide_in);
        Animation slide_out = AnimationUtils.loadAnimation(this, R.anim.slide_out);

        flipper = (ViewFlipper) findViewById(R.id.flipper);

        flipper.setInAnimation(slide_in);
        flipper.setOutAnimation(slide_out);

        loadPictures();

        setPeriodForFlipper();
        flipper.setAutoStart(true);
        flipper.startFlipping();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        delayedHide(100);
    }

    private void loadPictures() {
        for (int resId : picturesId) {
            addPictureByResId(resId);
        }
    }

    private void addPictureByResId(int resId) {
        ImageView picture = new ImageView(this);
        ViewGroup.LayoutParams params =
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
        picture.setLayoutParams(params);
        picture.setScaleType(ImageView.ScaleType.FIT_CENTER);
        picture.setImageResource(resId);
        flipper.addView(picture);
    }

    private void addPictureByUri(Uri uri) {
        ImageView picture = new ImageView(this);
        ViewGroup.LayoutParams params =
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
        picture.setLayoutParams(params);
        picture.setScaleType(ImageView.ScaleType.FIT_CENTER);
        picture.setImageURI(uri);
        flipper.addView(picture);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mVisible = false;

        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        setPeriodForFlipper();
    }

    private void setPeriodForFlipper() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int period = Integer.parseInt(prefs.getString(getResources().getString(R.string.key_pref_period), "5"));
        if (period < 1) {
            period = 1;
        } else if (period > 60) {
            period = 60;
        }
        period *= 1000;

        flipper.setFlipInterval(period);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivityForResult(intent, 0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
