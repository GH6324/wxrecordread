package com.coderpwh.pojo

data class UserInfo(
    val nickName: String?,
    val wxId: String?
) {
    override fun toString(): String {
        return "微信昵称: [$nickName] wxid: $wxId"
    }
}