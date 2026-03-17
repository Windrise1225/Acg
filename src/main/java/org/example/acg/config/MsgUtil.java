package org.example.acg.config;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

/**
 * 全局消息提示工具类
 * 用法：
 *   MsgUtil.success("保存成功"); // 默认底部居中
 *   MsgUtil.success("保存成功", Notification.Position.TOP_END); // 指定右上角
 */
public class MsgUtil {

    // 默认显示时间 (毫秒)
    private static final int DEFAULT_DURATION = 3000;

    // ================= 默认位置 (底部居中) 的快捷方法 =================

    public static void success(String message) {
        show(message, NotificationVariant.LUMO_SUCCESS, DEFAULT_DURATION, Notification.Position.BOTTOM_CENTER);
    }

    public static void success(String message, int durationMs) {
        show(message, NotificationVariant.LUMO_SUCCESS, durationMs, Notification.Position.BOTTOM_CENTER);
    }

    public static void error(String message) {
        show(message, NotificationVariant.LUMO_ERROR, DEFAULT_DURATION, Notification.Position.BOTTOM_CENTER);
    }

    public static void errorPermanent(String message) {
        show(message, NotificationVariant.LUMO_ERROR, 0, Notification.Position.BOTTOM_CENTER);
    }

    public static void warning(String message) {
        show(message, NotificationVariant.LUMO_WARNING, DEFAULT_DURATION, Notification.Position.BOTTOM_CENTER);
    }

    public static void info(String message) {
        show(message, null, DEFAULT_DURATION, Notification.Position.BOTTOM_CENTER);
    }

    // ================= 可自定义位置的重载方法 =================

    /**
     * 成功提示 (自定义位置)
     */
    public static void success(String message, Notification.Position position) {
        show(message, NotificationVariant.LUMO_SUCCESS, DEFAULT_DURATION, position);
    }

    /**
     * 错误提示 (自定义位置)
     */
    public static void error(String message, Notification.Position position) {
        show(message, NotificationVariant.LUMO_ERROR, DEFAULT_DURATION, position);
    }

    /**
     * 警告提示 (自定义位置)
     */
    public static void warning(String message, Notification.Position position) {
        show(message, NotificationVariant.LUMO_WARNING, DEFAULT_DURATION, position);
    }

    /**
     * 普通提示 (自定义位置)
     */
    public static void info(String message, Notification.Position position) {
        show(message, null, DEFAULT_DURATION, position);
    }

    /**
     * 通用方法：完全自定义 (内容, 样式, 时间, 位置)
     */
    public static void show(String message, NotificationVariant variant, int durationMs, Notification.Position position) {
        if (UI.getCurrent() == null) {
            System.err.println("错误：MsgUtil 必须在 UI 线程中调用！");
            return;
        }

        Notification notification = new Notification(message);
        notification.setDuration(durationMs);

        // 设置位置
        notification.setPosition(position != null ? position : Notification.Position.BOTTOM_CENTER);

        if (variant != null) {
            notification.addThemeVariants(variant);
        }

        notification.open();
    }
}