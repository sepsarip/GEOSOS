package pnj.exam.geosos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactAdapter(
    private val kontak: List<DataKontak>,
    private val onItemCheckListener: OnItemCheckListener
) : RecyclerView.Adapter<ContactAdapter.DataKontakViewHolder>() {

    interface OnItemCheckListener {
        fun onItemCheck(item: DataKontak)
        fun onItemUncheck(item: DataKontak)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataKontakViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_item, parent, false)
        return DataKontakViewHolder(view)
    }

    override fun onBindViewHolder(holder: DataKontakViewHolder, position: Int) {
        val contact = kontak[position]
        holder.name.text = contact.name
        holder.phone.text = contact.phone
        holder.checkBox.isChecked = contact.selected

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                contact.selected = true
                onItemCheckListener.onItemCheck(contact)
            } else {
                contact.selected = false
                onItemCheckListener.onItemUncheck(contact)
            }
        }
    }

    override fun getItemCount() = kontak.size

    class DataKontakViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.name)
        val phone: TextView = itemView.findViewById(R.id.phone)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)
    }
}
