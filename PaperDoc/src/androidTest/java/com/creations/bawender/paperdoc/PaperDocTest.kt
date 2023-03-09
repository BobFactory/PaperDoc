package com.creations.bawender.paperdoc

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import java.util.UUID

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class PaperDocTest {

    data class User(
        val key: String = UUID.randomUUID().toString(),
        val name: String
    )

    @Before
    fun setUp() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        PaperDoc.init(appContext)
    }

    @Test
    fun returns_the_same_collectionRef_if_already_initiated() {
        val firstCollection = collectionOf<User>()
        val secondCollection = collectionOf<User>()
        assertEquals(firstCollection, secondCollection)
    }

}