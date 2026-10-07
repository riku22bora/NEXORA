package com.nexora.launcher;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {

    private TextView clock;
    private TextView date;
    private NexoraAnimatedTextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.BLACK);
        getWindow().setNavigationBarColor(Color.BLACK);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setBackgroundColor(Color.BLACK);
        root.setPadding(24, 40, 24, 24);

        clock = new TextView(this);
        clock.setTextColor(Color.WHITE);
        clock.setTextSize(48);
        clock.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        clock.setGravity(Gravity.CENTER);

        date = new TextView(this);
        date.setTextColor(Color.LTGRAY);
        date.setTextSize(16);
        date.setGravity(Gravity.CENTER);
        date.setPadding(0, 0, 0, 30);

        name = new NexoraAnimatedTextView(this);
        name.setText("RIKU");
        name.setTextSize(20);
        name.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        name.setGravity(Gravity.CENTER);
        name.setLetterSpacing(0.18f);

        name.setSolidColor(Color.WHITE);
        name.setGlowColor(Color.rgb(0, 255, 255));
        name.setGlowRadius(20f);
        name.setAnimationSpeed(3000L);
        name.setAnimationMode(
                NexoraAnimatedTextView.AnimationMode.LIQUID
        );

        root.addView(name, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                70
        ));

        root.addView(clock, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        root.addView(date, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        ScrollView scrollView = new ScrollView(this);

        GridLayout appGrid = new GridLayout(this);
        appGrid.setColumnCount(4);
        appGrid.setUseDefaultMargins(true);

        loadApps(appGrid);

        scrollView.addView(appGrid);

        root.addView(scrollView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1
        ));

        setContentView(root);

        updateClock();
    }

    private void updateClock() {
        SimpleDateFormat timeFormat =
                new SimpleDateFormat("HH:mm", Locale.getDefault());

        SimpleDateFormat dateFormat =
                new SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault());

        clock.setText(timeFormat.format(new Date()));
        date.setText(dateFormat.format(new Date()));

        clock.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateClock();
            }
        }, 1000);
    }

    private void loadApps(GridLayout appGrid) {
        PackageManager pm = getPackageManager();

        Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
        launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<android.content.pm.ResolveInfo> launchableApps =
                pm.queryIntentActivities(
                        launcherIntent,
                        PackageManager.MATCH_ALL
                );

        for (android.content.pm.ResolveInfo resolveInfo : launchableApps) {
            ApplicationInfo app = resolveInfo.activityInfo.applicationInfo;
            LinearLayout item = new LinearLayout(this);
            item.setOrientation(LinearLayout.VERTICAL);
            item.setGravity(Gravity.CENTER);
            item.setPadding(8, 12, 8, 12);

            ImageView icon = new ImageView(this);
            icon.setImageDrawable(app.loadIcon(pm));

            TextView name = new TextView(this);
            name.setText(app.loadLabel(pm));
            name.setTextColor(Color.WHITE);
            name.setTextSize(12);
            name.setGravity(Gravity.CENTER);
            name.setMaxLines(1);

            item.addView(icon, new LinearLayout.LayoutParams(72, 72));
            item.addView(name, new LinearLayout.LayoutParams(
                    90,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent launchIntent =
                            pm.getLaunchIntentForPackage(app.packageName);

                    if (launchIntent != null) {
                        startActivity(launchIntent);
                    }
                }
            });

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.columnSpec =
                    GridLayout.spec(GridLayout.UNDEFINED, 1f);

            appGrid.addView(item, params);
        }
    }
}
