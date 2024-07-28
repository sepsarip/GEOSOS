package pnj.exam.geosos.fragment

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.telephony.SmsManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.*
import pnj.exam.geosos.R
import pnj.exam.geosos.database.DatabaseHelper
import pnj.exam.geosos.viewmodel.ContactViewModel
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var sendButton: Button
    private lateinit var sendButtonSMS: Button
    private lateinit var tvLocation: TextView
    private var currentLocation: Location? = null
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var contactViewModel: ContactViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        dbHelper = DatabaseHelper(requireContext())
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        sendButton = view.findViewById(R.id.btnSendSOS)
        sendButtonSMS = view.findViewById(R.id.btnSendSOS_SMS)
        tvLocation = view.findViewById(R.id.tvLocation)

        contactViewModel = ViewModelProvider(requireActivity()).get(ContactViewModel::class.java)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                currentLocation = locationResult.lastLocation
                updateLocationUI()
            }
        }

        getCurrentLocation()

        sendButton.setOnClickListener {
            sendMessageWA()
        }

        sendButtonSMS.setOnClickListener {
            sendMessageSMS()
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        getCurrentLocation()
    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        if (!isLocationEnabled()) {
            Toast.makeText(requireContext(), "Please enable location services", Toast.LENGTH_SHORT).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                currentLocation = location
                updateLocationUI()
            } else {
                // Request an active location update if lastLocation is null
                val locationRequest = LocationRequest.create().apply {
                    interval = 10000
                    fastestInterval = 5000
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                }
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
            }
        }.addOnFailureListener { e ->
            Log.e("HomeFragment", "Failed to get location: ${e.message}")
            Toast.makeText(requireContext(), "Failed to get location, permission issue.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateLocationUI() {
        val location = currentLocation
        if (location != null) {
            val locationText = "Current location: Latitude: ${location.latitude}, Longitude: ${location.longitude}"
            tvLocation.text = locationText
        } else {
            tvLocation.text = "Failed to update ui location."
        }
    }

    private fun sendMessageWA() {
        val location = currentLocation
        if (location != null) {
            val message = "Current location: https://maps.google.com/?q=${location.latitude},${location.longitude}"
            // via WhatsApp
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.setPackage("com.whatsapp")
            intent.putExtra(Intent.EXTRA_TEXT, message)

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val date = dateFormat.format(Date())
            val time = timeFormat.format(Date())

            try {
                startActivity(intent)
                dbHelper.insertHistory(date, time, message)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "WhatsApp not installed.", Toast.LENGTH_SHORT).show()
                dbHelper.insertHistory(date, time, message)
            }
        } else {
            Toast.makeText(requireContext(), "Failed to get location in whatsapp send.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendMessageSMS() {
        val location = currentLocation
        if (location != null) {
            val message = "Current location: https://maps.google.com/?q=${location.latitude},${location.longitude}"

            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.SEND_SMS), SMS_PERMISSION_REQUEST_CODE)
                return
            }

            val selectedContacts = contactViewModel.getSelectedContacts()
            if (selectedContacts.isEmpty()) {
                Toast.makeText(requireContext(), "No contacts selected.", Toast.LENGTH_SHORT).show()
                return
            }

            val sentToNumbers = mutableListOf<String>()
            try {
                val smsManager = SmsManager.getDefault()
                for (contact in selectedContacts) {
                    smsManager.sendTextMessage(contact.phone, null, message, null, null)
                    sentToNumbers.add(contact.phone)
                }
                Toast.makeText(requireContext(), "Message sent to selected contacts.", Toast.LENGTH_SHORT).show()
                Toast.makeText(requireContext(), "Message sent to: ${sentToNumbers.joinToString(", ")}", Toast.LENGTH_LONG).show()

                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                val date = dateFormat.format(Date())
                val time = timeFormat.format(Date())
                dbHelper.insertHistory(date, time, message)

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Failed to send message.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Failed to get location in sms send.", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val SMS_PERMISSION_REQUEST_CODE = 2
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
