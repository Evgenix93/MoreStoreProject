package com.project.morestore.repositories

import android.content.Context
import java.io.File

class PhotoVideoRepository(val context: Context) {


    fun createFileForPhoto(): File{
        val name = "${System.currentTimeMillis()/1000}_image.jpg"
        val dir = context.cacheDir
        return File(dir, name)
    }

    fun createFileForVideo(): File{
        val name = "${System.currentTimeMillis()/1000}_video.mp4"
        val dir = context.cacheDir
        return File(dir, name)
    }
}