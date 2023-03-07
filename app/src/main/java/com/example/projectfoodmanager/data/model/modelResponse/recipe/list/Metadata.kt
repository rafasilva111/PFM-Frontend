package com.example.projectfoodmanager.data.model.modelResponse.recipe.list

data class Metadata(
    val Links: List<Link>,
    val page: Int,
    val page_count: Int,
    val per_page: Int,
    val recipes_total: Int
)