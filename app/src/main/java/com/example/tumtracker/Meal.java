package com.example.tumtracker;

import java.util.ArrayList;
import java.util.Date;

public class Meal {
    private ArrayList<String> foodList;
    private int pain;
    private String date;

    public Meal(ArrayList<String> foodList,int pain, String date){
        this.foodList = foodList;
        this.pain = pain;
        this.date = date;
    }

    public void addToFoodList(String food){
        foodList.add(food);
    }

    public void removeFromFoodList(String food){
        foodList.remove(food);
    }

    public void setPain(int p){
        pain = p;
    }

    public String getDate(){
        return date;
    }

    public int getPain(){
        return pain;
    }

    public ArrayList<String> getFoodList(){
        return foodList;
    }

    @Override
    public String toString() {
        String foodList = "";
        for(String food : getFoodList()){
            foodList = food + "\n";
        }
        return String.format(getDate() + "\n" + foodList + "Pain: " + getPain() + "\n");
    }

}
