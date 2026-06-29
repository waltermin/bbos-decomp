package net.rim.device.api.crypto.asn1;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import net.rim.device.api.io.SharedInputStream;

final class ASN1Field {
   private SharedInputStream _input;
   private int _tag;
   private int _valueLength;
   private int _startPosition;
   private int _dataPosition;
   private int _endPosition;
   private int _maxPosition;
   private boolean _isExplicit;
   private static final int RECURSIVE_LIMIT = 32;

   ASN1Field(ASN1Field field, SharedInputStream input) {
      this(field, input, -1);
   }

   ASN1Field(ASN1Field field, SharedInputStream input, int lengthLimit) {
      this(field, input, lengthLimit, -1);
   }

   ASN1Field(ASN1Field field, SharedInputStream input, int lengthLimit, int explicitTag) {
      this(input, field.getEndPosition(), lengthLimit, explicitTag);
   }

   ASN1Field(SharedInputStream input, int offset, int lengthLimit) {
      this(input, offset, lengthLimit, -1);
   }

   ASN1Field(SharedInputStream input, int offset, int lengthLimit, int explicitTag) throws EOFException {
      if (input == null) {
         throw new IllegalArgumentException();
      }

      this._input = input;
      this._startPosition = offset;
      this._input.setCurrentPosition(offset);
      if (lengthLimit >= 0) {
         this._maxPosition = this._startPosition + lengthLimit;
      } else {
         this._maxPosition = -1;
      }

      this._tag = this._input.read();
      if (this._tag == -1) {
         throw new EOFException();
      }

      if (explicitTag != -1) {
         if (this._tag != explicitTag) {
            return;
         }

         this._isExplicit = true;
         this.readDefiniteLength(true);
         this._tag = this._input.read();
         if (this._tag == -1) {
            throw new EOFException();
         }
      }

      this._valueLength = this.readDefiniteLength(!this._isExplicit);
      this._dataPosition = this._input.getCurrentPosition();
   }

   ASN1Field(int startPosition, int length) {
      this._startPosition = startPosition;
      this._dataPosition = startPosition;
      this._endPosition = startPosition + length;
      this._maxPosition = -1;
      this._valueLength = length;
   }

   public final int getTag() {
      return this._tag;
   }

   public final int getValueLength() {
      if (this._valueLength == -1) {
         this._input.setCurrentPosition(this._startPosition + 1);
         this._valueLength = this.readIndefiniteLength(true, 0) - 2;
      }

      return this._valueLength;
   }

   public final int getFieldLength() {
      return this.getEndPosition() - this._startPosition;
   }

   public final int getValueEndPosition() {
      return this._dataPosition + this.getValueLength();
   }

   public final int getStartPosition() {
      return this._startPosition;
   }

   public final int getEndPosition() {
      this.getValueLength();
      return this._endPosition;
   }

   public final InputStream getInputStream() {
      SharedInputStream input = this._input.readInputStream();
      if (this._valueLength != -1) {
         input.setLength(this._valueLength);
      }

      return input;
   }

   public final SharedInputStream getUnderlyingStream() {
      return this._input;
   }

   public final ASN1InputStream getASN1InputStream() {
      return new ASN1InputStream(this._input);
   }

   public final byte[] getValueAsByteArray() {
      return this.getByteArray(this.getValueLength());
   }

   public final byte[] getFieldAsByteArray() {
      int fieldLength = this.getFieldLength();
      this._input.setCurrentPosition(this._startPosition);
      byte[] array = this.getByteArray(fieldLength);
      this._input.setCurrentPosition(this._dataPosition);
      return array;
   }

   private final byte[] getByteArray(int length) throws IOException {
      byte[] array = new byte[length];
      int lengthRead = this._input.read(array);
      if (lengthRead != length) {
         throw new IOException();
      } else {
         return array;
      }
   }

   public final boolean isValidExplicit() {
      return this._isExplicit;
   }

   private final int readDefiniteLength(boolean setEnd) throws EOFException, ASN1EncodingException {
      int length = this._input.read();
      if (length == -1) {
         throw new EOFException();
      }

      if (length == 128) {
         this._endPosition = -1;
         return -1;
      }

      if ((length & 128) != 0) {
         int numLengthOctets = length & 127;
         if (numLengthOctets > 4) {
            throw new ASN1EncodingException();
         }

         length = 0;

         for (int i = 0; i < numLengthOctets; i++) {
            int next = this._input.read();
            if (next == -1) {
               throw new EOFException();
            }

            length = length << 8 | next & 0xFF;
         }
      }

      if (setEnd) {
         this.setEndPosition(this._input.getCurrentPosition() + length);
      }

      return length;
   }

   private final int readIndefiniteLength(boolean setEnd, int depth) throws ASN1EncodingException {
      if (depth == 32) {
         throw new ASN1EncodingException();
      }

      this._input.skip(1);
      int fieldStartPosition = this._input.getCurrentPosition();
      boolean dataCutOff = false;

      while (true) {
         int nextByte = this._input.read();
         if (nextByte == -1) {
            dataCutOff = true;
            break;
         }

         if (nextByte == 0) {
            break;
         }

         int fieldLength;
         if (this._input.peek() == 128) {
            fieldLength = this.readIndefiniteLength(false, depth + 1);
         } else {
            fieldLength = this.readDefiniteLength(false);
         }

         this._input.skip(fieldLength);
      }

      if (!dataCutOff && this._input.read() != 0) {
         throw new ASN1EncodingException();
      }

      int fieldEndPosition = this._input.getCurrentPosition();
      this._input.setCurrentPosition(fieldStartPosition);
      if (setEnd) {
         this.setEndPosition(fieldEndPosition);
      }

      return fieldEndPosition - fieldStartPosition;
   }

   private final void setEndPosition(int endPosition) throws ASN1EncodingException {
      this._endPosition = endPosition;
      if (this._maxPosition > 0 && this._endPosition > this._maxPosition) {
         throw new ASN1EncodingException();
      }
   }
}
