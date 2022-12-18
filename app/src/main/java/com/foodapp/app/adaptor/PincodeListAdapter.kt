package com.foodapp.app.adaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.foodapp.app.R
import kotlinx.android.synthetic.main.row_pincode_list.view.*

class PinCodeListAdapter(val pinCodeArray: ArrayList<String>, val listener: (String, Int) -> Unit) :
        RecyclerView.Adapter<PinCodeListAdapter.PinCodeViewHolder>() {


    inner class PinCodeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(pinCode: String, listener: (String, Int) -> Unit, position: Int) =
                with(itemView) {
                    tvPinCode.text = pinCode
                    itemView.setOnClickListener {
                        listener("ItemClick", position)
                    }
                }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PinCodeViewHolder {
        val view =
                LayoutInflater.from(parent.context).inflate(R.layout.row_pincode_list, parent, false)
        return PinCodeViewHolder(view)
    }

    override fun onBindViewHolder(holder: PinCodeViewHolder, position: Int) {
        holder.bindItems(pinCodeArray[position], listener, position)
    }

    override fun getItemCount(): Int {
        return pinCodeArray.size
    }
}