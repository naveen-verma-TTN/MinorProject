package com.minorproject.cloudgallery.views.pages.home

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.minorproject.cloudgallery.R
import com.minorproject.cloudgallery.model.Category
import com.minorproject.cloudgallery.viewmodels.CategoryViewModel
import com.minorproject.cloudgallery.views.adapters.HomeDetailAdapter
import com.minorproject.cloudgallery.views.adapters.HomeRecyclerAdapter
import com.minorproject.cloudgallery.views.interfaces.HomeItemClick
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.home_recycler

@RequiresApi(Build.VERSION_CODES.O)
class Home : Fragment(), HomeItemClick {
    private lateinit var viewModel: CategoryViewModel
    private lateinit var adapter: HomeRecyclerAdapter
    private var list: ArrayList<Category> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView(view)

        home_fab.setOnClickListener {
            val transaction = activity!!.supportFragmentManager.beginTransaction()
            transaction.addToBackStack(null)
            val dialogFragment = AddCategory.newInstance(
                viewModel
            )
            dialogFragment.show(transaction, null)
        }

        viewModel = ViewModelProviders.of(this)
            .get(CategoryViewModel::class.java)

        viewModel.allCategories.observe(
            requireActivity(),
            Observer { category ->
                adapter.setList(category)
                adapter.notifyDataSetChanged()
            })
    }


    /**
     * Initialize the recycler View
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun initRecyclerView(view: View) {
        view.home_recycler.layoutManager =
            GridLayoutManager(view.context, 2, RecyclerView.VERTICAL, false)
        adapter =
            HomeRecyclerAdapter(
                list,
                this
            )
        home_recycler.adapter = adapter
    }



    override fun onItemClicked(category: Category, position: Int) {
       val categoryDetailPage = CategoryDetailPage.newInstance(category)
        activity!!.supportFragmentManager.beginTransaction().addToBackStack("CategoryDetailPage")
            .add(R.id.home_layout, categoryDetailPage).commit()
    }
}