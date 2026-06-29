package net.rim.device.cldc.io.commlink;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.USBPortListener;
import net.rim.device.internal.system.USBPortInternal;

final class USBTransport extends CommLinkTransport implements USBPortListener {
   private Transport _transport;
   private ProfileString _profile;
   private boolean _failure;
   private int _channel;
   private USBPortInternal _port;
   private int _pendingReadLength;
   private Object _receiveLock;
   private Object _sendLock;
   private byte[] _sendBuffer;
   private static final String CHANNEL_NAME = "RIM Desktop";

   USBTransport(Transport transport, ProfileString profile) {
      this._transport = transport;
      this._profile = profile;
      this.register();
      Application app = Application.getApplication();
      app.addIOPortListener(this);
   }

   private final void waitForSend() throws DTRException {
      if (this._failure) {
         throw new DTRException();
      }

      label28:
      try {
         this._sendLock.wait();
      } finally {
         break label28;
      }

      if (this._failure) {
         throw new DTRException();
      }
   }

   private final void register() {
      this._channel = USBPortInternal.registerChannel("RIM Desktop", 1024, 1024);
   }

   private final void sendHelloAck() {
      synchronized (this._sendLock) {
         this._port.write(-50);
         this.waitForSend();
      }
   }

   @Override
   final void sendReply(int reply) {
      synchronized (this._sendLock) {
         this._profile.add(82, 111);
         this._sendBuffer[0] = 65;
         this._sendBuffer[1] = (byte)(reply >> 8);
         this._sendBuffer[2] = (byte)reply;
         this._port.write(this._sendBuffer, 0, 3);
         this.waitForSend();
      }
   }

   @Override
   final int sendDataPacket(int id, byte[] data, int off, int len, boolean more, boolean onInternalThread) {
      synchronized (this._sendLock) {
         this._sendBuffer[0] = (byte)(64 | (more ? 32 : 0));
         this._sendBuffer[1] = (byte)id;
         if (len > 0) {
            System.arraycopy(data, off, this._sendBuffer, 2, len);
         }

         this._port.write(this._sendBuffer, 0, len + 2);
         this.waitForSend();
         return 0;
      }
   }

   @Override
   final int readFrame(long timeout) {
      byte[] packet = this._transport._rxBuffer;

      while (true) {
         int length;
         synchronized (this._receiveLock) {
            if (this._pendingReadLength == -1) {
               if (this._failure) {
                  throw new DTRException();
               }

               label77:
               try {
                  this._receiveLock.wait();
               } finally {
                  break label77;
               }

               if (this._failure) {
                  throw new DTRException();
               }
            }

            length = this._port.read(packet, 0, this._pendingReadLength);
            this._pendingReadLength = -1;
         }

         if ((packet[0] & 67) == 64) {
            if (length >= 2) {
               this._transport._rxLength = length - 2;
               this._transport._rxDestination = packet[1] & 255;
               this._transport._rxBase = 2;
               this._profile.add(68, 105);
               return packet[0];
            }

            this._profile.add(68, 33);
         } else if ((packet[0] & 67) == 65) {
            if (length == 3) {
               this._transport._rxBase = 1;
               this._profile.add(82, 108);
               return packet[0];
            }

            this._profile.add(82, 33);
         } else if (packet[0] == -49) {
            this.sendHelloAck();
            this._profile.add(72);
         } else {
            this._profile.add(63);
         }
      }
   }

   @Override
   final boolean open() {
      this._sendBuffer = new byte[1024];
      this._sendLock = this._sendBuffer;
      return true;
   }

   private final void closePort() {
      if (this._sendLock != null) {
         synchronized (this._sendLock) {
            if (this._port != null) {
               this._port.close();
               this._port = null;
            }
         }
      }
   }

   @Override
   final void close() {
      this.closePort();
      this._sendBuffer = null;
      this._sendLock = null;
      this._receiveLock = null;
   }

   @Override
   final int sendChallenge(int use_password) {
      byte[] challenge = new byte[]{0, 2, 0, 4};
      return this._transport.sendDataPacket(0, challenge, 0, challenge.length, false, true);
   }

   @Override
   final boolean needsResponse() {
      return false;
   }

   @Override
   final boolean checkResponse(byte[] rxBuffer, int off, int len) {
      this._transport.setMtu(1022);
      return true;
   }

   @Override
   final void die() {
      this.disconnected();
      this.closePort();
   }

   @Override
   public final int getChannel() {
      return this._channel;
   }

   @Override
   public final void dataSent() {
      synchronized (this._sendLock) {
         this._sendLock.notify();
      }
   }

   @Override
   public final void dataNotSent() {
      this._profile.add(75, 33);
      synchronized (this._sendLock) {
         this._failure = true;
         this._sendLock.notify();
      }
   }

   @Override
   public final void dataReceived(int length) {
      try {
         synchronized (this._receiveLock) {
            this._pendingReadLength = length;
            this._receiveLock.notify();
         }
      } finally {
         return;
      }
   }

   @Override
   public final void connectionRequested() {
      try {
         this._failure = false;
         this._receiveLock = new Object();
         this._pendingReadLength = -1;
         this._port = (USBPortInternal)(new Object(this._channel));
      } finally {
         return;
      }
   }

   @Override
   public final void connected() {
      if (!this._transport.startThread(this)) {
         this._port.close();
         this._port = null;
      }
   }

   @Override
   public final void disconnected() {
      try {
         synchronized (this._sendLock) {
            this._failure = true;
            this._sendLock.notify();
            this.closePort();
         }

         synchronized (this._receiveLock) {
            this._failure = true;
            this._receiveLock.notify();
         }
      } finally {
         return;
      }
   }

   @Override
   final void standbyMode() {
   }

   @Override
   final void checkSpeed() {
   }

   @Override
   public final void receiveError(int error) {
   }

   @Override
   public final void patternReceived(byte[] pattern) {
   }
}
