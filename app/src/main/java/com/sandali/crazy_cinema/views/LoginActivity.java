package com.sandali.crazy_cinema.views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.sandali.crazy_cinema.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences rPreferences;
    private SharedPreferences.Editor rEditor;
    private Button btnSignin;
    private EditText emailEditText;
    private EditText passwordEditText;
    private TextView textViewSignUp;
    private CheckBox rCheckBox;

    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//Connecting to firebase
        firebaseAuth = FirebaseAuth.getInstance();

        //if the user has already logged in
//        if(firebaseAuth.getCurrentUser()!=null)
//        {
//            //profile activity
//            Intent FirstIntent = new Intent(LoginActivity.this, First_Acitivity.class);
//            startActivity(FirstIntent);
//            finish();
//        }

        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        textViewSignUp = (TextView) findViewById(R.id.textViewSignUp);
        btnSignin =(Button) findViewById(R.id.btnSignin);
        rCheckBox =(CheckBox) findViewById(R.id.rCheckBox);

        rPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        rEditor = rPreferences.edit();

        btnSignin.setOnClickListener((View.OnClickListener) this);
        textViewSignUp.setOnClickListener((View.OnClickListener) this);


        checkSharedPreferences();
    }

    private void checkSharedPreferences()
    {
        String checkbox = rPreferences.getString(getString(R.string.checkbox), "False");
        String name = rPreferences.getString(getString(R.string.name), "");
        String password = rPreferences.getString(getString(R.string.password), "");

        emailEditText.setText(name);
        passwordEditText.setText(password);

        if(checkbox.equals("true")){
            rCheckBox.setChecked(true);
        }else
        {
            rCheckBox.setChecked(false);
        }
    }



    private void UserLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();


        if (TextUtils.isEmpty(email)) {
            //email is empty
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            //stopping the function executing further
            return;
        }
        if (TextUtils.isEmpty(password)) {
            //password is empty
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
            //stopping the function executing further
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    //start the profile
//                    Intent FirstIntent = new Intent(LoginActivity.this, HomeActivity.class);
//                    startActivity(FirstIntent);
//                    finish();
                }
            }
        });
    }


  @Override
    public void onClick(View view) {

        if(view==btnSignin){
            finish();
            //startActivity(new Intent(this,HomeActivity.class));
        }
        if(rCheckBox.isChecked())
        {
            rEditor.putString(getString(R.string.checkbox),"true");
            rEditor.commit();

            String name= emailEditText.getText().toString();
            rEditor.putString(getString(R.string.name), name);
            rEditor.commit();

            String password = passwordEditText.getText().toString();
            rEditor.putString(getString(R.string.password),password);
            rEditor.commit();

        }else
        {
            rEditor.putString(getString(R.string.checkbox),"false");
            rEditor.commit();

            rEditor.putString(getString(R.string.name), "");
            rEditor.commit();

            rEditor.putString(getString(R.string.password),"");
            rEditor.commit();
        }
    }
}
