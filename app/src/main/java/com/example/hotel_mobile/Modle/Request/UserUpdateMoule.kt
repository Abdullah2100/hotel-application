package com.example.hotel_mobile.Modle.Request

import java.io.File
import java.util.UUID

data class UserUpdateMoule(
  val  id :UUID,
 val name :String?,
 val email:String?,
val phone:String?,
val  address:String?,
val  userName:String?,
val password:String?,
val currenPassword:String?,
val imagePath: File?
)
