package net.rim.device.apps.internal.mms.ui;

import javax.microedition.io.HttpConnection;
import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.RenderingSession;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.internal.mms.MMSUtilities;
import net.rim.device.apps.internal.mms.api.AttachmentDataProvider;
import net.rim.device.apps.internal.mms.api.MMSAttachment;
import net.rim.device.apps.internal.mms.api.MMSPresentationModel;

final class BrowserPresentationElementField extends VerticalFieldManager implements PresentationElement, MMSAttachment, BrowserRenderingApplication$Callback {
   private MMSAttachment _attachment;
   private AttachmentDataProvider _attachmentProvider;
   private boolean _isForwardLocked;
   private BrowserPresentationElementField$MMSRenderingApplication _mmsRenderingApplication;

   public final void setHeight(int height) {
      if (this._mmsRenderingApplication != null) {
         this._mmsRenderingApplication.setHeight(height);
      }
   }

   @Override
   public final boolean canMove() {
      return false;
   }

   @Override
   public final void move(boolean mode) {
      throw new IllegalArgumentException();
   }

   @Override
   public final String getName() {
      return this._attachment.getName();
   }

   @Override
   public final int getType() {
      return this._attachment.getType();
   }

   @Override
   public final byte[] getData() {
      return this._attachment.getData();
   }

   @Override
   public final String getCharset() {
      return this._attachment.getCharset();
   }

   @Override
   public final int getDataSize() {
      return this._attachment.getDataSize();
   }

   @Override
   public final void copyTo(MMSPresentationModel target) {
      target.addPresentationElement(this.getName(), this.getType(), this.isEditable());
   }

   @Override
   public final void displayBrowserContent(BrowserContent browserContent) {
      synchronized (Application.getEventLock()) {
         this.deleteAll();
         if (browserContent != null) {
            this.add(browserContent.getDisplayableContent());
         }
      }
   }

   private final Field getBrowserContent() {
      try {
         RenderingSession session = RenderingSession.getNewInstance();
         int drmStatus = 0;
         if (this._attachment.getType() == 72) {
            drmStatus = 2048;
         }

         this._mmsRenderingApplication = new BrowserPresentationElementField$MMSRenderingApplication(this, this);
         return session.getBrowserContent(openConnection(this._attachment, drmStatus), this._mmsRenderingApplication, 0).getDisplayableContent();
      } finally {
         ;
      }
   }

   private static final HttpConnection openConnection(MMSAttachment attachment, int drmStatus) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.addProperty("content-type", MMSUtilities.getMIMETypeString(attachment.getType()));
      responseHeaders.addProperty("content-length", Integer.toString(attachment.getDataSize()));
      HttpHeaders requestHeaders = new HttpHeaders();
      requestHeaders.addProperty("charset", attachment.getCharset());
      requestHeaders.addProperty("drm-status", Integer.toString(drmStatus));
      requestHeaders.addProperty("smil-context", "MMS");
      return new AttachmentHttpConnection(attachment, requestHeaders, responseHeaders);
   }

   public BrowserPresentationElementField(MMSAttachment attachment, boolean isEditable, AttachmentDataProvider attachmentProvider, boolean isForwardLocked) {
      this._attachment = attachment;
      this._attachmentProvider = attachmentProvider;
      this._isForwardLocked = isForwardLocked;
      Field f = this.getBrowserContent();
      if (f != null) {
         this.add(f);
      }
   }
}
