package com.example.passreader;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import com.google.common.hash.Hashing;
import org.json.JSONObject;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.charset.Charset;
import java.security.Key;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Home extends AppCompatActivity {

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginBtn = findViewById(R.id.button);
        Button newAccountBtn = findViewById(R.id.newAccount);
        EditText username = findViewById(R.id.username);
        EditText hidePass = findViewById(R.id.password);
        EditText showPass = findViewById(R.id.VisiblePass);
        Switch show = findViewById(R.id.switch1);
        TextView errorView = findViewById(R.id.errorView);

        loginBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    loginBtn.setBackgroundResource(R.drawable.button_clicked);
                } else if(event.getAction() == MotionEvent.ACTION_UP){
                    loginBtn.setBackgroundResource(R.drawable.button);
                }

                return false;
            }
        });

        loginBtn.setOnClickListener(event -> {

            try {
                FileReader reader = new FileReader(getFilesDir() + "/values.json");
                int i;
                StringBuilder jsonValue = new StringBuilder();
                while((i = reader.read()) != -1){ jsonValue.append((char)i); }
                reader.close();

                String password = show.isChecked() ? showPass.getText().toString() : hidePass.getText().toString();

                String newValue = decrypt(jsonValue.toString(), toSHA256(password).substring(0, 32));

                JSONObject json = new JSONObject(newValue);

                FileWriter writer = new FileWriter(getFilesDir() + "/values.json");
                writer.write(newValue);
                writer.close();


                if (json.get("username").equals(toSHA256(username.getText().toString()))
                    && json.get("password").equals(toSHA256(password))) {

                    Intent passwords = new Intent(this, Passwords.class);
                    startActivity(passwords);

                    errorView.setVisibility(View.INVISIBLE);
                } else {
                    errorView.setVisibility(View.VISIBLE);
                }

            } catch (Exception e) {
                errorView.setVisibility(View.VISIBLE);
            }
        });

        newAccountBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    newAccountBtn.setBackgroundResource(R.drawable.button_clicked);
                } else if(event.getAction() == MotionEvent.ACTION_UP){
                    newAccountBtn.setBackgroundResource(R.drawable.button);
                }

                return false;
            }
        });

        newAccountBtn.setOnClickListener(e -> {
            Intent register = new Intent(this, Register.class);
            startActivity(register);
        });

        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) { username.setHint("");}
                else { username.setHint("Username");}
            }
        });

        hidePass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) { hidePass.setHint("");}
                else { hidePass.setHint("Password");}
            }
        });

        showPass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) { showPass.setHint("");}
                else { showPass.setHint("Password");}
            }
        });

        show.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (show.isChecked()) {
                    hidePass.setVisibility(View.INVISIBLE);
                    showPass.setVisibility(View.VISIBLE);

                    showPass.setText(hidePass.getText());
                } else {
                    hidePass.setVisibility(View.VISIBLE);
                    showPass.setVisibility(View.INVISIBLE);

                    hidePass.setText(showPass.getText());
                }
            }

        });

    }

    public String decrypt(String encryptedMessage, String key) throws Exception {
        byte[] byteKey = key.getBytes();

        Key secretKey = new SecretKeySpec(byteKey, "AES");

        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.DECRYPT_MODE, secretKey);

        byte[] decryptedMessage = android.util.Base64.decode(encryptedMessage, 0);
        byte[] byteMessage = c.doFinal(decryptedMessage);

        String message = new String (byteMessage);
        return message;
    }

    public String toSHA256(String hashed) {
        return Hashing.sha256().hashString(hashed, Charset.defaultCharset()).toString();
    }

}