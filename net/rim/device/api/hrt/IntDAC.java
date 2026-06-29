package net.rim.device.api.hrt;

import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.NumberUtilities;
import net.rim.device.api.util.Persistable;

public final class IntDAC extends DAC implements Persistable {
   private int[] _addrs;
   private int _radix;
   private int _mask;
   private int _fieldCode;

   public IntDAC(int radix, int mask, int fieldCode) {
      this._radix = radix;
      this._mask = mask;
      this._fieldCode = fieldCode;
   }

   public final int getFieldCode() {
      return this._fieldCode;
   }

   @Override
   public final DAC clone() {
      IntDAC d = new IntDAC(this._radix, this._mask, this._fieldCode);
      d._addrs = Arrays.copy(this._addrs);
      this.cloneInto(d);
      return d;
   }

   @Override
   public final boolean isValid() {
      return this._addrs != null && this._addrs.length > 0;
   }

   @Override
   public final int getNumCodes() {
      return this._addrs != null ? this._addrs.length : 0;
   }

   public final int[] getAddresses() {
      return this._addrs;
   }

   public final void setAddresses(int[] addrs) {
      super._dirty = true;
      this._addrs = addrs;
      super._nextCodeIndex = 0;
      if (addrs == null || addrs.length == 0) {
         super._nextCodeIndex = -1;
      }
   }

   @Override
   public final boolean parseField(int type, int length, DataBuffer b) {
      if (type != this._fieldCode) {
         return super.parseField(type, length, b);
      }

      int numAddrs = b.readUnsignedByte();
      if (numAddrs * 4 != length - 1) {
         throw new Object();
      }

      int[] addrs = new int[numAddrs];

      for (int i = 0; i < numAddrs; i++) {
         addrs[i] = b.readInt() & this._mask;
      }

      this.setAddresses(addrs);
      return true;
   }

   @Override
   public final boolean hasEquivalentDestination(DAC dac) {
      if (dac instanceof IntDAC) {
         IntDAC id = (IntDAC)dac;
         int index = id.getNextCodeIndex();
         int addr = id._addrs[index];

         for (int i = this._addrs.length - 1; i >= 0; i--) {
            if (this._addrs[i] == addr) {
               return true;
            }
         }
      }

      return false;
   }

   public final int string2Addr(String str) {
      return Integer.parseInt(str, this._radix);
   }

   public final String addr2String(int addr) {
      StringBuffer strBuf = (StringBuffer)(new Object(16));
      NumberUtilities.appendNumber(strBuf, addr, this._radix);
      return strBuf.toString();
   }

   @Override
   public final void appendAddressString(StringBuffer strBuf, int index) {
      if (this._addrs != null && index >= 0 && index < this._addrs.length) {
         NumberUtilities.appendNumber(strBuf, this._addrs[index], this._radix);
      }
   }

   @Override
   public final int rcvdFromAddress(String addr, int start, int length) {
      int result = 0;

      for (int i = start; i < length; i++) {
         int digit = Character.digit(addr.charAt(i), this._radix);
         if (digit < 0) {
            break;
         }

         if (this._radix == 16) {
            result = result << 4 | digit;
         } else {
            result = result * this._radix + digit;
         }
      }

      if (this._addrs != null) {
         for (int i = this._addrs.length - 1; i >= 0; i--) {
            if (this._addrs[i] == result) {
               return i;
            }
         }
      }

      return -1;
   }

   @Override
   public final int rcvdFromAddress(DatagramAddressBase addr) {
      throw new Object();
   }
}
