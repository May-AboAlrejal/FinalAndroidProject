package com.mayabo.finalandroidproject.chargestationfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.material.snackbar.Snackbar;
import com.mayabo.finalandroidproject.R;

import static android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
import static android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;

public class ActivityFavorites extends AppCompatActivity {
    private RecyclerView mFavoritesView;
    private TextView mEmptyInfoView;
    private MyAdapter mFavoritesAdapter;
    private int mOrigNavigationBarColor;

    /**
     * Initializes class variables to initial states.
     * @param savedInstanceState previous status
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge_station_finder_favorites);

        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        backupNavigationBarColor();
        setupNavigationBarColor();

        mFavoritesAdapter = new MyAdapter();

        mFavoritesView = findViewById(R.id.favorites);
        mEmptyInfoView = findViewById(R.id.info_empty);
        mFavoritesView.setAdapter(mFavoritesAdapter);
        mFavoritesView.setLayoutManager(new LinearLayoutManager(this));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback());
        itemTouchHelper.attachToRecyclerView(mFavoritesView);

        if (!ChargeStationFinderActivity.favorites.isEmpty()) {
            mEmptyInfoView.setVisibility(View.GONE);
            mFavoritesView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Restores previous status.
     */
    @Override
    public void onResume() {
        super.onResume();
        setupNavigationBarColor();
    }

    /**
     * Restore to default settings.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        restoreNavigationBarColor();
    }

    /**
     * Calls onBackPressed.
     * @return true
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Save the default settings.
     */
    private void backupNavigationBarColor() {
        mOrigNavigationBarColor = getWindow().getNavigationBarColor();
    }

    /**
     * Revert to the default settings.
     */
    private void restoreNavigationBarColor() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setNavigationBarColor(mOrigNavigationBarColor);
    }

    /**
     * Set to custom settings.
     */
    private void setupNavigationBarColor() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(getColor(R.color.colorPrimary));
        getWindow().setNavigationBarColor(getWindow().getDecorView().getRootView().getSolidColor());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().getDecorView().setSystemUiVisibility(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS | SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
    }

    /**
     * Creates an icon with color specified.
     * @param resId id of the icon to use
     * @param color color to use
     * @return colored drawable icon
     */
    private Drawable fillIconWithColor(int resId, int color) {
        Drawable icon = getResources().getDrawable(resId, getTheme());
        icon.mutate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            icon.setColorFilter(new BlendModeColorFilter(color, BlendMode.SRC_ATOP));
        } else {
            icon.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }
        return icon;
    }

    public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
        private Drawable icon;
        private final ColorDrawable background;
        private final ColorDrawable divider;
        private final ColorDrawable clearDivider;

        public SwipeToDeleteCallback() {
            super(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
            icon = fillIconWithColor(R.drawable.outline_delete_outline_24, Color.parseColor("#ffffff"));
            background = new ColorDrawable(getColor(R.color.colorSecondaryDark));
            divider = new ColorDrawable(Color.parseColor("#D0D0D0"));
            clearDivider = new ColorDrawable(Color.parseColor("#ffffff"));
        }
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }
        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            Record record = ChargeStationFinderActivity.favorites.get(position);
            RecordOpenHelper db = new RecordOpenHelper(ActivityFavorites.this);
            db.remove(record);
            ChargeStationFinderActivity.favorites.remove(position);
            mFavoritesAdapter.notifyItemRemoved(position);
            record.setIsFavorite(false);
            Snackbar.make(findViewById(R.id.root), record.getTitle() + " removed", Snackbar.LENGTH_LONG)
                .setAction("Undo", view -> {
                    db.insert(record);
                    ChargeStationFinderActivity.favorites.add(position, record);
                    mFavoritesAdapter.notifyItemInserted(position);
                    record.setIsFavorite(true);
                })
                .show();
        }
        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            View itemView = viewHolder.itemView;
            int backgroundCornerOffset = 20;
            int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
            int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
            int iconBottom = iconTop + icon.getIntrinsicHeight();

            if (dX > 0) { // Swiping to the right
                int iconLeft = itemView.getLeft() + iconMargin;
                int iconRight = itemView.getLeft() + iconMargin + icon.getIntrinsicWidth();
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                background.setBounds(
                    itemView.getLeft(),
                    itemView.getTop(),
                    itemView.getLeft() + ((int) dX) + backgroundCornerOffset,
                    itemView.getBottom()
                );
            } else if (dX < 0) { // Swiping to the left
                int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
                int iconRight = itemView.getRight() - iconMargin;
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                background.setBounds(
                    itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                    itemView.getTop(),
                    itemView.getRight(),
                    itemView.getBottom()
                );
            } else { // view is unSwiped
                background.setBounds(0, 0, 0, 0);
            }

            ColorDrawable whichDivider;

            if (dX != 0) {
                whichDivider = divider;
            } else {
                whichDivider = clearDivider;
            }
            whichDivider.setBounds(
                itemView.getLeft(),
                itemView.getBottom(),
                itemView.getRight(),
                itemView.getBottom() + 1
            );

            background.draw(c);
            icon.draw(c);
            whichDivider.draw(c);
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View content = inflater.inflate(R.layout.charge_station_brief, parent, false);
            return new ViewHolder(content);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Record record = ChargeStationFinderActivity.favorites.get(position);
            holder.title.setText(record.getTitle());
            if (record.getContact() != null) {
                holder.contact.setText(record.getContact());
            }
            if (record.getAddress() != null) {
                holder.address.setText(record.getAddress());
            }
            holder.distance.setVisibility(View.GONE);
            holder.isFavorite.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(view -> {
                View content = getLayoutInflater().inflate(R.layout.charge_station_detail, null, false);
                ((TextView) content.findViewById(R.id.title)).setText(record.getTitle());
                ((TextView) content.findViewById(R.id.latitude)).setText(record.getLatitude());
                ((TextView) content.findViewById(R.id.longitude)).setText(record.getLongitude());
                ((TextView) content.findViewById(R.id.contact)).setText(record.getContact());
                new AlertDialog.Builder(ActivityFavorites.this)
                    .setIcon(fillIconWithColor(R.drawable.outline_info_24, getColor(R.color.colorPrimary)))
                    .setTitle(record.getTitle())
                    .setView(content)
                    .setPositiveButton("Open map", (dialogInterface, i) -> {
                        Uri gmmIntentUri = Uri.parse("geo:" + record.getLatitude() + "," + record.getLongitude() + "?q=" + record.getAddress());
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    })
                    .setNegativeButton("Cancel", (dialogInterface, i) -> {})
                    .create().show();
            });
        }

        @Override
        public int getItemCount() {
            return ChargeStationFinderActivity.favorites.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            public TextView title;
            public TextView contact;
            public TextView address;
            public TextView distance;
            public ImageView isFavorite;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.title);
                contact = itemView.findViewById(R.id.contact);
                address = itemView.findViewById(R.id.address);
                distance = itemView.findViewById(R.id.distance);
                isFavorite = itemView.findViewById(R.id.ic_favorite);
            }
        }
    }
}
