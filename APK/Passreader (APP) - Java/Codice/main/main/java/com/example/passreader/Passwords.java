package com.example.passreader;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.FileReader;
import java.io.FileWriter;
import java.security.Key;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Passwords extends AppCompatActivity {

    static int itemPosition = -1;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passwords);

        Button addBtn = findViewById(R.id.addBtn);
        Button remBtn = findViewById(R.id.remBtn);
        EditText keyField = findViewById(R.id.setKey);
        EditText passField = findViewById(R.id.setPass);
        ListView list = findViewById(R.id.List);


        ArrayList<Values> arrayList = new ArrayList<>();

        try {
            FileReader reader = new FileReader(getFilesDir() + "/values.json");

            int i;
            StringBuilder jsonValue = new StringBuilder();
            while((i = reader.read()) != -1){ jsonValue.append((char)i); }
            reader.close();

            JSONObject json = new JSONObject(jsonValue.toString());
            JSONObject passwords = (JSONObject) json.get("Pass");

            Iterator iterator = passwords.keys();
            String actualKey;

            while (iterator.hasNext()){
                actualKey = iterator.next().toString();
                arrayList.add(new Values(actualKey, passwords.get(actualKey).toString()));
            }

        } catch (Exception e) { }


        final CustomAdapter[] adapter = {new CustomAdapter(this, R.layout.activity_custom_item, arrayList)};
        list.setAdapter(adapter[0]);

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String copied = arrayList.get(position).getValue2();

                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData data = ClipData.newPlainText("copied", copied);
                clipboard.setPrimaryClip(data);

                Toast.makeText(Passwords.this, "Password copied", Toast.LENGTH_SHORT).show();

                return false;
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            View oldView;

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (oldView != null)
                    oldView.setBackgroundResource(R.color.grey);
                view.setBackgroundResource(R.color.SelectedColor);
                oldView = view;

                itemPosition = position;

            }
        });

        keyField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) { keyField.setHint("");}
                else { keyField.setHint("Key");}
            }
        });

        passField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) { passField.setHint("");}
                else { passField.setHint("Password");}
            }
        });

        addBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    addBtn.setBackgroundResource(R.drawable.button_clicked);
                } else if(event.getAction() == MotionEvent.ACTION_UP){
                    addBtn.setBackgroundResource(R.drawable.button);
                }

                return false;
            }
        });

        addBtn.setOnClickListener(event -> {
            if (keyField.getText().toString().length() > 0 && passField.getText().toString().length() > 0) {

                for (int i = 0; i < arrayList.size(); i++){
                    if (keyField.getText().toString().equals(arrayList.get(i).getValue1())){
                        Toast.makeText(Passwords.this, "A password with this key already exists", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                try {
                    FileReader reader = new FileReader(getFilesDir() + "/values.json");

                    int i;
                    StringBuilder jsonValue = new StringBuilder();
                    while((i = reader.read()) != -1){ jsonValue.append((char)i); }
                    reader.close();

                    JSONObject json = new JSONObject(jsonValue.toString());
                    JSONObject passwords = (JSONObject) json.get("Pass");

                    adapter[0].add(new Values(keyField.getText().toString(), passField.getText().toString()));

                    passwords.put(keyField.getText().toString(), passField.getText().toString());


                    FileWriter writer = new FileWriter(getFilesDir() + "/values.json");
                    writer.write(json.toString());
                    writer.close();

                } catch (Exception e) { }
            }
        });

        remBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    remBtn.setBackgroundResource(R.drawable.button_clicked);
                } else if(event.getAction() == MotionEvent.ACTION_UP){
                    remBtn.setBackgroundResource(R.drawable.button);
                }

                return false;
            }
        });

        remBtn.setOnClickListener(event -> {

            try {
                FileReader reader = new FileReader(getFilesDir() + "/values.json");

                int i;
                StringBuilder jsonValue = new StringBuilder();
                while((i = reader.read()) != -1){ jsonValue.append((char)i); }
                reader.close();

                JSONObject json = new JSONObject(jsonValue.toString());
                JSONObject passwords = (JSONObject) json.get("Pass");

                passwords.remove(arrayList.get(itemPosition).getValue1());

                FileWriter writer = new FileWriter(getFilesDir() + "/values.json");
                writer.write(json.toString());
                writer.close();

                Values value = arrayList.get(itemPosition);
                arrayList.remove(value);
                adapter[0] = new CustomAdapter(getApplicationContext(), R.layout.activity_custom_item, arrayList);
                list.setAdapter(adapter[0]);
                itemPosition = -1;
            } catch (Exception e) {
                Toast.makeText(Passwords.this, e.toString(), Toast.LENGTH_LONG).show();
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


    @Override
    protected void onPause() {
        super.onPause();

        try {
            FileReader reader = new FileReader(getFilesDir() + "/values.json");

            int i;
            StringBuilder jsonValue = new StringBuilder();
            while ((i = reader.read()) != -1) {
                jsonValue.append((char) i);
            }
            reader.close();

            JSONObject json = new JSONObject(jsonValue.toString());

            FileWriter writer = new FileWriter(getFilesDir() + "/values.json");
            writer.write(encrypt(jsonValue.toString(), json.get("password").toString().substring(0, 32)));
            writer.close();

            finish();

        } catch (Exception e) { }


    }
}