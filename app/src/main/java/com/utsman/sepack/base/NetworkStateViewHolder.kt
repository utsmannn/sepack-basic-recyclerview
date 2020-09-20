package com.utsman.sepack.base

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.utsman.sepack.data.state.NetworkState
import com.utsman.sepack.data.state.Status
import kotlinx.android.synthetic.main.item_loader.view.*

class NetworkStateViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bind(networkState: NetworkState?) {
        itemView.run {
            txt_message.text = networkState?.message

            txt_message.isVisible = networkState?.status == Status.FAILED
            progress_circular.isVisible = networkState?.status == Status.RUNNING
        }
    }
}