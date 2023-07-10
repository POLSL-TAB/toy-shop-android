package pl.shop.toyshop.model

data class ShoppingCart (
    var productId: String,
    var quantity: String
){
    constructor() : this("", "")
}