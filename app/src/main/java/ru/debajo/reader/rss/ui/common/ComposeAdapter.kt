package ru.debajo.reader.rss.ui.common

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*

class ComposeAdapter<T>(
    private val keyEquality: ((old: T, new: T) -> Boolean)? = null,
    private val contentEquality: (old: T, new: T) -> Boolean = { old, new -> old == new },
) : RecyclerView.Adapter<ComposeAdapter.ViewHolder<T>>() {

    private var coroutineScope: CoroutineScope? = null
    private var currentCalculation: Job? = null

    var content: @Composable (T) -> Unit = {}
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var items: List<T> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            val oldValue = field
            field = value
            val coroutineScope = coroutineScope
            currentCalculation?.cancel()
            if (oldValue.isEmpty() || value.isEmpty() || coroutineScope == null || keyEquality == null) {
                notifyDataSetChanged()
            } else {
                currentCalculation = coroutineScope.launch {
                    calculateDiff(oldValue, value)
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<T> {
        return ViewHolder(
            view = ComposeView(parent.context),
            content = { content(it) },
        )
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        coroutineScope = CoroutineScope(Dispatchers.Main)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        coroutineScope?.cancel()
        coroutineScope = null
        currentCalculation = null
    }

    override fun onBindViewHolder(holder: ViewHolder<T>, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    private suspend fun calculateDiff(oldValue: List<T>, newValue: List<T>) {
        val result = withContext(Dispatchers.Default) {
            DiffUtil.calculateDiff(createCallback(oldValue, newValue))
        }
        result.dispatchUpdatesTo(this)
    }

    private fun createCallback(oldValue: List<T>, newValue: List<T>): DiffUtil.Callback {
        return object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = oldValue.size

            override fun getNewListSize(): Int = newValue.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return keyEquality?.invoke(oldValue[oldItemPosition], newValue[newItemPosition]) == true
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return contentEquality(oldValue[oldItemPosition], newValue[newItemPosition])
            }
        }
    }

    class ViewHolder<T>(
        view: ComposeView,
        content: @Composable (T) -> Unit,
    ) : RecyclerView.ViewHolder(view) {

        private val state: MutableState<T?> = mutableStateOf(null)

        init {
            view.setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool
            )

            view.setContent {
                val article by state
                article?.let { content(it) }
            }
        }

        fun bind(item: T) {
            state.value = item
        }
    }
}