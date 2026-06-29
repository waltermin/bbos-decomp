package net.rim.device.apps.internal.browser.page;

import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.internal.browser.common.RenderingUtilities;
import net.rim.device.apps.internal.browser.core.IBrowserContext;
import net.rim.device.apps.internal.browser.html.HTMLContext;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.stack.CacheResult;
import net.rim.device.apps.internal.browser.stack.ModelResult;
import net.rim.device.apps.internal.browser.wml.WMLContext;

public final class BrowserPageModelFactory implements Factory {
   @Override
   public final Object createInstance(Object initialData) {
      if (ContextObject.getFlag(initialData, 61) && ContextObject.getFlag(initialData, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)ContextObject.get(initialData, 255);
         if (syncBuffer != null && !syncBuffer.isEmpty()) {
            if (syncBuffer.getFieldType() != 1 && syncBuffer.getFieldType() != 8 && syncBuffer.getFieldType() != 16 && syncBuffer.getFieldType() != 17) {
               return null;
            }

            BrowserPageModel pageModel = null;

            try {
               int position = syncBuffer.getPosition();
               pageModel = this.readBrowserPageModel(syncBuffer);
               syncBuffer.setPosition(position);
               syncBuffer.skipField();
            } finally {
               ;
            }

            if (syncBuffer.getFieldType() != 2
               && syncBuffer.getFieldType() != 11
               && syncBuffer.getFieldType() != 12
               && syncBuffer.getFieldType() != 14
               && syncBuffer.getFieldType() != 18) {
               return pageModel;
            }

            ModelResult modelResult = null;

            try {
               int position = syncBuffer.getPosition();
               modelResult = this.readModelResult(syncBuffer);
               pageModel.setModelResult(modelResult);
               syncBuffer.setPosition(position);
               syncBuffer.skipField();
            } finally {
               ;
            }

            while (!syncBuffer.isEmpty()) {
               int position = syncBuffer.getPosition();
               switch (syncBuffer.getFieldType()) {
                  case 3:
                  case 7:
                  case 9:
                  case 10:
                  case 13:
                  case 19:
                     CacheResult cacheResult = null;

                     try {
                        cacheResult = CacheResult.readCacheResult(syncBuffer);
                     } finally {
                        ;
                     }

                     modelResult.setCacheResult(cacheResult);
                     break;
                  case 4:
                  case 20:
                     IBrowserContext context = WMLContext.createInstance(syncBuffer);
                     if (context == null) {
                        return null;
                     }

                     modelResult.setContext(context);
                     break;
                  case 24:
                     IBrowserContext htmlContext = HTMLContext.createInstance(syncBuffer);
                     if (htmlContext == null) {
                        return null;
                     }

                     modelResult.setContext(htmlContext);
               }

               try {
                  syncBuffer.setPosition(position);
                  syncBuffer.skipField();
               } finally {
                  ;
               }
            }

            return pageModel;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private final BrowserPageModel readBrowserPageModel(SyncBuffer syncBuffer) {
      int fieldType = syncBuffer.getFieldType();
      DataBuffer dataBuffer = syncBuffer.getDataBuffer();

      try {
         dataBuffer.readShort();
         dataBuffer.readByte();
      } finally {
         ;
      }

      long luid = dataBuffer.readCompressedLong();
      long timestamp = dataBuffer.readCompressedLong();
      if (fieldType < 16) {
         luid = BrowserPageModel.makeLUID();
      }

      int status = dataBuffer.readCompressedInt();
      String title = null;
      if (dataBuffer.readBoolean()) {
         title = dataBuffer.readUTF();
      }

      long parentFolderID = dataBuffer.readCompressedLong();
      boolean homePage = false;
      if (fieldType == 1) {
         String homePageTitle = BrowserResources.getString(355);
         if (homePageTitle.equals(title)) {
            homePage = true;
         }
      } else {
         homePage = dataBuffer.readBoolean();
      }

      String iconUrl = null;
      int version = 0;
      if (fieldType == 17) {
         version = dataBuffer.readCompressedInt();
      }

      if (version >= 2 && dataBuffer.readBoolean()) {
         iconUrl = dataBuffer.readUTF();
      }

      byte updateFlags = 0;
      int updateStart = 0;
      int updatePeriod = 0;
      long lastAccessedTime = 0;
      if (version >= 3) {
         int numItems = dataBuffer.readCompressedInt();

         for (int i = 0; i < numItems; i++) {
            updateFlags = (byte)dataBuffer.readCompressedInt();
            updateStart = dataBuffer.readCompressedInt();
            updatePeriod = dataBuffer.readCompressedInt();
         }
      }

      if (version >= 4) {
         lastAccessedTime = dataBuffer.readCompressedLong();
      }

      BrowserPageModel browserPageModel = new BrowserPageModel(
         luid, timestamp, 0, title, null, parentFolderID, homePage, iconUrl, updateFlags, updateStart, updatePeriod
      );
      browserPageModel.setLastAccessedTime(lastAccessedTime);
      browserPageModel.changeStatus(status);
      return browserPageModel;
   }

   private final ModelResult readModelResult(SyncBuffer syncBuffer) {
      int fieldType = syncBuffer.getFieldType();
      DataBuffer dataBuffer = syncBuffer.getDataBuffer();

      try {
         dataBuffer.readShort();
         dataBuffer.readByte();
      } finally {
         ;
      }

      String url = dataBuffer.readUTF();
      boolean historyUpdate = dataBuffer.readBoolean();
      int renderingFlags = dataBuffer.readCompressedInt();
      int numRequestHeaders = dataBuffer.readCompressedInt();
      HttpHeaders requestHeaders = null;
      if (numRequestHeaders > 0) {
         requestHeaders = new HttpHeaders();

         for (int i = 0; i < numRequestHeaders; i++) {
            String key = dataBuffer.readUTF();
            String value = dataBuffer.readUTF();
            requestHeaders.setProperty(key, value);
         }
      }

      int numMimeTypesToNotAccept = dataBuffer.readCompressedInt();
      if (numMimeTypesToNotAccept > 0) {
         for (int i = 0; i < numMimeTypesToNotAccept; i++) {
            dataBuffer.readUTF();
         }
      }

      String refererURL = null;
      if (dataBuffer.readBoolean()) {
         refererURL = dataBuffer.readUTF();
      }

      if (refererURL != null) {
         if (requestHeaders == null) {
            requestHeaders = new HttpHeaders();
         }

         RenderingUtilities.setReferrer(requestHeaders, refererURL);
      }

      String postDataStr = null;
      if (dataBuffer.readBoolean()) {
         postDataStr = dataBuffer.readUTF();
      }

      boolean isHomePage = false;
      if (fieldType >= 11) {
         isHomePage = dataBuffer.readBoolean();
      }

      String uid = null;
      if (fieldType >= 12 && dataBuffer.readBoolean()) {
         uid = dataBuffer.readUTF();
      }

      if (fieldType >= 14 && dataBuffer.readBoolean()) {
         dataBuffer.readUTF();
      }

      int version = 0;
      if (fieldType == 18) {
         version = dataBuffer.readCompressedInt();
      }

      byte[] postData = null;
      if (version >= 2 && dataBuffer.readBoolean()) {
         postData = dataBuffer.readByteArray();
      }

      if (postData == null && postDataStr != null) {
         postData = postDataStr.getBytes();
      }

      String transportCID = null;
      if (version >= 3 && dataBuffer.readBoolean()) {
         transportCID = dataBuffer.readUTF();
      }

      int configType = BrowserConfigRecord.INVALID_VALUE;
      if (version >= 4) {
         configType = dataBuffer.readCompressedInt();
      }

      ModelResult modelResult = new ModelResult(url, renderingFlags | (historyUpdate ? 8192 : 0), requestHeaders);
      modelResult.setPostData(postData);
      modelResult.setHomePage(isHomePage);
      modelResult.setConfigUID(uid);
      modelResult.setConfigType(configType);
      modelResult.setTransportCID(transportCID);
      return modelResult;
   }
}
