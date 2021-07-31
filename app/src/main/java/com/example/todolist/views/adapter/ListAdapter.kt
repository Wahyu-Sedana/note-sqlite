package com.example.todolist.views.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.room.Data
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.item_list.view.*

class ListAdapter(private val data: ArrayList<Data>, var listener: OnAdapterListener):RecyclerView.Adapter<ListAdapter.ListViewHolder>() {
    class ListViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)

        fun setData(list: List<Data>){
            data.clear()
            data.addAll(list)
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
       return ListViewHolder(
           LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
       )
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val note = data[position]
        holder.itemView.judul.text = note.title
        holder.itemView.deskripsi.text = note.description
        holder.itemView.card.setOnClickListener{listener.onUpdate(note)}
        holder.itemView.deleteItem.setOnClickListener { listener.onDelete(note) }
    }

    override fun getItemCount(): Int = data.size

    interface OnAdapterListener{
        fun onUpdate(data: Data)
        fun onDelete(data: Data)
    }
}