package com.udacity.notepad.recycler

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.udacity.notepad.R
import com.udacity.notepad.data.DataStore
import com.udacity.notepad.data.Note
import com.udacity.notepad.recycler.NotesAdapter.NotesViewHolder
import com.udacity.notepad.util.layoutInflater
import com.udacity.notepad.util.succes
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.item_note.view.*
import java.util.*


class NotesAdapter(private val context: Context) :
    RecyclerView.Adapter<NotesViewHolder>() {
    private val editNoteSubject = PublishSubject.create<Int>()
    private val showLoadingSubject = PublishSubject.create<Boolean>()
    private val dismissLoadingSubject = PublishSubject.create<Boolean>()


    private var refreshDisposable: Disposable? = null
    private var notes: List<Note> =
        ArrayList()
    private var isRefreshing = false
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        refresh()
    }

    override fun getItemId(position: Int): Long {
        return notes[position].id.toLong()
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val view = context.layoutInflater.inflate(R.layout.item_note, parent, false)
        return NotesViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        val note = notes[position]
        holder.itemView.content.setOnClickListener{
            getClickListener(note)
        }
        holder.itemView.content.setOnLongClickListener{
            getLongClickListener(note)
        }
        holder.text.text = note.text
        holder.id = note.id
    }

    private fun getClickListener(note: Note) {
        editNoteSubject.onNext(note.id)
    }

    private fun getLongClickListener(note: Note): Boolean {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setMessage("Are you sure?")
            .setPositiveButton("Yes") { dialogInterface, viewId ->
                Single.fromCallable {
                    val notes = DataStore.notes.loadAllByIds(note.id)
                    if (notes.size == 1) {
                        DataStore.notes.delete(notes[0])
                    }
                }.succes {
                    refresh()
                    dialogInterface.dismiss()
                }.subscribe()
            }
            .setNegativeButton("No") { dialogInterface, viewId ->
                dialogInterface.dismiss()
            }
            .show()
        return true
    }

    fun refresh() {
        if (isRefreshing) return
        isRefreshing = true
        showLoadingSubject.onNext(true)

        Single.fromCallable{
            DataStore.notes.getAll()
        }.succes {
            notes = it
            notifyDataSetChanged()
            isRefreshing = false
            dismissLoadingSubject.onNext(true)
        }.subscribe()
    }

    fun getEditNote() : Observable<Int> {
        return editNoteSubject.hide()
    }

    fun getShowLoading() : Observable<Boolean> {
        return showLoadingSubject.hide()
    }

    fun getDismissLoading() : Observable<Boolean> {
        return dismissLoadingSubject.hide()
    }

    class NotesViewHolder (itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var id: Int = -1
        var text: TextView = itemView.text

        init {
            itemView.setOnLongClickListener{
                val builder: AlertDialog.Builder = AlertDialog.Builder(itemView.context)
                builder.setMessage("Are you sure?")
                    .setPositiveButton("Yes") { dialogInterface, viewId ->
                        Single.fromCallable{
                            val notes = DataStore.notes.loadAllByIds(id)
                            DataStore.notes.delete(notes[0])
                        }.succes {
                            dialogInterface.dismiss()
                        }.subscribe()
                    }
                    .setNegativeButton("No") {
                            dialogInterface, viewId ->  dialogInterface.dismiss()
                    }
                    .show()
                true
            }
        }
    }

    init {
        setHasStableIds(true)
    }
}

