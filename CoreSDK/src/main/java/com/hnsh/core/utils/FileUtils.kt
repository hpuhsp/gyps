package com.hnsh.core.utils

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import androidx.core.content.FileProvider
import java.io.*
import java.nio.channels.FileChannel
import java.util.*
import javax.inject.Singleton
import kotlin.collections.ArrayList


/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/9/28 19:42
 * @UpdateRemark:   更新说明：
 */
object FileUtils {
    /**
     * TAG for log messages.
     */
    internal const val TAG = "FileUtils"

    private const val EXTERNAL_STORAGE_PERMISSION =
        "android.permission.WRITE_EXTERNAL_STORAGE"

    const val TYPE_AUDIO = 101  // 音频
    const val TYPE_IMAGE = 102  // 图片
    const val TYPE_VIDEO = 103  // 视频

    private const val prefix = "file://"
    private const val prefixN = "content://"

    val IMAGE_SUFFIX = ".png"
    val VIDEO_SUFFIX = ".mp4"
    val AUDIO_SUFFIX = ".mp3"
    val APP_DIR = "AppData"
    val CAMERA_PATH = "/$APP_DIR/CameraImage/"
    val CAMERA_AUDIO_PATH = "/$APP_DIR/CameraAudio/"
    val CROP_PATH = "/$APP_DIR/CropImage/"
    val Cache_PATH = "/$APP_DIR/MediaCache/"

    private const val DEFAULT_CACHE_DIR = "picture_cache"

    /**
     * 获取带前缀的文件路径
     *
     * @param path
     * @return
     */
    fun getPrefixPath(path: String?): String? {
        var path = path
        if (path == null) {
            path = ""
        }
        path = when {
            path.startsWith(prefix) -> {
                return path
            }
            path.startsWith(prefixN) -> {
                return path
            }
            else -> {
                prefix + path
            }
        }
        return path
    }

    /**
     * 获取照片存储的路径
     *
     * @return
     */
    fun getPicSavePath(context: Context): String {
        var appCacheDir: File? = null
        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
            && hasExternalStoragePermission(context)
        ) {
            appCacheDir = File(
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath,
                "Pic"
            )
        }
        if (appCacheDir == null || !appCacheDir.exists() && !appCacheDir.mkdirs()) {
            appCacheDir = context.cacheDir
        }
        return appCacheDir?.path ?: ""
    }

    /**
     * 获取照片存储的路径
     *
     * @return
     */
    fun getMediaSavePath(context: Context): String {
        var appCacheDir: File? = null
        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
            && hasExternalStoragePermission(context)
        ) {
            appCacheDir = File(
                context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)?.absolutePath,
                "Media"
            )
        }
        if (appCacheDir == null || !appCacheDir.exists() && !appCacheDir.mkdirs()) {
            appCacheDir = context.cacheDir
        }
        return appCacheDir?.path ?: ""
    }

    /**
     * 获取视频uri
     */
    fun getVideoContentUri(
        context: Context,
        path: String
    ): Uri? {
        val cursor = context.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Video.Media._ID),
            MediaStore.Video.Media.DATA + "=? ",
            arrayOf(path),
            null
        )
        return if (cursor != null && cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID))
            val baseUri =
                Uri.parse("content://media/external/video/media")
            Uri.withAppendedPath(baseUri, "" + id)
        } else {
            if (File(path).exists()) {
                val values = ContentValues()
                values.put(MediaStore.Video.Media.DATA, path)
                context.contentResolver
                    .insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
            } else {
                null
            }
        }
    }

    /**
     * 获取流方式写入本地文件
     */
    fun getVideoGalleryFile(
        file: File,
        fileDescriptor: ParcelFileDescriptor
    ): File? {
        try {
            val fos = FileOutputStream(file)
            val fis = FileInputStream(fileDescriptor.fileDescriptor)
            val buffer = ByteArray(1024)
            var byteRead: Int
            while (-1 != fis.read(buffer).also { byteRead = it }) {
                fos.write(buffer, 0, byteRead)
            }
            fis.close()
            fos.flush()
            fos.close()
            return file
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 获取相册文件访问URI
     */
    fun getAlbumMediaUri(context: Context, file: File): Uri {
        val uri: Uri
        uri = if (Build.VERSION.SDK_INT >= 24) {
            val packageName: String = context.packageName + ".fileprovider"
            FileProvider.getUriForFile(context.applicationContext, packageName, file)
        } else {
            Uri.fromFile(file)
        }
        return uri
    }

    /**
     * 获取保存图片地址
     */
    fun getPictureSavePath(context: Context): String {
        return "${getPicSavePath(context)}/${System.currentTimeMillis()}.png"
    }

    /**
     * 判断权限
     */
    private fun hasExternalStoragePermission(context: Context): Boolean {
        val perm =
            context.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION)
        return perm == PackageManager.PERMISSION_GRANTED
    }

    /***
     * 获取文件类型
     * @param paramString
     * @return
     */
    fun getFileType(paramString: String?): String {
        var str = ""
        if (paramString.isNullOrEmpty()) {
            Log.e("tag", "paramString---->null")
            return str
        }
        Log.e("tag", "paramString:$paramString")
        val i = paramString.lastIndexOf('.')
        if (i <= -1) {
            Log.e("tag", "i <= -1")
            return str
        }
        str = paramString.substring(i + 1)
        Log.e("tag", "paramString.substring(i + 1)------>$str")
        return str
    }

    fun createCameraFile(context: Context, type: Int, outputCameraPath: String): File {
        val path: String
        if (type == TYPE_AUDIO) {
            path = if (!TextUtils.isEmpty(outputCameraPath))
                outputCameraPath
            else
                CAMERA_AUDIO_PATH
        } else {
            path = if (!TextUtils.isEmpty(outputCameraPath))
                outputCameraPath
            else
                CAMERA_PATH
        }
        return if (type == TYPE_AUDIO)
            createMediaFile(context, path, type)
        else
            createMediaFile(context, path, type)
    }

    fun createCropFile(context: Context, type: Int): File {
        return createMediaFile(context, CROP_PATH, type)
    }

    /**
     * 创建媒体文件
     */
    fun createMediaFile(context: Context, parentPath: String, type: Int): File {
        var appCacheDir: File? = null
        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
            && hasExternalStoragePermission(context)
        ) {
            appCacheDir = File(
                context.getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath,
                parentPath
            )
        }
        if (appCacheDir == null || !appCacheDir.exists() && !appCacheDir.mkdirs()) {
            appCacheDir = context.cacheDir
        }

        return when (type) {
            TYPE_IMAGE -> File(appCacheDir, System.currentTimeMillis().toString() + IMAGE_SUFFIX)
            TYPE_VIDEO -> File(appCacheDir, System.currentTimeMillis().toString() + VIDEO_SUFFIX)
            TYPE_AUDIO -> File(appCacheDir, System.currentTimeMillis().toString() + AUDIO_SUFFIX)
            else -> File(appCacheDir, System.currentTimeMillis().toString() + IMAGE_SUFFIX)
        }
    }

    /**
     * @param uri The Uri to check.
     * *
     * @return Whether the Uri authority is ExternalStorageProvider.
     * *
     * @author paulburke
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * *
     * @return Whether the Uri authority is DownloadsProvider.
     * *
     * @author paulburke
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * *
     * @return Whether the Uri authority is MediaProvider.
     * *
     * @author paulburke
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * *
     * @return Whether the Uri authority is Google Photos.
     */
    fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.

     * @param context       The mContext.
     * *
     * @param uri           The Uri to query.
     * *
     * @param selection     (Optional) Filter used in the query.
     * *
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * *
     * @return The value of the _data column, which is typically a file path.
     * *
     * @author paulburke
     */
    @SuppressLint("LogNotTimber")
    fun getDataColumn(
        context: Context, uri: Uri, selection: String?,
        selectionArgs: Array<String>?
    ): String? {

        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)

        try {
            cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } catch (ex: IllegalArgumentException) {
            Log.i(
                TAG,
                String.format(Locale.getDefault(), "getDataColumn: _data - [%s]", ex.message)
            )
        } finally {
            if (cursor != null) {
                cursor.close()
            }
        }
        return null
    }

    fun getPhotoCacheDir(context: Context, file: File): File {
        val cacheDir = context.cacheDir
        val file_name = file.name
        if (cacheDir != null) {
            val mCacheDir = File(cacheDir, DEFAULT_CACHE_DIR)
            return if (!mCacheDir.mkdirs() && (!mCacheDir.exists() || !mCacheDir.isDirectory)) {
                file
            } else {
                val fileName = if (file_name.endsWith(".webp")) {
                    System.currentTimeMillis().toString() + ".webp"
                } else {
                    System.currentTimeMillis().toString() + ".png"
                }
                File(mCacheDir, fileName)
            }
        }
        if (Log.isLoggable(TAG, Log.ERROR)) {
            Log.e(TAG, "default disk cache dir is null")
        }
        return file
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.<br></br>
     * <br></br>
     * Callers should check whether the path is local before assuming it
     * represents a local file.

     * @param context The mContext.
     * *
     * @param uri     The Uri to query.
     * *
     * @author paulburke
     */
    @SuppressLint("NewApi")
    fun getPath(context: Context, uri: Uri): String? {
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) {

                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)!!
                )

                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                val contentUri: Uri
                contentUri = when (type) {
                    "image" -> {
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    }
                    "video" -> {
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    }
                    "audio" -> {
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                    else -> {
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    }
                }

                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])

                return getDataColumn(context, contentUri, selection, selectionArgs)
            }// MediaProvider
            // DownloadsProvider
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {

            // Return the remote address
            if (isGooglePhotosUri(uri)) {
                return uri.lastPathSegment
            }

            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }

        return null
    }

    /**
     * Copies one file into the other with the given paths.
     * In the event that the paths are the same, trying to copy one file to the other
     * will cause both files to become null.
     * Simply skipping this step if the paths are identical.
     */
    @Throws(IOException::class)
    fun copyFile(pathFrom: String, pathTo: String) {
        if (pathFrom.equals(pathTo, ignoreCase = true)) {
            return
        }

        var outputChannel: FileChannel? = null
        var inputChannel: FileChannel? = null
        try {
            inputChannel = FileInputStream(File(pathFrom)).channel
            outputChannel = FileOutputStream(File(pathTo)).channel
            inputChannel!!.transferTo(0, inputChannel.size(), outputChannel)
            inputChannel.close()
        } finally {
            if (inputChannel != null) inputChannel.close()
            if (outputChannel != null) outputChannel.close()
        }
    }

    /**
     * Copies one file into the other with the given paths.
     * In the event that the paths are the same, trying to copy one file to the other
     * will cause both files to become null.
     * Simply skipping this step if the paths are identical.
     */
    @Throws(IOException::class)
    fun copyAudioFile(pathFrom: String, pathTo: String) {
        if (pathFrom.equals(pathTo, ignoreCase = true)) {
            return
        }

        var outputChannel: FileChannel? = null
        var inputChannel: FileChannel? = null
        try {
            inputChannel = FileInputStream(File(pathFrom)).channel
            outputChannel = FileOutputStream(File(pathTo)).channel
            inputChannel!!.transferTo(0, inputChannel.size(), outputChannel)
            inputChannel.close()
        } finally {
            if (inputChannel != null) inputChannel.close()
            if (outputChannel != null) outputChannel.close()
            deleteFile(pathFrom)
        }
    }

    /**
     * 读取图片属性：旋转的角度

     * @param path 图片绝对路径
     * *
     * @return degree旋转的角度
     */
    fun readPictureDegree(path: String): Int {
        var degree = 0
        try {
            val exifInterface = ExifInterface(path)
            val orientation = exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return degree
    }

    /*
     * 旋转图片
     * @param angle
     * @param bitmap
     * @return Bitmap
     */
    fun rotaingImageView(angle: Int, bitmap: Bitmap): Bitmap {
        //旋转图片 动作
        val matrix = Matrix()
        matrix.postRotate(angle.toFloat())
        println("angle2=" + angle)
        // 创建新的图片
        val resizedBitmap = Bitmap.createBitmap(
            bitmap, 0, 0,
            bitmap.width, bitmap.height, matrix, true
        )
        return resizedBitmap
    }

    fun saveBitmapFile(bitmap: Bitmap, file: File) {
        try {
            val bos = BufferedOutputStream(FileOutputStream(file))
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
            bos.flush()
            bos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    /**
     * 转换图片成圆形

     * @param bitmap 传入Bitmap对象
     * *
     * @return
     */
    fun toRoundBitmap(bitmap: Bitmap): Bitmap {
        var width = bitmap.width
        var height = bitmap.height
        val roundPx: Float
        val left: Float
        val top: Float
        val right: Float
        val bottom: Float
        val dst_left: Float
        val dst_top: Float
        val dst_right: Float
        val dst_bottom: Float
        if (width <= height) {
            roundPx = (width / 2).toFloat()

            left = 0f
            top = 0f
            right = width.toFloat()
            bottom = width.toFloat()

            height = width

            dst_left = 0f
            dst_top = 0f
            dst_right = width.toFloat()
            dst_bottom = width.toFloat()
        } else {
            roundPx = (height / 2).toFloat()

            val clip = ((width - height) / 2).toFloat()

            left = clip
            right = width - clip
            top = 0f
            bottom = height.toFloat()
            width = height

            dst_left = 0f
            dst_top = 0f
            dst_right = height.toFloat()
            dst_bottom = height.toFloat()
        }

        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val paint = Paint()
        val src = Rect(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
        val dst = Rect(dst_left.toInt(), dst_top.toInt(), dst_right.toInt(), dst_bottom.toInt())
        val rectF = RectF(dst)

        paint.isAntiAlias = true// 设置画笔无锯齿

        canvas.drawARGB(0, 0, 0, 0) // 填充整个Canvas

        // 以下有两种方法画圆,drawRounRect和drawCircle
        canvas.drawRoundRect(
            rectF,
            roundPx,
            roundPx,
            paint
        )// 画圆角矩形，第一个参数为图形显示区域，第二个参数和第三个参数分别是水平圆角半径和垂直圆角半径。
        // canvas.drawCircle(roundPx, roundPx, roundPx, paint);

        paint.xfermode =
            PorterDuffXfermode(PorterDuff.Mode.SRC_IN)// 设置两张图片相交时的模式,参考http://trylovecatch.iteye.com/blog/1189452
        canvas.drawBitmap(bitmap, src, dst, paint) // 以Mode.SRC_IN模式合并bitmap和已经draw了的Circle

        return output
    }

    /**
     * 创建文件夹

     * @param filename
     * *
     * @return
     */
    fun createDir(context: Context, filename: String, directory_path: String): String {
        val state = Environment.getExternalStorageState()
        val rootDir =
            if (state == Environment.MEDIA_MOUNTED) Environment.getExternalStorageDirectory() else context.cacheDir
        var path: File? = null
        if (!TextUtils.isEmpty(directory_path)) {
            // 自定义保存目录
            path = File(rootDir.absolutePath + directory_path)
        } else {
            path = File(rootDir.absolutePath + "/Phoenix")
        }
        if (!path.exists())
        // 若不存在，创建目录，可以在应用启动的时候创建
            path.mkdirs()

        return path.toString() + "/" + filename
    }


    /**
     * image is Damage

     * @param path
     * *
     * @return
     */
    fun isDamage(path: String): Int {
        var options: BitmapFactory.Options? = null
        if (options == null) options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, options) //filePath代表图片路径
        if (options.mCancel || options.outWidth == -1
            || options.outHeight == -1
        ) {
            //表示图片已损毁
            return -1
        }
        return 0
    }

    /**
     * 获取某目录下所有文件路径

     * @param dir
     */
    fun getDirFiles(dir: String): List<String> {
        val scanner5Directory = File(dir)
        val list = ArrayList<String>()
        if (scanner5Directory.isDirectory) {
            for (file in scanner5Directory.listFiles()) {
                val path = file.absolutePath
                if (path.endsWith(".jpg") || path.endsWith(".jpeg")
                    || path.endsWith(".png") || path.endsWith(".gif")
                    || path.endsWith(".webp")
                ) {
                    list.add(path)
                }
            }
        }
        return list
    }

    val dcimCameraPath: String
        get() {
            val absolutePath: String
            try {
                absolutePath =
                    "%" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath + "/Camera"
            } catch (e: Exception) {
                e.printStackTrace()
                return ""
            }

            return absolutePath
        }

    /**
     * set empty Phoenix Cache

     * @param mContext
     */
    fun deleteCacheDirFile(mContext: Context) {
        val cutDir = mContext.cacheDir
        val compressDir = File(mContext.cacheDir.toString() + "/picture_cache")
        val lubanDir = File(mContext.cacheDir.toString() + "/luban_disk_cache")
        if (cutDir != null) {
            val files = cutDir.listFiles()
            for (file in files) {
                if (file.isFile)
                    file.delete()
            }
        }

        if (compressDir != null) {
            val files = compressDir.listFiles()
            if (files != null)
                for (file in files) {
                    if (file.isFile)
                        file.delete()
                }
        }

        if (lubanDir != null) {
            val files = lubanDir.listFiles()
            if (files != null)
                for (file in files) {
                    if (file.isFile)
                        file.delete()
                }
        }
        Log.i(TAG, "Cache delete success!")
    }

    /**
     * delete file

     * @param path
     */
    fun deleteFile(path: String) {
        try {
            if (!TextUtils.isEmpty(path)) {
                val file = File(path)
                file?.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    /**
     *  清除所有缓存文件
     *
     * @param context
     * @param type    image、video、audio ...
     */
    fun deleteAllCacheDirFile(context: Context) {
        val dirPictures =
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (dirPictures != null) {
            val files = dirPictures.listFiles()
            if (files != null) {
                for (file in files) {
                    if (file.isFile) {
                        file.delete()
                    }
                }
            }
        }

        val dirDic =
            context.getExternalFilesDir(Environment.DIRECTORY_DCIM)
        if (dirDic != null) {
            val files = dirDic.listFiles()
            if (files != null) {
                for (file in files) {
                    if (file.isFile) {
                        file.delete()
                    }
                }
            }
        }

        val dirMovies =
            context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        if (dirMovies != null) {
            val files = dirMovies.listFiles()
            if (files != null) {
                for (file in files) {
                    if (file.isFile) {
                        file.delete()
                    }
                }
            }
        }
        val dirMusic =
            context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        if (dirMusic != null) {
            val files = dirMusic.listFiles()
            if (files != null) {
                for (file in files) {
                    if (file.isFile) {
                        file.delete()
                    }
                }
            }
        }
    }

    /**
     * ===========================针对PictureSelector库 自定义文件目录====================================================>
     */
    /**
     * 获取照片存储的路径
     *
     * @return
     */
    fun getCompressPicPath(context: Context): String {
        var appCacheDir: File? = null
        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
            && hasExternalStoragePermission(context)
        ) {
            appCacheDir = File(
                context.getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath,
                "CompressCache"
            )
        }
        if (appCacheDir == null || !appCacheDir.exists() && !appCacheDir.mkdirs()) {
            appCacheDir = context.cacheDir
        }
        return appCacheDir?.path ?: ""
    }

    /**
     * 获取照片存储的路径
     *
     * @return
     */
    fun getCompressVideoFile(context: Context, filename: String): String {
        var appCacheDir: File? = null
        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
            && hasExternalStoragePermission(context)
        ) {
            appCacheDir = File(
                context.getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath,
                "CompressCache"
            )
        }
        if (appCacheDir == null || !appCacheDir.exists() && !appCacheDir.mkdirs()) {
            appCacheDir = context.cacheDir
        }
        return File(appCacheDir, filename + VIDEO_SUFFIX).absolutePath

    }

    /**
     * 删除压缩、裁剪 视频 图片缓存目录
     */
    fun deleteCompressCache(context: Context) {
        val cacheDir = File(
            context.getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath,
            "CompressCache"
        )
        if (cacheDir != null) {
            val files = cacheDir.listFiles()
            if (files != null) {
                for (file in files) {
                    if (file.isFile) {
                        file.delete()
                    }
                }
            }
        }
    }
}