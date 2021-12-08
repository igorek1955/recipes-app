package com.example.recipesapp.ui.fragments.recipe_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.example.recipesapp.databinding.FragmentInstructionsBinding
import com.example.recipesapp.models.Result
import com.example.recipesapp.utilities.Constants.Companion.RECIPE_RESULT_ARGS_KEY

class InstructionsFragment : Fragment() {
    private var binding: FragmentInstructionsBinding? = null
    private var result: Result? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentInstructionsBinding.inflate(layoutInflater, container, false)
        result = arguments?.getParcelable<Result>(RECIPE_RESULT_ARGS_KEY)
        setupWebView()
        return binding!!.root
    }

    private fun setupWebView() {
        binding!!.instructionsWebView.webViewClient = object : WebViewClient() { }
        val webUrl: String = result!!.sourceUrl!!
        binding!!.instructionsWebView.loadUrl(webUrl)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}