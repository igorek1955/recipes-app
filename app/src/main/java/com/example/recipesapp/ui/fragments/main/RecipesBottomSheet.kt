package com.example.recipesapp.ui.fragments.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.example.recipesapp.databinding.RecipesBottomSheetBinding
import com.example.recipesapp.utilities.Constants.Companion.DEFAULT_DIET_TYPE
import com.example.recipesapp.utilities.Constants.Companion.DEFAULT_MEAL_TYPE
import com.example.recipesapp.viewmodels.RecipesViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception
import java.util.*

@AndroidEntryPoint
class RecipesBottomSheet : BottomSheetDialogFragment() {

    private var binding: RecipesBottomSheetBinding? = null
    private lateinit var recipesViewModel: RecipesViewModel
    private var mealTypeChip = DEFAULT_MEAL_TYPE
    private var mealTypeChipId = 0
    private var dietTypeChip = DEFAULT_DIET_TYPE
    private var dietTypeChipId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recipesViewModel = ViewModelProvider(requireActivity())[RecipesViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = RecipesBottomSheetBinding.inflate(layoutInflater, container, false)
        readSharedPreferences()
        setChipsListener()
        setupApplyButton()
        return binding!!.root
    }

    private fun readSharedPreferences() {
        recipesViewModel.readMealAndDietType.asLiveData().observe(viewLifecycleOwner, { value ->
            mealTypeChip = value.selectedMealType
            dietTypeChip = value.selectedDietType
            updateChip(value.selectedMealTypeId, binding!!.mealTypeChipGroup)
            updateChip(value.selectedDietTypeId, binding!!.dietTypeChipGroup)
        })
    }

    private fun updateChip(chipId: Int, chipGroup: ChipGroup) {
        if (chipId != 0) {
            try {
                val targetView = chipGroup.findViewById<Chip>(chipId)
                targetView.isChecked = true
                chipGroup.requestChildFocus(targetView, targetView)
            } catch (e: Exception) {
                Log.i(
                    "RecipeBottomSheet",
                    "updateChip ERROR ${e.message.toString()} ${e.stackTrace.toString()}"
                )
            }
        }
    }

    /**
     * new search query action initated from recipesFragment IF it detects:
     * backFromBottomSheet = true (see my_nav.xml)
     */
    private fun setupApplyButton() {
        binding!!.applyBtn.setOnClickListener {
            //saving to temp storage first, will persist data ONLY on successful rest response
            recipesViewModel.saveMealAndDietTypeTemp(
                mealTypeChip,
                mealTypeChipId,
                dietTypeChip,
                dietTypeChipId
            )
            val action =
                RecipesBottomSheetDirections.actionRecipesBottomSheetToRecipesFragment(true)
            findNavController().navigate(action)
        }
    }

    private fun setChipsListener() {
        binding!!.mealTypeChipGroup.setOnCheckedChangeListener { group, checkedId ->
            val chip = group.findViewById<Chip>(checkedId)
            val selectedMealType = chip.text.toString().lowercase(Locale.getDefault())
            mealTypeChip = selectedMealType
            mealTypeChipId = checkedId
        }

        binding!!.dietTypeChipGroup.setOnCheckedChangeListener { group, checkedId ->
            val chip = group.findViewById<Chip>(checkedId)
            val selectedDietType = chip.text.toString().lowercase(Locale.getDefault())
            dietTypeChip = selectedDietType
            dietTypeChipId = checkedId
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}