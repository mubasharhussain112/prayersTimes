package com.muslim.prayer.times.ui.fragments

import android.Manifest
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.muslim.prayer.times.R
import com.muslim.prayer.times.databinding.FragmentHomeBinding
import com.muslim.prayer.times.service.LocationService
import com.muslim.prayer.times.utils.LocationHelper
import com.muslim.prayer.times.utils.MyLocationListener
import com.muslim.prayer.times.viewModel.PrayersTimesViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding

    private val mViewModel by viewModels<PrayersTimesViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        ContextCompat.startForegroundService(
            requireActivity(),
            Intent(requireActivity(), LocationService::class.java)
        )
        Dexter.withContext(requireActivity())
            .withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.POST_NOTIFICATIONS
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) { /* ... */
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest?>?,
                    token: PermissionToken?
                ) { /* ... */
                }
            }).check()


        LocationHelper().startListeningUserLocation(
            requireActivity(), object : MyLocationListener {
                override fun onLocationChanged(location: Location?) {
                    mViewModel.ViewModel()
                }
            })



        mViewModel.prayersLiveData.observe(viewLifecycleOwner) {
            binding.apply {
                tvFajjarTime.text = it[1]
                tvSunrise.text = it[2]
                tvDhudhrTime.text = it[3]
                tvAsrTime.text = it[4]
                tvMagribTime.text = it[5]
                tvIshaTime.text = it[6]
            }

            selectedPosition(it, 1, 2)
            selectedPosition(it, 2, 3)
            selectedPosition(it, 3, 4)
            selectedPosition(it, 4, 5)
        }



        return binding.root
    }

    private fun selectedPosition(prayersTimeList: ArrayList<String>, pos: Int, posSecond: Int) {
        val customCalendar = Calendar.getInstance()
        val year = Calendar.getInstance().get(Calendar.YEAR)
        val month = Calendar.getInstance().get(Calendar.MONTH)
        val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

        customCalendar.set(
            year,
            month,
            day,
            dateConversion(prayersTimeList[pos]).hours,
            dateConversion(prayersTimeList[pos]).minutes,
            0
        )
        customCalendar.set(
            year,
            month,
            day,
            dateConversion(prayersTimeList[posSecond]).hours,
            dateConversion(prayersTimeList[posSecond]).minutes,
            0
        )
        val customTime = customCalendar.timeInMillis
        val secondCustomTime = customCalendar.timeInMillis
        val currentTime = System.currentTimeMillis()//
        if (customTime > currentTime) {

            when (pos) {
                1 -> {
                    binding.llFajjarTime.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.black
                        )
                    )
                }
                2 -> {
                    binding.llSunrise.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.black
                        )
                    )
                }
                3 -> {
                    binding.llDhudhrTime.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.black
                        )
                    )
                }
                4 -> {
                    binding.llAsrTime.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.black
                        )
                    )
                }
                5 -> {
                    binding.llMagribTime.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.black
                        )
                    )
                }
                6 -> {
                    binding.llIshaTime.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.black
                        )
                    )
                }
                else->{
                binding.llFajjarTime.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
                    )
                )
                }
            }
        }
    }

    fun dateConversion(date: String): Date {
        val displayFormat = SimpleDateFormat("HH:mm")
        val parseFormat = SimpleDateFormat("hh:mm a")
        val date: Date = parseFormat.parse(date)
        return date
    }

}