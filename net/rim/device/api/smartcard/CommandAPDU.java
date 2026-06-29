package net.rim.device.api.smartcard;

import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.vm.Array;

public class CommandAPDU {
   private byte _CLA;
   private byte _INS;
   private byte _P1;
   private byte _P2;
   private int _Lc;
   private int _Le;
   private byte[] _data;
   private int _apduHeaderLength = 4;
   public static final int CASE_1 = 1;
   public static final int CASE_2 = 2;
   public static final int CASE_3 = 3;
   public static final int CASE_4 = 4;

   public CommandAPDU() {
   }

   public CommandAPDU(byte CLA, byte INS, byte P1, byte P2) {
      this.set(CLA, INS, P1, P2, null, -1);
   }

   public CommandAPDU(byte CLA, byte INS, byte P1, byte P2, int Le) {
      this.set(CLA, INS, P1, P2, null, Le);
   }

   public CommandAPDU(byte CLA, byte INS, byte P1, byte P2, byte[] LcData) {
      this.set(CLA, INS, P1, P2, LcData, -1);
   }

   public CommandAPDU(byte CLA, byte INS, byte P1, byte P2, byte[] LcData, int Le) {
      this.set(CLA, INS, P1, P2, LcData, Le);
   }

   public CommandAPDU(byte[] apduBytes) {
      this(new DataBuffer(apduBytes, 0, apduBytes.length, true));
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public CommandAPDU(DataBuffer apduBuffer) throws SmartCardInvalidAPDUException {
      try {
         int bytesAvailable = apduBuffer.available();
         if (bytesAvailable == 0) {
            throw new SmartCardInvalidAPDUException("Invalid command APDU length. Length = " + apduBuffer.available());
         }

         this._apduHeaderLength = 0;
         byte cla = apduBuffer.readByte();
         this._apduHeaderLength++;
         byte ins = 0;
         byte p1 = 0;
         byte p2 = 0;
         if (this._apduHeaderLength < bytesAvailable) {
            ins = apduBuffer.readByte();
            this._apduHeaderLength++;
         }

         if (this._apduHeaderLength < bytesAvailable) {
            p1 = apduBuffer.readByte();
            this._apduHeaderLength++;
         }

         if (this._apduHeaderLength < bytesAvailable) {
            p2 = apduBuffer.readByte();
            this._apduHeaderLength++;
         }

         int le = -1;
         byte[] lcData = null;
         boolean apduSet = false;
         int bytesLeft = apduBuffer.available();
         if (bytesLeft != 0) {
            if (bytesLeft == 1) {
               le = apduBuffer.readByte() & 255;
            } else {
               int b1 = apduBuffer.readByte() & 255;
               if (bytesLeft == 1 + b1 && b1 != 0) {
                  lcData = new byte[b1];
                  apduBuffer.read(lcData);
               } else if (bytesLeft == 2 + b1 && b1 != 0) {
                  lcData = new byte[b1];
                  apduBuffer.read(lcData);
                  le = apduBuffer.readByte() & 255;
               } else if (bytesLeft == 3 && b1 == 0) {
                  le = (apduBuffer.readByte() & 255) << 8;
                  le |= apduBuffer.readByte() & 255;
                  if (le == 0) {
                     le = 65536;
                  }
               } else if (b1 != 0) {
                  if (bytesLeft <= 1 || b1 == 0) {
                     throw new SmartCardInvalidAPDUException("Invalid Command APDU format");
                  }

                  int available = apduBuffer.available();
                  if (available < b1) {
                     lcData = new byte[available];
                     apduBuffer.read(lcData);
                  }

                  this.set(cla, ins, p1, p2, lcData, b1, le);
                  apduSet = true;
               } else {
                  int lc = (apduBuffer.readByte() & 255) << 8;
                  lc |= apduBuffer.readByte() & 255;
                  if (bytesLeft == 3 + lc && lc != 0) {
                     lcData = new byte[lc];
                     apduBuffer.read(lcData);
                  } else if (bytesLeft != 3 || lc != 0) {
                     if (bytesLeft == 5 + lc && lc != 0) {
                        lcData = new byte[lc];
                        apduBuffer.read(lcData);
                        le = (apduBuffer.readByte() & 255) << 8;
                        le |= apduBuffer.readByte() & 255;
                        if (le == 0) {
                           le = 65536;
                        }
                     } else if (bytesLeft == 5 && lc == 0) {
                        le = (apduBuffer.readByte() & 255) << 8;
                        le |= apduBuffer.readByte() & 255;
                        if (le == 0) {
                           le = 65536;
                        }
                     }
                  }
               }
            }
         }

         if (!apduSet) {
            this.set(cla, ins, p1, p2, lcData, le);
         }
      } catch (Throwable var15) {
         throw new SmartCardInvalidAPDUException(e.toString());
      }
   }

   public void set(byte CLA, byte INS, byte P1, byte P2) {
      this.set(CLA, INS, P1, P2, null, -1);
   }

   public void set(byte CLA, byte INS, byte P1, byte P2, int Le) {
      this.set(CLA, INS, P1, P2, null, Le);
   }

   public void set(byte CLA, byte INS, byte P1, byte P2, byte[] LcData) {
      this.set(CLA, INS, P1, P2, LcData, -1);
   }

   public void set(byte CLA, byte INS, byte P1, byte P2, byte[] LcData, int Lc, int Le) {
      this.set(CLA, INS, P1, P2, LcData, Le);
      this._Lc = Lc;
   }

   public void set(byte CLA, byte INS, byte P1, byte P2, byte[] LcData, int Le) {
      this._CLA = CLA;
      this._INS = INS;
      this._P1 = P1;
      this._P2 = P2;
      this.setLe(Le);
      this._Lc = -1;
      this._data = null;
      if (LcData != null) {
         this.setLcData(LcData);
      }
   }

   public byte getCLA() {
      return this._CLA;
   }

   public byte getINS() {
      return this._INS;
   }

   public byte getP1() {
      return this._P1;
   }

   public byte getP2() {
      return this._P2;
   }

   public void setLcData(byte[] LcData) {
      if (LcData == null) {
         throw new IllegalArgumentException();
      }

      this.setLcData(LcData, 0, LcData.length);
   }

   public void setLcData(byte[] LcData, int offset, int length) {
      if (LcData != null && offset + length <= LcData.length && offset >= 0 && length > 0) {
         this._data = Arrays.copy(LcData, offset, length);
         this._Lc = length;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void appendLcData(byte[] moreLcData) {
      if (moreLcData == null) {
         throw new IllegalArgumentException();
      }

      this.appendLcData(moreLcData, 0, moreLcData.length);
   }

   public void appendLcData(byte[] moreLcData, int offset, int length) {
      if (moreLcData == null || offset + length > moreLcData.length || offset < 0 || length <= 0) {
         throw new IllegalArgumentException();
      }

      if (this._data == null) {
         this.setLcData(moreLcData, offset, length);
      } else {
         int dataLength = this._data.length;
         Array.resize(this._data, dataLength + length);
         System.arraycopy(moreLcData, offset, this._data, dataLength, length);
         this._Lc = this._data.length;
      }
   }

   public byte[] getLcData() {
      return this._data;
   }

   public int getLc() {
      return this._Lc;
   }

   public void setLe(int Le) {
      if (Le < 0) {
         this._Le = -1;
      } else {
         this._Le = Le;
      }
   }

   public int getLe() {
      return this._Le;
   }

   public int getCase() {
      if (this._data != null) {
         return this._Le == -1 ? 3 : 4;
      } else {
         return this._Le == -1 ? 1 : 2;
      }
   }

   public boolean isExtended() {
      return this._Le > 255 || this._Lc > 255;
   }

   public byte[] getAPDU() {
      int caseType = this.getCase();
      boolean isExtended = this.isExtended();
      int apduLength = this._apduHeaderLength < 4 ? this._apduHeaderLength : 4;
      if (this._data != null) {
         apduLength += this._data.length;
      }

      switch (caseType) {
         case 1:
            break;
         case 2:
         default:
            apduLength += isExtended ? 3 : 1;
            break;
         case 3:
            apduLength += isExtended ? 3 : 1;
            break;
         case 4:
            apduLength += isExtended ? 5 : 2;
      }

      byte[] result = new byte[apduLength];
      int offset = 0;
      result[offset++] = (byte)(this._CLA & 0xFF);
      if (apduLength < 2) {
         return result;
      }

      result[offset++] = (byte)(this._INS & 0xFF);
      if (apduLength < 3) {
         return result;
      }

      result[offset++] = (byte)(this._P1 & 0xFF);
      if (apduLength < 4) {
         return result;
      }

      result[offset++] = (byte)(this._P2 & 0xFF);
      if (this._data != null) {
         if (!isExtended) {
            result[offset++] = (byte)(this._Lc & 0xFF);
            System.arraycopy(this._data, 0, result, offset, this._data.length);
         } else {
            result[offset++] = 0;
            result[offset++] = (byte)(this._Lc >> 8 & 0xFF);
            result[offset++] = (byte)(this._Lc & 0xFF);
            System.arraycopy(this._data, 0, result, offset, this._data.length);
         }

         offset += this._data.length;
      }

      if (this._Le != -1) {
         if (!isExtended) {
            result[offset++] = (byte)(this._Le & 0xFF);
         } else {
            if (caseType == 2) {
               result[offset++] = 0;
            }

            result[offset++] = (byte)(this._Le >> 8 & 0xFF);
            result[offset++] = (byte)(this._Le & 0xFF);
         }
      }

      return result;
   }

   @Override
   public String toString() {
      byte[] apdu = this.getAPDU();
      StringBuffer data = new StringBuffer();
      if (apdu != null) {
         int length = apdu.length;

         for (int i = 0; i < length; i++) {
            data.append(Integer.toHexString(apdu[i] & 255));
            if (i != length - 1) {
               data.append(' ');
            }
         }
      }

      return data.toString();
   }
}
