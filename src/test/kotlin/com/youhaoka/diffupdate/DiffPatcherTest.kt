package com.youhaoka.diffupdate

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Test

class DiffPatcherTest {
    @Test
    fun patchAtomicRejectsOutputOverOldFile() {
        val oldFile = File("old.apk")
        val diffFile = File("update.hdiff")

        val result = DiffPatcher.patchAtomic(oldFile, diffFile, oldFile)

        assertEquals(DiffPatcher.PATCH_OUTPUT_PATH_ERROR, result)
    }

    @Test
    fun patchAtomicRejectsOutputOverDiffFile() {
        val oldFile = File("old.apk")
        val diffFile = File("update.hdiff")

        val result = DiffPatcher.patchAtomic(oldFile, diffFile, diffFile)

        assertEquals(DiffPatcher.PATCH_OUTPUT_PATH_ERROR, result)
    }
}
