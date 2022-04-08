package com.example.task31c;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    EditText usernameEditText;
    int[] questionOrder = {1, 2, 3, 4, 5};

    public void jumpClick (View view)
    {
        //make sure user has entered a name
        EditText inputEditText = findViewById(R.id.editTextTextPersonName);
        String inputString = inputEditText.getText().toString().trim();
        if (inputString.trim().length() == 0 || inputString == "" || inputString == null)
        {
            Toast.makeText(MainActivity.this, R.string.no_input_error, Toast.LENGTH_SHORT).show();
            return;
        }

        //we have a name, so send it to next activity
        Intent intent = new Intent(this, SecondActivity.class);
        intent.putExtra("username", usernameEditText.getText().toString());
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usernameEditText = findViewById(R.id.editTextTextPersonName);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((resultCode == RESULT_OK)) {
            String name = data.getStringExtra("username");
            usernameEditText.setText(name);
        }
    }
}