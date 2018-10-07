package in.rasta.cameraapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.view.MenuItem;

import java.util.ArrayList;

import Adapters.ImageListAdapter;
import Utility.Util;
import ViewModel.ImageListViewModel;
import in.rasta.cameraapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ImageListAdapter imageListAdapter;
    private ImageListViewModel viewModel;
    private GridLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(ImageListViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setViewModel(viewModel);
        binding.executePendingBindings();
        binding.setLifecycleOwner(this);

        binding.setActivity(this);
        linearLayoutManager = new GridLayoutManager(MainActivity.this, 2);
        imageListAdapter = new ImageListAdapter(MainActivity.this);
        binding.recyclerView.setLayoutManager(linearLayoutManager);

        binding.recyclerView.setAdapter(imageListAdapter);
        imageListAdapter.notifyDataSetChanged();
        setSupportActionBar((android.support.v7.widget.Toolbar) binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        registerForLiveData();
    }

    private void registerForLiveData() {

        viewModel.getImageList().observe(this, new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(@Nullable ArrayList<String> strings) {
                if (strings != null && strings.size() > 0) {
                    imageListAdapter.addAll(strings);
                } else {
                    Util.showToast(MainActivity.this, "No image found on server.");
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
