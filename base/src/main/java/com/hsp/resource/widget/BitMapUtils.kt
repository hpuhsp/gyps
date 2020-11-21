package com.hsp.resource.widget

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View

/**
 * @Description: 功能描述：
 * @Author:   Andyhuo
 * @Email:    616872253@qq.com
 * @CreateTime: on 2020/9/29 9:20
 * @UpdateRemark:   更新标记：
 */
object BitMapUtils {
    fun convertViewToBitmap(view: View, bitmapWidth: Int, bitmapHeight: Int): Bitmap? {
        val bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
        view.draw(Canvas(bitmap))
        return bitmap
    }

    /**
     * 将bitmap存成文件
     *
     * @param context
     * @param bitmap
     * @param imageName
     */
//    fun saveBitmap(context: Context, bitmap: Bitmap, imageName: String?): String? {
//        var fos: FileOutputStream? = null
//        var os: OutputStream? = null
//        var inputStream: BufferedInputStream? = null
//        var imageFile: File? = null
//        return try {
//            //生成路径
//            //            File filePath = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),APP_FOLDER_PHOTO);
//            val filePath =
//                File(StringUtils.getString(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
//                    "/",
//                    context.packageName,
//                    "/photo/"))
//            Log.e("aaa->",
//                " filePath:" + filePath.path.toString() + " fileAbsolutePath:" + filePath.absolutePath)
//            if (!filePath.exists()) {
//                val `is`: Boolean = filePath.mkdirs()
//                Log.e("aaa->", "is: $`is`")
//            }
//
//            //获取文件
//            imageFile = File(filePath, imageName)
//            fos = FileOutputStream(imageFile)
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
//            fos.flush()
//            val values = ContentValues()
//            values.put(MediaStore.Images.Media.DESCRIPTION, "This is an image")
//            values.put(MediaStore.Images.Media.DISPLAY_NAME, imageName)
//            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
//            values.put(MediaStore.Images.Media.TITLE, "Image.png")
//            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/")
//            val external: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//            val resolver = context.contentResolver
//            val insertUri: Uri? = resolver.insert(external, values)
//            inputStream = BufferedInputStream(FileInputStream(imageFile))
//            if (insertUri != null) {
//                os = resolver.openOutputStream(insertUri)
//            }
//            if (os != null) {
//                val buffer = ByteArray(1024 * 4)
//                var len: Int
//                while (inputStream.read(buffer).also { len = it } != -1) {
//                    os.write(buffer, 0, len)
//                }
//                os.flush()
//            }
//
//            //通知系统相册刷新
//            context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
//                Uri.fromFile(imageFile)))
//            imageFile.getPath()
//        } catch (e: Exception) {
//            e.printStackTrace()
//            null
//        } finally {
//            try {
//                fos!!.close()
//                os!!.close()
//                inputStream!!.close()
//                imageFile!!.delete() // 这里删除源文件不存在 但相册可见
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//        }
//    }

}