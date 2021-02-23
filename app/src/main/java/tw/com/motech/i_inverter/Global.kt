package tw.com.motech.i_inverter

val BaseUrl : String = "https://i-inverter.motech.com.tw/vUserApisvr/api/values"
var LoginSP : String = "USER_LOGIN"
var UserName : String = "XX"
var sSiteNo_GLB: String = "MOT001"
var sSite_Name_GLB: String = "茂迪五廠"


data class SiteResult(
    val sSiteType: String,
    val nSort: Int,
    val sSiteNo: String,
    val sSite_Name: String,
    val nSHI: Int,
    val nDMY: Double,
    val nPR: Double
)