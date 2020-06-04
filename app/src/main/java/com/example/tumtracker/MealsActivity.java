package com.example.tumtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

public class MealsActivity extends AppCompatActivity {

    public SQLite dbHelper;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meals);

        dbHelper = new SQLite(this);

        ArrayList<Meal> mealList = getMeals();
        for(int i=mealList.size()-1; i>=0; i--){
            addMeal(null, mealList.get(i));
        }

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

    public void addMeal(View view, final Meal meal){

        final LinearLayout mealActivity = findViewById(R.id.MealList);

        final LinearLayout mealList = new LinearLayout(this);
        mealList.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        mealList.setOrientation(LinearLayout.VERTICAL);

        final TextView mealTitle = new TextView(this);
        mealTitle.setText("Meal on " + meal.getDate());
        mealTitle.setTextSize(19);
        mealTitle.setTextColor(Color.BLACK);

        Button b = new Button(this);
        b.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        b.setText("X");
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mealActivity.removeView(mealList);
                delete(meal.getDate());
            }
        });

        mealList.addView(mealTitle);
        mealList.addView(b);

        final LinearLayout meals = new LinearLayout(this);
        meals.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        meals.setOrientation(LinearLayout.VERTICAL);

        mealList.addView(meals);

        for(String foodItem : meal.getFoodList()) {

            final LinearLayout parent = new LinearLayout(this);

            parent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            parent.setOrientation(LinearLayout.HORIZONTAL);

            //children of parent linearlayout

            final EditText food = new EditText(this);
            food.setText(foodItem);
            food.setEms(10);
            Button btn = new Button(this);
            btn.setText("X");
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    meals.removeView(parent);
                    meal.removeFromFoodList(food.getText().toString());
                }
            });

            parent.addView(food);
            parent.addView(btn);

            meals.addView(parent);
        }

        final LinearLayout painLayout = new LinearLayout(this);

        painLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        painLayout.setOrientation(LinearLayout.HORIZONTAL);

        Button addB = new Button(this);
        addB.setText("+");
        addB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final LinearLayout temp = new LinearLayout(context);

                temp.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                temp.setOrientation(LinearLayout.HORIZONTAL);

//children of parent linearlayout

                EditText food = new EditText(context);
                food.setHint("food group");
                food.setEms(10);
                Button btn = new Button(context);
                btn.setText("X");
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        meals.removeView(temp);
                    }
                });

                temp.addView(food);
                temp.addView(btn);

                meals.addView(temp);
            }
        });

        mealList.addView(addB);

        TextView pain = new TextView(this);
        pain.setText("Pain");
        pain.setTextColor(Color.BLACK);
        pain.setTextSize(19);

        Spinner spinner = new Spinner(this);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.painLevel, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setSelection(meal.getPain());

        painLayout.addView(pain);
        painLayout.addView(spinner);

        mealList.addView(painLayout);

        mealList.setPadding(80,80,80,80);

        mealActivity.addView(mealList);

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

}
