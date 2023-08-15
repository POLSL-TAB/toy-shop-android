package pl.shop.toyshop.model

data class Complaint(
    val id: Int,
    val orderId: Int,
    val status: String,
    val reason: String,
    val created: String,
    val updated: String
)
