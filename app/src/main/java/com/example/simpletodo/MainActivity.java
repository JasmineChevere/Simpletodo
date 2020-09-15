 package com.example.simpletodo;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;


import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final int EDIT_TEXT_CODE = 20;
    List<String> items;

    Button btnadd;
    EditText editem;
    RecyclerView rvitems;
    ItemsAdapter itemsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnadd = findViewById(R.id.btnadd);
        editem = findViewById(R.id.editem);
        rvitems = findViewById(R.id.rvitems);



        loadItems();


        ItemsAdapter.OnLongClickListener onLongClickListener = new ItemsAdapter.OnLongClickListener() {
            @Override
            public void onItemLongClicked(int position) {
                // delete the item from the item
                items.remove(position);
                // notify at which position we deleted
                itemsAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(), "Item was removed", Toast.LENGTH_SHORT).show();
                saveItems();

            }
        };

        ItemsAdapter.OnClickListener onClickListener = new ItemsAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
                Log.d("Main Activity", "Single click at position" + position);
                //create a new activity
                Intent i = new Intent(MainActivity.this, EditActivity.class);
                // pass the data being edited
                i.putExtra(KEY_ITEM_TEXT, items.get(position));
                i.putExtra(KEY_ITEM_POSITION, position);
                // display the activity
                startActivityForResult(i, EDIT_TEXT_CODE);
            }
        };

       itemsAdapter =  new ItemsAdapter(items, onLongClickListener, onClickListener);
       rvitems.setAdapter(itemsAdapter);
       rvitems.setLayoutManager(new LinearLayoutManager(this));

       btnadd.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               String todoItem = editem.getText().toString();
               //aDD ITEM TO MODE;
               items.add(todoItem);
               //nOTIFY ADAPTER THAT AN ITEM HAS BEEN ADDED
               itemsAdapter.notifyItemInserted(items.size() - 1);
               editem.setText("");
               Toast.makeText(getApplicationContext(), "Item was added", Toast.LENGTH_SHORT).show();
               saveItems();
           }
       });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        if (resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE){
            // Retrieve updated value
            String itemText = data.getStringExtra(KEY_ITEM_TEXT);
            // extract the original position of the edited item
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);
            // UPDATE THE MODEL
            items.set(position, itemText);
            //NOTIFY THE ADAPTER
            itemsAdapter.notifyItemChanged(position);
            //PERSIST CHANGES
            saveItems();
            Toast.makeText(getApplicationContext(), "Item updated succesfully", Toast.LENGTH_SHORT).show();
        } else {
            Log.w("MainActivity", "Unknown Call to onActivityResults");
        }
    }
    private File getDataFile() {
        return new File(getFilesDir(), "data.txt");
    }

    private void loadItems(){
        try{
            items = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e){
            Log.e("MainActivity", "ERROR READING ITEMS", e);
            items = new ArrayList<>();
        }
    }
    private void saveItems(){
        try{
            FileUtils.writeLines(getDataFile(), items);
        } catch (IOException e) {
            Log.e("MainActivity", "ERROR READING ITEMS", e);
        }
    }

}