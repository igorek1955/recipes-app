package com.example.recipesapp.ui.fragments.main

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipesapp.R
import com.example.recipesapp.adapters.FavoriteRecipesAdapter
import com.example.recipesapp.data.database.entities.FavoritesEntity
import com.example.recipesapp.databinding.FragmentFavoriteRecipesBinding
import com.example.recipesapp.viewmodels.MainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteRecipesFragment : Fragment() {

    var binding: FragmentFavoriteRecipesBinding? = null
    private val mainViewModel: MainViewModel by viewModels()
    private val mAdapter: FavoriteRecipesAdapter by lazy {
        FavoriteRecipesAdapter(
            requireActivity(),
            mainViewModel
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFavoriteRecipesBinding.inflate(inflater, container, false)
        binding!!.lifecycleOwner = this
        binding!!.mainViewModel = mainViewModel
        binding!!.mAdapter = mAdapter
        setupRecyclerView()
        setupMenu()
        return binding!!.root
    }

    /**
     * actual menu initialization occurs in onCreateOptionsMenu
     */
    private fun setupMenu() {
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.favorites_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.favorites_menu_deleteAll_item) {
            mainViewModel.deleteAllFavoriteRecipes()
            showSnackBar()
        }
        return true
    }

    private fun setupRecyclerView() {
        binding!!.favoriteRecipesRv.adapter = mAdapter
        binding!!.favoriteRecipesRv.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun showSnackBar() {
        Snackbar.make(binding!!.root, "All recipes removed", Snackbar.LENGTH_LONG)
            .setAction("OKAY") {}
            .show()
    }
}