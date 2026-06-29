package net.rim.device.api.system;

import net.rim.device.internal.system.SIMCardEfHandler;
import net.rim.device.internal.system.SIMCardEfTask;

class CellBroadcast$LanguageIndication implements SIMCardEfTask {
   private byte[] _buffer;
   private CellBroadcast$LanguagePreference[] _prefs;

   public CellBroadcast$LanguagePreference[] getLangPrefs() {
      if (this._prefs == null) {
         this._prefs = new CellBroadcast$LanguagePreference[CellBroadcast.MAX_LANG_PREFS];
         CellBroadcast.fillLP_TableWithDefaults(this._prefs, 0);
      }

      return this._prefs;
   }

   @Override
   public void doWork(SIMCardEfHandler efHandler) {
      int seenLangs = 0;
      int curPriority = 0;
      this._prefs = new CellBroadcast$LanguagePreference[CellBroadcast.MAX_LANG_PREFS];
      int result = efHandler.infoRequest(102);
      if (result == 0) {
         this._buffer = new byte[efHandler.getFileSize()];
         result = efHandler.readRequest(0, this._buffer);
      }

      if (result == 0 && this._buffer.length >= 1) {
         if (this._buffer[0] == 255 && this._buffer[1] == 255) {
            CellBroadcast.fillLP_TableWithDefaults(this._prefs, seenLangs);
         } else {
            for (int i = 0; i < this._buffer.length - 1; i += 2) {
               for (int j = 0; j < CellBroadcast.MAX_LANG_PREFS; j++) {
                  int temp = this._buffer[i] << 8;
                  temp |= this._buffer[i + 1];
                  if (temp == CellBroadcast.ISO639_TO_DEFAULTS[j] && curPriority < CellBroadcast.MAX_LANG_PREFS) {
                     CellBroadcast$LanguagePreference tempPref = new CellBroadcast$LanguagePreference(CellBroadcast.langPrefTable[j]);
                     tempPref.setPriority(curPriority);
                     tempPref.setEnabled(true);
                     this._prefs[curPriority] = tempPref;
                     seenLangs |= 1 << CellBroadcast.langPrefTable[j];
                     curPriority++;
                  }
               }
            }

            CellBroadcast.fillLP_TableWithDefaults(this._prefs, seenLangs);
         }
      } else {
         CellBroadcast.fillLP_TableWithDefaults(this._prefs, seenLangs);
      }
   }

   public CellBroadcast$LanguageIndication() {
   }
}
