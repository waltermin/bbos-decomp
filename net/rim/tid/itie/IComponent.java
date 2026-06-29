package net.rim.tid.itie;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.XYRect;
import net.rim.tid.awt.Event;
import net.rim.tid.awt.event.InputMethodListener;
import net.rim.tid.awt.im.InputContext;
import net.rim.tid.awt.im.InputMethodRequests;

public interface IComponent extends InputMethodListener {
   InputMethodRequests getInputMethodRequests();

   InputContext getInputContext();

   void enableInputMethods(boolean var1);

   void dispatchEvent(Event var1);

   boolean isInputMethodEnabled();

   XYRect getBounds();

   void setFont(Font var1);

   void actionPerformed(int var1, Object var2);

   void setIMCookieCache(Object var1);

   int getFieldStyle();
}
