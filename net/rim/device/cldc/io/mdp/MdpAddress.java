package net.rim.device.cldc.io.mdp;

import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.io.UdpAddress;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.cldc.io.udp.UdpInternalAddress;

public final class MdpAddress extends DatagramAddressBase {
   private DatagramAddressBase _native;
   private int _destPort;
   private int _srcPort;

   public MdpAddress() {
      this.init(null, -1, -1);
   }

   public MdpAddress(DatagramAddressBase addressBase, int destPort, int srcPort) {
      this.init(addressBase, destPort, srcPort);
   }

   public MdpAddress(String nativeAddress, int destPort, int srcPort, int wtAddress) {
      this.init(this.makeNativeAddressBase(nativeAddress, wtAddress), destPort, srcPort);
   }

   public MdpAddress(DatagramAddressBase addressBase) {
      if (!(addressBase instanceof MdpAddress)) {
         this.setAddress(addressBase.getAddress());
      } else {
         MdpAddress addr = (MdpAddress)addressBase;
         this.init(this.makeNativeAddressBase(addr._native), addr._destPort, addr._srcPort);
      }
   }

   public MdpAddress(String address) {
      this.setAddress(address);
   }

   private final void init(DatagramAddressBase addressBase, int destPort, int srcPort) {
      this._native = addressBase;
      this._destPort = destPort;
      this._srcPort = srcPort;
      super._key = (addressBase != null ? addressBase.getKey() : 0) ^ (destPort << 16 | srcPort & 65535);
   }

   public final int getDestPort() {
      return this._destPort;
   }

   public final int getSrcPort() {
      return this._srcPort;
   }

   @Override
   public final void setAddress(String address) {
      int scan = address.length();
      int delim = address.lastIndexOf(59) + 1;
      if (delim > 0 && delim <= scan) {
         int srcPort;
         if (delim == scan) {
            srcPort = -1;
         } else {
            int ret = DatagramAddressBase.parseInt(address, delim, scan, 10);
            if (ret < 0 || ret > 65535) {
               throw new IllegalArgumentException("Invalid SRC_PORT");
            }

            srcPort = ret;
         }

         scan = delim - 1;
         delim = address.lastIndexOf(58, scan) + 1;
         if (delim > 0 && delim <= scan) {
            int destPort;
            if (delim == scan) {
               destPort = -1;
            } else {
               int ret = DatagramAddressBase.parseInt(address, delim, scan, 10);
               if (ret < 0 || ret > 65535) {
                  throw new IllegalArgumentException("Invalid DEST_PORT");
               }

               destPort = ret;
            }

            scan = delim - 1;
            delim = scan;
            if (delim < 0) {
               throw new IllegalArgumentException("Bad NATIVE_ADDRESS");
            }

            DatagramAddressBase nativeAddr;
            if (delim == 0) {
               nativeAddr = null;
            } else {
               nativeAddr = this.makeNativeAddressBase(address.substring(0, delim), 0);
            }

            super._address = address;
            this.init(nativeAddr, destPort, srcPort);
         } else {
            throw new IllegalArgumentException("Bad DEST_PORT");
         }
      } else {
         throw new IllegalArgumentException("Bad SRC_PORT");
      }
   }

   @Override
   public final String getAddress() {
      if (super._address == null) {
         super._address = makeAddress(false, this._native, this._destPort, this._srcPort);
      }

      return super._address;
   }

   @Override
   public final String getSubAddress() {
      String ret = null;
      if (this._native != null) {
         ret = this._native.getAddress();
      }

      return ret;
   }

   @Override
   public final DatagramAddressBase getSubAddressBase() {
      return this._native;
   }

   @Override
   public final void swap() {
      if (this._native != null) {
         this._native.swap();
      }

      int port = this._destPort;
      this._destPort = this._srcPort;
      this._srcPort = port;
      super._address = null;
   }

   @Override
   public final boolean equals(Object addressBase) {
      if (addressBase == this) {
         return true;
      }

      if (!(addressBase instanceof MdpAddress)) {
         return false;
      }

      MdpAddress address = (MdpAddress)addressBase;
      return (this._native == null || address._native == null || this._native.equals(address._native))
         && (this._destPort == -1 || address._destPort == -1 || this._destPort == address._destPort)
         && (this._srcPort == -1 || address._srcPort == -1 || this._srcPort == address._srcPort);
   }

   @Override
   public final int hashCode() {
      int hash = 7;
      hash = 31 * hash + this._native.hashCode();
      hash = 31 * hash + this._destPort;
      return 31 * hash + this._srcPort;
   }

   public static final String makeAddress(boolean open, DatagramAddressBase addressBase, int destPort, int srcPort) {
      return makeAddress(open, addressBase.getAddress(), destPort, srcPort);
   }

   public static final String makeAddress(boolean open, String nativeAddress, int destPort, int srcPort) {
      StringBuffer buf = new StringBuffer(128);
      appendAddress(buf, open, nativeAddress, destPort, srcPort);
      return buf.toString();
   }

   public static final String makeAddress(boolean open, String nativeAddress, int nativeOffset, int nativeLength, int destPort, int srcPort) {
      return makeAddress(open, nativeAddress != null ? nativeAddress.substring(nativeOffset, nativeOffset + nativeLength) : null, destPort, srcPort);
   }

   public static final void appendAddress(StringBuffer buf, boolean open, String nativeAddress, int nativeOffset, int nativeLength, int destPort, int srcPort) {
      appendAddress(buf, open, nativeAddress != null ? nativeAddress.substring(nativeOffset, nativeOffset + nativeLength) : null, destPort, srcPort);
   }

   public static final void appendAddress(StringBuffer buf, boolean open, String nativeAddress, int destPort, int srcPort) {
      if (open) {
         buf.append("mdp:");
      }

      if (nativeAddress != null) {
         buf.append(nativeAddress);
      }

      buf.append(':');
      if (destPort != -1) {
         buf.append(destPort);
      }

      buf.append(';');
      if (srcPort != -1) {
         buf.append(srcPort);
      }
   }

   private final DatagramAddressBase makeNativeAddressBase(String address, int wtAddress) {
      if (address != null && address.length() != 0) {
         switch (RadioInfo.getNetworkType()) {
            case 2:
               return new DatagramAddressBase(address);
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            default:
               UdpInternalAddress addr = new UdpInternalAddress(address);
               addr.setGpakHostAddress(wtAddress);
               return addr;
         }
      } else {
         return null;
      }
   }

   private final DatagramAddressBase makeNativeAddressBase(DatagramAddressBase addressBase) {
      if (addressBase != null) {
         if (addressBase instanceof UdpInternalAddress) {
            return new UdpInternalAddress(addressBase);
         }

         if (addressBase instanceof UdpAddress) {
            return new UdpAddress(addressBase);
         }

         addressBase = new DatagramAddressBase(addressBase);
      }

      return addressBase;
   }
}
