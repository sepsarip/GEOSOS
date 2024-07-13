package pnj.exam.geosos

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.provider.ContactsContract

class ContactFragment : Fragment(), ContactAdapter.OnItemCheckListener {

    private lateinit var selectedContacts: MutableList<DataKontak>
    private lateinit var recyclerView: RecyclerView

    private val REQUEST_CONTACTS_PERMISSION = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_contact, container, false)

        selectedContacts = mutableListOf()

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view_contacts)
        recyclerView.layoutManager = LinearLayoutManager(context)

        requestContactsPermission()

        return view
    }

    private fun requestContactsPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_CONTACTS),
                REQUEST_CONTACTS_PERMISSION
            )
        } else {
            loadContacts()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CONTACTS_PERMISSION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                loadContacts()
            } else {
                // Permission denied
            }
        }
    }

    private fun loadContacts() {
        val contactList = mutableListOf<DataKontak>()
        val contentResolver = requireContext().contentResolver
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )

        cursor?.use {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val phoneIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (it.moveToNext()) {
                val name = it.getString(nameIndex)
                val phone = it.getString(phoneIndex)
                contactList.add(DataKontak(name, phone))
            }
        }

        val adapter = ContactAdapter(contactList, this)
        recyclerView.adapter = adapter
    }

    override fun onItemCheck(item: DataKontak) {
        selectedContacts.add(item)
    }

    override fun onItemUncheck(item: DataKontak) {
        selectedContacts.remove(item)
    }
}
