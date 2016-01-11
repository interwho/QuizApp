package ca.uwaterloo.quizapp;

import android.database.Cursor;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.app.*;
import android.view.*;
import android.os.Bundle;
import android.widget.*;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    CountDownTimer countdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
    }

    // Global Fragment Methods
    public void exit(View v) {
        countdown.cancel();
        setContentView(R.layout.login);
        ((TextView)findViewById(R.id.welcomeHeader)).setText(getApplicationContext().getString(R.string.welcome_header));
        ((EditText)findViewById(R.id.username)).setText("");
        ((EditText)findViewById(R.id.password)).setText("");
    }

    // Login Fragment Methods
    public void login(View v) {
        String adminUsername = getApplicationContext().getString(R.string.admin_username);
        String adminPassword = getApplicationContext().getString(R.string.admin_password);

        String username = ((EditText)findViewById(R.id.username)).getText().toString();
        String password = ((EditText)findViewById(R.id.password)).getText().toString();

        if (username.equals(adminUsername) && password.equals(adminPassword)) {
            setContentView(R.layout.quiz_builder);
            loadQuizBuilder();
        } else {
            DBHelper dbHelper = new DBHelper(this);
            Cursor rs = dbHelper.getUser(username);
            if(rs.moveToFirst()) {
                String realPass = rs.getString(rs.getColumnIndex(DBHelper.USERS_COLUMN_PASSWORD));
                if (password.equals(realPass)) {
                    setContentView(R.layout.quiz_taker);
                    loadQuizTaker(username);
                } else {
                    ((TextView)findViewById(R.id.welcomeHeader)).setText(getApplicationContext().getString(R.string.incorrect_login));
                }
            }
            if (!rs.isClosed()) {
                rs.close();
            }
        }
    }

    // Quiz Builder Methods
    public void loadQuizBuilder() {
        // Clear all pre-existing environment variables
        ((EditText)findViewById(R.id.addQuestionTitle)).setText("");
        ((EditText)findViewById(R.id.addQuestionCorrectAnswer)).setText("");
        ((EditText)findViewById(R.id.addQuestionIncorrectAnswer1)).setText("");
        ((EditText)findViewById(R.id.addQuestionIncorrectAnswer2)).setText("");
        ((EditText)findViewById(R.id.addQuestionIncorrectAnswer3)).setText("");
        ((EditText)findViewById(R.id.addQuestionTime)).setText("");
        ((EditText)findViewById(R.id.questionToDelete)).setText("");
        ((EditText)findViewById(R.id.createUserUsername)).setText("");
        ((EditText)findViewById(R.id.createUserPassword)).setText("");
        ((EditText)findViewById(R.id.usernameToDelete)).setText("");
        ((EditText)findViewById(R.id.usernameToClear)).setText("");

        // Load the data viewer
        DBHelper dbHelper = new DBHelper(this);

        // Load Users
        final Cursor userCursor = dbHelper.getAllUsers();

        String [] userColumns = new String[] {
                DBHelper.USERS_COLUMN_ID,
                DBHelper.USERS_COLUMN_USERNAME,
                DBHelper.USERS_COLUMN_PASSWORD
        };

        int [] userWidgets = new int[] {
                R.id.viewUserUserId,
                R.id.viewUserUsername,
                R.id.viewUserPassword
        };

        SimpleCursorAdapter userCursorAdapter = new SimpleCursorAdapter(this, R.layout.quiz_builder_users_view,
                userCursor, userColumns, userWidgets, 0);
        ListView userList = (ListView)findViewById(R.id.userListView);
        userList.setAdapter(userCursorAdapter);

        // Load Questions
        final Cursor questionCursor = dbHelper.getAllQuestions();

        String [] questionColumns = new String[] {
                DBHelper.QUESTIONS_COLUMN_ID,
                DBHelper.QUESTIONS_COLUMN_Q,
                DBHelper.QUESTIONS_COLUMN_A,
                DBHelper.QUESTIONS_COLUMN_I1,
                DBHelper.QUESTIONS_COLUMN_I2,
                DBHelper.QUESTIONS_COLUMN_I3,
                DBHelper.QUESTIONS_COLUMN_TIME
        };

        int [] questionWidgets = new int[] {
                R.id.viewQuestionId,
                R.id.viewQuestionQuestion,
                R.id.viewQuestionAnswer,
                R.id.viewQuestionI1,
                R.id.viewQuestionI2,
                R.id.viewQuestionI3,
                R.id.viewQuestionTime
        };

        SimpleCursorAdapter questionCursorAdapter = new SimpleCursorAdapter(this, R.layout.quiz_builder_questions_view,
                questionCursor, questionColumns, questionWidgets, 0);
        ListView questionList = (ListView)findViewById(R.id.questionListView);
        questionList.setAdapter(questionCursorAdapter);

        // Load Stats
        final Cursor statCursor = dbHelper.getAllStats();

        String [] statColumns = new String[] {
                DBHelper.STATS_COLUMN_ID,
                DBHelper.STATS_COLUMN_UID,
                DBHelper.STATS_COLUMN_QID,
                DBHelper.STATS_COLUMN_SCORE
        };

        int [] statWidgets = new int[] {
                R.id.viewStatsId,
                R.id.viewStatsUid,
                R.id.viewStatsQid,
                R.id.viewStatsScore
        };

        SimpleCursorAdapter statCursorAdapter = new SimpleCursorAdapter(this, R.layout.quiz_builder_stats_view,
                statCursor, statColumns, statWidgets, 0);
        ListView statList = (ListView)findViewById(R.id.statListView);
        statList.setAdapter(statCursorAdapter);
    }

    public void addUser(View v) {
        DBHelper dbHelper = new DBHelper(this);
        String username = ((EditText)findViewById(R.id.createUserUsername)).getText().toString();
        String password = ((EditText)findViewById(R.id.createUserPassword)).getText().toString();
        dbHelper.insertUser(username, password);
        loadQuizBuilder();
    }

    public void deleteUser(View v) {
        DBHelper dbHelper = new DBHelper(this);
        String username = ((EditText)findViewById(R.id.usernameToDelete)).getText().toString();
        dbHelper.deleteUser(username);
        loadQuizBuilder();
    }

    public void clearUserStats(View v) {
        DBHelper dbHelper = new DBHelper(this);
        String username = ((EditText)findViewById(R.id.usernameToClear)).getText().toString();
        dbHelper.deleteAllStatsByUser(username);
        loadQuizBuilder();
    }

    public void addQuestion(View v) {
        DBHelper dbHelper = new DBHelper(this);
        String question = ((EditText)findViewById(R.id.addQuestionTitle)).getText().toString();
        String answer = ((EditText)findViewById(R.id.addQuestionCorrectAnswer)).getText().toString();
        String i1 = ((EditText)findViewById(R.id.addQuestionIncorrectAnswer1)).getText().toString();
        String i2 = ((EditText)findViewById(R.id.addQuestionIncorrectAnswer2)).getText().toString();
        String i3 = ((EditText)findViewById(R.id.addQuestionIncorrectAnswer3)).getText().toString();
        String time = ((EditText)findViewById(R.id.addQuestionTime)).getText().toString();

        // Check minimum time
        if((Integer.parseInt(time) > 30) || (Integer.parseInt(time) < 10)) {
            loadQuizBuilder();
        } else {
            // Complies with our standards
            dbHelper.insertQuestion(question, answer, i1, i2, i3, time);
            loadQuizBuilder();
        }
    }

    public void deleteQuestion(View v) {
        DBHelper dbHelper = new DBHelper(this);
        String username = ((EditText)findViewById(R.id.questionToDelete)).getText().toString();
        dbHelper.deleteQuestion(username);
        loadQuizBuilder();
    }

    // Quiz Taker Methods
    public void loadQuizTaker(String vusername) {
        // Clear all quiz variables + colors in preparation for the next session
        ((RadioGroup)findViewById(R.id.answerRadioGroup)).clearCheck();
        ((TextView)findViewById(R.id.username)).setText(vusername);
        ((TextView)findViewById(R.id.userQuestion)).setText("");
        ((TextView)findViewById(R.id.userTime)).setText("");
        ((TextView)findViewById(R.id.userScore)).setText("");
        ((RadioButton)findViewById(R.id.answer1)).setText("");
        ((RadioButton)findViewById(R.id.answer2)).setText("");
        ((RadioButton)findViewById(R.id.answer3)).setText("");
        ((RadioButton)findViewById(R.id.answer4)).setText("");
        ((RadioButton)findViewById(R.id.answer1)).setTextColor(Color.WHITE);
        ((RadioButton)findViewById(R.id.answer2)).setTextColor(Color.WHITE);
        ((RadioButton)findViewById(R.id.answer3)).setTextColor(Color.WHITE);
        ((RadioButton)findViewById(R.id.answer4)).setTextColor(Color.WHITE);

        // Enable all buttons
        ((RadioButton)findViewById(R.id.answer1)).setEnabled(true);
        ((RadioButton)findViewById(R.id.answer2)).setEnabled(true);
        ((RadioButton)findViewById(R.id.answer3)).setEnabled(true);
        ((RadioButton)findViewById(R.id.answer4)).setEnabled(true);

        // Populate a new question that hasn't been shown before
        DBHelper dbHelper = new DBHelper(this);
        Cursor cursor = dbHelper.getRandomQuestion();
        cursor.moveToFirst();
        String question = cursor.getString(cursor.getColumnIndex(DBHelper.QUESTIONS_COLUMN_Q));
        String answer1 = cursor.getString(cursor.getColumnIndex(DBHelper.QUESTIONS_COLUMN_A));
        String answer2 = cursor.getString(cursor.getColumnIndex(DBHelper.QUESTIONS_COLUMN_I1));
        String answer3 = cursor.getString(cursor.getColumnIndex(DBHelper.QUESTIONS_COLUMN_I2));
        String answer4 = cursor.getString(cursor.getColumnIndex(DBHelper.QUESTIONS_COLUMN_I3));
        String time = cursor.getString(cursor.getColumnIndex(DBHelper.QUESTIONS_COLUMN_TIME));
        cursor.close();

        String[] solutionArray = { answer1, answer2, answer3, answer4 };
        shuffleArray(solutionArray);

        ((TextView)findViewById(R.id.userQuestion)).setText(question);
        ((TextView)findViewById(R.id.userTime)).setText(time);
        ((RadioButton)findViewById(R.id.answer1)).setText(solutionArray[0]);
        ((RadioButton)findViewById(R.id.answer2)).setText(solutionArray[1]);
        ((RadioButton)findViewById(R.id.answer3)).setText(solutionArray[2]);
        ((RadioButton)findViewById(R.id.answer4)).setText(solutionArray[3]);

        // Populate score
        String attempts = "0";
        String score = "0";
        String uid;

        String username = ((TextView)findViewById(R.id.username)).getText().toString();

        Cursor uCursor = dbHelper.getUser(username);
        uCursor.moveToFirst();
        uid = uCursor.getString(uCursor.getColumnIndex(DBHelper.USERS_COLUMN_ID));
        uCursor.close();

        Cursor cscursor = dbHelper.getCorrectStatsByUser(uid);
        score = Integer.toString(cscursor.getCount());
        cscursor.close();

        Cursor ascursor = dbHelper.getTotalStatsByUser(uid);
        attempts = Integer.toString(ascursor.getCount());
        ascursor.close();

        ((TextView)findViewById(R.id.userScore)).setText(score + "/" + attempts);

        // Start timer
        countdown = new CountDownTimer(Integer.parseInt(time) * 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                if (((RadioButton)findViewById(R.id.answer1)).isEnabled()) {
                    ((TextView) findViewById(R.id.userTime)).setText(Long.toString(millisUntilFinished / 1000));
                }
            }

            public void onFinish() {
                ((TextView)findViewById(R.id.userTime)).setText("0");
                if (((RadioButton)findViewById(R.id.answer1)).isEnabled()) {
                    answerQuestionIncorrect();
                }
            }
        };

        countdown.start();
    }

    static void shuffleArray(String[] ar)
    {
        // If running on Java 6 or older, use `new Random()` on RHS here
        Random rnd = new Random();
        for (int i = ar.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            String a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }

    public void loadQuizTakerFromView(View v) {
        loadQuizTaker(((TextView)findViewById(R.id.username)).getText().toString());
    }

    public void answerQuestion(View v) {
        // Disable all buttons
        ((RadioButton)findViewById(R.id.answer1)).setEnabled(false);
        ((RadioButton)findViewById(R.id.answer2)).setEnabled(false);
        ((RadioButton)findViewById(R.id.answer3)).setEnabled(false);
        ((RadioButton)findViewById(R.id.answer4)).setEnabled(false);

        countdown.cancel();

        // Is the button now checked?
        boolean checked = ((RadioButton) v).isChecked();
        String question = ((TextView)findViewById(R.id.userQuestion)).getText().toString();
        String corrAnswer;
        String answer = "incorrect***";
        String score = "0";
        String qid;
        String uid;
        String username;

        DBHelper dbHelper = new DBHelper(this);
        Cursor cursor = dbHelper.getAnswerByQuestion(question);
        cursor.moveToFirst();
        corrAnswer = cursor.getString(cursor.getColumnIndex(DBHelper.QUESTIONS_COLUMN_A));
        qid = cursor.getString(cursor.getColumnIndex(DBHelper.QUESTIONS_COLUMN_ID));
        username = ((TextView)findViewById(R.id.username)).getText().toString();
        cursor.close();

        Cursor uCursor = dbHelper.getUser(username);
        uCursor.moveToFirst();
        uid = uCursor.getString(uCursor.getColumnIndex(DBHelper.USERS_COLUMN_ID));
        uCursor.close();

        // Check which radio button was clicked
        switch(v.getId()) {
            case R.id.answer1:
                if (checked)
                    answer = ((RadioButton)findViewById(R.id.answer1)).getText().toString();
                    break;
            case R.id.answer2:
                if (checked)
                    answer = ((RadioButton)findViewById(R.id.answer2)).getText().toString();
                    break;
            case R.id.answer3:
                if (checked)
                    answer = ((RadioButton)findViewById(R.id.answer3)).getText().toString();
                    break;
            case R.id.answer4:
                if (checked)
                    answer = ((RadioButton)findViewById(R.id.answer4)).getText().toString();
                    break;
        }

        if(answer.equals(corrAnswer) && (!((TextView)findViewById(R.id.userTime)).getText().toString().equals("0"))) {
            score = "1";
        }

        dbHelper.insertStat(qid, uid, score);

        if (((RadioButton)findViewById(R.id.answer1)).getText().equals(corrAnswer)) {
            ((RadioButton)findViewById(R.id.answer1)).setTextColor(Color.GREEN);
            ((RadioButton)findViewById(R.id.answer2)).setTextColor(Color.RED);
            ((RadioButton)findViewById(R.id.answer3)).setTextColor(Color.RED);
            ((RadioButton)findViewById(R.id.answer4)).setTextColor(Color.RED);
        }

        if (((RadioButton)findViewById(R.id.answer2)).getText().equals(corrAnswer)) {
            ((RadioButton)findViewById(R.id.answer1)).setTextColor(Color.RED);
            ((RadioButton)findViewById(R.id.answer2)).setTextColor(Color.GREEN);
            ((RadioButton)findViewById(R.id.answer3)).setTextColor(Color.RED);
            ((RadioButton)findViewById(R.id.answer4)).setTextColor(Color.RED);
        }

        if (((RadioButton)findViewById(R.id.answer3)).getText().equals(corrAnswer)) {
            ((RadioButton)findViewById(R.id.answer1)).setTextColor(Color.RED);
            ((RadioButton)findViewById(R.id.answer2)).setTextColor(Color.RED);
            ((RadioButton)findViewById(R.id.answer3)).setTextColor(Color.GREEN);
            ((RadioButton)findViewById(R.id.answer4)).setTextColor(Color.RED);
        }

        if (((RadioButton)findViewById(R.id.answer4)).getText().equals(corrAnswer)) {
            ((RadioButton)findViewById(R.id.answer1)).setTextColor(Color.RED);
            ((RadioButton)findViewById(R.id.answer2)).setTextColor(Color.RED);
            ((RadioButton)findViewById(R.id.answer3)).setTextColor(Color.RED);
            ((RadioButton)findViewById(R.id.answer4)).setTextColor(Color.GREEN);
        }
    }

    public void answerQuestionIncorrect() {
        // Disable all buttons
        ((RadioButton)findViewById(R.id.answer1)).setEnabled(false);
        ((RadioButton)findViewById(R.id.answer2)).setEnabled(false);
        ((RadioButton)findViewById(R.id.answer3)).setEnabled(false);
        ((RadioButton)findViewById(R.id.answer4)).setEnabled(false);

        // Time ran out, so mark the question incorrect
        String question = ((TextView)findViewById(R.id.userQuestion)).getText().toString();
        String answer = "incorrect***";
        String score = "0";
        String qid;
        String uid;
        String corrAnswer;
        String username;

        DBHelper dbHelper = new DBHelper(this);
        Cursor cursor = dbHelper.getAnswerByQuestion(question);
        cursor.moveToFirst();
        corrAnswer = cursor.getString(cursor.getColumnIndex(DBHelper.QUESTIONS_COLUMN_A));
        qid = cursor.getString(cursor.getColumnIndex(DBHelper.QUESTIONS_COLUMN_ID));
        username = ((TextView)findViewById(R.id.username)).getText().toString();
        cursor.close();

        Cursor uCursor = dbHelper.getUser(username);
        uCursor.moveToFirst();
        uid = uCursor.getString(uCursor.getColumnIndex(DBHelper.USERS_COLUMN_ID));
        uCursor.close();

        dbHelper.insertStat(qid, uid, score);

        if (((RadioButton)findViewById(R.id.answer1)).getText().equals(corrAnswer)) {
            ((RadioButton)findViewById(R.id.answer1)).setTextColor(Color.GREEN);
            ((RadioButton)findViewById(R.id.answer2)).setTextColor(Color.RED);
            ((RadioButton)findViewById(R.id.answer3)).setTextColor(Color.RED);
            ((RadioButton)findViewById(R.id.answer4)).setTextColor(Color.RED);
        }

        if (((RadioButton)findViewById(R.id.answer2)).getText().equals(corrAnswer)) {
            ((RadioButton)findViewById(R.id.answer1)).setTextColor(Color.RED);
            ((RadioButton)findViewById(R.id.answer2)).setTextColor(Color.GREEN);
            ((RadioButton)findViewById(R.id.answer3)).setTextColor(Color.RED);
            ((RadioButton)findViewById(R.id.answer4)).setTextColor(Color.RED);
        }

        if (((RadioButton)findViewById(R.id.answer3)).getText().equals(corrAnswer)) {
            ((RadioButton)findViewById(R.id.answer1)).setTextColor(Color.RED);
            ((RadioButton)findViewById(R.id.answer2)).setTextColor(Color.RED);
            ((RadioButton)findViewById(R.id.answer3)).setTextColor(Color.GREEN);
            ((RadioButton)findViewById(R.id.answer4)).setTextColor(Color.RED);
        }

        if (((RadioButton)findViewById(R.id.answer4)).getText().equals(corrAnswer)) {
            ((RadioButton)findViewById(R.id.answer1)).setTextColor(Color.RED);
            ((RadioButton)findViewById(R.id.answer2)).setTextColor(Color.RED);
            ((RadioButton)findViewById(R.id.answer3)).setTextColor(Color.RED);
            ((RadioButton)findViewById(R.id.answer4)).setTextColor(Color.GREEN);
        }
    }
}
