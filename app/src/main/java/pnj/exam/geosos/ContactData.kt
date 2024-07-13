package pnj.exam.geosos

import android.provider.ContactsContract.CommonDataKinds.Phone

data class DataKontak(
    val name : String,
    val phone: String,
    var selected: Boolean = false
)