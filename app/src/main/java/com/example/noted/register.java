package com.example.noted;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.os.Bundle;
import android.widget.*;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class register extends AppCompatActivity {

    public static final String TAG = "TAG";
    EditText mfname, mlname, muserid, memail, mpassword;
    Button mbtnRegister;
    FirebaseAuth mAuth;
    ProgressBar mprogressBar;
    FirebaseFirestore db;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mfname = findViewById(R.id.fname);
        mlname = findViewById(R.id.lname);
        muserid = findViewById(R.id.userid);
        memail = findViewById(R.id.email);
        mpassword = findViewById(R.id.password);
        mprogressBar = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();
        mbtnRegister = findViewById(R.id.btnRegister);
        db = FirebaseFirestore.getInstance();

        //check if already logged in
//        if(mAuth.getCurrentUser() != null){
//            startActivity(new Intent(getApplicationContext(), dashboard.class));
//            finish();
//        }

        mbtnRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {


                final String email = memail.getText().toString().trim();
                String password = mpassword.getText().toString().trim();
                final String fname = mfname.getText().toString().trim();
                final String lname = mlname.getText().toString().trim();
                final String userid = muserid.getText().toString().trim();


                //Validate values
                if(TextUtils.isEmpty(fname)){
                    mfname.setError("Enter your name here");
                    return;
                }
                if(TextUtils.isEmpty(userid)){
                    muserid.setError("Enter a username for yourself");
                    return;
                }
                if(TextUtils.isEmpty(email)){
                    memail.setError("Email is required");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    mpassword.setError("Password is required");
                    return;
                }

                if(password.length()<6){
                    mpassword.setError("Password must have at least 6 characters");
                    return;
                }

                mprogressBar.setVisibility(View.VISIBLE);

                //Start registering the user
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override

                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            Toast.makeText(register.this, "Account Registered",
                                    Toast.LENGTH_SHORT).show();

                            userID = mAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = db.collection("users").document(userID);
                            Map<String, Object> user = new HashMap<>();
                            user.put("fname", fname);
                            user.put("lname", lname);
                            user.put("email", email);
                            user.put("userid", userid);
                            Toast.makeText(register.this, "Checkpoint", Toast.LENGTH_SHORT).show();
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "OnSuccess : User profile is created for " + userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "OnFailure : " + e.toString());
                                    Toast.makeText(register.this, "DB FAIIL", Toast.LENGTH_SHORT).show();
                                }
                            });

                            startActivity(new Intent(getApplicationContext(), dashboard.class));
                        }
                        else{
                            Toast.makeText(register.this, "Error! " +
                                    task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        });

        TextView reg = (TextView) findViewById(R.id.linkLogin);
        reg.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                goToLogin();
            }
        });
    }

    private void goToLogin() {

        Intent intent = new Intent(this, register.class);
        startActivity(intent);
    }
}
