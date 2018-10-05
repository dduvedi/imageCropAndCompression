package in.rasta.cameraapp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import Utility.PermissionUtil;
import Utility.Util;

public class CaptureImage extends AppCompatActivity {

    private Uri imageUri = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public void captureImage() {
        if (PermissionUtil.checkCameraPermission(CaptureImage.this)) {
            startCamera();
        }
    }

    public void selectImage() {
        if (PermissionUtil.checkStoragePermission(CaptureImage.this)) {
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
                Util.showToast(CaptureImage.this, "Camera permission denied.");
            }
        } else if (requestCode == PermissionUtil.PERMISSION_GALLERY_REQUEST) {
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Util.showToast(CaptureImage.this, "Storage access permission denied.");
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
        imageUri = Util.getUri(CaptureImage.this);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        startActivityForResult(cameraIntent, 1);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Intent intent = new Intent(CaptureImage.this, CropImageActivity.class);
            intent.putExtra("imageUri", imageUri);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            Intent intent = new Intent(CaptureImage.this, CropImageActivity.class);
            intent.putExtra("imageUri", uri);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        }
    }
}
