package nit2x.paba.room

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nit2x.paba.room.database.daftarBelanja
import nit2x.paba.room.database.daftarBelanjaDAO
import nit2x.paba.room.database.daftarBelanjaDB
import nit2x.paba.room.database.historyBarangDB

class MainActivity : AppCompatActivity() {
    private lateinit var DB : daftarBelanjaDB
    private lateinit var adapterDaftar : adapterDaftar
    private var arDaftar : MutableList<daftarBelanja> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        var _fabAdd = findViewById<FloatingActionButton>(R.id.fabAdd)
        var _btnHistory = findViewById<Button>(R.id.btnHistory)
        DB = daftarBelanjaDB.getDatabase(this)

        super.onStart()
        CoroutineScope(Dispatchers.Main).async {
            val daftarBelanja = DB.fundaftarBelanjaDAO().selectAll()
            Log.d("data ROOM", daftarBelanja.toString())
            adapterDaftar.isiData(daftarBelanja)
        }

        _fabAdd.setOnClickListener {
            startActivity(Intent(this,TambahDaftar::class.java))
        }

        adapterDaftar = adapterDaftar(arDaftar)
        var _rvDaftar = findViewById<RecyclerView>(R.id.rvNotes)
        _rvDaftar.layoutManager = LinearLayoutManager(this)
        _rvDaftar.adapter = adapterDaftar


        adapterDaftar.setOnItemClickCallback(
            object : adapterDaftar.OnItemClickCallback{
                override fun delData(dtBelanja: daftarBelanja) {
                    CoroutineScope(Dispatchers.IO).async {
                        DB.fundaftarBelanjaDAO().delete(dtBelanja)
                        val daftar = DB.fundaftarBelanjaDAO().selectAll()
                        withContext(Dispatchers.Main){
                            adapterDaftar.isiData(daftar)
                        }
                    }
                }
                override fun setActive(dtBelanja: daftarBelanja) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val historyDb = historyBarangDB.getDatabase(this@MainActivity)
                        historyDb.funhistoryBarangDAO().insert(dtBelanja)
                        historyDb.funhistoryBarangDAO().updateStatus(dtBelanja.id)

                        DB.fundaftarBelanjaDAO().delete(dtBelanja)

                        val daftar = DB.fundaftarBelanjaDAO().selectAll()
                        withContext(Dispatchers.Main) {
                            adapterDaftar.isiData(daftar)
                        }
                    }
                }

            })

        _btnHistory.setOnClickListener {
            val intent = Intent(this, TabelHistoryBarang::class.java)
            startActivity(intent)
        }

    }
}