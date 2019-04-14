package lk.sliit.androidalarmsystem;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;
import java.util.TreeMap;

import lk.sliit.androidalarmsystem.domain.Question;

public class QuestionDatabaseHelper extends SQLiteOpenHelper {

    private final static String TAG = "APP-QuestionDbHelper";
    private final static String QUESTION_TABLE = "questions";
    private final static String ANSWER_TABLE = "answers";
    private final static String DB_NAME = "ctse_alarms.db";

    public QuestionDatabaseHelper(Context context) {
        super(context.getApplicationContext(), DB_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
        // Create Questions Table
        db.execSQL("CREATE TABLE IF NOT EXISTS " + QUESTION_TABLE +
                "(id number PRIMARY KEY NOT NULL," +
                "question text," +
                "answer number," +
                "FOREIGN KEY (answer) REFERENCES " + ANSWER_TABLE + "(id))");
        // Create Answers Table
        db.execSQL("CREATE TABLE IF NOT EXISTS " + ANSWER_TABLE +
                "(id number PRIMARY KEY NOT NULL," +
                "question number," +
                "answer text," +
                "FOREIGN KEY (question) REFERENCES " + QUESTION_TABLE + "(id))");
        Log.i(TAG, "Tables Created");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "onCreate");
    }

    public Question getQuestion(long questionId) {

        SQLiteDatabase db = this.getReadableDatabase();
        Question questionObj = new Question();
        questionObj.setId(questionId);

        // Get Question
        Cursor questionCursor = db.rawQuery("SELECT * " +
                "FROM " + QUESTION_TABLE +
                " WHERE id = " + questionId, null);
        questionCursor.moveToNext();
        int idIndex = questionCursor.getColumnIndex("id");
        int questionIndex = questionCursor.getColumnIndex("question");
        int answerIndex = questionCursor.getColumnIndex("answer");
        String question = questionCursor.getString(questionIndex);
        long answer = questionCursor.getLong(answerIndex);
        questionObj.setQuestion(question);
        questionObj.setAnswer(answer);

        // Get Choices
        Cursor choiceCursor = db.rawQuery("SELECT a.id, a.answer " +
                        "FROM " + QUESTION_TABLE + " q, " + ANSWER_TABLE + " a " +
                        " WHERE a.question = q.id AND q.id = " + questionId,
                null);
        choiceCursor.moveToNext();
        // Getting the indexes of the columns
        idIndex = choiceCursor.getColumnIndex("id");
        answerIndex = choiceCursor.getColumnIndex("answer");
        TreeMap<Long, String> choices = new TreeMap<>();
        while (!choiceCursor.isAfterLast()) {
            long id = choiceCursor.getLong(idIndex);
            String choice = choiceCursor.getString(answerIndex);
            choices.put(id, choice);
            choiceCursor.moveToNext();
        }
        questionObj.setChoices(choices);

        db.close();
        return questionObj;
    }

    public void fillDatabase() throws SQLiteConstraintException {
        SQLiteDatabase db = this.getWritableDatabase();

        // Inserting the Questions
        db.execSQL("INSERT INTO " + QUESTION_TABLE + " VALUES " +
                "(1, 'What is the force that binds the neutrons and protons in a nucleus?', 2)," +
                "(2, 'Which of the following is not a valid " +
                "conservation law of classical Physics?', 6)," +
                "(3, 'What is the range of Strong Nuclear force?', 11)," +
                "(4, 'What is the phenomenon Nuclear Reactors are based on?', 15)");

        // Inserting the Answers
        db.execSQL("INSERT INTO " + ANSWER_TABLE + " VALUES " +

                // Question 1
                "(1, 1, 'Weak Nuclear Force')," +
                "(2, 1, 'Strong Nuclear Force')," +
                "(3, 1, 'Electromagnetic Force')," +
                "(4, 1, 'Gravitational Force')," +
                // Question 2
                "(5, 2, 'Law of conservation of energy')," +
                "(6, 2, 'Law of conservation of current')," +
                "(7, 2, 'Law of conservation of angular momentum')," +
                "(8, 2, 'Law of conservation of charge')," +
                // Question 3
                "(9, 3, 'Infinite')," +
                "(10, 3, 'Very short Subnuclear size')," +
                "(11, 3, 'Very short Nuclear size')," +
                "(12, 3, 'None')," +
                // Question 4
                "(13, 4, 'Magnetic Confinement of Plasma')," +
                "(14, 4, 'Nuclear Fusion')," +
                "(15, 4, 'Controlled Nuclear Fission')," +
                "(16, 4, 'None of the above')");
        db.close();

        Log.i(TAG, "Data Inserted");
    }

    public int getQuestionsCount() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT count(*) " +
                "FROM " + QUESTION_TABLE, null);
        cursor.moveToNext();
        int count = cursor.getInt(0);

        db.close();
        return count;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

