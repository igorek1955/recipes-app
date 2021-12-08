package com.example.recipesapp.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.recipesapp.models.FoodRecipe
import com.example.recipesapp.utilities.Constants

/**
 * entity for cashing purposes
 * table has only one field
 */
@Entity(tableName = Constants.RECIPES_TABLE)
class RecipesEntity(var foodRecipe: FoodRecipe) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0
}