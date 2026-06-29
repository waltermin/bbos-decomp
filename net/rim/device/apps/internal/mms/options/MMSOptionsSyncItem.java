package net.rim.device.apps.internal.mms.options;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.OTASyncCapableSyncItem;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.DataBuffer;

class MMSOptionsSyncItem extends OTASyncCapableSyncItem {
   private static final long MMS_OPTIONS_SYNCITEM_GUID = -750633336941733904L;
   private static MMSOptionsSyncItem _instance;
   private static final String DATABASE_NAME = "MMS Options";
   public static final int CURRENT_VERSION = 1;
   private static final int RECEPTION_MODE_TAG = 1;
   private static final int RETRIEVAL_MODE_TAG = 2;
   private static final int RETRIEVAL_LIMIT_TAG = 3;
   private static final int FLAGS_TAG = 4;

   private MMSOptionsSyncItem() {
   }

   public static MMSOptionsSyncItem getInstance() {
      if (_instance == null) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         _instance = (MMSOptionsSyncItem)ar.get(-750633336941733904L);
         if (_instance == null) {
            synchronized (ar) {
               _instance = (MMSOptionsSyncItem)ar.get(-750633336941733904L);
               if (_instance == null) {
                  _instance = new MMSOptionsSyncItem();
                  ar.put(-750633336941733904L, _instance);
               }
            }
         }
      }

      return _instance;
   }

   @Override
   public String getSyncName() {
      return "MMS Options";
   }

   @Override
   public String getSyncName(Locale locale) {
      return null;
   }

   @Override
   public int getSyncVersion() {
      return 1;
   }

   @Override
   public boolean removeAllSyncObjects() {
      MMSOptions.getInstance().resetAllOptions();
      return true;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public boolean setSyncData(DataBuffer buffer, int version) {
      try {
         MMSOptions options = MMSOptions.getInstance();
         boolean allowHomeOnly = MMSClientServiceBook.allowHomeOnly();

         while (buffer.available() > 0) {
            switch (ConverterUtilities.getType(buffer)) {
               case 0:
                  ConverterUtilities.skipField(buffer);
                  break;
               case 1:
               default: {
                  int mode = ConverterUtilities.readInt(buffer);
                  options.setReceptionMode(!allowHomeOnly && mode == 2 ? 0 : mode);
                  break;
               }
               case 2: {
                  int mode = ConverterUtilities.readInt(buffer);
                  options.setAutomaticRetrievalMode(!allowHomeOnly && mode == 2 ? 0 : mode);
                  break;
               }
               case 3:
                  ConverterUtilities.readInt(buffer);
                  break;
               case 4:
                  options.setOptionFlags(ConverterUtilities.readInt(buffer));
            }
         }

         options.commit();
         return true;
      } catch (Throwable var7) {
         System.out.println(ex.toString());
         return false;
      }
   }

   @Override
   public boolean getSyncData(DataBuffer buffer, int version) {
      MMSOptions options = MMSOptions.getInstance();
      ConverterUtilities.convertInt(buffer, 1, options.getReceptionMode(), 1);
      ConverterUtilities.convertInt(buffer, 2, options.getAutomaticRetrievalMode(), 1);
      ConverterUtilities.convertInt(buffer, 4, options.getOptionFlags(), 4);
      return true;
   }
}
