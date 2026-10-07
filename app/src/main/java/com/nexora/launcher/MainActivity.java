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
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {

    private static final int WALLPAPER_PICKER_REQUEST = 1001;
    private static final String WALLPAPER_PREFS = "nexora_wallpaper";
    private static final String WALLPAPER_URI = "wallpaper_uri";

    private static final String CUSTOM_NAME_PREFS =
            "nexora_custom_name_widget";

    private static final String CUSTOM_NAME_EXISTS =
            "exists";

    private static final String CUSTOM_NAME_TEXT =
            "text";

    private static final String CUSTOM_NAME_COLOR1 =
            "color1";

    private static final String CUSTOM_NAME_COLOR2 =
            "color2";

    private static final String CUSTOM_NAME_GLOW =
            "glow";

    private static final String CUSTOM_NAME_SPEED =
            "speed";

    private static final String CUSTOM_NAME_ANIMATION =
            "animation";

    private static final String CUSTOM_NAME_X =
            "x";

    private static final String CUSTOM_NAME_Y =
            "y";

    private static final String HOME_SETTINGS_PREFS =
            "nexora_home_settings";

    private static final String HOME_SHOW_NAME =
            "show_name";

    private static final String HOME_SHOW_CLOCK =
            "show_clock";

    private static final String HOME_SHOW_DATE =
            "show_date";

    private ImageView wallpaperView;

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

        android.content.SharedPreferences homePrefs =
                getSharedPreferences(
                        HOME_SETTINGS_PREFS,
                        MODE_PRIVATE
                );

        boolean showName =
                homePrefs.getBoolean(
                        HOME_SHOW_NAME,
                        true
                );

        boolean showClock =
                homePrefs.getBoolean(
                        HOME_SHOW_CLOCK,
                        true
                );

        boolean showDate =
                homePrefs.getBoolean(
                        HOME_SHOW_DATE,
                        true
                );

        FrameLayout screenRoot = new FrameLayout(this);

        screenRoot.setBackgroundColor(Color.BLACK);
        screenRoot.setClipChildren(false);
        screenRoot.setClipToPadding(false);

        wallpaperView =
                new NexoraParallaxWallpaperView(this);

        wallpaperView.setScaleType(
                ImageView.ScaleType.CENTER_CROP
        );

        wallpaperView.setBackgroundColor(
                Color.rgb(3, 5, 8)
        );

        loadSavedWallpaper(wallpaperView);

        screenRoot.addView(
                wallpaperView,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                )
        );

        NexoraLiquidBackgroundView liquidBackground =
                new NexoraLiquidBackgroundView(this);

        screenRoot.addView(
                liquidBackground,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                )
        );

        homeRoot = new LinearLayout(this);

        homeRoot.setOrientation(
                LinearLayout.VERTICAL
        );

        homeRoot.setGravity(
                Gravity.CENTER_HORIZONTAL
        );

        homeRoot.setBackgroundColor(
                Color.TRANSPARENT
        );

        homeRoot.setPadding(
                24,
                40,
                24,
                24
        );

        screenRoot.addView(
                homeRoot,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                )
        );

        FrameLayout widgetLayer = new FrameLayout(this);

        widgetLayer.setClipChildren(false);
        widgetLayer.setClipToPadding(false);

        widgetLayer.setBackgroundColor(
                Color.TRANSPARENT
        );

        screenRoot.addView(
                widgetLayer,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                )
        );

        restoreCustomNameWidget(
                widgetLayer
        );

        clock = new NexoraClockView(this);

        date = new TextView(this);

        date.setTextColor(
                Color.LTGRAY
        );

        date.setTextSize(16);

        date.setGravity(
                Gravity.CENTER
        );

        date.setPadding(
                0,
                0,
                0,
                30
        );

        name = new NexoraAnimatedTextView(this);

        name.setText("RIKU");

        name.setTextSize(20);

        name.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        name.setGravity(
                Gravity.CENTER
        );

        name.setLetterSpacing(
                0.18f
        );

        name.setSolidColor(
                Color.WHITE
        );

        name.setGlowColor(
                Color.rgb(0, 255, 255)
        );

        name.setGlowRadius(
                20f
        );

        name.setAnimationSpeed(
                3000L
        );

        name.setAnimationMode(
                NexoraAnimatedTextView.AnimationMode.LIQUID
        );

        homeRoot.addView(
                name,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        70
                )
        );

        homeRoot.addView(
                clock,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        homeRoot.addView(
                date,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        name.setVisibility(
                showName
                        ? View.VISIBLE
                        : View.GONE
        );

        clock.setVisibility(
                showClock
                        ? View.VISIBLE
                        : View.GONE
        );

        date.setVisibility(
                showDate
                        ? View.VISIBLE
                        : View.GONE
        );

        View spacer = new View(this);

        homeRoot.addView(
                spacer,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0,
                        1
                )
        );

        Button appDrawerButton = new Button(this);

        appDrawerButton.setText("\uD83E\uDD86");

        appDrawerButton.setTextColor(
                Color.rgb(220, 220, 220)
        );

        appDrawerButton.setTextSize(38);

        appDrawerButton.setAllCaps(false);

        appDrawerButton.setGravity(
                Gravity.CENTER
        );

        appDrawerButton.setBackgroundColor(
                Color.TRANSPARENT
        );

        appDrawerButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAppDrawer();
                    }
                }
        );

        homeRoot.addView(
                appDrawerButton,
                new LinearLayout.LayoutParams(
                        96,
                        96
                )
        );

        homeRoot.setOnLongClickListener(
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showHomeMenu();
                        return true;
                    }
                }
        );

        setContentView(screenRoot);

        updateClock();
    }

    private void openWallpaperPicker() {
        Intent intent =
                new Intent(Intent.ACTION_OPEN_DOCUMENT);

        intent.addCategory(
                Intent.CATEGORY_OPENABLE
        );

        intent.setType("image/*");

        intent.addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
        );

        startActivityForResult(
                intent,
                WALLPAPER_PICKER_REQUEST
        );
    }

    private void loadSavedWallpaper(ImageView target) {
        String uriString =
                getSharedPreferences(
                        WALLPAPER_PREFS,
                        MODE_PRIVATE
                ).getString(
                        WALLPAPER_URI,
                        null
                );

        if (uriString == null) {
            return;
        }

        try {
            target.setImageURI(
                    android.net.Uri.parse(uriString)
            );
        } catch (Exception ignored) {
            getSharedPreferences(
                    WALLPAPER_PREFS,
                    MODE_PRIVATE
            ).edit()
                    .remove(WALLPAPER_URI)
                    .apply();
        }
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data
    ) {
        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );

        if (
                requestCode == WALLPAPER_PICKER_REQUEST
                        && resultCode == RESULT_OK
                        && data != null
                        && data.getData() != null
        ) {
            android.net.Uri selectedUri =
                    data.getData();

            try {
                getContentResolver()
                        .takePersistableUriPermission(
                                selectedUri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                        );
            } catch (Exception ignored) {
            }

            getSharedPreferences(
                    WALLPAPER_PREFS,
                    MODE_PRIVATE
            ).edit()
                    .putString(
                            WALLPAPER_URI,
                            selectedUri.toString()
                    )
                    .apply();

            if (wallpaperView != null) {
                wallpaperView.setImageURI(
                        selectedUri
                );
            }
        }
    }

    private void showHomeMenu() {

        final android.app.Dialog dialog =
                new android.app.Dialog(this);

        LinearLayout menu =
                new LinearLayout(this);

        menu.setOrientation(
                LinearLayout.VERTICAL
        );

        menu.setPadding(
                24,
                24,
                24,
                24
        );

        menu.setBackgroundColor(
                Color.rgb(12, 15, 20)
        );

        TextView title =
                new TextView(this);

        title.setText("NEXORA HOME");
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, 20);

        menu.addView(
                title,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        Button wallpaperButton =
                new Button(this);

        wallpaperButton.setText("🖼️  Wallpaper");
        wallpaperButton.setTextColor(Color.WHITE);
        wallpaperButton.setAllCaps(false);
        wallpaperButton.setTextSize(16);
        wallpaperButton.setBackgroundColor(
                Color.rgb(25, 30, 38)
        );

        wallpaperButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        openWallpaperPicker();
                    }
                }
        );

        menu.addView(
                wallpaperButton,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        60
                )
        );

        Button widgetsButton =
                new Button(this);

        widgetsButton.setText("🧩  Widgets");
        widgetsButton.setTextColor(Color.WHITE);
        widgetsButton.setAllCaps(false);
        widgetsButton.setTextSize(16);
        widgetsButton.setBackgroundColor(
                Color.rgb(25, 30, 38)
        );

        widgetsButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        showWidgetPicker();
                    }
                }
        );

        LinearLayout.LayoutParams widgetParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        60
                );

        widgetParams.topMargin = 12;

        menu.addView(
                widgetsButton,
                widgetParams
        );

        Button settingsButton =
                new Button(this);

        settingsButton.setText("⚙️  Home Settings");
        settingsButton.setTextColor(Color.WHITE);
        settingsButton.setAllCaps(false);
        settingsButton.setTextSize(16);
        settingsButton.setBackgroundColor(
                Color.rgb(25, 30, 38)
        );

        settingsButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        showHomeSettings();
                    }
                }
        );

        LinearLayout.LayoutParams settingsParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        60
                );

        settingsParams.topMargin = 12;

        menu.addView(
                settingsButton,
                settingsParams
        );

        dialog.setContentView(menu);

        android.view.Window window =
                dialog.getWindow();

        if (window != null) {
            window.setBackgroundDrawable(
                    new android.graphics.drawable.ColorDrawable(
                            Color.TRANSPARENT
                    )
            );
        }

        dialog.show();

        window = dialog.getWindow();

        if (window != null) {
            window.setLayout(
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.88f),
                    android.view.WindowManager.LayoutParams.WRAP_CONTENT
            );
        }
    }

    private void showHomeSettings() {

        android.content.SharedPreferences prefs =
                getSharedPreferences(
                        HOME_SETTINGS_PREFS,
                        MODE_PRIVATE
                );

        final android.widget.CheckBox nameCheck =
                new android.widget.CheckBox(this);

        nameCheck.setText("RIKU Name");
        nameCheck.setTextColor(Color.WHITE);
        nameCheck.setTextSize(16);
        nameCheck.setChecked(
                prefs.getBoolean(
                        HOME_SHOW_NAME,
                        true
                )
        );

        final android.widget.CheckBox clockCheck =
                new android.widget.CheckBox(this);

        clockCheck.setText("Clock");
        clockCheck.setTextColor(Color.WHITE);
        clockCheck.setTextSize(16);
        clockCheck.setChecked(
                prefs.getBoolean(
                        HOME_SHOW_CLOCK,
                        true
                )
        );

        final android.widget.CheckBox dateCheck =
                new android.widget.CheckBox(this);

        dateCheck.setText("Date");
        dateCheck.setTextColor(Color.WHITE);
        dateCheck.setTextSize(16);
        dateCheck.setChecked(
                prefs.getBoolean(
                        HOME_SHOW_DATE,
                        true
                )
        );

        LinearLayout panel =
                new LinearLayout(this);

        panel.setOrientation(
                LinearLayout.VERTICAL
        );

        panel.setPadding(
                24,
                24,
                24,
                24
        );

        panel.setBackgroundColor(
                Color.rgb(12, 15, 20)
        );

        panel.addView(
                nameCheck,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        60
                )
        );

        panel.addView(
                clockCheck,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        60
                )
        );

        panel.addView(
                dateCheck,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        60
                )
        );

        android.app.AlertDialog dialog =
                new android.app.AlertDialog.Builder(this)
                        .setTitle("HOME SETTINGS")
                        .setView(panel)
                        .setPositiveButton(
                                "APPLY",
                                null
                        )
                        .setNegativeButton(
                                "CANCEL",
                                null
                        )
                        .create();

        dialog.setOnShowListener(
                new android.content.DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(
                            android.content.DialogInterface d
                    ) {
                        dialog.getButton(
                                android.app.AlertDialog.BUTTON_POSITIVE
                        ).setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        prefs.edit()
                                                .putBoolean(
                                                        HOME_SHOW_NAME,
                                                        nameCheck.isChecked()
                                                )
                                                .putBoolean(
                                                        HOME_SHOW_CLOCK,
                                                        clockCheck.isChecked()
                                                )
                                                .putBoolean(
                                                        HOME_SHOW_DATE,
                                                        dateCheck.isChecked()
                                                )
                                                .apply();

                                        if (name != null) {
                                            name.setVisibility(
                                                    nameCheck.isChecked()
                                                            ? View.VISIBLE
                                                            : View.GONE
                                            );
                                        }

                                        if (clock != null) {
                                            clock.setVisibility(
                                                    clockCheck.isChecked()
                                                            ? View.VISIBLE
                                                            : View.GONE
                                            );
                                        }

                                        if (date != null) {
                                            date.setVisibility(
                                                    dateCheck.isChecked()
                                                            ? View.VISIBLE
                                                            : View.GONE
                                            );
                                        }

                                        dialog.dismiss();
                                    }
                                }
                        );
                    }
                }
        );

        dialog.show();
    }

    private void showWidgetPicker() {

        android.app.AlertDialog.Builder builder =
                new android.app.AlertDialog.Builder(this);

        builder.setTitle("NEXORA Widgets");

        builder.setItems(
                new String[]{
                        "Custom Name"
                },
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(
                            android.content.DialogInterface dialog,
                            int which
                    ) {
                        if (which == 0) {
                            addCustomNameWidget();
                        }
                    }
                }
        );

        builder.setNegativeButton(
                "CANCEL",
                null
        );

        builder.show();
    }

    private void saveCustomNameWidget(
            NexoraCustomNameWidget widget
    ) {

        getSharedPreferences(
                CUSTOM_NAME_PREFS,
                MODE_PRIVATE
        )
                .edit()
                .putBoolean(
                        CUSTOM_NAME_EXISTS,
                        true
                )
                .putString(
                        CUSTOM_NAME_TEXT,
                        widget.getName()
                )
                .putInt(
                        CUSTOM_NAME_COLOR1,
                        widget.getColor1()
                )
                .putInt(
                        CUSTOM_NAME_COLOR2,
                        widget.getColor2()
                )
                .putInt(
                        CUSTOM_NAME_GLOW,
                        widget.getGlowColor()
                )
                .putLong(
                        CUSTOM_NAME_SPEED,
                        widget.getSpeed()
                )
                .putString(
                        CUSTOM_NAME_ANIMATION,
                        widget.getAnimation().name()
                )
                .putFloat(
                        CUSTOM_NAME_X,
                        widget.getX()
                )
                .putFloat(
                        CUSTOM_NAME_Y,
                        widget.getY()
                )
                .apply();
    }

    private void attachCustomNameListeners(
            NexoraCustomNameWidget widget
    ) {

        widget.setOnLongPressListener(
                new NexoraCustomNameWidget.OnLongPressListener() {
                    @Override
                    public void onLongPress(
                            NexoraCustomNameWidget widget
                    ) {
                        showCustomNameSettings(widget);
                    }
                }
        );

        widget.setOnPositionChangedListener(
                new NexoraCustomNameWidget.OnPositionChangedListener() {
                    @Override
                    public void onPositionChanged(
                            NexoraCustomNameWidget widget
                    ) {
                        saveCustomNameWidget(widget);
                    }
                }
        );
    }

    private NexoraCustomNameWidget restoreCustomNameWidget(
            FrameLayout widgetLayer
    ) {

        android.content.SharedPreferences prefs =
                getSharedPreferences(
                        CUSTOM_NAME_PREFS,
                        MODE_PRIVATE
                );

        if (
                !prefs.getBoolean(
                        CUSTOM_NAME_EXISTS,
                        false
                )
        ) {
            return null;
        }

        NexoraCustomNameWidget widget =
                new NexoraCustomNameWidget(this);

        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(
                        300,
                        90
                );

        widgetLayer.addView(
                widget,
                params
        );

        widget.setName(
                prefs.getString(
                        CUSTOM_NAME_TEXT,
                        "RIKU"
                )
        );

        widget.setColor1(
                prefs.getInt(
                        CUSTOM_NAME_COLOR1,
                        Color.WHITE
                )
        );

        widget.setColor2(
                prefs.getInt(
                        CUSTOM_NAME_COLOR2,
                        Color.CYAN
                )
        );

        widget.setGlow(
                prefs.getInt(
                        CUSTOM_NAME_GLOW,
                        Color.CYAN
                ),
                18f
        );

        widget.setSpeed(
                prefs.getLong(
                        CUSTOM_NAME_SPEED,
                        3000L
                )
        );

        try {
            widget.setAnimation(
                    NexoraAnimatedTextView.AnimationMode.valueOf(
                            prefs.getString(
                                    CUSTOM_NAME_ANIMATION,
                                    NexoraAnimatedTextView.AnimationMode.LIQUID.name()
                            )
                    )
            );
        } catch (Exception ignored) {
        }

        final float savedX =
                prefs.getFloat(
                        CUSTOM_NAME_X,
                        0f
                );

        final float savedY =
                prefs.getFloat(
                        CUSTOM_NAME_Y,
                        0f
                );

        widget.post(
                new Runnable() {
                    @Override
                    public void run() {
                        float maxX =
                                Math.max(
                                        0f,
                                        widgetLayer.getWidth()
                                                - widget.getWidth()
                                );

                        float maxY =
                                Math.max(
                                        0f,
                                        widgetLayer.getHeight()
                                                - widget.getHeight()
                                );

                        widget.setX(
                                Math.max(
                                        0f,
                                        Math.min(
                                                savedX,
                                                maxX
                                        )
                                )
                        );

                        widget.setY(
                                Math.max(
                                        0f,
                                        Math.min(
                                                savedY,
                                                maxY
                                        )
                                )
                        );
                    }
                }
        );

        attachCustomNameListeners(
                widget
        );

        return widget;
    }

    private void addCustomNameWidget() {

        if (homeRoot == null) {
            return;
        }

        FrameLayout screenRoot =
                (FrameLayout) homeRoot.getParent();

        if (screenRoot == null) {
            return;
        }

        FrameLayout widgetLayer = null;

        for (int i = 0; i < screenRoot.getChildCount(); i++) {
            View child = screenRoot.getChildAt(i);

            if (child instanceof FrameLayout) {
                if (child != homeRoot) {
                    widgetLayer = (FrameLayout) child;
                }
            }
        }

        if (widgetLayer == null) {
            return;
        }

        for (int i = 0; i < widgetLayer.getChildCount(); i++) {
            if (
                    widgetLayer.getChildAt(i)
                            instanceof NexoraCustomNameWidget
            ) {
                return;
            }
        }

        NexoraCustomNameWidget widget =
                new NexoraCustomNameWidget(this);

        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(
                        300,
                        90
                );

        params.leftMargin =
                Math.max(
                        0,
                        (screenRoot.getWidth() - 300) / 2
                );

        params.topMargin =
                Math.max(
                        0,
                        (screenRoot.getHeight() - 90) / 2
                );

        widgetLayer.addView(
                widget,
                params
        );

        attachCustomNameListeners(
                widget
        );

        saveCustomNameWidget(widget);
    }

    private void addColorPalette(
            LinearLayout parent,
            final EditText target,
            final int[] colors
    ) {

        LinearLayout palette =
                new LinearLayout(this);

        palette.setOrientation(
                LinearLayout.HORIZONTAL
        );

        palette.setGravity(
                Gravity.CENTER
        );

        for (final int color : colors) {

            TextView colorChip =
                    new TextView(this);

            colorChip.setClickable(true);
            colorChip.setFocusable(true);

            android.graphics.drawable.GradientDrawable chipBackground =
                    new android.graphics.drawable.GradientDrawable();

            chipBackground.setShape(
                    android.graphics.drawable.GradientDrawable.OVAL
            );

            chipBackground.setColor(
                    color
            );

            boolean selected =
                    target.getText()
                            .toString()
                            .equalsIgnoreCase(
                                    String.format(
                                            "#%06X",
                                            0xFFFFFF & color
                                    )
                            );

            chipBackground.setStroke(
                    selected ? 4 : 2,
                    selected
                            ? Color.WHITE
                            : Color.argb(
                                    170,
                                    255,
                                    255,
                                    255
                            )
            );

            colorChip.setBackground(
                    chipBackground
            );

            if (selected) {
                colorChip.setElevation(12f);
            }

            colorChip.setElevation(
                    6f
            );

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            42,
                            42
                    );

            params.leftMargin = 6;
            params.rightMargin = 6;

            palette.addView(
                    colorChip,
                    params
            );

            colorChip.setOnTouchListener(
                    (v, event) -> {

                        if (
                                event.getAction()
                                        == android.view.MotionEvent.ACTION_DOWN
                        ) {
                            v.animate()
                                    .scaleX(0.82f)
                                    .scaleY(0.82f)
                                    .setDuration(90L)
                                    .start();
                        } else if (
                                event.getAction()
                                        == android.view.MotionEvent.ACTION_UP
                                        || event.getAction()
                                        == android.view.MotionEvent.ACTION_CANCEL
                        ) {
                            v.animate()
                                    .scaleX(1.0f)
                                    .scaleY(1.0f)
                                    .setDuration(130L)
                                    .start();
                        }

                        return false;
                    }
            );

            colorChip.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            target.setText(
                                    String.format(
                                            "#%06X",
                                            0xFFFFFF & color
                                    )
                            );

                            for (int i = 0; i < palette.getChildCount(); i++) {

                                View child =
                                        palette.getChildAt(i);

                                if (!(child instanceof TextView)) {
                                    continue;
                                }

                                android.graphics.drawable.Drawable drawable =
                                        child.getBackground();

                                if (!(drawable instanceof android.graphics.drawable.GradientDrawable)) {
                                    continue;
                                }

                                android.graphics.drawable.GradientDrawable chip =
                                        (android.graphics.drawable.GradientDrawable)
                                                drawable;

                                if (child == v) {
                                    chip.setStroke(
                                            4,
                                            Color.WHITE
                                    );

                                    child.setElevation(
                                            12f
                                    );
                                } else {
                                    chip.setStroke(
                                            2,
                                            Color.argb(
                                                    170,
                                                    255,
                                                    255,
                                                    255
                                            )
                                    );

                                    child.setElevation(
                                            6f
                                    );
                                }
                            }

                            target.clearFocus();
                        }
                    }
            );
        }

        LinearLayout.LayoutParams paletteParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        58
                );

        paletteParams.topMargin = 4;

        parent.addView(
                palette,
                paletteParams
        );
    }

    private void showCustomNameSettings(
            final NexoraCustomNameWidget widget
    ) {

        LinearLayout panel =
                new LinearLayout(this);

        panel.setOrientation(
                LinearLayout.VERTICAL
        );

        panel.setPadding(
                32,
                28,
                32,
                28
        );

        panel.setBackgroundColor(
                Color.rgb(12, 15, 20)
        );

        TextView title =
                new TextView(this);

        title.setText("CUSTOM NAME");
        title.setTextColor(Color.WHITE);
        title.setTextSize(21);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, 24);

        panel.addView(
                title,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        EditText nameInput =
                new EditText(this);

        nameInput.setHint("Enter name or text");
        nameInput.setText(widget.getName());
        nameInput.setTextColor(Color.WHITE);
        nameInput.setHintTextColor(
                Color.rgb(130, 135, 145)
        );
        nameInput.setTextSize(17);
        nameInput.setSingleLine(true);

        panel.addView(
                nameInput,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        60
                )
        );

        EditText color1Input =
                new EditText(this);

        color1Input.setHint("Color 1  #00FFFF");
        color1Input.setText(
                String.format(
                        "#%06X",
                        0xFFFFFF & widget.getColor1()
                )
        );
        color1Input.setTextColor(Color.WHITE);
        color1Input.setHintTextColor(
                Color.rgb(130, 135, 145)
        );
        color1Input.setSingleLine(true);

        LinearLayout.LayoutParams color1Params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        60
                );

        color1Params.topMargin = 12;

        panel.addView(
                color1Input,
                color1Params
        );

        addColorPalette(
                panel,
                color1Input,
                new int[]{
                        Color.WHITE,
                        Color.CYAN,
                        Color.BLUE,
                        Color.MAGENTA,
                        Color.GREEN,
                        Color.YELLOW,
                        Color.RED
                }
        );

        EditText color2Input =
                new EditText(this);

        color2Input.setHint("Color 2  #FFFFFF");
        color2Input.setText(
                String.format(
                        "#%06X",
                        0xFFFFFF & widget.getColor2()
                )
        );
        color2Input.setTextColor(Color.WHITE);
        color2Input.setHintTextColor(
                Color.rgb(130, 135, 145)
        );
        color2Input.setSingleLine(true);

        LinearLayout.LayoutParams color2Params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        60
                );

        color2Params.topMargin = 12;

        panel.addView(
                color2Input,
                color2Params
        );

        addColorPalette(
                panel,
                color2Input,
                new int[]{
                        Color.WHITE,
                        Color.CYAN,
                        Color.BLUE,
                        Color.MAGENTA,
                        Color.GREEN,
                        Color.YELLOW,
                        Color.RED
                }
        );

        EditText glowInput =
                new EditText(this);

        glowInput.setHint("Glow color  #00FFFF");
        glowInput.setText(
                String.format(
                        "#%06X",
                        0xFFFFFF & widget.getGlowColor()
                )
        );
        glowInput.setTextColor(Color.WHITE);
        glowInput.setHintTextColor(
                Color.rgb(130, 135, 145)
        );
        glowInput.setSingleLine(true);

        LinearLayout.LayoutParams glowParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        60
                );

        glowParams.topMargin = 12;

        panel.addView(
                glowInput,
                glowParams
        );

        addColorPalette(
                panel,
                glowInput,
                new int[]{
                        Color.WHITE,
                        Color.CYAN,
                        Color.BLUE,
                        Color.MAGENTA,
                        Color.GREEN,
                        Color.YELLOW,
                        Color.RED
                }
        );

        android.widget.Spinner animationSpinner =
                new android.widget.Spinner(this);

        String[] animations = {
                "STATIC",
                "SHIMMER",
                "FLASH",
                "HEARTBEAT",
                "LIQUID",
                "RAINBOW",
                "COLOR FLOW"
        };

        android.widget.ArrayAdapter<String> animationAdapter =
                new android.widget.ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        animations
                );

        animationSpinner.setAdapter(
                animationAdapter
        );

        switch (widget.getAnimation()) {

            case STATIC:
                animationSpinner.setSelection(0);
                break;

            case SHIMMER:
                animationSpinner.setSelection(1);
                break;

            case FLASH:
                animationSpinner.setSelection(2);
                break;

            case HEARTBEAT:
                animationSpinner.setSelection(3);
                break;

            case LIQUID:
                animationSpinner.setSelection(4);
                break;

            case RAINBOW:
                animationSpinner.setSelection(5);
                break;

            case COLOR_FLOW:
                animationSpinner.setSelection(6);
                break;
        }

        LinearLayout.LayoutParams animationParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        60
                );

        animationParams.topMargin = 12;

        panel.addView(
                animationSpinner,
                animationParams
        );

        TextView speedLabel =
                new TextView(this);

        speedLabel.setText("Animation Speed");
        speedLabel.setTextColor(Color.LTGRAY);
        speedLabel.setTextSize(15);

        LinearLayout.LayoutParams speedLabelParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        speedLabelParams.topMargin = 18;

        panel.addView(
                speedLabel,
                speedLabelParams
        );

        android.widget.SeekBar speedBar =
                new android.widget.SeekBar(this);

        speedBar.setMax(9000);

        speedBar.setProgress(
                (int)
                        Math.max(
                                0L,
                                Math.min(
                                        9000L,
                                        widget.getSpeed() - 1000L
                                )
                        )
        );

        panel.addView(
                speedBar,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        50
                )
        );

        Button removeButton =
                new Button(this);

        removeButton.setText("REMOVE WIDGET");
        removeButton.setTextColor(
                Color.rgb(255, 120, 120)
        );
        removeButton.setAllCaps(false);
        removeButton.setTextSize(16);
        removeButton.setBackgroundColor(
                Color.rgb(35, 20, 24)
        );

        LinearLayout.LayoutParams removeParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        60
                );

        removeParams.topMargin = 20;

        panel.addView(
                removeButton,
                removeParams
        );

        Button applyButton =
                new Button(this);

        applyButton.setText("APPLY");
        applyButton.setTextColor(Color.WHITE);
        applyButton.setAllCaps(false);
        applyButton.setTextSize(16);
        applyButton.setBackgroundColor(
                Color.rgb(25, 30, 38)
        );

        LinearLayout.LayoutParams applyParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        60
                );

        applyParams.topMargin = 20;

        panel.addView(
                applyButton,
                applyParams
        );

        final android.app.Dialog dialog =
                new android.app.Dialog(this);

        final Runnable updatePreview =
                new Runnable() {
                    @Override
                    public void run() {

                        String newName =
                                nameInput.getText()
                                        .toString()
                                        .trim();

                        if (!newName.isEmpty()) {
                            widget.setName(newName);
                        }

                        try {
                            widget.setColor1(
                                    Color.parseColor(
                                            color1Input.getText()
                                                    .toString()
                                                    .trim()
                                    )
                            );
                        } catch (Exception ignored) {
                        }

                        try {
                            widget.setColor2(
                                    Color.parseColor(
                                            color2Input.getText()
                                                    .toString()
                                                    .trim()
                                    )
                            );
                        } catch (Exception ignored) {
                        }

                        try {
                            widget.setGlow(
                                    Color.parseColor(
                                            glowInput.getText()
                                                    .toString()
                                                    .trim()
                                    ),
                                    18f
                            );
                        } catch (Exception ignored) {
                        }

                        NexoraAnimatedTextView.AnimationMode mode =
                                NexoraAnimatedTextView.AnimationMode.STATIC;

                        switch (
                                animationSpinner
                                        .getSelectedItemPosition()
                        ) {
                            case 1:
                                mode =
                                        NexoraAnimatedTextView.AnimationMode.SHIMMER;
                                break;

                            case 2:
                                mode =
                                        NexoraAnimatedTextView.AnimationMode.FLASH;
                                break;

                            case 3:
                                mode =
                                        NexoraAnimatedTextView.AnimationMode.HEARTBEAT;
                                break;

                            case 4:
                                mode =
                                        NexoraAnimatedTextView.AnimationMode.LIQUID;
                                break;

                            case 5:
                                mode =
                                        NexoraAnimatedTextView.AnimationMode.RAINBOW;
                                break;

                            case 6:
                                mode =
                                        NexoraAnimatedTextView.AnimationMode.COLOR_FLOW;
                                break;
                        }

                        widget.setAnimation(mode);

                        long speed =
                                1000L
                                        + speedBar.getProgress();

                        widget.setSpeed(speed);
                    }
                };

        android.text.TextWatcher livePreviewWatcher =
                new android.text.TextWatcher() {

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
                        updatePreview.run();
                    }

                    @Override
                    public void afterTextChanged(
                            android.text.Editable s
                    ) {
                    }
                };

        nameInput.addTextChangedListener(
                livePreviewWatcher
        );

        color1Input.addTextChangedListener(
                livePreviewWatcher
        );

        color2Input.addTextChangedListener(
                livePreviewWatcher
        );

        glowInput.addTextChangedListener(
                livePreviewWatcher
        );

        nameInput.setOnFocusChangeListener(
                (v, hasFocus) -> {
                    if (!hasFocus) {
                        updatePreview.run();
                    }
                }
        );

        color1Input.setOnFocusChangeListener(
                (v, hasFocus) -> {
                    if (!hasFocus) {
                        updatePreview.run();
                    }
                }
        );

        color2Input.setOnFocusChangeListener(
                (v, hasFocus) -> {
                    if (!hasFocus) {
                        updatePreview.run();
                    }
                }
        );

        glowInput.setOnFocusChangeListener(
                (v, hasFocus) -> {
                    if (!hasFocus) {
                        updatePreview.run();
                    }
                }
        );

        animationSpinner.setOnItemSelectedListener(
                new android.widget.AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            android.widget.AdapterView<?> parent,
                            View view,
                            int position,
                            long id
                    ) {
                        updatePreview.run();
                    }

                    @Override
                    public void onNothingSelected(
                            android.widget.AdapterView<?> parent
                    ) {
                    }
                }
        );

        speedBar.setOnSeekBarChangeListener(
                new android.widget.SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(
                            android.widget.SeekBar seekBar,
                            int progress,
                            boolean fromUser
                    ) {
                        if (fromUser) {
                            updatePreview.run();
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(
                            android.widget.SeekBar seekBar
                    ) {
                    }

                    @Override
                    public void onStopTrackingTouch(
                            android.widget.SeekBar seekBar
                    ) {
                    }
                }
        );

        removeButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        android.view.ViewParent parent =
                                widget.getParent();

                        if (parent instanceof FrameLayout) {
                            ((FrameLayout) parent)
                                    .removeView(widget);
                        }

                        getSharedPreferences(
                                CUSTOM_NAME_PREFS,
                                MODE_PRIVATE
                        )
                                .edit()
                                .clear()
                                .apply();

                        dialog.dismiss();
                    }
                }
        );

        applyButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updatePreview.run();
                        saveCustomNameWidget(widget);
                        dialog.dismiss();
                    }
                }
        );

        dialog.setContentView(panel);

        android.view.Window window =
                dialog.getWindow();

        if (window != null) {
            window.setBackgroundDrawable(
                    new android.graphics.drawable.ColorDrawable(
                            Color.TRANSPARENT
                    )
            );
        }

        dialog.show();

        window = dialog.getWindow();

        if (window != null) {
            window.setLayout(
                    (int)
                            (
                                    getResources()
                                            .getDisplayMetrics()
                                            .widthPixels
                                            * 0.90f
                            ),
                    android.view.WindowManager.LayoutParams.WRAP_CONTENT
            );
        }
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

            item.setOnTouchListener(
                    (v, event) -> {
                        if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                            v.animate()
                                    .scaleX(0.90f)
                                    .scaleY(0.90f)
                                    .setDuration(100L)
                                    .start();
                        } else if (
                                event.getAction() == android.view.MotionEvent.ACTION_UP
                                        || event.getAction() == android.view.MotionEvent.ACTION_CANCEL
                        ) {
                            v.animate()
                                    .scaleX(1.0f)
                                    .scaleY(1.0f)
                                    .setDuration(140L)
                                    .start();
                        }

                        return false;
                    }
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
