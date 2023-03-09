package com.creations.bawender.paperdoc

import android.util.Log
import io.paperdb.Book
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

enum class CollectionEvents {
    CREATE, UPDATE, DELETE
}

data class CollectionData<T>(val event: CollectionEvents, val data: T)

class CollectionRef<T>(
    private val collectionKey: String,
    private val book: Book,
    private val documents: MutableMap<String, T> = mutableMapOf()
) {
    private val updates = MutableSharedFlow<CollectionData<T>>(extraBufferCapacity = 10)
    private val mutex = Mutex()

    suspend fun setDoc(key: String, data: T) = mutex.withLock(Dispatchers.IO) {
        val event =
            if (documents.containsKey(key)) CollectionEvents.UPDATE else CollectionEvents.CREATE

        documents[key] = data
        book.write(collectionKey, documents)
        updates.tryEmit(CollectionData(event, data))
    }

    suspend fun deleteDoc(key: String) = mutex.withLock(Dispatchers.IO) {
        val data = documents.remove(key)

        if (data != null) {
            book.write(collectionKey, documents)
            updates.tryEmit(CollectionData(CollectionEvents.DELETE, data))
        }
    }

    suspend fun deleteAllDocs() = mutex.withLock(Dispatchers.IO) {
        val old = documents
        documents.clear()
        book.write(collectionKey, documents)

        old.forEach { (_, value) ->
            updates.tryEmit(CollectionData(CollectionEvents.DELETE, value))
        }
    }

    fun getAllDocs(): List<T> = documents.values.toList()

    fun first(): T? = documents.values.firstOrNull()

    fun last(): T? = documents.values.lastOrNull()

    fun getDoc(key: String): T? = documents[key]

    fun count(): Int = documents.size

    fun watch(includeExisting: Boolean = true): Flow<CollectionData<T>> = channelFlow {
        if (includeExisting) {
            launch {
                documents.values.forEach { data ->
                    send(CollectionData(CollectionEvents.CREATE, data))
                }
            }
        }

        updates.collect { data ->
            send(data)
        }
    }

    fun watchAll(): Flow<List<T>> = channelFlow {
        launch { send(documents.values.toList()) }

        //Send documents again when a modification happens
        updates.collect {
            send(documents.values.toList())
        }
    }

}


