package com.coderpwh.hook

import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Environment
import android.text.SpannableString
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.alibaba.fastjson.JSON
import com.coderpwh.pojo.ChangeMsgEt
import com.coderpwh.pojo.LuckHookEt
import com.coderpwh.pojo.RecordHookEt
import com.coderpwh.pojo.UserInfo
import com.coderpwh.utils.factory.showDialog
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.log.loggerD
import com.highcapable.yukihookapi.hook.log.loggerI
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.type.java.StringType
import com.highcapable.yukihookapi.hook.type.java.UnitType
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.XposedHelpers.callMethod
import de.robv.android.xposed.XposedHelpers.callStaticMethod
import de.robv.android.xposed.XposedHelpers.findClass
import java.io.File
import java.io.InputStream

@InjectYukiHookWithXposed(entryClassName = "HookEntryInit", modulePackageName = "com.coderpwh")
class HookEntry : IYukiHookXposedInit {


    override fun onInit() = configs {
        isDebug = true
        isAllowPrintingLogs = true
    }

    lateinit var orText:EditText
    lateinit var prText:EditText
    companion object {
        val wxVersion by lazy {
            getApplicationVersionCode("com.tencent.mm")
        }
        val wxVersionName by lazy {
            getApplicationVersionName("com.tencent.mm")
        }
        val ht by lazy {
            when(wxVersionName) {
                "8.0.7" ->
                    RecordHookEt("lpz","SyI","SyG","Szn","SAb","a")
                "8.0.23"->
                    RecordHookEt("pUJ","agVN","agVL","agWs","agXg","a")
                "8.0.25" ->
                    RecordHookEt("qvi","ajat","ajar","ajaY","iJz","a")
                "8.0.40" ->
                    RecordHookEt("f", "g1", "f1", "d", "o","S7")
                else -> null
            }
        }

        val lt by lazy {
            when(wxVersionName) {
                "8.0.40"->
                    LuckHookEt("W7","z","u","n","Q","M","j","d","n","f")
                "8.0.7"->
                    LuckHookEt("a","Ezl","Ezg","ybP","Ezw","Ezu","DLp","EAG","userName","EAr")
                else -> LuckHookEt("W7","z","u","n","Q","M","j","d","n","f")
            }
        }

        val cmt by lazy {
            when(wxVersionName) {
                "8.0.40" ->
                    ChangeMsgEt(0x7f094fb5,"d")
                "8.0.42" ->
                    ChangeMsgEt(0x7f0952c4,"b")
                else -> ChangeMsgEt(0x7f094fb5,"d")
            }
        }

        const val WXCONFIGPATH = "/storage/emulated/0/Android/data/com.tencent.mm/files"

        val pr by lazy {
            var f = File(WXCONFIGPATH+"/wx/wxmsg.properties")
            var p = mutableMapOf<String,String>()
            if (!f.exists()) {
                var dir = File(WXCONFIGPATH+"/wx")
                if (!dir.exists()) {
                    dir.mkdir()
                }
                f.createNewFile()
                return@lazy p
            } else {
                p.load(f.inputStream())
                return@lazy p
            }
        }
    }


    fun changeMsgDialog(ct:Context) {
        var view = ct.genMsgCgLg()
//        var rl = RelativeLayout(ct)
        var dialog = AlertDialog.Builder(ct)
            .setTitle("消息修改")
            .setView(view)
            .setCancelable(false)
            .setPositiveButton("修改保存",null)
            .setNeutralButton("修改",null)
            .setNegativeButton("取消",null)
            .create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    pbtn ->
                pr.add(orText.text.toString(),prText.text.toString())
                orText.setText("")
                prText.setText("")
                pr.save()
            }
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
                    negbtn ->
                dialog.dismiss()
            }
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
                    neubtn ->
                pr.add(orText.text.toString(),prText.text.toString())
                orText.setText("")
                prText.setText("")
            }

        }
        dialog.show()
    }

    fun Context.genMsgCgLg(): View {
        var activity_login = LinearLayout(this);
        activity_login.orientation = LinearLayout.VERTICAL
//        activity_login.setId(980881);
//        activity_login.setBackgroundResource(111);
        //显示有问题
        var layout_239 = ViewGroup.LayoutParams(-1,-1);
        activity_login.layoutParams = layout_239

        var inputText = LinearLayout(this);
//        inputText.setId(119903);
        inputText.setOrientation(LinearLayout.VERTICAL);
        var layout_810 = ViewGroup.LayoutParams(-1,-2);
        inputText.setLayoutParams(layout_810);
//WRAP_CONTENT = -2; wrap_content
        //MATCH_PARENT = -1 match_parent
        orText = EditText(this);
//        phone_text.setId(R.id.phone_text);
        orText.setHint("请输入原消息");
//        phone_text.setFilters(11);
        orText.setTextSize((35/getApplicationContext().getResources().getDisplayMetrics().scaledDensity));
        var layout_792 = ViewGroup.LayoutParams(-1,-2);
        orText.setLayoutParams(layout_792);
        inputText.addView(orText);

        prText = EditText(this);
//        password_text.setId(R.id.password_text);
        prText.setEms(10);
//        password_text.setFilters(32);
        prText.setHint("请输入更换后的消息")
        prText.setTextSize((35/getApplicationContext().getResources().getDisplayMetrics().scaledDensity));
        var layout_214 = ViewGroup.LayoutParams(-1,-2);
        prText.setLayoutParams(layout_214);
        inputText.addView(prText);
        var fuBtns = LinearLayout(this)
        fuBtns.orientation = LinearLayout.HORIZONTAL
        var layout_fu = ViewGroup.LayoutParams(-1,-2);
        fuBtns.layoutParams = layout_fu
        //增加两个按钮
        var layout_616 = ViewGroup.LayoutParams(-2,-2);
        var b1 = Button(this)
        b1.layoutParams = layout_616
        b1.text = "恢复初始"
        b1.setOnClickListener {
            pr.clear()
        }

        var b2 = Button(this)
        b2.layoutParams = layout_616
        b2.text = "加载配置"
        b2.setOnClickListener {
            pr.load()
        }

        var b3 = Button(this)
        b3.layoutParams = layout_616
        b3.text = "清除配置"
        b3.setOnClickListener {
            pr.clear()
            pr.save()
        }



        fuBtns.addView(b1)
        fuBtns.addView(b2)
        fuBtns.addView(b3)
        activity_login.addView(inputText);
        activity_login.addView(fuBtns)
//        var layout_743 = ViewGroup.LayoutParams(-1,-2);


        activity_login.buildLayer()
        return activity_login
    }
//ko4
    fun PackageParam.addRbtnOcli() {
        TextView::class.java
            .hook {
                injectMember {
                    method {
                        name = "setText"
//                        superClass(true)
                    }
                    afterHook {
                        instance<View>().apply {
//                            loggerD("wxbtnHook","btn id ${id}")
                            if (this.id== cmt.viewId) {
                                loggerD("wxbtnHook","success find btn")
                                setOnClickListener {
                                    Toast.makeText(this.context,"修改消息",Toast.LENGTH_LONG).show()
                                    loggerD("wxbtnHook","success hook btn")
                                    changeMsgDialog(this.context)
                                    return@setOnClickListener
                                }
                            }
                        }
                    }
                }
            }
    }



    fun PackageParam.changeMsg() {
        findClass("com.tencent.neattextview.textview.view.NeatTextView")
            .hook {
                injectMember {
                    method {
                        name = cmt.methodName
                        returnType = UnitType
                    }
                    beforeHook {
                        loggerD("wxmsgHook","before hook wxmsg")
                        var msg = args(0).any()
                        var msgStr = msg.toString().replace("\n","\\n")
                        loggerD("wxmsgHook","msg is ${msgStr} type:${msg!!.javaClass}")
                        if (pr.containsKey(msgStr)) {
                            args(0).set(SpannableString((pr[msgStr] as String).replace("\\n","\n")))
                        }
                        /*if (msg is SpannableString) {
                            if (msg.toString().contains("大家都")) {
                                loggerD("wxmsgHook","hook 大家都msg")
                                args(0).set(SpannableString("大家都999"))
                            }
                        }*/

                    }
                    afterHook {
                        loggerD("wxmsgHook","after hook wxmsg:${args(0).any().toString()}")
                    }
                }
            }
    }

    private fun parseJsonArrayToLList(ja: JsonArray):List<LuckyItem> {
        return ja.map {
                je ->
            je.asJsonObject.let {
                LuckyItem(it.get(lt.nickName).asString,it.get(lt.wxid).asString,it.get(lt.money).asLong)
            }
        }.toList()
    }

    fun PackageParam.lmMsg() {
        findClass("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI").hook {
            injectMember {
                method {
                    name = lt.methodName
                    paramCount = 1
                    if(lt.methodName=="a") {
                        param("com.tencent.mm.plugin.luckymoney.model.q".clazz)
                    }
                }
                //com.tencent.mm.plugin.luckymoney.model.q
                beforeHook {
                    val data = args(0).any()
                    val gson = Gson()
                    val toJson = gson.toJson(data)
                    loggerI(msg = "toJson:$toJson")
                    args(0).any()!!.current {
                        val mMsg = field {
                            name = lt.moneyDesc
                        }.string()
                        loggerI(msg = "z:$mMsg")
                        val totalM = field {
                            name = lt.totalMoney
                        }.long()
                        loggerI(msg = "totalM:$totalM")
                        val resultMsg = if (mMsg.contains("共"))  {
                            mMsg
                        } else {
                            "共${(totalM/100.0)}元,${mMsg}"
                        }
                        field {
                            name = lt.moneyDesc
                        }.set(resultMsg)
                    }

                    var jsonObj = gson.toJsonTree(data).asJsonObject
                    jsonObj.apply {
                        var v = get(lt.versionName).asString
                        LuckyMoneyHold.sender = get(lt.sender).asString
                        if(LuckyMoneyHold.v.isEmpty() || !LuckyMoneyHold.v.equals(v)) {
                            LuckyMoneyHold.v = v
                            val asJsonArray = get(lt.moneyArr).asJsonArray
                            LuckyMoneyHold.luckyList = parseJsonArrayToLList(asJsonArray).toMutableList()
                        } else {
                            val asJsonArray = get(lt.moneyArr).asJsonArray
                            parseJsonArrayToLList(asJsonArray).forEach {
                                if (!LuckyMoneyHold.luckyList.contains(it)) {
                                    LuckyMoneyHold.luckyList.add(it)
                                }
                            }
                        }
                    }
                    instance.current {
                        //Eut
                        val ge3View = field {
                            name = lt.tView
                        }.cast<TextView>()
                        ge3View!!.setOnClickListener {
                            AlertDialog.Builder(it.context).setTitle("领取详情")
                                .setItems(LuckyMoneyHold.luckyList.map { "${it.nickName}领取${it.money/100.0}元,wxid:${it.wxid}" }.toTypedArray()) {
                                        d,index ->
                                    val lItem = LuckyMoneyHold.luckyList.get(index)
                                    val clipboardManager =
                                        appContext!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboardManager.setPrimaryClip(
                                        ClipData.newPlainText(
                                            "wx_luck_info",
                                            "nickName:${lItem.nickName}\nwxid:${lItem.wxid}"
                                        )
                                    )
                                    Toast.makeText(it.context,"成功复制",Toast.LENGTH_LONG).show()
                                }
                                .setPositiveButton("复制") {
                                        _,_ ->
                                    var joinToString =
                                        LuckyMoneyHold.luckyList.map { "${it.nickName}领取${it.money / 100.0}元,wxid:${it.wxid}" }
                                            .joinToString("\n")
                                    joinToString = "发送者:${LuckyMoneyHold.sender}\n" + joinToString
                                    val clipboardManager =
                                        appContext!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboardManager.setPrimaryClip(
                                        ClipData.newPlainText(
                                            "wx_luck_all",
                                            joinToString
                                        )
                                    )
                                }
                                .setNegativeButton("取消") {
                                        it,_ ->
                                    it.cancel()
                                }
                                .show()
                        }
                    }
                }
            }
        }
    }


    override fun onHook() = encase {
        // Your code here.
        loadApp("com.tencent.mm") {
            changeMsg()
            addRbtnOcli()
            lmMsg()
            findClass("com.tencent.mm.plugin.record.ui.RecordMsgDetailUI")
                .hook {
                    injectMember {
                        method {
                            name = if (ht != null) {
                                ht!!.methodName
                            } else {
                                "a"
                            }
                            returnType = StringType
                            paramCount = 1
                        }
                        beforeHook {
                            loggerD("wxhook", "before hook")
                            loggerD("wxhook","wechat version:${wxVersion}")
                            loggerD("wxhook","wechat version:${wxVersionName}")

                            var a1 = args(0)
                            var gson = Gson()
                            var readText = gson.toJson(a1.any())
                            loggerD("wxhook", readText)
                            if (ht==null) {
                                loggerD("wxhook","当前版本尚未支持")
                                var clipboardManager =
                                    appContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboardManager.setPrimaryClip(
                                    ClipData.newPlainText("wx_id_list", readText)
                                )
                                instance<Activity>().apply {
                                    Toast.makeText(this.baseContext,"当前版本尚未支持",Toast.LENGTH_LONG).show()
                                }
                                return@beforeHook
                            }
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

object LuckyMoneyHold {
    var sender: String = ""
    var v:String = ""
    var luckyList:MutableList<LuckyItem> = mutableListOf()
}

data class LuckyItem(val nickName:String,val wxid:String,val money:Long)

fun MutableMap<String,String>.load(ins:InputStream) {
    ins.bufferedReader().lines().forEach {
        var split = it.split("=")
        if (split.size==2) {
            put(split[0]?:"",split[1]?:"")
        }
    }
    remove("")
}

fun MutableMap<String,String>.load() {
    load(
        File(HookEntry.WXCONFIGPATH+"/wx/wxmsg.properties")
            .inputStream()
    )
}


fun MutableMap<String,String>.save() {
    var f = File(HookEntry.WXCONFIGPATH+"/wx/wxmsg.properties")
    var sb = StringBuffer()
    forEach{
        sb.append(it.key)
            .append('=')
            .append(it.value)
            .append('\n')
    }
    f.writeText(sb.toString())
}

fun MutableMap<String,String>.add(key:String,value:String) {
    put(key.replace("\n","\\n"),value.replace("\n","\\n"))
}