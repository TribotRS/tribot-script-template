package org.tribot.gradle.plugin

import com.google.gson.Gson
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.math.BigInteger
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.http.HttpResponse.BodyHandlers
import java.nio.file.Path
import java.security.MessageDigest

class TribotSplash {

    private val tribotSplashUrl = "https://runeautomation.com/api/internal/products/tribot-splash/releases/latest"
    private val jarName = "tribot-splash.jar"
    private val lockFile: String = getTribotDirectory().absolutePath + File.separator + "tribot-splash.lock"

    val filePath: String = getTribotDirectory().absolutePath + File.separator + jarName

    private val httpClient: HttpClient = HttpClient.newHttpClient();

    fun ensureUpdated() {
        RandomAccessFile(lockFile, "rw").use { tmp ->
            tmp.channel.lock() // this will be unlocked when tmp is automatically closed
            val file = getSplashJarFile()
            val local = getLocalHash()
            println("TRiBot splash hash: " + file.hash + "; local file hash: " + local)
            if (file.hash != local) {
                println("Attempting to update local tribot-splash.jar")
                download(file)
                println("Updated tribot-splash.jar")
            }
            else {
                println("TRiBot splash is up-to-date")
            }
        }
    }

    @Throws(IOException::class)
    private fun download(file: ProductFile) {
        val request: HttpRequest = HttpRequest.newBuilder(URI.create(file.url))
                .GET()
                .build()
        try {
            httpClient.send(request, BodyHandlers.ofFile(Path.of(filePath)))
        }
        catch (e: InterruptedException) {
            throw IllegalStateException(e)
        }
    }

    private fun getLocalHash(): String? {
        return try {
            val file = File(filePath);
            val bytes = file.readBytes();
            val md = MessageDigest.getInstance("MD5")
            return BigInteger(1, md.digest(bytes)).toString(16).padStart(32, '0')
        }
        catch (e: Exception) {
            null
        }
    }

    private fun getSplashJarFile(): ProductFile {
        val response = getSplashProduct()
        for (file in response.files) {
            if (jarName == file.fileName) {
                return file
            }
        }
        throw IllegalStateException("Could not find the tribot splash jar")
    }

    private fun getSplashProduct(): TribotProduct {
        val request: HttpRequest = HttpRequest.newBuilder(URI.create(tribotSplashUrl))
                .GET()
                .build()
        val response: HttpResponse<String> = try {
            httpClient.send(request, BodyHandlers.ofString())
        }
        catch (e: InterruptedException) {
            throw IllegalStateException(e)
        }
        return Gson().fromJson(response.body(), TribotProduct::class.java)
    }

    private class TribotProduct(val productName: String, val major: String, val minor: String, val patch: String, val files: Array<ProductFile>)

    private class ProductFile(val fileName: String, val hash: String, val url: String)

}