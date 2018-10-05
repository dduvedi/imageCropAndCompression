package in.rasta.cameraapp;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.squareup.picasso.Picasso;

import Utility.TouchImageView;

public class PreviewImage extends AppCompatActivity {
    private Toolbar toolbar;
    private TouchImageView previewImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_docimage);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        previewImage = (TouchImageView) findViewById(R.id.previewImage);
        PreferenceManager.getDefaultSharedPreferences(PreviewImage.this).edit().putBoolean("isShow", false).commit();

        Picasso.with(PreviewImage.this).load(getIntent().getExtras().getString("imageUrl")).into(previewImage);

    }

    /**
     * Overriding back button of android system
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        supportFinishAfterTransition();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


}