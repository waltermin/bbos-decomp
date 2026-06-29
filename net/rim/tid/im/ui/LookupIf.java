package net.rim.tid.im.ui;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.ui.XYRect;
import net.rim.tid.im.SLInputMethod;
import net.rim.tid.im.conv.SLVariants;

public interface LookupIf {
   byte LS_VERTICAL;
   byte LS_USE_TEXT_WIDTH;

   boolean isVisible();

   boolean isDisplayed();

   void setVisible(boolean var1);

   void composedTextChanged(XYRect var1);

   void setVariants(SLVariants var1);

   int actionPerformed(Object var1, int var2, Object var3);

   void setStyle(byte var1);

   void init(SLInputMethod var1, Locale var2, int var3);

   void setLabels(String var1, String var2);
}
