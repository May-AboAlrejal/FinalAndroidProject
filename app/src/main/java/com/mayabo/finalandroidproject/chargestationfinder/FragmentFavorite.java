package com.mayabo.finalandroidproject.chargestationfinder;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.mayabo.finalandroidproject.R;

public class FragmentFavorite extends Fragment {
    private RecyclerView mFavoritesView;
    private MyAdapter mFavoritesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result =  inflater.inflate(R.layout.fragment_charge_station_finder_favorites, container, false);

        mFavoritesView = result.findViewById(R.id.favorites);
        mFavoritesAdapter = new MyAdapter();

        mFavoritesView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mFavoritesView.setAdapter(mFavoritesAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback());
        itemTouchHelper.attachToRecyclerView(mFavoritesView);

        if (!ChargeStationFinderActivity.favorites.isEmpty()) {
//            mEmptyInfoView.setVisibility(View.GONE);
            mFavoritesView.setVisibility(View.VISIBLE);
        }

        return result;
    }

    private Drawable fillIconWithColor(int resId, int color) {
        Drawable icon = getResources().getDrawable(resId, this.getActivity().getTheme());
        icon.mutate();
        icon.setColorFilter(new BlendModeColorFilter(color, BlendMode.SRC_ATOP));
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
            background = new ColorDrawable(FragmentFavorite.this.getActivity().getColor(R.color.colorSecondaryDark));
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
            RecordOpenHelper db = new RecordOpenHelper(FragmentFavorite.this.getActivity());
            db.remove(record);
            ((ChargeStationFinderActivity) getActivity()).removeFavoriteItem(position);
            mFavoritesAdapter.notifyItemRemoved(position);
            record.setIsFavorite(false);
            Snackbar.make(FragmentFavorite.this.getActivity().findViewById(R.id.root), record.getTitle() + " removed", Snackbar.LENGTH_LONG)
                .setAction("Undo", view -> {
                    db.insert(record);
                    ((ChargeStationFinderActivity) getActivity()).addFavoriteItem(position, record);
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
                new AlertDialog.Builder(FragmentFavorite.this.getContext())
                        .setIcon(fillIconWithColor(R.drawable.outline_info_24, FragmentFavorite.this.getContext().getColor(R.color.colorPrimary)))
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
