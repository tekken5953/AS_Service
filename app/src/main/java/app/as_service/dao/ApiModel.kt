package app.as_service.dao

import com.google.gson.annotations.SerializedName

class ApiModel {
    // 회원가입 시 Body에 넣어서 POST 할 데이터 모델
    data class Member(val userId: String, val phone: String, val password: String)
    // 로그인 시 Body에 넣어서 POST 할 데이터 모델
    data class Login(val userId: String, val password: String)
    // 로그인 시 발행된 AccessToken Body로 Get할 데이터 모델
    data class AccessToken(var access: String)
    // 장치추가 시 Body에 넣어서 POST 할 데이터 모델
    data class PostDevice(val device: String, val id: String, val deviceName: String, val business: String)
    // 장치생성 리턴 텍스트
    data class ReturnPost(val result: String)
    // 유저정보 GET할 데이터 모델
    data class GetMyInfo(val userId: String, val email: String, val name: String, val authority: String)
    // 유저 비밀번호 변경 모델
    data class PutMyPassword(val password: String)
    // 유저 이메일 변경 모델
    data class PutMyEmail(val email: String)
    // 장치데이터 리스트 GET할 데이터 모델
    data class GetData(val TEMPval: String, val HUMIDval: String, val PM2P5val: String,
                       val CO2val: String, val COval: String, val TVOCval: String,
                       val CAIval: String, val Virusval: String, val date: Long)
}