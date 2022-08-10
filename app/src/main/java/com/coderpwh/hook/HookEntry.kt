package com.coderpwh.hook

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.JsonReader
import com.alibaba.fastjson.JSON
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
                            var a1 = args(0)
                            var gson = Gson()
                            var readText = gson.toJson(a1.any())
                            loggerD("wxhook", readText)
                            var jsonArray = JSON.parseObject(readText)
                                .getJSONArray("pUJ")
                            var userInfos = mutableListOf<UserInfo>()
                            loggerD("wxhook", "userInfos before jsonArray len:${jsonArray.size}")
                            for (i in 0 until jsonArray.size) {
                                var jsonObject = jsonArray.getJSONObject(i)
                                var nickName = jsonObject.getString("agVN")
                                var wxId = jsonObject.getJSONObject("agVL")
                                    .getJSONObject("agWs")
                                    .getString("agXg")
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

    /**
     * 利用 Reflection 获取当前的系统 Context
     */
    fun getSystemContext(): Context {
        val activityThreadClass = findClass("android.app.ActivityThread", null)
        val activityThread = callStaticMethod(activityThreadClass, "currentActivityThread")
        val context = callMethod(activityThread, "getSystemContext") as? Context
        return context ?: throw Error("Failed to get system context.")
    }

}