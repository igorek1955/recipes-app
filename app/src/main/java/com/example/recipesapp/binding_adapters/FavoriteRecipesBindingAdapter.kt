package com.example.recipesapp.binding_adapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.recipesapp.adapters.FavoriteRecipesAdapter
import com.example.recipesapp.data.database.entities.FavoritesEntity

class FavoriteRecipesBindingAdapter {
    companion object {
        @BindingAdapter("setVisibility", "setData", requireAll = false)
        @JvmStatic
        fun setVisibility(view: View, favList: List<FavoritesEntity>?, mAdapter: FavoriteRecipesAdapter?) {
            when (view) {
                is RecyclerView -> {
                    val dataCheck = favList.isNullOrEmpty()
                    view.isInvisible = dataCheck
                    if (!dataCheck) {
                        favList?.let { mAdapter?.setData(it) }
                    }
                }
                else -> view.isVisible = favList.isNullOrEmpty()
            }
        }
    }
}