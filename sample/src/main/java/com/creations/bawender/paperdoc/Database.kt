package com.creations.bawender.paperdoc

object Database {

    /*
    Mandatory to keep a single collection instance throughout the app usage
    as this adds the necessary observability related options for a collection.
    Creating multiple collections of the same type can cause data inconsistencies.
     */
    val notesCollection = collectionOf<Note>()
}