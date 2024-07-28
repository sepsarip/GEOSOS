package pnj.exam.geosos.viewmodel

import androidx.lifecycle.ViewModel
import pnj.exam.geosos.DataKontak

class ContactViewModel : ViewModel() {

    private val selectedContacts = mutableListOf<DataKontak>()

    fun addContact(contact: DataKontak) {
        if (!selectedContacts.contains(contact)) {
            selectedContacts.add(contact)
        }
    }

    fun removeContact(contact: DataKontak) {
        selectedContacts.remove(contact)
    }

    fun getSelectedContacts(): List<DataKontak> {
        return selectedContacts
    }
}
