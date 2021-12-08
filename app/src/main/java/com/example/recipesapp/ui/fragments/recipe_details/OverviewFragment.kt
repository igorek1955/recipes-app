package com.example.recipesapp.ui.fragments.recipe_details

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import coil.load
import com.example.recipesapp.R
import com.example.recipesapp.databinding.FragmentOverviewBinding
import com.example.recipesapp.models.Result
import com.example.recipesapp.utilities.Constants.Companion.RECIPE_RESULT_ARGS_KEY
import org.jsoup.Jsoup

class OverviewFragment : Fragment() {

    private var binding: FragmentOverviewBinding? = null
    private var currentRecipe: Result? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentOverviewBinding.inflate(layoutInflater, container, false)
        currentRecipe = arguments?.getParcelable<Result>(RECIPE_RESULT_ARGS_KEY)
        setupViewsFromRecipeBundle()
        return binding!!.root
    }

    private fun setupViewsFromRecipeBundle() {
        try {
            binding!!.mainImageView.load(currentRecipe?.image)
            binding!!.titleTextView.text = currentRecipe?.title
            binding!!.likesTextView.text = currentRecipe?.aggregateLikes.toString()
            binding!!.timeTextView.text = currentRecipe?.readyInMinutes.toString()

            currentRecipe?.summary.let {
                val parsedSummary = Jsoup.parse(it).text()
                binding!!.summaryTextView.text = parsedSummary
            }

            updateColors(currentRecipe!!.vegetarian, binding!!.vegetarianTextView, binding!!.vegetarianImageView)
            updateColors(currentRecipe!!.vegan, binding!!.veganTextView, binding!!.veganImageView)
            updateColors(currentRecipe!!.cheap, binding!!.cheapTextView, binding!!.cheapImageView)
            updateColors(currentRecipe!!.dairyFree, binding!!.dairyFreeTextView, binding!!.dairyFreeImageView)
            updateColors(currentRecipe!!.glutenFree, binding!!.glutenFreeTextView, binding!!.glutenFreeImageView)
            updateColors(currentRecipe!!.veryHealthy, binding!!.healthyTextView, binding!!.healthyImageView)
        } catch (e: Exception) {
            Log.i(
                "OverviewFragment",
                "setupViewsFromRecipeBundle ERROR ${e.message} ${e.stackTrace}"
            )
        }
    }

    private fun updateColors(statsIsOn: Boolean, textView: TextView, imageView: ImageView) {
        if (statsIsOn) {
            imageView.setColorFilter(ContextCompat.getColor(
                requireContext(),
                R.color.green
            ))
            textView.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.green
                )
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}