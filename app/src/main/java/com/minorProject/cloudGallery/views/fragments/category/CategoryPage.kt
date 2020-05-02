package com.minorProject.cloudGallery.views.fragments.category

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.minorProject.cloudGallery.R
import com.minorProject.cloudGallery.model.bean.Category
import com.minorProject.cloudGallery.util.ProgressDialog
import com.minorProject.cloudGallery.viewModels.CategoriesViewModel
import com.minorProject.cloudGallery.views.adapters.CategoryPageAdapter
import com.minorProject.cloudGallery.views.adapters.CategoryPageItemClick
import kotlinx.android.synthetic.main.f_category.*
import kotlinx.android.synthetic.main.f_category.view.*
import kotlinx.android.synthetic.main.f_timeline.*

/**
 * CategoryPage fragment
 */
@RequiresApi(Build.VERSION_CODES.O)
class CategoryPage : Fragment(), CategoryPageItemClick {
    private lateinit var categoriesViewModel: CategoriesViewModel
    private lateinit var adapter: CategoryPageAdapter
    private var list: ArrayList<Category> = ArrayList()
    private lateinit var progressDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        categoriesViewModel = ViewModelProviders.of(requireActivity())
            .get(CategoriesViewModel::class.java)
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
        progressDialog  = ProgressDialog.progressDialog(requireView().context)
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
                progressDialog.hide()
                if(category.size == 0) {
                    empty_view.visibility = View.VISIBLE
                }
                adapter.setList(category)
                adapter.notifyDataSetChanged()
            })
    }


    /**
     * Initialize the recycler View
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun initRecyclerView(view: View) {
        progressDialog.show()
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
        requireActivity().supportFragmentManager.beginTransaction().addToBackStack("CategoryDetailPage")
            .add(R.id.home_layout, categoryDetailPage).commit()
    }
}
