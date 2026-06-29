package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Screen;

public interface DocViewGUIProvider {
   void currentElementDescriptionChanged(Object var1);

   void displayEmbeddedObject(String var1);

   void displayEmbeddedLikeObject(DocViewParser var1, String var2, int var3, int var4, byte var5, boolean var6, boolean var7);

   Screen getRenderedScreen(String var1, String var2, EncodedImage var3, int var4, int var5, String var6);

   String getFileName();
}
