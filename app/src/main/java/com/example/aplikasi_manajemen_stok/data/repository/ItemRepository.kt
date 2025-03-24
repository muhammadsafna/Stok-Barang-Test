package com.example.aplikasimanajemenstok.data.repository

import android.content.Context
import android.util.Log
import com.example.aplikasimanajemenstok.data.api.ApiClient
import com.example.aplikasimanajemenstok.data.api.ItemResponse
import com.example.aplikasimanajemenstok.data.db.AppDatabase
import com.example.aplikasimanajemenstok.data.db.ItemEntity
import retrofit2.Response

class ItemRepository(context: Context) {
    private val itemDao = AppDatabase.getDatabase(context).itemDao()

    // Mengambil data dari API untuk refresh (source of truth)
    suspend fun fetchItemsFromApi(apiToken: String): Response<ItemResponse> {
        return ApiClient.apiService.getItems("Bearer $apiToken")
    }

    // Hapus semua data lokal
    suspend fun deleteAllItems() {
        itemDao.deleteAllItems()
    }

    // Operasi CRUD lokal:
    suspend fun createItemLocal(item: ItemEntity): Boolean {
        return try {
            itemDao.insertItem(item)
            true
        } catch (e: Exception) {
            Log.e("ItemRepository", "Error createItemLocal: ${e.message}")
            false
        }
    }

    suspend fun updateItemLocal(item: ItemEntity): Boolean {
        return try {
            itemDao.updateItem(item)
            true
        } catch (e: Exception) {
            Log.e("ItemRepository", "Error updateItemLocal: ${e.message}")
            false
        }
    }

    suspend fun deleteItemLocal(item: ItemEntity): Boolean {
        return try {
            itemDao.deleteItem(item)
            true
        } catch (e: Exception) {
            Log.e("ItemRepository", "Error deleteItemLocal: ${e.message}")
            false
        }
    }

    suspend fun getAllItems(): List<ItemEntity> {
        return itemDao.getAllItems()
    }

    suspend fun insertItems(items: List<ItemEntity>) {
        itemDao.insertAll(items)
    }
}
