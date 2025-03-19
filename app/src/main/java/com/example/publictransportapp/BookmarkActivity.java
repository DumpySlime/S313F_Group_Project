package com.example.publictransportapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class BookmarkActivity extends AppCompatActivity {
    private ListView bookmarkListView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> bookmarks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        // Initialize UI components
        bookmarkListView = findViewById(R.id.bookmarkListView);
        Button addBookmarkButton = findViewById(R.id.addBookmarkButton);

        // Initialize the bookmarks list and adapter
        bookmarks = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, bookmarks);
        bookmarkListView.setAdapter(adapter);

        // Add bookmark button functionality
        addBookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewBookmark();
            }
        });

        // Handle item click (view details)
        bookmarkListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedBookmark = bookmarks.get(position);
                viewBookmarkDetails(selectedBookmark);
            }
        });

        // Handle item long click (delete)
        bookmarkListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                confirmDeleteBookmark(position);
                return true;
            }
        });
    }

    // Add a new bookmark
    private void addNewBookmark() {
        bookmarks.add("Route " + (bookmarks.size() + 1) + ": Example Stop");
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "Bookmark added!", Toast.LENGTH_SHORT).show();
    }

    // View bookmark details
    private void viewBookmarkDetails(String bookmark) {
        // Example action: Show a toast or navigate to another screen
        Toast.makeText(this, "Viewing details for: " + bookmark, Toast.LENGTH_SHORT).show();
        // Intent intent = new Intent(this, RealTimeTrackingActivity.class);
        // intent.putExtra("bookmark", bookmark);
        // startActivity(intent);
    }

    // Confirm delete
    private void confirmDeleteBookmark(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Bookmark")
                .setMessage("Are you sure you want to delete this bookmark?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        bookmarks.remove(position);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(BookmarkActivity.this, "Bookmark deleted!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}