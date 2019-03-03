package com.code.codemercenaries.girdthyswordpro.activities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.code.codemercenaries.girdthyswordpro.R;
import com.code.codemercenaries.girdthyswordpro.persistence.DBConstants;
import com.code.codemercenaries.girdthyswordpro.utilities.FontHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class StatsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "StatsActivity";

    FontHelper fontHelper;
    ImageView badge5;
    ImageView badge20;
    ImageView badge40;
    ImageView badge70;
    ImageView badge100;
    ImageView badge150;
    TextView versesMemorizedTextView;
    Integer versesMemorized;
    TextView versesAddedTextView;
    Integer versesAdded;

    DatabaseReference usersReference;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fontHelper = new FontHelper();
        fontHelper.initialize(this);

        setContentView(R.layout.activity_stats);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        TextView displayName = navigationView.getHeaderView(0).findViewById(R.id.display_name);
        ImageView displayImage = navigationView.getHeaderView(0).findViewById(R.id.display_image);

        if(mAuth != null && mAuth.getCurrentUser() != null){
            displayName.setText(mAuth.getCurrentUser().getDisplayName());
            Glide.with(this).load(mAuth.getCurrentUser().getPhotoUrl()).into(displayImage);
        }

        ImageView settingsIcon = navigationView.getHeaderView(0).findViewById(R.id.settings_icon);
        settingsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StatsActivity.this,SettingsActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        badge5 = findViewById(R.id.badge5);
        badge20 = findViewById(R.id.badge20);
        badge40 = findViewById(R.id.badge40);
        badge70 = findViewById(R.id.badge70);
        badge100 = findViewById(R.id.badge100);
        badge150 = findViewById(R.id.badge150);
        versesMemorizedTextView = findViewById(R.id.memorizedVerses);
        versesAddedTextView = findViewById(R.id.addedVerses);

        InputStream inputStream;
        Drawable drawable;
        try {
            inputStream = getAssets().open("images/badges/5_hide.png");
            drawable = Drawable.createFromStream(inputStream,null);
            badge5.setImageDrawable(drawable);
            inputStream.close();

            inputStream = getAssets().open("images/badges/20_hide.png");
            drawable = Drawable.createFromStream(inputStream,null);
            badge20.setImageDrawable(drawable);
            inputStream.close();

            inputStream = getAssets().open("images/badges/40_hide.png");
            drawable = Drawable.createFromStream(inputStream,null);
            badge40.setImageDrawable(drawable);
            inputStream.close();

            inputStream = getAssets().open("images/badges/70_hide.png");
            drawable = Drawable.createFromStream(inputStream,null);
            badge70.setImageDrawable(drawable);
            inputStream.close();

            inputStream = getAssets().open("images/badges/100_hide.png");
            drawable = Drawable.createFromStream(inputStream,null);
            badge100.setImageDrawable(drawable);
            inputStream.close();

            inputStream = getAssets().open("images/badges/150_hide.png");
            drawable = Drawable.createFromStream(inputStream,null);
            badge150.setImageDrawable(drawable);
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        versesMemorized = 0;
        versesMemorizedTextView.setText(String.format(Locale.getDefault(),"%d",versesMemorized));

        versesAdded = 0;
        versesAddedTextView.setText(String.format(Locale.getDefault(),"%d",versesAdded));

        mAuth = FirebaseAuth.getInstance();
        usersReference = FirebaseDatabase.getInstance().getReference(DBConstants.FIREBASE_TABLE_USERS).child(mAuth.getCurrentUser().getUid());
        usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(DBConstants.FIREBASE_U_KEY_VERSES_MEMORIZED).getValue() != null) {
                    versesMemorized = dataSnapshot.child(DBConstants.FIREBASE_U_KEY_VERSES_MEMORIZED).getValue(Integer.class);
                    if(versesMemorized != null) {
                        versesMemorizedTextView.setText(String.format(Locale.getDefault(),"%d",versesMemorized));
                        updateBadges();
                    }
                }
                if(dataSnapshot.child(DBConstants.FIREBASE_U_KEY_VERSES_ADDED).getValue() != null) {
                    versesAdded = dataSnapshot.child(DBConstants.FIREBASE_U_KEY_VERSES_ADDED).getValue(Integer.class);
                    if(versesAdded != null) {
                        versesAddedTextView.setText(String.format(Locale.getDefault(),"%d",versesAdded));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.toString());
            }
        });
    }

    private void updateBadges() {
        InputStream inputStream;
        Drawable drawable;
        try {

            if(versesMemorized >= 5) {
                inputStream = getAssets().open("images/badges/5.png");
                drawable = Drawable.createFromStream(inputStream,null);
                badge5.setImageDrawable(drawable);
                inputStream.close();

                if(versesMemorized >= 20) {
                    inputStream = getAssets().open("images/badges/20.png");
                    drawable = Drawable.createFromStream(inputStream,null);
                    badge20.setImageDrawable(drawable);
                    inputStream.close();

                    if(versesMemorized >= 40) {
                        inputStream = getAssets().open("images/badges/40.png");
                        drawable = Drawable.createFromStream(inputStream,null);
                        badge40.setImageDrawable(drawable);
                        inputStream.close();

                        if(versesMemorized >= 70) {
                            inputStream = getAssets().open("images/badges/70.png");
                            drawable = Drawable.createFromStream(inputStream,null);
                            badge70.setImageDrawable(drawable);
                            inputStream.close();

                            if(versesMemorized >= 100) {
                                inputStream = getAssets().open("images/badges/100.png");
                                drawable = Drawable.createFromStream(inputStream,null);
                                badge100.setImageDrawable(drawable);
                                inputStream.close();

                                if(versesMemorized >= 150) {
                                    inputStream = getAssets().open("images/badges/150.png");
                                    drawable = Drawable.createFromStream(inputStream,null);
                                    badge150.setImageDrawable(drawable);
                                    inputStream.close();

                                } else {
                                    inputStream = getAssets().open("images/badges/150_hide.png");
                                    drawable = Drawable.createFromStream(inputStream,null);
                                    badge150.setImageDrawable(drawable);
                                    inputStream.close();
                                }

                            } else {
                                inputStream = getAssets().open("images/badges/100_hide.png");
                                drawable = Drawable.createFromStream(inputStream,null);
                                badge100.setImageDrawable(drawable);
                                inputStream.close();

                                inputStream = getAssets().open("images/badges/150_hide.png");
                                drawable = Drawable.createFromStream(inputStream,null);
                                badge150.setImageDrawable(drawable);
                                inputStream.close();
                            }

                        } else {
                            inputStream = getAssets().open("images/badges/70_hide.png");
                            drawable = Drawable.createFromStream(inputStream,null);
                            badge70.setImageDrawable(drawable);
                            inputStream.close();

                            inputStream = getAssets().open("images/badges/100_hide.png");
                            drawable = Drawable.createFromStream(inputStream,null);
                            badge100.setImageDrawable(drawable);
                            inputStream.close();

                            inputStream = getAssets().open("images/badges/150_hide.png");
                            drawable = Drawable.createFromStream(inputStream,null);
                            badge150.setImageDrawable(drawable);
                            inputStream.close();
                        }

                    } else {
                        inputStream = getAssets().open("images/badges/40_hide.png");
                        drawable = Drawable.createFromStream(inputStream,null);
                        badge40.setImageDrawable(drawable);
                        inputStream.close();

                        inputStream = getAssets().open("images/badges/70_hide.png");
                        drawable = Drawable.createFromStream(inputStream,null);
                        badge70.setImageDrawable(drawable);
                        inputStream.close();

                        inputStream = getAssets().open("images/badges/100_hide.png");
                        drawable = Drawable.createFromStream(inputStream,null);
                        badge100.setImageDrawable(drawable);
                        inputStream.close();

                        inputStream = getAssets().open("images/badges/150_hide.png");
                        drawable = Drawable.createFromStream(inputStream,null);
                        badge150.setImageDrawable(drawable);
                        inputStream.close();
                    }

                } else {
                    inputStream = getAssets().open("images/badges/20_hide.png");
                    drawable = Drawable.createFromStream(inputStream,null);
                    badge20.setImageDrawable(drawable);
                    inputStream.close();

                    inputStream = getAssets().open("images/badges/40_hide.png");
                    drawable = Drawable.createFromStream(inputStream,null);
                    badge40.setImageDrawable(drawable);
                    inputStream.close();

                    inputStream = getAssets().open("images/badges/70_hide.png");
                    drawable = Drawable.createFromStream(inputStream,null);
                    badge70.setImageDrawable(drawable);
                    inputStream.close();

                    inputStream = getAssets().open("images/badges/100_hide.png");
                    drawable = Drawable.createFromStream(inputStream,null);
                    badge100.setImageDrawable(drawable);
                    inputStream.close();

                    inputStream = getAssets().open("images/badges/150_hide.png");
                    drawable = Drawable.createFromStream(inputStream,null);
                    badge150.setImageDrawable(drawable);
                    inputStream.close();
                }

            } else {
                inputStream = getAssets().open("images/badges/5_hide.png");
                drawable = Drawable.createFromStream(inputStream,null);
                badge5.setImageDrawable(drawable);
                inputStream.close();

                inputStream = getAssets().open("images/badges/20_hide.png");
                drawable = Drawable.createFromStream(inputStream,null);
                badge20.setImageDrawable(drawable);
                inputStream.close();

                inputStream = getAssets().open("images/badges/40_hide.png");
                drawable = Drawable.createFromStream(inputStream,null);
                badge40.setImageDrawable(drawable);
                inputStream.close();

                inputStream = getAssets().open("images/badges/70_hide.png");
                drawable = Drawable.createFromStream(inputStream,null);
                badge70.setImageDrawable(drawable);
                inputStream.close();

                inputStream = getAssets().open("images/badges/100_hide.png");
                drawable = Drawable.createFromStream(inputStream,null);
                badge100.setImageDrawable(drawable);
                inputStream.close();

                inputStream = getAssets().open("images/badges/150_hide.png");
                drawable = Drawable.createFromStream(inputStream,null);
                badge150.setImageDrawable(drawable);
                inputStream.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_help) {
            startActivity(new Intent(this, HelpActivity.class));
            return true;
        } else if (id == R.id.action_rate_app) {
            Uri uri = Uri.parse("market://details?id=" + getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            // To count with Play market backstack, After pressing back button,
            // to taken back to our application, we need to add following flags to intent.
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch(id) {
            case R.id.nav_home:
                startActivity(new Intent(StatsActivity.this,HomeActivity.class));
                break;
            case R.id.nav_stats:
                break;
            case R.id.nav_read:
                startActivity(new Intent(StatsActivity.this,ReadActivity.class));
                break;
            case R.id.nav_leaderboard:
                startActivity(new Intent(StatsActivity.this,LeaderboardActivity.class));
                break;
            case R.id.nav_tavern:
                startActivity(new Intent(StatsActivity.this,TavernActivity.class));
                break;
            case R.id.nav_share:
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT,
                        getString(R.string.share_message));
                shareIntent.setType("text/plain");
                startActivity(shareIntent);
                break;
            case R.id.nav_blog:
                startActivity(new Intent(StatsActivity.this,BlogActivity.class));
                break;
            case R.id.nav_help:
                startActivity(new Intent(StatsActivity.this, HelpActivity.class));
                break;
            case R.id.nav_about:
                startActivity(new Intent(StatsActivity.this,AboutActivity.class));
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
