package com.example.recipesapp.models


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ExtendedIngredient(
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("consistency")
    val consistency: String?,
    @SerializedName("image")
    val image: String?,
    @SerializedName("name")
    val name: String,
    @SerializedName("original")
    val original: String,
    @SerializedName("unit")
    val unit: String
): Parcelable {
    override fun toString(): String {
        return "ExtendedIngredient(amount=$amount, consistency='$consistency', image='$image', name='$name', original='$original', unit='$unit')"
    }
}