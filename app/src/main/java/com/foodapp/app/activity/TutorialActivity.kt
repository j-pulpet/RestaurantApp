package com.foodapp.app.activity

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.foodapp.app.R
import com.foodapp.app.base.BaseActivity
import com.foodapp.app.utils.Common
import com.foodapp.app.utils.SharePreference.Companion.isTutorial
import com.foodapp.app.utils.SharePreference.Companion.setBooleanPref
import kotlinx.android.synthetic.main.activity_tutorial.*

class TutorialActivity : BaseActivity() {
    var imagelist: ArrayList<Drawable>? = null
    override fun setLayout(): Int {
        return R.layout.activity_tutorial
    }

    override fun InitView() {
        Common.getCurrentLanguage(this@TutorialActivity, false)
        imagelist = ArrayList()
        imagelist!!.add(ResourcesCompat.getDrawable(resources, R.drawable.ic_pageone, null)!!)
        imagelist!!.add(ResourcesCompat.getDrawable(resources, R.drawable.ic_pagetwo, null)!!)
        imagelist!!.add(ResourcesCompat.getDrawable(resources, R.drawable.ic_pagethree, null)!!)
        viewPager.adapter = StartScreenAdapter(this@TutorialActivity, imagelist!!)
        tabLayout.setupWithViewPager(viewPager, true)

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(i: Int, v: Float, i1: Int) {}
            override fun onPageSelected(i: Int) {
                if (i == imagelist!!.size - 1) {
                    tvBtnSkip.text = resources.getString(R.string.start_)
                } else {
                    tvBtnSkip.text = resources.getString(R.string.skip)
                }
            }

            override fun onPageScrollStateChanged(i: Int) {}
        })

        tvBtnSkip.setOnClickListener {
            setBooleanPref(this@TutorialActivity, isTutorial, true)
            openActivity(DashboardActivity::class.java)
            finish()
        }
    }

    class StartScreenAdapter(var mContext: Context, var mImagelist: ArrayList<Drawable>) : PagerAdapter() {
        @SuppressLint("SetTextI18n")
        override fun instantiateItem(collection: ViewGroup, position: Int): Any {
            val inflater = LayoutInflater.from(mContext)
            val layout = inflater.inflate(R.layout.row_tutorial, collection, false) as ViewGroup
            val iv: ImageView = layout.findViewById(R.id.ivScreen)
            iv.setImageDrawable(mImagelist[position])
            collection.addView(layout)
            return layout
        }

        override fun destroyItem(
                collection: ViewGroup,
                position: Int,
                view: Any
        ) {
            collection.removeView(view as View)
        }

        override fun getCount(): Int {
            return mImagelist.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }
    }

    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(this@TutorialActivity, false)
    }
}