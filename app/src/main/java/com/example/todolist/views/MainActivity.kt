package com.example.todolist.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.todolist.R
import com.example.todolist.room.Constant
import com.example.todolist.room.Data
import com.example.todolist.room.NoteDB
import com.example.todolist.views.adapter.ListAdapter
import com.varunjohn1990.iosdialogs4android.IOSDialog
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    val db by lazy {
        NoteDB(this)
    }
    private lateinit var listAdapter: com.example.todolist.views.adapter.ListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupRecyclerView()
        setupListener()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        CoroutineScope(Dispatchers.IO).launch {
            listAdapter.setData(db.noteDao().getNotes())
            withContext(Dispatchers.Main) {
                listAdapter.notifyDataSetChanged()
            }
        }
    }

    // pindah ke add activity
    private fun setupListener() {
        addData.setOnClickListener {
            intentEdit(Constant.TYPE_CREATE, 0)
        }
    }

    private fun setupRecyclerView() {
        listAdapter = ListAdapter(arrayListOf(), object : ListAdapter.OnAdapterListener{
            override fun onUpdate(data: Data) {
                intentEdit(Constant.TYPE_UPDATE, data.id_note)
            }

            override fun onDelete(data: Data) {
                deleteMessage(data)
            }

            private fun deleteMessage(data: Data) {
                IOSDialog.Builder(applicationContext)
                    .title("Konfirmasi Hapus")
                    .message("apakah anda akan menghapus note ${data.title}")
                    .positiveButtonText("Hapus")
                    .negativeButtonText("Tidak")
                    .positiveClickListener(IOSDialog.Listener(){
                        it.dismiss()
                        CoroutineScope(Dispatchers.IO).launch {
                            db.noteDao().deleteNote(data)
                            loadData()
                        }
                    })
                    .negativeClickListener(IOSDialog.Listener(){
                            it.dismiss()
                    })
                    .build()
                    .show()
            }
        })
        // menampilkan list
        tvList.apply {
            // menjadikan list ke grid
            tvList.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            tvList.adapter = listAdapter
        }
    }

    override fun onStart() {
        super.onStart()
        // list ditampilan menggunakan Corountines
        CoroutineScope(Dispatchers.IO).launch {
            val notes = db.noteDao().getNotes()
            Log.d("mainAcitivty", "dbResponse: $notes")
            withContext(Dispatchers.Main){
                listAdapter.setData(notes)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_setting, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        checkTheme()
        when(item.itemId){
            R.id.setting ->
//                startActivity(Intent(this, SettingActivity::class.java))
            chooseThemeDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    // cek tema ketika di klik
    private fun checkTheme() {
        when (SharedPreferens(this).darkMode) {
            0 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                delegate.applyDayNight()
            }
            1 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                delegate.applyDayNight()
            }
            2 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                delegate.applyDayNight()
            }
        }
    }

    // menampilkan alert dialog
    private fun chooseThemeDialog() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.choose_theme_text))
        val styles = arrayOf("Light","Dark","System default")
        val checkedItem = SharedPreferens(this).darkMode

        builder.setSingleChoiceItems(styles, checkedItem) { dialog, which ->
            // memilih aksi untuk perubahan tema
            when (which) {
                0 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    SharedPreferens(this).darkMode = 0
                    delegate.applyDayNight()
                    dialog.dismiss()
                }
                1 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    SharedPreferens(this).darkMode = 1
                    delegate.applyDayNight()
                    dialog.dismiss()
                }
                2 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    SharedPreferens(this).darkMode = 2
                    delegate.applyDayNight()
                    dialog.dismiss()
                }

            }
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun intentEdit(intent_type: Int, note_id: Int) {
        startActivity(
            Intent(this, AddAcitvity::class.java)
                .putExtra("intent_type", intent_type)
                .putExtra("note_id", note_id)
        )
    }
}