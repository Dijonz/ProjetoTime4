package com.dijonz.projeto_grupo4

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class AdapterEmergencia(private val nomes: List<String>, private val telefones: List<String>): RecyclerView.Adapter<AdapterEmergencia.EmergenciaViewHolder>() {

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }
    private lateinit var mListener: onItemClickListener

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener
    }

    override fun getItemCount(): Int {
        return nomes.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmergenciaViewHolder {
        return EmergenciaViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycle_type, parent, false), mListener
        )
    }

    override fun onBindViewHolder(holder: EmergenciaViewHolder, position: Int) {
        val emergencia = nomes[position]
        val temergencia = telefones[position]
        holder.emergenciaNome.text = "   ${emergencia}: ${temergencia}."





    }
    class EmergenciaViewHolder(itemView: View, listener: onItemClickListener): RecyclerView.ViewHolder(itemView) {
        val emergenciaNome: TextView = itemView.findViewById(R.id.tvNomeeee)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(absoluteAdapterPosition)
            }
        }

    }
}
