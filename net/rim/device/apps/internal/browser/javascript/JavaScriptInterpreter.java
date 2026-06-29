package net.rim.device.apps.internal.browser.javascript;

import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.RenderingSession;
import net.rim.device.apps.internal.browser.util.Frame;
import org.w3c.dom.html2.HTMLDocument;

public interface JavaScriptInterpreter {
   void documentCreated(HTMLDocument var1, BrowserContent var2, RenderingSession var3, Frame var4);

   void documentLoaded(HTMLDocument var1, String var2);

   void documentClosed(HTMLDocument var1);

   void documentHidden(HTMLDocument var1);

   boolean executeScript(String var1, String var2);

   boolean executeCompiledScript(byte[] var1, int var2, int var3);

   boolean executeMethod(Object var1, String var2, Object[] var3, boolean var4);

   boolean executeMethod(Object var1, String var2, Object[] var3, boolean var4, long var5);

   byte[] getStreamOutput();
}
