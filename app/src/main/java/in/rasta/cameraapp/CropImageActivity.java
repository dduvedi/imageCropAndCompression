package in.rasta.cameraapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.firebase.database.Exclude;
import com.takusemba.cropme.OnCropListener;

import java.io.FileNotFoundException;

import Interface.ImageCompressor;
import Interface.ImageCompressorImpl;
import Utility.Constants;
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.cropView.setUri((Uri) getIntent().getExtras().get("imageUri"));


        registerForLiveData(viewModel);
    }

    private void registerForLiveData(CropImageViewModel viewModel) {
        viewModel.getImageUploaded().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean) {
                    Intent intent = new Intent(CropImageActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
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
        try {
            if (Util.isNetworkConnected(CropImageActivity.this)) {
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
            } else {
                Util.showProgressDialog(CropImageActivity.this, Constants.INTERNET_ERROR);
            }
        } catch (Exception e) {
            try {
                viewModel.uploadImage(Util.bitmapToByteArray(BitmapFactory.
                        decodeStream(getContentResolver().openInputStream((Uri) getIntent().getExtras().get("imageUri")))));

            } catch (FileNotFoundException e1) {
                Util.showToast(CropImageActivity.this, "Error while getting image.");
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }
}
