package com.coppermobile.locator;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

/**
 * @author Rohan Taneja
 * Displays the list of all favourites
 */
public class FavouritesActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ArrayList<FavouriteLocation> mFavs;
    private FavouritesArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        setTitle("Favourite Locations");

        mFavs = new ArrayList<>();
        mFavs.addAll(FavouriteLocation.listAll(FavouriteLocation.class));

        adapter = new FavouritesArrayAdapter(FavouritesActivity.this, mFavs);
        mRecyclerView = (RecyclerView) findViewById(R.id.favouritesRecyclerView);

        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(adapter);
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(FavouritesActivity.this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_favourites, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.deleteAll:

                AlertDialog.Builder b = new AlertDialog.Builder(this);
                b.setMessage("Delete all saved locations?");
                b.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FavouriteLocation.deleteAll(FavouriteLocation.class);
                        mFavs.clear();
                        adapter.notifyDataSetChanged();

                    }
                });
                b.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                b.create().show();


                return true;

            case R.id.viewAllFavourites:

                ArrayList<String> lat = new ArrayList<>();
                ArrayList<String> lng = new ArrayList<>();

                for(int i = 0 ; i < mFavs.size(); i++){
                    lat.add(String.valueOf(mFavs.get(i).getLatitude()));
                    lng.add(String.valueOf(mFavs.get(i).getLongitude()));
                }

                Intent i = new Intent(FavouritesActivity.this, AllFavouritesActivity.class);
                i.putStringArrayListExtra("lat", lat);
                i.putStringArrayListExtra("lng", lng);
                startActivity(i);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
