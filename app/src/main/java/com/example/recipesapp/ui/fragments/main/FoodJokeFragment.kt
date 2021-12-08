package com.example.recipesapp.ui.fragments.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.recipesapp.R
import com.example.recipesapp.databinding.FragmentFoodJokeBinding
import com.example.recipesapp.models.FoodJoke
import com.example.recipesapp.utilities.Constants.Companion.API_KEY
import com.example.recipesapp.utilities.NetworkResult
import com.example.recipesapp.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FoodJokeFragment : Fragment() {

    //using delegate to get main view model
    private val mainViewModel: MainViewModel by viewModels()
    private var binding: FragmentFoodJokeBinding? = null
    private var foodJoke = "no joke yet"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFoodJokeBinding.inflate(layoutInflater, container, false)
        binding?.lifecycleOwner = viewLifecycleOwner
        binding?.mainViewModel = mainViewModel
        getFoodJoke()
        setupMenu()
        return binding!!.root
    }

    private fun setupMenu() {
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.food_joke_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.share_joke_item) {
            shareJoke()
        }
        return true
    }

    private fun shareJoke() {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, foodJoke)
            type = "text/plain"
        }
        startActivity(shareIntent)
    }

    private fun getFoodJoke() {
        mainViewModel.getFoodJoke(API_KEY)
        mainViewModel.foodJokeResponse.observe(viewLifecycleOwner, {
            processResponse(it)
        })
    }

    private fun processResponse(response: NetworkResult<FoodJoke>?) {
        when (response) {
            is NetworkResult.Success -> {
                binding!!.foodJokeTv.text = response.data?.text
                //storing joke for sharing purposes
                if (response.data != null) {
                    foodJoke = response.data.text
                }
            }
            is NetworkResult.Error -> {
                loadDataFromCache()
                Toast.makeText(requireContext(), response.message, Toast.LENGTH_LONG).show()
            }
            is NetworkResult.Loading -> {
                Log.d("FoodJokeFragment", "processResponse INFO loading joke ")
            }
        }
    }

    private fun loadDataFromCache() {
        mainViewModel.localFoodJokes.observe(viewLifecycleOwner, {database->
            if (!database.isNullOrEmpty()) {
                binding!!.foodJokeTv.text = database[0].foodJoke.text
                //storing joke for sharing purposes
                foodJoke = database[0].foodJoke.text
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}