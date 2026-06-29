package com.fourthpass.wapstack.wsp;

import com.fourthpass.wapstack.util.InetAddress;

public final class WSPAddress {
   private byte[] _wspAddress;
   private InetAddress _netAddress;

   public WSPAddress(byte[] address) {
      this._wspAddress = new byte[address.length + 1];
      this._wspAddress[0] = (byte)address.length;
      System.arraycopy(address, 0, this._wspAddress, 1, address.length);
      this._netAddress = new InetAddress(address);
   }

   public WSPAddress(byte bearerType, int portNumber, byte[] address) {
      this._wspAddress = new byte[address.length + 4];
      this._wspAddress[0] = (byte)(address.length + 128 + 64);
      this._wspAddress[1] = bearerType;
      this._wspAddress[2] = (byte)(portNumber >> 8 & 0xFF);
      this._wspAddress[3] = (byte)(portNumber & 0xFF);
      System.arraycopy(address, 0, this._wspAddress, 4, address.length);
      this._netAddress = new InetAddress(address);
   }

   public WSPAddress(byte bearerType, byte[] address) {
      this._wspAddress = new byte[address.length + 2];
      this._wspAddress[0] = (byte)(address.length + 128);
      this._wspAddress[1] = bearerType;
      System.arraycopy(address, 0, this._wspAddress, 2, address.length);
      this._netAddress = new InetAddress(address);
   }

   public WSPAddress(byte bearerType, int portNumber, InetAddress netAdd) {
      this(bearerType, portNumber, netAdd.getAddress());
      this._netAddress = netAdd;
   }

   public WSPAddress(byte bearerType, InetAddress netAdd) {
      this(bearerType, netAdd.getAddress());
      this._netAddress = netAdd;
   }

   public WSPAddress(InetAddress netAdd) {
      this(netAdd.getAddress());
   }

   public final boolean isBearerTypeIncluded() {
      return (this._wspAddress[0] & 128) != 0;
   }

   public final boolean isPortNumberIncluded() {
      return (this._wspAddress[0] & 64) != 0;
   }

   public final int getPortNumber() {
      int port = 0;
      int msbIndex = 2;
      int lsbIndex = 3;
      if (this.isPortNumberIncluded()) {
         if (!this.isBearerTypeIncluded()) {
            msbIndex = 1;
            lsbIndex = 2;
         }

         port = (this._wspAddress[msbIndex] << 8 & 0xFF00) + (this._wspAddress[lsbIndex] & 255);
      }

      return port;
   }

   public final byte[] getWSPAddress() {
      return this._wspAddress;
   }

   public final InetAddress getInetAddress() {
      return this._netAddress;
   }
}
