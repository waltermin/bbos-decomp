package net.rim.device.internal.EScreens.sessions;

import java.util.Vector;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.internal.EScreens.EScreenSession;
import net.rim.device.internal.EScreens.EScreenSession$InvalidInputException;
import net.rim.device.internal.EScreens.EScreenSessionManager;
import net.rim.device.internal.system.ICMPPacketHeader;
import net.rim.device.internal.system.ICMPPacketListener;
import net.rim.device.internal.system.RadioInternal;

public final class PingSession extends EScreenSession implements ICMPPacketListener {
   private int _numPingsToSend;
   private int _pingTimeout;
   private int _pingSize;
   private DataBuffer _payload;
   private int _curPacketId;
   private int _curStartTime;
   private int _curTotalTime;
   private int _pingsSent;
   private short _pingId;
   private EditField _numPingField = (EditField)(new Object("Pings to send: ", "5", Integer.MAX_VALUE, 16777216));
   private EditField _pingSizeField = (EditField)(new Object("Ping size/start ping size: ", "2", Integer.MAX_VALUE, 16777216));
   private EditField _timeoutField = (EditField)(new Object("Delay between pings (in secs): ", "3", Integer.MAX_VALUE, 16777216));
   private ObjectChoiceField _incrementalPingsField;
   private static final int PING_HEADER_SIZE;
   public static final int MIN_PING_SIZE;
   public static final int MAX_PING_SIZE;
   public static final int MAX_RESULT_STRS;
   public static final int COUNTER_TX;
   public static final int COUNTER_TX_ERROR;
   public static final int COUNTER_RX;
   public static final int COUNTER_RX_ERROR;
   public static final int COUNTER_RX_TIMEOUT;
   public static final int COUNTER_MIN_TIME;
   public static final int COUNTER_MAX_TIME;
   public static final int COUNTER_AVG_TIME;
   public static final int COUNTER_PING_SIZE;
   public static final int MAX_COUNTERS;
   private static final String[] COUNTER_HEADERS = new String[]{
      "Tx Count: ",
      "Tx ErrorCount: ",
      "Rx Count: ",
      "Rx Error Count: ",
      "Rx Timeout Count: ",
      "Min Ping Time: ",
      "Max Ping Time: ",
      "Avg Ping Time: ",
      "Current Ping Size: "
   };
   private static final String[] INCREMENTAL_PINGS_FIELD_STRINGS = new String[]{"Disabled", "Enabled"};

   public PingSession() {
      this._incrementalPingsField = (ObjectChoiceField)(new Object("Incremental Ping Session: ", INCREMENTAL_PINGS_FIELD_STRINGS));
   }

   @Override
   public final void init(EScreenSessionManager manager, int si) {
      super.init(manager, si);
      this.setCounterHeaders(COUNTER_HEADERS);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final boolean verifyParameters() {
      boolean var13 = false /* VF: Semaphore variable */;

      try {
         var13 = true;
         this._numPingsToSend = Integer.parseInt(this._numPingField.getText());
         var13 = false;
      } finally {
         if (var13) {
            Dialog.alert("pings to send must be a number");
            return false;
         }
      }

      boolean var9 = false /* VF: Semaphore variable */;

      try {
         var9 = true;
         this._pingSize = Integer.parseInt(this._pingSizeField.getText());
         var9 = false;
      } finally {
         if (var9) {
            Dialog.alert("ping size must be a number");
            return false;
         }
      }

      boolean var5 = false /* VF: Semaphore variable */;

      try {
         var5 = true;
         this._pingTimeout = Integer.parseInt(this._timeoutField.getText());
         var5 = false;
      } finally {
         if (var5) {
            Dialog.alert("ping delay must be a number");
            return false;
         }
      }

      if (this._pingSize < 1) {
         Dialog.alert("PING is too small");
         return false;
      }

      if (this._pingSize > 1484) {
         Dialog.alert("PING is too big");
         return false;
      }

      if (this._incrementalPingsField.getSelectedIndex() == 1 && this._pingSize + this._numPingsToSend - 1 > 1484) {
         Dialog.alert("Combination of ping size and number of pings to big for incremental session.");
      }

      return super.verifyParameters();
   }

   @Override
   public final void start() {
      this.setNumResults(Math.min(this._numPingsToSend, 10));
      this.setCounter(8, this._pingSize - (this._incrementalPingsField.getSelectedIndex() == 1 ? 1 : 0));
      byte[] buffer = new byte[this._pingSize + 4];
      Arrays.fill(buffer, (byte)112);
      this._payload = (DataBuffer)(new Object(buffer, 0, buffer.length, true));
      this._pingId = (short)(EScreenSession._rand.nextInt() & 65535);
      this._payload.writeShort(this._pingId);
      Application.getApplication().addRadioListener(this);
      this.sendPing();
   }

   @Override
   public final void stop() {
      super.stop();
      Application.getApplication().removeRadioListener(this);
   }

   @Override
   public final String getSessionType() {
      return "Ping";
   }

   @Override
   public final void addParameterFields(Vector v) {
      v.addElement(this._numPingField);
      v.addElement(this._pingSizeField);
      v.addElement(this._timeoutField);
      v.addElement(this._incrementalPingsField);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void sendPing() {
      try {
         while (true) {
            ICMPPacketHeader head = (ICMPPacketHeader)this.makeHeader(1);
            head.setType(8);
            head.setCode(0);
            head.setChecksum(0);
            this._payload.setPosition(2);
            this._payload.writeShort(++this._pingsSent);
            if (this._pingsSent > 0 && this._incrementalPingsField.getSelectedIndex() == 1) {
               this._pingSize++;
               this._payload.setLength(this._pingSize + 4);
               this._payload.setPosition(this._pingSize + 4 - 1);
               this._payload.writeByte(112);
            }

            this._curStartTime = (int)System.currentTimeMillis();
            boolean var7 = false /* VF: Semaphore variable */;

            label60:
            try {
               var7 = true;
               this._curPacketId = RadioInternal.sendPacket(head, this._payload.getArray(), 0, this._payload.getArrayLength());
               var7 = false;
            } finally {
               if (var7) {
                  this._curPacketId = -1;
                  break label60;
               }
            }

            if (this._curPacketId >= 0) {
               this.setTimeoutCounter(this._pingTimeout);
            } else {
               this.setResult(this._pingsSent, "Unable to send", 1);
               if (!this.isDone()) {
                  continue;
               }

               this.stop();
            }

            if (this._incrementalPingsField.getSelectedIndex() == 1) {
               this.incrementCounter(8);
            }

            return;
         }
      } catch (EScreenSession$InvalidInputException e) {
         this.stop();
         this.resetStats();
         synchronized (Application.getEventLock()) {
            Status.show("Invalid Header Type!", 3000);
         }
      }
   }

   @Override
   public final synchronized void handleTimeout() {
      this.setResult(this._pingsSent, "PING timed out", 4);
      if (!this.isDone()) {
         this.sendPing();
      } else {
         this.stop();
      }
   }

   @Override
   public final void packetSent(int packetId, int networkId) {
      if (this.getState() == 1 && this._curPacketId == packetId) {
         this.incrementCounter(0);
      }
   }

   @Override
   public final void packetNotSent(int packetId, int networkId) {
      if (this.getState() == 1 && this._curPacketId == packetId) {
         this.incrementCounter(1);
         if (!this.isDone()) {
            this.setTimeoutCounter(-1);
            return;
         }

         this.stop();
      }
   }

   @Override
   public final void packetStatus(int packetId, int status) {
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final synchronized void packetReceived(ICMPPacketHeader header, byte[] data) {
      if (this.getState() == 1) {
         int time = (int)System.currentTimeMillis() - this._curStartTime;
         boolean var7 = false /* VF: Semaphore variable */;

         try {
            var7 = true;
            DataBuffer e = new Object(data, 0, data.length, true);
            if (!this.isFromDestination(header.getSourceAddress()) || ((DataBuffer)e).readShort() != this._pingId) {
               return;
            }

            int temp = ((DataBuffer)e).readShort();
            if (temp < this._pingsSent) {
               return;
            }

            if (temp != this._pingsSent) {
               this.setResult(this._pingsSent, "RX w/ bad seqNum", 3);
               return;
            }

            if (!Arrays.equals(data, 4, this._payload.getArray(), 4, this._payload.getLength() - 4)) {
               this.setResult(this._pingsSent, "RX w/ bad payload", 3);
               return;
            }

            this.setResult(this._pingsSent, ((StringBuffer)(new Object("Success! "))).append(time).toString(), 2);
            this.checkTimeCounters(time);
            var7 = false;
         } finally {
            if (var7) {
               this.setResult(this._pingsSent, "Exception on RX", 3);
               return;
            }
         }

         if (!this.isDone()) {
            this.sendPing();
         } else {
            this.stop();
         }
      }
   }

   @Override
   protected final void resetStats() {
      super.resetStats();
      this._curPacketId = 0;
      this._curStartTime = 0;
      this._curTotalTime = 0;
      this._pingsSent = -1;
      this._pingId = -1;
      this.setCounter(5, Integer.MAX_VALUE);
      this.setCounter(6, Integer.MIN_VALUE);
      this.setCounter(8, this._pingSize);
   }

   private final boolean isDone() {
      return this._pingsSent + 1 >= this._numPingsToSend;
   }

   private final void checkTimeCounters(int time) {
      if (this.getCounter(5) > time) {
         this.setCounter(5, time);
      }

      if (this.getCounter(6) < time) {
         this.setCounter(6, time);
      }

      this._curTotalTime += time;
      this.setCounter(7, this._curTotalTime / this.getCounter(2));
   }
}
