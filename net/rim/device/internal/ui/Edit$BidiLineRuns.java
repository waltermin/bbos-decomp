package net.rim.device.internal.ui;

import net.rim.device.api.util.Arrays;

public class Edit$BidiLineRuns {
   public int[] _runs;
   public byte[] _bidiState;
   private boolean _ignoreState = true;
   public static final int PARAGRAPH_DIR_LTR = 0;
   public static final int PARAGRAPH_DIR_RTL = 1;
   public static final int PARAGRAPH_DIR_FORCED_LTR = 2;
   public static final int PARAGRAPH_DIR_FORCED_RTL = 3;

   Edit$BidiLineRuns() {
      this._runs = new int[0];
      this._bidiState = new byte[0];
   }

   private Edit$BidiLineRuns(int[] runs, byte[] bidiState) {
      this._runs = runs;
      this._bidiState = bidiState;
      this._ignoreState = false;
   }

   public void ignore(boolean state) {
      this._ignoreState = state;
   }

   public boolean isIgnored() {
      return this._ignoreState;
   }

   public Edit$BidiLineRuns clone() {
      return this._ignoreState
         ? null
         : new Edit$BidiLineRuns(Arrays.copy(this._runs), this._bidiState != null && this._bidiState.length > 0 ? Arrays.copy(this._bidiState) : null);
   }

   public boolean isCompleteForwardRun() {
      if (this._runs.length == 3) {
         return this._runs[2] == 0;
      }

      int nextPos = 0;

      for (int i = 0; i < this._runs.length; i++) {
         if (this._runs[i++] != nextPos) {
            return false;
         }

         nextPos += this._runs[i++];
         if (this._runs[i] != 0) {
            return false;
         }
      }

      return true;
   }
}
