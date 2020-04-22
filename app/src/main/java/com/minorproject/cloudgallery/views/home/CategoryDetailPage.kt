package com.minorproject.cloudgallery.views.home

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.minorproject.cloudgallery.R
import com.minorproject.cloudgallery.model.Category
import com.minorproject.cloudgallery.model.Image
import com.minorproject.cloudgallery.viewmodels.CategoryViewModel
import com.minorproject.cloudgallery.views.adapters.HomeRecyclerAdapter
import com.minorproject.cloudgallery.views.interfaces.HomeItemClick
import com.stfalcon.imageviewer.StfalconImageViewer
import kotlinx.android.synthetic.main.fragment_category_detail_page.*
import kotlinx.android.synthetic.main.fragment_category_detail_page.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_home.view.home_recycler
import org.jetbrains.anko.view
import java.util.*
import kotlin.collections.ArrayList

@RequiresApi(Build.VERSION_CODES.O)
class CategoryDetailPage : Fragment(), HomeItemClick {
    private lateinit var viewModel: CategoryViewModel
    private var isRotate = false
    private lateinit var adapter: HomeDetailAdapter
    private var list: ArrayList<Category> = ArrayList()
    private lateinit var category: Category

    companion object {
        fun newInstance(category: Category): CategoryDetailPage {
            val fragment = CategoryDetailPage()
            val args = Bundle()
            args.putParcelable("CATEGORY", category)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_category_detail_page, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        category = arguments?.get("CATEGORY") as Category

        toolbar.title = category.CategoryName.toUpperCase(Locale.getDefault())
        toolbar.subtitle = category.ListImage?.size.toString()

        toolbar.setNavigationIcon(R.drawable.back_button)

        toolbar.setNavigationOnClickListener {
                activity!!.supportFragmentManager.popBackStack()
        }

        initRecyclerView(view)

        ViewAnimation.init(home_detail_fab_camera)
        ViewAnimation.init(home_detail_fab_gallery)

        home_detail_fab_cloud.setOnClickListener { v ->
            isRotate = ViewAnimation.rotateFab(v, !isRotate)
            if (isRotate) {
                ViewAnimation.showIn(home_detail_fab_camera)
                ViewAnimation.showIn(home_detail_fab_gallery)
            } else {
                ViewAnimation.showOut(home_detail_fab_camera)
                ViewAnimation.showOut(home_detail_fab_gallery)
            }
        }

        viewModel = ViewModelProviders.of(this)
            .get(CategoryViewModel::class.java)

        viewModel.allCategories.observe(
            requireActivity(),
            Observer { category ->
                list = category
                adapter.setList(category)
                adapter.notifyDataSetChanged()
            })
    }

    /**
     * Initialize the recycler View
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun initRecyclerView(view: View) {
        view.home_detail_recycler.layoutManager =
            GridLayoutManager(view.context, 4, RecyclerView.VERTICAL, false)
        adapter =
            HomeDetailAdapter(
                list,
                this
            )
        home_detail_recycler.adapter = adapter
    }

    override fun onItemClicked(category: Category, position: Int) {
        Log.e("image", category.CategoryName)
        StfalconImageViewer.Builder<Category>(context, list) { view, image ->
            Glide.with(this).load(image.CategoryThumbLink).into(view)
        }.withStartPosition(position).withHiddenStatusBar(false).show()
    }
}
