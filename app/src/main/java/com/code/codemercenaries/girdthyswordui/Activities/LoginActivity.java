package com.code.codemercenaries.girdthyswordui.Activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.widget.ImageView;

import com.code.codemercenaries.girdthyswordui.R;

import java.io.IOException;
import java.io.InputStream;

public class LoginActivity extends AppCompatActivity {

    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        logo = findViewById(R.id.logo);

        InputStream inputStream;
        try {
            inputStream = getAssets().open(getString(R.string.logo_path));
            Drawable drawable = Drawable.createFromStream(inputStream,null);
            logo.setImageDrawable(drawable);

        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
        startActivity(intent);
    }
}
