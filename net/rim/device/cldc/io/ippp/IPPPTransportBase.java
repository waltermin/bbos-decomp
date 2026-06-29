package net.rim.device.cldc.io.ippp;

import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;
import net.rim.device.api.io.DatagramBase;
import net.rim.device.api.io.DatagramStatusListener;
import net.rim.device.api.io.DatagramTransportBase;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.api.util.TLEUtilities;
import net.rim.device.cldc.io.daemon.TransportRegistry;
import net.rim.device.cldc.io.gme.GMEDatagram;
import net.rim.device.cldc.io.gme.GMETarget;
import net.rim.device.internal.firewall.Firewall;
import net.rim.device.internal.provisioning.ProvisioningHandler;
import net.rim.device.internal.provisioning.ProvisioningService;
import net.rim.vm.PersistentInteger;
import net.rim.vm.WeakReference;

public class IPPPTransportBase extends DatagramTransportBase implements DatagramStatusListener, ProvisioningHandler {
   private IntHashtable _gmeDatagramTable;
   private int _sysCheckTimeout = 10000;
   private int _numSysCheckSentId;
   private int _numSysCheckSentAndWorkedId;
   private static final int EVENT_LOG_TX;
   private static final int EVENT_LOG_RX;
   private static final int EVENT_LOG_SYS_CHCK;
   public static final byte VERSION;
   public static final long IPPP_TRANSPORT_GUID;
   private static final int DEFAULT_SYS_CHECK_TIMEOUT;
   private static final long SYS_CHECK_PERSISTENT_ID;
   private static final long SYS_CHECK_SENT_PERSISTENT_ID;
   private static final long SYS_CHECK_SENT_AND_WORKED_PERSISTENT_ID;
   static Class class$net$rim$device$cldc$io$ippp$Transport;

   void sysCheckWorked() {
      PersistentInteger.set(this._numSysCheckSentAndWorkedId, PersistentInteger.get(this._numSysCheckSentAndWorkedId) + 1);
   }

   public int getInteractivePingPacketTimeout() {
      return this._sysCheckTimeout;
   }

   void sendSysCheck(String groupUID, String specificUID) {
      if (groupUID == null) {
         throw new Object("Could not find a service book entry for IPPP");
      }

      StringBuffer address = (StringBuffer)(new Object());
      address.append("IPPP/").append(groupUID);
      if (specificUID != null) {
         address.append('(').append(specificUID).append(')');
      }

      GMEDatagram txGmeDatagram = (GMEDatagram)super._subConnection.newDatagram(super._subConnection.getNominalLength());
      txGmeDatagram.setAddress(address.toString());
      txGmeDatagram.setCommandByte(10);
      this.addSendRequest(super._subConnection, txGmeDatagram);
      PersistentInteger.set(this._numSysCheckSentId, PersistentInteger.get(this._numSysCheckSentId) + 1);
      EventLogger.logEvent(6406224406390975741L, 1417171779, 0);
   }

   @Override
   public void updateProvisioningData(IntIntHashtable chunks, DataBuffer db) {
      int value = PersistentInteger.get(PersistentInteger.getId(-5227468754307966203L, -1));
      if (value == -1 && chunks.containsKey(27)) {
         db.setPosition(chunks.get(27));
         value = TLEUtilities.readIntegerField(db);
         if (value < 0 || value > 600000) {
            value = -1;
         }
      }

      if (value != -1) {
         this._sysCheckTimeout = value;
      } else {
         this._sysCheckTimeout = 10000;
      }
   }

   @Override
   public void updateDatagramStatus(int dgId, int code, Object context) {
      Object[] notifyObj = null;
      if (code != 0 && (code & 128) == 0) {
         notifyObj = (Object[])this._gmeDatagramTable.get(dgId);
      } else {
         notifyObj = (Object[])this._gmeDatagramTable.remove(dgId);
      }

      if (notifyObj != null) {
         WeakReference ref = (WeakReference)notifyObj[1];
         DatagramStatusListener listener = (DatagramStatusListener)ref.get();
         if (listener != null) {
            try {
               listener.updateDatagramStatus(((GMEDatagram)notifyObj[0]).getDatagramId(), code, context);
               return;
            } finally {
               return;
            }
         }
      }
   }

   public static int getTransportInteractivePingPacketTimeout() {
      try {
         IPPPTransportBase instance = (IPPPTransportBase)TransportRegistry.get(
            (class$net$rim$device$cldc$io$ippp$Transport == null
                  ? (class$net$rim$device$cldc$io$ippp$Transport = class$("net.rim.device.cldc.io.ippp.Transport"))
                  : class$net$rim$device$cldc$io$ippp$Transport)
               .getName()
         );
         return instance._sysCheckTimeout;
      } finally {
         ;
      }
   }

   public static int getNumInteractivePingSentCount() {
      try {
         IPPPTransportBase instance = (IPPPTransportBase)TransportRegistry.get(
            (class$net$rim$device$cldc$io$ippp$Transport == null
                  ? (class$net$rim$device$cldc$io$ippp$Transport = class$("net.rim.device.cldc.io.ippp.Transport"))
                  : class$net$rim$device$cldc$io$ippp$Transport)
               .getName()
         );
         return PersistentInteger.get(instance._numSysCheckSentId);
      } finally {
         ;
      }
   }

   public static int getNumInteractivePingAndWorkedCount() {
      try {
         IPPPTransportBase instance = (IPPPTransportBase)TransportRegistry.get(
            (class$net$rim$device$cldc$io$ippp$Transport == null
                  ? (class$net$rim$device$cldc$io$ippp$Transport = class$("net.rim.device.cldc.io.ippp.Transport"))
                  : class$net$rim$device$cldc$io$ippp$Transport)
               .getName()
         );
         return PersistentInteger.get(instance._numSysCheckSentAndWorkedId);
      } finally {
         ;
      }
   }

   @Override
   public void init() {
      super.init((DatagramConnection)Connector.open("gme:IPPP"));
      super._subConnection.setDatagramStatusListener(this);
   }

   @Override
   public int getMaximumLength() {
      return super._subConnection.getMaximumLength() - 1 - 4 - 1 - 1;
   }

   @Override
   public int getNominalLength() {
      return super._subConnection.getNominalLength() - 1 - 4 - 1 - 1;
   }

   @Override
   protected synchronized int getNextDatagramId(DatagramBase dgram) {
      super._subConnection.allocateDatagramId(dgram);
      return dgram.getDatagramId();
   }

   @Override
   public Datagram newDatagram(byte[] _1, int _2, int _3, String _4) {
      throw null;
   }

   public IPPPTransportBase() {
      EventLogger.register(6406224406390975741L, "net.rim.ippp", 2);
      this._gmeDatagramTable = (IntHashtable)(new Object());

      label20:
      try {
         ProvisioningService.getInstance().addHandler(this);
      } finally {
         break label20;
      }

      this._numSysCheckSentId = PersistentInteger.getId(-3040950248556710806L, 0);
      this._numSysCheckSentAndWorkedId = PersistentInteger.getId(-367257579294980944L, 0);
   }

   @Override
   public void send(Datagram datagram) {
      EventLogger.logEvent(6406224406390975741L, 1415082868, datagram.getLength(), 10, 0);
      IPPPDatagramBase ipppDatagram = (IPPPDatagramBase)datagram;
      GMEDatagram txGmeDatagram = (GMEDatagram)super._subConnection.newDatagram(super._subConnection.getNominalLength());
      String groupUID = ipppDatagram.getGroupUID();
      if (groupUID == null) {
         throw new Object("Could not find a service book entry for IPPP");
      }

      StringBuffer address = (StringBuffer)(new Object());
      address.append("IPPP/").append(groupUID);
      String specificUID = ipppDatagram.getSpecificUID();
      if (specificUID != null) {
         address.append('(').append(specificUID).append(')');
      }

      txGmeDatagram.setAddress(address.toString());
      ipppDatagram.writeTo(txGmeDatagram);
      txGmeDatagram.setLength(txGmeDatagram.getPosition());
      txGmeDatagram.setDatagramId(ipppDatagram.getDatagramId());
      txGmeDatagram.setDatagramStatusListener(this);
      this._gmeDatagramTable.put(txGmeDatagram.getDatagramId(), new Object[]{txGmeDatagram, new Object(ipppDatagram.getDatagramStatusListener())});
      this.addSendRequest(super._subConnection, txGmeDatagram);
      EventLogger.logEvent(6406224406390975741L, 1417169218, 5);
   }

   @Override
   protected void processReceivedDatagram(Datagram datagram) {
      GMEDatagram gmeDatagram = (GMEDatagram)datagram;
      if (gmeDatagram.isFromPeer()) {
         Firewall.getInstance().incrementBlockedCount((byte)-4);
      } else {
         EventLogger.logEvent(6406224406390975741L, 1381516132, datagram.getLength(), 10, 0);
         IPPPDatagramBase ipppDatagram = (IPPPDatagramBase)this.newDatagram(null, 0, 0, null);
         ipppDatagram.readFrom(gmeDatagram);
         ipppDatagram.setAddress(Integer.toString(ipppDatagram.getConnectionID()));
         GMETarget target = gmeDatagram.getGMEAddress().getSrc();
         ipppDatagram.setGroupUID(target.address);
         ipppDatagram.setSpecificUID(target.redirect);
         ipppDatagram.setEncrypted(gmeDatagram.wasEncrypted());
         ipppDatagram.setDatagramSecure(gmeDatagram.wasDatagramSecure());
         EventLogger.logEvent(6406224406390975741L, 1383614786, 5);
         this.passUpDatagram(ipppDatagram);
      }
   }

   public static void setInteractivePingPacketTimeoutOverride(int value) {
      PersistentInteger.set(PersistentInteger.getId(-5227468754307966203L, -1), value);

      try {
         IPPPTransportBase instance = (IPPPTransportBase)TransportRegistry.get(
            (class$net$rim$device$cldc$io$ippp$Transport == null
                  ? (class$net$rim$device$cldc$io$ippp$Transport = class$("net.rim.device.cldc.io.ippp.Transport"))
                  : class$net$rim$device$cldc$io$ippp$Transport)
               .getName()
         );
         instance._sysCheckTimeout = value;
      } finally {
         return;
      }
   }

   @Override
   public void superCancel(Datagram datagram) {
      super.superCancel(datagram);
      EventLogger.logEvent(6406224406390975741L, 1415078753, 0);
      IPPPDatagramBase ipppDatagram = (IPPPDatagramBase)datagram;
      Object[] datagramInfo = (Object[])this._gmeDatagramTable.get(ipppDatagram.getDatagramId());
      if (datagramInfo != null) {
         super._subConnection.cancel((Datagram)datagramInfo[0]);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new Object(x1.getMessage());
      }
   }
}
