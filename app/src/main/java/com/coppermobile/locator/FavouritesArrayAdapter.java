package com.coppermobile.locator;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;

/**
 * @author Rohan Taneja
 * Array Adapter for favourite locations.
 */
public class FavouritesArrayAdapter extends RecyclerView.Adapter<FavouritesArrayAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<FavouriteLocation> mFavourites;

    public FavouritesArrayAdapter(Context context, ArrayList<FavouriteLocation> favourites) {
        mContext = context;
        mFavourites = favourites;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.favourites_list_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        TextView textOfFavourite = holder.titleOfFavouriteTextView;

        final FavouriteLocation favouriteLocation = mFavourites.get(position);
        textOfFavourite.setText(favouriteLocation.getTitle());

        final int positionInDatabase = position + 1;

        textOfFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> items = new ArrayList<>();
                items.add("Show on map");
                items.add("Delete");

                new MaterialDialog.Builder(mContext)
                        .title(favouriteLocation.getTitle())
                        .items(items)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {

                                switch (which) {

                                    case 0:

                                        Intent i = new Intent(mContext, MapsActivity.class);
                                        i.putExtra(Constants.LATITUDE, favouriteLocation.getLatitude());
                                        i.putExtra(Constants.LONGITUDE, favouriteLocation.getLongitude());
                                        mContext.startActivity(i);

                                        break;

                                    case 1:

                                        FavouriteLocation fav = FavouriteLocation.findById(FavouriteLocation.class, favouriteLocation.getId());
                                        fav.delete();
                                        mFavourites.remove(positionInDatabase - 1);
                                        notifyDataSetChanged();

                                        break;

                                }
                            }
                        }).show();
            }
        });

        textOfFavourite.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(mContext, "Lat/Long: " + favouriteLocation.getLatitude() + "/" + favouriteLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }


    @Override
    public int getItemCount() {
        return mFavourites.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView titleOfFavouriteTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            titleOfFavouriteTextView = (TextView) itemView.findViewById(R.id.favouritesTitle);
        }
    }

}
