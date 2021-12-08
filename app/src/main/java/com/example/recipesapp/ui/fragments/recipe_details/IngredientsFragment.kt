package com.example.recipesapp.ui.fragments.recipe_details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipesapp.R
import com.example.recipesapp.adapters.IngredientsAdapter
import com.example.recipesapp.databinding.FragmentIngredientsBinding
import com.example.recipesapp.models.Result
import com.example.recipesapp.utilities.Constants.Companion.RECIPE_RESULT_ARGS_KEY

class IngredientsFragment : Fragment() {

    private var currentRecipe: Result? = null
    private val mAdapter: IngredientsAdapter by lazy { IngredientsAdapter() }
    private var binding: FragmentIngredientsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentIngredientsBinding.inflate(layoutInflater, container, false)
        currentRecipe = arguments?.getParcelable<Result>(RECIPE_RESULT_ARGS_KEY)
        setupRecyclerView()
        return binding!!.root
    }

    private fun setupRecyclerView() {
        binding!!.ingredientsRv.layoutManager = LinearLayoutManager(requireContext())
        binding!!.ingredientsRv.adapter = mAdapter
        mAdapter.setData(currentRecipe!!.extendedIngredients)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}