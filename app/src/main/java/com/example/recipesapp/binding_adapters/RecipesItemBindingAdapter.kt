package com.example.recipesapp.binding_adapters

import android.util.Log
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import coil.Coil
import coil.load
import com.example.recipesapp.R
import com.example.recipesapp.models.Result
import com.example.recipesapp.ui.fragments.main.RecipesFragmentDirections
import org.jsoup.Jsoup
import java.lang.Exception
/**
 * recipes ITEMS binding adapter , on RecipesFragment
 */
class RecipesItemBindingAdapter {
    companion object {

        @BindingAdapter("onRecipeClickListener")
        @JvmStatic
        fun onRecipeClickListener(recipesItemLayout: ConstraintLayout, result: Result) {
            recipesItemLayout.setOnClickListener {
                Log.d(
                    "RecipesItemBindingAdapter",
                    "onRecipeClickListener INFO clicked: ${result.toString()}"
                )
                try {
                    val action =
                        RecipesFragmentDirections.actionRecipesFragmentToRecipeDetailsActivity(
                            result
                        )
                    recipesItemLayout.findNavController().navigate(action)
                } catch (e: Exception) {
                    Log.d(
                        "RecipesItemBindingAdapter",
                        "onRecipeClickListener ERROR ${e.message} ${e.stackTrace.toString()}"
                    )
                }
            }
        }

        @BindingAdapter("loadImageFromUrl")
        @JvmStatic
        fun loadImageFromUrl(imageView: ImageView, url: String) {
            imageView.load(url) {
                crossfade(600)
                error(R.drawable.ic_error_placeholder)
            }
        }

        @BindingAdapter("applyVeganColor")
        @JvmStatic
        fun applyVeganColor(view: View, vegan: Boolean) {
            if (vegan && view is TextView) {
                view.setTextColor(ContextCompat.getColor(view.context, R.color.green))
            }

            if (vegan && view is ImageView) {
                view.setColorFilter(ContextCompat.getColor(view.context, R.color.green))
            }
        }

        @BindingAdapter("parseHtml")
        @JvmStatic
        fun parseHtml(textView: TextView, description: String?) {
            if (description != null) {
                val parsedDescription = Jsoup.parse(description).text()
                textView.text = parsedDescription
            }
        }
    }
}