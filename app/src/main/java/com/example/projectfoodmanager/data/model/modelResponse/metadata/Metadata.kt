package com.example.projectfoodmanager.data.model.modelResponse.metadata

data class Metadata(
    val Links: List<Links>,
    val current_page: Int,
    val items_per_page: Int,
    val total_items: Int,
    val total_pages: Int
)