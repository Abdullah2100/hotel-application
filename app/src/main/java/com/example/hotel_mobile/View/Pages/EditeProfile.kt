package com.example.hotel_mobile.View.Pages

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.hotel_mobile.Dto.LoginDto
import com.example.hotel_mobile.Modle.Request.RoomImageCreation
import com.example.hotel_mobile.Modle.Request.UserUpdateMoule
import com.example.hotel_mobile.Modle.enNetworkStatus
import com.example.hotel_mobile.R
import com.example.hotel_mobile.Util.General.toCustomFil
import com.example.hotel_mobile.View.component.CustomErrorSnackBar
import com.example.hotel_mobile.ViewModle.HomeViewModle
import java.io.File


@Composable
fun EditeProfilePage(
    homeViewModle: HomeViewModle,
    nav: NavHostController,
) {
    val keyboardController = LocalSoftwareKeyboardController.current;
    val context = LocalContext.current;
    val userData = homeViewModle.myInfo.collectAsState()

    val profileImage = remember { mutableStateOf<File?>(null) }

    val name = remember { mutableStateOf(userData.value?.personData?.name ?: "") }
    val email = remember { mutableStateOf(userData.value?.personData?.email ?: "") }
    val phone = remember { mutableStateOf(userData.value?.personData?.phone ?: "") }
    val address = remember { mutableStateOf(userData.value?.personData?.address ?: "") }
    val userName = remember { mutableStateOf(userData.value?.userName ?: "") }
    val passwore = remember { mutableStateOf("") }
    val newPassword = remember { mutableStateOf("") }
    val isEditePassword = remember { mutableStateOf(false) }

    val loadinState = homeViewModle.statusChange.collectAsState()

    val onImageSelection = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val fileHolder = uri.toCustomFil(context = context);
            if (fileHolder != null)
                profileImage.value = fileHolder;

        }
    }

    val snackbarHostState = remember { SnackbarHostState() }


    CustomErrorSnackBar(
        homeViewModel = homeViewModle,
        authViewModel = null
    ) {

        Scaffold(
            topBar = {
                Box(
                    modifier = Modifier
                        .padding(top = 20.dp, start = 10.dp)
                        .height(30.dp)
                        .width(30.dp)
                        .background(Color.Blue, shape = RoundedCornerShape(15.dp))
                        .clickable {
                            nav.popBackStack()
                        }) {
                    Image(
                        Icons.Outlined.KeyboardArrowLeft,
                        "",
                        colorFilter = ColorFilter.tint(color = Color.White),
                        modifier = Modifier.size(40.dp)
                    )
                }
            },

            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier=Modifier.padding(bottom = 90.dp))
            },
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

                ConstraintLayout() {
                    var (image, buttonIcon) = createRefs()

                    Box(
                        modifier = Modifier
                            .padding(top = 20.dp)
                            .height(170.dp)
                            .width(170.dp)
                            .background(
                                Color.White,
                                shape = RoundedCornerShape(90.dp)
                            )
                            .constrainAs(image) {
                                top.linkTo(parent.top)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        when (profileImage.value == null) {
                            true -> {
                                when (userData.value?.imagePath != null) {
                                    true -> {
                                        SubcomposeAsyncImage(
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
//                                                .padding(top = 35.dp)
                                                .height(90.dp)
                                                .width(90.dp)
                                                .clip(RoundedCornerShape(8.dp)),
                                            model =
                                            ImageRequest.Builder(context)
                                                .data(userData.value!!.imagePath)
                                                .build(),
                                            contentDescription = "",
                                            loading = {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize(),
                                                    contentAlignment = Alignment.Center // Ensures the loader is centered and doesn't expand
                                                ) {
                                                    CircularProgressIndicator(
                                                        color = Color.Black,
                                                        modifier = Modifier.size(54.dp) // Adjust the size here
                                                    )
                                                }
                                            },
                                        )
                                    }

                                    else -> {
                                        Icon(
                                            Icons.Default.Person,
                                            contentDescription = "",
                                            modifier = Modifier.size(140.dp)
                                        )
                                    }

                                }
                            }

                            else -> {
                                SubcomposeAsyncImage(
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
//                                        .padding(top = 35.dp)
                                        .height(120.dp)
                                        .width(120.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    model =
                                    ImageRequest.Builder(context)
                                        .data(profileImage.value!!.path)
                                        .build(),
                                    contentDescription = "",
                                    loading = {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize(),
                                            contentAlignment = Alignment.Center // Ensures the loader is centered and doesn't expand
                                        ) {
                                            CircularProgressIndicator(
                                                color = Color.Black,
                                                modifier = Modifier.size(54.dp) // Adjust the size here
                                            )
                                        }
                                    },
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .offset(x = -56.dp, y = -45.dp)
                            .constrainAs(buttonIcon) {
                                top.linkTo(image.bottom)
                                start.linkTo(image.end)
                            }
                            .height(40.dp)
                            .width(40.dp)
                            .clip(
                                shape = RoundedCornerShape(26.dp)

                            )
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                            )
                            .clickable {
                                onImageSelection.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            ImageVector.vectorResource(R.drawable.camera),
                            contentDescription = "",
                            modifier = Modifier.size(20.dp),
                            tint = Color.White
                        )
                    }
                }

                OutlinedTextField(
                    maxLines = 1,
                    value = name.value,
                    onValueChange = { name.value = it },
                    label = {
                        Text(
                            "الاسم",
                            color = Color.Gray.copy(alpha = 0.66f)
                        )
                    },
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(9.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White.copy(alpha = 0.46f),
                        focusedContainerColor = Color.White,
                        unfocusedLabelColor = Color.Black,
                        focusedLabelColor = Color.Black,
                    )
                )

                OutlinedTextField(
                    enabled = false,
                    maxLines = 1,
                    value = email.value,
                    onValueChange = { email.value = it },
                    label = {
                        Text(
                            "الايميل",
                            color = Color.Gray.copy(alpha = 0.66f)
                        )
                    },
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(9.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White.copy(alpha = 0.46f),
                        focusedContainerColor = Color.White,
                        unfocusedLabelColor = Color.Black,
                        focusedLabelColor = Color.Black,
                    )
                )

                OutlinedTextField(
                    maxLines = 1,
                    value = phone.value,
                    onValueChange = { phone.value = it },
                    label = {
                        Text(
                            "phone",
                            color = Color.Gray.copy(alpha = 0.66f)
                        )
                    },
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(9.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White.copy(alpha = 0.46f),
                        focusedContainerColor = Color.White,
                        unfocusedLabelColor = Color.Black,
                        focusedLabelColor = Color.Black,
                    )
                )

                OutlinedTextField(
                    maxLines = 1,
                    value = address.value,
                    onValueChange = { address.value = it },
                    label = {
                        Text(
                            "العنوان",
                            color = Color.Gray.copy(alpha = 0.66f)
                        )
                    },
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(9.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White.copy(alpha = 0.46f),
                        focusedContainerColor = Color.White,
                        unfocusedLabelColor = Color.Black,
                        focusedLabelColor = Color.Black,
                    )
                )

                OutlinedTextField(
                    maxLines = 1,
                    value = userName.value,
                    onValueChange = { userName.value = it },
                    label = {
                        Text(
                            "اسم المستخدم",
                            color = Color.Gray.copy(alpha = 0.66f)
                        )
                    },
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(9.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White.copy(alpha = 0.46f),
                        focusedContainerColor = Color.White,
                        unfocusedLabelColor = Color.Black,
                        focusedLabelColor = Color.Black,
                    )
                )

                OutlinedTextField(
                    enabled = isEditePassword.value,
                    maxLines = 1,
                    value = passwore.value,
                    onValueChange = { passwore.value = it },
                    label = {
                        Text(
                            "كلمة المرور",
                            color = Color.Gray.copy(alpha = 0.66f)
                        )
                    },
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(9.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White.copy(alpha = 0.46f),
                        focusedContainerColor = Color.White,
                        unfocusedLabelColor = Color.Black,
                        focusedLabelColor = Color.Black,
                    ),
                    trailingIcon = {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "",
                            modifier = Modifier
                                .size(25.dp)
                                .clickable {
                                    isEditePassword.value = true
                                }
                        )
                    }
                )

                if (isEditePassword.value)
                    OutlinedTextField(
                        maxLines = 1,
                        value = newPassword.value,
                        onValueChange = { newPassword.value = it },
                        label = {
                            Text(
                                "كلمة المرور الجديدة",
                                color = Color.Gray.copy(alpha = 0.66f)
                            )
                        },
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(9.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White.copy(alpha = 0.46f),
                            focusedContainerColor = Color.White,
                            unfocusedLabelColor = Color.Black,
                            focusedLabelColor = Color.Black,
                        )

                    )
                Button(
                    enabled = loadinState.value != enNetworkStatus.Loading,

                    onClick = {
                        keyboardController?.hide();
                        homeViewModle.updateMyInfo(
                            UserUpdateMoule(
                                id = userData.value!!.userId!!,
                                name = name.value,
                                email=email.value,
                                phone=phone.value,
                                address = address.value,
                                password = newPassword.value,
                                currenPassword = passwore.value,
                                userName = userName.value,
                                imagePath = profileImage.value
                            ),
                            snackbarHostState,
                            nav
                        )
                    },
                    modifier = Modifier
                        .padding(top = 15.dp)
//                        .padding(horizontal = 50.dp)
                        .fillMaxWidth()
                        .height(35.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    when (loadinState.value) {
                        enNetworkStatus.Loading -> {
                            CircularProgressIndicator(
                                color = Color.Blue, modifier = Modifier
                                    .offset(y = -3.dp)
                                    .height(25.dp)
                                    .width(25.dp)
                            )
                        }

                        else -> {
                            Text(
                                "تعديل",
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        }
                    }


                }

            }
        }
    }

}