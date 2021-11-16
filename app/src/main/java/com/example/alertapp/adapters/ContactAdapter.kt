package com.example.alertapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.example.alertapp.R
import com.example.alertapp.entities.Contact

class ContactAdapter(
    context: Context,
    var model: ArrayList<Contact>
) : Adapter<ContactAdapter.ViewHolder>(), View.OnClickListener{

    var inflater: LayoutInflater = LayoutInflater.from(context)

    lateinit var listener : View.OnClickListener

    override fun onClick(v: View){
        listener.onClick(v)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name : TextView = itemView.findViewById(R.id.contactName)
        var number : TextView = itemView.findViewById(R.id.contactPhone)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = inflater.inflate(R.layout.list_contacts, parent, false)
        view.setOnClickListener(this)
        return ViewHolder(view)
    }

    fun setOnClickListener(listener:View.OnClickListener){
        this.listener = listener
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val name:String = model[position].getContactName()
        val number:String = model[position].getPhoneNumber()
        holder.name.text = name
        holder.number.text = number
    }

    override fun getItemCount(): Int {
        return model.size
    }
}