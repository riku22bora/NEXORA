package com.nexora.launcher;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {

    private NexoraClockView clock;
    private TextView date;
    private NexoraAnimatedTextView name;

    private LinearLayout homeRoot;
    private LinearLayout drawerRoot;
    private GridLayout appGrid;
    private TextView noResultsText;

    private final List<ApplicationInfo> allApps =
            new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.BLACK);
        getWindow().setNavigationBarColor(Color.BLACK);

        loadInstalledApps();
        showHomeScreen();
    }

    private void showHomeScreen() {
        drawerRoot = null;

        homeRoot = new LinearLayout(this);
        homeRoot.setOrientation(LinearLayout.VERTICAL);
        homeRoot.setGravity(Gravity.CENTER_HORIZONTAL);
        homeRoot.setBackgroundColor(Color.BLACK);
        homeRoot.setPadding(24, 40, 24, 24);

        clock = new NexoraClockView(this);

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

        homeRoot.addView(name, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                70
        ));

        homeRoot.addView(clock, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        homeRoot.addView(date, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        View spacer = new View(this);

        homeRoot.addView(spacer, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1
        ));

        Button appDrawerButton = new Button(this);
        appDrawerButton.setText("ALL APPS");
        appDrawerButton.setTextColor(Color.WHITE);
        appDrawerButton.setTextSize(14);
        appDrawerButton.setAllCaps(false);
        appDrawerButton.setBackgroundColor(
                Color.rgb(25, 25, 25)
        );

        appDrawerButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAppDrawer();
                    }
                }
        );

        homeRoot.addView(appDrawerButton,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        60
                ));

        setContentView(homeRoot);

        updateClock();
    }

    private void showAppDrawer() {
        drawerRoot = new LinearLayout(this);
        drawerRoot.setOrientation(LinearLayout.VERTICAL);
        drawerRoot.setBackgroundColor(Color.BLACK);
        drawerRoot.setPadding(16, 24, 16, 16);

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);

        TextView title = new TextView(this);
        title.setText("ALL APPS");
        title.setTextColor(Color.WHITE);
        title.setTextSize(24);
        title.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        Button backButton = new Button(this);
        backButton.setText("BACK");
        backButton.setTextColor(Color.WHITE);
        backButton.setAllCaps(false);
        backButton.setBackgroundColor(
                Color.rgb(25, 25, 25)
        );

        backButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showHomeScreen();
                    }
                }
        );

        header.addView(title,
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                ));

        header.addView(backButton,
                new LinearLayout.LayoutParams(
                        100,
                        52
                ));

        drawerRoot.addView(header);

        EditText searchBox = new EditText(this);
        searchBox.setHint("Search apps...");
        searchBox.setHintTextColor(
                Color.rgb(150, 150, 150)
        );
        searchBox.setTextColor(Color.WHITE);
        searchBox.setTextSize(16);
        searchBox.setSingleLine(true);
        searchBox.setPadding(20, 0, 20, 0);
        searchBox.setBackgroundColor(
                Color.rgb(25, 25, 25)
        );

        LinearLayout.LayoutParams searchParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        56
                );

        searchParams.setMargins(0, 16, 0, 16);

        drawerRoot.addView(searchBox, searchParams);

        ScrollView scrollView = new ScrollView(this);

        LinearLayout resultsContainer =
                new LinearLayout(this);

        resultsContainer.setOrientation(
                LinearLayout.VERTICAL
        );

        appGrid = new GridLayout(this);
        appGrid.setColumnCount(4);
        appGrid.setUseDefaultMargins(true);

        noResultsText = new TextView(this);
        noResultsText.setText(
                "No apps found"
        );
        noResultsText.setTextColor(
                Color.LTGRAY
        );
        noResultsText.setTextSize(16);
        noResultsText.setGravity(
                Gravity.CENTER
        );
        noResultsText.setVisibility(
                View.GONE
        );
        noResultsText.setPadding(0, 40, 0, 40);

        resultsContainer.addView(
                noResultsText,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        resultsContainer.addView(appGrid);

        scrollView.addView(resultsContainer);

        drawerRoot.addView(scrollView,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0,
                        1
                ));

        setContentView(drawerRoot);

        populateAppGrid(allApps);

        searchBox.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after
                    ) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count
                    ) {
                        filterApps(
                                s.toString()
                        );
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s
                    ) {
                    }
                }
        );
    }

    private void loadInstalledApps() {
        allApps.clear();

        PackageManager pm = getPackageManager();

        Intent launcherIntent =
                new Intent(Intent.ACTION_MAIN);

        launcherIntent.addCategory(
                Intent.CATEGORY_LAUNCHER
        );

        List<android.content.pm.ResolveInfo>
                launchableApps =
                pm.queryIntentActivities(
                        launcherIntent,
                        PackageManager.MATCH_ALL
                );

        for (
                android.content.pm.ResolveInfo resolveInfo
                : launchableApps
        ) {
            ApplicationInfo app =
                    resolveInfo.activityInfo.applicationInfo;

            if (!allApps.contains(app)) {
                allApps.add(app);
            }
        }
    }

    private void filterApps(String query) {
        String searchQuery =
                query.trim().toLowerCase(
                        Locale.getDefault()
                );

        List<ApplicationInfo> filteredApps =
                new ArrayList<>();

        PackageManager pm = getPackageManager();

        for (ApplicationInfo app : allApps) {
            String appName =
                    app.loadLabel(pm)
                            .toString()
                            .toLowerCase(
                                    Locale.getDefault()
                            );

            if (appName.contains(searchQuery)) {
                filteredApps.add(app);
            }
        }

        populateAppGrid(filteredApps);
    }

    private void populateAppGrid(
            List<ApplicationInfo> apps
    ) {
        if (appGrid == null) {
            return;
        }

        appGrid.removeAllViews();

        PackageManager pm = getPackageManager();

        for (ApplicationInfo app : apps) {
            LinearLayout item =
                    new LinearLayout(this);

            item.setOrientation(
                    LinearLayout.VERTICAL
            );

            item.setGravity(Gravity.CENTER);
            item.setPadding(8, 12, 8, 12);

            NexoraIconView icon =
                    new NexoraIconView(this);

            icon.setIcon(
                    app.loadIcon(pm)
            );

            icon.setIconSize(68);
            icon.setCornerRadius(22);
            icon.setBackgroundColorValue(
                    Color.argb(42, 255, 255, 255)
            );
            icon.setGlowEnabled(true);

            TextView appName =
                    new TextView(this);

            appName.setText(
                    app.loadLabel(pm)
            );

            appName.setTextColor(
                    Color.WHITE
            );

            appName.setTextSize(12);
            appName.setGravity(
                    Gravity.CENTER
            );
            appName.setMaxLines(1);

            item.addView(
                    icon,
                    new LinearLayout.LayoutParams(
                            80,
                            80
                    )
            );

            item.addView(
                    appName,
                    new LinearLayout.LayoutParams(
                            90,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    )
            );

            item.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent launchIntent =
                                    pm.getLaunchIntentForPackage(
                                            app.packageName
                                    );

                            if (launchIntent != null) {
                                startActivity(
                                        launchIntent
                                );
                            }
                        }
                    }
            );

            GridLayout.LayoutParams params =
                    new GridLayout.LayoutParams();

            params.width = 0;

            params.columnSpec =
                    GridLayout.spec(
                            GridLayout.UNDEFINED,
                            1f
                    );

            appGrid.addView(
                    item,
                    params
            );
        }

        noResultsText.setVisibility(
                apps.isEmpty()
                        ? View.VISIBLE
                        : View.GONE
        );
    }

    private void updateClock() {
        if (clock == null || date == null) {
            return;
        }

        SimpleDateFormat timeFormat =
                new SimpleDateFormat(
                        "HH:mm",
                        Locale.getDefault()
                );

        SimpleDateFormat dateFormat =
                new SimpleDateFormat(
                        "EEE, dd MMM yyyy",
                        Locale.getDefault()
                );

        Date now = new Date();

        clock.setText(
                timeFormat.format(now)
        );

        date.setText(
                dateFormat.format(now)
        );

        clock.postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        updateClock();
                    }
                },
                1000
        );
    }

    @Override
    public void onBackPressed() {
        if (drawerRoot != null) {
            drawerRoot = null;
            showHomeScreen();
        } else {
            super.onBackPressed();
        }
    }
}
