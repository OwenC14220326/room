package nit2x.paba.room

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nit2x.paba.room.database.daftarBelanja
import nit2x.paba.room.database.daftarBelanjaDB

class adapterDaftar (private val daftarBelanja : MutableList<daftarBelanja>):
    RecyclerView.Adapter<adapterDaftar.ListViewHolder>(){
    class ListViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        var _tvItemBarang = itemView.findViewById<TextView>(R.id.tvItemBarang)
        var _tvJumlahBarang = itemView.findViewById<TextView>(R.id.tvJumlah)
        var _tvTanggal = itemView.findViewById<TextView>(R.id.tvTanggal)

        var _btnEdit = itemView.findViewById<Button>(R.id.btnEdit)
        var _btnDelete = itemView.findViewById<Button>(R.id.btnDelete)
        var _btnActive = itemView.findViewById<Button>(R.id.btnActive)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): adapterDaftar.ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(
            R.layout.item_recycler, parent,
            false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        var daftar = daftarBelanja[position]

        holder._tvTanggal.setText(daftar.tanggal)
        holder._tvItemBarang.setText(daftar.item)
        holder._tvJumlahBarang.setText(daftar.jumlah)

        holder._btnEdit.setOnClickListener {
            val intent = Intent(it.context, TambahDaftar::class.java)
            intent.putExtra("id", daftar.id)
            intent.putExtra("addEdit", 1)
            it.context.startActivity(intent)
        }

        holder._btnDelete.setOnClickListener {
            onItemClickCallback.delData(daftar)
        }

        holder._btnActive.setOnClickListener {
            onItemClickCallback.setActive(daftar)
        }

    }

    interface OnItemClickCallback {
        fun delData(dtBelanja: daftarBelanja)
        fun setActive(dtBelanja: daftarBelanja)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun isiData(daftar: List<daftarBelanja>){
        daftarBelanja.clear()
        daftarBelanja.addAll(daftar)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return daftarBelanja.size
    }
}