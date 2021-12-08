package com.example.recipesapp.viewmodels

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.recipesapp.data.DataStoreRepository
import com.example.recipesapp.data.MealAndDietType
import com.example.recipesapp.utilities.Constants
import com.example.recipesapp.utilities.Constants.Companion.API_KEY
import com.example.recipesapp.utilities.Constants.Companion.DEFAULT_DIET_TYPE
import com.example.recipesapp.utilities.Constants.Companion.DEFAULT_MEAL_TYPE
import com.example.recipesapp.utilities.Constants.Companion.DEFAULT_RECIPES_NUMBER
import com.example.recipesapp.utilities.Constants.Companion.QUERY_ADD_RECIPE_INFORMATION
import com.example.recipesapp.utilities.Constants.Companion.QUERY_API_KEY
import com.example.recipesapp.utilities.Constants.Companion.QUERY_DIET
import com.example.recipesapp.utilities.Constants.Companion.QUERY_FILL_INGREDIENTS
import com.example.recipesapp.utilities.Constants.Companion.QUERY_NUMBER
import com.example.recipesapp.utilities.Constants.Companion.QUERY_SEARCH
import com.example.recipesapp.utilities.Constants.Companion.QUERY_TYPE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipesViewModel @Inject constructor(
    application: Application,
    private val dataStoreRepository: DataStoreRepository
) : AndroidViewModel(application) {

    val readMealAndDietType = dataStoreRepository.readMealAndDietType
    val readBackOnline = dataStoreRepository.readBackOnline

    //false = offline
    var networkStatus = false

    //set to true after reestablish internet connection
    var backOnline = false

    private lateinit var mealAndDiet: MealAndDietType

    /**
     * saving updated query settings to dataStore (aka shared preferences)
     * when we receive successful api rest response
     */
    fun saveMealAndDietTypeToCache() = viewModelScope.launch(Dispatchers.IO) {
        dataStoreRepository.saveMealAndDietType(
            mealAndDiet.selectedMealType,
            mealAndDiet.selectedMealTypeId,
            mealAndDiet.selectedDietType,
            mealAndDiet.selectedDietTypeId
        )
    }

    /**
     * locally saving meal and type id when we press apply query params on main screen (bottom sheet)
     */
    fun saveMealAndDietTypeTemp(
        mealType: String,
        mealTypeId: Int,
        dietType: String,
        dietTypeId: Int
    ) {
        mealAndDiet = MealAndDietType(mealType, mealTypeId, dietType, dietTypeId)
    }

    fun saveBackOnline(isBackOnline: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        dataStoreRepository.saveBackOnline(isBackOnline)
    }

    fun applyQueries(): HashMap<String, String> {
        return hashMapOf(
            QUERY_NUMBER to DEFAULT_RECIPES_NUMBER,
            QUERY_API_KEY to API_KEY,
            QUERY_TYPE to mealAndDiet.selectedMealType,
            QUERY_DIET to mealAndDiet.selectedDietType,
            QUERY_ADD_RECIPE_INFORMATION to "true",
            QUERY_FILL_INGREDIENTS to "true"
        )
    }

    fun prepareRecipeSearchQuery(query: String): Map<String, String> {
        return hashMapOf(
            QUERY_SEARCH to query,
            QUERY_NUMBER to DEFAULT_RECIPES_NUMBER,
            QUERY_API_KEY to API_KEY,
            QUERY_ADD_RECIPE_INFORMATION to "true",
            QUERY_FILL_INGREDIENTS to "true"
        )
    }

    fun showNetworkStatus() {
        if (!networkStatus) {
            Toast.makeText(getApplication(), "No Internet Connection", Toast.LENGTH_LONG).show()
            saveBackOnline(true)
        } else {
            if (backOnline) {
                Toast.makeText(getApplication(), "Connected to Internet now", Toast.LENGTH_LONG).show()
                saveBackOnline(false)
            }
        }
    }


}