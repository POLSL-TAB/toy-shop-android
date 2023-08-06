package pl.shop.toyshop.model

data class UpdateProduct(
    val id: Int,
    val name: String,
    val description: String,
    val price: String,
    val stock: Int
)
