package com.example.aplikasimanajemenstok.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.aplikasimanajemenstok.data.db.ItemEntity
import com.example.aplikasimanajemenstok.data.repository.ItemRepository
import com.example.aplikasimanajemenstok.databinding.DialogItemFormBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ItemFormDialogFragment : DialogFragment() {

    private var _binding: DialogItemFormBinding? = null
    private val binding get() = _binding!!
    private lateinit var itemRepository: ItemRepository
    private var editMode = false
    private var itemId: Int = 0

    interface ItemFormListener {
        fun onDataChanged()
    }
    var listener: ItemFormListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogItemFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        itemRepository = ItemRepository(requireContext())
        Log.d("ItemFormDialog", "Repository initialized")

        // Cek argumen untuk mode edit
        arguments?.let { bundle ->
            if (bundle.containsKey("extra_item_id")) {
                editMode = true
                itemId = bundle.getInt("extra_item_id")
                val name = bundle.getString("extra_item_name") ?: ""
                val stock = bundle.getInt("extra_item_stock", 0)
                val unit = bundle.getString("extra_item_unit") ?: ""
                Log.d("ItemFormDialog", "Edit mode: id=$itemId, name=$name, stock=$stock, unit=$unit")
                binding.etItemName.setText(name)
                binding.etStock.setText(stock.toString())
                binding.etUnit.setText(unit)
            } else {
                Log.d("ItemFormDialog", "Create mode: tidak ada argumen")
            }
        } ?: Log.d("ItemFormDialog", "No bundle arguments")

        binding.btnSave.setOnClickListener {
            val itemName = binding.etItemName.text.toString().trim()
            val stockStr = binding.etStock.text.toString().trim()
            val unit = binding.etUnit.text.toString().trim()

            Log.d("ItemFormDialog", "btnSave clicked: itemName='$itemName', stockStr='$stockStr', unit='$unit'")

            if (itemName.isEmpty() || stockStr.isEmpty() || unit.isEmpty()) {
                Toast.makeText(requireContext(), "Semua field harus diisi", Toast.LENGTH_SHORT).show()
                Log.d("ItemFormDialog", "Validation failed: ada field kosong")
                return@setOnClickListener
            }

            val stock = stockStr.toIntOrNull()
            if (stock == null) {
                Toast.makeText(requireContext(), "Stock harus berupa angka", Toast.LENGTH_SHORT).show()
                Log.d("ItemFormDialog", "Validation failed: stock bukan angka")
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val success = if (editMode) {
                        Log.d("ItemFormDialog", "Updating item id=$itemId")
                        itemRepository.updateItemLocal(ItemEntity(itemId, itemName, stock, unit))
                    } else {
                        val localItems = itemRepository.getAllItems()
                        val newId = (localItems.maxOfOrNull { it.id } ?: 0) + 1
                        Log.d("ItemFormDialog", "Creating new item with id=$newId")
                        itemRepository.createItemLocal(ItemEntity(newId, itemName, stock, unit))
                    }
                    requireActivity().runOnUiThread {
                        if (success) {
                            Toast.makeText(requireContext(), "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
                            Log.d("ItemFormDialog", "Data saved successfully")
                            listener?.onDataChanged()
                            dismiss()
                        } else {
                            Toast.makeText(requireContext(), "Gagal menyimpan data", Toast.LENGTH_SHORT).show()
                            Log.d("ItemFormDialog", "Failed to save data")
                        }
                    }
                } catch (e: Exception) {
                    Log.e("ItemFormDialog", "Exception during save: ${e.message}", e)
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawableResource(android.R.color.transparent)
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d("ItemFormDialog", "Binding cleared in onDestroyView")
    }
}
