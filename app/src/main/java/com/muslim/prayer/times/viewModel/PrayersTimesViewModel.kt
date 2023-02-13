package com.muslim.prayer.times.viewModel

import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.muslim.prayer.times.repository.UserRepository
import com.muslim.prayer.times.service.LocationService.Companion.mLocation
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PrayersTimesViewModel @Inject constructor(private val userRepository: UserRepository) :
    ViewModel() {
    var prayersLiveData: MutableLiveData<ArrayList<String>> = MutableLiveData()

    fun ViewModel() {
        mLocation?.let {
            prayersLiveData.postValue(userRepository.prayerstimes(it))
        }
    }

}