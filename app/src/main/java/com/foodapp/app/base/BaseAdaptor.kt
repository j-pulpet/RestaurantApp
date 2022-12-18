package com.foodapp.app.base


import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.util.*

abstract class BaseAdaptor<T>(private val context: Activity, private var items: ArrayList<T>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    abstract fun onBindData(holder: RecyclerView.ViewHolder?, `val`: T, position: Int)
    abstract fun setItemLayout(): Int

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(setItemLayout(), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        onBindData(holder, items[position], position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    internal inner class ViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView)
}