package com.minorproject.cloudgallery.screens.timeline

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.minorproject.cloudgallery.R
import com.minorproject.cloudgallery.components.RecyclerItemTouchHelper
import com.minorproject.cloudgallery.interfaces.ItemClickListener
import com.minorproject.cloudgallery.model.Image
import com.squareup.picasso.Picasso
import com.stfalcon.imageviewer.StfalconImageViewer
import kotlinx.android.synthetic.main.main_page_layout.*
import xyz.sangcomz.stickytimelineview.RecyclerSectionItemDecoration
import xyz.sangcomz.stickytimelineview.TimeLineRecyclerView
import xyz.sangcomz.stickytimelineview.model.SectionInfo
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class Timeline : Fragment(),
    ItemClickListener, RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    private lateinit var navController: NavController
    private lateinit var imageList: ArrayList<Image>
    private lateinit var adapter: ImageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_timeline, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        activity!!.menu.setOnItemSelectedListener {
            when (it) {
                R.id.home -> navController.navigate(R.id.action_timeline2_to_home2)
                R.id.profile -> navController.navigate(R.id.action_timeline2_to_userProfile)
            }
        }

        val recyclerView: TimeLineRecyclerView = view.findViewById(R.id.recycler_view)

        val itemTouchHelperCallback: ItemTouchHelper.SimpleCallback =
            RecyclerItemTouchHelper(0, ItemTouchHelper.RIGHT, this)
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView)


        recyclerView.layoutManager = LinearLayoutManager(
            view.context,
            LinearLayoutManager.VERTICAL,
            false
        )

        //Get data
        imageList = getImageList()

        recyclerView.itemAnimator = DefaultItemAnimator()

        //Add RecyclerSectionItemDecoration.SectionCallback
        recyclerView.addItemDecoration(getSectionCallback(imageList))

        //Set Adapter
        adapter = ImageAdapter(
            imageList,
            this
        )
        recyclerView.adapter = adapter
    }



    override fun onItemClicked(position: Int) {
        StfalconImageViewer.Builder<Image>(context, imageList) { view, image ->
            Picasso.get().load(image.link).into(view)
        }.withStartPosition(position).withHiddenStatusBar(false).show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sortList(imageList: ArrayList<Image>) {
        imageList.sortWith(Comparator { o1: Image, o2: Image ->
            if (o1.uploadTime.isAfter(o2.uploadTime)) {
                -1
            } else {
                1
            }
        })
    }


    //Get data method
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getImageList(): ArrayList<Image> {
        val list = ImageRepoTemp().imageRepo
        sortList(list)
        return list
    }


    //Get SectionCallback method
    private fun getSectionCallback(imageList: List<Image>): RecyclerSectionItemDecoration.SectionCallback {
        return object : RecyclerSectionItemDecoration.SectionCallback {
            //In your data, implement a method to determine if this is a section.
            override fun isSection(position: Int): Boolean =
                imageList[position].uploadTime != imageList[position - 1].uploadTime

            @RequiresApi(Build.VERSION_CODES.O)
            fun dateFormatting(date: LocalDate): String {
                val formatter = DateTimeFormatter.ofPattern("dd MMM, yyyy")
                return date.format(formatter)
            }

            //Implement a method that returns a SectionHeader.
            @RequiresApi(Build.VERSION_CODES.O)
            override fun getSectionHeader(position: Int): SectionInfo? =
                SectionInfo(
                    dateFormatting(imageList[position].uploadTime),
                    imageList[position].category
                )
        }
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int, position: Int) {
        Toast.makeText(view?.context,"Delete ${imageList[position].category}",Toast.LENGTH_LONG).show()
        imageList.removeAt(position)
        adapter.notifyDataSetChanged();
    }
}
