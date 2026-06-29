package com.sun.cldc.i18n.j2me;

final class BreakingDataRegistryHelper {
   private BreakingDataRegistryHelper$BreakingData[] _breakingDataTable = new BreakingDataRegistryHelper$BreakingData[0];
   private int _supportedLocalesNum;
   private static final int BREAKING_INCREMENT_NUMBER;

   final synchronized boolean loadBreakingData(int locale, int dataType, byte[][][] data) {
      int i = 0;

      int length;
      for (length = this._breakingDataTable.length; i < length; i++) {
         BreakingDataRegistryHelper$BreakingData entry = this._breakingDataTable[i];
         if (entry != null && entry._locale == locale && entry._dataType == dataType) {
            break;
         }
      }

      if (i == length) {
         if (this._supportedLocalesNum == length) {
            BreakingDataRegistryHelper$BreakingData[] arr = new BreakingDataRegistryHelper$BreakingData[this._supportedLocalesNum + 2];
            System.arraycopy(this._breakingDataTable, 0, arr, 0, this._breakingDataTable.length);
            this._breakingDataTable = arr;
         }

         this._breakingDataTable[this._supportedLocalesNum++] = new BreakingDataRegistryHelper$BreakingData(locale, dataType);
         this._breakingDataTable[this._supportedLocalesNum - 1]._binaryData = data;
         return true;
      } else {
         this._breakingDataTable[i]._locale = locale;
         this._breakingDataTable[i]._binaryData = data;
         return true;
      }
   }

   final byte[][][] getBreakingData(int locale, int dataType) {
      BreakingDataRegistryHelper$BreakingData entry = this.runOverTheData(locale, dataType);
      return (byte[][][])(entry != null ? entry._binaryData : (byte[][])null);
   }

   final int getTextProcessingDataID(int locale, int dataType) {
      BreakingDataRegistryHelper$BreakingData entry = this.runOverTheData(locale, dataType);
      return entry != null ? entry._locale : -1;
   }

   private final synchronized BreakingDataRegistryHelper$BreakingData runOverTheData(int locale, int dataType) {
      int length = this._breakingDataTable.length;

      for (int i = 0; i < length; i++) {
         BreakingDataRegistryHelper$BreakingData entry = this._breakingDataTable[i];
         if (entry != null && entry._locale == locale && entry._dataType == dataType) {
            return entry;
         }
      }

      return null;
   }
}
