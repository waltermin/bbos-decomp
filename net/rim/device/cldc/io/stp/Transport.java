package net.rim.device.cldc.io.stp;

import java.util.Random;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;
import net.rim.device.api.io.ConnEvent;
import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.io.DatagramBase;
import net.rim.device.api.io.DatagramStatusListener;
import net.rim.device.api.io.DatagramTransportBase;
import net.rim.device.api.io.IOProperties;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RealtimeClockListener;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.cldc.io.daemon.ProtocolDaemon;
import net.rim.device.cldc.io.lstp.LstpListener;
import net.rim.device.cldc.io.lstp.LstpUtil;

public final class Transport extends DatagramTransportBase implements LstpListener, DatagramStatusListener, RealtimeClockListener, ConnEvent {
   private int _nextDgramId;
   private int _maxPacketLength;
   private Object _lock = new Object();
   private Datagram _txDatagram;
   private int _txDatagramId;
   private int _txStatus;
   private DatagramBase _txPacket;
   private IntHashtable _inboundDatagrams;
   private Vector _inboundTimes;
   private int _timer;
   private static final long ID = 1679319515711487829L;
   private static final String STP_STR = "net.rim.stp";
   private static final int SEND_RESPONSE_TIMEOUT = 120000;
   public static final int ERROR_NOT_ROUTABLE = 8577;
   public static final int ERROR_FAILED = 8579;
   public static final int TX_TIMEOUT = 1415083119;
   public static final int TX_NOT_ROUTABLE = 1415081586;
   public static final int RX_STATUS = 1381004148;
   private static final int CONFIRMATION_TMO = 3;

   public Transport() {
      super.GUID = 1679319515711487829L;
      super.STR = "net.rim.stp";
   }

   @Override
   public final void init() {
      super.init((DatagramConnection)Connector.open("lstp:GME Data"));
      EventLogger.register(1679319515711487829L, "net.rim.stp", 2);
      this._nextDgramId = (((Random)(new Object())).nextInt() & 1073741823) + 256;
      this._maxPacketLength = StpUtil.getMaximumLength(super._subConnection.getMaximumLength());
      this._txPacket = (DatagramBase)super._subConnection.newDatagram(null, 0, 0, null);
      this._inboundDatagrams = (IntHashtable)(new Object(8));
      this._inboundTimes = (Vector)(new Object(8));
      LstpUtil.getInstance().addListener(this);
      ProtocolDaemon.getInstance().addRealtimeClockListener(this);
      EventLogger.logEvent(1679319515711487829L, 1229878386, 0);
   }

   @Override
   public final int getMaximumLength() {
      return this._maxPacketLength;
   }

   @Override
   public final int getNominalLength() {
      return StpUtil.getMaximumLength(super._subConnection.getNominalLength());
   }

   @Override
   protected final synchronized int getNextDatagramId(DatagramBase dgram) {
      if (++this._nextDgramId > Integer.MAX_VALUE) {
         this._nextDgramId = 256;
      }

      return this._nextDgramId;
   }

   @Override
   public final DatagramAddressBase newDatagramAddressBase(String address, boolean swap) {
      return null;
   }

   @Override
   public final DatagramAddressBase newDatagramAddressBase(DatagramAddressBase addressBase, boolean swap) {
      return null;
   }

   @Override
   public final void send(Datagram datagram) {
      this.send(datagram, null, null, null, 0);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void send(Datagram datagram, DatagramAddressBase addressBase, IOProperties properties, DatagramStatusListener listener, int dgramId) {
      EventLogger.logEvent(1679319515711487829L, 1415082868, 4);
      if (datagram.getLength() > this._maxPacketLength) {
         EventLogger.logEvent(1679319515711487829L, 1413834351, 2);
         this.xmitDgslEvent(listener, dgramId, 12674, null);
         throw new Object();
      }

      int tag = dgramId != 0 ? dgramId : this.getNextDatagramId(null);
      StpUtil.encodeDataCommand(tag, datagram.getData(), datagram.getOffset(), datagram.getLength(), this._txPacket);
      synchronized (this._lock) {
         this._txDatagram = datagram;
         this._txDatagramId = tag;
         this._txStatus = -1;
      }

      this._txPacket.setDatagramStatusListener(this);
      boolean var19 = false /* VF: Semaphore variable */;

      try {
         var19 = true;
         EventLogger.logEvent(1679319515711487829L, 1415082850, 5);
         this.subSend(this._txPacket, listener, dgramId, datagram);
         var19 = false;
      } finally {
         if (var19) {
            this._txPacket.reset();
         }
      }

      this._txPacket.reset();
      int status;
      synchronized (this._lock) {
         if (this._txStatus == -1) {
            label110:
            try {
               this._lock.wait(120000);
            } finally {
               break label110;
            }
         }

         status = this._txDatagram != null ? this._txStatus : 129;
         this._txDatagram = null;
         this._txDatagramId = 0;
      }

      switch (status) {
         case -1:
            EventLogger.logEvent(1679319515711487829L, 1415083119, 2);
            this.xmitDgslEvent(listener, dgramId, 12933, null);
            throw new Object();
         case 6:
            EventLogger.logEvent(1679319515711487829L, 1415082867, 4);
            this.xmitDgslEvent(listener, dgramId, 0, null);
            return;
         case 129:
            EventLogger.logEvent(1679319515711487829L, 1415078753, 3);
            this.xmitDgslEvent(listener, dgramId, 129, null);
            throw new Object();
         case 8337:
            EventLogger.logEvent(1679319515711487829L, 1413837414, 2);
            this.xmitDgslEvent(listener, dgramId, 8337, null);
            throw new Object();
         case 8577:
            EventLogger.logEvent(1679319515711487829L, 1415081586, 5);
            this.xmitDgslEvent(listener, dgramId, 1, null);
            throw new Object();
         default:
            EventLogger.logEvent(1679319515711487829L, 1413834337, 2);
            this.xmitDgslEvent(listener, dgramId, 8579, null);
            throw new Object();
      }
   }

   @Override
   public final void cancel(Datagram datagram) {
      synchronized (this._lock) {
         if (datagram == this._txDatagram) {
            super._subConnection.cancel(this._txPacket);
            this._txDatagram = null;
            this._lock.notify();
         }
      }
   }

   @Override
   protected final void processReceivedDatagram(Datagram datagram) {
      EventLogger.logEvent(1679319515711487829L, 1381528436, 4);
      DatagramAddressBase addressBase = null;
      if (datagram instanceof Object) {
         addressBase = ((DatagramBase)datagram).getAddressBase();
      }

      if (addressBase == null) {
         addressBase = super._subConnection.newDatagramAddressBase(datagram.getAddress(), false);
      }

      StpDatagram dgram = new StpDatagram();
      int ret = StpUtil.decode(datagram.getData(), datagram.getOffset(), datagram.getLength(), dgram);
      switch (dgram.getCommand()) {
         case -14:
            if (ret != 0) {
               EventLogger.logEvent(1679319515711487829L, 1380279919, 3);
               return;
            }
         case 2:
            break;
         case 4:
            if (ret == 0) {
               this.processStatus(dgram.getDatagramId(), dgram.getResult());
               return;
            }

            EventLogger.logEvent(1679319515711487829L, 1380279919, 3);
            return;
         case 6:
            if (ret == 0) {
               this.processState(dgram.getServices());
               return;
            }

            EventLogger.logEvent(1679319515711487829L, 1380279919, 3);
            return;
         case 8:
            if (ret == 0) {
               this.processData(addressBase, dgram);
               return;
            }

            this.sendStatus(addressBase, dgram.getDatagramId(), ret);
            return;
         default:
            EventLogger.logEvent(1679319515711487829L, 1380279919, 3);
      }
   }

   private final void processState(StpUtil$ServiceInfo[] services) {
      for (int i = (services != null ? services.length : 0) - 1; i >= 0; i--) {
         StpUtil$ServiceInfo service = services[i];
         StpUtil.getInstance().setServiceState(service._uid, StpUtil.getStpCapabilities(services[i]), service._state, true);
      }
   }

   private final void processStatus(int tag, int result) {
      EventLogger.logEvent(1679319515711487829L, 1381004148, 4);
      int status;
      switch (result) {
         case 1:
            status = 6;
            break;
         case 2:
            status = 8337;
            break;
         case 7:
            status = 8577;
            break;
         default:
            status = 8579;
      }

      synchronized (this._lock) {
         if (tag == this._txDatagramId) {
            this._txStatus = status;
            this._lock.notify();
            return;
         }
      }

      this.xmitDgslEvent(null, tag, status, null);
   }

   private final void processData(DatagramAddressBase addressBase, StpDatagram dgram) {
      int tag = dgram.getDatagramId();
      synchronized (this._inboundDatagrams) {
         if (this._inboundDatagrams.containsKey(tag)) {
            EventLogger.logEvent(1679319515711487829L, 1380213615, 3);
         }
      }

      synchronized (this._inboundDatagrams) {
         Transport$StpRxDatagram rxDatagram = new Transport$StpRxDatagram(null);
         rxDatagram._timer = this._timer + 3;
         rxDatagram._key = tag;
         rxDatagram._address = addressBase;
         this._inboundDatagrams.put(tag, rxDatagram);
         this._inboundTimes.addElement(rxDatagram);
      }

      EventLogger.logEvent(1679319515711487829L, 1381527669, 5);
      if (!this.passUpDatagram(dgram)) {
         EventLogger.logEvent(1679319515711487829L, 1381527152, 3);
         this.processInboundDatagram(tag, 1);
      }
   }

   private final void sendConnect() {
      try {
         StpUtil$ServiceInfo[] services = StpUtil.getServiceInfo();
         if (services.length <= 0) {
            EventLogger.logEvent(1679319515711487829L, 1313829491, 0);
         } else {
            DatagramBase dgram = (DatagramBase)super._subConnection.newDatagram();
            StpUtil.encodeConnectCommand(services, dgram);
            this.addSendRequest(super._subConnection, dgram);
         }
      } finally {
         EventLogger.logEvent(1679319515711487829L, 1413834337, 2);
         return;
      }
   }

   private final void sendStatus(DatagramAddressBase addressBase, int tag, int result) {
      if (result == 1) {
         EventLogger.logEvent(1679319515711487829L, 1414554721, 4);
      } else {
         EventLogger.logEvent(1679319515711487829L, 1414558310, 3);
      }

      try {
         DatagramBase dgram = (DatagramBase)super._subConnection.newDatagram();
         StpUtil.encodeStatusCommand(tag, result, dgram);
         this.addSendRequest(super._subConnection, dgram);
      } finally {
         EventLogger.logEvent(1679319515711487829L, 1413834337, 2);
         return;
      }
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      super.eventOccurred(guid, data0, data1, object0, object1);
      if ((guid == -4220058463650496006L || guid == 8288627527798139133L || guid == 2522898683889177438L) && LstpUtil.getInstance().getLinkState()) {
         this.sendConnect();
      }
   }

   @Override
   public final void lstpLinkStateChanged(boolean linkState) {
      if (linkState) {
         this.sendConnect();
      } else {
         synchronized (this._lock) {
            if (this._txDatagram != null) {
               this._txStatus = 8577;
               this._lock.notify();
            }
         }

         StpUtil.getInstance().disableAllServices(false);
      }
   }

   @Override
   public final void updateDatagramStatus(int subId, int event, Object context) {
      if (event != 0) {
         this.passDgslEvent(subId, event, context);
      }
   }

   @Override
   public final void datagramProcessed(int datagramId) {
      this.processInboundDatagram(datagramId, 1);
   }

   private final void processInboundDatagram(int tag, int result) {
      DatagramAddressBase addressBase = null;
      synchronized (this._inboundDatagrams) {
         Transport$StpRxDatagram rxDatagram = (Transport$StpRxDatagram)this._inboundDatagrams.remove(tag);
         if (rxDatagram != null) {
            this._inboundTimes.removeElement(rxDatagram);
            if (rxDatagram._address != null) {
               addressBase = rxDatagram._address;
            }

            rxDatagram._address = null;
         }
      }

      if (addressBase != null) {
         this.sendStatus(addressBase, tag, result);
      }
   }

   @Override
   public final void clockUpdated() {
      synchronized (this._inboundDatagrams) {
         this._timer++;

         while (!this._inboundTimes.isEmpty()) {
            Transport$StpRxDatagram stpDatagram = (Transport$StpRxDatagram)this._inboundTimes.firstElement();
            if (this._timer < stpDatagram._timer) {
               break;
            }

            EventLogger.logEvent(super.GUID, 1380213624, 0);
            this._inboundDatagrams.remove(stpDatagram._key);
            this._inboundTimes.removeElementAt(0);
            stpDatagram._address = null;
         }
      }
   }
}
