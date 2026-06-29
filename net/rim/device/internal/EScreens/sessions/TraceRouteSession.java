package net.rim.device.internal.EScreens.sessions;

import java.util.Vector;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.util.Arrays;
import net.rim.device.cldc.io.dns.DNSResolverIPv4;
import net.rim.device.internal.EScreens.EScreenSession;
import net.rim.device.internal.EScreens.EScreenSession$InvalidInputException;
import net.rim.device.internal.EScreens.EScreenSessionManager;
import net.rim.device.internal.system.ICMPPacketHeader;
import net.rim.device.internal.system.ICMPPacketListener;
import net.rim.device.internal.system.RadioInternal;
import net.rim.device.internal.ui.component.IPEditField;

public final class TraceRouteSession extends EScreenSession implements ICMPPacketListener {
   private int _numHops;
   private int _hopID;
   private int _currentPacketID;
   private int _consecutiveTimeoutCount;
   private byte[] _payload;
   private int[] _dnsIds;
   private EditField _maxHopsField = (EditField)(new Object("MaxHops: ", "30", Integer.MAX_VALUE, 16777216));
   private static final int MAX_RESULTS = 30;
   private static final int SEND_NOW = -1;
   private static final int HOP_TIMEOUT = 5;
   public static final int COUNTER_TX = 0;
   public static final int COUNTER_TX_ERR = 1;
   public static final int COUNTER_RX = 2;
   public static final int COUNTER_RX_ERR = 3;
   public static final int COUNTER_RX_TIMEOUT = 4;
   public static final int MAX_COUNTERS = 5;
   private static final String[] COUNTER_HEADERS = new String[]{"Tx: ", "Tx err: ", "Rx: ", "Rx err: ", "Rx timeout: "};

   @Override
   protected final void setNumResults(int numResults) {
      super.setNumResults(numResults);
      this._dnsIds = new int[numResults];
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
      boolean var3 = false /* VF: Semaphore variable */;

      try {
         var3 = true;
         this._numHops = Integer.parseInt(this._maxHopsField.getText());
         var3 = false;
      } finally {
         if (var3) {
            Dialog.alert("You must enter MaxHops");
            return false;
         }
      }

      if (this._numHops == 0) {
         Dialog.alert("MaxHops can't be zero");
         return false;
      } else {
         return super.verifyParameters();
      }
   }

   @Override
   public final void start() {
      this.setNumResults(Math.min(this._numHops, 30));
      this._consecutiveTimeoutCount = 0;
      this._payload = new byte[4];
      Arrays.fill(this._payload, (byte)-38);
      Application.getApplication().addRadioListener(this);
      this.sendPacket();
   }

   @Override
   public final void stop() {
      super.stop();
      Application.getApplication().removeRadioListener(this);
   }

   @Override
   public final String getSessionType() {
      return "TraceRoute";
   }

   @Override
   public final void addParameterFields(Vector v) {
      v.addElement(this._maxHopsField);
   }

   @Override
   public final synchronized void handleTimeout() {
      this.setResult(this._hopID, "Timeout", 4);
      if (++this._consecutiveTimeoutCount >= 5) {
         this.stop();
      }

      this._hopID++;
      this.sendPacket();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void sendPacket() {
      try {
         if (this._hopID >= this._numHops) {
            this.stop();
         }

         ICMPPacketHeader head = (ICMPPacketHeader)this.makeHeader(1);
         head.setType(8);
         head.setCode(0);
         head.setChecksum(0);
         head.setTTL((byte)(this._hopID + 1));
         boolean var7 = false /* VF: Semaphore variable */;

         label45:
         try {
            var7 = true;
            this._currentPacketID = RadioInternal.sendPacket(head, this._payload);
            var7 = false;
         } finally {
            if (var7) {
               this._currentPacketID = -1;
               break label45;
            }
         }

         if (this._currentPacketID >= 0) {
            this.setTimeoutCounter(5);
         } else {
            this.incrementCounter(1);
            this.stop();
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
   public final void packetSent(int packetId, int networkId) {
      if (this.getState() == 1 && this._currentPacketID == packetId) {
         this.incrementCounter(0);
      }
   }

   @Override
   public final void packetNotSent(int packetId, int networkId) {
      if (this.getState() == 1 && this._currentPacketID == packetId) {
         this.incrementCounter(1);
         this.stop();
      }
   }

   @Override
   public final void packetStatus(int packetId, int status) {
   }

   @Override
   public final synchronized void packetReceived(ICMPPacketHeader header, byte[] data) {
      StringBuffer strBuf = (StringBuffer)(new Object());
      IPEditField.appendIpAddr(strBuf, header.getSourceAddress());
      this._consecutiveTimeoutCount = 0;
      switch (header.getType()) {
         case 0:
            this.setResult(this._hopID, strBuf.toString(), 2);
            this._dnsIds[this._hopID] = DNSResolverIPv4.instance().getHostnameByAddress(header.getSourceAddress(), this, header.getAccessPointNumber());
            this.stop();
            return;
         case 11:
            this.setResult(this._hopID, strBuf.toString(), 2);
            this._dnsIds[this._hopID % super._numResults] = DNSResolverIPv4.instance()
               .getHostnameByAddress(header.getSourceAddress(), this, header.getAccessPointNumber());
            this._hopID++;
            this.sendPacket();
            return;
         default:
            this.setResult(this._hopID, "Invalid ICMP type", 3);
            this.stop();
      }
   }

   @Override
   protected final void resetStats() {
      super.resetStats();
      this._hopID = 0;
      this._payload = null;
   }

   @Override
   public final void DNSEvent(int id, int type, Vector results) {
      if (type == 1) {
         super.DNSEvent(id, type, results);
      } else {
         if (type == 11) {
            String hostName = (String)results.elementAt(0);

            for (int i = 0; i < super._numResults; i++) {
               if (this._dnsIds[i] == id) {
                  this.setResult(i, hostName, -1);
                  return;
               }
            }
         }
      }
   }
}
