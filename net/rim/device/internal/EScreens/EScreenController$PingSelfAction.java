package net.rim.device.internal.EScreens;

import java.util.Random;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.util.Arrays;
import net.rim.device.internal.system.ICMPPacketHeader;
import net.rim.device.internal.system.ICMPPacketListener;
import net.rim.device.internal.system.RadioInternal;

final class EScreenController$PingSelfAction implements ICMPPacketListener, Runnable {
   private int _state;
   private byte[] _pingBuffer;
   private int _curPacketID;
   private Random _rand = (Random)(new Object());
   private long _sendTimestamp;
   private int _timerID;
   private Application _app;
   private int _responseTimeout;
   private int _apnId;
   private static final int SEND_TIMEOUT;
   private static final int BASE_PAYLOAD_SIZE;
   private static final int STATE_IDLE;
   private static final int STATE_INIT;
   private static final int STATE_SEND_WAIT;
   private static final int STATE_RESPONSE_WAIT;

   public EScreenController$PingSelfAction(Application app) {
      this._app = app;
      this._timerID = -1;
   }

   public final void go(int apnId, int responseTimeout, int payloadSize) {
      synchronized (this) {
         if (this._state != 0) {
            return;
         }

         this._state = 1;
      }

      this._pingBuffer = new byte[payloadSize + 2];
      Arrays.fill(this._pingBuffer, (byte)115);
      int ri = this._rand.nextInt();
      this._pingBuffer[0] = (byte)(ri & 0xFF);
      this._pingBuffer[1] = (byte)(ri >> 8 & 0xFF);
      this._apnId = apnId;
      this._responseTimeout = responseTimeout;
      this._app.addRadioListener(this);
      this.sendPing(apnId, 5000);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void sendPing(int apnId, int timeout) {
      ICMPPacketHeader head = (ICMPPacketHeader)(new Object());
      head.setAccessPointNumber(apnId);
      head.setDestinationAddress(RadioInfo.getIPAddress(apnId));
      head.setType(8);
      head.setCode(0);
      head.setChecksum(0);
      synchronized (this) {
         boolean var9 = false /* VF: Semaphore variable */;

         label34:
         try {
            var9 = true;
            this._curPacketID = RadioInternal.sendPacket(head, this._pingBuffer);
            var9 = false;
         } finally {
            if (var9) {
               this._curPacketID = -1;
               break label34;
            }
         }
      }

      if (this._curPacketID >= 0) {
         this._state = 2;
         this.startTimer(timeout);
      } else {
         this.done(false, "Unable to send packet");
      }
   }

   private final void done(boolean success, String msg) {
      this._app.removeRadioListener(this);
      this.stopTimer();
      if (success) {
         Dialog.inform(msg);
      } else {
         Dialog.alert(msg);
      }

      this._state = 0;
   }

   public final synchronized void startTimer(int millis) {
      if (this._timerID == -1) {
         this._timerID = this._app.invokeLater(this, millis, false);
         if (this._timerID == -1) {
            this.done(false, "Can't start timer");
         }
      }
   }

   public final synchronized void stopTimer() {
      if (this._timerID != -1) {
         this._app.cancelInvokeLater(this._timerID);
         this._timerID = -1;
      }
   }

   @Override
   public final synchronized void run() {
      this._timerID = -1;
      String msg;
      switch (this._state) {
         case 1:
            return;
         case 2:
         default:
            msg = "Packet not sent within 5ms";
            break;
         case 3:
            msg = "Timeout waiting for response";
      }

      this.done(false, msg);
   }

   @Override
   public final synchronized void packetSent(int packetID, int networkID) {
      if (this._state == 2 && packetID == this._curPacketID) {
         this._state = 3;
         this._sendTimestamp = System.currentTimeMillis();
         this.stopTimer();
         this.startTimer(this._responseTimeout);
      }
   }

   @Override
   public final void packetNotSent(int packetID, int networkID) {
      if (this._state == 2 && packetID == this._curPacketID) {
         this.done(false, "Packet not sent");
      }
   }

   @Override
   public final void packetStatus(int packetID, int status) {
   }

   @Override
   public final synchronized void packetReceived(ICMPPacketHeader header, byte[] data) {
      if (this._state == 3) {
         if (header.getType() == 8) {
            byte[] temp = header.getSourceAddress();
            header.setSourceAddress(header.getDestinationAddress());
            header.setDestinationAddress(temp);
            header.setType(0);

            try {
               RadioInternal.sendPacket(header, data);
               return;
            } finally {
               this.done(false, "Unable to respond to echo request");
               return;
            }
         }

         if (header.getType() == 0) {
            long time = System.currentTimeMillis() - this._sendTimestamp;
            if (!Arrays.equals(header.getSourceAddress(), RadioInfo.getIPAddress(this._apnId))) {
               return;
            }

            String msg;
            if (Arrays.equals(data, this._pingBuffer)) {
               msg = ((StringBuffer)(new Object("Response received in "))).append(time).append("ms").toString();
            } else {
               msg = "ICMP packet received, but payload bad.";
            }

            this.done(true, msg);
         }
      }
   }
}
