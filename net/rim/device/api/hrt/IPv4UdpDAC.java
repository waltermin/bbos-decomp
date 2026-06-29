package net.rim.device.api.hrt;

import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.io.UdpAddress;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.NumberUtilities;
import net.rim.device.api.util.Persistable;

public final class IPv4UdpDAC extends DAC implements Persistable {
   private long[] _addrs;
   private int _udpFieldType;
   private int _ipFieldType;

   public IPv4UdpDAC(int ipType, int udpType) {
      this._ipFieldType = ipType;
      this._udpFieldType = udpType;
   }

   @Override
   public final boolean isValid() {
      return this._addrs != null && this._addrs.length > 0;
   }

   @Override
   public final DAC clone() {
      IPv4UdpDAC d = new IPv4UdpDAC(this._ipFieldType, this._udpFieldType);
      d._addrs = Arrays.copy(this._addrs);
      this.cloneInto(d);
      return d;
   }

   @Override
   public final int getNumCodes() {
      return this._addrs != null ? this._addrs.length : 0;
   }

   public final long[] getAddresses() {
      return this._addrs;
   }

   public final void setAddresses(long[] addrs) {
      super._dirty = true;
      this._addrs = addrs;
      super._nextCodeIndex = 0;
      if (addrs == null || addrs.length == 0) {
         super._nextCodeIndex = -1;
      }
   }

   @Override
   public final void appendAddressString(StringBuffer strBuf, int index) {
      if (this._addrs != null && index >= 0 && index < this._addrs.length) {
         long addr = this._addrs[index];
         NumberUtilities.appendNumber(strBuf, addr2IpAddress(addr), 16);
         strBuf.append(':');
         NumberUtilities.appendNumber(strBuf, addr2DstPort(addr), 16);
         strBuf.append(':');
         int temp = addr2SrcPort(addr);
         if (temp != 0) {
            NumberUtilities.appendNumber(strBuf, temp, 16);
         }
      }
   }

   @Override
   public final int rcvdFromAddress(String addr, int start, int length) {
      int ipAddr = 0;

      for (int i = start; i < length; i++) {
         int digit = Character.digit(addr.charAt(i), 16);
         if (digit < 0) {
            break;
         }

         ipAddr = ipAddr << 4 | digit;
      }

      if (this._addrs != null) {
         for (int i = this._addrs.length - 1; i >= 0; i--) {
            if (addr2IpAddress(this._addrs[i]) == ipAddr) {
               return i;
            }
         }
      }

      return -1;
   }

   @Override
   public final int rcvdFromAddress(DatagramAddressBase addr) {
      if (!(addr instanceof UdpAddress)) {
         throw new IllegalArgumentException();
      }

      UdpAddress udpAddress = (UdpAddress)addr;
      int ipAddr = udpAddress.getIpAddressInt();

      for (int i = 0; i < this._addrs.length; i++) {
         if (ipAddr == addr2IpAddress(this._addrs[i])) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public final boolean parseField(int type, int length, DataBuffer b) {
      if (type != this._udpFieldType && type != this._ipFieldType) {
         return super.parseField(type, length, b);
      }

      int numAddrs = b.readUnsignedByte();
      if (numAddrs * 4 != length - 1) {
         throw new IllegalArgumentException();
      }

      long[] addrs = this.getAddresses();
      if (addrs == null) {
         addrs = new long[numAddrs];
      } else if (addrs.length != numAddrs) {
         throw new IllegalArgumentException();
      }

      for (int i = 0; i < numAddrs; i++) {
         int ipAddr;
         int srcPort;
         int dstPort;
         if (type == this._ipFieldType) {
            ipAddr = b.readInt();
            dstPort = addr2DstPort(addrs[i]);
            srcPort = addr2SrcPort(addrs[i]);
         } else {
            ipAddr = addr2IpAddress(addrs[i]);
            dstPort = b.readUnsignedShort();
            srcPort = b.readUnsignedShort();
         }

         addrs[i] = makeAddr(ipAddr, dstPort, srcPort);
      }

      this.setAddresses(addrs);
      return true;
   }

   @Override
   public final boolean hasEquivalentDestination(DAC dac) {
      if (dac instanceof IPv4UdpDAC) {
         IPv4UdpDAC ipd = (IPv4UdpDAC)dac;
         int index = ipd.getNextCodeIndex();
         long addr = ipd._addrs[index];

         for (int i = this._addrs.length - 1; i >= 0; i--) {
            if (this._addrs[i] == addr) {
               return true;
            }
         }
      }

      return false;
   }

   public static final int addr2IpAddress(long addr) {
      return (int)(addr >>> 32);
   }

   public static final int addr2DstPort(long addr) {
      return (int)(addr >>> 16) & 65535;
   }

   public static final int addr2SrcPort(long addr) {
      return (int)addr & 65535;
   }

   public static final long makeAddr(int ipAddr, int dstPort, int srcPort) {
      return (long)ipAddr << 32 | (dstPort << 16 | srcPort) & 4294967295L;
   }

   public static final long string2Addr(String str) {
      int index = 0;
      int nextIndex = str.indexOf(46);
      long val = 0;
      int shift = 56;

      while (shift > 32) {
         if (nextIndex != -1 && index < str.length()) {
            int i = Integer.parseInt(str.substring(index, nextIndex), 10);
            if (i >= 0 && i <= 255) {
               val |= (long)(i & 0xFF) << shift;
               index = nextIndex + 1;
               nextIndex = str.indexOf(46, index);
               shift -= 8;
               continue;
            }

            throw new NumberFormatException();
         }

         throw new NumberFormatException();
      }

      nextIndex = str.indexOf(58, index);
      if (nextIndex == -1) {
         throw new NumberFormatException();
      }

      int i = Integer.parseInt(str.substring(index, nextIndex), 10);
      val |= (long)(i & 0xFF) << shift;
      index = nextIndex + 1;
      nextIndex = str.indexOf(58, index);
      String sstr;
      if (nextIndex == -1) {
         sstr = str.substring(index);
      } else {
         sstr = str.substring(index, nextIndex);
      }

      i = Integer.parseInt(sstr, 10);
      val |= i << 16 & 4294967295L;
      if (nextIndex != -1) {
         sstr = str.substring(nextIndex + 1);
         i = Integer.parseInt(sstr, 10);
         val |= i;
      }

      return val;
   }

   public static final String addr2String(long addr) {
      StringBuffer strBuf = new StringBuffer(15);

      int shift;
      for (shift = 56; shift > 32; shift -= 8) {
         int s = (int)(addr >>> shift & 255);
         strBuf.append(Integer.toString(s, 10));
         strBuf.append('.');
      }

      strBuf.append(Long.toString(addr >>> shift & 255, 10));
      strBuf.append(':');
      int s = (int)(addr >>> 16 & 65535);
      strBuf.append(Integer.toString(s, 10));
      s = (int)(addr & 65535);
      if (s != 0) {
         strBuf.append(':');
         strBuf.append(Integer.toString(s, 10));
      }

      return strBuf.toString();
   }
}
