package com.project.morestore.repositories

import android.content.Context
import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class PhotoVideoRepository(val context: Context) {


    fun createFileForPhoto(): File{
        val name = "${System.currentTimeMillis()/1000}_image.jpg"
        val dir = context.cacheDir
        return File(dir, name)
    }

    fun createFileForVideo(): File{
        val name = "${System.currentTimeMillis()/1000}_video.mp4"
        val dir = context.externalCacheDir
        return File(dir, name)
    }

   suspend fun createFileForPhoto(bitmap: Bitmap): File? {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(context.cacheDir, "${System.currentTimeMillis() / 1000}.jpg")
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, file.outputStream())
                file
            }catch (e: Throwable){
                null
            }
        }
    }
}