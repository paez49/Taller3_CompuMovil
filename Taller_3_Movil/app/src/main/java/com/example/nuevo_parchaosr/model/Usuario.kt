package com.example.nuevo_parchaosr.model

import lombok.Getter
import lombok.Setter
import lombok.ToString

@Getter
@Setter
@ToString
class Usuario {
    var nombreUsuario: String = ""
    var email: String = ""
    var biografia: String = "Sin biografia aún"
    var videoURI:String = "videos/defaultVideoProfile.mp4"
    var listParches= HashMap<String, String>()
    fun Usuario(){}
}