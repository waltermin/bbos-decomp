package net.rim.device.cldc.io.lstp;

import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.cldc.io.daemon.ProtocolDaemon;

class NativeLayer implements Runnable {
   protected Transport _transport;
   protected LstpUtil _lstpUtil;
   protected Object _receiveLock = new Object();
   protected int _dataAvailable;
   protected static final byte HELLO = 58;
   protected static final byte HELLO_ACK = 57;
   protected static final byte DATA = 64;
   protected static final byte DATA_MORE = 96;
   protected static final byte ACK = 6;
   protected static final byte NACK = 21;
   protected static final byte VERSION = 15;
   protected static final int RX_AVAILABLE = 1;
   protected static final int RX_NONE = 0;
   protected static final int RX_ERROR = -1;
   protected static final int RX_TIMED_OUT = -2;

   protected NativeLayer(Transport transport) {
      this._transport = transport;
      this._lstpUtil = LstpUtil.getInstance();
   }

   protected int getFragmentSize() {
      throw null;
   }

   protected MuxerThread createMuxerThread() {
      throw null;
   }

   protected int encodeDataFragment(byte _1, byte[] _2, int _3, int _4, DataBuffer _5) {
      throw null;
   }

   protected void sendPacket(byte[] _1, int _2, int _3) {
      throw null;
   }

   protected void cancelPacket() {
      throw null;
   }

   protected void configure(int baudrate, int fragmentSize) {
   }

   protected void nativeOpen() {
      throw null;
   }

   protected void nativeClose() {
      throw null;
   }

   protected void shutdownReceiveThread() {
      throw null;
   }

   protected void close(boolean redirect) {
      if (this._transport.activateNativeLayer(this)) {
         this.nativeClose();
         this.shutdownReceiveThread();
         this.cancelPacket();
         this._lstpUtil.setLinkState(false, redirect);
         EventLogger.logEvent(-754053862978797267L, 1280205934, 4);
         this._transport.deactivateNativeLayer(this);
      }
   }

   protected void startNativeLayer() {
      try {
         if (this._transport.activateNativeLayer(this)) {
            EventLogger.logEvent(-754053862978797267L, 1280210288, 4);
            ProtocolDaemon protocolDaemon = ProtocolDaemon.getInstance();
            this.nativeOpen();
            protocolDaemon.startThread(this.createMuxerThread());
            protocolDaemon.submitRunnable(this);
         }
      } finally {
         return;
      }
   }

   @Override
   public void run() {
      throw null;
   }
}
