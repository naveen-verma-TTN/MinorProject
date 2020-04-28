package com.minorproject.cloudgallery.views.pages.category

import android.Manifest.permission.CAMERA
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.minorproject.cloudgallery.R
import com.minorproject.cloudgallery.model.Category
import com.minorproject.cloudgallery.model.Image
import com.minorproject.cloudgallery.repo.Compress
import com.minorproject.cloudgallery.viewmodels.CategoryViewModel
import com.minorproject.cloudgallery.views.adapters.CategoryPageDetailAdapter
import com.minorproject.cloudgallery.views.adapters.CategoryPageDetailItemClick
import com.stfalcon.imageviewer.StfalconImageViewer
import kotlinx.android.synthetic.main.fragment_category_detail_page.*
import kotlinx.android.synthetic.main.fragment_category_detail_page.view.*
import java.util.*
import kotlin.collections.ArrayList


@Suppress("DEPRECATION")
@RequiresApi(Build.VERSION_CODES.O)
class CategoryDetailPage : Fragment(), CategoryPageDetailItemClick {
    private lateinit var viewModel: CategoryViewModel
    private var isRotate = false
    private lateinit var adapter: CategoryPageDetailAdapter
    private var list: ArrayList<Image> = ArrayList()
    private lateinit var category: Category

    private var tracker: SelectionTracker<Long>? = null

    companion object {
        private const val TAG: String = "CategoryDetailPage"

        private const val REQUEST_SELECT_IMAGE_IN_ALBUM = 102
        private const val REQUEST_SELECT_IMAGE_IN_CAMERA = 101
        private const val PERMISSION_REQUEST_CODE = 110


        fun newInstance(category: Category): CategoryDetailPage {
            val fragment = CategoryDetailPage()
            val args = Bundle()
            args.putParcelable("CATEGORY", category)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!)
            .get(CategoryViewModel::class.java)

        tracker?.onRestoreInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        tracker?.onSaveInstanceState(outState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_category_detail_page, container, false)
        val toolbar: Toolbar = view.findViewById(R.id.toolbar) as Toolbar
        (activity as AppCompatActivity?)?.setSupportActionBar(toolbar)
        setHasOptionsMenu(true)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        category = arguments?.get("CATEGORY") as Category

        toolbar.title = category.CategoryName.toUpperCase(Locale.getDefault())

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

        viewModel.allCategories.observe(
            requireActivity(),
            Observer { category ->
                list = getImageList(category)!!
                toolbar.subtitle = list.size.toString()
                adapter.setList(list)
                adapter.notifyDataSetChanged()
            })


        home_detail_fab_gallery.setOnClickListener {
            selectImageInAlbum()
        }

        home_detail_fab_camera.setOnClickListener {
            if (checkPermission()) takePhotoFromCamera() else requestPermission()
        }

        tracker?.addObserver(
            object : SelectionTracker.SelectionObserver<Long>() {
                override fun onSelectionChanged() {
                    val nItems: Int? = tracker?.selection?.size()
                    if (nItems != null && nItems > 0) {

                        toolbar.navigationIcon = resources.getDrawable(R.drawable.close)

                        toolbar.setNavigationOnClickListener {
                            tracker!!.clearSelection()
                        }

                        toolbar.menu.findItem(R.id.delete).isVisible = true

                        toolbar.setOnMenuItemClickListener {
                            return@setOnMenuItemClickListener when (it.itemId) {
                                R.id.delete -> {
                                    tracker!!.selection.forEach { item ->
                                        Log.e(TAG, "Delete: $item")
                                    }
                                    tracker!!.clearSelection()
                                    true
                                }
                                else -> {
                                    false
                                }
                            }
                        }

                        toolbar.title = "$nItems items selected"
                        toolbar.setBackgroundDrawable(
                            ColorDrawable(getColor(view.context, R.color.colorPrimary))
                        )
                    } else {

                        toolbar.title = category.CategoryName.toUpperCase(Locale.getDefault())

                        toolbar.setNavigationIcon(R.drawable.back_button)

                        if (toolbar.menu.findItem(R.id.delete) != null)
                            toolbar.menu.findItem(R.id.delete).isVisible = false

                        toolbar.setNavigationOnClickListener {
                            activity!!.supportFragmentManager.popBackStack()
                        }
                        toolbar.setBackgroundDrawable(
                            ColorDrawable(getColor(view.context, R.color.colorAccent))
                        )
                    }
                }
            })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.detail_page_menu, menu)
    }


    private fun getImageList(categoryList: ArrayList<Category>?): ArrayList<Image>? {
        var list: ArrayList<Image>? = ArrayList()
        categoryList?.forEach { item ->
            if (item.CategoryName.equals(category.CategoryName, true) && item.ImagesList != null) {
                list = item.ImagesList
            }
        }
        return list
    }


    /**
     * Initialize the recycler View
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun initRecyclerView(view: View) {
        view.home_detail_recycler.layoutManager =
            GridLayoutManager(view.context, 3, RecyclerView.VERTICAL, false)
        adapter =
            CategoryPageDetailAdapter(
                list,
                this
            )

        home_detail_recycler.adapter = adapter

        tracker = SelectionTracker.Builder<Long>(
            "selection-1",
            view.home_detail_recycler,
            StableIdKeyProvider(view.home_detail_recycler),
            CategoryPageDetailAdapter.MyLookup(view.home_detail_recycler),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()
        adapter.setTracker(tracker)
    }

    override fun onItemClicked(imageUrl: String, position: Int) {
        if (!tracker!!.hasSelection()) {
            StfalconImageViewer.Builder(context, list) { view, image ->
                Glide.with(this).load(image.link).into(view)
            }.withStartPosition(position).withHiddenStatusBar(false).show()
        }
    }

    private fun selectImageInAlbum() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if (intent.resolveActivity(activity!!.packageManager) != null) {
            startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM)
        }
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_CAMERA)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SELECT_IMAGE_IN_ALBUM) {
            if (data != null) {
                val contentURI = data.data
                viewModel.saveImageToFireStore(contentURI, category.CategoryName)
            }

        } else if (requestCode == REQUEST_SELECT_IMAGE_IN_CAMERA) {

            val bitmap: Bitmap = data!!.extras!!.get("data") as Bitmap
            val contentURI: Uri =
                Compress.getImageUri(bitmap, "image_", "${System.currentTimeMillis()}.jpg")
            viewModel.saveImageToFireStore(contentURI, category.CategoryName)
        }

    }


    private fun checkPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(view?.context!!, CAMERA) ==
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            view?.context!!,
            WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermission() {
        requestPermissions(
            arrayOf(WRITE_EXTERNAL_STORAGE, CAMERA),
            PERMISSION_REQUEST_CODE
        )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {
                    takePhotoFromCamera()
                } else {
                    Toast.makeText(
                        activity!!,
                        getString(R.string.permission_denied_short_message),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
            else -> {
                Toast.makeText(
                    activity!!,
                    getString(R.string.permission_denied),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
