package ViewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ImageListViewModel extends ViewModel {
    private int itemPerPage = 18;
    private String lastItem;
    private MutableLiveData<ArrayList<String>> imageList = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> showProgress = new MutableLiveData<>();

    public LiveData<ArrayList<String>> getImageList() {
        showProgress.setValue(true);
        Query images;
       /* if (lastItem == null || lastItem.isEmpty()) {
        } else {
            images = FirebaseDatabase.getInstance().getReference().child("images").startAt(lastItem).limitToFirst(itemPerPage);
        }
*/
        images = FirebaseDatabase.getInstance().getReference().child("images");

        images.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    showProgress.setValue(false);
                    ArrayList<String> images = new ArrayList<>();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        lastItem = ds.getKey();
                        HashMap<String, Object> object = (HashMap<String, Object>) ds.getValue();
                        images.add((String) object.get("imageUrl"));
                    }
                    imageList.setValue(images);
                } else {
                    showProgress.setValue(false);
                    imageList.setValue(null);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showProgress.setValue(false);
                errorMessage.setValue(databaseError.getMessage());
            }
        });

        return imageList;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getShowProgress() {
        return showProgress;
    }


}
