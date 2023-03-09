# PaperDoc
A simple Document Collection based NoSql Database base on PaperDb

## Motivation 

This is a creative projetc where I try to add kotlin flow and coroutine support to the traditional Java base PaperDb. Sure Java and Kotlin can be used
interchangebly but it still lacks some of the mordern Kotlin based api's (flow support and coroutine support). This project looks to these two capabilities
to it. Also this assumes that a collection solely comprises of obejcts of single type and not dynbamic types. This adds the type system that is mostly 
available in other local databases. 


## Installations

```groovy
allprojects {
  repositories {
   ...
   maven { url 'https://jitpack.io' }
  }
}
```
<br>

```groovy
dependencies {
 implementation 'com.github.BobFactory:PaperDoc:1.0.2'
}
```

## Initialize PaperDoc
Should be initialized once in `Application.onCreate()` :
```kotlin
class SampleApp : Application() {

    override fun onCreate() {
        super.onCreate()
        PaperDoc.init(this)
    }
}
```
Internally this uses PaperDb to save the data. Please read its documentation to understand how it internally automatically hanndles 
saving data and migrating data changes. 
PaperDb: https://github.com/pilgr/Paper


## Create a collection

Create a collection of objects to save the data 

```kotlin
data class Note(
    val key: String = UUID.randomUUID().leastSignificantBits.toString(),
    val text: String,
    val createdAt: LocalDate
)


val notes = collectionOf<Note>()


//Save note by using the `setDoc` function
suspend fun saveData(newNote: Note) {
  val note = Note(
    text = "Random Note",
    createdAt = LocalDate.now()
  )
  notes.setDoc(note.key, note)
}


//Delete note by using the `deleteDoc` function
suspend fun delete(note: Note) {
  notes.deleteDoc(note.key)
}
```

## Observe a collection 

All changes to a collection are forwarded to a channel that is exposed through a flow of data. Whenever a change happens this function is triggered. 

```kotlin

collectionOf<Note>().watchAll()
  .onEach { notes -> //Do something here }
  .launchIn(viewModelScope)

```

There are some other utility functions that are available in the library. 



