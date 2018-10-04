package in.rasta.cameraapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import Adapters.ImageListAdapter;
import Utility.PermissionUtil;
import Utility.Util;
import ViewModel.ImageListViewModel;
import in.rasta.cameraapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Uri imageUri = null;
    private ImageListAdapter imageListAdapter;
    private ImageListViewModel viewModel;
    private LinearLayoutManager linearLayoutManager;
    private int THRESHOLD_TO_LOAD = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(ImageListViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setActivity(this);
        binding.setViewModel(viewModel);
        binding.executePendingBindings();
        binding.setLifecycleOwner(this);

        binding.setActivity(this);
        linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        imageListAdapter = new ImageListAdapter(MainActivity.this);
        binding.recyclerView.setLayoutManager(linearLayoutManager);

        binding.recyclerView.setAdapter(imageListAdapter);
        imageListAdapter.notifyDataSetChanged();
        setSupportActionBar((android.support.v7.widget.Toolbar) binding.toolbar);
        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    int itemCount = linearLayoutManager.getItemCount();
                    int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();

                    if (!viewModel.getShowProgress().getValue() && itemCount <= (lastVisibleItemPosition
                            + THRESHOLD_TO_LOAD)) {
                        viewModel.getImageList();
                    }
                }

            }
        });
        registerForLiveData();
    }

    private void registerForLiveData() {

        viewModel.getImageList().observe(this, new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(@Nullable ArrayList<String> strings) {
                if (strings != null && strings.size() > 0) {
                    imageListAdapter.addAll(strings);
                }
            }
        });


        viewModel.getErrorMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                Util.showToast(MainActivity.this, "Error:" + s);
            }
        });

        viewModel.getShowProgress().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean) {
                    Util.showProgressDialog(MainActivity.this, "Wait...");
                } else {
                    Util.removeProgressDialog();
                }
            }
        });

    }

    public void captureImage() {
        if (PermissionUtil.checkCameraPermission(MainActivity.this)) {
            startCamera();
        }
    }

    public void selectImage() {
        if (PermissionUtil.checkStoragePermission(MainActivity.this)) {
            openGallery();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == PermissionUtil.PERMISSION_CAMERA_REQUEST) {
            if (grantResults.length == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Util.showToast(MainActivity.this, "Camera permission denied.");
            }
        } else if (requestCode == PermissionUtil.PERMISSION_GALLERY_REQUEST) {
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Util.showToast(MainActivity.this, "Storage access permission denied.");
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, 2);
    }

    private void startCamera() {
        imageUri = null;
        imageUri = Util.getUri(MainActivity.this);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        startActivityForResult(cameraIntent, 1);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Intent intent = new Intent(MainActivity.this, CropImageActivity.class);
            intent.putExtra("imageUri", imageUri);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            Intent intent = new Intent(MainActivity.this, CropImageActivity.class);
            intent.putExtra("imageUri", uri);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        }
    }

}
