package net.rim.device.apps.internal.browser.dd;

import javax.microedition.io.HttpConnection;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.browser.field.RequestedResource;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.NumberUtilities;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.internal.browser.page.BrowserContentImpl;
import net.rim.device.apps.internal.browser.page.Renderer;

final class DownloadDescriptorBrowserContent extends BrowserContentImpl {
   private CodeliveredMediaObject[] _codeliveredMediaObjects = new CodeliveredMediaObject[0];
   private boolean _finishedLoading;
   private boolean _aborted;

   public DownloadDescriptorBrowserContent(
      Renderer renderer, String url, Manager contentManager, RenderingApplication renderingApplication, RenderingOptions renderingOptions, int flags
   ) {
      super(renderer, url, contentManager, renderingApplication, renderingOptions, flags);
   }

   @Override
   public final boolean includeInPageCache() {
      return false;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void finishLoading() {
      boolean var9 = false /* VF: Semaphore variable */;

      try {
         var9 = true;
         super.finishLoading();
         var9 = false;
      } finally {
         if (var9) {
            this._finishedLoading = true;
            synchronized (this._codeliveredMediaObjects) {
               this._codeliveredMediaObjects.notify();
            }
         }
      }

      this._finishedLoading = true;
      synchronized (this._codeliveredMediaObjects) {
         this._codeliveredMediaObjects.notify();
      }
   }

   final void abort() {
      this._aborted = true;
      synchronized (this._codeliveredMediaObjects) {
         this._codeliveredMediaObjects.notify();
      }
   }

   @Override
   public final void resourceReady(RequestedResource resource) {
      if (resource != null) {
         try {
            HttpConnection connection = resource.getHttpConnection();
            if (connection != null && connection.getResponseCode() == 200) {
               CodeliveredMediaObject codeliveredMediaObject = new CodeliveredMediaObject(
                  connection.openInputStream(), connection.getType(), connection.getHeaderField("Content-ID"), connection.getURL()
               );
               synchronized (this._codeliveredMediaObjects) {
                  Arrays.add(this._codeliveredMediaObjects, codeliveredMediaObject);
                  this._codeliveredMediaObjects.notify();
               }
            }
         } finally {
            return;
         }
      }
   }

   final CodeliveredMediaObject getCodelivery(String url) {
      if ((super._flags & 512) != 0 && url != null) {
         while (!this._aborted) {
            synchronized (this._codeliveredMediaObjects) {
               int length = this._codeliveredMediaObjects.length;
               if (length > 0) {
                  if (StringUtilities.startsWithIgnoreCase(url, "cid:", 1701707776)) {
                     String contentID = cidURLtoContentID(url);

                     for (int i = 0; i < length; i++) {
                        if (contentID.equals(this._codeliveredMediaObjects[i].getContentID())) {
                           return this._codeliveredMediaObjects[i];
                        }
                     }
                  } else {
                     for (int i = 0; i < length; i++) {
                        if (url.equals(this._codeliveredMediaObjects[i].getURL())) {
                           return this._codeliveredMediaObjects[i];
                        }
                     }
                  }
               }

               if (this._finishedLoading) {
                  return null;
               }

               try {
                  this._codeliveredMediaObjects.wait();
               } finally {
                  continue;
               }
            }
         }
      }

      return null;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static final String cidURLtoContentID(String cidURL) {
      StringBuffer buffer = new StringBuffer(cidURL.length());
      buffer.append('<');
      int urlLength = cidURL.length();

      for (int i = 4; i < urlLength; i++) {
         char ch = cidURL.charAt(i);
         if (ch == '%' && i + 2 < urlLength) {
            char digit1 = cidURL.charAt(++i);
            char digit2 = cidURL.charAt(++i);
            boolean var10 = false /* VF: Semaphore variable */;

            try {
               var10 = true;
               int nfe = NumberUtilities.hexDigitToInt(digit1);
               int h0 = NumberUtilities.hexDigitToInt(digit2);
               buffer.append((char)(nfe << 4 | h0));
               var10 = false;
            } finally {
               if (var10) {
                  buffer.append(ch);
                  buffer.append(digit1);
                  buffer.append(digit2);
                  continue;
               }
            }
         } else {
            buffer.append(ch);
         }
      }

      buffer.append('>');
      return buffer.toString();
   }
}
