package net.rim.device.api.system;

import net.rim.device.internal.system.SIMCardEfHandler;
import net.rim.device.internal.system.SIMCardEfTask;

class CellBroadcast$LanguageIndicationWriter implements SIMCardEfTask {
   private byte[] _buffer;
   private CellBroadcast$LanguagePreference[] prefs;
   private int BUFFER_SIZE = 2;

   public CellBroadcast$LanguageIndicationWriter(CellBroadcast$LanguagePreference[] langPrefTableToWrite) {
      this.prefs = langPrefTableToWrite;
   }

   private static int findLanguagePosition(int id) {
      for (int i = 0; i < CellBroadcast.langPrefTable.length; i++) {
         if (id == CellBroadcast.langPrefTable[i]) {
            return i;
         }
      }

      return CellBroadcast.MAX_LANG_PREFS;
   }

   @Override
   public void doWork(SIMCardEfHandler efHandler) {
      int result = efHandler.infoRequest(102);
      if (result == 0) {
         this.BUFFER_SIZE = efHandler.getFileSize();
         int tempD = 0;
         int bufferPosition = 0;
         this._buffer = new byte[this.BUFFER_SIZE];

         for (int i = 0; i < this.BUFFER_SIZE; i++) {
            this._buffer[i] = -1;
         }

         for (int i = 0; i < this.prefs.length; i++) {
            if (this.prefs[i].isEnabled()) {
               tempD = CellBroadcast.ISO639_TO_DEFAULTS[findLanguagePosition(this.prefs[i].getId())];
               this._buffer[bufferPosition + 1] = (byte)(tempD & 0xFF);
               this._buffer[bufferPosition] = (byte)((tempD & 0xFF00) >>> 8);
               bufferPosition += 2;
               if (bufferPosition > 2 * this.BUFFER_SIZE) {
                  break;
               }
            }
         }

         int writeReturnValue = 0;
         if (this._buffer != null) {
            synchronized (CellBroadcast.getInternalChannelInfos()) {
               writeReturnValue = efHandler.writeRequest(102, 0, 0, this._buffer);
            }
         }
      }
   }
}
