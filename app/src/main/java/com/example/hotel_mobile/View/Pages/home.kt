package com.example.hotel_mobile.View.Pages

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.hotel_mobile.R
import com.example.hotel_mobile.View.component.CustomErrorSnackBar
import com.example.hotel_mobile.View.component.RoomLoaingHolder
import com.example.hotel_mobile.View.component.RoomShape
import com.example.hotel_mobile.ViewModle.HomeViewModle

@Composable
fun HomePage(
    nav: NavHostController,
    homeViewModel: HomeViewModle
) {
    val roomData = homeViewModel.rooms.collectAsState()



CustomErrorSnackBar(
    homeViewModel = homeViewModel,
    authViewModel = null,
    nav = nav,
    page = {
        Scaffold(

            modifier = Modifier
                .padding(top = 35.dp)
                .fillMaxWidth()
                .fillMaxHeight()
            ,
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


                when (roomData.value == null) {
                    true -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            (0..4).forEach {
                                RoomLoaingHolder()
                            }
                        }
                    }


                    else -> {
                        when (roomData.value!=null&&roomData.value!!.size == 0) {
                            true -> {

                                Column(
                                    modifier = Modifier

                                        .fillMaxWidth()
                                        .fillMaxHeight(),

                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .padding(bottom = 70.dp)
//                            .background(Color.Red)
                                    ) {

                                        Image(

                                            painter = painterResource(R.drawable.no_found_data),
                                            contentDescription = "", contentScale = ContentScale.Fit
                                        )
                                    }
                                }
                            }

                            else -> {
                                LazyColumn(
                                    modifier = Modifier
                                        .padding(top = 5.dp)
                                        .padding(horizontal = 15.dp)
                                        .fillMaxSize(),
                                ) {
                                    items(roomData.value!!.size) { index ->
                                        RoomShape(
                                            roomData.value!![index],
                                            nav
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