package campus.tech.kakao.map.Room

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import campus.tech.kakao.map.kakaoAPI.NetworkService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapItemViewModel @Inject constructor(
    private val mapDB: MapDatabase,
    private val networkService: NetworkService
) : ViewModel() {

    private val _kakaoMapItemList: MutableLiveData<List<MapItemEntity>> = MutableLiveData()
    val kakaoMapItemList: LiveData<List<MapItemEntity>> get() = _kakaoMapItemList

    private val _selectItemList: MutableLiveData<List<MapItemEntity>> = MutableLiveData()
    val selectItemList: LiveData<List<MapItemEntity>> get() = _selectItemList

    init {
        makeAllSelectItemList()
    }

    fun makeAllSelectItemList() {
        _selectItemList.postValue(mapDB.selectMapItemDao().getAll())
    }

    suspend fun searchKakaoMapItem(category: String) {
        _kakaoMapItemList.postValue(networkService.searchKakaoMapItem(category))
    }

    fun insertSelectItem(mapItem: MapItemEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            if (mapDB.selectMapItemDao().checkItemInDB(mapItem.kakaoId) > 0) {
                mapDB.selectMapItemDao().deleteItem(mapItem.kakaoId)
            }
            mapDB.selectMapItemDao().insertItem(mapItem)
        }
        makeAllSelectItemList()
    }

    fun deleteSelectItem(mapItem: MapItemEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            mapDB.selectMapItemDao().deleteItem(mapItem.kakaoId)
        }
        makeAllSelectItemList()
    }
}