package com.udacity.notepad

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.udacity.notepad.crud.CreateActivity
import com.udacity.notepad.recycler.NotesAdapter
import com.udacity.notepad.util.loadable.LoadingDialogHelper
import com.udacity.notepad.util.SpaceItemDecoration
import com.udacity.notepad.util.loadable.Loadable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), Loadable {


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener { startActivity(CreateActivity.getAddIntent(this@MainActivity)) }
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.addItemDecoration(SpaceItemDecoration(this, R.dimen.margin_small))
        val notesAdapter = NotesAdapter(this)
        setLoader(notesAdapter)
        setEditor(notesAdapter);
        recycler.adapter = notesAdapter
    }

    @SuppressLint("CheckResult")
    fun setEditor(notesAdapter: NotesAdapter) {
        notesAdapter.getEditNote().subscribe{
            startActivity(CreateActivity.getEditIntent(this@MainActivity, it))
        }
    }

    @SuppressLint("CheckResult")
    fun setLoader(notesAdapter: NotesAdapter) {
        notesAdapter.getShowLoading().subscribe{
            showLoadingDialog()
        }
        notesAdapter.getDismissLoading().subscribe{
            dismissLoadingDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    public override fun onDestroy() {
        super.onDestroy()
        recycler.adapter = null
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean { // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean { // Handle action bar item clicks here. The action bar will
// automatically handle clicks on the Home/Up button, so long
// as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun refresh() {
        (recycler.adapter as NotesAdapter).refresh()
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
}