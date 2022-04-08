package com.example.task31c;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.content.Intent;

import javax.xml.transform.Result;

public class ThirdActivity extends AppCompatActivity {

    String username;

    public void backClick(View view)
    {
        Intent newIntent = new Intent();
        newIntent.putExtra("username", username);
        setResult(RESULT_OK, newIntent);
        finish();
    }

    //finishes all activities with the same affinity, by default all activities in the application have same affinity,
    //so effectively this closes all activities
    public void quitClick(View view)
    { finishAffinity(); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //disable title
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        setContentView(R.layout.activity_third);

        //receive the score, total number of questions answered and user name
        Intent intent = getIntent();
        int score = intent.getIntExtra("score", 0);
        int totalQuests = intent.getIntExtra("totalQuests", 0);
        username = intent.getStringExtra("username");

        //display congratulations text
        TextView congratulations = findViewById(R.id.congratsText);
        congratulations.setText("Congratulations, " + username + "!");

        //display score
        TextView scoreText = findViewById(R.id.resultText);
        scoreText.setText(score + "/" + totalQuests);
    }
}