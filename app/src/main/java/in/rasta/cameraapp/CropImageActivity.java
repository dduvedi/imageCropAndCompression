package in.rasta.cameraapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.takusemba.cropme.OnCropListener;

import Interface.ImageCompressor;
import Interface.ImageCompressorImpl;
import Utility.Util;
import ViewModel.CropImageViewModel;
import in.rasta.cameraapp.databinding.ActivityCropImageBinding;

public class CropImageActivity extends AppCompatActivity {
    private ActivityCropImageBinding binding;
    private CropImageViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(CropImageViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_crop_image);
        binding.setViewModel(viewModel);
        binding.executePendingBindings();
        binding.setLifecycleOwner(this);
        binding.setActivity(this);
        setSupportActionBar((android.support.v7.widget.Toolbar) binding.toolbar);

        binding.cropView.setUri((Uri) getIntent().getExtras().get("imageUri"));

        registerForLiveData(viewModel);

    }

    private void registerForLiveData(CropImageViewModel viewModel) {
        viewModel.getImageUploaded().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean) {
                    Util.showToast(CropImageActivity.this, "Image uploaded");
                } else {
                    Util.showToast(CropImageActivity.this, "Error while uploading image");
                }
            }
        });

        viewModel.getShowProgress().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean) {
                    Util.showProgressDialog(CropImageActivity.this, "Uploading image...");
                } else {
                    Util.removeProgressDialog();
                }
            }
        });

        viewModel.getErrorMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                Util.showToast(CropImageActivity.this, s);
            }
        });

    }


    public void cropAndUploadImage() {
        binding.cropView.crop(new OnCropListener() {
            @Override
            public void onSuccess(Bitmap bitmap) {
                ImageCompressor imageCompressor = new ImageCompressorImpl();
                bitmap = imageCompressor.compressBitmap(CropImageActivity.this, Util.getImageUri(CropImageActivity.this, bitmap));
                viewModel.uploadImage(Util.bitmapToByteArray(bitmap));
            }

            @Override
            public void onFailure() {
                Util.showToast(CropImageActivity.this, "Error while cropping image...");
            }
        });
    }
}