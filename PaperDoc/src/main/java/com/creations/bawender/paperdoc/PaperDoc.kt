package com.creations.bawender.paperdoc

import android.content.Context
import io.paperdb.Book
import io.paperdb.Paper

object PaperDoc {
    private lateinit var book: Book
    private val collectionsMap = mutableMapOf<String, CollectionRef<*>>()

    private fun assertInitialized() {
        if (!::book.isInitialized) {
            error("PaperDoc not initialized. Call PaperDoc.init(Context) once")
        }
    }

    fun init(context: Context) {
        Paper.init(context)
        book = Paper.book()
    }

    fun <T> collection(key: String): CollectionRef<T> {
        assertInitialized()

        return if (collectionsMap.containsKey(key)) {
            collectionsMap[key] as CollectionRef<T>
        } else {
            val documents = book.read(key) ?: mutableMapOf<String, T>()
            val collection = CollectionRef(key, book, documents)
            collectionsMap[key] = collection
            collection
        }
    }

}

inline fun <reified T> collectionOf(): CollectionRef<T> {
    val name = T::class.simpleName ?: error("Cannot use anonymous classes for a collection")
    return PaperDoc.collection(name)
}


fun main() {
    val usersBook = Paper.book("users")
    usersBook.write("1213", "user1")


}


