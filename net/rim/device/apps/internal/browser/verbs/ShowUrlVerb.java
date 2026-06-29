package net.rim.device.apps.internal.browser.verbs;

import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.system.Clipboard;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.messagelist.ForwardAsVerb;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.utility.general.URI;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

public final class ShowUrlVerb extends Verb {
   private String _url;
   private String _title;
   private int _type;
   public static final int TYPE_PAGE_URL;
   public static final int TYPE_LINK_URL;
   public static final int TYPE_IMAGE_URL;
   private static final int SHOW_PAGE_URL_DESCRIPTION;
   private static final int SHOW_LINK_URL_DESCRIPTION;
   private static final int SHOW_IMAGE_URL_DESCRIPTION;

   public ShowUrlVerb(String title, String url, BrowserContent browserContent, int type) {
      super(type == 0 ? 16987157 : 16987136);
      if (browserContent != null) {
         this._url = browserContent.resolveUrl(url);
      } else {
         this._url = URI.getAbsoluteURL(url, null);
      }

      this._type = type;
      this._title = title;
   }

   @Override
   public final String toString() {
      switch (this._type) {
         case 0:
            return BrowserResources.getString(162);
         case 2:
            return BrowserResources.getString(547);
         default:
            return BrowserResources.getString(352);
      }
   }

   @Override
   public final Object invoke(Object context) {
      showUrl(this._title, this._url);
      return null;
   }

   public static final void showUrl(String title, String url) {
      if (url != null) {
         String[] choices = new Object[]{CommonResources.getString(117), BrowserResources.getString(356), BrowserResources.getString(676)};
         StringBuffer buffer = (StringBuffer)(new Object());
         if (title != null) {
            buffer.append(BrowserResources.getString(706));
            buffer.append(title);
            buffer.append('\n');
         }

         buffer.append(BrowserResources.getString(277));
         buffer.append(url);
         Dialog showUrl = (Dialog)(new Object(buffer.toString(), choices, null, 0, null));
         int returnVal = showUrl.doModal();
         if (returnVal == 1) {
            Clipboard.getClipboard().put(url);
         } else {
            if (returnVal == 2) {
               sendAddress(url, title, 676);
            }
         }
      }
   }

   public static final void sendAddress(String url, String subject, int rbId) {
      ShowUrlVerb$BrowserMessagePartsProvider bmpr = new ShowUrlVerb$BrowserMessagePartsProvider(url, subject);
      ForwardAsVerb faVerb = (ForwardAsVerb)(new Object(bmpr, 0, BrowserResources.getResourceBundle(), rbId));
      if (faVerb.canInvoke(new Object(61))) {
         faVerb.invoke(null);
      }
   }
}
