package com.example.clouddatabaseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.clouddatabaseapp.Transform.APP_PREFERENCES;
import static com.example.clouddatabaseapp.Transform.SaveUser;
import static com.example.clouddatabaseapp.Transform.StringNoNull;
import static com.example.clouddatabaseapp.Transform.Vibrate;
import static com.example.clouddatabaseapp.UserStaticInfo.*;

public class SignActivity extends AppCompatActivity {

    private EditText LoginTextView, PasswordTextView;
    private EditText NewLoginTextView, NewPasswordTextView, NewAgeTextView, NewNameTextView, NewStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        Init();
        CheckSignInInfo();
    }

    private void Init() {
        TabHost tabHost = findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tag1");
        tabSpec.setContent(R.id.tabSignIn);
        tabSpec.setIndicator("Вход");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag2");
        tabSpec.setContent(R.id.tabSignUp);
        tabSpec.setIndicator("Регистрация");
        tabHost.addTab(tabSpec);

        tabHost.setCurrentTab(0);

        LoginTextView = findViewById(R.id.LoginTextView);
        PasswordTextView = findViewById(R.id.PasswordTextView);

        NewLoginTextView = findViewById(R.id.NewLoginTextView);
        NewPasswordTextView = findViewById(R.id.NewPasswordTextView);
        NewAgeTextView = findViewById(R.id.NewAgeTextView);
        NewNameTextView = findViewById(R.id.NewNameTextView);
        NewStateTextView = findViewById(R.id.NewStateTextView);
    }

    private void CheckSignInInfo() {
        SharedPreferences sp = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        String login = sp.getString(LOGIN, "");
        String password = sp.getString(PASSWORD, "");
        LoginTextView.setText(login);
        PasswordTextView.setText(password);
        if (StringNoNull(login) && StringNoNull(password)) {
            SignIn();
        }
    }

    public void SignIn(View view) {
        SignIn();
    }

    public void SignIn() {
        if (EditTextNoNullWithAnimation(LoginTextView) & EditTextNoNullWithAnimation(PasswordTextView)) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference(USERS_SIGN_IN_INFO);
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String login = getLogin();
                    Object value = dataSnapshot.child(login).child(PASSWORD).getValue();
                    if (value != null) {
                        if (value.toString().equals(getPassword())) {
                            Object idObj = dataSnapshot.child(login).child(PROFILE_ID).getValue();
                            String id = idObj != null ? idObj.toString() : "1";
                            goNext(id, login, getPassword());
                        } else {
                            CantSignIn();
                        }
                    } else {
                        CantSignIn();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        } else {
            Vibrate(SignActivity.this);
            Toast.makeText(SignActivity.this, getResources().getText(R.string.NullParametersMessage), Toast.LENGTH_SHORT).show();
        }
    }

    public void SignUp(View view) {
        if (EditTextNoNullWithAnimation(NewLoginTextView) &
                EditTextNoNullWithAnimation(NewPasswordTextView) &
                EditTextNoNullWithAnimation(NewNameTextView) &
                EditTextNoNullWithAnimation(NewStateTextView)) {

            FirebaseDatabase database1 = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database1.getReference(USERS_SIGN_IN_INFO).child(getNewLogin());
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.child(PASSWORD).exists()) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        String id = database.getReference(USERS_PROFILE_INFO).push().getKey();
                        String login = getNewLogin();

                        database.getReference(USERS_SIGN_IN_INFO).child(login).child(PASSWORD).setValue(getNewPassword());
                        database.getReference(USERS_SIGN_IN_INFO).child(login).child(PROFILE_ID).setValue(id);

                        database.getReference(USERS_PROFILE_INFO).child(id).child(AGE).setValue(getNewAge());
                        database.getReference(USERS_PROFILE_INFO).child(id).child(NAME).setValue(getNewName());
                        database.getReference(USERS_PROFILE_INFO).child(id).child(STATE).setValue(getNewState());

                        goNext(id, login, getNewPassword());
                    } else {
                        Toast.makeText(SignActivity.this, getResources().getText(R.string.UserExistMessage), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        } else {
            Vibrate(SignActivity.this);
            Toast.makeText(SignActivity.this, getResources().getText(R.string.NullParametersMessage), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean EditTextNoNullWithAnimation(EditText animationTextView) {
        boolean NoNullText = StringNoNull(animationTextView.getText().toString());
        Animation animation = AnimationUtils.loadAnimation(SignActivity.this, R.anim.error_edit);
        if (!NoNullText) {
            animationTextView.startAnimation(animation);
        }
        return NoNullText;
    }

    private void goNext(String profileId, String login, String password) {
        SaveUser(getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE), login, password);
        UserStaticInfo.profileId = profileId;
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void CantSignIn() {
        Toast.makeText(SignActivity.this, getResources().getText(R.string.CantSignInMessage), Toast.LENGTH_SHORT).show();
    }

    private String getLogin() { return LoginTextView.getText().toString(); }
    private String getPassword() { return PasswordTextView.getText().toString(); }
    private String getNewLogin() { return NewLoginTextView.getText().toString(); }
    private String getNewPassword() { return NewPasswordTextView.getText().toString(); }
    private int getNewAge() { return Transform.parseIntOrDefault(NewAgeTextView.getText().toString(), 0); }
    private String getNewName() { return NewNameTextView.getText().toString(); }
    private String getNewState() { return NewStateTextView.getText().toString(); }
}
