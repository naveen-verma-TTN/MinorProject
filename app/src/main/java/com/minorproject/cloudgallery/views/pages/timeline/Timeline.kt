package com.minorproject.cloudgallery.views.pages.timeline

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.minorproject.cloudgallery.R
import com.minorproject.cloudgallery.model.Category
import com.minorproject.cloudgallery.model.Image
import com.minorproject.cloudgallery.viewmodels.CategoryViewModel
import com.minorproject.cloudgallery.views.adapters.ImageAdapter
import com.minorproject.cloudgallery.views.adapters.ImageItemClickListener
import com.stfalcon.imageviewer.StfalconImageViewer
import xyz.sangcomz.stickytimelineview.RecyclerSectionItemDecoration
import xyz.sangcomz.stickytimelineview.TimeLineRecyclerView
import xyz.sangcomz.stickytimelineview.model.SectionInfo
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList


@Suppress("DEPRECATION")
class Timeline : Fragment(),
    ImageItemClickListener {
    /*ItemClickListener, RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {*/
    private var imageList: ArrayList<Image> = ArrayList()
    private var adapter: ImageAdapter? = null
    private lateinit var viewModel: CategoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!)
            .get(CategoryViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_timeline, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: TimeLineRecyclerView = view.findViewById(R.id.recycler_view)

/*        val itemTouchHelperCallback: ItemTouchHelper.SimpleCallback =
            RecyclerItemTouchHelper(
                0,
                ItemTouchHelper.RIGHT,
                this
            )
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView)*/


        recyclerView.layoutManager = LinearLayoutManager(
            view.context,
            LinearLayoutManager.VERTICAL,
            false
        )

        viewModel.allCategories.observe(
            requireActivity(),
            Observer { category ->
                //RemoveAll RecyclerSectionItemDecoration.SectionCallback
                recyclerView.removeDecoration(recyclerView)

                imageList = getImageList(category)!!

                sortList(imageList)
                //Add RecyclerSectionItemDecoration.SectionCallback
                recyclerView.addItemDecoration(getSectionCallback(imageList))

                adapter?.setList(imageList)
                adapter?.notifyDataSetChanged()
            })

        recyclerView.itemAnimator = DefaultItemAnimator()

        //Set Adapter
        adapter = ImageAdapter(
            imageList,
            this
        )
        recyclerView.adapter = adapter
    }


    private fun getImageList(categoryList: ArrayList<Category>?): ArrayList<Image>? {
        val list: ArrayList<Image>? = ArrayList()
        categoryList?.forEach { item ->
            if (item.ImagesList != null) {
                item.ImagesList.forEach { image ->
                    list!!.add(image)
                }
            }
        }
        return list
    }


    override fun onItemClicked(position: Int) {
        StfalconImageViewer.Builder<Image>(context, imageList) { view, image ->
            Glide.with(view!!.context).load(image.link).into(view)
        }.withStartPosition(position).withHiddenStatusBar(false).show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sortList(imageList: ArrayList<Image>) {
        imageList.sortWith(Comparator { o1: Image, o2: Image ->
            if (o1.uploadTime.toDate().toInstant().isAfter(o2.uploadTime.toDate().toInstant())) {
                -1
            } else {
                1
            }
        })
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
                            imageList[position].uploadTime.toDate().toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        ),
                        imageList[position].category.toUpperCase(Locale.getDefault()),
                        ContextCompat.getDrawable(view!!.context, R.drawable.icon)
                    )
                return null
            }

            @RequiresApi(Build.VERSION_CODES.O)
            fun dateFormatting(date: LocalDate): String {
                val formatter = DateTimeFormatter.ofPattern("dd MMM, yyyy")
                return date.format(formatter)
            }
        }
    }

/*    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int, position: Int) {
        Toast.makeText(view?.context, "Delete ${imageList[position].category}", Toast.LENGTH_LONG)
            .show()
        imageList.removeAt(position)
        adapter.notifyDataSetChanged()
    }*/
}

private fun TimeLineRecyclerView.removeDecoration(recyclerView: RecyclerView) {
    while (recyclerView.itemDecorationCount > 0) {
        recyclerView.removeItemDecorationAt(0)
    }
}
