package in.rasta.cameraapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import Utility.Constants;
import Utility.PermissionUtil;
import Utility.Util;
import in.rasta.cameraapp.databinding.ActivityChooseActionBinding;

public class ChooseAction extends AppCompatActivity {
    private Uri imageUri;
    private ActivityChooseActionBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_choose_action);
        binding.executePendingBindings();
        binding.setLifecycleOwner(this);
        binding.setActivity(this);
        setSupportActionBar((android.support.v7.widget.Toolbar) binding.toolbar);
    }


    public void showImages() {
        if (Util.isNetworkConnected(ChooseAction.this)) {
            Intent intent = new Intent(ChooseAction.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        } else {
            Util.showToast(ChooseAction.this, Constants.INTERNET_ERROR);
        }
    }

    public void captureOrPick() {
        final CharSequence[] items = {"Take Photo", "Choose from Gallery",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ChooseAction.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    captureImage();
                } else if (items[item].equals("Choose from Gallery")) {
                    selectImage();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void captureImage() {
        if (PermissionUtil.checkCameraPermission(ChooseAction.this)) {
            startCamera();
        }
    }

    public void selectImage() {
        if (PermissionUtil.checkStoragePermission(ChooseAction.this)) {
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
                Util.showToast(ChooseAction.this, "Camera permission denied.");
            }
        } else if (requestCode == PermissionUtil.PERMISSION_GALLERY_REQUEST) {
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Util.showToast(ChooseAction.this, "Storage access permission denied.");
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
        imageUri = Util.getUri(ChooseAction.this);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        startActivityForResult(cameraIntent, 1);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Intent intent = new Intent(ChooseAction.this, CropImageActivity.class);
            intent.putExtra("imageUri", imageUri);
            startActivity(intent);
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            Intent intent = new Intent(ChooseAction.this, CropImageActivity.class);
            intent.putExtra("imageUri", uri);
            startActivity(intent);
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        }
    }

}
