package com.example.aplikasimanajemenstok.ui.main

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplikasimanajemenstok.data.db.ItemEntity
import com.example.aplikasimanajemenstok.data.repository.ItemRepository
import com.example.aplikasimanajemenstok.utils.PreferenceManager
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    val items = MutableLiveData<List<ItemEntity>>()
    val errorMessage = MutableLiveData<String>()
    val loading = MutableLiveData<Boolean>()
    private lateinit var itemRepository: ItemRepository

    companion object {
        private const val TAG = "MainViewModel"
    }

    fun refreshItems(context: Context) {
        val token = PreferenceManager.getToken(context)
        if (token.isNullOrEmpty()) {
            errorMessage.postValue("Token tidak valid")
            return
        }
        itemRepository = ItemRepository(context)
        loading.postValue(true)
        viewModelScope.launch {
            try {
                val response = itemRepository.fetchItemsFromApi(token)
                if (response.isSuccessful && response.body()?.statusCode == 1) {
                    val apiEntities = response.body()?.data?.map { apiItem ->
                        ItemEntity(
                            id = apiItem.id,
                            item_name = apiItem.itemName ?: "Tidak tersedia",
                            stock = apiItem.stock,
                            unit = apiItem.unit ?: "Tidak tersedia"
                        )
                    } ?: listOf()

                    // Overwrite DB lokal
                    itemRepository.deleteAllItems()
                    itemRepository.insertItems(apiEntities)

                    items.postValue(apiEntities)
                    errorMessage.postValue("")
                    Log.d(TAG, "Data refreshed (overwritten): ${apiEntities.size} items")
                } else {
                    errorMessage.postValue("API call gagal atau statusCode != 1")
                    val localItems = itemRepository.getAllItems()
                    items.postValue(localItems)
                    Log.d(TAG, "Menggunakan data lokal: ${localItems.size} items")
                }
            } catch (e: Exception) {
                errorMessage.postValue("Error: ${e.message}")
                val localItems = itemRepository.getAllItems()
                items.postValue(localItems)
                Log.e(TAG, "Error refreshItems: ${e.message}", e)
            } finally {
                loading.postValue(false)
            }
        }
    }

    fun loadLocalItems(context: Context) {
        itemRepository = ItemRepository(context)
        viewModelScope.launch {
            val localItems = itemRepository.getAllItems()
            items.postValue(localItems)
            Log.d(TAG, "Local items loaded: ${localItems.size} items")
        }
    }

    fun deleteItemSync(item: ItemEntity) {
        viewModelScope.launch {
            val success = itemRepository.deleteItemLocal(item)
            if (success) {
                items.postValue(itemRepository.getAllItems())
                Log.d(TAG, "Item deleted: ${item.item_name}")
            } else {
                errorMessage.postValue("Delete item gagal")
                Log.d(TAG, "Delete item gagal: ${item.item_name}")
            }
        }
    }
}
