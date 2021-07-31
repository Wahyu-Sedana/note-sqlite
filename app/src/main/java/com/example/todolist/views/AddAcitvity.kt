package com.example.todolist.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.todolist.R
import com.example.todolist.room.Constant
import com.example.todolist.room.Data
import com.example.todolist.room.NoteDB
import kotlinx.android.synthetic.main.activity_add_acitvity.*
import kotlinx.android.synthetic.main.item_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddAcitvity : AppCompatActivity() {
    val db by lazy {
        NoteDB(this)
    }
    private var noteId = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_acitvity)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Add Note"
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        setupView()
        setupListener()
    }

    private fun setupListener() {
        updateData.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                db.noteDao().upadateNote(
                    Data(
                        noteId,
                        addJudul.text.toString(),
                        addDeskripsi.text.toString()
                    )
                )
                finish()
            }
        }
    }

    private fun setupView() {
        when (intentType()) {
            Constant.TYPE_UPDATE -> {
                supportActionBar!!.title = "Edit Note"
                updateData.visibility = View.GONE
                updateData.visibility = View.VISIBLE
                getNote()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_save, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.save -> CoroutineScope(Dispatchers.IO).launch {
                db.noteDao().addNote(
                    Data(0, addJudul.text.toString(), addDeskripsi.text.toString())
                )
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun intentType(): Int {
        return intent.getIntExtra("intent_type", 0)
    }

    private fun getNote(){
        noteId = intent.getIntExtra("note_id", 0)
        CoroutineScope(Dispatchers.IO).launch {
            val notes = db.noteDao().getNote(noteId).get(0)
            addJudul.setText( notes.title )
            addDeskripsi.setText( notes.description )
        }
    }
}