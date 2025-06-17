package com.example.hotel_mobile.View.Pages

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.hotel_mobile.View.component.BookingLoaingHolder
import com.example.hotel_mobile.View.component.BookingShape
import com.example.hotel_mobile.View.component.CustomErrorSnackBar
import com.example.hotel_mobile.ViewModle.HomeViewModle


@Composable
fun BookingForMyRoom(
    homeViewModel: HomeViewModle, nav: NavHostController
) {

    val bookings = homeViewModel.bookingsBelongToMyRoomList.collectAsState()


    CustomErrorSnackBar(
        homeViewModel = homeViewModel, authViewModel = null,
        nav = nav,
        page = {
            Scaffold(

                modifier = Modifier
                    .padding(top = 35.dp)
                    .fillMaxWidth()
                    .fillMaxHeight(),


                ) {
                it.calculateTopPadding()
                it.calculateBottomPadding()

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .background(
                            Color.Gray.copy(0.17f)
                        ),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {


                    when (bookings.value == null) {
                        true -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {

                                (0..4).forEach {
                                    BookingLoaingHolder()
                                }
                            }
                        }


                        else -> {

                            when (bookings.value.isNullOrEmpty()) {
                                true -> {
                                    Column(
                                        modifier = Modifier

                                            .fillMaxWidth()
                                            .fillMaxHeight(),

                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DateRange,
                                            contentDescription = "",
                                            modifier = Modifier.size(150.dp),
                                            tint = Color.Black.copy(0.56f)
                                        )
                                        Text(
                                            "لا يوجد اي حجوزات لغرفي",
                                            color = Color.Black.copy(0.56f),
                                            fontWeight = FontWeight.Bold
                                        )

                                    }
                                }

                                else -> {
                                    LazyColumn(
                                        modifier = Modifier
                                            .padding(top = 5.dp)
                                            .padding(horizontal = 15.dp)
                                            .fillMaxSize(),
                                    ) {
                                        items(bookings.value!!.size) { index ->
                                            BookingShape(
                                                bookingData = bookings.value!![index]
                                            )
                                        }
                                    }

                                }
                            }
                        }

                    }
                }
            }
        }
    )
}