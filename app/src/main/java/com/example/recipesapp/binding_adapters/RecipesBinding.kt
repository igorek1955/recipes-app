package com.example.recipesapp.binding_adapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.example.recipesapp.data.database.entities.RecipesEntity
import com.example.recipesapp.models.FoodRecipe
import com.example.recipesapp.utilities.NetworkResult

/**
 * recipes screen binding
 */
class RecipesBinding {
    companion object {
        @JvmStatic
        @BindingAdapter("readApiResponse", "readDatabase", requireAll = true)
        fun errorImageViewVisibility(
            view: View,
            apiResponse: NetworkResult<FoodRecipe>?,
            database: List<RecipesEntity>?
        ) {
            when (view) {
                is ImageView -> {
                    view.isVisible = apiResponse is NetworkResult.Error && database.isNullOrEmpty()
                }
                is TextView -> {
                    view.isVisible = apiResponse is NetworkResult.Error && database.isNullOrEmpty()
                    view.text = apiResponse?.message.toString()
                }
            }
        }
    }
}