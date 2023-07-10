package pl.shop.toyshop.model

data class Order(
    val id: Int,
    val userEmail: String,
    val paid: Boolean,
    val created: String,
    val completed: String?,
    val paymentType: String?,
    val oderItems: List<Item>?,
    val returned: Boolean
)

