package com.example.recipesapp.ui.fragments.main

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipesapp.R
import com.example.recipesapp.viewmodels.MainViewModel
import com.example.recipesapp.adapters.RecipesAdapter
import com.example.recipesapp.databinding.FragmentRecipesBinding
import com.example.recipesapp.utilities.NetworkListener
import com.example.recipesapp.utilities.NetworkResult
import com.example.recipesapp.utilities.observeOnce
import com.example.recipesapp.viewmodels.RecipesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecipesFragment : Fragment(), SearchView.OnQueryTextListener {
    private val args by navArgs<RecipesFragmentArgs>()
    private lateinit var mainViewModel: MainViewModel
    private lateinit var recipesViewModel: RecipesViewModel
    private val mAdapter by lazy { RecipesAdapter() }
    private var binding: FragmentRecipesBinding? = null
    private lateinit var networkListener: NetworkListener

    override fun onCreate(savedInstanceState: Bundle?) {
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        recipesViewModel = ViewModelProvider(requireActivity())[RecipesViewModel::class.java]
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRecipesBinding.inflate(inflater, container, false)
        binding!!.lifecycleOwner = this
        binding!!.mainViewModel = mainViewModel

        setupMenu()
        setupRecyclerView()
        setupNetworkListener()
        setupFab()
        return binding!!.root
    }

    /**
     * actual menu initialization occurs in onCreateOptionsMenu
     */
    private fun setupMenu() {
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.recipes_menu, menu)

        val search = menu.findItem(R.id.menu_search)
        val searchView = search.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (!query.isNullOrEmpty()) {
           sendSearchRequest(query)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }


    private fun setupNetworkListener() {

        recipesViewModel.readBackOnline.asLiveData().observe(viewLifecycleOwner, {
            recipesViewModel.backOnline = it
        })

        lifecycleScope.launchWhenStarted {
            networkListener = NetworkListener()
            networkListener.checkNetworkAvailability(requireContext())
                .collect {
                    Log.d("RecipesFragment", "onCreateView DEBUG networkAvailability: ${it.toString()}")
                    recipesViewModel.networkStatus = it
                    recipesViewModel.showNetworkStatus()
                    readDatabase()
                }
        }
    }

    private fun setupFab() {
        binding!!.fabFragmentRecipes.setOnClickListener {
            if (recipesViewModel.networkStatus) {
                findNavController().navigate(R.id.action_recipesFragment_to_recipesBottomSheet)
            } else {
                recipesViewModel.showNetworkStatus()
            }
        }
    }

    private fun setupRecyclerView() {
        binding!!.recyclerView.adapter = mAdapter
        binding!!.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        showShimmerEffect()
    }

    /**
     * loading data from db on QUERY ERROR
     */
    private fun loadDataFromCache() {
        lifecycleScope.launch {
            mainViewModel.localRecipes.observe(viewLifecycleOwner, { database ->
                if (database.isNotEmpty()) {
                    mAdapter.setData(database[0].foodRecipe)
                }
            })
        }
    }

    private fun readDatabase() {
        lifecycleScope.launch {
            mainViewModel.localRecipes.observeOnce(viewLifecycleOwner, { database ->
                /**
                 * read database ONLY if db is not empty AND we
                 * have not pressed apply new search params in RecipesBottomSheet
                 * ELSE -> request new data
                 */
                if (database.isNotEmpty() && !args.backFromBottomSheet) {
                    Log.i("RecipesFragment", "readDatabase called")
                    mAdapter.setData(database[0].foodRecipe)
                    hideShimmerEffect()
                } else {
                    requestApiData()
                }
            })
        }
    }

    /**
     * making network call to fetch new data
     */
    private fun requestApiData() {
        Log.i("RecipesFragment", "requestApiData called")
        mainViewModel.getRecipes(recipesViewModel.applyQueries())
        mainViewModel.recipesResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is NetworkResult.Success -> {
                    hideShimmerEffect()
                    response.data?.let { mAdapter.setData(it) }
                    recipesViewModel.saveMealAndDietTypeToCache()
                }
                is NetworkResult.Error -> {
                    hideShimmerEffect()
                    loadDataFromCache()
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
                is NetworkResult.Loading -> {
                    showShimmerEffect()
                }
            }
        })
    }

    /**
     * search request with specific user defined query
     */
    private fun sendSearchRequest(query: String) {
        showShimmerEffect()
        Log.d("RecipesFragment", "onQueryTextSubmit called for query: $query")
        mainViewModel.searchRecipes(recipesViewModel.prepareRecipeSearchQuery(query))
        mainViewModel.searchedRecipesResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is NetworkResult.Success -> {
                    hideShimmerEffect()
                    response.data?.let { mAdapter.setData(it) }
                }
                is NetworkResult.Error -> {
                    hideShimmerEffect()
                    loadDataFromCache()
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is NetworkResult.Loading -> {
                    showShimmerEffect()
                }
            }
        })
    }



    private fun showShimmerEffect() {
        binding!!.shimmerFrameLayout.visibility = View.VISIBLE
        binding!!.shimmerFrameLayout.startShimmer()
        binding!!.recyclerView.visibility = View.GONE
    }

    private fun hideShimmerEffect() {
        binding!!.shimmerFrameLayout.stopShimmer()
        binding!!.shimmerFrameLayout.visibility = View.INVISIBLE
        binding!!.recyclerView.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //setting to null to avoid memory leaks
        binding = null
    }
}