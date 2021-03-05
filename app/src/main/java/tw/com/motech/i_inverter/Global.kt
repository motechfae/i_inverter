package tw.com.motech.i_inverter

val BaseUrl : String = "https://i-inverter.motech.com.tw/vUserApisvr/api/values"
var LoginSP : String = "USER_LOGIN"
var UserName : String = "XX"
var sSiteNo_GLB: String = "MOT001"
var sSite_Name_GLB: String = "茂迪五廠"
var sZoneNo_GLB: String = "F5b1zone"


data class SiteResult(
    val sSiteType: String,
    val nSort: Int,
    val sSiteNo: String,
    val sSite_Name: String,
    val nSHI: Int,
    val nDMY: Double,
    val nPR: Double
)

data class ZoneResult(
    val sZoneNo: String,
    val sMemo: String
)

data class SiteData(
    val sDataKey: String,
    val nEa: Double,
    val nHi: Float,
    val nTmp: Float
)

data class LastSiteData(
    val sDataKey: String,
    val sSiteNo: String,
    val nEa: Double,
    val nHi: Float,
    val nTmp: Float,
    val nTEa: Double,
    val nEaMax: Double
)