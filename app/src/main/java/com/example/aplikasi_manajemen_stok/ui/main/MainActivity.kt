package com.example.aplikasimanajemenstok.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplikasimanajemenstok.data.db.ItemEntity
import com.example.aplikasimanajemenstok.databinding.ActivityMainBinding
import com.example.aplikasimanajemenstok.ui.login.LoginActivity
import com.example.aplikasimanajemenstok.utils.PreferenceManager

class MainActivity : AppCompatActivity(), ItemFormDialogFragment.ItemFormListener {
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by lazy { MainViewModel() }
    private lateinit var adapter: ItemAdapter
    private var allItems: List<ItemEntity> = emptyList()

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onDataChanged() {
        mainViewModel.loadLocalItems(this)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d(TAG, "onCreate: MainActivity started")

        // Inisialisasi adapter
        adapter = ItemAdapter { item, action ->
            when (action) {
                ItemAdapter.Action.EDIT -> {
                    val dialog = ItemFormDialogFragment().apply {
                        arguments = Bundle().apply {
                            putInt("extra_item_id", item.id)
                            putString("extra_item_name", item.item_name)
                            putInt("extra_item_stock", item.stock)
                            putString("extra_item_unit", item.unit)
                        }
                        listener = this@MainActivity
                    }
                    dialog.show(supportFragmentManager, "ItemFormDialog")
                }
                ItemAdapter.Action.DELETE -> {
                    mainViewModel.deleteItemSync(item)
                }
            }
        }

        // Setup RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // Tambahkan SpacingItemDecoration (2dp)
        val spacingInDp = 2
        val spacingInPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, spacingInDp.toFloat(), resources.displayMetrics
        ).toInt()
        binding.recyclerView.addItemDecoration(SpacingItemDecoration(spacingInPx))

        // FAB untuk tambah item
        binding.fabAdd.setOnClickListener {
            val dialog = ItemFormDialogFragment().apply {
                listener = this@MainActivity
            }
            dialog.show(supportFragmentManager, "ItemFormDialog")
        }

        // Fungsi DRAG FAB
        binding.fabAdd.setOnTouchListener(object : View.OnTouchListener {
            private var dX = 0f
            private var dY = 0f
            private var lastAction = 0

            override fun onTouch(view: View, event: MotionEvent): Boolean {
                return when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        dX = view.x - event.rawX
                        dY = view.y - event.rawY
                        lastAction = MotionEvent.ACTION_DOWN
                        true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        view.animate()
                            .x(event.rawX + dX)
                            .y(event.rawY + dY)
                            .setDuration(0)
                            .start()
                        lastAction = MotionEvent.ACTION_MOVE
                        true
                    }
                    MotionEvent.ACTION_UP -> {
                        if (lastAction == MotionEvent.ACTION_DOWN) {
                            view.performClick()
                        }
                        true
                    }
                    else -> false
                }
            }
        })

        // Tombol Refresh untuk memanggil API dan overwrite data lokal
        binding.btnRefresh.setOnClickListener {
            binding.swipeRefresh.isRefreshing = true
            mainViewModel.refreshItems(this)
        }

        // SwipeRefresh untuk refresh data
        binding.swipeRefresh.setOnRefreshListener {
            mainViewModel.refreshItems(this)
        }

        // Observasi LiveData items
        mainViewModel.items.observe(this) { items ->
            Log.d(TAG, "Items updated: ${items.size} item(s)")
            allItems = items
            filterItems(binding.etSearch.text.toString())
            binding.swipeRefresh.isRefreshing = false
        }

        // Observasi errorMessage
        mainViewModel.errorMessage.observe(this) { error ->
            if (!error.isNullOrEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        }

        // Observasi loading untuk overlay refresh di Main Screen
        mainViewModel.loading.observe(this) { isLoading ->
            binding.loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Fitur Search
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterItems(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Tombol Logout
        binding.btnLogout.setOnClickListener {
            PreferenceManager.clearToken(this)
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // Refresh data saat awal masuk Main Screen
        mainViewModel.refreshItems(this)
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.loadLocalItems(this)
    }

    private fun filterItems(query: String) {
        if (query.isBlank()) {
            adapter.submitList(allItems)
        } else {
            val filteredList = allItems.filter { item ->
                item.item_name.contains(query, ignoreCase = true)
                        || item.stock.toString().contains(query)
                        || item.unit.contains(query, ignoreCase = true)
            }
            adapter.submitList(filteredList)
        }
    }
}
