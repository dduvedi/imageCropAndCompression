package ViewModel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import Utility.Constants;

public class CropImageViewModel extends ViewModel {

    MutableLiveData<Boolean> imageUploaded = new MutableLiveData<>();

    MutableLiveData<Boolean> showProgress = new MutableLiveData<>();
    MutableLiveData<String> errorMessage = new MutableLiveData<>();


    public MutableLiveData<Boolean> getImageUploaded() {
        return imageUploaded;
    }

    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public MutableLiveData<Boolean> getShowProgress() {
        return showProgress;
    }

    public void uploadImage(byte[] image) {
        if (image != null) {
            showProgress.setValue(true);
            final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(Constants.IMAGE_STORE_PATH + UUID.randomUUID());
            UploadTask uploadTask = storageReference.putBytes(image);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        imageUploaded.setValue(false);
                        showProgress.setValue(false);
                        errorMessage.setValue("Error " + task.getException().getLocalizedMessage());
                        throw task.getException();
                    }
                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (!task.isSuccessful()) {
                        errorMessage.setValue("Error while uploading file");
                        return;
                    }
                    Map<String, Object> requestMap = new HashMap<>();
                    requestMap.put("imageUrl", task.getResult().toString());
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference userRef = reference.child("images");
                    userRef.push().setValue(requestMap).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showProgress.setValue(false);
                            errorMessage.setValue(e.getLocalizedMessage());
                        }
                    });
                    imageUploaded.setValue(true);
                    showProgress.setValue(false);
                }
            });
        } else {
            errorMessage.setValue("No image found to upload");
        }

    }

}
