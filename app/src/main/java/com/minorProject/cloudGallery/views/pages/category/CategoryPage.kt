package com.minorProject.cloudGallery.views.pages.category

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

import com.minorProject.cloudGallery.R
import com.minorProject.cloudGallery.model.Category
import com.minorProject.cloudGallery.viewModels.CategoryViewModel
import com.minorProject.cloudGallery.views.adapters.CategoryPageItemClick
import com.minorProject.cloudGallery.views.adapters.CategoryPageAdapter
import kotlinx.android.synthetic.main.fragment_category.*
import kotlinx.android.synthetic.main.fragment_category.view.home_recycler

/**
 * CategoryPage fragment
 */
@RequiresApi(Build.VERSION_CODES.O)
class CategoryPage : Fragment(), CategoryPageItemClick {
    private lateinit var viewModel: CategoryViewModel
    private lateinit var adapter: CategoryPageAdapter
    private var list: ArrayList<Category> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!)
            .get(CategoryViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_category, container, false)
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

        //Observer to observe allCategories to update recyclerview's category list
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
            CategoryPageAdapter(
                list,
                this
            )
        home_recycler.adapter = adapter
    }

    /**
     * fun callback to open specific category fragment
     */
    override fun onItemClicked(category: Category, position: Int) {
        val categoryDetailPage = CategoryDetailPage.newInstance(category)
        activity!!.supportFragmentManager.beginTransaction().addToBackStack("CategoryDetailPage")
            .add(R.id.home_layout, categoryDetailPage).commit()
    }
}
