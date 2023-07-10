package pl.shop.toyshop.model

data class Products(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val stock: Int
)