package net.rim.device.apps.internal.browser.multipart;

import net.rim.device.apps.internal.browser.common.BrowserConverterDescriptor;

public final class MultipartConverterDescriptor extends BrowserConverterDescriptor {
   private static final String[] ACCEPT = new String[]{
      "application/vnd.wap.multipart.mixed",
      "application/vnd.wap.multipart.alternative",
      "application/vnd.wap.multipart.related",
      "multipart/mixed",
      "multipart/alternative",
      "multipart/related"
   };

   public MultipartConverterDescriptor() {
      super(new MultipartConverter("net.rim.device.apps.internal.browser"), ACCEPT);
   }

   public static final void registerOnStartup() {
      new MultipartConverterDescriptor().register();
   }
}
