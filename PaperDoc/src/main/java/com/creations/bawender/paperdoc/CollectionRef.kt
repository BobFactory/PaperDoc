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

    suspend fun getAllDocs(): List<T> = mutex.withLock { documents.values.toList() }

    suspend fun first(): T? = mutex.withLock {
        documents.values.firstOrNull()
    }

    suspend fun last(): T? = mutex.withLock {
        documents.values.lastOrNull()
    }

    suspend fun getDoc(key: String): T? = mutex.withLock {
        documents[key]
    }

    suspend fun count(): Int = mutex.withLock {
        documents.size
    }

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


