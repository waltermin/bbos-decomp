package net.rim.device.apps.internal.browser.channel;

import net.rim.device.api.synchronization.UIDGenerator;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;
import net.rim.device.apps.internal.browser.stack.CacheModel;
import net.rim.device.apps.internal.browser.stack.CacheModelFactory;
import net.rim.device.apps.internal.browser.stack.CacheNode;
import net.rim.device.apps.internal.browser.stack.CacheResult;
import net.rim.device.apps.internal.browser.stack.RawDataCache;

public final class ChannelModelFactory implements Factory {
   @Override
   public final Object createInstance(Object initialData) {
      if (ContextObject.getFlag(initialData, 71) && ContextObject.getFlag(initialData, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)ContextObject.get(initialData, 255);
         if (syncBuffer != null && !syncBuffer.isEmpty()) {
            if (syncBuffer.getFieldType() != 5 && syncBuffer.getFieldType() != 21) {
               return null;
            }

            ChannelModel channelModel = null;

            try {
               int position = syncBuffer.getPosition();
               channelModel = this.readChannelModel(syncBuffer);
               syncBuffer.setPosition(position);
               syncBuffer.skipField();
            } finally {
               ;
            }

            while (!syncBuffer.isEmpty()) {
               int position = syncBuffer.getPosition();
               int type = syncBuffer.getFieldType();
               if (type == 6 || type == 22) {
                  CacheModel cacheModel = null;

                  try {
                     cacheModel = CacheModelFactory.readCacheModel(syncBuffer);
                     if (cacheModel == null) {
                        return null;
                     }

                     RawDataCache rawDataCache = BrowserDaemonRegistry.getInstance().getRawDataCache();
                     CacheNode cacheNode = cacheModel.getCacheNode();
                     CacheResult cacheResult = cacheModel.getCacheResult();
                     String url = cacheResult.getURLWithoutFragment();
                     if (url != null && url.equals(channelModel.getReadIconURL())) {
                        channelModel.setReadIconData(cacheResult);
                     } else if (url != null && url.equals(channelModel.getUnreadIconURL())) {
                        channelModel.setUneadIconData(cacheResult);
                     }

                     cacheNode.setUrl(url);
                     rawDataCache.addNode(cacheNode, cacheModel.isSticky());
                     rawDataCache.commit();
                  } finally {
                     ;
                  }
               }

               try {
                  syncBuffer.setPosition(position);
                  syncBuffer.skipField();
               } finally {
                  ;
               }
            }

            return channelModel;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private final ChannelModel readChannelModel(SyncBuffer syncBuffer) {
      DataBuffer dataBuffer = syncBuffer.getDataBuffer();
      int fieldType = syncBuffer.getFieldType();

      try {
         dataBuffer.readShort();
         dataBuffer.readByte();
      } finally {
         ;
      }

      String id = dataBuffer.readUTF();
      String url = dataBuffer.readUTF();
      String readIconUrl = null;
      if (dataBuffer.readBoolean()) {
         readIconUrl = dataBuffer.readUTF();
      }

      String unreadIconUrl = null;
      if (dataBuffer.readBoolean()) {
         unreadIconUrl = dataBuffer.readUTF();
      }

      String title = null;
      if (dataBuffer.readBoolean()) {
         title = dataBuffer.readUTF();
      }

      String description = null;
      if (dataBuffer.readBoolean()) {
         description = dataBuffer.readUTF();
      }

      long timestamp = dataBuffer.readCompressedLong();
      long luid = dataBuffer.readCompressedLong();
      long parentFolderLuid = dataBuffer.readCompressedLong();
      int ribbonPosition = dataBuffer.readCompressedInt();
      int status = dataBuffer.readCompressedInt();
      int version = 0;
      if (fieldType == 21) {
         version = dataBuffer.readCompressedInt();
      }

      String configUid = null;
      if (version > 2 && dataBuffer.readBoolean()) {
         configUid = dataBuffer.readUTF();
      }

      String transportCid = null;
      if (version > 3 && dataBuffer.readBoolean()) {
         transportCid = dataBuffer.readUTF();
      }

      int configType = BrowserConfigRecord.INVALID_VALUE;
      if (version > 4) {
         configType = dataBuffer.readCompressedInt();
      }

      int priority = 0;
      if (version > 5) {
         priority = dataBuffer.readCompressedInt();
      }

      String deleteUrl = null;
      if (version > 6 && dataBuffer.readBoolean()) {
         deleteUrl = dataBuffer.readUTF();
      }

      ChannelModel channelModel = new ChannelModel(
         id, url, deleteUrl, readIconUrl, unreadIconUrl, title, description, ribbonPosition, priority, configUid, configType, transportCid
      );
      channelModel.setTimestamp(timestamp);
      if (version < 2) {
         luid = UIDGenerator.getUID();
      }

      channelModel.setLUID(luid);
      channelModel.setParentFolderLUID(parentFolderLuid);
      channelModel.setStatus(status);
      return channelModel;
   }
}
