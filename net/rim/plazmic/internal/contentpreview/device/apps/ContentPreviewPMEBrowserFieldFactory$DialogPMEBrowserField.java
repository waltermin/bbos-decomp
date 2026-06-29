package net.rim.plazmic.internal.contentpreview.device.apps;

import java.io.InputStream;
import javax.microedition.io.InputConnection;
import net.rim.device.api.browser.field.BrowserContentBaseImpl;
import net.rim.device.apps.internal.browser.pme.PMEBrowserField;

final class ContentPreviewPMEBrowserFieldFactory$DialogPMEBrowserField extends PMEBrowserField {
   private String _message;

   private ContentPreviewPMEBrowserFieldFactory$DialogPMEBrowserField(
      InputConnection inputConnection, InputStream in, BrowserContentBaseImpl browserContent, long fieldStyle, String message
   ) {
      super(inputConnection, in, browserContent, fieldStyle);
      this._message = message;
   }

   @Override
   public final void onDisplay() {
      super.onDisplay();
   }

   ContentPreviewPMEBrowserFieldFactory$DialogPMEBrowserField(
      InputConnection x0, InputStream x1, BrowserContentBaseImpl x2, long x3, String x4, ContentPreviewPMEBrowserFieldFactory$1 x5
   ) {
      this(x0, x1, x2, x3, x4);
   }
}
