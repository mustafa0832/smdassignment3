package com.ghulammustafa.smd_a2


import java.io.Serializable

data class MessageDataClass(
    var id: String? = null, // Added id field
    var senderId: String? = null,
    var receiverId: String? = null,
    val content: String? = null,
    val timestamp: Long? = null
) : Serializable
