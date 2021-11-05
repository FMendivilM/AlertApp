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

class ContactAdapter : Adapter<ContactAdapter.ViewHolder>, View.OnClickListener{

    var model: ArrayList<Contact>
    var inflater: LayoutInflater

    lateinit var listener : View.OnClickListener

    constructor(context: Context, model:ArrayList<Contact>){
        this.inflater = LayoutInflater.from(context)
        this.model = model
    }

    override fun onClick(v: View){
        if(listener!=null){
            listener.onClick(v)
        }
    }

    class ViewHolder : RecyclerView.ViewHolder{
        lateinit var name : TextView
        lateinit var number : TextView
        constructor(itemView: View) : super(itemView) {
            name = itemView.findViewById(R.id.contactName)
            number = itemView.findViewById(R.id.contactPhone)
        }
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