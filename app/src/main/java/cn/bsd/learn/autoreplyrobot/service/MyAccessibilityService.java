package cn.bsd.learn.autoreplyrobot.service;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.bsd.learn.autoreplyrobot.utils.HttpUtils;

public class MyAccessibilityService extends AccessibilityService {

    private static final List<String> messageList = new ArrayList<>();
    private static final Random sRandom = new Random(messageList.size());

    private long sendTime;

    static {
        messageList.add("你好呀？");
        messageList.add("吃了吗？");
        messageList.add("几点睡？");
        messageList.add("哪的人啊？");
        messageList.add("我也是");
        messageList.add("好巧啊");
        messageList.add("你住哪啊？");
        messageList.add("我也在附近");
        messageList.add("出来吃饭啊");
        messageList.add("看个电影也行啊");
        messageList.add("出去兜风啊");
        messageList.add("一个人没意思");
        messageList.add("恐怖片不敢看");
        messageList.add("你玩游戏吗？");
        messageList.add("我玩王者");
        messageList.add("你玩什么");
        messageList.add("吃鸡吗？");
        messageList.add("带飞不了");
        messageList.add("自己瞎玩吧");
    }

    /**
     * 1、获取到聊天内容
     * 2、跳转到微信的聊天页面
     * 3、找到输入框输入需要回复的内容
     * 4、找到发送按钮，点击
     *
     * @param event
     */
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        //获取事件类型
        int eventType = event.getEventType();
        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
//            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                //窗口状态变更
                //3、找到输入框输入需要回复的内容
                //获取页面节点信息
                fill();
                break;
        }
    }

    private void fill() {
        int time = sRandom.nextInt(10 * 1000);
        if (System.currentTimeMillis() - sendTime < time) {
            return;
        }
        sendTime = System.currentTimeMillis();
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode != null) {
            //判断收到的消息（去选择回复什么消息）
            String message = messageList.get(sRandom.nextInt(messageList.size()));
            if (findEditText(rootNode, message)) {
                //找到发送按钮，点击
                send();
            }
        }
    }

    private void send() {
        //重新获取页面的节点信息
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        //
        if (rootNode != null) {
            List<AccessibilityNodeInfo> list = rootNode.findAccessibilityNodeInfosByText("发送");
            if (list != null && list.size() > 0) {
                for (AccessibilityNodeInfo nodeInfo : list) {
                    if (nodeInfo.isEnabled()) {
                        nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        return;
                    }
                }
            } else {
                List<AccessibilityNodeInfo> lists = rootNode.findAccessibilityNodeInfosByText("Send");
                if (lists != null && lists.size() > 0) {
                    for (AccessibilityNodeInfo nodeInfo : lists) {
                        if (nodeInfo.getClassName().equals("android.widget.Button") && nodeInfo.isEnabled()) {
                            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }
                }
            }
        }
        //返回到主界面
//        backToHome();

    }

    private void backToHome() {
//        performGlobalAction()
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
    }

    private boolean findEditText(AccessibilityNodeInfo rootNode, String content) {
        //找到输入框输入需要回复的内容
        int childCount = rootNode.getChildCount();
        //遍历查找
        for (int i = 0; i < childCount; i++) {
            AccessibilityNodeInfo nodeChild = rootNode.getChild(i);
            if (nodeChild == null) {
                continue;
            }
            if (nodeChild.getClassName().equals("android.widget.EditText")) {
                //已经找到了输入框
                //输入需要回复的内容
                nodeChild.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                //将文本输入到剪贴板
                ClipData clipData = ClipData.newPlainText("label", content);
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                clipboardManager.setPrimaryClip(clipData);
                nodeChild.performAction(AccessibilityNodeInfo.ACTION_PASTE);
                return true;
            }
            if (findEditText(nodeChild, content)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onInterrupt() {

    }
}
