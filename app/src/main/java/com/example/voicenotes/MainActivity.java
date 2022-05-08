package com.example.voicenotes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ImageView imageView_logoSeethrough, imageView_info;
    TextView textView_noNotes;
    RecyclerView recyclerView_notesView;
    SQLiteDatabase notesDB;
    ArrayList<String> notes = new ArrayList<>();
    ArrayList<String> date = new ArrayList<>();
    FloatingActionButton floatingActionButton_addnotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        notesDB = openOrCreateDatabase("notesDB", 0, null);
        notesDB.execSQL("CREATE TABLE IF NOT EXISTS notes (" +
                "id integer primary key autoincrement," +
                "notes varchar(2000)," +
                "date_created varchar(255)," +
                "date_modified varchar(255)" +
                ")");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView_info = findViewById(R.id.imageView_info);
        textView_noNotes = findViewById(R.id.textView_noNotes);
        imageView_logoSeethrough = findViewById(R.id.imageView_logoSeethrough);
        floatingActionButton_addnotes = findViewById(R.id.floatingActionButton_addnotes);

        gotoInfoScreen();

        addNotes();

        recyclerView_notesView = findViewById(R.id.recyclerView_notesView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView_notesView.setLayoutManager(linearLayoutManager);
        recyclerView_notesView.setAdapter(new NotesAdapter(getApplicationContext(), notes, date));
        selectData();

        showNoNotes();
        if (recyclerView_notesView.getAdapter().getItemCount() > 0) {
            recyclerView_notesView.setVisibility(View.VISIBLE);
            imageView_logoSeethrough.setVisibility(View.INVISIBLE);
            textView_noNotes.setVisibility(View.INVISIBLE);
        }


//        if(recyclerView_notesView.getAdapter().getItemCount() != 0){
//            recyclerView_notesView.setVisibility(View.VISIBLE);
//            imageView_logoSeethrough.setVisibility(View.INVISIBLE);
//            textView_noNotes.setVisibility(View.INVISIBLE);
//        }


    }

    private void gotoInfoScreen() {
        imageView_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), InfoScreen.class);
                startActivity(intent);
            }
        });

    }

    private void showNoNotes() {
        recyclerView_notesView.setVisibility(View.INVISIBLE);
        imageView_logoSeethrough.setVisibility(View.VISIBLE);
        textView_noNotes.setVisibility(View.VISIBLE);
    }

    void selectData() {
        notesDB = openOrCreateDatabase("notesDB", 0, null);
        Cursor cursor = notesDB.query("notes", null, null, null, null, null, "id");
        while (cursor.moveToNext()) {
            notes.add(cursor.getString(1));
            date.add(cursor.getString(3));
        }
        cursor.close();
        notesDB.close();
    }

    private void addNotes() {
        floatingActionButton_addnotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String state = "add";
                Intent intent = new Intent(getApplicationContext(), CreateNotes.class);
                intent.putExtra("modify_state", state);
                startActivity(intent);
            }
        });
    }
}