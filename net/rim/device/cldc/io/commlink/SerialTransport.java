package net.rim.device.cldc.io.commlink;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import net.rim.device.api.crypto.RandomSource;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.util.CRC16;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.system.Security;

class SerialTransport extends CommLinkTransport {
   protected Transport _transport;
   protected ProfileString _profile;
   protected Security _security;
   private byte[] _challenge;
   private byte _challengeType = -1;
   private DataInputStream _in;
   private DataOutputStream _out;
   private int _baud;
   private boolean _ackNak;
   private Object _ackFlag;
   private Object _ackLock = new Object();
   protected Object _readSemaphore;
   protected Object _writeSemaphore;
   protected IOException _exception;
   protected InputStream _is;
   protected OutputStream _os;
   protected long _timeout;
   protected boolean _dtr;
   protected boolean _ignoreDTR;
   protected Object _dtrSemaphore;
   protected byte[] _readBuffer;
   protected int _readBufferEnd;
   protected int _readBufferStart;
   protected byte[] _writeBuffer;
   protected int _writeBufferLen;
   protected int _writeBufferStart;
   protected static final int BUFFER_SIZE = 1024;

   protected SerialTransport(Transport transport, ProfileString profile) {
      this._security = Security.getInstance();
      this._transport = transport;
      this._profile = profile;
      this._dtr = false;
      this._readSemaphore = new Object();
      this._writeSemaphore = new Object();
      this._dtrSemaphore = new Object();
   }

   protected void init() {
      this._ignoreDTR = true;
      this._readBufferEnd = this._readBufferStart = 0;
      this._writeBufferLen = this._writeBufferStart = 0;
      this._timeout = 0;
      this._exception = null;
      this._readBuffer = new byte[1024];
      this._writeBuffer = new byte[1024];
      this._is = new SerialInputStream(this);
      this._os = new SerialOutputStream(this);
   }

   protected void fini() {
      this._is = null;
      this._os = null;
      synchronized (this._writeSemaphore) {
         this._writeBufferLen = 0;
         this._writeBuffer = null;
      }

      this._readBuffer = null;
      this._exception = null;
   }

   private boolean readAckNak() {
      synchronized (this._ackLock) {
         if (this._ackFlag == null) {
            label36:
            try {
               this._ackLock.wait(3000);
            } finally {
               break label36;
            }
         }

         if (this._ackFlag == null) {
            return false;
         }

         boolean result = this._ackNak;
         this._ackFlag = null;
         return result;
      }
   }

   private void setAckNak(boolean ack) {
      synchronized (this._ackLock) {
         this._ackNak = ack;
         this._ackFlag = this._ackLock;
         this._ackLock.notify();
      }
   }

   private boolean readAckNakInternal() {
      try {
         this.setTimeout(3000);

         while (true) {
            switch (this._in.readByte()) {
               case -99:
               case -65:
               case -22:
                  break;
               case 6:
                  return true;
               default:
                  return false;
            }
         }
      } catch (TimeoutException e) {
         return false;
      }
   }

   protected void sendHelloAck() {
      synchronized (this._out) {
         this._out.write(Constants.HEADER);
         this._out.write(-63);
         this._out.write(Constants.HEADER);
         this._out.write(-50);
         this._out.flush();
      }
   }

   @Override
   void sendReply(int reply) {
      synchronized (this._out) {
         this._profile.add(82, 111);
         this._out.write(Constants.HEADER);
         this._out.write(65);
         this._out.writeShort(reply);
         this._out.flush();
      }
   }

   @Override
   int sendDataPacket(int id, byte[] data, int off, int len, boolean more, boolean onInternalThread) {
      int retries = 0;
      int flags = 64 | (more ? 32 : 0);
      int crc = 65535;
      crc = CRC16.update(crc, Constants.HEADER);
      crc = CRC16.update(crc, flags);
      crc = CRC16.update(crc, id);
      crc = CRC16.update(crc, len >> 8);
      crc = CRC16.update(crc, len);
      if (len > 0) {
         crc = CRC16.update(crc, data, off, len);
      }

      crc = ~crc;
      crc = (crc & 0xFF00) >> 8 | (crc & 0xFF) << 8;

      while (this.getDtr()) {
         if (retries > 3) {
            return 32768;
         }

         this.flushReadBuffer();
         synchronized (this._out) {
            this._out.write(Constants.HEADER);
            this._out.writeByte(flags);
            this._out.writeByte(id);
            this._out.writeShort(len);
            if (len > 0) {
               this._out.write(data, off, len);
            }

            this._out.writeShort(crc);
            this._out.flush();
         }

         if (onInternalThread) {
            if (!this.readAckNakInternal()) {
               retries++;
               continue;
            }
         } else if (!this.readAckNak()) {
            retries++;
            continue;
         }

         return 0;
      }

      return 32770;
   }

   private boolean readMightBeAckNak(int expected) {
      int value = this._in.readByte();
      if (value == expected) {
         return true;
      }

      if (value == 6) {
         this._profile.add(65);
         this.setAckNak(true);
         return false;
      }

      if (value == 21) {
         this._profile.add(78);
         this.setAckNak(false);
      }

      return false;
   }

   private void sendAckNak(boolean ack) {
      synchronized (this._out) {
         this._out.write(ack ? 6 : 21);
         this._out.flush();
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   int readFrame(long timeout) {
      int retries = 0;

      while (true) {
         boolean var11 = false /* VF: Semaphore variable */;

         TimeoutException e;
         try {
            var11 = true;
            this.setTimeout(timeout);
            if (!this.readMightBeAckNak(-39)) {
               var11 = false;
            } else if (!this.readMightBeAckNak(-82)) {
               var11 = false;
            } else if (!this.readMightBeAckNak(-5)) {
               var11 = false;
            } else {
               this.setTimeout(3000);
               int rxType = this._in.readByte();
               if ((rxType & 67) == 64) {
                  this._transport._rxDestination = this._in.readByte() & 255;
                  this._transport._rxLength = this._in.readShort();
                  this._transport._rxBase = 0;
                  if (this._transport._rxLength > 0) {
                     this._in.readFully(this._transport._rxBuffer, 0, this._transport._rxLength);
                  }

                  int actualcrc = this._in.readByte() & 255;
                  int var23 = actualcrc | (this._in.readByte() & 255) << 8;
                  int crc = 65535;
                  crc = CRC16.update(crc, Constants.HEADER);
                  crc = CRC16.update(crc, rxType);
                  crc = CRC16.update(crc, this._transport._rxDestination);
                  crc = CRC16.update(crc, this._transport._rxLength >> 8 & 0xFF);
                  crc = CRC16.update(crc, this._transport._rxLength & 0xFF);
                  crc = CRC16.update(crc, this._transport._rxBuffer, 0, this._transport._rxLength);
                  if (crc == (var23 ^ 65535)) {
                     this.sendAckNak(true);
                     this._profile.add(68, 105);
                     return rxType;
                  }

                  this.sendAckNak(false);
                  var11 = false;
               } else {
                  if ((rxType & 67) == 65) {
                     this._in.readFully(this._transport._rxBuffer, 0, 2);
                     this._transport._rxLength = 2;
                     this._transport._rxBase = 0;
                     return rxType & 67;
                  }

                  if (rxType == -49) {
                     this.sendHelloAck();
                     this._profile.add(72);
                     var11 = false;
                  } else if (rxType == 15) {
                     this._profile.add(86);
                     int version = this._in.readInt();
                     if (version >= 65536) {
                        this._profile.add(70);
                        var11 = false;
                     }
                  } else {
                     this._profile.add(63);
                     var11 = false;
                  }
               }
            }
            continue;
         } catch (DTRException var12) {
            var11 = false;
            throw var12;
         } catch (TimeoutException var13) {
            e = var13;
            var11 = false;
         } finally {
            if (var11) {
               retries++;
               this.sendAckNak(false);
               if (retries <= 3) {
                  continue;
               }
            }
         }

         retries++;
         this.sendAckNak(false);
         timeout = 0;
         if (retries > 3) {
            throw e;
         }
      }
   }

   @Override
   boolean open() {
      this._baud = 9600;
      if (!this.lowLevelOpen(this._baud)) {
         return false;
      }

      if (!this.getDtr()) {
         return false;
      }

      this._in = (DataInputStream)(new Object(this._is));
      this._out = (DataOutputStream)(new Object(this._os));
      this._ackFlag = null;
      this.flushReadBuffer();
      this.sendHelloAck();
      return true;
   }

   @Override
   void close() {
      this.lowLevelClose();
      this._challenge = null;
      this._challengeType = -1;
      this._in = null;
      this._out = null;
   }

   @Override
   int sendChallenge(int challengeType) {
      DataBuffer txBuffer = (DataBuffer)(new Object(15, true));
      txBuffer.writeByte(0);
      txBuffer.writeByte(2);
      txBuffer.writeByte(0);
      txBuffer.writeByte(4);
      txBuffer.writeByte(RadioInfo.getNetworkType());
      txBuffer.writeInt(DeviceInfo.getDeviceId());
      txBuffer.writeInt(115200);
      txBuffer.writeShort(1024);
      this._challengeType = (byte)challengeType;
      if (!this._security.isPasswordEnabled()) {
         txBuffer.writeByte(0);
         this._challenge = null;
         this._challengeType = -1;
      } else {
         txBuffer.ensureCapacity(23 + Constants.PASSWORD_TYPE.length);
         txBuffer.writeByte(1);
         this._challenge = new byte[20];
         RandomSource.getBytes(this._challenge);
         txBuffer.write(this._challenge);
         txBuffer.writeByte(this._security.getMaxPasswordAttempts() - this._security.getPasswordFailureCount());
         txBuffer.writeByte(Constants.PASSWORD_TYPE.length);
         txBuffer.write(Constants.PASSWORD_TYPE);
      }

      return this._transport.sendDataPacket(0, txBuffer.getArray(), txBuffer.getArrayStart(), txBuffer.getLength(), false, true);
   }

   @Override
   public boolean needsResponse() {
      return true;
   }

   @Override
   boolean checkResponse(byte[] rxBuffer, int off, int len) {
      DataBuffer db = (DataBuffer)(new Object(rxBuffer, off, len, true));

      int newbaud;
      int newmtu;
      try {
         newbaud = db.readInt();
         newmtu = db.readShort();
      } finally {
         ;
      }

      if (newmtu < 128) {
         return false;
      }

      if (newmtu > 1024) {
         newmtu = 1024;
      }

      switch (newbaud) {
         case 9600:
         case 19200:
         case 38400:
         case 57600:
         case 115200:
            if (this._challenge != null) {
               int available = db.available();
               byte[] response = new byte[available];
               db.read(response);
               if (this._challengeType != 2 && this._challengeType != 18) {
                  if (!this._security.verifyStoredPasswordOnly(StringUtilities.cStr2String(response, 0, response.length))) {
                     this._challengeType = -1;
                     this._challenge = null;
                     return false;
                  }
               } else {
                  boolean responseResult = false;

                  label97:
                  try {
                     responseResult = this._security.verifyPasswordChallenge(this._challenge, response);
                  } finally {
                     break label97;
                  }

                  if (!responseResult) {
                     this._challengeType = -1;
                     this._challenge = null;
                     return false;
                  }
               }

               this._challengeType = -1;
               this._challenge = null;
            }

            this._transport.setMtu(newmtu);
            this._baud = newbaud;
            return true;
         default:
            return false;
      }
   }

   @Override
   void checkSpeed() {
      this.setBaud(this._baud);
   }

   @Override
   void die() {
      this.connected();
   }

   private boolean readHelper() {
      if (this._exception != null) {
         IOException e = this._exception;
         this._exception = null;
         throw e;
      }

      if (this._readBufferStart == this._readBufferEnd) {
         this._readBufferStart = 0;
         this._readBufferEnd = this.lowLevelRead(this._readBuffer);
         if (this._readBufferEnd < 0) {
            this._readBufferEnd = 0;
         }

         return this._readBufferEnd != 0;
      } else {
         return true;
      }
   }

   private void waitForDataAvailableNoTimeout() {
      while (!this.readHelper()) {
         try {
            this._readSemaphore.wait();
         } finally {
            continue;
         }
      }
   }

   private void waitForDataAvailable() {
      if (!this.readHelper()) {
         long time = System.currentTimeMillis();
         long timeoutTime = time + this._timeout;

         do {
            label37:
            try {
               this._readSemaphore.wait(timeoutTime - time);
            } finally {
               break label37;
            }

            if (this.readHelper()) {
               return;
            }

            time = System.currentTimeMillis();
         } while (time < timeoutTime);

         throw new TimeoutException();
      }
   }

   int read() {
      synchronized (this._readSemaphore) {
         if (this._timeout != 0) {
            this.waitForDataAvailable();
         } else {
            this.waitForDataAvailableNoTimeout();
         }

         return this._readBuffer[this._readBufferStart++] & 0xFF;
      }
   }

   int read(byte[] b, int off, int len) {
      synchronized (this._readSemaphore) {
         if (this._timeout != 0) {
            this.waitForDataAvailable();
         } else {
            this.waitForDataAvailableNoTimeout();
         }

         int actual = Math.min(this._readBufferEnd - this._readBufferStart, len);
         System.arraycopy(this._readBuffer, this._readBufferStart, b, off, actual);
         this._readBufferStart += actual;
         return actual;
      }
   }

   private boolean addToWriteBuffer(int b) {
      if (this._writeBufferLen < 1024) {
         this._writeBuffer[(this._writeBufferStart + this._writeBufferLen) % 1024] = (byte)b;
         this._writeBufferLen++;
         return true;
      } else {
         return false;
      }
   }

   private int addToWriteBuffer(byte[] b, int off, int len) {
      if (this._writeBufferLen < 1024) {
         int actual = Math.min(1024 - this._writeBufferLen, len);
         len = actual;
         int start = (this._writeBufferStart + this._writeBufferLen) % 1024;
         if (start + actual > 1024) {
            int tempsize = 1024 - start;
            System.arraycopy(b, off, this._writeBuffer, start, tempsize);
            off += tempsize;
            len -= tempsize;
            start = 0;
         }

         if (len > 0) {
            System.arraycopy(b, off, this._writeBuffer, start, len);
         }

         this._writeBufferLen += actual;
         return actual;
      } else {
         return 0;
      }
   }

   protected boolean sendWriteBuffer() {
      boolean gotsome = false;

      while (this._writeBufferLen > 0) {
         int desired;
         if (this._writeBufferStart + this._writeBufferLen > 1024) {
            desired = 1024 - this._writeBufferStart;
         } else {
            desired = this._writeBufferLen;
         }

         int len = this.lowLevelWrite(this._writeBuffer, this._writeBufferStart, desired);
         if (len <= 0) {
            len = 0;
         } else {
            gotsome = true;
         }

         this._writeBufferStart = (this._writeBufferStart + len) % 1024;
         this._writeBufferLen -= len;
         if (len != desired) {
            break;
         }
      }

      return gotsome;
   }

   void write(int b) {
      synchronized (this._writeSemaphore) {
         while (!this.addToWriteBuffer(b)) {
            if (!this.sendWriteBuffer()) {
               try {
                  this._writeSemaphore.wait();
               } finally {
                  continue;
               }
            }
         }
      }
   }

   void write(byte[] b, int off, int len) {
      synchronized (this._writeSemaphore) {
         while (true) {
            int actual = this.addToWriteBuffer(b, off, len);
            off += actual;
            len -= actual;
            if (len == 0) {
               return;
            }

            if (!this.sendWriteBuffer()) {
               try {
                  this._writeSemaphore.wait();
               } finally {
                  continue;
               }
            }
         }
      }
   }

   void flush() {
      synchronized (this._writeSemaphore) {
         this.sendWriteBuffer();
      }
   }

   protected void setIOException(String message) {
      synchronized (this._readSemaphore) {
         if (this._exception == null) {
            this._exception = (IOException)(new Object(message));
            this._readSemaphore.notify();
         }
      }
   }

   protected boolean lowLevelOpen(int _1) {
      throw null;
   }

   protected void lowLevelClose() {
      throw null;
   }

   protected int lowLevelRead(byte[] _1) {
      throw null;
   }

   protected int lowLevelWrite(byte[] _1, int _2, int _3) {
      throw null;
   }

   protected void flushReadBuffer() {
      throw null;
   }

   protected void setBaud(int _1) {
      throw null;
   }

   protected void setDsr(boolean _1) {
      throw null;
   }

   protected boolean getDtr() {
      throw null;
   }

   public void connected() {
      throw null;
   }

   protected void setTimeout(long timeout) {
      this._timeout = timeout;
   }

   protected boolean waitForDtr(boolean desiredState, long timeout) {
      synchronized (this._dtrSemaphore) {
         if (this._dtr == desiredState) {
            return true;
         }

         label45:
         try {
            this._dtrSemaphore.wait(timeout);
         } finally {
            break label45;
         }

         if (this._exception instanceof DTRException) {
            this._exception = null;
         }

         return this._dtr == desiredState;
      }
   }
}
