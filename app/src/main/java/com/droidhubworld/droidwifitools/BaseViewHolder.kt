package com.droidhubworld.droidwifitools

import android.view.View
import androidx.recyclerview.widget.RecyclerView


/**
 * @Author: Anand Patel
 * @Date: 25,January,2024
 * @Email: anandkumara30@gmail.com
 */
abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    abstract fun onBind(position: Int, item: Any)

}