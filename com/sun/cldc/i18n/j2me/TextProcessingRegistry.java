package com.sun.cldc.i18n.j2me;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.resources.Resource;
import net.rim.device.resources.Resource$Internal;

public final class TextProcessingRegistry {
   private ConversionDataRegistryHelper _conversionRegistryHelper = new ConversionDataRegistryHelper();
   private BreakingDataRegistryHelper _breakingDataRegistryHelper = new BreakingDataRegistryHelper();
   public static final int CONVERSION_DATA_TYPE = 0;
   public static final int COLLATION_DATA_TYPE_TAILORING = 1;
   public static final int BREAKING_DATA_TYPE_LINE = 2;
   public static final int BREAKING_DATA_TYPE_CHAR = 4;
   public static final int BREAKING_DATA_TYPE_WORD = 5;
   private static final long REGISTRY_NAME = -4826416735495760855L;
   private static TextProcessingRegistry _registry;

   public static final TextProcessingRegistry getInstance() {
      return _registry;
   }

   public static final boolean loadTextProcessingData(String dataName, String dataLocation, int dataType, int localeCode, String typeface) {
      return loadTextProcessingData(dataName, dataLocation, dataType, null, -1, localeCode, typeface);
   }

   public static final boolean loadTextProcessingData(
      String dataName, String dataLocation, int dataType, String dataSpecificName, int dataSpecificID, int localeCode, String typeface
   ) {
      boolean rc = false;
      Resource resourceClass = Resource$Internal.getResourceClass(dataLocation);
      if (resourceClass == null) {
         return rc;
      } else {
         byte[] data = resourceClass.getResource(dataName);
         if (data != null) {
            byte[][] blocks = new byte[][]{data};
            return loadTextProcessingData(blocks, dataType, dataSpecificName, dataSpecificID, localeCode, typeface);
         } else {
            return rc;
         }
      }
   }

   public static final boolean loadTextProcessingData(byte[][] data, int dataType, String dataSpecificName, int dataSpecificID, int localeCode, String typeface) {
      switch (dataType) {
         case 0:
            return getInstance()._conversionRegistryHelper.loadConversionData(dataSpecificName, dataSpecificID, localeCode, typeface, data);
         case 2:
            return getInstance()._breakingDataRegistryHelper.loadBreakingData(localeCode, dataType, data);
         default:
            return false;
      }
   }

   public final byte[][] getTextProcessingData(int dataID, int dataType, int[] dataOffset) {
      switch (dataType) {
         case 0:
            return this._conversionRegistryHelper.getConversionData(dataID, dataOffset);
         case 2:
            return this._breakingDataRegistryHelper.getBreakingData(dataID, dataType);
         default:
            return (byte[][])null;
      }
   }

   public final String[] getSupported(int dataType) {
      switch (dataType) {
         case 0:
            return this._conversionRegistryHelper.getSupportedEncodings();
         default:
            return null;
      }
   }

   public final boolean isSupported(String dataSpecificName, int dataType) {
      switch (dataType) {
         case 0:
            return this._conversionRegistryHelper.isSupported(dataSpecificName);
         default:
            return false;
      }
   }

   public final int getSuggestedLocale(String enc) {
      return this._conversionRegistryHelper.getSuggestedLocale(enc);
   }

   public final String getSuggestedTypeface(String enc) {
      return this._conversionRegistryHelper.getSuggestedTypeface(enc);
   }

   public final String getSuggestedTypeface(int localeCode) {
      return this._conversionRegistryHelper.getSuggestedTypeface(localeCode);
   }

   public final String getSuggestedEncoding(int localeCode) {
      return this._conversionRegistryHelper.getSuggestedEncoding(localeCode);
   }

   public final int getTextProcessingDataID(String dataSpecificName, int dataType) {
      switch (dataType) {
         case 0:
            return this._conversionRegistryHelper.getEncodingID(dataSpecificName);
         default:
            return -1;
      }
   }

   public final int getTextProcessingDataID(int localeCode, int dataType) {
      switch (dataType) {
         case 2:
            return this._breakingDataRegistryHelper.getTextProcessingDataID(localeCode, dataType);
         default:
            return -1;
      }
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      if (ar != null) {
         _registry = (TextProcessingRegistry)ar.getOrWaitFor(-4826416735495760855L);
         if (_registry == null) {
            _registry = new TextProcessingRegistry();
            ar.put(-4826416735495760855L, _registry);
            return;
         }
      } else {
         _registry = new TextProcessingRegistry();
      }
   }
}
