package net.rim.device.apps.internal.browser.history;

import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.FrameComparator;
import net.rim.device.apps.internal.browser.core.IBrowserContext;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;
import net.rim.device.apps.internal.browser.page.Page;
import net.rim.device.apps.internal.browser.ui.FrameManager;
import net.rim.device.apps.internal.browser.util.Frame;

public final class HistoryNode {
   private String _url;
   private String _title;
   private byte[] _postData;
   private HttpHeaders _requestHeaders;
   private IBrowserContext _context;
   private boolean _isHomePage;
   private long _lastScrollPosition;
   private Frame _frameset;
   private String _configUID;
   private int _configType = BrowserConfigRecord.INVALID_VALUE;
   private String _transportCID;

   public HistoryNode(
      String url, String title, IBrowserContext context, byte[] postData, HttpHeaders requestHeaders, boolean isHomePage, BrowserConfigRecord browserConfig
   ) {
      this(url, title, context, postData, requestHeaders, isHomePage, browserConfig, -1);
   }

   public HistoryNode(
      String url,
      String title,
      IBrowserContext context,
      byte[] postData,
      HttpHeaders requestHeaders,
      boolean isHomePage,
      BrowserConfigRecord browserConfig,
      long lastScrollPosition
   ) {
      this(url, title, context, postData, requestHeaders, isHomePage, browserConfig, lastScrollPosition, null);
   }

   public HistoryNode(
      String url,
      String title,
      IBrowserContext context,
      byte[] postData,
      HttpHeaders requestHeaders,
      boolean isHomePage,
      BrowserConfigRecord browserConfig,
      long lastScrollPosition,
      Frame frameset
   ) {
      this._url = url == null ? "" : url;
      this.setTitle(title);
      this._context = context;
      this._postData = postData;
      this._requestHeaders = requestHeaders;
      this._isHomePage = isHomePage;
      this._lastScrollPosition = lastScrollPosition;
      this._frameset = frameset;
      if (browserConfig != null) {
         this._configUID = browserConfig.getUid();
         this._configType = browserConfig.getPropertyAsInt(12);
         this._transportCID = browserConfig.getPropertyAsString(3);
      }
   }

   public final void setFrameset(Frame frameset) {
      this._frameset = frameset;
   }

   public final String getUrl() {
      return this._url;
   }

   public final String getTitle() {
      return this._title;
   }

   public final void setTitle(String title) {
      this._title = title == null ? "" : title;
   }

   public final byte[] getPostData() {
      return this._postData;
   }

   public final HttpHeaders getRequestHeaders() {
      return this._requestHeaders;
   }

   public final IBrowserContext getContext() {
      return this._context;
   }

   public final boolean isHomePage() {
      return this._isHomePage;
   }

   public final void setLastScrollPosition(long position) {
      this._lastScrollPosition = position;
   }

   public final long getLastScrollPosition() {
      return this._lastScrollPosition;
   }

   public final Frame getFrameset() {
      if (this._frameset == null) {
         return null;
      }

      Page p = BrowserDaemonRegistry.getInstance().getCurrentPage();
      Field field = p.getBrowserContent().getDisplayableContent();
      if (field != null && field instanceof Object) {
         FrameManager fm = this.getFrameManager((Manager)field);
         if (fm != null) {
            Frame top = fm.getFrame().getTop();
            Frame pageClone = Frame.getClone(p.getFrameset(), true);
            FrameComparator comparator = new FrameComparator();
            boolean updateFrame = !comparator.equals(pageClone, top);
            if (!updateFrame) {
               return Frame.getClone(this._frameset.getTop(), false);
            }

            Frame differentFrame1 = comparator.getDifferentFrame1();
            Frame differentFrame2 = comparator.getDifferentFrame2();
            if (differentFrame1 != null) {
               Frame diff1Parent = differentFrame1.getParent();
               if (diff1Parent != null) {
                  diff1Parent.removeChild(differentFrame1);
                  if (differentFrame2 != null) {
                     Frame diff2Frame = (Frame)(new Object(diff1Parent, differentFrame2.getName(), differentFrame2.getUrl(), differentFrame2.getId()));
                     diff1Parent.addFrame(diff2Frame);
                  }
               }

               return pageClone;
            }
         }
      }

      return Frame.getClone(this._frameset, false);
   }

   private final FrameManager getFrameManager(Manager manager) {
      if (!(manager instanceof Object)) {
         int size = manager.getFieldCount();

         for (int i = 0; i < size; i++) {
            Field f = manager.getField(i);
            if (f instanceof Object) {
               return (FrameManager)f;
            }

            if (f instanceof Object) {
               return this.getFrameManager((Manager)f);
            }
         }

         return null;
      } else {
         return (FrameManager)manager;
      }
   }

   public final String getConfigUID() {
      return this._configUID;
   }

   public final int getConfigType() {
      return this._configType;
   }

   public final String getTransportCID() {
      return this._transportCID;
   }

   public static final boolean skipData(DataBuffer dataBuffer, int version) {
      try {
         dataBuffer.readUTF();
         if (version > 0) {
            dataBuffer.readUTF();
         }

         if (dataBuffer.readBoolean()) {
            SyncBuffer syncBuffer = (SyncBuffer)(new Object(dataBuffer, 0, 0));
            syncBuffer.skipField();
         }

         if (version > 1) {
            dataBuffer.readUTF();
         }

         if (version > 2) {
            dataBuffer.readBoolean();
         }

         if (version > 3) {
            dataBuffer.readUTF();
         }

         if (version > 4) {
            dataBuffer.readCompressedLong();
         }

         if (version > 5 && dataBuffer.readBoolean()) {
            dataBuffer.readByteArray();
            return true;
         } else {
            return true;
         }
      } finally {
         ;
      }
   }
}
