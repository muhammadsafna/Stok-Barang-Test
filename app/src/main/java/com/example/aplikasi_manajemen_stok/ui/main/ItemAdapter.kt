package com.example.aplikasimanajemenstok.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasimanajemenstok.data.db.ItemEntity
import com.example.aplikasimanajemenstok.databinding.ItemListBinding

class ItemAdapter(private val onItemAction: (ItemEntity, Action) -> Unit) :
    ListAdapter<ItemEntity, ItemAdapter.ItemViewHolder>(ItemDiffCallback()) {

    enum class Action {
        EDIT, DELETE
    }

    inner class ItemViewHolder(private val binding: ItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ItemEntity) {
            binding.tvNama.text = item.item_name
            binding.tvStok.text = "Stok: ${item.stock} ${item.unit}"
            binding.btnEdit.setOnClickListener { onItemAction(item, Action.EDIT) }
            binding.btnDelete.setOnClickListener { onItemAction(item, Action.DELETE) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class ItemDiffCallback : DiffUtil.ItemCallback<ItemEntity>() {
    override fun areItemsTheSame(oldItem: ItemEntity, newItem: ItemEntity): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: ItemEntity, newItem: ItemEntity): Boolean =
        oldItem == newItem
}
