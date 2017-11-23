package com.shijiwei.xkit.utility.display;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;

import java.lang.reflect.Method;

/**
 * Created by shijiwei on 2017/9/19.
 *
 * @VERSION 1.0
 */

public class DisplayUtility {


    /**
     * 获取屏幕尺寸，但是不包括虚拟功能高度
     *
     * @return
     */
    public static int getNoHasVirtualKey(Activity act) {
        Point display = new Point();
        act.getWindowManager().getDefaultDisplay().getSize(display);
        return display.y;
    }

    /**
     * 通过反射，获取包含虚拟键的整体屏幕高度
     *
     * @return
     */
    public static int getHasVirtualKey(Activity act) {
        int dpi = 0;
        Display display = act.getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            dpi = dm.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dpi;
    }

    /**
     * 获取是否存在NavigationBar(虚拟键)
     *
     * @param ctx
     * @return
     */
    public static boolean checkDeviceHasVirtualKey(Context ctx) {
        boolean hasNavigationBar = false;
        Resources rs = ctx.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {

        }
        return hasNavigationBar;

    }

    /**
     * 如果设备有虚拟键，则返回其高度dpi
     *
     * @param act
     * @return
     */
    public static int getVirtualKeyHeight(Activity act) {
        int dpi = 0;
        if (checkDeviceHasVirtualKey(act)) {
            dpi = getHasVirtualKey(act) - getNoHasVirtualKey(act);
        }
        return dpi;
    }

    /**
     * 获取状态栏的高度
     *
     * @param act
     * @return
     */
    public static int getStatusbarHeight(Activity act) {
        int statusBarHeight = 0;
        int heightResId = act.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (heightResId > 0) {
            statusBarHeight = act.getResources().getDimensionPixelSize(heightResId);
        }
        return statusBarHeight;
    }




    /**
     * 依据设备的分辨率 dp 转 px
     *
     * @param context
     * @param dp
     * @return
     */
    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    /**
     * 依据设备的分辨率 px 转 dp
     *
     * @param context
     * @param px
     * @return
     */
    public static int px2dp(Context context, int px) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px, context.getResources().getDisplayMetrics());
    }


}
