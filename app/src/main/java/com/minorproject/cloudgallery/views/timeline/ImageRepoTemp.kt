package com.minorproject.cloudgallery.views.timeline

import android.os.Build
import androidx.annotation.RequiresApi
import com.minorproject.cloudgallery.model.Image
import java.time.LocalDate
import kotlin.collections.ArrayList

class ImageRepoTemp {
    val imageRepo: ArrayList<Image>
        @RequiresApi(Build.VERSION_CODES.O)
        get() {
            val imageRepoList = ArrayList<Image>()

            imageRepoList.add(
                Image(
                    "abc1234",
                    "Category 1",
                    "image_name",
                    543,
                    LocalDate.of(2020, 2, 15),
                    "https://cdn.imgbin.com/24/0/8/imgbin-deadpool-youtube-drawing-chibi-man-cartoon-chibi-xxYSK5RpubTKk76PWXDLmVWzs.jpg"
                )
            )

            imageRepoList.add(
                Image(
                    "abc1234",
                    "Category 2",
                    "image_name",
                    543,
                    LocalDate.of(2020, 3, 4),
                    "https://www.kindpng.com/picc/m/16-164742_hot-anime-bad-boy-hd-png-download.png"
                )
            )

            imageRepoList.add(
                Image(
                    "abc1234",
                    "Category 2",
                    "image_name",
                    543,
                    LocalDate.of(2020, 3, 4),
                    "https://pngimage.net/wp-content/uploads/2018/05/cartoon-anime-png-8.png"
                )
            )

            imageRepoList.add(
                Image(
                    "abc1234",
                    "Category 3",
                    "image_name",
                    543,
                    LocalDate.of(2020, 3, 4),
                    "https://cdn.imgbin.com/24/0/8/imgbin-deadpool-youtube-drawing-chibi-man-cartoon-chibi-xxYSK5RpubTKk76PWXDLmVWzs.jpg"
                )
            )

            imageRepoList.add(
                Image(
                    "abc1234",
                    "Category 3",
                    "image_name",
                    543,
                    LocalDate.of(2020, 4, 24),
                    "https://pngimage.net/wp-content/uploads/2018/05/cartoon-anime-png-8.png"
                )
            )

            imageRepoList.add(
                Image(
                    "abc1234",
                    "Category 3",
                    "image_name",
                    543,
                    LocalDate.of(2020, 4, 24),
                    "https://www.kindpng.com/picc/m/16-164742_hot-anime-bad-boy-hd-png-download.png"
                )
            )

            imageRepoList.add(
                Image(
                    "abc1234",
                    "Category 1",
                    "image_name",
                    543,
                    LocalDate.of(2020, 5, 15),
                    "https://pngimage.net/wp-content/uploads/2018/05/cartoon-anime-png-8.png"
                )
            )

            imageRepoList.add(
                Image(
                    "abc1234",
                    "Category 1",
                    "image_name",
                    543,
                    LocalDate.of(2020, 2, 12),
                    "https://www.kindpng.com/picc/m/16-164742_hot-anime-bad-boy-hd-png-download.png"
                )
            )

            imageRepoList.add(
                Image(
                    "abc1234",
                    "Category 2",
                    "image_name",
                    543,
                    LocalDate.of(2020, 3, 10),
                    "https://cdn.imgbin.com/24/0/8/imgbin-deadpool-youtube-drawing-chibi-man-cartoon-chibi-xxYSK5RpubTKk76PWXDLmVWzs.jpg"
                )
            )

            imageRepoList.add(
                Image(
                    "abc1234",
                    "Category 2",
                    "image_name",
                    543,
                    LocalDate.of(2020, 3, 10),
                    "https://cdn.imgbin.com/24/0/8/imgbin-deadpool-youtube-drawing-chibi-man-cartoon-chibi-xxYSK5RpubTKk76PWXDLmVWzs.jpg"
                )
            )

            imageRepoList.add(
                Image(
                    "abc1234",
                    "Category 3",
                    "image_name",
                    543,
                    LocalDate.of(2020, 4, 15),
                    "https://pngimage.net/wp-content/uploads/2018/05/cartoon-anime-png-8.png"
                )
            )

            imageRepoList.add(
                Image(
                    "abc1234",
                    "Category 3",
                    "image_name",
                    543,
                    LocalDate.of(2020, 4, 24),
                    "https://cdn.imgbin.com/24/0/8/imgbin-deadpool-youtube-drawing-chibi-man-cartoon-chibi-xxYSK5RpubTKk76PWXDLmVWzs.jpg"
                )
            )

            imageRepoList.add(
                Image(
                    "abc1234",
                    "Category 3",
                    "image_name",
                    543,
                    LocalDate.of(2020, 4, 12),
                    "https://pngimage.net/wp-content/uploads/2018/05/cartoon-anime-png-8.png"
                )
            )

            imageRepoList.add(
                Image(
                    "abc1234",
                    "Category 1",
                    "image_name",
                    543,
                    LocalDate.of(2020, 4, 12),
                    "https://cdn.imgbin.com/24/0/8/imgbin-deadpool-youtube-drawing-chibi-man-cartoon-chibi-xxYSK5RpubTKk76PWXDLmVWzs.jpg"
                )
            )

            return imageRepoList
        }
}