package com.minorProject.cloudGallery.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.minorProject.cloudGallery.R
import com.minorProject.cloudGallery.model.bean.Image
import com.stfalcon.imageviewer.StfalconImageViewer
import com.stfalcon.imageviewer.listeners.OnDismissListener

class FullScreenImageDisplay : AppCompatActivity(), OnDismissListener {
    private var imageList: ArrayList<Image> = ArrayList()
    private var position: Int = 0

    private lateinit var stfalcon: StfalconImageViewer<Image>

    companion object {
        const val FULLSCREEN_IMAGE_DISPLAY_IMAGE_LIST: String =
            "FULLSCREEN_IMAGE_DISPLAY_IMAGE_LIST"
        const val FULLSCREEN_IMAGE_DISPLAY_IMAGE_POSITION: String =
            "FULLSCREEN_IMAGE_DISPLAY_IMAGE_POSITION"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setContentView(R.layout.a_full_screen_image_display)

        if (intent != null) {
            if (intent.hasExtra(FULLSCREEN_IMAGE_DISPLAY_IMAGE_LIST) && intent.hasExtra(
                    FULLSCREEN_IMAGE_DISPLAY_IMAGE_POSITION
                )
            ) {
                imageList =
                    intent.getParcelableArrayListExtra<Image>(FULLSCREEN_IMAGE_DISPLAY_IMAGE_LIST)!!
                position = intent.getIntExtra(FULLSCREEN_IMAGE_DISPLAY_IMAGE_POSITION, 0)
            } else {
                finish()
            }
        }

        // photoViewer
        stfalcon = StfalconImageViewer.Builder(this, imageList) { _view, image ->
            Glide.with(this).load(image.link).into(_view)
        }.withStartPosition(position).withHiddenStatusBar(false)
            .withHiddenStatusBar(true)
            .withDismissListener(this)
            .show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        stfalcon.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        stfalcon.close()
    }

    override fun onDismiss() {
        finish()
    }
}