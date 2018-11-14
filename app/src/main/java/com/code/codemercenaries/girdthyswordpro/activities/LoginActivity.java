package com.code.codemercenaries.girdthyswordpro.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.code.codemercenaries.girdthyswordpro.R;
import com.code.codemercenaries.girdthyswordpro.beans.remote.User;
import com.code.codemercenaries.girdthyswordpro.persistence.DBConstants;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Date;

public class LoginActivity extends AppCompatActivity {

    private final static String SYSTEM_PREF = "system";
    private final static String TAG = "LoginActivity";
    private final static int RC_SIGN_IN = 2;

    ImageView logo;
    SignInButton signInButton;
    FirebaseAuth mAuth;
    GoogleApiClient mGoogleApiClient;
    FirebaseAuth.AuthStateListener mAuthListener;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        logo = findViewById(R.id.logo);
        signInButton = findViewById(R.id.sign_in_button);

        mAuth = FirebaseAuth.getInstance();

        sharedPreferences = getSharedPreferences(SYSTEM_PREF,0);

        InputStream inputStream;
        try {
            inputStream = getAssets().open(getString(R.string.logo_path));
            Drawable drawable = Drawable.createFromStream(inputStream,null);
            logo.setImageDrawable(drawable);

        } catch (IOException e) {
            e.printStackTrace();
        }

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null) {
                    if(mAuth.getCurrentUser() != null) {
                        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                        progressDialog.setMessage("Downloading Data");

                        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(DBConstants.FIREBASE_TABLE_USERS).child(mAuth.getCurrentUser().getUid());

                        if(isNetworkAvailable() && !sharedPreferences.getBoolean("logged_in",false)) {
                            progressDialog.show();
                        }

                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String remoteRecordUid = dataSnapshot.child("uuid").getValue(String.class);
                                if(remoteRecordUid != null) {
                                    Log.d(TAG,"Account already exists. Signing In");

                                    progressDialog.dismiss();

                                    sharedPreferences.edit().putBoolean("logged_in",true).apply();

                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                } else {
                                    Log.d(TAG,"Account doesn't exist. Creating new record");

                                    Date date = new Date();
                                    long time = date.getTime();
                                    Timestamp timestamp = new Timestamp(time);

                                    User user = new User(mAuth.getCurrentUser().getUid(),mAuth.getCurrentUser().getDisplayName(),mAuth.getCurrentUser().getEmail(),timestamp.toString(),timestamp.toString());
                                    dataSnapshot.getRef().setValue(user);

                                    progressDialog.dismiss();

                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.d(TAG, "Initialization Failed");
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Initialization Failed", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }
        };

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(LoginActivity.this, getString(R.string.error_something_went_wrong), Toast.LENGTH_LONG).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                Toast.makeText(this, getString(R.string.info_logging_in), Toast.LENGTH_LONG).show();
                Log.d(TAG, "First time");
            } else {
                Toast.makeText(LoginActivity.this, getString(R.string.error_authorization_went_wrong), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        //Disable back button
    }

    private void signIn() {

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getEmail());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                        } else {

                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        } else {
            return false;
        }
    }
}
