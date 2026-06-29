package net.rim.device.apps.internal.browser.plugin.media.menu;

import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.UrlRequestedEvent;
import net.rim.device.api.io.MIMETypeAssociations;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.internal.browser.plugin.media.field.MediaBrowserField;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.ui.SaveFileDialog;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.media.HTTPBufferingManager;

public final class SaveMenuItem extends MenuItem {
   private String _contentUrl;
   private String _contentType;
   private boolean _drmForwardLock;
   private MediaBrowserField _field;

   public SaveMenuItem(MediaBrowserField field, boolean drmForwardLock, String contentUrl, String contentType) {
      super(CommonResource.getBundle(), 18, 332288, Integer.MAX_VALUE);
      this._contentUrl = contentUrl;
      this._contentType = contentType;
      this._drmForwardLock = drmForwardLock;
      this._field = field;
   }

   @Override
   public final void run() {
      HTTPBufferingManager bufferingManager = this._field.getBufferingManager();
      if (bufferingManager == null || this._contentType == null) {
         Dialog.alert(BrowserResources.getString(828));
      } else if (bufferingManager.bufferContainsAllContent()) {
         SaveFileDialog.save(
            this._contentUrl,
            this._contentType,
            MIMETypeAssociations.getMediaTypeFromMIMEType(this._contentType),
            this._drmForwardLock,
            bufferingManager.getBuffer()
         );
      } else {
         BrowserContent browserContent = this._field.getBrowserContent();
         browserContent.getRenderingApplication().eventOccurred(new UrlRequestedEvent(browserContent, this._contentUrl, null, null, false, 0));
      }
   }
}
