package com.minorProject.cloudGallery.view.fragments.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.minorProject.cloudGallery.R
import com.minorProject.cloudGallery.model.bean.Image
import com.stfalcon.imageviewer.StfalconImageViewer
import com.stfalcon.imageviewer.listeners.OnImageChangeListener

/**
 * Fragment to display image in fullscreen
 */
class FullScreenView : Fragment(), OnImageChangeListener {
    private lateinit var stfalcon: StfalconImageViewer<Image>

    companion object {
        private lateinit var list: ArrayList<Image>
        private var position: Int = 0

        // fun to create new instance of AddCategory DialogFragment
        @JvmStatic
        fun newInstance(_list: ArrayList<Image>, _position: Int) = FullScreenView()
            .apply {
                list = _list
                position = _position
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.d_fullscreen_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            position = savedInstanceState.getInt("position", 0)
        }

        // photoViewer
        stfalcon = StfalconImageViewer.Builder(context, list) { _view, image ->
            Glide.with(requireView().context).load(image.link).into(_view)
        }.withStartPosition(position).withHiddenStatusBar(false).withImageChangeListener(this)
            .show()

    }

    override fun onImageChange(newPosition: Int) {
        position = newPosition
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("position", position)
    }

    override fun onDetach() {
        super.onDetach()
        stfalcon.dismiss()
    }
}

