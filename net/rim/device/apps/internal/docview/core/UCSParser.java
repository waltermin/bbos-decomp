package net.rim.device.apps.internal.docview.core;

import java.io.EOFException;

public final class UCSParser {
   private int _crtCommandCode;
   private int _crtCommandSize;
   private byte _byRecordCodeSize;
   private byte _byRecordSizeSize;
   private int _totalLength;
   private DocViewInputStream _dataStream;
   private Object _dataStreamSyncObject;
   private final StringBuffer _stringBuffer = new StringBuffer(128);

   public UCSParser() {
      this.initialize();
   }

   public final int getCurrentCommandCode() {
      return this._crtCommandCode;
   }

   public final int getCurrentCommandSize() {
      return this._crtCommandSize;
   }

   public final synchronized boolean set(DocViewInputStream inputData) throws EOFException {
      this.initialize();
      int nSignatureLength = ArznConstants._kabyUCSSignature.length;
      byte[] header = new byte[nSignatureLength + 2];
      int readCount = inputData.read(header, 0, nSignatureLength + 2);
      if (readCount != nSignatureLength + 2) {
         throw new EOFException("Incomplete UCS header");
      }

      for (int i = 0; i < nSignatureLength; i++) {
         if (header[i] != ArznConstants._kabyUCSSignature[i]) {
            return false;
         }
      }

      this._dataStream = inputData;
      this._dataStreamSyncObject = inputData.getInputStreamSyncObject();
      if (this._dataStream.supportsTotalByteCount()) {
         this._totalLength = this._dataStream.available();
      } else {
         this._totalLength = 0;
      }

      this._byRecordCodeSize = header[nSignatureLength];
      this._byRecordSizeSize = header[nSignatureLength + 1];
      return true;
   }

   public final int getCurrentParsingPercentage() {
      return this._totalLength > 0 ? (this._totalLength - this._dataStream.available()) * 100 / this._totalLength : 0;
   }

   public final void readNextCommand() {
      synchronized (this._dataStreamSyncObject) {
         this._crtCommandCode = this.readData(false, this._byRecordCodeSize);
         this._crtCommandSize = this.readData(true, this._byRecordSizeSize);
      }
   }

   final boolean isCommandDataRemaining() {
      if (this._totalLength > 0) {
         try {
            return this._dataStream.available() > this._byRecordCodeSize + this._byRecordSizeSize;
         } finally {
            return true;
         }
      } else {
         return true;
      }
   }

   public final void readByteArray(byte[] array) throws EOFException {
      int result = this._dataStream.read(array);
      if (result != array.length) {
         throw new EOFException();
      }
   }

   public final StringBuffer readString(boolean unicode, int nStringSize) {
      if (unicode) {
         int chars = nStringSize + 1 >> 1;
         synchronized (this._stringBuffer) {
            this._stringBuffer.setLength(0);
            synchronized (this._dataStreamSyncObject) {
               for (int i = 0; i < chars - 1; i++) {
                  this._stringBuffer.append(this._dataStream.readChar());
               }

               if (nStringSize % 2 == 0) {
                  this._stringBuffer.append(this._dataStream.readChar());
               } else {
                  this._stringBuffer.append((char)this._dataStream.readUnsignedByte());
               }
            }
         }
      } else {
         synchronized (this._stringBuffer) {
            this._stringBuffer.setLength(0);
            synchronized (this._dataStreamSyncObject) {
               for (int i = 0; i < nStringSize; i++) {
                  this._stringBuffer.append((char)this._dataStream.readUnsignedByte());
               }
            }
         }
      }

      return this._stringBuffer;
   }

   public final int readUnsignedByte() {
      return this._dataStream.readUnsignedByte();
   }

   public final int readUnsignedShort() {
      return this._dataStream.readUnsignedShort();
   }

   public final int readUnsignedInt() {
      return this._dataStream.readInt();
   }

   public final byte readByte() {
      return this._dataStream.readByte();
   }

   public final short readShort() {
      return this._dataStream.readShort();
   }

   public final int readInt() {
      return this._dataStream.readInt();
   }

   public final void jumpCursor(int nJumpSize) throws EOFException {
      if (nJumpSize > 0) {
         if (this._dataStream.skip(nJumpSize) != nJumpSize) {
            throw new EOFException();
         }
      }
   }

   public final byte getCommandHeaderSize() {
      return (byte)(this._byRecordCodeSize + this._byRecordSizeSize);
   }

   public final int readRawCommandCode(int nCommandCode) {
      switch (this._byRecordCodeSize) {
         case 0:
            return nCommandCode & 268435455;
         case 1:
         default:
            return nCommandCode & 127;
         case 2:
            return nCommandCode & 4095;
      }
   }

   public final byte getAtomicIdentifer(int commandCode) {
      switch (this.readRawCommandCode(commandCode)) {
         case 8:
            return 0;
         case 54:
            return 1;
         default:
            return -1;
      }
   }

   public final boolean isContainer(int nCommandCode) {
      boolean bReturn = false;
      switch (this._byRecordCodeSize) {
         case 0:
         case 3:
            break;
         case 1:
         default:
            if ((nCommandCode & 128) != 0) {
               return true;
            }
            break;
         case 2:
            if ((nCommandCode & 32768) != 0) {
               return true;
            }
            break;
         case 4:
            if ((nCommandCode & -2147483648) != 0) {
               bReturn = true;
            }
      }

      return bReturn;
   }

   public final boolean isIncomplete(int nCommandCode) {
      boolean bReturn = false;
      switch (this._byRecordCodeSize) {
         case 0:
         case 3:
            break;
         case 1:
         default:
            return true;
         case 2:
            if ((nCommandCode & 16384) != 0) {
               return true;
            }
            break;
         case 4:
            if ((nCommandCode & 1073741824) != 0) {
               bReturn = true;
            }
      }

      return bReturn;
   }

   final synchronized void waitForNotify() {
      try {
         this.wait();
      } finally {
         return;
      }
   }

   final synchronized void resumeParsing() {
      this.notifyAll();
   }

   final int getMoreAvailableBytes() {
      return this._dataStream.available();
   }

   private final int readData(boolean unsignedFlag, byte size) {
      if (size == 1) {
         return unsignedFlag ? this._dataStream.readUnsignedByte() : this._dataStream.readByte();
      } else if (size == 2) {
         return unsignedFlag ? this._dataStream.readUnsignedShort() : this._dataStream.readShort();
      } else {
         return this._dataStream.readInt();
      }
   }

   private final void initialize() {
      this._byRecordCodeSize = this._byRecordSizeSize = 0;
      this._crtCommandCode = this._crtCommandSize = 0;
      this._stringBuffer.setLength(0);
      this._dataStream = null;
   }
}
