package net.rim.device.api.hrt;

import java.util.Random;
import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.Persistable;

public class DAC implements Persistable {
   protected boolean _dirty;
   protected int _nextCodeIndex = -1;
   private int _loadSharingCode;
   public static final int LS_USE_FIRST_VALUE_ONLY = 0;
   public static final int LS_USE_SEQUENTIAL = 1;
   public static final int LS_USE_SEQUENTIAL_ON_FAILURE = 2;
   public static final int LS_USE_RANDOM = 3;
   public static final int LS_USE_RANDOM_ON_FAILURE = 4;
   public static final int LS_USE_LAST_RECEIVED_SEQUENTIAL_ON_FAILURE = 5;
   public static final int LS_USE_LAST_RECEIVED_RANDOM_ON_FAILURE = 6;
   private static Random _rand = (Random)(new Object());

   protected DAC() {
   }

   public DAC clone() {
      throw null;
   }

   protected void cloneInto(DAC dac) {
      dac._dirty = this._dirty;
      dac._loadSharingCode = this._loadSharingCode;
      dac._nextCodeIndex = this._nextCodeIndex;
   }

   public boolean isDirty() {
      return this._dirty;
   }

   public void setDirty(boolean flag) {
      this._dirty = flag;
   }

   public boolean isValid() {
      throw null;
   }

   public int getLoadSharingCode() {
      return this._loadSharingCode;
   }

   public void setLoadSharingCode(int code) {
      this._loadSharingCode = code;
      this._dirty = true;
   }

   public int getNumCodes() {
      throw null;
   }

   public int getNextCodeIndex() {
      return this._nextCodeIndex;
   }

   public void setNextCodeIndex(int next) {
      int num = this.getNumCodes();
      if (num > 0 && next >= 0 && next < num) {
         this._nextCodeIndex = next;
      }
   }

   public void appendAddressString(StringBuffer _1, int _2) {
      throw null;
   }

   public int rcvdFromAddress(String _1, int _2, int _3) {
      throw null;
   }

   public int rcvdFromAddress(DatagramAddressBase _1) {
      throw null;
   }

   public boolean parseField(int type, int length, DataBuffer b) {
      if (type == 3) {
         this.setLoadSharingCode(b.readUnsignedByte());
         return true;
      } else {
         return false;
      }
   }

   public boolean hasEquivalentDestination(DAC _1) {
      throw null;
   }

   boolean handleSendError(int count) {
      int numDacs = this.getNumCodes();
      int index = this._nextCodeIndex;
      boolean result = true;
      if (count <= 0) {
         throw new Object();
      }

      if (numDacs == 0) {
         return false;
      }

      if (count >= numDacs) {
         result = false;
      }

      switch (this._loadSharingCode) {
         case -1:
            break;
         case 0:
         case 1:
         case 3:
         default:
            result = false;
            break;
         case 2:
         case 5:
            index = ++index % numDacs;
            break;
         case 4:
         case 6:
            index = Math.abs(_rand.nextInt()) % numDacs;
      }

      this._nextCodeIndex = index;
      return result;
   }

   void sendSuccessful() {
      int numDacs = this.getNumCodes();
      int index = this._nextCodeIndex;
      if (numDacs != 0) {
         switch (this._loadSharingCode) {
            case -1:
            case 2:
               return;
            case 0:
            default:
               index = 0;
               break;
            case 1:
               index = ++index % numDacs;
               break;
            case 3:
               index = Math.abs(_rand.nextInt()) % numDacs;
         }

         this._nextCodeIndex = index;
      }
   }

   void rcvFromIndex(int index) {
      if (this._loadSharingCode == 5 || this._loadSharingCode == 6) {
         this.setNextCodeIndex(index);
      }
   }
}
