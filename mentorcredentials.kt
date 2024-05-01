package com.ghulammustafa.smd_a2
import java.io.Serializable

data class mentorcredentials(
    var mentorId: String? = null,
    val name: String? = null,
    val description: String? = null,
    val status: String? = null,
    val price: String? = null,
    var profilePictureUri: String? = null
) : Serializable
