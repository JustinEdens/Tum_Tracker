package com.example.tumtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.tumtracker.FeedReaderContract.FeedEntry.TABLE_NAME;

public class MainActivity extends AppCompatActivity {

    public SQLite dbHelper;
    public ArrayList<EditText> currentFoodList = new ArrayList<>();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.Meals:
                Intent myIntent = new Intent(this, MealsActivity.class);
                startActivity(myIntent);
                return true;
            case R.id.Statistics:
                //showHelp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //start of sqlite

        dbHelper = new SQLite(this);

        //end of sqlite

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.painLevel, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        addBtn(null);

    }

    public void delete(String item){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // Define 'where' part of query.
        String selection = FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE + " LIKE ?";
// Specify arguments in placeholder order.
        String[] selectionArgs = { item };
// Issue SQL statement.
        int deletedRows = db.delete(FeedReaderContract.FeedEntry.TABLE_NAME, selection, selectionArgs);
    }

    public ArrayList<Meal> getMeals(){

        ArrayList<Meal> arrayList=new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from Meals", null );
        res.moveToFirst();

        if (res != null)
        {
            while(res.isAfterLast() == false){
                String date = (res.getString(res.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE)));
                String foodlist = (res.getString(res.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_FOODS)));
                ArrayList<String> foodList = new ArrayList<>();
                foodList.add(foodlist);
                String pain = (res.getString(res.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_PAIN)));

                Meal temp = new Meal(foodList,Integer.parseInt(pain),date);
                arrayList.add(temp);

                res.moveToNext();
            }}
        return arrayList;
    }

    public String search(String item){
        SQLiteDatabase db = dbHelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                BaseColumns._ID,
                FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE};

// Filter results WHERE "title" = 'item'
        String selection = FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE + " = ?";
        String[] selectionArgs = { item };

// How you want the results sorted in the resulting Cursor
        String sortOrder = " DESC";

        Cursor cursor = db.query(
                TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        List itemIds = new ArrayList<>();
        ArrayList<String> itemNames = new ArrayList<>();
        while(cursor.moveToNext()) {
            long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry._ID));
            itemNames.add(cursor.getString(0));
            itemIds.add(itemId);
        }
        cursor.close();

        if(itemIds.size() == 0){
            return "Not Found";
        }
        return projection[1];
    }

    public long insert(Meal meal){
        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();

// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE, meal.getDate());
        values.put(FeedReaderContract.FeedEntry.COLUMN_FOODS, meal.getFoodList().toString());
        values.put(FeedReaderContract.FeedEntry.COLUMN_PAIN, Integer.toString(meal.getPain()));

// Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(TABLE_NAME, null, values);

        return newRowId;
    }

    public void submitBtn(View view){

        ArrayList<String> foodList = new ArrayList<>();
        for(EditText editText : currentFoodList){
            String food = editText.getText().toString();
            foodList.add(food);
        }

        Spinner painSpinner = findViewById(R.id.spinner);
        int pain = Integer.parseInt(painSpinner.getSelectedItem().toString());

        Date today = new Date();

        Meal temp = new Meal(foodList, pain, today.toString());
        insert(temp);

        final LinearLayout foodsLayout = findViewById(R.id.foodsLayout);
        foodsLayout.removeAllViews();
        addBtn(null);

    }

    public void addBtn(View view){

        final LinearLayout foodsLayout = findViewById(R.id.foodsLayout);

        final LinearLayout parent = new LinearLayout(this);

        parent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        parent.setOrientation(LinearLayout.HORIZONTAL);

//children of parent linearlayout

        final EditText food = new EditText(this);
        currentFoodList.add(food);
        food.setHint("food group");
        food.setEms(10);
        Button btn = new Button(this);
        btn.setText("X");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foodsLayout.removeView(parent);
                currentFoodList.remove(food);
            }
        });

        parent.addView(food);
        parent.addView(btn);

        foodsLayout.addView(parent);

    }

}
