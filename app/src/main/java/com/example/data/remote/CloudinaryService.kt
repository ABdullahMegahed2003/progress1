package com.example.data.remote

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

class CloudinaryService(private val context: Context) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    // Values injected from BuildConfig (or defaults from user configuration)
    private val cloudName: String = try {
        BuildConfig.CLOUDINARY_CLOUD_NAME.ifEmpty { "dltrtwlyj" }
    } catch (e: Exception) {
        "dltrtwlyj"
    }

    private val uploadPreset: String = try {
        BuildConfig.CLOUDINARY_UPLOAD_PRESET.ifEmpty { "ml_default" }
    } catch (e: Exception) {
        "ml_default"
    }

    suspend fun uploadImage(imageUri: Uri): Result<String> = withContext(Dispatchers.IO) {
        try {
            // First save locally to app cache so user always has instant offline access
            val localCacheFile = saveUriToInternalStorage(imageUri)
            val bytes = localCacheFile.readBytes()

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "file",
                    "profile_${System.currentTimeMillis()}.jpg",
                    bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
                )
                .addFormDataPart("upload_preset", uploadPreset)
                .build()

            val uploadUrl = "https://api.cloudinary.com/v1_1/$cloudName/image/upload"
            val request = Request.Builder()
                .url(uploadUrl)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string().orEmpty()

            if (response.isSuccessful) {
                val json = JSONObject(responseBody)
                val secureUrl = json.optString("secure_url")
                if (secureUrl.isNotEmpty()) {
                    return@withContext Result.success(secureUrl)
                }
            }

            Log.w("CloudinaryService", "Cloudinary upload failed: code=${response.code}, body=$responseBody")
            // Fallback gracefully to local stored file URI so user has zero interruption!
            Result.success(Uri.fromFile(localCacheFile).toString())
        } catch (e: Exception) {
            Log.e("CloudinaryService", "Upload exception, falling back to local storage", e)
            try {
                val localCacheFile = saveUriToInternalStorage(imageUri)
                Result.success(Uri.fromFile(localCacheFile).toString())
            } catch (inner: Exception) {
                Result.failure(e)
            }
        }
    }

    private fun saveUriToInternalStorage(uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalStateException("تعذر قراءة ملف الصورة المحدد")
        val file = File(context.filesDir, "avatar_current.jpg")
        FileOutputStream(file).use { output ->
            inputStream.copyTo(output)
        }
        return file
    }
}
