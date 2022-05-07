package com.example.voicenotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesHolder> {
    //TODO: UPDATE BUTTONS
    Context context;
    ArrayList<String> notes;
    ArrayList<String> date;
    SQLiteDatabase notesDB;

    public NotesAdapter(Context context, ArrayList<String> notes, ArrayList<String> date) {
        this.context = context;
        this.notes = notes;
        this.date = date;
    }

    @NonNull
    @Override
    public NotesAdapter.NotesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.activity_notes_adapter, parent, false);
        return new NotesHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesAdapter.NotesHolder holder, int position) {
        holder.textView_notesHolder.setText(notes.get(position));
        holder.textView_notesdate.setText(date.get(position));

        holder.button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notesDB = context.openOrCreateDatabase("notesDB", 0, null);
                String[] args = new String[]{notes.get(position)};
                notesDB.delete("notes", "ID = (SELECT id FROM notes WHERE notes = ? LIMIT 1)", args);
                notesDB.close();
                notes.remove(position);
                date.remove(position);
                notifyDataSetChanged();
                Toast.makeText(context, "Note Deleted Sucessfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public class NotesHolder extends RecyclerView.ViewHolder {
        TextView textView_notesHolder, textView_notesdate;
        Button button_edit, button_delete;
        private NotesAdapter adapter;

        public NotesHolder(@NonNull View itemView) {
            super(itemView);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            textView_notesHolder = itemView.findViewById(R.id.textView_notesHolder);
            textView_notesdate = itemView.findViewById(R.id.textView_notesdate);

            button_edit = itemView.findViewById(R.id.button_edit);
            button_delete = itemView.findViewById(R.id.button_delete);

            updateNotes();
//            deleteNotes();


        }

        private void deleteNotes() {
            button_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapter.notes.remove(getAdapterPosition());
                    adapter.date.remove(getAdapterPosition());
                    adapter.notifyDataSetChanged();
                    notesDB = context.openOrCreateDatabase("notesDB", 0, null);
                    String[] args = new String[]{notes.get(getAdapterPosition())};
                    notesDB.delete("notes", "ID = (SELECT id FROM notes WHERE notes = ? LIMIT 1)", args);
                    notesDB.close();


                }
            });
        }

        private void updateNotes() {
            button_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), CreateNotes.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("notes", textView_notesHolder.getText().toString());
                    intent.putExtra("date", textView_notesdate.getText().toString());
                    intent.putExtra("modify_state", button_edit.getText().toString());
                    context.startActivity(intent);
                }
            });

        }
    }
}