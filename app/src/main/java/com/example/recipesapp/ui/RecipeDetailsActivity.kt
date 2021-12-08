package com.example.recipesapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.navArgs
import com.example.recipesapp.R
import com.example.recipesapp.adapters.DetailsPagerAdapter
import com.example.recipesapp.data.database.entities.FavoritesEntity
import com.example.recipesapp.databinding.ActivityRecipeDetailsBinding
import com.example.recipesapp.ui.fragments.recipe_details.IngredientsFragment
import com.example.recipesapp.ui.fragments.recipe_details.InstructionsFragment
import com.example.recipesapp.ui.fragments.recipe_details.OverviewFragment
import com.example.recipesapp.utilities.Constants.Companion.RECIPE_RESULT_ARGS_KEY
import com.example.recipesapp.viewmodels.MainViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception

/**
 * recipe details page
 */
@AndroidEntryPoint
class RecipeDetailsActivity : AppCompatActivity() {

    //storing Result data class between fragments
    private val args by navArgs<RecipeDetailsActivityArgs>()
    lateinit var binding: ActivityRecipeDetailsBinding
    private val mainViewModel: MainViewModel by viewModels()

    private var recipeSaved = false
    private var savedRecipeId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
        setupViewPager()
    }


    private fun setupViewPager() {
        val fragments = arrayListOf<Fragment>(
            OverviewFragment(),
            IngredientsFragment(),
            InstructionsFragment()
        )

        val titles = arrayListOf<String>(
            "Overview",
            "Ingredients",
            "Instructions"
        )

        val resultBundle = Bundle()
        resultBundle.putParcelable(RECIPE_RESULT_ARGS_KEY, args.result)

        val pagerAdapter = DetailsPagerAdapter(resultBundle, fragments, this)

        /**
         * disabling swiping between fragments to avoid conflict with motion layout
         */
        binding.viewPager.isUserInputEnabled = false
        binding.viewPager.apply {
            adapter = pagerAdapter
        }

        //setting up tabs
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }

    /**
     * setting up toolbar with navigation back to home screen
     */
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.details_menu, menu)
        val menuItem = menu?.findItem(R.id.save_to_favorites_item)
        changeMenuItemColor(menuItem!!, R.color.white)
        checkSavedRecipes(menuItem!!)
        return true
    }

    /**
     * if recipe is already in favorites -> change star icon color to yellow
     */
    private fun checkSavedRecipes(menuItem: MenuItem) {
        mainViewModel.localFavoriteRecipes.observe(this, { favoriteRecipes ->
            try {
                for (savedRecipe in favoriteRecipes) {
                    if (savedRecipe.result.id == args.result.id) {
                        changeMenuItemColor(menuItem, R.color.yellow)
                        savedRecipeId = savedRecipe.id
                        recipeSaved = true
                    }
                }
            } catch (e: Exception) {
                Log.e(
                    "RecipeDetailsActivity",
                    "checkSavedRecipes ERROR ${e.message}, ${e.stackTrace}"
                )
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        } else if (item.itemId == R.id.save_to_favorites_item && !recipeSaved) {
            saveToFavorites(item)
        } else if (item.itemId == R.id.save_to_favorites_item && recipeSaved) {
            removeFromFavorites(item)
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * after saving favorite recipe to db we change star icon color to yellow
     */
    private fun changeMenuItemColor(item: MenuItem, color: Int) {
        item.icon.setTint(ContextCompat.getColor(this, color))
    }

    private fun saveToFavorites(item: MenuItem) {
        val favoritesEntity = FavoritesEntity(0, args.result)
        mainViewModel.insertFavoriteRecipe(favoritesEntity)
        changeMenuItemColor(item, R.color.yellow)
        showSnackBar("Recipe Saved To Favorites")
        recipeSaved = true
    }

    private fun removeFromFavorites(item: MenuItem) {
        val favoritesEntity = FavoritesEntity(savedRecipeId, args.result)
        mainViewModel.deleteFavoriteRecipe(favoritesEntity)
        changeMenuItemColor(item, R.color.white)
        showSnackBar("Removed ${favoritesEntity.result.title} from Favorites")
        recipeSaved = false
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).setAction("Okay") { }.show()
    }
}