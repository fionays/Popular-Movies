package com.nano.android.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
/**
 * This is a custom ImageAdapter which extends the ArrayAdapter.
 * Created by YANG on 9/16/2015.
 */
public class ImageAdapter extends ArrayAdapter<MovieHolder> {
    private static final String LOG_TAG = ImageAdapter.class.getSimpleName();

    /**
     * @param context         The current context to inflate the layout file.
     * @param images          A ArrayList of Integer objects to display in the grid view.
     */
    public ImageAdapter(Context context, ArrayList<MovieHolder> images ) {
        super(context, 0, images);
    }

    /**
     * Get a view which display data in the AdapterView at the given position.
     * @param position     The AdapterView position which want the view.
     * @param convertView  The recycled view to populate.
     * @param parent       The parent which is attachted by the view.
     * @return             The requested view for the position in the AdapterView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView;
        MovieHolder movieHolder = getItem(position);
        LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.grid_item_movie, parent, false);

            //convertView.setTag(R.id.grid_item_image, imageView);
        }

        imageView = (ImageView) convertView.findViewById(R.id.grid_item_image);

        //imageView = (ImageView)convertView.getTag();

        // Adapter load image (get resource) via Picasso into ImageView.
        Picasso.with(getContext()).load(movieHolder.posterPath)
                .placeholder(R.drawable.image_holder)
                .error(R.drawable.image_holder)
                .into(imageView);
        // Still need to return a convertView?
        return convertView;

    }
}