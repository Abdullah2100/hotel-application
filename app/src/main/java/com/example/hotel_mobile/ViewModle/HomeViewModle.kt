package com.example.hotel_mobile.ViewModle

import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.hotel_mobile.Data.Repository.HotelRepository
import com.example.hotel_mobile.Data.Room.AuthDao
import com.example.hotel_mobile.Data.Room.AuthModleEntity
import com.example.hotel_mobile.Di.IoDispatcher
import com.example.hotel_mobile.Di.MainDispatcher
import com.example.hotel_mobile.Dto.AuthResultDto
import com.example.hotel_mobile.Dto.LoginDto
import com.example.hotel_mobile.Dto.RoomDto
import com.example.hotel_mobile.Dto.RoomTypeDto
import com.example.hotel_mobile.Dto.SingUpDto
import com.example.hotel_mobile.Dto.response.BookingResponseDto
import com.example.hotel_mobile.Dto.response.UserDto
import com.example.hotel_mobile.Modle.response.BookingModel
import com.example.hotel_mobile.Modle.Request.BookingModleHolder
import com.example.hotel_mobile.Modle.NetworkCallHandler
import com.example.hotel_mobile.Modle.Request.RoomCreationModel
import com.example.hotel_mobile.Modle.Request.RoomImageCreation
import com.example.hotel_mobile.Modle.Request.UserUpdateMoule
import com.example.hotel_mobile.Modle.Screens
import com.example.hotel_mobile.Modle.response.RoomModel
import com.example.hotel_mobile.Modle.enDropDownDateType
import com.example.hotel_mobile.Modle.enNetworkStatus
import com.example.hotel_mobile.Modle.response.PersonModel
import com.example.hotel_mobile.Modle.response.RoomTypeModel
import com.example.hotel_mobile.Modle.response.UserModel
import com.example.hotel_mobile.Util.DtoToModule.toBookingModel
import com.example.hotel_mobile.Util.DtoToModule.toRoomModel
import com.example.hotel_mobile.Util.DtoToModule.toRoomTypeModel
import com.example.hotel_mobile.Util.DtoToModule.toUserModel
import com.example.hotel_mobile.Util.General
import com.example.hotel_mobile.Util.MoudelToDto.toBookingDto
import com.example.hotel_mobile.Util.Validation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json.Default.decodeFromString
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HomeViewModle @Inject constructor(
    val homeRepository: HotelRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    private val authDao: AuthDao
) : ViewModel() {

    private val _rooms = MutableStateFlow<MutableList<RoomModel>?>(null)
    val rooms = _rooms.asStateFlow()

    private val _myRooms = MutableStateFlow<MutableList<RoomModel>?>(null)
    val myRooms = _myRooms.asStateFlow()

    private val _statusChange = MutableStateFlow<enNetworkStatus>(enNetworkStatus.None)
    val statusChange: StateFlow<enNetworkStatus> = _statusChange.asStateFlow()

    private val _bookedStartBookingDayAtMonthAndYear = MutableStateFlow<Map<Int, Int>?>(null)
    val bookedStartBookingDayAtMonthAndYear = _bookedStartBookingDayAtMonthAndYear.asStateFlow();


    private val _bookedEndBookingDayAtMonthAndYear = MutableStateFlow<Map<Int, Int>?>(null)
    val bookedEndBookingDayAtMonthAndYear = _bookedEndBookingDayAtMonthAndYear.asStateFlow();


    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private  var _authDataStreem = authDao.readChunksLive().flowOn(ioDispatcher)
    var authDataStreem = _authDataStreem


    private val _bookingData = MutableStateFlow<BookingModleHolder>(
        BookingModleHolder(
            startYear = General.getCurrentYear(),
            startMonth = General.getCurrentMonth(),
            startDay = null,
            startTime = "",
            endYear = General.getCurrentYear(),
            endMonth = General.getCurrentMonth(),
            endDay = null,
            endTime = "",
            roomId = null
        )
    )
    val bookingData: StateFlow<BookingModleHolder> = _bookingData.asStateFlow()


    private val _bookingsList = MutableStateFlow<List<BookingModel>?>(null)
    val bookingsList = _bookingsList.asStateFlow()


    private val _bookingsBelongToMyRoomList = MutableStateFlow<List<BookingModel>?>(null)
    val bookingsBelongToMyRoomList = _bookingsBelongToMyRoomList.asStateFlow()

    private val _roomData = MutableStateFlow<RoomCreationModel>(RoomCreationModel())
    val roomData = _roomData.asStateFlow()

    private val _roomTypesData = MutableStateFlow<List<RoomTypeModel>?>(null)
    val roomTypesData = _roomTypesData.asStateFlow()

    private val _myInfo = MutableStateFlow<UserModel?>(null)
    val myInfo = _myInfo.asStateFlow()


    suspend fun updateMyInfo(
        name: String? = null,
        email: String? = null,
        phone: String? = null,
        address: String? = null,
        userName: String? = null,
        password: String? = null,
        imagePath: String? = null,
    ) {
        viewModelScope.launch {
            if (name != null) {
                val personModel = _myInfo.value?.personData?.copy(name = name)
                val newMyInfo = _myInfo.value?.copy(personData = personModel!!)
                _myInfo.emit(newMyInfo)
            }
            if (email != null) {
                val personModel = _myInfo.value?.personData?.copy(email = email)
                val newMyInfo = _myInfo.value?.copy(personData = personModel!!)
                _myInfo.emit(newMyInfo)
            }
            if (phone != null) {
                val personModel = _myInfo.value?.personData?.copy(phone = phone)
                val newMyInfo = _myInfo.value?.copy(personData = personModel!!)
                _myInfo.emit(newMyInfo)
            }
            if (address != null) {
                val personModel = _myInfo.value?.personData?.copy(address = address)
                val newMyInfo = _myInfo.value?.copy(personData = personModel!!)
                _myInfo.emit(newMyInfo)
            }
            if (userName != null) {
                val newMyInfo = _myInfo.value?.copy(userName = userName)
                _myInfo.emit(newMyInfo)
            }
            if (password != null) {
                val newMyInfo = _myInfo.value?.copy(password = password)
                _myInfo.emit(newMyInfo)
            }
            if (imagePath != null) {
                val newMyInfo = _myInfo.value?.copy(imagePath = imagePath)
                _myInfo.emit(newMyInfo)
            }
        }
    }

    suspend fun deletedImage(path: String, isThumbnail: Boolean? = false) {
        if (_roomData.value.images != null) {
            val imagesHolder =
                _roomData.value.copy(images = _roomData.value.images!!.filter { it.data.path != path }
                    .toMutableList())
            _roomData.emit(imagesHolder)
        }
    }

    fun setRoomThumnail(image: RoomImageCreation, isDeleted: Boolean = false) {

        viewModelScope.launch(mainDispatcher + errorHandling) {
            if (isDeleted) {
                deletedImage(image.data.path, isDeleted)
            } else {
                val imagesCop = _roomData.value.images;
                val imagesList = mutableListOf<RoomImageCreation>()
                when (imagesCop == null) {
                    true -> {
                        imagesList.add(image)
                    }

                    else -> {
                        imagesList.addAll(_roomData.value.images!!)
                        imagesList.add(image)
                    }
                }
                val copyRoomData = _roomData.value.copy(images = imagesList)
                _roomData.emit(copyRoomData)
            }

        }
    }

    fun setRoomData(
        pricePerNight: Double? = null,
        capacity: Int? = null,
        roomtypeid: UUID? = null,
        bedNumber: Int? = null,
        location: String? = null,
        latitude: Double? = null,
        longitude: Double? = null
    ) {
        viewModelScope.launch {
            if (pricePerNight != null) {
                val roomDataCopy = _roomData.value.copy(pricePerNight = pricePerNight)
                _roomData.emit(roomDataCopy)
            }
            if (capacity != null) {
                val roomDataCopy = _roomData.value.copy(capacity = capacity)
                _roomData.emit(roomDataCopy)
            }
            if (roomtypeid != null) {
                val roomDataCopy = _roomData.value.copy(roomtypeid = roomtypeid)
                _roomData.emit(roomDataCopy)
            }
            if (bedNumber != null) {
                val roomDataCopy = _roomData.value.copy(bedNumber = bedNumber)
                _roomData.emit(roomDataCopy)
            }
            if (location != null) {
                val roomDataCopy = _roomData.value.copy(location = location)
                _roomData.emit(roomDataCopy)
            }

            if (latitude != null) {
                val roomDataCopy = _roomData.value.copy(latitude = latitude)
                _roomData.emit(roomDataCopy)
            }
            if (longitude != null) {
                val roomDataCopy = _roomData.value.copy(longitude = longitude)
                _roomData.emit(roomDataCopy)
            }
        }

    }

    fun handlTheSelectionDialog(
        day: Int,
        month: Int,
        year: Int,
        hour: Int? = 0,
        minit: Int? = 0,
        enDropTyp: enDropDownDateType
    ) {
        viewModelScope.launch {
            when (enDropTyp) {
                enDropDownDateType.YearStartBooking -> {
                    Log.d("timeResultData", "1")
                    if (bookingData.value.startYear != year) {
                        _bookedStartBookingDayAtMonthAndYear.emit(null)
                        getBookedBookingDayAt(
                            year = year,
                            month = bookingData.value.startMonth
                        )
                    }
                    val newData = _bookingData.value.copy(startYear = year)
                    _bookingData.value = newData
                }

                enDropDownDateType.MonthStartBooking -> {
                    Log.d("timeResultData", "2")
                    if (bookingData.value.startMonth != month) {
                        _bookedStartBookingDayAtMonthAndYear.emit(null)
                        getBookedBookingDayAt(
                            month = month, year = bookingData.value.startYear
                        )
                    }
                    val newData = _bookingData.value.copy(startMonth = month)
                    _bookingData.value = newData
                }

                enDropDownDateType.DayStartBooking -> {
                    Log.d("timeResultData", "3")
                    val newData = _bookingData.value.copy(startDay = day)
                    _bookingData.value = newData
                }

                enDropDownDateType.TimeStartBooking -> {
                    Log.d("timeResultData", "4")
                    val newData = _bookingData.value.copy(startTime = "$hour:$minit")
                    _bookingData.value = newData

                }

                enDropDownDateType.YearEndBooking -> {
                    Log.d("timeResultData", "5")
                    if (bookingData.value.endYear != year) {
                        _bookedEndBookingDayAtMonthAndYear.emit(null)

                        getBookedBookingDayAt(
                            year = year,
                            month = bookingData.value.endMonth,
                            dropDownType = enDropDownDateType.MonthEndBooking
                        )
                    }
                    val newData = _bookingData.value.copy(endYear = year)
                    _bookingData.value = newData


                }

                enDropDownDateType.MonthEndBooking -> {
                    Log.d("timeResultData", "6")
                    if (bookingData.value.endMonth != month) {
                        _bookedEndBookingDayAtMonthAndYear.emit(null)
                        getBookedBookingDayAt(
                            year = bookingData.value.endYear,
                            month = month,
                            dropDownType = enDropDownDateType.MonthEndBooking

                        )
                    }

                    val newData = _bookingData.value.copy(endMonth = month)
                    _bookingData.value = newData
                }

                enDropDownDateType.DayEndBooking -> {
                    Log.d("timeResultData", "7")
                    val newData = _bookingData.value.copy(endDay = day)
                    _bookingData.value = newData

                }

                enDropDownDateType.TimeEndBooking -> {
                    Log.d("timeResultData", "8")
                    val newData = _bookingData.value.copy(endTime = "$hour:$minit")
                    _bookingData.value = newData

                }
            }

        }

    }


    val errorHandling = CoroutineExceptionHandler { _, ex ->
        viewModelScope.launch {
            _errorMessage.update {
                ex.message
            }
        }
    }


    private fun validateBookingCreation(
        bookingData: BookingModleHolder,
    ): String? {

        var message: String? = null;
        if (bookingData.startDay == null) {
            message = "بداية يوم الحجز لا يمكن ان يكون فارغاا"
        } else if (bookingData.startTime.isNullOrEmpty())
            message = "بداية وقت الحجز لا يمكن ان يكون فارغا"
        else if (bookingData.endDay == null) {
            message = "نهاية يوم الحجز لا يمكن ان يكون فارغاا"
        } else if (bookingData.endTime.isNullOrEmpty())
            message = "نهاية وقت الحجز لا يمكن ان يكون فارغا"


        return message;
    }

    fun getRooms(pageNumber: Int = 1, isBelongToMe: Boolean = false) {
        viewModelScope.launch(mainDispatcher + errorHandling) {

            when (val result = homeRepository.getRooms(pageNumber, isBelongToMe)) {
                is NetworkCallHandler.Successful<*> -> {
                    val roomData = result.data as List<RoomDto>?
                    if (!roomData.isNullOrEmpty()) {
                        var roomDataToMutale = roomData
                            .map { listData -> listData.toRoomModel() }
                            .toMutableList()
                        when (isBelongToMe) {
                            true -> {
                                _myRooms.emit(roomDataToMutale)
                            }

                            else -> {
                                _rooms.emit(roomDataToMutale)
                            }
                        }
                    } else {

                        when (isBelongToMe) {
                            true -> {
                                if (_myRooms.value == null)
                                    _myRooms.emit(mutableListOf<RoomModel>())
                            }

                            else -> {
                                if (_rooms.value == null)
                                    _rooms.emit(mutableListOf<RoomModel>())
                            }
                        }
                    }
                }

                is NetworkCallHandler.Error -> {
                    when (isBelongToMe) {
                        true -> {
                            if (_myRooms.value == null)
                                _myRooms.emit(mutableListOf<RoomModel>())
                        }

                        else -> {
                            if (_rooms.value == null)
                                _rooms.emit(mutableListOf<RoomModel>())
                        }
                    }

                    throw Exception(result.data?.replace("\"", ""));
                }

                else -> {
                    when (isBelongToMe) {
                        true -> {
                            if (_myRooms.value == null)
                                _myRooms.emit(mutableListOf<RoomModel>())
                        }

                        else -> {
                            if (_rooms.value == null)
                                _rooms.emit(mutableListOf<RoomModel>())
                        }
                    }
                    throw Exception("unexpected Stat");
                }
            }

        }
    }


    fun getMyRoomTypes() {
        viewModelScope.launch(mainDispatcher + errorHandling) {

            when (val result = homeRepository.getRoomType()) {
                is NetworkCallHandler.Successful<*> -> {
                    val roomTypesData = result.data as List<RoomTypeDto>?
                    if (!roomTypesData.isNullOrEmpty()) {

                        _roomTypesData.emit(roomTypesData.map { it.toRoomTypeModel() })
                    } else {

                        if (_roomTypesData.value == null)
                            _roomTypesData.emit(mutableListOf())
                    }
                }

                is NetworkCallHandler.Error -> {
                    if (_roomTypesData.value == null)
                        _roomTypesData.emit(mutableListOf())


                }

                else -> {
                    if (_myRooms.value == null)
                        _roomTypesData.emit(mutableListOf())
                }
            }

        }
    }

    private suspend fun validationInput(
        snackbarHostState: SnackbarHostState
    ): Boolean {

        var message = ""
        if (_roomData.value.images?.firstOrNull { it.isThumbnail == true } == null) {
            message = "لا بد من تحديد الصورة الاساسية للغرفة"
        } else if (_roomData.value.images?.map { it.isThumbnail != true } == null || _roomData.value.images!!.map { it.isThumbnail != true }.size == 0)
            message = "لا بد من ادراج صور للغرفة"
        else if (_roomData.value.pricePerNight == null)
            message = "لا بد من تحديد سعر الغرفة لليلة الواحدة"
        else if (_roomData.value.capacity == null)
            message = "لا بد من تحديد سعة الغرفة"
        else if (_roomData.value.bedNumber == null)
            message = "لا بد من تحديد عدد السرائر داخل الغرفة"
        else if (_roomData.value.latitude == null || _roomData.value.latitude == null)
            message = "لا بد من تحديد الموقع الحالي للغرفة"

        if (message.isNotEmpty()) {
            _statusChange.emit(enNetworkStatus.None)
            snackbarHostState.showSnackbar(message)
            return false
        }
        return true;
    }

    fun createRoom(
        snackbarHostState: SnackbarHostState,
        navController: NavHostController
    ) {
        viewModelScope.launch(mainDispatcher + errorHandling) {
            if (!validationInput(snackbarHostState)) {

            } else {
                _statusChange.emit(enNetworkStatus.Loading)

                val resultValidatoin = validationInput(snackbarHostState)
                if (resultValidatoin) {
                    delay(1000L)
                    when (val result = homeRepository.createRoom(_roomData.value)) {
                        is NetworkCallHandler.Successful<*> -> {
                            val roomData = result.data as String;
                            _roomData.emit(RoomCreationModel())
                            _statusChange.update { enNetworkStatus.Complate }
                            navController.popBackStack()
                        }

                        is NetworkCallHandler.Error -> {
                            _statusChange.update { enNetworkStatus.Error }

                            throw Exception(result.data?.replace("\"", ""));

                        }

                        else -> {
                            _statusChange.update { enNetworkStatus.Error }

                            throw Exception("unexpected Stat");

                        }
                    }

                }
            }


        }
    }

    fun logout(){
      viewModelScope.launch {

          authDao.nukeTable()
      }
    }


    fun createBooking(
        bookingData: BookingModleHolder,
        errorMessage: MutableState<String?>,
        showBottomSheet: MutableState<Boolean>,
        roomId: UUID
    ) {
        viewModelScope.launch(mainDispatcher + errorHandling) {
            val newData = _bookingData.value.copy(roomId = roomId)
            _bookingData.emit(newData)

            errorMessage.value = validateBookingCreation(bookingData = bookingData)
            if (errorMessage.value != null) {
                showBottomSheet.value = true
            } else {
                _statusChange.emit(enNetworkStatus.Loading)
                val result = homeRepository.createBooking(
                    bookingData.toBookingDto()
                )

                when (result) {
                    is NetworkCallHandler.Successful<*> -> {

                        _statusChange.emit(enNetworkStatus.Complate)
                    }

                    is NetworkCallHandler.Error -> {
                        _statusChange.emit(enNetworkStatus.Error)
                        throw Exception(result.data?.replace("\"", ""));
                    }

                    else -> {
                        _statusChange.emit(enNetworkStatus.Error)

                        throw Exception("unexpected Stat");
                    }
                }

            }

        }
    }


    fun getBookingData(pageNumber: Int = 1) {
        viewModelScope.launch(mainDispatcher + errorHandling) {

            when (val result = homeRepository.getUserBookings(pageNumber)) {
                is NetworkCallHandler.Successful<*> -> {
                    val bookingDataHolder = result.data as List<BookingResponseDto>?
                    if (!bookingDataHolder.isNullOrEmpty()) {
                        val roomDataToMutale = bookingDataHolder
                            .map { listData -> listData.toBookingModel() }
                            .toMutableList()

                        _bookingsList.emit(roomDataToMutale)
                    } else {

                        if (_bookingsList.value == null)
                            _bookingsList.emit(emptyList())
                    }
                }

                is NetworkCallHandler.Error -> {
                    if (_bookingsList.value == null)
                        _bookingsList.emit(emptyList())


                    throw Exception(result.data?.replace("\"", ""));
                }

                else -> {
                    if (_rooms.value == null)
                        _rooms.emit(mutableListOf<RoomModel>())

                    throw Exception("unexpected Stat");
                }
            }

        }
    }


    fun getBookingBelongToMyRoom(pageNumber: Int = 1) {
        viewModelScope.launch(mainDispatcher + errorHandling) {

            when (val result = homeRepository.getBookingBelongToMyRoom(pageNumber)) {
                is NetworkCallHandler.Successful<*> -> {
                    val bookingDataHolder = result.data as List<BookingResponseDto>?
                    if (!bookingDataHolder.isNullOrEmpty()) {
                        val roomDataToMutale = bookingDataHolder
                            .map { listData -> listData.toBookingModel() }
                            .toMutableList()

                        _bookingsBelongToMyRoomList.emit(roomDataToMutale)
                    } else {

                        if (_bookingsBelongToMyRoomList.value == null)
                            _bookingsBelongToMyRoomList.emit(emptyList())
                    }
                }

                is NetworkCallHandler.Error -> {
                    if (_bookingsBelongToMyRoomList.value == null)
                        _bookingsBelongToMyRoomList.emit(emptyList())


                    throw Exception(result.data?.replace("\"", ""));
                }

                else -> {
                    if (_bookingsBelongToMyRoomList.value == null)
                        _bookingsBelongToMyRoomList.emit(mutableListOf())

                    throw Exception("unexpected Stat");
                }
            }

        }
    }

    fun getBookedBookingDayAt(
        month: Int = General.getCurrentMonth(),
        year: Int = General.getCurrentYear(),
        dropDownType: enDropDownDateType? = enDropDownDateType.MonthStartBooking
    ) {
        viewModelScope.launch(ioDispatcher + errorHandling) {
            val result =
                homeRepository.getBookingDayAtSpecficMonthAndYear(year = year, month = month)
            when (result) {
                is NetworkCallHandler.Successful<*> -> {
                    val bookedDate = result.data as List<String>
                    val bookedDateToMap =
                        bookedDate.map { it.trim().toInt() to it.trim().toInt() }.toMap()
                    if (dropDownType == enDropDownDateType.MonthStartBooking) {
                        _bookedStartBookingDayAtMonthAndYear.emit(
                            if (bookedDateToMap.size > 0) bookedDateToMap else emptyMap()
                        )
                        if (_bookedEndBookingDayAtMonthAndYear.value == null) {
                            _bookedEndBookingDayAtMonthAndYear.emit(
                                if (bookedDateToMap.size > 0) bookedDateToMap else emptyMap()
                            )
                        }
                    } else if (dropDownType == enDropDownDateType.MonthEndBooking) {
                        _bookedEndBookingDayAtMonthAndYear.emit(
                            if (bookedDateToMap.size > 0) bookedDateToMap else emptyMap()
                        )
                    }
                }

                is NetworkCallHandler.Error -> {
                    _statusChange.emit(enNetworkStatus.Error)
                    throw Exception(result.data?.replace("\"", ""));
                }

                else -> {
                    _statusChange.emit(enNetworkStatus.Error)

                    throw Exception("unexpected Stat");
                }
            }
        }

    }


    fun getMyInfo() {
        viewModelScope.launch(mainDispatcher + errorHandling) {
            val result =
                homeRepository.getMyIfo()
            when (result) {
                is NetworkCallHandler.Successful<*> -> {
                    val resultData = result.data as UserDto;
                    _myInfo.emit(resultData.toUserModel())
                }

                is NetworkCallHandler.Error -> {
                    _statusChange.emit(enNetworkStatus.Error)
                    throw Exception(result.data?.replace("\"", ""));
                }

                else -> {
                    _statusChange.emit(enNetworkStatus.Error)

                    throw Exception("unexpected Stat");
                }
            }
        }

    }


    private suspend fun validationInputSignUp(
        userDto: UserUpdateMoule,
        snackbarHostState: SnackbarHostState
    ): Boolean {
        var message = ""


         if (!userDto.email.isNullOrEmpty() && !Validation.emailValidation(userDto.email))
            message = "لا بد من ادخال ايميل  صالح"
        else if (!userDto.password.isNullOrEmpty()&&!Validation.passwordCapitalValidation(userDto.password))
            message = "لا بد ان تحتوي كلمة المرور على حرفين  capital"
        else if (!userDto.password.isNullOrEmpty()&&!Validation.passwordSmallValidation(userDto.password))
            message = "لا بد ان تحتوي كلمة المرور على حرفين  small"
        else if (!userDto.password.isNullOrEmpty()&&!Validation.passwordNumberValidation(userDto.password))
            message = "لا بد ان تحتوي كلمة المرور على رقمين"
        else if (!userDto.password.isNullOrEmpty()&&!Validation.passwordSpicialCharracterValidation(userDto.password))
            message = "لا بد ان تحتوي كلمة المرور على رمزين"
        if (message.isNotEmpty()) {
            _statusChange.emit(enNetworkStatus.None)
            snackbarHostState.showSnackbar(message)
            return false
        }
        return true;

    }


    fun updateMyInfo(
        updateData: UserUpdateMoule,
        snackbarHostState: SnackbarHostState,
        nav: NavHostController
    ) {
        viewModelScope.launch(mainDispatcher + errorHandling) {

            if (!validationInputSignUp(
                    userDto = updateData,
                    snackbarHostState
                )
            ) {

            } else {
                _statusChange.emit(enNetworkStatus.Loading)
                val result =
                    homeRepository.updateMyInfo(updateData)
                when (result) {
                    is NetworkCallHandler.Successful<*> -> {
                        _statusChange.emit(enNetworkStatus.Complate)
                        snackbarHostState.showSnackbar("تم تعديل المعلومات بنجاح")
                        getMyInfo()
                        nav.popBackStack()
                    }
                    is NetworkCallHandler.Error -> {
                        _statusChange.emit(enNetworkStatus.Error)
                        throw Exception(result.data?.replace("\"", ""));
                    }
                    else -> {
                        _statusChange.emit(enNetworkStatus.Error)

                        throw Exception("unexpected Stat");
                    }
                }
            }

        }

    }

    suspend fun clearErrorMessage() {
        _errorMessage.emit("")
    }

    init {
        getMyInfo()

        getRooms(1)
        getRooms(1, true)
        getBookingData(1)
        getBookingBelongToMyRoom(1)
    }

}