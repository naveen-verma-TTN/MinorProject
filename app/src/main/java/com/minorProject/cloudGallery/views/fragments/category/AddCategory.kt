package com.minorProject.cloudGallery.views.fragments.category

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.minorProject.cloudGallery.R
import com.minorProject.cloudGallery.viewModels.CategoriesViewModel
import kotlinx.android.synthetic.main.d_add_category.*
import kotlinx.android.synthetic.main.d_add_category.view.*

/**
 * AddCategory DialogFragment
 */
class AddCategory : DialogFragment(), View.OnClickListener {
    companion object {
        private lateinit var categoriesViewModel: CategoriesViewModel

        // fun to create new instance of AddCategory DialogFragment
        @JvmStatic
        fun newInstance(_viewModel: CategoriesViewModel) = AddCategory()
            .apply {
                categoriesViewModel = _viewModel
                }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.d_add_category, container, false)
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
                        val categoryName = requireView().category_name.text.toString()
                        categoriesViewModel.createCategory(categoryName)
                        this.dismiss()
                    }
                }

            }
        }
    }

    /**
     * fun to see if the same name category exist or not
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun containValue(text: String): Boolean {
        var flag = false
        categoriesViewModel.getCategories().value?.forEach { it ->
            if(it.CategoryName.trim().equals(text.trim(),true)){
                flag = true
            }
        }
        return flag
    }
}

