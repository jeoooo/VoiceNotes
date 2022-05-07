package com.example.voicenotes;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.media.Image;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Locale;

public class CreateNotes extends AppCompatActivity {

    //LLLL dd, yyyy KK:mm aaa DATE FORMAT
    SQLiteDatabase notesDB;
    Button button_save, button_deleteAll;
//    ToggleButton toggleButton_addReplace;
    ImageView imageView_mic;
    TextView textView_notes, textView_date;

    String dateTime;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_notes);

        button_save = findViewById(R.id.button_save);
        button_deleteAll = findViewById(R.id.button_deleteAll);

        imageView_mic = findViewById(R.id.imageView_mic);
        textView_notes = findViewById(R.id.textView_notes);
        textView_date = findViewById(R.id.textView_date);

        notesDB = openOrCreateDatabase("notesDB", 0, null);
//        notesDB.execSQL("CREATE TABLE IF NOT EXISTS notes (" +
//                "id integer primary key autoincrement," +
//                "notes varchar(2000)," +
//                "date_created varchar(255)," +
//                "date_modified varchar(255)" +
//                ")");
//        notesDB.execSQL("DROP TABLE IF EXISTS notes");
        Intent intent = getIntent();
        String state = intent.getStringExtra("modify_state");
        if (state.equalsIgnoreCase("Edit")){
            getSpeechInput();
            eraseCurrentNotes();
            update();
        }
        if(state.equalsIgnoreCase("add")){
            save();
            getSpeechInput();
            eraseCurrentNotes();
        }
        notesDB.close();



    }

    private void update() {
        Intent intent = getIntent();
        String modify_state = intent.getStringExtra("modify_state");
        String notes = intent.getStringExtra("notes");
        String date = "Date Modified: " + intent.getStringExtra("date");
        textView_notes.setText(notes);
        textView_date.setText(date);
        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notesDB = openOrCreateDatabase("notesDB", 0, null);
                ContentValues cv = new ContentValues();
                calendar = Calendar.getInstance();
                simpleDateFormat = new SimpleDateFormat("LLLL dd, yyyy KK:mm aaa");
                dateTime = simpleDateFormat.format(calendar.getTime());
                cv.put("notes", textView_notes.getText().toString());
                cv.put("date_modified", dateTime);
                String[] args = new String[]{notes};
                notesDB.update("notes", cv, "id = (SELECT id FROM notes WHERE notes = ? LIMIT 1)",args);
//                notesDB.execSQL("UPDATE notes SET notes = ?, date_modified = ? WHERE id = (SELECT id FROM CUSTOMERS WHERE notes = ?)", args);
                notesDB.close();
                Intent movetoMainActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(movetoMainActivity);
            }
        });
    }

    private void getSpeechInput() {
        ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    textView_notes.setText(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));
                }
            }
        });

        imageView_mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Start Speaking");

                launcher.launch(intent);
            }
        });
    }

    private void eraseCurrentNotes() {
        button_deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView_notes.setText("");
            }
        });
    }

    private void insertData(){
        notesDB = openOrCreateDatabase("notesDB", 0, null);
        ContentValues cv = new ContentValues();
        cv.put("notes", textView_notes.getText().toString());
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("LLLL dd, yyyy KK:mm aaa");
        dateTime = simpleDateFormat.format(calendar.getTime());
        cv.put("date_created", dateTime);
        cv.put("date_modified", dateTime);
        notesDB.insert("notes", null,cv);
        notesDB.close();
    }
    private void save(){
        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertData();
                Toast.makeText(CreateNotes.this, "Notes saved successfully!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });
    }


}