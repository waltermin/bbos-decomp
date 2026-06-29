package com.fourthpass.wapstack.wdp;

import net.rim.vm.Array;

public final class WDPPacket {
   private int _dataLength;
   private byte[] _data;
   private String _source;

   public WDPPacket() {
      this(1500);
   }

   public WDPPacket(int size) {
      this._data = new byte[size];
      this._dataLength = this._data.length;
   }

   public WDPPacket(byte[] data, int size) {
      this._data = new byte[size];
      this._dataLength = this._data.length;
      System.arraycopy(data, 0, this._data, 0, this._dataLength);
   }

   public WDPPacket(byte[] data) {
      this._dataLength = data.length;
      this._data = data;
   }

   public final void setPacketData(byte[] data, int length) {
      this.setPacketData(data, 0, length);
   }

   public final void setPacketData(byte[] data, int offset, int length) {
      if (length > this._data.length) {
         Array.resize(this._data, length);
      }

      System.arraycopy(data, offset, this._data, 0, length);
      this._dataLength = length;
   }

   public final byte[] getPacketData() {
      return this._data;
   }

   public final int getDataLength() {
      return this._dataLength;
   }

   public final void setSource(String source) {
      this._source = source;
   }

   public final String getSource() {
      return this._source;
   }
}
