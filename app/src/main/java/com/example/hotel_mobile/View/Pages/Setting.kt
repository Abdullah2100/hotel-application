package com.example.hotel_mobile.View.Pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.hotel_mobile.Modle.Screens
import com.example.hotel_mobile.View.component.CustomErrorSnackBar
import com.example.hotel_mobile.ViewModle.HomeViewModle
import okhttp3.internal.wait


@Composable
fun SettingPage(
    homeViewModel: HomeViewModle,
    nav: NavHostController
) {

    CustomErrorSnackBar(
        homeViewModel = homeViewModel,
        authViewModel = null
    ) {

        var myInfo = homeViewModel.myInfo.collectAsState()
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
                    )
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .fillMaxWidth()
                        .height(120.dp)
                        .clickable {
                     nav.navigate(Screens.editeProfile)
                        }
                        .background(Color.White,
                            shape = RoundedCornerShape(8.dp)
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxHeight(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        when(myInfo.value?.imagePath==null){
                            true->{
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = ""
                                , modifier = Modifier.size(98.dp)
                                )
                            }
                            else->{

                            }
                        }

                        Column {
                            Text(myInfo.value?.personData?.name ?:"")
                            Text("@"+(myInfo.value?.userName?:""))
                        }
                    }

                    Icon(Icons.Default.Edit,
                        contentDescription = "",
                        modifier=Modifier
                            .padding(end = 20.dp)
                            .size(40.dp),
                        tint = MaterialTheme.colorScheme.primary)

                }


            }
        }
    }

}