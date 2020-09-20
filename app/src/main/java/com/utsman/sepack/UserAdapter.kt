package com.utsman.sepack

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.utsman.sepack.base.BaseAdapter
import com.utsman.sepack.data.model.Reqres
import com.utsman.sepack.ext.inflate
import com.utsman.sepack.ext.loadUrl
import kotlinx.android.synthetic.main.item_user.view.*

class UserAdapter : BaseAdapter<Reqres.User>() {
    override fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return UserViewHolder(parent.inflate(R.layout.item_user))
    }

    override fun enableAnimation(): Boolean {
        return true
    }

    override fun bindHolder(holder: RecyclerView.ViewHolder, item: Reqres.User, position: Int) {
        (holder as UserViewHolder).bind(item)
    }

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(user: Reqres.User) = itemView.run {
            val name = "${user.firstName} ${user.lastName}"
            val email = user.email
            val avatar = user.avatar

            txt_user_name.text = name
            txt_user_email.text = email
            img_user.loadUrl(avatar, user.id.toString())
        }
    }
}