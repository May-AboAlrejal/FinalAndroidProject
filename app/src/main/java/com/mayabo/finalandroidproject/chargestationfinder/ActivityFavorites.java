package com.mayabo.finalandroidproject.chargestationfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.mayabo.finalandroidproject.R;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
import static android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;

public class ActivityFavorites extends AppCompatActivity {
    private RecyclerView mFavoritesView;
    private TextView mEmptyInfoView;
    private MyAdapter mFavoritesAdapter;
    private int mOrigNavigationBarColor;
    private Drawable mPrimaryIconFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge_station_finder_favorites);

        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        backupNavigationBarColor();
        setupNavigationBarColor();

        mPrimaryIconFavorite = fillIconWithColor(R.drawable.outline_favorite_24, getColor(R.color.colorSecondary));

        mFavoritesAdapter = new MyAdapter();

        mFavoritesView = findViewById(R.id.favorites);
        mEmptyInfoView = findViewById(R.id.info_empty);
        mFavoritesView.setAdapter(mFavoritesAdapter);
        mFavoritesView.setLayoutManager(new LinearLayoutManager(this));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
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
                        mFavoritesAdapter.notifyDataSetChanged();
                        record.setIsFavorite(true);
                    })
                    .show();
            }
        });
        itemTouchHelper.attachToRecyclerView(mFavoritesView);

        if (!ChargeStationFinderActivity.favorites.isEmpty()) {
            mEmptyInfoView.setVisibility(View.GONE);
            mFavoritesView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setupNavigationBarColor();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        restoreNavigationBarColor();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void backupNavigationBarColor() {
        mOrigNavigationBarColor = getWindow().getNavigationBarColor();
    }

    private void restoreNavigationBarColor() {
        getWindow().setNavigationBarColor(mOrigNavigationBarColor);
    }

    private void setupNavigationBarColor() {
        getWindow().setNavigationBarColor(getWindow().getDecorView().getRootView().getSolidColor());
        getWindow().getDecorView().setSystemUiVisibility(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS | SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
    }

    private Drawable fillIconWithColor(int resId, int color) {
        Drawable icon = getResources().getDrawable(resId, getTheme());
        icon.mutate();
        icon.setColorFilter(new BlendModeColorFilter(color, BlendMode.SRC_ATOP));
        return icon;
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
            holder.contact.setText(record.getContact());
            holder.isFavorite.setVisibility(View.GONE);
        }

        @Override
        public int getItemCount() {
            return ChargeStationFinderActivity.favorites.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            public TextView title;
            public TextView contact;
            public ImageView isFavorite;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.title);
                contact = itemView.findViewById(R.id.contact);
                isFavorite = itemView.findViewById(R.id.ic_favorite);
            }
        }
    }
}
