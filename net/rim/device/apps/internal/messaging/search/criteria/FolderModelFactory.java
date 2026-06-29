package net.rim.device.apps.internal.messaging.search.criteria;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.internal.messaging.search.LegacyFilterConvert;

public final class FolderModelFactory extends RIMModelFactory {
   private static final long REGKEY;
   private static FolderModelFactory _factory;

   private FolderModelFactory() {
   }

   public static final FolderModelFactory getInstance() {
      if (_factory == null) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         _factory = (FolderModelFactory)ar.getOrWaitFor(2031962665796173461L);
         if (_factory == null) {
            _factory = new FolderModelFactory();
            ar.put(2031962665796173461L, _factory);
         }
      }

      return _factory;
   }

   @Override
   public final boolean recognize(Object o) {
      if (ContextObject.getFlag(o, 22) && ContextObject.getFlag(o, 19)) {
         SyncBuffer sb = (SyncBuffer)ContextObject.get(o, 255);
         if (sb == null) {
            return false;
         }

         switch (sb.getFieldType()) {
            case 8:
            case 13:
               return true;
            default:
               return false;
         }
      } else {
         return o instanceof FolderModel;
      }
   }

   @Override
   public final int getMaximumCount(Object context) {
      return 1;
   }

   @Override
   public final Object createInstance(Object context) {
      if (ContextObject.getFlag(context, 22) && ContextObject.getFlag(context, 19)) {
         SyncBuffer sb = (SyncBuffer)ContextObject.get(context, 255);
         if (sb != null) {
            try {
               int position = sb.getPosition();
               long folderLuid;
               if (sb.containsType(8)) {
                  folderLuid = sb.getLong(8, true);
                  sb.setPosition(position);
                  if (sb.containsType(13)) {
                     sb.getBytes(13, true);
                  }
               } else {
                  byte[] cppFolder = sb.getBytes(13, true);
                  folderLuid = LegacyFilterConvert.extractFolderLUID(cppFolder, 0, false);
               }

               FolderModel m = new FolderModel();
               m._folderId = folderLuid;
               return m;
            } finally {
               return null;
            }
         } else {
            return null;
         }
      } else {
         if (!(context instanceof Object)) {
            return new FolderModel();
         }

         Folder f = (Folder)context;
         FolderModel m = new FolderModel();
         m._folderId = f.getLUID();
         return m;
      }
   }
}
