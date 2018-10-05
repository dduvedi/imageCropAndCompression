package Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import in.rasta.cameraapp.PreviewImage;
import in.rasta.cameraapp.R;
import in.rasta.cameraapp.databinding.AdapterImageListBinding;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.BindingHolder> {

    private Context context;
    private ArrayList<String> imageList;
    private AdapterImageListBinding binding;

    public ImageListAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public BindingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_image_list, parent, false);
        binding = DataBindingUtil.bind(view);
        return new BindingHolder(binding);
    }

    @Override
    public void onBindViewHolder(final BindingHolder holder, final int position) {
        ImageView viewById = holder.getBinding().getRoot().findViewById(R.id.imageView);
        Picasso.with(context).load(imageList.get(position)).into(viewById);


        CardView cardView = holder.getBinding().getRoot().findViewById(R.id.basicDetailLayout);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previewImage(position);
            }
        });
        holder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return imageList == null ? 0 : imageList.size();
    }

    class BindingHolder extends RecyclerView.ViewHolder {

        private AdapterImageListBinding binding;

        BindingHolder(AdapterImageListBinding binding) {
            super(binding.getRoot());
            setBinding(binding);
        }

        public void setBinding(AdapterImageListBinding binding) {
            this.binding = binding;
        }

        public AdapterImageListBinding getBinding() {
            return binding;
        }
    }

    public void addAll(ArrayList<String> imageUrlcollection) {
        int size = 0;
        if (imageList != null) {
            size = imageUrlcollection.size();
            imageUrlcollection.clear();
            imageList.addAll(imageUrlcollection);
        } else {
            imageList = new ArrayList<>();
            imageList.addAll(imageUrlcollection);
        }

        notifyItemRangeChanged(size, imageList.size());
    }

    public void previewImage(int position) {
        Intent intent = new Intent(context, PreviewImage.class);
        intent.putExtra("imageUrl", imageList.get(position));
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation((Activity) context, (View) binding.basicDetailLayout, "profile");
        context.startActivity(intent, options.toBundle());

    }
}
