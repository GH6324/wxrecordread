package com.coderpwh.hook

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.util.JsonReader
import android.widget.Toast
import com.alibaba.fastjson.JSON
import com.coderpwh.pojo.RecordHookEt
import com.coderpwh.pojo.UserInfo
import com.coderpwh.utils.factory.showDialog
import com.google.gson.Gson
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.log.loggerD
import com.highcapable.yukihookapi.hook.log.loggerI
import com.highcapable.yukihookapi.hook.type.java.JSONObjectClass
import com.highcapable.yukihookapi.hook.type.java.StringType
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit
import de.robv.android.xposed.XposedHelpers.*

@InjectYukiHookWithXposed(entryClassName = "HookEntryInit")
class HookEntry : IYukiHookXposedInit {

    override fun onInit() = configs {
        isDebug = true
        isAllowPrintingLogs = true
    }

    companion object {
        val wxVersion by lazy {
            getApplicationVersionCode("com.tencent.mm")
        }
        val wxVersionName by lazy {
            getApplicationVersionName("com.tencent.mm")
        }
        val ht by lazy {
            when(wxVersionName) {
                 "8.0.23"->
                    RecordHookEt("pUJ","agVN","agVL","agWs","agXg")
                "8.0.25" ->
                    RecordHookEt("qvi","ajat","ajar","ajaY","iJz")
                else -> null
            }
        }
    }

    override fun onHook() = encase {
        // Your code here.
        loadApp("com.tencent.mm") {
            findClass("com.tencent.mm.plugin.record.ui.RecordMsgDetailUI")
                .hook {
                    injectMember {
                        method {
                            name = "a"
                            returnType = StringType
                            param("com.tencent.mm.protocal.b.a.c".clazz)
                        }
                        beforeHook {
                            loggerD("wxhook", "before hook")
                            loggerD("wxhook","wechat version:${wxVersion}")
                            loggerD("wxhook","wechat version:${wxVersionName}")
                            if (ht==null) {
                                loggerD("wxhook","当前版本尚未支持")
                                instance<Activity>().apply {
                                    Toast.makeText(this.baseContext,"当前版本尚未支持",Toast.LENGTH_LONG).show()
                                }
                                return@beforeHook
                            }
                            var a1 = args(0)
                            var gson = Gson()
                            var readText = gson.toJson(a1.any())
                            loggerD("wxhook", readText)
                            var jsonArray = JSON.parseObject(readText)
                                .getJSONArray(ht!!.st)
                            var userInfos = mutableListOf<UserInfo>()
                            loggerD("wxhook", "userInfos before jsonArray len:${jsonArray.size}")
                            for (i in 0 until jsonArray.size) {
                                var jsonObject = jsonArray.getJSONObject(i)
                                var nickName = jsonObject.getString(ht!!.nicknameF)
                                var wxId = jsonObject.getJSONObject(ht!!.l1)
                                    .getJSONObject(ht!!.ll1)
                                    .getString(ht!!.wxIdF)
                                userInfos.add(UserInfo(nickName, wxId))
                            }
                            var sb = StringBuffer()
                            loggerD("wxhook", "userInfos len:${userInfos.size}")
                            userInfos.forEach { it ->
                                sb.append(it.toString()).append('\n')
                            }
                            var msgData = sb.toString().trim()

                            instance<Activity>().apply {
                                showDialog {
                                    title = "微信wx_id列表"
                                    msg = msgData
                                    confirmButton(text = "复制到剪切板", {
                                        var clipboardManager =
                                            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboardManager.setPrimaryClip(
                                            ClipData.newPlainText("wx_id_list", msgData)
                                        )
                                    })
                                    cancelButton(text = "退出")
                                    noCancelable()
                                }
                            }
                        }
                    }
                }
        }
    }


}

/**
 * 利用 Reflection 获取当前的系统 Context
 */
fun getSystemContext(): Context {
    val activityThreadClass = findClass("android.app.ActivityThread", null)
    val activityThread = callStaticMethod(activityThreadClass, "currentActivityThread")
    val context = callMethod(activityThread, "getSystemContext") as? Context
    return context ?: throw Error("Failed to get system context.")
}

/**
 * 获取指定应用的版本号
 */
fun getApplicationVersionCode(packageName: String): Int {
    val pm = getSystemContext().packageManager
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        pm.getPackageInfo(packageName, 0).longVersionCode.toInt()
    } else {
        pm.getPackageInfo(packageName, 0).versionCode
    }
}

fun getApplicationVersionName(packageName: String): String {
    val pm = getSystemContext().packageManager
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        pm.getPackageInfo(packageName, 0).versionName
    } else {
        pm.getPackageInfo(packageName, 0).versionName
    }
}