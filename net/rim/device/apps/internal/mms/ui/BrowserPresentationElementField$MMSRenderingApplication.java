package net.rim.device.apps.internal.mms.ui;

import javax.microedition.io.HttpConnection;
import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.RequestedResource;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.internal.mms.api.MMSAttachment;
import net.rim.device.apps.internal.mms.service.DRMConverter;
import net.rim.device.cldc.io.utility.URIDecoder;

class BrowserPresentationElementField$MMSRenderingApplication extends BrowserRenderingApplication {
   private int _height;
   private int _width;
   private final BrowserPresentationElementField this$0;

   BrowserPresentationElementField$MMSRenderingApplication(BrowserPresentationElementField _1, BrowserRenderingApplication$Callback callback) {
      super(callback);
      this.this$0 = _1;
      this._height = Display.getHeight();
      this._width = Display.getWidth();
   }

   @Override
   public HttpConnection getResource(RequestedResource resource, BrowserContent referrer) {
      String url = URIDecoder.decode(resource.getUrl(), "utf-8");
      MMSAttachment attachment = this.this$0._attachmentProvider.getAttachment(url);
      if (attachment == null) {
         int endpos = url.length();

         while (endpos > 0 && url.charAt(endpos - 1) == '/') {
            endpos--;
         }

         if (endpos > 0) {
            int startpos = url.lastIndexOf(47, endpos - 1);
            if (startpos >= 0) {
               String name = url.substring(startpos + 1, endpos);
               attachment = this.this$0._attachmentProvider.getAttachment(name);
            }
         }
      }

      if (attachment == null && StringUtilities.startsWithIgnoreCase(url, "cid:", 1701707776)) {
         String name = url.substring(4);
         attachment = this.this$0._attachmentProvider.getAttachment(name);
      }

      int drmStatus = this.this$0._isForwardLocked ? 2048 : 0;
      if (attachment != null && attachment.getType() == 72) {
         attachment = DRMConverter.unwrap(attachment);
         if (attachment == null) {
            System.out.println(((StringBuffer)(new Object("MMS - DRM parse failure: "))).append(url).toString());
            return null;
         }

         drmStatus = 2048;
      }

      if (attachment != null) {
         return BrowserPresentationElementField.openConnection(attachment, drmStatus);
      } else {
         System.out.println(((StringBuffer)(new Object("MMS missing resource: "))).append(url).toString());
         if (RadioInfo.getState() != 1) {
            System.out.println("MMS - Can't fetch resource (radio not on)");
            return null;
         } else {
            return null;
         }
      }
   }

   public void setHeight(int height) {
      this._height = height;
   }

   public void setWidth(int width) {
      this._width = width;
   }

   @Override
   public int getAvailableHeight(BrowserContent browserContent) {
      return this._height;
   }

   @Override
   public int getAvailableWidth(BrowserContent browserContent) {
      return this._width;
   }
}
