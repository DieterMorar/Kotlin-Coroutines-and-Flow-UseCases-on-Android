package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase2.callbacks

import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.AndroidVersion
import com.lukaslechner.coroutineusecasesonandroid.mock.VersionFeatures
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SequentialNetworkRequestsCallbacksViewModel(
    private val mockApi: CallbackMockApi = mockApi()
) : BaseViewModel<UiState>() {
    private var getAndroidVersionCall: Call<List<AndroidVersion>>? = null
    private var getAndroidFeaturesCall: Call<VersionFeatures>? = null
    fun perform2SequentialNetworkRequest() {
        uiState.value = UiState.Loading
        getAndroidVersionCall = mockApi.getRecentAndroidVersions()
        getAndroidVersionCall!!.enqueue(object : Callback<List<AndroidVersion>> {
            override fun onResponse(
                call: Call<List<AndroidVersion>>,
                response: Response<List<AndroidVersion>>
            ) {
                if (response.isSuccessful) {
                    val mostRecentVersion = response.body()!!.last()
                    getAndroidFeaturesCall =
                        mockApi.getAndroidVersionFeatures(mostRecentVersion.apiLevel)
                    getAndroidFeaturesCall!!.enqueue(object : Callback<VersionFeatures> {
                        override fun onResponse(
                            p0: Call<VersionFeatures>,
                            p1: Response<VersionFeatures>
                        ) {
                            if (p1.isSuccessful) {
                                uiState.value = UiState.Success(p1.body()!!)
                            }
                        }

                        override fun onFailure(p0: Call<VersionFeatures>, p1: Throwable) {
                            uiState.value = UiState.Error("Something unexpected happens")
                        }

                    })
                } else {
                    uiState.value = UiState.Error("Bla")
                }
            }

            override fun onFailure(call: Call<List<AndroidVersion>>, p1: Throwable) {
                uiState.value = UiState.Error("Something unexpected happens")
            }

        })
    }

    override fun onCleared() {
        super.onCleared()
        getAndroidVersionCall?.cancel()
        getAndroidFeaturesCall?.cancel()
    }
}