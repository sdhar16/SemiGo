package com.example.semigo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
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

public class LoginActivity extends AppCompatActivity implements
        View.OnClickListener {

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    String org_name;

    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "MyApp";
    public ProgressBar mProgress;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("ONstart", "hello");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById(R.id.sign_in_button).setOnClickListener(this);

        mProgress = findViewById(R.id.progressBar1);
        mProgress.setVisibility(View.GONE);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
//        Bundle bundle = new Bundle();
//        bundle.putString(FirebaseAnalytics.Param.METHOD, "LoginMethod");
//        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);



    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle I = getIntent().getExtras(); //Get intent if user is logging out or not

        if(I==null){
            // Check if user is signed in (non-null) and update UI accordingly.
            FirebaseUser currentUser = mAuth.getCurrentUser();
            updateUI(currentUser);
        }
        else{
            Boolean b = (Boolean)I.get("logout");
            if(b){
                signOut();
            }

        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mProgress.setVisibility(View.VISIBLE);
        findViewById(R.id.sign_in_button).setVisibility(View.GONE);
//        mProgress.showContextMenu();
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (Exception e) {
                // Google Sign In failed, update UI appropriately
                Log.w("SemiGo:Error", "Google sign in failed", e);
                mProgress.setVisibility(View.GONE);
                findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
                // ...
            }
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("Semigo:startauth", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Semigo:googlesuccess", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            String email_id = user.getEmail();
                            String org = email_id.substring(email_id.indexOf("@")+1, email_id.indexOf(".",email_id.indexOf("@"))).toUpperCase();
                            if(org.equals("GMAIL")){
                                signOut();
                                Toast.makeText(getApplicationContext(),"Please log-in with an organization email-id",Toast.LENGTH_SHORT).show();
                                new Handler().postDelayed(new Runnable() {

                                    @Override
                                    public void run() {
                                        mProgress.setVisibility(View.GONE);
                                        findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
                                    }
                                }, 2200);
                            }
                            else {
                                writeUserToDatabase(user);
                            }
                           // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Semigo:googlefail", "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }
    private void signOut() {
        // Firebase sign out
        Log.d("SignOut", "hello");
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent I = new Intent(this, Main2Activity.class);
            I.putExtra("name",user.getDisplayName());
            I.putExtra("email",user.getEmail());
            I.putExtra("photo", user.getPhotoUrl());
            Log.d("login", org_name + " ");
            I.putExtra("org", org_name);
            startActivity(I);
            finish();
        } else {
            Bundle b = getIntent().getExtras();
            if(b!=null && (Boolean)b.get("logout")){
                Intent I = new Intent(this, LoginActivity.class);
                startActivity(I);
                finish();
            }
        }
    }
    public void writeUserToDatabase(final FirebaseUser user)
    {
        //Get and update database
        Log.d(TAG, "6969");
        final String email_id = user.getEmail(); // cxontains the email id of the user who is signing in
        final String name = user.getDisplayName();  // contains the name of the user who is signing in
        final String UID = user.getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef;
        myRef = database.getReference("User");


        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild(UID))
                {
                    // run some code
                    Log.d(TAG,"HAHAHAHAHAHAHAH");
                }
                else
                {
                  //  FirebaseUser a = user;
                    org_name = email_id.substring(email_id.indexOf("@")+1, email_id.indexOf(".",email_id.indexOf("@"))).toUpperCase();
                    Log.i("Org name", org_name);
                    User u = new User(name, email_id, org_name);
                    myRef.child(UID).setValue(u);
                }

                updateUI(user);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });
    }

}