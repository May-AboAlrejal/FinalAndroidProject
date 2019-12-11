package com.mayabo.finalandroidproject.chargestationfinder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.mayabo.finalandroidproject.R;

public class SettingsItem {
    public static final int DIVIDER = -1;
    public static final int GROUP_GENERAL = 0;
    public static final int MAX_RESULTS = 1;
    public static final int DISTANCE_UNIT = 2;
    public static final int GROUP_OTHERS = 3;
    public static final int CLEAR_FAVORITES = 4;

    public static void layoutFor(int which, View view, Context context) {
        switch (which) {
            case SettingsItem.DIVIDER:
                divider(view);
                break;
            case SettingsItem.GROUP_GENERAL:
                groupGeneral(view, context);
                break;
            case SettingsItem.MAX_RESULTS:
                maxResult(view, context);
                break;
            case SettingsItem.DISTANCE_UNIT:
                distanceUnit(view, context);
                break;
            case GROUP_OTHERS:
                groupOthers(view, context);
                break;
            case CLEAR_FAVORITES:
                clearFavorites(view, context);
                break;
        }
    }

    private static void divider(View view) {
        FrameLayout divider = view.findViewById(R.id.divider);
        divider.setVisibility(View.VISIBLE);
        view.setPadding(view.getPaddingLeft(), 0, view.getPaddingRight(), 0);
        view.setClickable(false);
    }

    private static void groupGeneral(View view, Context context) {
        groupTitle(view, context, R.string.general_settings);
    }

    private static void groupOthers(View view, Context context) {
        groupTitle(view, context, R.string.other_settings);
    }

    private static void maxResult(View view, Context context) {
        TextView title = view.findViewById(R.id.setting_title);
        TextView desc = view.findViewById(R.id.setting_desc);
        title.setVisibility(View.VISIBLE);
        desc.setVisibility(View.VISIBLE);
        title.setText(R.string.max_results);
        desc.setText(R.string.max_results_desc);
        view.setOnClickListener(self -> {
            SharedPreferences preferences = context.getSharedPreferences("charge_station_finder_pref", 0);
            SharedPreferences.Editor editor = preferences.edit();
            View content = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.charge_station_finder_settings_item_max_result_dialog, null, false);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            EditText maxResultsView = content.findViewById(R.id.max_result);
            maxResultsView.setText(preferences.getString("max_results", ""));
            maxResultsView.setInputType(InputType.TYPE_CLASS_NUMBER);
            maxResultsView.requestFocus();
            builder.setTitle(R.string.max_results)
                .setView(content)
                .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                    String input = maxResultsView.getText().toString();
                    String maxResults;
                    if (input.isEmpty()) {
                        input = "0";
                    }
                    maxResults = String.valueOf(Long.valueOf(input));
                    editor.putString("max_results", maxResults);
                    editor.apply();
                })
                .setNegativeButton(R.string.cancel, null)
                .create().show();
        });
    }

    private static void distanceUnit(View view, Context context) {
        TextView title = view.findViewById(R.id.setting_title);
        TextView desc = view.findViewById(R.id.setting_desc);
        title.setVisibility(View.VISIBLE);
        desc.setVisibility(View.VISIBLE);
        title.setText(R.string.distance_unit);
        desc.setText(R.string.distance_unit_desc);
        view.setOnClickListener(self -> {
            SharedPreferences preferences = context.getSharedPreferences("charge_station_finder_pref", 0);
            SharedPreferences.Editor editor = preferences.edit();
            View content = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.charge_station_finder_settings_item_distance_unit_dialog, null, false);
            RadioButton kmsView = content.findViewById(R.id.kms);
            RadioButton milesView = content.findViewById(R.id.miles);
            boolean checked = preferences.getBoolean("distance_unit", true);
            kmsView.setChecked(checked);
            milesView.setChecked(!checked);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            AlertDialog dialog = builder.setTitle("Distance Unit")
                .setView(content)
                .setNegativeButton(R.string.cancel, null)
                .create();
            kmsView.setOnCheckedChangeListener((compoundButton, b) -> {
                dialog.dismiss();
                editor.putBoolean("distance_unit", kmsView.isChecked());
                editor.apply();
            });
            milesView.setOnCheckedChangeListener((compoundButton, b) -> {
                dialog.dismiss();
                editor.putBoolean("distance_unit", kmsView.isChecked());
                editor.apply();
            });
            dialog.show();
        });
    }

    private static void clearFavorites(View view, Context context) {
        TextView title = view.findViewById(R.id.setting_title);
        TextView desc = view.findViewById(R.id.setting_desc);
        title.setVisibility(View.VISIBLE);
        desc.setVisibility(View.VISIBLE);
        title.setText(R.string.reset_favorites);
        desc.setText(R.string.reset_favorites_desc);
        view.setOnClickListener(self -> {
            new AlertDialog.Builder(context)
                .setIcon(fillIconWithColor(R.drawable.outline_delete_outline_24, R.color.colorError, context))
                .setTitle("Reset favorites")
                .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                    ChargeStationFinderActivity.favorites.clear();
                    RecordOpenHelper db = new RecordOpenHelper(context);
                    db.reset();
                })
                .setNegativeButton(R.string.cancel, null)
                .create().show();
        });
    }

    private static void groupTitle(View view, Context context, int titleId) {
        final float scale = context.getResources().getDisplayMetrics().density;
        view.setPadding((int)(16 * scale + .5f), (int)(16 * scale + .5f), (int)(16 * scale + .5f), 0);
        view.setClickable(false);
        TextView titleView = view.findViewById(R.id.settings_group_tag);
        titleView.setText(titleId);
        titleView.setVisibility(View.VISIBLE);
    }

    /**
     * Creates a drawable icon from a drawable id and a color id.
     * @param resId id of icon
     * @param colorId id of color to use
     * @return drawable icon with specified color
     */
    private static Drawable fillIconWithColor(int resId, int colorId, Context context) {
        Drawable icon = context.getResources().getDrawable(resId, context.getTheme());
        int color = context.getColor(colorId);
        icon.mutate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            icon.setColorFilter(new BlendModeColorFilter(color, BlendMode.SRC_ATOP));
        } else {
            icon.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }
        return icon;
    }
}
