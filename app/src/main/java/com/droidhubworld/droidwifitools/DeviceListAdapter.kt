package com.droidhubworld.droidwifitools

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DeviceListAdapter : RecyclerView.Adapter<BaseViewHolder>() {
    var listener: OnClickListener? = null
    var mItems = mutableListOf<String>()

    interface OnClickListener {
        fun onItemClick(position: Int, data: String)
    }
    fun setOnClickListener(l: OnClickListener) {
        listener = l
    }

    fun clear() {
        mItems.clear()
        notifyDataSetChanged()
    }

    fun setContent(list: MutableList<String>) {
        mItems = list
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int {
        return mItems.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position, mItems[position])

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.device_list_item, parent, false)

        return ItemViewHolder(view)
    }

    inner class ItemViewHolder(var binding: View) : BaseViewHolder(binding) {

        override fun onBind(position: Int, item: Any) {
            val textView = binding.findViewById(R.id.label) as TextView

            textView.text = item.toString()
        }
    }
}