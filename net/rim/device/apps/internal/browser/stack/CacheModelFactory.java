package net.rim.device.apps.internal.browser.stack;

import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.SyncBuffer;

public final class CacheModelFactory implements Factory {
   @Override
   public final Object createInstance(Object initialData) {
      if (ContextObject.getFlag(initialData, 72) && ContextObject.getFlag(initialData, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)ContextObject.get(initialData, 255);
         if (syncBuffer != null && !syncBuffer.isEmpty()) {
            if (syncBuffer.getFieldType() != 6 && syncBuffer.getFieldType() != 22) {
               return null;
            }

            CacheModel cacheModel = null;

            try {
               return readCacheModel(syncBuffer);
            } finally {
               ;
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public static final CacheModel readCacheModel(SyncBuffer syncBuffer) {
      CacheModel cacheModel = null;
      int position = syncBuffer.getPosition();
      int fieldType = syncBuffer.getFieldType();
      DataBuffer dataBuffer = syncBuffer.getDataBuffer();

      try {
         dataBuffer.readShort();
         dataBuffer.readByte();
      } finally {
         ;
      }

      byte persistence = dataBuffer.readByte();
      long creationDate = dataBuffer.readCompressedLong();
      long accessedDate = dataBuffer.readCompressedLong();
      if (fieldType == 22) {
         dataBuffer.readCompressedInt();
      }

      syncBuffer.setPosition(position);
      syncBuffer.skipField();
      fieldType = syncBuffer.getFieldType();
      if (fieldType != 3 && fieldType != 7 && fieldType != 9 && fieldType != 10 && fieldType != 13 && fieldType != 19) {
         return null;
      }

      try {
         position = syncBuffer.getPosition();
         CacheResult cacheResult = CacheResult.readCacheResult(syncBuffer);
         if (cacheResult == null) {
            return null;
         }

         CacheNode cacheNode = new CacheNode(null, cacheResult, (persistence & 2) != 0);
         cacheNode.setCreationDate(creationDate);
         cacheNode.setAccessedDate(accessedDate);
         cacheModel = new CacheModel(cacheNode, persistence > 0);
         syncBuffer.setPosition(position);
         syncBuffer.skipField();
         return cacheModel;
      } finally {
         ;
      }
   }
}
