package com.youhaoka.diffupdate

import com.github.sisong.HPatch
import java.io.File
import java.io.IOException
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.Files
import java.nio.file.StandardCopyOption.ATOMIC_MOVE
import java.nio.file.StandardCopyOption.REPLACE_EXISTING

/**
 * 差分合成入口，负责把旧 APK、差分包合成为新 APK。
 *
 * native 实现随本模块的 AAR 一起打包，接入方只需要依赖 AAR 并调用这里的上层 API。
 */
object DiffPatcher {
    const val MIN_CACHE_MEMORY_BYTES: Long = 256 * 1024L
    const val DEFAULT_CACHE_MEMORY_BYTES: Long = 8 * 1024 * 1024L
    const val MAX_SUPPORTED_THREAD_COUNT: Int = 1
    const val PATCH_OUTPUT_PREPARE_ERROR: Int = -1
    const val PATCH_OUTPUT_REPLACE_ERROR: Int = -2
    const val PATCH_OUTPUT_PATH_ERROR: Int = -3

    /**
     * 使用文件对象执行差分合成。
     *
     * @return native hpatch 返回码，0 表示成功。
     */
    @JvmStatic
    @JvmOverloads
    fun patch(
        oldFile: File,
        diffFile: File,
        outputFile: File,
        cacheMemory: Long = DEFAULT_CACHE_MEMORY_BYTES,
        threadCount: Int = MAX_SUPPORTED_THREAD_COUNT
    ): Int {
        return patch(
            oldFileName = oldFile.absolutePath,
            diffFileName = diffFile.absolutePath,
            outputFileName = outputFile.absolutePath,
            cacheMemory = cacheMemory,
            threadCount = threadCount
        )
    }

    /**
     * 先合成到同目录临时文件，再替换目标文件，避免失败时留下半成品。
     *
     * @return 0 表示成功；负数表示上层文件准备或替换失败；正数为 native hpatch 返回码。
     */
    @JvmStatic
    @JvmOverloads
    fun patchAtomic(
        oldFile: File,
        diffFile: File,
        outputFile: File,
        cacheMemory: Long = DEFAULT_CACHE_MEMORY_BYTES,
        threadCount: Int = MAX_SUPPORTED_THREAD_COUNT
    ): Int {
        if (oldFile.isSameFileAs(outputFile) || diffFile.isSameFileAs(outputFile)) {
            return PATCH_OUTPUT_PATH_ERROR
        }

        val outputParent = outputFile.absoluteFile.parentFile
        if (outputParent != null && !outputParent.exists() && !outputParent.mkdirs()) {
            return PATCH_OUTPUT_PREPARE_ERROR
        }

        val tempOutputFile = try {
            File.createTempFile(outputFile.safeTempPrefix(), ".patching", outputParent)
        } catch (_: IOException) {
            return PATCH_OUTPUT_PREPARE_ERROR
        }

        val patchResult = patch(oldFile, diffFile, tempOutputFile, cacheMemory, threadCount)
        if (patchResult != 0) {
            tempOutputFile.delete()
            return patchResult
        }

        return try {
            moveReplacing(tempOutputFile, outputFile)
            0
        } catch (_: IOException) {
            tempOutputFile.delete()
            PATCH_OUTPUT_REPLACE_ERROR
        }
    }

    /**
     * 使用文件路径执行原子差分合成。
     *
     * @return 0 表示成功；负数表示上层文件准备或替换失败；正数为 native hpatch 返回码。
     */
    @JvmStatic
    @JvmOverloads
    fun patchAtomic(
        oldFileName: String,
        diffFileName: String,
        outputFileName: String,
        cacheMemory: Long = DEFAULT_CACHE_MEMORY_BYTES,
        threadCount: Int = MAX_SUPPORTED_THREAD_COUNT
    ): Int {
        return patchAtomic(
            oldFile = File(oldFileName),
            diffFile = File(diffFileName),
            outputFile = File(outputFileName),
            cacheMemory = cacheMemory,
            threadCount = threadCount
        )
    }

    /**
     * 使用文件路径执行差分合成。
     *
     * @return native hpatch 返回码，0 表示成功。
     */
    @JvmStatic
    @JvmOverloads
    fun patch(
        oldFileName: String,
        diffFileName: String,
        outputFileName: String,
        cacheMemory: Long = DEFAULT_CACHE_MEMORY_BYTES,
        threadCount: Int = MAX_SUPPORTED_THREAD_COUNT
    ): Int {
        return HPatch.patch(
            oldFileName,
            diffFileName,
            outputFileName,
            cacheMemory.coerceAtLeast(MIN_CACHE_MEMORY_BYTES),
            threadCount.coerceIn(1, MAX_SUPPORTED_THREAD_COUNT)
        )
    }

    private fun File.safeTempPrefix(): String {
        val candidate = "${name.ifBlank { "patch" }}."
        return if (candidate.length >= 3) candidate else "patch."
    }

    private fun moveReplacing(source: File, target: File) {
        try {
            Files.move(source.toPath(), target.toPath(), REPLACE_EXISTING, ATOMIC_MOVE)
        } catch (_: AtomicMoveNotSupportedException) {
            Files.move(source.toPath(), target.toPath(), REPLACE_EXISTING)
        }
    }

    private fun File.isSameFileAs(other: File): Boolean {
        return try {
            canonicalFile == other.canonicalFile
        } catch (_: IOException) {
            absoluteFile == other.absoluteFile
        }
    }
}
