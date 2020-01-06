package com.udacity.notepad.data

import android.provider.BaseColumns
import com.udacity.notepad.data.NotesContract.NoteTable.CREATED_AT
import com.udacity.notepad.data.NotesContract.NoteTable.IS_PINNED
import com.udacity.notepad.data.NotesContract.NoteTable.TEXT
import com.udacity.notepad.data.NotesContract.NoteTable.UPDATED_AT
import com.udacity.notepad.data.NotesContract.NoteTable._TABLE_NAME

object NotesContract {
    const val SQL_CREATE_ENTRIES =
        "CREATE TABLE " +
                "$_TABLE_NAME (${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "$TEXT TEXT, " +
                "$IS_PINNED INTEGER, " +
                "$CREATED_AT INTEGER, " +
                "$UPDATED_AT INTEGER)"

    const val SQL_DELETE_ENTRIES =
        "DROP TABLE IF EXISTS $_TABLE_NAME"

    const val SQL_QUERY_ALL =
        "SELECT * FROM $_TABLE_NAME ORDER BY $CREATED_AT DESC"

    object NoteTable {
        const val _TABLE_NAME = "notes"
        const val TEXT = "text"
        const val IS_PINNED = "is_pinned"
        const val CREATED_AT = "created_at"
        const val UPDATED_AT = "updated_at"
    }
}