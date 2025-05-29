package com.example.passreader;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
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
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.Charset;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Register extends AppCompatActivity {

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button registerBtn = findViewById(R.id.RegisterBtn);
        EditText username = findViewById(R.id.newUsername);
        EditText hidePass = findViewById(R.id.newPassword);
        EditText showPass = findViewById(R.id.newVisiblePassword);
        Switch show = findViewById(R.id.show);
        TextView warning = findViewById(R.id.warningText);
        TextView confirm = findViewById(R.id.confirmView);


        registerBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    registerBtn.setBackgroundResource(R.drawable.button_clicked);
                } else if(event.getAction() == MotionEvent.ACTION_UP){
                    registerBtn.setBackgroundResource(R.drawable.button);
                }

                return false;
            }
        });

        registerBtn.setOnClickListener(event -> {

            String password = show.isChecked() ? showPass.getText().toString() : hidePass.getText().toString();
            JSONObject jsonObject = new JSONObject();
            JSONObject jsonArray = new JSONObject();
            try {
                if (username.getText().toString().length() > 0  && password.length() > 0) {
                    jsonObject.put("username", toSHA256(username.getText().toString()));
                    jsonObject.put("password", toSHA256(password));
                    jsonObject.put("Pass", jsonArray);
                    File values = new File(getFilesDir() + "/values.json");
                    FileWriter writer = new FileWriter(values);
                    writer.write(encrypt(jsonObject.toString(), toSHA256(password).substring(0, 32)));
                    writer.close();
                    warning.setVisibility(View.INVISIBLE);
                    confirm.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) { }

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

    public String encrypt(String message, String key) {
        try {
            byte[] byteFile = message.getBytes();
            byte[] byteKey = key.getBytes();

            Key secretKey = new SecretKeySpec(byteKey, "AES");

            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] cipher = c.doFinal(byteFile);

            String encryptedMessage = android.util.Base64.encodeToString(cipher, 0);
            return encryptedMessage;
        } catch (Exception e) {
            return "error";
        }

    }

    public String toSHA256(String hashed) {
        return Hashing.sha256().hashString(hashed, Charset.defaultCharset()).toString();
    }

}