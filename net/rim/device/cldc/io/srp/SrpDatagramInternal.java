package net.rim.device.cldc.io.srp;

import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.io.DatagramBase;

final class SrpDatagramInternal extends DatagramBase {
   private byte[] _payload;
   private int _offset;
   private int _length;

   public SrpDatagramInternal() {
   }

   public SrpDatagramInternal(byte[] buffer, int offset, int length) {
   }

   public SrpDatagramInternal(byte[] buffer, int offset, int length, String address) {
   }

   public SrpDatagramInternal(byte[] buffer, int offset, int length, DatagramAddressBase addressBase) {
   }

   @Override
   public final void copy(DatagramBase dgram) {
      super.copy(dgram);
      if (dgram instanceof SrpDatagramInternal) {
         this.setPayload(
            ((SrpDatagramInternal)dgram).getPayload(), ((SrpDatagramInternal)dgram).getPayloadOffset(), ((SrpDatagramInternal)dgram).getPayloadLength()
         );
      }
   }

   @Override
   public final void simpleReset() {
      this._payload = null;
      this._offset = this._length = 0;
      super.simpleReset();
   }

   final void setPayload(byte[] payload, int offset, int length) {
      this._payload = payload;
      this._offset = offset;
      this._length = length;
   }

   final byte[] getPayload() {
      return this._payload;
   }

   final int getPayloadOffset() {
      return this._offset;
   }

   final int getPayloadLength() {
      return this._length;
   }
}
