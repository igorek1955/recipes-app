package com.example.recipesapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.recipesapp.R
import com.example.recipesapp.databinding.IngredientsItemBinding
import com.example.recipesapp.models.ExtendedIngredient
import com.example.recipesapp.utilities.Constants.Companion.BASE_IMAGE_URL
import com.example.recipesapp.utilities.RecipesDiffUtil

class IngredientsAdapter: RecyclerView.Adapter<IngredientsAdapter.IngredientsViewHolder>() {
    private var ingredientsList = emptyList<ExtendedIngredient>()
    class IngredientsViewHolder(val binding: IngredientsItemBinding) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = IngredientsItemBinding.inflate(layoutInflater, parent, false)
        return IngredientsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IngredientsViewHolder, position: Int) {
        holder.binding.ingredientIv.load(BASE_IMAGE_URL + ingredientsList[position].image) {
            crossfade(600)
            error(R.drawable.ic_error_placeholder)
        }
        holder.binding.nameTv.text = ingredientsList[position].name.capitalize()
        holder.binding.amountTv.text = ingredientsList[position].amount.toString()
        holder.binding.unitTv.text = ingredientsList[position].unit
        holder.binding.consistencyTv.text = ingredientsList[position].consistency
        holder.binding.originalTv.text = ingredientsList[position].original
    }

    override fun getItemCount(): Int {
        return ingredientsList.size
    }

    fun setData(newIngredients: List<ExtendedIngredient>) {
        val ingredientsDiffUtil = RecipesDiffUtil(ingredientsList, newIngredients)
        val diffUtilResult = DiffUtil.calculateDiff(ingredientsDiffUtil)
        ingredientsList = newIngredients
        diffUtilResult.dispatchUpdatesTo(this)
    }
}