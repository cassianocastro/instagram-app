package com.example.appinstagram.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.appinstagram.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 *
 */
public class AdapterGrid extends ArrayAdapter<String>
{

    private Context context;
    private int layoutResource;
    private List<String> urlFotos;

    public AdapterGrid(@NonNull Context context, int resource, @NonNull List<String> objects)
    {
        super(context, resource, objects);

        this.context        = context;
        this.layoutResource = resource;
        this.urlFotos       = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        ViewHolder viewHolder;

        if ( convertView != null )
        {
            viewHolder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView             = inflater.inflate(layoutResource, parent, false);
            viewHolder.progressBar  = convertView.findViewById(R.id.progressGridPerfil);
            viewHolder.imageView    = convertView.findViewById(R.id.imageGridPerfil);

            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String urlImagem = getItem(position);
        ImageLoader imageLoader = ImageLoader.getInstance();

        imageLoader.displayImage(
            urlImagem,
            viewHolder.imageView,
            new ImageLoadingListener()
            {
                @Override
                public void onLoadingStarted(String imageUri, View view)
                {
                    viewHolder.progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason)
                {
                    viewHolder.progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
                {
                    viewHolder.progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view)
                {
                    viewHolder.progressBar.setVisibility(View.GONE);
                }
            }
        );

        return convertView;
    }

    public class ViewHolder
    {
        ImageView imageView;
        ProgressBar progressBar;
    }
}