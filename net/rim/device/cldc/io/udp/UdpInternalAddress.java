package net.rim.device.cldc.io.udp;

import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.io.UdpAddress;
import net.rim.device.api.util.StringUtilities;

public final class UdpInternalAddress extends UdpAddress {
   private byte[] _data;
   private int _gpakHostAddress = 0;
   private static String PROMISCUOUS_MODE = ";promiscuousmode";
   private static String NO_TUNNEL = ";openTunnel=false";
   private static String ALLOW_NO_CONTEXT_RETRY = ";retrynocontext=true";
   private static String INTERFACE_WIFI = ";interface=wifi";

   protected UdpInternalAddress(byte[] ipAddress, int destPort, int srcPort, String apn, int type, byte[] data) {
      super(ipAddress, destPort, srcPort, apn, type);
      this._data = data;
   }

   public UdpInternalAddress(DatagramAddressBase addressBase) {
      super(addressBase);
      if (addressBase instanceof UdpInternalAddress) {
         this._gpakHostAddress = ((UdpInternalAddress)addressBase)._gpakHostAddress;
      }
   }

   public UdpInternalAddress(String address) {
      super(address);
   }

   protected final void setType(int type) {
      super._type = type;
      super._address = null;
   }

   protected final boolean equals(DatagramAddressBase addressBase, boolean promiscuousMode) {
      if (!(addressBase instanceof UdpInternalAddress)) {
         return false;
      }

      UdpInternalAddress address = (UdpInternalAddress)addressBase;
      if ((super._ipAddress == -1 || address._ipAddress == -1 || super._ipAddress == address._ipAddress)
         && (super._destPort == -1 || address._destPort == -1 || super._destPort == address._destPort)
         && (super._srcPort == -1 || address._srcPort == -1 || super._srcPort == address._srcPort)
         && (super._apn == null || address._apn == null || super._apn.equalsIgnoreCase(address._apn))) {
         int type;
         if (super._type != -1 && (super._type & 6) != 0) {
            type = GpakUtil.decode(address._data);
         } else {
            type = address._type;
         }

         if ((super._type == -1 || type == -1 || (super._type & type) != 0) && (super._destPort != -1 || promiscuousMode)) {
            return true;
         }
      }

      return false;
   }

   public static final String makeAddress(
      boolean open, byte[] ipAddress, int destPort, int srcPort, String apn, int apnOffset, int apnLength, int type, boolean promiscuousMode
   ) {
      return makeAddress(open, ipAddress, destPort, srcPort, apn, apnOffset, apnLength, type, promiscuousMode, promiscuousMode);
   }

   public static final String makeAddress(
      boolean open,
      byte[] ipAddress,
      int destPort,
      int srcPort,
      String apn,
      int apnOffset,
      int apnLength,
      int type,
      boolean promiscuousMode,
      boolean noTunnelRequired
   ) {
      String address = UdpAddress.makeAddress(
         open, ipAddress, destPort, srcPort, apn != null ? apn.substring(apnOffset, apnOffset + apnLength) : null, type, null, null
      );
      if (promiscuousMode) {
         address = address.concat(PROMISCUOUS_MODE);
      }

      if (!noTunnelRequired) {
         address = address.concat(NO_TUNNEL);
      }

      return address;
   }

   static final boolean isPromiscuousMode(String address) {
      return address != null && address.indexOf(PROMISCUOUS_MODE) >= 0;
   }

   static final boolean noTunnelRequired(String address) {
      return address == null || address.length() == 0 || address.indexOf(NO_TUNNEL) >= 0;
   }

   static final boolean retriesOnNoContextRequested(String address) {
      return address == null || address.length() == 0 || address.indexOf(ALLOW_NO_CONTEXT_RETRY) >= 0;
   }

   static final boolean wifiRequested(String address) {
      return address != null && StringUtilities.toLowerCase(address, 1701707776).indexOf(INTERFACE_WIFI) >= 0;
   }

   @Override
   public final int hashCode() {
      int hash = super.hashCode();
      if ((super._type & 2) == 2 && this._gpakHostAddress != 0) {
         hash = 31 * hash + this._gpakHostAddress;
      }

      return hash;
   }

   public final void setGpakHostAddress(int address) {
      this._gpakHostAddress = address;
   }

   public final int getGpakHostAddress() {
      return this._gpakHostAddress;
   }
}
