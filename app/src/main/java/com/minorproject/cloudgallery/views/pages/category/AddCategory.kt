package com.minorproject.cloudgallery.views.pages.category

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.minorproject.cloudgallery.R
import com.minorproject.cloudgallery.viewmodels.CategoryViewModel
import kotlinx.android.synthetic.main.fragment_add_category.*
import kotlinx.android.synthetic.main.fragment_add_category.view.*
import kotlinx.android.synthetic.main.fragment_add_category.view.category_name


class AddCategory : DialogFragment(), View.OnClickListener {

    companion object {
        private lateinit var viewModel: CategoryViewModel

        @JvmStatic
        fun newInstance(
            _viewModel: CategoryViewModel
        ) =
            AddCategory()
                .apply {
                    viewModel = _viewModel
                }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.cancel.setOnClickListener(this)
        view.add.setOnClickListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.cancel -> this.dismiss()
            R.id.add -> {
                when {
                    TextUtils.isEmpty(category_name.text) -> {
                        Toast.makeText(
                            v.context,
                            "Category name can't be empty.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    containValue(category_name.text.toString()) -> {
                        Toast.makeText(
                            v.context,
                            "Category name already exist.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    else -> {
                        val categoryName = view!!.category_name.text.toString()
                        viewModel.insertCategory(categoryName)
                        this.dismiss()
                    }
                }

            }
        }
    }

    private fun containValue(text: String): Boolean {
        var flag = false
        viewModel.allCategories.value?.forEach { it ->
            if(it.CategoryName.equals(text.trim(),true)){
                flag = true
            }
        }
        return flag
    }
}

