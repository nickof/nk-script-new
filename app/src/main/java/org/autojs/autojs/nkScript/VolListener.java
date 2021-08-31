package org.autojs.autojs.nkScript;



import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;

import com.stardust.view.accessibility.AccessibilityService;

import org.autojs.autojs.nkScript.interImp.InterMy;


public class VolListener extends AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        InterMy.volEventDo(event);
        return super.onKeyEvent(event);
    }

}
