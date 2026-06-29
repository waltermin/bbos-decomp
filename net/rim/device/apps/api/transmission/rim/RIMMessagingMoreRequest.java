package net.rim.device.apps.api.transmission.rim;

import net.rim.device.api.util.DataBuffer;

public final class RIMMessagingMoreRequest extends RIMMessagingTransmission {
   private int _reference;
   private int _part;
   private int _offset;
   private int _length;
   private byte[] _contentTypeRequested;
   private byte[] _objectDescriptor;
   private String _nnePassword;
   private DataBuffer _buffer;

   public final void setLength(int anInt) {
      this._length = anInt;
   }

   public final void setOffset(int anInt) {
      this._offset = anInt;
   }

   public final void setPartIdentifier(int anInt) {
      this._part = anInt;
   }

   public final void setReferenceIdentifier(int anInt) {
      this._reference = anInt;
   }

   public final void setContentType(byte[] contentTypeRequested) {
      this._contentTypeRequested = contentTypeRequested;
   }

   public final void setObjectDescriptor(byte[] descriptor) {
      this._objectDescriptor = descriptor;
   }

   public final void setNNEPassword(String password) {
      this._nnePassword = password;
   }

   @Override
   public final DataBuffer write() {
      if (this._buffer == null) {
         this._buffer = (DataBuffer)(new Object());
         this._buffer.writeByte(4);
         CMIMEParameters parameters = new CMIMEParameters(this._buffer, 4, 1);
         parameters.addCMIMEInteger((byte)48, this._reference);
         parameters.addCMIMEInteger((byte)48, this._part);
         parameters.addCMIMEIntegerInCompactWay((byte)48, this._offset);
         parameters.addCMIMEIntegerInCompactWay((byte)48, this._length);
         if (this._nnePassword != null && this._nnePassword.length() > 0) {
            parameters.add((byte)113, this._nnePassword.getBytes());
         }

         if (this._contentTypeRequested != null || this._objectDescriptor != null) {
            this._buffer.writeByte(112);
            DataBuffer nestedBuffer = (DataBuffer)(new Object());
            CMIMEParameters nestedParameters = new CMIMEParameters(nestedBuffer, 4, 1);
            if (this._contentTypeRequested != null) {
               nestedParameters.add((byte)1, this._contentTypeRequested);
            }

            if (this._objectDescriptor != null) {
               nestedParameters.add((byte)2, this._objectDescriptor);
            }

            this._buffer.writeByteArray(nestedBuffer.toArray());
         }
      }

      return this._buffer;
   }
}
