package com.example.aplikasimanajemenstok.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey val id: Int,
    val item_name: String,
    val stock: Int,
    val unit: String
)
