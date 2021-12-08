package com.example.recipesapp.data

import com.example.recipesapp.data.network.FoodRecipesApi
import com.example.recipesapp.models.FoodJoke
import com.example.recipesapp.models.FoodRecipe
import retrofit2.Response
import javax.inject.Inject

/**
 * using retrofit to make rest api calls
 */
class RemoteDataSource @Inject constructor(
    private val foodRecipesApi: FoodRecipesApi
) {
    suspend fun getRecipes(queries: Map<String, String>): Response<FoodRecipe> {
        return foodRecipesApi.getRecipes(queries)
    }

    suspend fun searchRecipes(queries: Map<String, String>): Response<FoodRecipe> {
        return foodRecipesApi.searchRecipes(queries)
    }

    suspend fun getFoodJoke(apiKey: String): Response<FoodJoke> {
        return foodRecipesApi.getFoodJoke(apiKey)
    }
}