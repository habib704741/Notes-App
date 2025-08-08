package com.example.personalnotesapp.data

import io.realm.kotlin.Realm
import io.realm.kotlin.query.RealmResults
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import io.realm.kotlin.ext.query

class NotesRepository(private var realm : Realm) {

    //get all notes
    fun getAllNotes(): Flow<RealmResults<Note>> {
        return realm.query(Note::class).asFlow().map { it.list }
    }

    //Add new note
    suspend fun addNote(title: String, content: String){
        realm.write {
            // We create a managed copy of the Note object
            copyToRealm(Note().apply {
                this.title = title
                this.content = content
                // The timestamp and ObjectId will be set by the Note() constructor defaults
            })
        }
    }


    //Delete note
    suspend fun deleteNote(note:Note){
        realm.write {
            val latestNote = findLatest(note)
            latestNote?.let {
                delete(it)
            }
        }
    }

    //update existing note
    suspend fun updateNote(
        note: Note,
        newTitle: String,
        newContent: String,
        newCategory: String? = null
    ){
        realm.write {
            val managedNote = findLatest(note)
            managedNote?.apply {
                title = newTitle
                content = newContent
                category = newCategory
                timestamp = System.currentTimeMillis()

            }
        }
    }

    //get note by id
    fun getNoteById(id : org.mongodb.kbson.ObjectId): Note?{
        return realm.query<Note>("_id == $0", id).find().firstOrNull()
    }
}