package com.code.codemercenaries.girdthyswordpro.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.code.codemercenaries.girdthyswordpro.R;
import com.code.codemercenaries.girdthyswordpro.adapters.LeaderboardRecycleListAdapter;
import com.code.codemercenaries.girdthyswordpro.beans.remote.User;
import com.code.codemercenaries.girdthyswordpro.persistence.DBConstants;
import com.code.codemercenaries.girdthyswordpro.utilities.Algorithms;
import com.code.codemercenaries.girdthyswordpro.utilities.FontHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class LeaderboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "LeaderboardActivity";
    FontHelper fontHelper;
    RecyclerView list;
    ImageView image;
    TextView rank;
    TextView name;
    ImageView sword;
    TextView versesMemorized;
    LeaderboardRecycleListAdapter adapter;

    FirebaseAuth mAuth;
    DatabaseReference user;
    Query usersQuery;
    User currentUser;
    ArrayList<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fontHelper = new FontHelper();
        fontHelper.initialize(this);

        setContentView(R.layout.activity_leaderboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseDatabase.getInstance().getReference(DBConstants.FIREBASE_TABLE_USERS).child(mAuth.getCurrentUser().getUid());

        TextView displayName = navigationView.getHeaderView(0).findViewById(R.id.display_name);
        ImageView displayImage = navigationView.getHeaderView(0).findViewById(R.id.display_image);
        image = findViewById(R.id.image);
        name = findViewById(R.id.name);
        versesMemorized = findViewById(R.id.versesMemorized);
        sword = findViewById(R.id.sword);
        rank = findViewById(R.id.rank);

        if(mAuth != null && mAuth.getCurrentUser() != null){
            displayName.setText(mAuth.getCurrentUser().getDisplayName());
            Glide.with(this).load(mAuth.getCurrentUser().getPhotoUrl()).into(displayImage);
        }

        user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name.setText(mAuth.getCurrentUser().getDisplayName());
                Glide.with(LeaderboardActivity.this).load(mAuth.getCurrentUser().getPhotoUrl()).into(image);
                currentUser = dataSnapshot.getValue(User.class);
                if (currentUser != null) {
                    versesMemorized.setText(String.format(Locale.getDefault(), "%d", currentUser.getVersesMemorized()));
                    if (currentUser.getEquippedSword() != null) {
                        String swordPath;
                        switch (currentUser.getEquippedSword()) {
                            case DBConstants.BRONZE_SWORD:
                                swordPath = "images/swords/bronze_sword.png";
                                break;
                            case DBConstants.SOLDIER_SWORD:
                                swordPath = "images/swords/soldier_sword.png";
                                break;
                            case DBConstants.PIRATE_SWORD:
                                swordPath = "images/swords/pirate_sword.png";
                                break;
                            case DBConstants.GLASS_SWORD:
                                swordPath = "images/swords/glass_sword.png";
                                break;
                            case DBConstants.GOLD_SWORD:
                                swordPath = "images/swords/gold_sword.png";
                                break;
                            case DBConstants.DIAMOND_SWORD:
                                swordPath = "images/swords/diamond_sword.png";
                                break;
                            default:
                                swordPath = "images/swords/bronze_sword.png";
                        }
                        InputStream inputStream = null;
                        try {
                            inputStream = getAssets().open(swordPath);
                            Drawable drawable = Drawable.createFromStream(inputStream, null);
                            sword.setImageDrawable(drawable);
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ImageView settingsIcon = navigationView.getHeaderView(0).findViewById(R.id.settings_icon);
        settingsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LeaderboardActivity.this,SettingsActivity.class));
            }
        });

        list = findViewById(R.id.list);

        users = new ArrayList<>();

        adapter = new LeaderboardRecycleListAdapter(this,users);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(list.getContext(),linearLayoutManager.getOrientation());
        list.addItemDecoration(dividerItemDecoration);
        list.setHasFixedSize(true);
        list.setAdapter(adapter);

        usersQuery = FirebaseDatabase.getInstance().getReference(DBConstants.FIREBASE_TABLE_USERS).orderByChild(DBConstants.FIREBASE_U_KEY_VERSES_MEMORIZED);

        usersQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    users.add(user);
                    Log.d(TAG, "Downloaded User " + user.getUuid());
                }
                Collections.reverse(users);
                adapter.notifyDataSetChanged();
                Algorithms algorithms = new Algorithms();
                int currentUserRank = algorithms.searchUser(users, mAuth.getCurrentUser().getUid()) + 1;
                rank.setText(String.format(Locale.getDefault(), "%d", currentUserRank));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.toString());
            }
        });
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
        getMenuInflater().inflate(R.menu.leaderboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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
                startActivity(new Intent(LeaderboardActivity.this,HomeActivity.class));
                break;
            case R.id.nav_stats:
                startActivity(new Intent(LeaderboardActivity.this,StatsActivity.class));
                break;
            case R.id.nav_read:
                startActivity(new Intent(LeaderboardActivity.this,ReadActivity.class));
                break;
            case R.id.nav_leaderboard:
                break;
            case R.id.nav_tavern:
                startActivity(new Intent(LeaderboardActivity.this,TavernActivity.class));
                break;
            case R.id.nav_share:
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT,
                        "Hey, check out this cool Bible Memorization app that I found. It uses advanced algorithms and speech recognition to help you memorize Bible verses. It also has a leaderboard where you can compare your progress with other users. And guess what, it's absolutely free!!\n\nhttps://play.google.com/store/apps/details?id=com.code.codemercenaries.girdthysword&hl=en");
                shareIntent.setType("text/plain");
                startActivity(shareIntent);
                break;
            case R.id.nav_blog:
                startActivity(new Intent(LeaderboardActivity.this,BlogActivity.class));
                break;
            case R.id.nav_help:
                break;
            case R.id.nav_about:
                startActivity(new Intent(LeaderboardActivity.this,AboutActivity.class));
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
}
