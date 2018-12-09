package com.junaid.uberclone;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.junaid.uberclone.Model.User;
import com.rengwuxian.materialedittext.MaterialEditText;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_login, btn_signup;
    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;
    ConstraintLayout rootLayout;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/akrhip_font.ttf")
                .setFontAttrId(R.attr.fontPath).build());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference("users");

        rootLayout = findViewById(R.id.rootLayout);
        btn_login = findViewById(R.id.btn_sign_in);
        btn_signup = findViewById(R.id.btn_sign_up);

        btn_login.setOnClickListener(this);
        btn_signup.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        if (v == btn_login) {
            showLoginDialog();

        } else if (v == btn_signup) {
            showRegisterDialog();
        }
    }

    private void showLoginDialog() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.sign_up);
        dialog.setMessage("Please use email to sign in");

        LayoutInflater inflater = LayoutInflater.from(this);
        View login_layout = inflater.inflate(R.layout.layout_login, null);
        final MaterialEditText editText_email = login_layout.findViewById(R.id.editText_email);
        final MaterialEditText editText_password = login_layout.findViewById(R.id.editText_password);

        dialog.setView(login_layout);
        dialog.setPositiveButton("LOG IN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (TextUtils.isEmpty(editText_email.getText().toString().trim())) {
                    Snackbar.make(rootLayout, "Please Enter Email Address", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(editText_password.getText().toString().trim())) {
                    Snackbar.make(rootLayout, "Please Enter Email Password", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                auth.signInWithEmailAndPassword(editText_email.getText().toString(), editText_password.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                startActivity(new Intent(LoginActivity.this, WelcomeActivity.class));

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Snackbar.make(rootLayout,
                                e.getMessage(),
                                Snackbar.LENGTH_SHORT)
                                .show();


                    }
                });


            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

            }
        });
        dialog.show();

    }

    private void showRegisterDialog() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.sign_up);
        dialog.setMessage("Please use email to register");

        LayoutInflater inflater = LayoutInflater.from(this);
        View register_layout = inflater.inflate(R.layout.layout_register, null);
        final MaterialEditText editText_email = register_layout.findViewById(R.id.editText_email);
        final MaterialEditText editText_password = register_layout.findViewById(R.id.editText_password);
        final MaterialEditText editText_Phone = register_layout.findViewById(R.id.editText_phone);
        final MaterialEditText editText_name = register_layout.findViewById(R.id.editText_name);
        dialog.setView(register_layout);
        dialog.setPositiveButton("SignUp", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (TextUtils.isEmpty(editText_email.getText().toString().trim())) {
                    Snackbar.make(rootLayout, "Please Enter Email Address", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(editText_password.getText().toString().trim())) {
                    Snackbar.make(rootLayout, "Please Enter Email Password", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(editText_name.getText().toString().trim())) {
                    Snackbar.make(rootLayout, "Please Enter Email Name", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(editText_Phone.getText().toString().trim())) {
                    Snackbar.make(rootLayout, "Please Enter Email Phone", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                auth.createUserWithEmailAndPassword(editText_email.getText().toString(), editText_password.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {

                                User user = new User();
                                user.setEmail(editText_email.getText().toString().trim());
                                user.setPassword(editText_password.getText().toString().trim());
                                user.setName(editText_name.getText().toString().trim());
                                user.setPhone(editText_Phone.getText().toString().trim());

                                //users email to key
                                users.child(auth.getCurrentUser().getUid())
                                        .setValue(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Snackbar.make(rootLayout,
                                                        "User Registered Successfully",
                                                        Snackbar.LENGTH_SHORT)
                                                        .show();

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Snackbar.make(rootLayout,
                                                        e.getMessage(),
                                                        Snackbar.LENGTH_SHORT)
                                                        .show();

                                            }
                                        });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(rootLayout,
                                e.getMessage(),
                                Snackbar.LENGTH_SHORT)
                                .show();
                    }
                });

            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });

        dialog.show();
    }
}
