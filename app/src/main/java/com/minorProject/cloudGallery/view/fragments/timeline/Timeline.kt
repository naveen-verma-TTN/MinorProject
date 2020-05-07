package com.minorProject.cloudGallery.view.fragments.timeline

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.minorProject.cloudGallery.R
import com.minorProject.cloudGallery.model.bean.Image
import com.minorProject.cloudGallery.util.ProgressDialog
import com.minorProject.cloudGallery.view.adapters.ImageAdapter
import com.minorProject.cloudGallery.view.adapters.ImageItemClickListener
import com.minorProject.cloudGallery.viewModels.CategoriesViewModel
import com.minorProject.cloudGallery.viewModels.MyViewModelFactory
import com.stfalcon.imageviewer.StfalconImageViewer
import xyz.sangcomz.stickytimelineview.RecyclerSectionItemDecoration
import xyz.sangcomz.stickytimelineview.TimeLineRecyclerView
import xyz.sangcomz.stickytimelineview.model.SectionInfo
import java.util.*
import kotlin.collections.ArrayList

/**
 * TimeLine Fragment
 */
@Suppress("DEPRECATION")
class Timeline : Fragment(),
    ImageItemClickListener {
    //---------------------For future-use
    /*ItemClickListener, RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {*/
    private var imageList: ArrayList<Image> = ArrayList()
    private var adapter: ImageAdapter? = null
    private lateinit var recyclerView: TimeLineRecyclerView
    private lateinit var progressDialog: Dialog
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
        return inflater.inflate(R.layout.f_timeline, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()

        initRecyclerView()

        setUpObservers()
    }

    /**
     * fun for set up Listeners
     */
    private fun initListeners() {
        progressDialog = ProgressDialog.progressDialog(requireView().context)
        empty = view?.findViewById(R.id.empty_view_timeline)
    }

    private fun initRecyclerView() {
        progressDialog.show()
        recyclerView = requireView().findViewById(R.id.recycler_view)
        //---------------------For future-use
/*        val itemTouchHelperCallback: ItemTouchHelper.SimpleCallback =
            RecyclerItemTouchHelper(
                0,
                ItemTouchHelper.RIGHT,
                this
            )
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView)*/
        recyclerView.layoutManager = LinearLayoutManager(
            view?.context,
            LinearLayoutManager.VERTICAL,
            false
        )
        recyclerView.itemAnimator = DefaultItemAnimator()
        //Set Adapter
        adapter = ImageAdapter(
            imageList,
            this
        )
        recyclerView.adapter = adapter
    }

    /**
     * fun for setting up observers
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpObservers() {
        categoriesViewModel.getImageList().observe(
            requireActivity(),
            Observer { images ->
                //RemoveAll RecyclerSectionItemDecoration.SectionCallback
                recyclerView.removeDecoration(recyclerView)

                imageList = images

                updateView(images)

                //Add RecyclerSectionItemDecoration.SectionCallback
                recyclerView.addItemDecoration(getSectionCallback(imageList))

                adapter?.setList(imageList)
                adapter?.notifyDataSetChanged()
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
     * fun to show the images in fullscreen
     */
    override fun onItemClicked(position: Int) {
        StfalconImageViewer.Builder(context, imageList) { _view, image ->
            Glide.with(requireView().context).load(image.link).into(_view)
        }.withStartPosition(position).withHiddenStatusBar(false)
            .show()
    }


    //Get SectionCallback method
    private fun getSectionCallback(imageList: List<Image>): RecyclerSectionItemDecoration.SectionCallback {
        return object : RecyclerSectionItemDecoration.SectionCallback {
            //In your data, implement a method to determine if this is a section.
            override fun isSection(position: Int): Boolean {
                if (imageList.isNotEmpty()) {
                    return imageList[position].uploadTime.toDate().day != imageList[position - 1].uploadTime.toDate().day
                }
                return false
            }

            //Implement a method that returns a SectionHeader.
            @RequiresApi(Build.VERSION_CODES.O)
            override fun getSectionHeader(position: Int): SectionInfo? {
                if (imageList.isNotEmpty())
                    return SectionInfo(
                        dateFormatting(
                            imageList[position].uploadTime.toDate()
                        ),
                        imageList[position].category.toUpperCase(Locale.getDefault()),
                        ContextCompat.getDrawable(view!!.context, R.drawable.icon)
                    )
                return null
            }

            //fun to Formatting the date (eg. 17 Jan, 2020)
            @SuppressLint("SimpleDateFormat")
            @RequiresApi(Build.VERSION_CODES.O)
            fun dateFormatting(date: Date): String {
                return DateFormat.format("dd MMM, yyyy", date).toString()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        progressDialog.dismiss()
    }

    //---------------------For future-use
/*    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int, position: Int) {
        Toast.makeText(view?.context, "Delete ${imageList[position].category}", Toast.LENGTH_LONG)
            .show()
        imageList.removeAt(position)
        adapter.notifyDataSetChanged()
    }*/
}

// Remove all decoration from recycler view
private fun TimeLineRecyclerView.removeDecoration(recyclerView: RecyclerView) {
    while (recyclerView.itemDecorationCount > 0) {
        recyclerView.removeItemDecorationAt(0)
    }
}
