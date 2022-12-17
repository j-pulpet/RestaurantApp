package com.foodapp.app.adaptor

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.foodapp.app.R
import com.foodapp.app.activity.ImageSliderActivity
import java.util.*

class ImageSliderAdaptor(var context: Activity, private val arrayList: ArrayList<*>) : PagerAdapter() {
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view==`object`
    }

    override fun getCount(): Int {
        return arrayList.size
    }

    override fun instantiateItem(view: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(context)
        val itemView = inflater.inflate(R.layout.row_viewpager_item, view, false) as ViewGroup
        val mImageView = itemView.findViewById<View>(R.id.img_pager) as ImageView
        Glide.with(context)
            .load(arrayList[position])
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .into(mImageView)
        mImageView.setOnClickListener {
            context.startActivity(Intent(context,ImageSliderActivity::class.java).putExtra("imageList",arrayList))
        }
        (view as ViewPager).addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        (container as ViewPager).removeView(`object` as View?)
    }

}