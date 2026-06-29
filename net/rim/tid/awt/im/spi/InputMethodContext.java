package net.rim.tid.awt.im.spi;

import net.rim.device.api.i18n.Locale;
import net.rim.tid.awt.im.InputMethodRequests;
import net.rim.tid.im.ISupplementaryInputData;
import net.rim.tid.text.AttributedString;
import net.rim.tid.text.TextHitInfo;

public interface InputMethodContext extends InputMethodRequests {
   int dispatchInputMethodEvent(int var1, AttributedString var2, long var3, int var5, int var6, TextHitInfo var7, TextHitInfo var8);

   int dispatchInputMethodEvent(
      int var1, int var2, AttributedString var3, long var4, int var6, int var7, TextHitInfo var8, TextHitInfo var9, ISupplementaryInputData var10
   );

   int dispatchInputMethodEvent(
      int var1, int var2, AttributedString var3, long var4, int var6, int var7, TextHitInfo var8, TextHitInfo var9, ISupplementaryInputData var10, byte var11
   );

   void enableClientWindowNotification(InputMethod var1, boolean var2);

   InputMethod getInputMethod(Locale var1);

   void actionPerformed(int var1, Object var2);

   void setIMCookieCache(Object var1);
}
