package pl.shop.toyshop.model

data class User (
    val email: String,
    val password: String,
    val name: String? = null,
    val surname: String? = null
        )