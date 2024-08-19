package com.example.projectfoodmanager.data.model.modelResponse

import com.example.projectfoodmanager.data.model.modelResponse.metadata.Metadata

data class PaginatedList<T> (
    val _metadata: Metadata,
    val result: MutableList<T>

    )