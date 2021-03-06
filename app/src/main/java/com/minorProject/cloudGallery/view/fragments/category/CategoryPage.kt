package com.minorProject.cloudGallery.view.fragments.category

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.minorProject.cloudGallery.R
import com.minorProject.cloudGallery.model.bean.Category
import com.minorProject.cloudGallery.view.adapters.CategoryPageAdapter
import com.minorProject.cloudGallery.view.adapters.CategoryPageItemClick
import com.minorProject.cloudGallery.viewModels.CategoriesViewModel
import com.minorProject.cloudGallery.viewModels.MyViewModelFactory
import kotlinx.android.synthetic.main.f_category.*
import kotlinx.android.synthetic.main.f_category.view.*

/**
 * CategoryPage fragment
 */
@RequiresApi(Build.VERSION_CODES.O)
class CategoryPage : Fragment(), CategoryPageItemClick {
    private lateinit var adapter: CategoryPageAdapter
    private var list: ArrayList<Category> = ArrayList()
    private var empty: ImageView? = null

    private val categoriesViewModel by lazy {
        ViewModelProvider(
            requireActivity(),
            MyViewModelFactory()
        ).get(CategoriesViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.f_category, container, false)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpListeners()

        initRecyclerView(view)

        setUpObservers()
    }


    /**
     * fun for set up Listeners
     */
    private fun setUpListeners() {
        empty = view?.findViewById(R.id.empty_view_category)
        home_fab.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.addToBackStack(null)
            val dialogFragment = AddCategory.newInstance(
                categoriesViewModel
            )
            dialogFragment.show(transaction, null)
        }
    }


    /**
     * Observer to observe allCategories to update recyclerview's category list
     */
    private fun setUpObservers() {
        categoriesViewModel.getCategories().observe(
            requireActivity(),
            Observer { category ->
                updateView(category)
                adapter.setList(category)
                adapter.notifyDataSetChanged()
            })

        categoriesViewModel.getProgressStatus().observe(requireActivity(),
            Observer { status ->
                when (status) {
                    CategoriesViewModel.Companion.ProgressStatus.HIDE_PROGRESS -> {
                        avi.hide()
                    }
                    CategoriesViewModel.Companion.ProgressStatus.SHOW_PROGRESS -> {
                        avi.show()
                    }
                    else -> {
                    }
                }
            })
    }


    /**
     * fun to update the view
     */
    private fun updateView(images: ArrayList<Category>) {
        if (images.size == 0) {
            empty?.visibility = View.VISIBLE
        } else {
            empty?.visibility = View.GONE
        }
    }

    /**
     * Initialize the recycler View
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun initRecyclerView(view: View) {
        val orientation = resources.configuration.orientation
        val span = if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            3
        } else {
            2
        }
        view.home_recycler.layoutManager =
            GridLayoutManager(view.context, span, RecyclerView.VERTICAL, false)
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

        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.animator.slide_in_right,
                R.animator.slide_out_left,
                R.animator.slide_in_left,
                R.animator.slide_out_right
            )
            .addToBackStack("CategoryDetailPage")
            .add(R.id.home_page_layout, categoryDetailPage).commit()
    }
}
