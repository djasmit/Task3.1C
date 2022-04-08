package com.example.task31c;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.widget.Button;
import android.view.Window;
import java.util.Random;

public class SecondActivity extends AppCompatActivity {
    //important global constants
    int score;
    int currentIndex;
    int questNo;
    String username;
    int selAns;
    boolean answered, submitted;
    Question currentQuestion;
    Question[] questions;
    int totalQuests;

    int[] qOrder;
    int[] questIndices;

    //response for each of the three answer buttons being clicked
    public void ans1Click(View view) {
        if (!submitted) { ansClick(0); }
    }
    public void ans2Click(View view) {
        if (!submitted) { ansClick(1); }
    }
    public void ans3Click(View view) {
        if (!submitted) { ansClick(2); }
    }

    //response for submit/next button being clicked
    public void submitClick(View view)
    {
        Button submitButton = findViewById(R.id.submitButton);

        //question hasn't been answered yet,
        if (!(answered))
        { return; }

        if (!submitted) {
            //determine if the selected answer was correct
            examineGuess();

            //submission is now locked in, so we can change button to next
            submitButton.setText("Next");

            //set submission to true and update progress display
            submitted = true;
            displayWelcome();
        }

        else {
            submitButton.setEnabled(false);
            jumpClick();
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //disable title
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        setContentView(R.layout.activity_second);

        createQuestions();
        getIntentExtras();
        shuffleQuestions();
        getQuestion();
        displayWelcome();
        displayProgress();
        displayQuestion();
        displayAnswers();
        displaySubmission();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((resultCode == RESULT_OK) && (requestCode == 1)) {
            Intent newIntent = new Intent();
            newIntent.putExtra("username", username);
            setResult(RESULT_OK, newIntent);
            finish();
        }
    }

    //======================================== PRIVATE METHODS =============================================================
    //takes the user's guessed answer and determines if it's correct or not
    private void examineGuess() {
        int correct = 0;
        int scorePlus = 1; //assume we got question correct until told otherwise

        //get an array of answer buttons
        Button[] buttons = getButtons();

        //disables all buttons and checks which one has the correct answer
        for (int i = 0; i < buttons.length; i++) {
            Button currentButton = buttons[i];
            if (currentButton.getText().toString() == currentQuestion.getAnswerTrue())
            { correct = i; }

            //disable current button
            currentButton.setEnabled(false);
        }

        //tick correct answer
        buttons[correct].setBackgroundColor(getResources().getColor(R.color.correct));

        //check if guess was the correct button, if not then set the guess to red and remove bonus point
        if (selAns != correct) {
            buttons[selAns].setBackgroundColor(getResources().getColor(R.color.incorrect));
            scorePlus = 0;
        }

        //add bonus point
        score += scorePlus;
    }

    //captures the selected answer button
    private void ansClick(int index) {
        Button[] buttons = getButtons();

        //cycle through array of buttons, enabling all those that haven't been selected
        for (int i = 0; i < buttons.length; i++) {
            if (i != index) {
                buttons[i].setEnabled(true);
            }
        }

        //disable the selected button, and record its index
        buttons[index].setEnabled(false);
        selAns = index;
        answered = true;

        Button submit = findViewById(R.id.submitButton);
        submit.setEnabled(true);
    }

    //returns an array of all the buttons
    private Button[] getButtons() {
        Button A1 = findViewById(R.id.a1_button);
        Button A2 = findViewById(R.id.a2_button);
        Button A3 = findViewById(R.id.a3_button);
        Button[] buttons = {A1, A2, A3};
        return buttons;
    }

    //jumps to next activity
    private void jumpClick() {
        Class Class;

        //create new question activity if we have any left
        if (questNo < totalQuests) {
            questNo++;
            Class = SecondActivity.class;
        }

        //no questions left, so go to next activity
        else { Class = ThirdActivity.class; }

        Intent newIntent = new Intent(this, Class);
        newIntent.putExtra("questionNumber", questNo);
        newIntent.putExtra("username", username);
        newIntent.putExtra("questionOrder", questIndices);
        newIntent.putExtra("score", score);
        newIntent.putExtra("totalQuests", totalQuests);
        startActivityForResult(newIntent, 1);
    }

    //creates the original list of questions and default ordering
    private void createQuestions() {
        //create questions
        Question q1 = new Question("What is the following concept called?", "Defines the structure for a user interface in your app, such as an activity. All elements in it are built using a hierarchy of View and ViewGroup objects.", "Layout", "ViewStructure", "Widget");
        Question q2 = new Question("What OS kernel is used by Android?", "The OS Kernel handles core android architecture like memory and process management, network stack, security and driver model.", "Linux Kernel", "Android Kernel", "Solaris Kernel");
        Question q3 = new Question("What is the first activity in an application typically called?", "This activity isn't necessarily called this, but in convention it is referred to by this name.", "MainActivity", "HomeActivity", "StartActivity");
        Question q4 = new Question("Which of the following states isn't part of the activity lifecycle?", "There are 4 possible lifecycle states. Only two of these are on that list.", "Background", "Partially hidden", "Destroyed");
        Question q5 = new Question("What is the following concept called?", "A message which allows Android components to request functionality from other components of the Android system.", "Intent", "Request", "Command");

        //fill array, get length, and get order
        questions = new Question[]{q1, q2, q3, q4, q5};
        totalQuests = questions.length;
        qOrder = new int[]{0, 1, 2, 3, 4};
    }

    //gets the intent and extra details from previous activities
    private void getIntentExtras() {
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        questNo = intent.getIntExtra("questionNumber", 1);
        questIndices = intent.getIntArrayExtra("questionOrder");
        score = intent.getIntExtra("score", 0);
    }

    //shuffles the current questions
    private void shuffleQuestions() {
        //if we haven't received our array of indices from previous activity, generate it
        if (questIndices == null || questIndices.length == 0) {
            shuffleArray(qOrder);
            questIndices = qOrder;
        }
    }

    //if import is null then we take the default order and shuffle it
    private void shuffleArray(int[] in) {
        Random rand = new Random();
        for (int i = 0; i < in.length; i++) {
            //select random element in our array
            int randomIndexToSwap = rand.nextInt(in.length);

            //swap it with current element
            int temp = in[randomIndexToSwap];
            in[randomIndexToSwap] = in[i];
            in[i] = temp;
        }
    }

    //gets the order and current question
    private void getQuestion() {
        //we have the order, so select next question in order
        currentIndex = questIndices[questNo - 1];
        currentQuestion = questions[currentIndex];

        currentQuestion.shuffleAnswers();
    }

    //displays welcome message for first question, but not for others
    private void displayWelcome() {
        TextView displayName = findViewById(R.id.welcomeText);
        if (questNo == 1 && !submitted)
        { displayName.setText("Welcome, " + username + "!" ); }
        else
        {
            displayName.setText(null);
            displayName.setMaxHeight(0);
            displayName.setMinHeight(0);
        }
    }

    //displays progress bar and question count
    private void displayProgress() {
        TextView questNos = findViewById(R.id.questNo);
        ProgressBar progress = findViewById(R.id.progressBar);
        questNos.setText(questNo + "/" + totalQuests);
        progress.setProgress((100/totalQuests)*questNo);
    }

    //displays current question
    private void displayQuestion() {
        //question and answers
        TextView question = findViewById(R.id.QuestText);
        TextView description = findViewById(R.id.DescText);

        //set question, description, and answer text to that for current question
        question.setText(currentQuestion.getQuestion());
        description.setText(currentQuestion.getDescription());
    }

    //displays the answers to the current question
    private void displayAnswers() {
        Button answer1 = findViewById(R.id.a1_button);
        Button answer2 = findViewById(R.id.a2_button);
        Button answer3 = findViewById(R.id.a3_button);

        answer1.setText(currentQuestion.getAnswers()[0]);
        answer2.setText(currentQuestion.getAnswers()[1]);
        answer3.setText(currentQuestion.getAnswers()[2]);
    }

    //displays submission button
    private void displaySubmission() {

        Button submitButton = findViewById(R.id.submitButton);
        submitButton.setText("Submit");

        submitButton.setEnabled(false);
    }

    //======================================== NESTED CLASSES =============================================================
    // Quesion class contains data for questinos
    private class Question {
        private String _question;
        private String _description;
        private String _answerTrue;
        private String _answerFalse1;
        private String _answerFalse2;

        private String[] _answers;

        public String getQuestion()
        { return _question; }

        public String getDescription()
        { return _description; }

        public String getAnswerTrue()
        { return _answerTrue; }

        public String getAnswerFalse1()
        { return _answerFalse1; }

        public String getAnswerFalse2()
        { return _answerFalse2; }

        public String[] getAnswers()
        { return _answers; }

        //randomly shuffle list of answers
        public void shuffleAnswers()
        {
            Random rand = new Random();
            for (int i = 0; i < _answers.length; i++) {
                //select random element in our array
                int randomIndexToSwap = rand.nextInt(_answers.length);

                //swap it with current element
                String temp = _answers[randomIndexToSwap];
                _answers[randomIndexToSwap] = _answers[i];
                _answers[i] = temp;
            }
        }

        public Question(String question, String description, String answerTrue, String answerFalse1, String answerFalse2) {
            _question = question;
            _description = description;
            _answerTrue = answerTrue;
            _answerFalse1 = answerFalse1;
            _answerFalse2 = answerFalse2;

            _answers = new String []{ _answerTrue, _answerFalse1, _answerFalse2 };
        }
    }
}