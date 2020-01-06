package com.udacity.notepad.crud

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.udacity.notepad.R
import com.udacity.notepad.data.DataStore.notes
import com.udacity.notepad.data.Note
import com.udacity.notepad.util.loadable.Loadable
import com.udacity.notepad.util.loadable.LoadingDialogHelper
import com.udacity.notepad.util.succes
import io.reactivex.Single
import kotlinx.android.synthetic.main.activity_create.*
import java.util.*


class CreateActivity : AppCompatActivity(), Loadable {
    private var noteId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.hasExtra(EXTRA_NOTE_ID)) {
            noteId = intent.getIntExtra(EXTRA_NOTE_ID, -1)
        }
        setContentView(R.layout.activity_create)
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        outState.putString(EXTRA_NOTE_TEXT, edit_text.text.toString())
        noteId?.let {
            outState.putInt(EXTRA_NOTE_ID, it)
        }
        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState.containsKey(EXTRA_NOTE_ID)) {
            noteId = savedInstanceState.getInt(EXTRA_NOTE_ID)
        }
        edit_text.setText(savedInstanceState.getString(EXTRA_NOTE_ID))
    }

    override fun onResume() {
        super.onResume()
        setText()
    }

    private fun setText() {
        noteId?.let {
            showLoadingDialog()
            Single.fromCallable {
                val notes = notes.loadAllByIds(it)
                if (notes.size == 1) {
                    runOnUiThread{
                        setText(notes.get(0))
                    }
                }
            }.doOnError {
                dismissLoadingDialog()
            }.succes {
                dismissLoadingDialog()
            }.subscribe()
        }
    }

    private fun setText(note: Note) {
        note.text?.apply {
            edit_text.setText(this);
            edit_text.setSelection(this.length, this.length)
        };
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_accept, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_accept -> {
                save()
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun save() {
        showLoadingDialog()
        Single.fromCallable{
            val note = updateNote()
            if (noteId == null) {
                notes.insert(note)
            } else {
                notes.update(note)
            }
        }.doOnError{
            dismissLoadingDialog()
        }.succes {
            finish()
        }.subscribe()
    }

    private fun updateNote(): Note {
        val note = Note()
        noteId?.let {
            note.id = it
        }
        note.text = edit_text!!.text.toString()
        note.updatedAt = Date()
        return note
    }

    override fun showLoadingDialog() {
        if (!isFinishing) {
            LoadingDialogHelper.instance.showLoadingDialog(this)
        }
    }

    override fun dismissLoadingDialog() {
        if (!isFinishing) {
            LoadingDialogHelper.instance.dismissLoadingDialog(this)
        }
    }

    companion object {
        private val EXTRA_NOTE_ID = "NOTE_ID"
        private val EXTRA_NOTE_TEXT = "NOTE_TEXT"

        fun getAddIntent(context: Context?): Intent {
            return Intent(context, CreateActivity::class.java)
        }

        fun getEditIntent(context: Context?, noteId: Int): Intent {
            val intent = Intent(context, CreateActivity::class.java);
            intent.putExtra(EXTRA_NOTE_ID, noteId);
            return intent
        }
    }
}