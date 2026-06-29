package net.rim.device.internal.deviceoptions.synchronization;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.internal.deviceoptions.OptionsProviderChangeListener;
import net.rim.device.internal.deviceoptions.OptionsProviderGlobalEventListener;
import net.rim.device.internal.io.file.FileSystemOptions;

final class FileSystemOptionsProvider extends OptionsProviderBase implements OptionsProviderGlobalEventListener {
   private static final int UID = 1820001043;
   private static final int EXTERNAL_MEMORY_ENABLED = 1;
   private static final int USB_MASS_STORAGE_ENABLED = 2;
   private static final int AUTO_ENABLE_USB_MASS_STORAGE = 3;
   private static final int ENCRYPTION_LEVEL = 4;
   private static final int CONTENT_STORE_TOTAL_SIZE = 5;
   private static final int PICTURES_RESERVED_SIZE = 6;
   private static final int SAFELY_REMOVE_MODE = 7;

   FileSystemOptionsProvider(OptionsProviderChangeListener listener) {
      super(listener);
   }

   @Override
   public final int getUID() {
      return 1820001043;
   }

   @Override
   public final void getOptionsData(DataBuffer buffer) {
      if (FileSystemOptions.getExternalMemoryEnabled()) {
         ConverterUtilities.writeEmptyField(buffer, 1);
      }

      if (FileSystemOptions.getUSBMassStorageMode()) {
         ConverterUtilities.writeEmptyField(buffer, 2);
      }

      ConverterUtilities.writeInt(buffer, 3, FileSystemOptions.getAutoEnableUSBMassStorageMode());
      ConverterUtilities.writeInt(buffer, 4, FileSystemOptions.getExternalEncryptionLevel());
      if (FileSystemOptions.isSizesSet()) {
         ConverterUtilities.writeLong(buffer, 5, FileSystemOptions.getContentStoreTotalSize());
         ConverterUtilities.writeLong(buffer, 6, FileSystemOptions.getPicturesReservedSize());
      }

      ConverterUtilities.writeInt(buffer, 7, FileSystemOptions.getSafelyRemoveMode());
   }

   @Override
   public final void setOptionsData(DataBuffer buffer) {
      try {
         if (buffer.available() == 0) {
            FileSystemOptions.restoreDefaults();
         } else {
            boolean enableExternalMemory = false;
            boolean enableUSBMassStorage = false;
            int autoEnableUSBMassStorageMode = -1;
            int encryptionLevel = -1;
            long contentStoreTotalSize = -1;
            long picturesReservedSize = -1;
            int safelyRemoveMode = -1;

            while (buffer.available() > 0) {
               int tag = ConverterUtilities.getType(buffer, true);
               switch (tag) {
                  case 0:
                     ConverterUtilities.skipField(buffer);
                     break;
                  case 1:
                  default:
                     enableExternalMemory = true;
                     ConverterUtilities.skipField(buffer);
                     break;
                  case 2:
                     enableUSBMassStorage = true;
                     ConverterUtilities.skipField(buffer);
                     break;
                  case 3:
                     autoEnableUSBMassStorageMode = ConverterUtilities.readInt(buffer);
                     break;
                  case 4:
                     encryptionLevel = ConverterUtilities.readInt(buffer);
                     break;
                  case 5:
                     contentStoreTotalSize = ConverterUtilities.readLong(buffer);
                     break;
                  case 6:
                     picturesReservedSize = ConverterUtilities.readLong(buffer);
                     break;
                  case 7:
                     safelyRemoveMode = ConverterUtilities.readInt(buffer);
               }
            }

            FileSystemOptions.setExternalMemoryEnabled(enableExternalMemory);
            FileSystemOptions.setUSBMassStorageMode(enableUSBMassStorage);
            if (autoEnableUSBMassStorageMode != -1) {
               FileSystemOptions.setAutoEnableUSBMassStorageMode(autoEnableUSBMassStorageMode);
            }

            if (encryptionLevel != -1) {
               FileSystemOptions.setExternalEncryptionLevel(encryptionLevel);
            }

            if (contentStoreTotalSize >= 0 || picturesReservedSize >= 0) {
               FileSystemOptions.setContentSizes(contentStoreTotalSize, picturesReservedSize);
            }

            if (safelyRemoveMode != -1) {
               FileSystemOptions.setSafelyRemoveMode(safelyRemoveMode);
            }
         }

         FileSystemOptions.save();
      } finally {
         return;
      }
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -3054176912083292366L) {
         this.optionsProviderChanged();
      }
   }

   @Override
   public final long[] getGlobalEventUids() {
      return new long[]{
         -3054176912083292366L,
         20621692766257203L,
         23734410189882194L,
         -3455386809805045760L,
         -2918606221006897090L,
         -3455386809805045760L,
         -3297167379286550693L,
         -3455386809805045760L
      };
   }
}
