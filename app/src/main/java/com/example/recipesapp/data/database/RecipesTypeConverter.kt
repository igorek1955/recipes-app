package com.example.recipesapp.data.database

import androidx.room.TypeConverter
import com.example.recipesapp.models.FoodRecipe
import com.example.recipesapp.models.Result
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RecipesTypeConverter {
    var gson = Gson()

    @TypeConverter
    fun foodRecipeToString(foodRecipe: FoodRecipe): String {
        return gson.toJson(foodRecipe)
    }

    @TypeConverter
    fun stringToFoodRecipe(string: String): FoodRecipe {
        val listType = object : TypeToken<FoodRecipe>() {}.type
        return gson.fromJson(string, listType)
//        return gson.fromJson(string, FoodRecipe::class.java)
    }

    @TypeConverter
    fun resultToString(result: Result): String {
        return gson.toJson(result)
    }

    @TypeConverter
    fun stringToResult(string: String): Result {
        val listType = object : TypeToken<Result>() {}.type
        return gson.fromJson(string, listType)
    }
}