package net.rim.device.cldc.io.lstp;

import net.rim.device.api.io.DatagramBase;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.system.Security;

class SerialMuxerThread extends MuxerThread {
   protected byte _challengeType;
   protected byte[] _challenge;
   protected int _baudrate;
   protected int _fragmentSize;
   protected static final byte SC_MAJOR = 2;
   protected static final byte SC_MINOR = 0;
   protected static final byte OV_MAJOR = 3;
   protected static final byte OV_MINOR = 0;
   public static final int VERSION = 131075;
   protected static final int MAX_BAUD_RATE = 115200;
   protected static final int CHALLENGE_DEVICE = 0;
   protected static final int CHALLENGE_DESKTOP = 1;
   protected static final int CHALLENGE_DEVICE_FORCED = 16;
   protected static final int CHALLENGE_DESKTOP_FORCED = 17;
   protected static final int NO_PASSWORD = 0;
   protected static final int SHA_1 = 1;
   protected static final int CHALLENGE_SIZE = 20;
   protected static final int PROMPT = 1433625970;

   protected SerialMuxerThread(NativeLayer nativeLayer) {
      super(nativeLayer);
   }

   @Override
   protected void expectLogin() {
      DatagramBase dgram = (DatagramBase)super._conn.newDatagram();
      super._conn.receive(dgram);
      this._challengeType = dgram.readByte();
   }

   @Override
   protected void expectChallengeResponse() {
      DatagramBase dgram = (DatagramBase)super._conn.newDatagram();
      super._conn.receive(dgram);
      this._baudrate = dgram.readInt();
      this._fragmentSize = dgram.readShort() & '\uffff';
      if (this._challenge != null) {
         byte[] response = new byte[20];
         dgram.read(response);
         if (!Security.getInstance().verifyStoredPasswordOnly(StringUtilities.cStr2String(response, 0, response.length))) {
            EventLogger.logEvent(-754053862978797267L, 1297637734, 3);
            throw new Object();
         }
      }
   }

   @Override
   protected void configureLink() {
      super._nativeLayer.configure(this._baudrate, this._fragmentSize);
   }
}
