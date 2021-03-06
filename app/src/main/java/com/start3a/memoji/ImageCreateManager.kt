package com.start3a.memoji

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.start3a.memoji.repository.Repository
import java.io.*
import java.net.URL
import java.util.*

class ImageCreateManager {

    companion object {

        private const val TAG = "ImageCreateManager"
        const val UNSAVABLE_IMAGE = "Unsavable_Image"
        private const val VALUE_RESIZE = 200

        fun calculateInSampleSize(
            options: BitmapFactory.Options,
            reqWidth: Int,
            reqHeight: Int
        ): Int {
            // Raw height and width of image
            val (height: Int, width: Int) = options.run { outHeight to outWidth }
            var inSampleSize = 1

            if (height > reqHeight || width > reqWidth) {

                val halfHeight: Int = height / 2
                val halfWidth: Int = width / 2

                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                    inSampleSize *= 2
                }
            }

            return inSampleSize
        }

        fun decodeSampledBitmapFromResource(
            ist1: InputStream,
            ist2: InputStream,
            reqWidth: Int,
            reqHeight: Int
        ): Bitmap? {
            // First decode with inJustDecodeBounds=true to check dimensions
            return BitmapFactory.Options().run {
                inJustDecodeBounds = true
                BitmapFactory.decodeStream(ist1, null, this)
                // Calculate inSampleSize
                inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)

                // Decode bitmap with inSampleSize set
                inJustDecodeBounds = false

                BitmapFactory.decodeStream(ist2, null, this)
            }
        }

        fun saveBitmapToJpeg(bitmap: Bitmap, dir: String): String {
            // 파일 객체 생성
            val fileName = "${UUID.randomUUID()}.jpg"
            val tempFile = File(dir, fileName)

            // 파일 저장
            try {
                tempFile.createNewFile()
                val out = FileOutputStream(tempFile)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, out)
                out.close()

                return "$dir/$fileName"

            } catch (e: FileNotFoundException) {
                Log.e(TAG, "FileNotFoundException :  " + e.printStackTrace())
            } catch (e: IOException) {
                Log.e(TAG, "IOException : " + e.printStackTrace())
            } catch (e: SecurityException) {
                Log.e(TAG, "SecurityException : " + e.printStackTrace())
            }
            return ""
        }

        fun getURIToBitmap(context: Context, imgSrc: String): Bitmap? {
            return try {
                val ist = context.contentResolver.openInputStream(Uri.parse(imgSrc))
                BitmapFactory.decodeStream(ist)
            } catch (e: FileNotFoundException) {
                Log.e(TAG, "URI OpenStream Failed : " + e.printStackTrace())
                e.printStackTrace()
                null
            }
        }

        fun getURIToBitmapResize(context: Context, imgSrc: String): Bitmap? {
            return try {
                val uri = Uri.parse(imgSrc)
                val resv =  context.contentResolver
                val ist1 = resv.openInputStream(uri)
                ist1?.run {
                    val ist2 = resv.openInputStream(uri)
                    decodeSampledBitmapFromResource(this, ist2!!, VALUE_RESIZE, VALUE_RESIZE)
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                null
            }
        }

        fun setImageDirectory(filesDir: File, memoId: String) {
            // 썸네일 이미지 디렉토리
            val dirParent = "$filesDir/${Repository.MEMOS}/$memoId"
            val fileThumbnail = File("$dirParent/${Repository.THUMBNAIL_PATH}")
            if (!fileThumbnail.exists()) fileThumbnail.mkdirs()
            val fileOriginal = File("$dirParent/${Repository.ORIGINAL_PATH}")
            if (!fileOriginal.exists()) fileOriginal.mkdirs()
        }

        fun getURLToBitmap(imgSrc: String): Bitmap? {
            return try {
                val ist = URL(imgSrc).openStream()
                BitmapFactory.decodeStream(ist)
            } catch (e: IOException) {
                Log.d(TAG, "IOException : " + e.printStackTrace().toString())
                null
            }
        }

        fun getURLToBitmapResize(imgSrc: String): Bitmap? {
            return try {
                val ist1 = URL(imgSrc).openStream()
                ist1?.run {
                    val ist2 = URL(imgSrc).openStream()
                    decodeSampledBitmapFromResource(this, ist2!!, VALUE_RESIZE, VALUE_RESIZE)
                }
            } catch (e: IOException) {
                Log.d(TAG, "IOException : " + e.printStackTrace().toString())
                null
            }
        }
    }
}