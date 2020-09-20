package com.utsman.sepack.base

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.utsman.sepack.R
import com.utsman.sepack.data.state.NetworkState

abstract class BaseAdapter<T> : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val list: MutableList<T> = mutableListOf()
    lateinit var context: Context
    lateinit var parent: ViewGroup
    private val isHolderRecyclable = false

    val ITEM_TYPE = 1
    private val NETWORK_TYPE = 2

    private var networkState: NetworkState? = null
    private var onAttach = false

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        onAttach = false
        super.onAttachedToRecyclerView(recyclerView)
    }

    fun addList(list: List<T>, listResult: ((MutableList<T>) -> Unit)? = null) {
        val diffCallback = DiffCallback(this.list, list)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.list.addAll(list)
        listResult?.invoke(this.list)
        diffResult.dispatchUpdatesTo(this)
    }

    fun addItem(item: T, listResult: ((MutableList<T>) -> Unit)? = null) {
        val newList = listOf(item)
        val diffCallback = DiffCallback(this.list, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.list.addAll(newList)
        listResult?.invoke(this.list)
        diffResult.dispatchUpdatesTo(this)
    }

    fun updateList(list: List<T>, listResult: ((MutableList<T>) -> Unit)? = null) {
        val diffCallback = DiffCallback(this.list, list)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.list.clear()
        this.list.addAll(list)
        listResult?.invoke(this.list)
        diffResult.dispatchUpdatesTo(this)
    }

    fun filterList(newList: List<T>, whenFilter: (() -> Unit)? = null) {
        val diffCallback = DiffCallback(this.list, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.list.clear()
        this.list.addAll(newList)
        whenFilter?.invoke()
        diffResult.dispatchUpdatesTo(this)
    }

    fun removeList(list: List<T>) {
        val diffCallback = DiffCallback(this.list, list)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.list.removeAll(list)
        diffResult.dispatchUpdatesTo(this)
    }

    fun removeItem(item: T?) {
        val singleList = listOf(item)
        val diffCallback = DiffCallback(this.list, singleList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.list.removeAll(singleList)
        diffResult.dispatchUpdatesTo(this)
    }

    fun removePosition(position: Int) {
        val listRemoved = this.list
        listRemoved.removeAt(position)
        val diffCallback = DiffCallback(this.list, listRemoved)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        diffResult.dispatchUpdatesTo(this)
    }

    private fun setAnimation(itemView: View, originPosition: Int) {
        var position = originPosition
        if (!onAttach) {
            position = -1
        }
        val isNotFirstItem = position == -1
        position += 1
        itemView.alpha = 0f
        val animatorSet = AnimatorSet()
        val animator = ObjectAnimator.ofFloat(itemView, "alpha", 0f, 0.5f, 1.0f)
        ObjectAnimator.ofFloat(itemView, "alpha", 0f).start()
        animator.startDelay = if (isNotFirstItem) 200 / 2 else position.toLong() * 200 / 3
        animator.duration = 200
        animatorSet.play(animator)
        animator.start()
    }

    fun clearList() {
        this.list.clear()
        notifyDataSetChanged()
    }

    fun updateNetworkState(newNetworkState: NetworkState) {
        val previousState = this.networkState
        val hadExtraRow = hasExtraRow()
        this.networkState = newNetworkState
        val hasExtraRow = hasExtraRow()
        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                try {
                    notifyItemRemoved(itemCount)
                } catch (e: IllegalStateException) {
                    notifyItemRemoved(itemCount-1)
                } catch (e: IndexOutOfBoundsException) {
                    e.printStackTrace()
                }
            } else {
                try {
                    notifyItemInserted(itemCount)
                } catch (e: IllegalStateException) {
                    notifyItemRemoved(itemCount-1)
                } catch (e: IndexOutOfBoundsException) {
                    e.printStackTrace()
                }
            }
        } else if (hasExtraRow && previousState != newNetworkState) {
            notifyItemChanged(itemCount - 1)
        }
    }

    abstract fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder
    abstract fun enableAnimation(): Boolean
    abstract fun bindHolder(holder: RecyclerView.ViewHolder, item: T, position: Int)

    open fun useViewType(): Boolean {
        return false
    }

    open fun createViewHolderViewType(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        this.parent = parent
        return if (viewType == ITEM_TYPE) {
            createViewHolder(parent)
        } else {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_loader, parent, false)
            NetworkStateViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return when (networkState) {
            null -> list.size
            else -> list.size + if (hasExtraRow()) 1 else 0
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1 && networkState != null)
            NETWORK_TYPE
        else
            ITEM_TYPE
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == ITEM_TYPE) {
            val item = list[position]
            if (item != null) {
                bindHolder(holder, item, position)
                if (enableAnimation()) {
                    setAnimation(holder.itemView, position)
                }
            }
        } else {
            (holder as NetworkStateViewHolder).bind(networkState)
        }
    }

    private fun hasExtraRow(): Boolean {
        return networkState != null && networkState != NetworkState.LOADED
    }

    fun fixGridShimmerSpan(gridLayoutManager: GridLayoutManager, column: Int) {
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val viewTypes = getItemViewType(position)
                return if (viewTypes == ITEM_TYPE) 1 else column
            }
        }
    }

    class DiffCallback<T>(private val oldList: List<T>, private val newList: List<T>) :
        DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] === newList[newItemPosition]
        }

        override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
            val s = oldList[oldPosition]
            val d = newList[newPosition]
            return d == s
        }

        @Nullable
        override fun getChangePayload(oldPosition: Int, newPosition: Int): Any? {
            return super.getChangePayload(oldPosition, newPosition)
        }
    }
}