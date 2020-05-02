package com.minorProject.cloudGallery.views.fragments.category

import android.Manifest.permission.CAMERA
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.ImageView
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
import com.minorProject.cloudGallery.R
import com.minorProject.cloudGallery.model.bean.Category
import com.minorProject.cloudGallery.model.bean.Image
import com.minorProject.cloudGallery.model.repo.Compress
import com.minorProject.cloudGallery.util.ProgressDialog
import com.minorProject.cloudGallery.util.ViewAnimation
import com.minorProject.cloudGallery.viewModels.CategoriesViewModel
import com.minorProject.cloudGallery.views.adapters.CategoryPageDetailAdapter
import com.minorProject.cloudGallery.views.adapters.CategoryPageDetailItemClick
import com.stfalcon.imageviewer.StfalconImageViewer
import kotlinx.android.synthetic.main.f_category.*
import kotlinx.android.synthetic.main.f_category_detail_page.*
import kotlinx.android.synthetic.main.f_category_detail_page.view.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * CategoryDetailPage fragment
 */
@Suppress("DEPRECATION")
@RequiresApi(Build.VERSION_CODES.O)
class CategoryDetailPage : Fragment(), CategoryPageDetailItemClick {
    private var isMode = false
    private lateinit var categoriesViewModel: CategoriesViewModel
    private var isRotate = false
    private lateinit var adapter: CategoryPageDetailAdapter
    private var list: ArrayList<Image> = ArrayList()
    private lateinit var category: Category
    private lateinit var progressDialog: Dialog
    private var empty: ImageView? = null

    private var tracker: SelectionTracker<Long>? = null

    companion object {
        private const val REQUEST_SELECT_IMAGE_IN_ALBUM = 102
        private const val REQUEST_SELECT_IMAGE_IN_CAMERA = 101
        private const val PERMISSION_REQUEST_CODE = 110

        private val TAG = CategoriesViewModel::class.java.name

        // create new instance of AddCategory fragment
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
        categoriesViewModel = ViewModelProviders.of(requireActivity())
            .get(CategoriesViewModel::class.java)

        //Restore tracker instance
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
        //setting up toolbar
        val view: View = inflater.inflate(R.layout.f_category_detail_page, container, false)
        val toolbar: Toolbar = view.findViewById(R.id.toolbar) as Toolbar
        (activity as AppCompatActivity?)?.setSupportActionBar(toolbar)
        setHasOptionsMenu(true)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        category = arguments?.get("CATEGORY") as Category

        setUpToolbar(R.drawable.back_button, category.CategoryName, R.color.colorAccent, false) {
            requireActivity().supportFragmentManager.popBackStack()
        }

        setUpListeners()

        setUpFabAnimation()

        initRecyclerView(view)

        setUpObservers()
    }

    /**
     * fun for set up Listeners
     */
    private fun setUpListeners() {
        empty = view?.findViewById(R.id.empty_view_category_detail)
        progressDialog = ProgressDialog.progressDialog(requireView().context)
        home_detail_fab_gallery.setOnClickListener {
            selectImageInAlbum()
        }

        home_detail_fab_camera.setOnClickListener {
            if (checkPermission()) takePhotoFromCamera() else requestPermission()
        }
    }

    /**
     * fun to setup the Observers
     */
    private fun setUpObservers() {
        // tracker observer
        tracker?.addObserver(
            object : SelectionTracker.SelectionObserver<Long>() {
                override fun onSelectionChanged() {
                    isMode = true
                    val nItems: Int? = tracker?.selection?.size()
                    if (nItems != null && nItems > 0) {
                        if (toolbar != null) {
                            setUpToolbar(
                                R.drawable.close,
                                "$nItems items selected",
                                R.color.colorPrimary,
                                true
                            ) { tracker!!.clearSelection() }

                            toolbar.setOnMenuItemClickListener {
                                return@setOnMenuItemClickListener when (it.itemId) {
                                    R.id.delete -> {
                                        tracker!!.selection.forEach { item ->
                                            // deletion of image(s)
                                            categoriesViewModel.deleteImagesFromFirebase(list[item.toInt()])
                                        }
                                        tracker!!.clearSelection()
                                        true
                                    }
                                    else -> {
                                        false
                                    }
                                }
                            }
                        }
                    } else {
                        if (toolbar != null) {
                            if (toolbar.menu.findItem(R.id.delete) != null)
                                setUpToolbar(
                                    R.drawable.back_button,
                                    category.CategoryName,
                                    R.color.colorAccent,
                                    false
                                ) { activity!!.supportFragmentManager.popBackStack() }
                        }

                    }
                }
            })

        //Observer to observe allCategories and update recycler view list
        categoriesViewModel.getCategories().observe(
            requireActivity(),
            Observer { categories ->
                list = getCurrentCategoryItem(categories, category.CategoryName)
                updateView(list)
                if (toolbar != null)
                    toolbar.subtitle = list.size.toString()
                adapter.setList(list)
                adapter.notifyDataSetChanged()
            })
    }

    /**
     * fun to update the view
     */
    private fun updateView(images: ArrayList<Image>) {
        progressDialog.hide()
        if (images.size == 0) {
            empty?.visibility = View.VISIBLE
        } else {
            empty?.visibility = View.GONE
        }
    }

    /**
     *fun to get image list from category list
     **/
    private fun getCurrentCategoryItem(
        categoriesList: ArrayList<Category>,
        categoryName: String
    ): ArrayList<Image> {
        var result: ArrayList<Image> = ArrayList()
        categoriesList.forEach { item ->
            if (item.CategoryName.equals(categoryName, true) && item.ImagesList != null) {
                result = item.ImagesList
            }
        }
        return result
    }

    /**
     * Click callback from interface---to display image in fullscreen
     */
    override fun onItemClicked(imageUrl: String, position: Int) {
        if (isMode && tracker!!.selection.size() > 0) {
            return
        } else if (isMode && tracker!!.selection.size() == 0) {
            isMode = false
        } else if (!tracker!!.hasSelection() && !isMode) {
            StfalconImageViewer.Builder(context, list) { view, image ->
                Glide.with(this).load(image.link).into(view)
            }.withStartPosition(position).withHiddenStatusBar(false).show()
        }
    }


    /**
     * Initialize the recycler View
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun initRecyclerView(view: View) {
        progressDialog.show()
        view.home_detail_recycler.layoutManager =
            GridLayoutManager(view.context, 3, RecyclerView.VERTICAL, false)
        adapter =
            CategoryPageDetailAdapter(
                list,
                this
            )

        home_detail_recycler.adapter = adapter

        // Initialization tracker for multi-image selection
        tracker = SelectionTracker.Builder(
            "selection-1",
            view.home_detail_recycler,
            StableIdKeyProvider(view.home_detail_recycler),
            CategoryPageDetailAdapter.LookUp(view.home_detail_recycler),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()
        adapter.setTracker(tracker)
    }

    /**
     * fun to setupToolbar
     */
    private fun setUpToolbar(
        icon: Int,
        title: String,
        color: Int,
        visible: Boolean,
        function: () -> Unit
    ) {
        toolbar.title = title.toUpperCase(Locale.getDefault())

        toolbar.setBackgroundDrawable(
            ColorDrawable(getColor(requireView().context, color))
        )

        if (toolbar.menu.findItem(R.id.delete) != null)
            toolbar.menu.findItem(R.id.delete).isVisible = visible

        toolbar.setNavigationIcon(icon)

        toolbar.setNavigationOnClickListener {
            function.invoke()
        }
    }

    /**
     * Rotation animation of floating action button
     */
    private fun setUpFabAnimation() {

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
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.detail_page_menu, menu)
    }

    /**
     * select image from gallery
     */
    private fun selectImageInAlbum() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM)
        }
    }

    /**
     * select image from camera
     */
    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_CAMERA)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SELECT_IMAGE_IN_ALBUM) {
            if (data != null) {
                val contentURI = data.data
                categoriesViewModel.saveImageToFireStore(contentURI, category.CategoryName)
            }

        } else if (requestCode == REQUEST_SELECT_IMAGE_IN_CAMERA) {
            if (data != null) {
                val bitmap: Bitmap = data.extras!!.get("data") as Bitmap
                val contentURI: Uri =
                    Compress.getImageUri(bitmap, "image_", "${System.currentTimeMillis()}.jpg")
                categoriesViewModel.saveImageToFireStore(contentURI, category.CategoryName)
            }
        }
    }

    /**
     * fun to check for WRITE_EXTERNAL_STORAGE and CAMERA permission
     */
    private fun checkPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(view?.context!!, CAMERA) ==
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            view?.context!!,
            WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED)
    }

    /**
     * fun to Request for Permission
     */
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
                        view?.context,
                        getString(R.string.permission_denied_short_message),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
            else -> {
                Toast.makeText(
                    view?.context,
                    getString(R.string.permission_denied),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
