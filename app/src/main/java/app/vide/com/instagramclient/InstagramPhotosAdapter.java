package app.vide.com.instagramclient;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;

import java.util.List;

import app.vide.com.instagramclient.model.InstagramPhoto;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by pando on 1/22/16.
 */


public class InstagramPhotosAdapter extends RecyclerView.Adapter<InstagramPhotosAdapter.ItemPhotoViewHolder> {
    private List<InstagramPhoto> photoList;
    private Context context;

    public InstagramPhotosAdapter(List<InstagramPhoto> photos, Context c){
        this.photoList = photos;
        this.context = c;
    }

    public static class ItemPhotoViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tvCaption) protected TextView tvCaption;
        @Bind(R.id.tvUsername) protected TextView tvUserName;
        @Bind(R.id.ivPhoto) protected ImageView ivPhoto;
        @Bind(R.id.vvVideo) protected VideoView vvVideo;
        protected Context context;

        public ItemPhotoViewHolder(View v, Context context) {
            super(v);
            this.context = context;
            ButterKnife.bind(this, v);
         }


        @OnClick({R.id.ivPhoto, R.id.vvVideo})
        public void OnClickAnnotation() {
            Toast.makeText(this.context, "Hello!", Toast.LENGTH_LONG).show();
        }

    }


    @Override
    public ItemPhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_view, parent, false);
        return new ItemPhotoViewHolder(itemView, this.context);
    }

    @Override
    public void onBindViewHolder(final ItemPhotoViewHolder holder, int position) {
        InstagramPhoto i = photoList.get(position);
        final ItemPhotoViewHolder h = holder;
        holder.tvCaption.setText(i.getCaption());
        holder.tvUserName.setText(i.getUsername());
        holder.ivPhoto.setImageResource(0);
        if(i.getType().equals("video")) {
            Toast.makeText(this.context, i.getUrl(), Toast.LENGTH_LONG).show();
            System.out.println(i.getUrl());
            holder.vvVideo.setVisibility(View.VISIBLE);
            holder.vvVideo.setVideoURI(Uri.parse(i.getUrl()));
            MediaController mediaController = new MediaController(context);
            mediaController.setAnchorView(holder.vvVideo);
            holder.vvVideo.setMediaController(mediaController);
            holder.vvVideo.requestFocus();
            holder.vvVideo.start();

            holder.vvVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                // Close the progress bar and play the video
                public void onPrepared(MediaPlayer mp) {
                    h.vvVideo.start();
                }
            });

            holder.ivPhoto.setVisibility(View.INVISIBLE);
        } else {
            holder.vvVideo.setVisibility(View.INVISIBLE);
            holder.ivPhoto.setVisibility(View.VISIBLE);
            Picasso.with(context).load(i.getUrl()).placeholder(R.drawable.spinning_wheel_ios).into(holder.ivPhoto);
        }
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    public void clear() {
        this.photoList.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<InstagramPhoto> photos) {
        this.photoList.addAll(photos);
        notifyDataSetChanged();
    }

}



