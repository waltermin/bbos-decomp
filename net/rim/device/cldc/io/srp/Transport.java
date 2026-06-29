package net.rim.device.cldc.io.srp;

import java.io.IOException;
import java.util.Vector;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;
import net.rim.device.api.crypto.RandomSource;
import net.rim.device.api.io.ConnEvent;
import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.io.DatagramBase;
import net.rim.device.api.io.DatagramConnectionBase;
import net.rim.device.api.io.DatagramStatusListener;
import net.rim.device.api.io.DatagramTransportBase;
import net.rim.device.api.io.IONotRoutableException;
import net.rim.device.api.servicebook.ServiceRouting;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RealtimeClockListener;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.cldc.io.daemon.ProtocolDaemon;
import net.rim.vm.PersistentInteger;
import net.rim.vm.WeakReference;

public final class Transport extends DatagramTransportBase implements DatagramStatusListener, ConnEvent, RealtimeClockListener, SrpConstants {
   private int _nextDgramId = 0;
   private IntHashtable _datagrams = new IntHashtable();
   private Vector _confirmationDatagrams = new Vector();
   private IntHashtable _sentDatagrams = new IntHashtable();
   private Object _lock = new Object();
   private Vector _sentConfirmationDatagrams = new Vector();
   private int _timer;
   private SrpConnectionManager _connectionManager;
   private Transport$SrpConnectionThread _eventThread;
   static final long ID = 5159979649545707334L;
   private static final String SRP_STR = "net.rim.srp";
   private static final long TRANS_ID = -3101332663697609619L;

   final DatagramConnectionBase getSubConnection() {
      return super._subConnection;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   final void sendControl(SrpConfiguration srpConfiguration, SrpUtils$DatagramInfo info, boolean blocking) {
      DatagramBase datagram = (DatagramBase)super._subConnection.newDatagram(info.data, info.offset, info.length);
      datagram.setDatagramId(info.reference);
      info.data = null;
      info.offset = info.length = 0;
      boolean var7 = false /* VF: Semaphore variable */;

      try {
         var7 = true;
         SrpUtils.encode(datagram, info, srpConfiguration);
         var7 = false;
      } finally {
         if (var7) {
            EventLogger.logEvent(super.GUID, 1413834351, 2);
            return;
         }
      }

      if (!blocking) {
         this.addSendRequest(srpConfiguration._subConnection, datagram);
      } else {
         if (srpConfiguration.isConnectionActive()) {
            srpConfiguration._subConnection.send(datagram);
         }
      }
   }

   final void expireSentDatagrams(int comparisonValue, boolean timer) {
      synchronized (this._lock) {
         if (timer) {
            while (!this._sentConfirmationDatagrams.isEmpty()) {
               SrpUtils$DatagramInfo srpDatagram = (SrpUtils$DatagramInfo)this._sentConfirmationDatagrams.firstElement();
               if (srpDatagram != null && comparisonValue < srpDatagram.timer) {
                  break;
               }

               this.timeoutDatagram(srpDatagram, 0, 4560);
            }
         } else {
            int size = this._sentConfirmationDatagrams.size();
            int i = 0;

            while (i < size) {
               SrpUtils$DatagramInfo srpDatagram = (SrpUtils$DatagramInfo)this._sentConfirmationDatagrams.elementAt(i);
               if (srpDatagram != null && comparisonValue != 0 && comparisonValue != srpDatagram.flags) {
                  i++;
               } else {
                  this.timeoutDatagram(srpDatagram, i, 4243);
                  size--;
               }
            }
         }
      }
   }

   @Override
   public final void clockUpdated() {
      synchronized (this._lock) {
         this._timer++;

         while (!this._confirmationDatagrams.isEmpty()) {
            SrpUtils$DatagramInfo srpDatagram = (SrpUtils$DatagramInfo)this._confirmationDatagrams.firstElement();
            if (srpDatagram != null && this._timer < srpDatagram.timer) {
               break;
            }

            EventLogger.logEvent(super.GUID, 1380213624, 3);
            this._confirmationDatagrams.removeElementAt(0);
            this._datagrams.removeValue(srpDatagram);
         }

         this.expireSentDatagrams(this._timer, true);
      }
   }

   @Override
   public final void updateDatagramStatus(int subId, int event, Object context) {
      this.updateDatagramStatusLocal(subId, event, context, false);
   }

   @Override
   public final DatagramAddressBase newDatagramAddressBase(DatagramAddressBase addressBase, boolean swap) {
      SrpAddress ret = new SrpAddress(addressBase.getAddress());
      if (swap) {
         ret.swap();
      }

      return ret;
   }

   public Transport() {
      super.GUID = 5159979649545707334L;
      super.STR = "net.rim.srp";
   }

   @Override
   public final void init() {
      super.init(new Transport$SrpDummyConnection());
      EventLogger.register(super.GUID, "net.rim.srp", 2);
      int flashId = PersistentInteger.getId(-3101332663697609619L, Math.abs(RandomSource.getInt() % 2147483646 + 1));
      this._nextDgramId = PersistentInteger.get(flashId);
      PersistentInteger.set(flashId, Math.abs((this._nextDgramId + 9999991) % 2147483646 + 1));
      ProtocolDaemon pd = ProtocolDaemon.getInstance();
      pd.addRealtimeClockListener(this);
      this._connectionManager = new SrpConnectionManager(this);
      pd.addRadioListener(this._connectionManager);
      pd.addSystemListener(this._connectionManager);
      pd.addGlobalEventListener(this._connectionManager);
      ServiceRouting.getInstance().addListener(this._connectionManager);
      EventLogger.logEvent(super.GUID, 1229878386, 0);
   }

   @Override
   protected final synchronized int getNextDatagramId(DatagramBase dgram) {
      if (++this._nextDgramId == Integer.MAX_VALUE) {
         this._nextDgramId = 1;
      }

      return this._nextDgramId;
   }

   @Override
   public final int getMaximumLength() {
      return SrpUtils.getMaximumLength();
   }

   @Override
   public final int getNominalLength() {
      return SrpUtils.getNominalLength();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void send(Datagram datagram) throws IOException, IONotRoutableException {
      EventLogger.logEvent(super.GUID, 1415082868, 0);
      boolean internal = datagram instanceof DatagramBase;
      String address = datagram.getAddress();
      int linkType = -1;
      int connectionType = -1;
      if (internal) {
         DatagramAddressBase addr = ((DatagramBase)datagram).getAddressBase();
         if (addr instanceof SrpAddress) {
            linkType = ((SrpAddress)addr).getLinkType();
            connectionType = ((SrpAddress)addr).getConnectionType();
         }
      }

      SrpConfiguration srpConfiguration = null;
      if (address != null) {
         srpConfiguration = this._connectionManager.getConnectedSrpConfigurationByUid(linkType, connectionType, address, null, false);
      }

      if (srpConfiguration == null) {
         throw new IONotRoutableException();
      }

      if (!srpConfiguration.isConnectionActive()) {
         EventLogger.logEvent(super.GUID, 1129214834, 2);
         throw new IONotRoutableException();
      }

      DatagramBase dgram = (DatagramBase)super._subConnection.newDatagram(0);
      if (internal) {
         dgram.copy((DatagramBase)datagram);
         dgram.setDatagramStatusListener(((DatagramBase)datagram).getDatagramStatusListener());
      } else {
         dgram.setDatagramId(0);
         dgram.setAddress(datagram.getAddress());
         dgram.setData(datagram.getData(), datagram.getOffset(), datagram.getLength());
      }

      int superRef = dgram.getDatagramId();
      dgram.setDatagramId(this.getNextDatagramId(null));
      SrpUtils$DatagramInfo info = SrpUtils.makeDatagramInfo(srpConfiguration._version, (byte)8, dgram.getDatagramId());
      boolean var14 = false /* VF: Semaphore variable */;

      try {
         var14 = true;
         SrpUtils.encode(dgram, info, srpConfiguration);
         var14 = false;
      } finally {
         if (var14) {
            EventLogger.logEvent(super.GUID, 1413834351, 2);
            throw new IOException();
         }
      }

      if (superRef != 0) {
         synchronized (this._lock) {
            info.superReference = superRef;
            info.object = dgram.getDatagramStatusListener();
            info.timer = this._timer + (srpConfiguration.getConnectionType() == 1 ? 15 : 3);
            info.flags = srpConfiguration._configurationUID;
            this._sentDatagrams.put(info.reference, info);
            this._sentConfirmationDatagrams.addElement(info);
         }
      }

      this.addSendRequest(srpConfiguration._subConnection, dgram);
   }

   @Override
   public final DatagramAddressBase newDatagramAddressBase(String address, boolean swap) {
      SrpAddress ret = new SrpAddress(address);
      if (swap) {
         ret.swap();
      }

      return ret;
   }

   private final void updateDatagramStatusLocal(int subId, int event, Object context, boolean remove) {
      if (subId != 0) {
         DatagramStatusListener listener = null;
         SrpUtils$DatagramInfo info = null;
         synchronized (this._lock) {
            info = (SrpUtils$DatagramInfo)(remove ? this._sentDatagrams.remove(subId) : this._sentDatagrams.get(subId));
            if (info != null) {
               subId = info.superReference;
               listener = (DatagramStatusListener)info.object;
               if (remove) {
                  this._sentConfirmationDatagrams.removeElement(info);
                  info.superReference = info.reference = 0;
                  info.object = null;
                  info.data = null;
                  info.offset = info.length = 0;
               }
            }
         }

         if (info != null && subId != 0) {
            this.xmitDgslEvent(listener, subId, event, null);
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected final void processReceivedDatagram(Datagram datagram) throws IOException {
      EventLogger.logEvent(super.GUID, 1381528436, 0);
      SrpConfiguration srpConfiguration = null;
      Object address = null;
      if (!(datagram instanceof DatagramBase)) {
         address = datagram.getAddress();
         srpConfiguration = this._connectionManager.getConnectedSrpConfigurationByAddress(-1, -1, (String)address);
      } else {
         address = ((DatagramBase)datagram).getAddressBase();
         srpConfiguration = this._connectionManager
            .getConnectedSrpConfigurationByAddress(((SrpAddress)address).getLinkType(), ((SrpAddress)address).getConnectionType(), (DatagramAddressBase)address);
      }

      if (srpConfiguration == null) {
         EventLogger.logEvent(super.GUID, 1129213298, 2);
         throw new IOException();
      }

      SrpUtils$DatagramInfo info = null;
      boolean var7 = false /* VF: Semaphore variable */;

      try {
         var7 = true;
         if (datagram instanceof SrpDatagramInternal) {
            info = SrpUtils.decode(
               datagram.getData(),
               ((SrpDatagramInternal)datagram).getPayload(),
               ((SrpDatagramInternal)datagram).getPayloadOffset(),
               ((SrpDatagramInternal)datagram).getPayloadLength(),
               srpConfiguration
            );
            var7 = false;
         } else {
            info = SrpUtils.decode(null, datagram.getData(), datagram.getOffset(), datagram.getLength(), srpConfiguration);
            var7 = false;
         }
      } finally {
         if (var7) {
            EventLogger.logEvent(super.GUID, 1380279919, 2);
            throw new IOException();
         }
      }

      srpConfiguration.rxActivity(info.type != 2);
      switch (info.type) {
         case -15:
            this.sendControl(srpConfiguration, info, false);
            if (srpConfiguration.getConnectionType() == 1) {
               srpConfiguration.postRequest(-14, 0);
               return;
            }
            break;
         case -14:
            srpConfiguration.postRequest(-14, 0, false);
            break;
         case -4:
            info.type = -3;
            this.sendControl(srpConfiguration, info, false);
            return;
         case -3:
            EventLogger.logEvent(super.GUID, 1414557801, 0);
            srpConfiguration.postRequest(-3, 0);
            return;
         case 2:
            this.sendControl(srpConfiguration, info, false);
            return;
         case 4:
            this.processStatus(srpConfiguration, info);
            return;
         case 6:
            EventLogger.logEvent(super.GUID, 1129210979, info.flags, 16, (info.flags & -2147483648) == Integer.MIN_VALUE ? 2 : 3);
            srpConfiguration.postRequest(6, info.flags, false);
            return;
         case 8:
            info.object = address;
            this.processData(srpConfiguration, info);
            return;
         case 27:
            info.type = 28;
            this.sendControl(srpConfiguration, info, false);
            return;
      }
   }

   private final void processStatus(SrpConfiguration srpConfiguration, SrpUtils$DatagramInfo info) {
      EventLogger.logEvent(super.GUID, 1381000289, 4);
      int event = 0;
      short var7;
      switch (info.flags) {
         case 0:
         case 8:
            var7 = 13186;
            break;
         case 1:
         default:
            var7 = 0;
            break;
         case 2:
            var7 = 8337;
            break;
         case 3:
            var7 = 129;
            break;
         case 4:
            var7 = 12931;
            break;
         case 5:
            var7 = 12929;
            break;
         case 6:
            var7 = 13191;
            break;
         case 7:
            var7 = 4226;
            break;
         case 9:
            var7 = 4227;
      }

      this.updateDatagramStatusLocal(info.reference, var7, null, true);
      if (info.ackFlag) {
         info.type = 20;

         try {
            this.sendControl(srpConfiguration, info, false);
         } finally {
            EventLogger.logEvent(super.GUID, 1381000289, 2);
            return;
         }
      }
   }

   private final void processData(SrpConfiguration srpConfiguration, SrpUtils$DatagramInfo info) {
      DatagramBase dgram = (DatagramBase)this.newDatagram(info.data, info.offset, info.length, null);
      int datagramId = this.getNextDatagramId(null);
      dgram.setDatagramId(datagramId);
      info.data = null;
      info.offset = info.length = 0;
      if (info.object == null) {
         info.object = srpConfiguration.getCurrentSrpAddress();
      }

      dgram.setAddressBase(new SrpAddress(srpConfiguration._enabledServicesList, srpConfiguration.getLinkType(), srpConfiguration.getConnectionType()));
      synchronized (this._lock) {
         info.timer = this._timer + 3;
         this._datagrams.put(datagramId, info);
         this._confirmationDatagrams.addElement(info);
      }

      EventLogger.logEvent(super.GUID, 1381527669, 5);
      if (!this.passUpDatagram(dgram)) {
         EventLogger.logEvent(super.GUID, 1381527152, 3);
         this.datagramProcessed(datagramId);
      }
   }

   @Override
   public final void datagramProcessed(int datagramId) {
      Object address = null;
      SrpUtils$DatagramInfo info = null;
      synchronized (this._lock) {
         info = (SrpUtils$DatagramInfo)this._datagrams.remove(datagramId);
         if (info != null) {
            this._confirmationDatagrams.removeElement(info);
            address = info.object;
            info.object = null;
         }
      }

      if (address != null) {
         EventLogger.logEvent(super.GUID, 1414554721, 4);
         SrpConfiguration srpConfiguration = null;
         if (address instanceof SrpAddress) {
            srpConfiguration = this._connectionManager
               .getConnectedSrpConfigurationByAddress(
                  ((SrpAddress)address).getLinkType(), ((SrpAddress)address).getConnectionType(), (DatagramAddressBase)address
               );
         } else {
            srpConfiguration = this._connectionManager.getConnectedSrpConfigurationByAddress(-1, -1, (String)address);
         }

         if (srpConfiguration != null) {
            info.version = srpConfiguration._version;
            info.type = 4;
            info.flags = 1;

            try {
               this.sendControl(srpConfiguration, info, false);
            } finally {
               EventLogger.logEvent(super.GUID, 1414554721, 2);
               return;
            }
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void close(DatagramConnection connection) {
      boolean var15 = false /* VF: Semaphore variable */;

      try {
         var15 = true;
         super.close(connection);
         var15 = false;
      } finally {
         if (var15) {
            if (connection instanceof Protocol) {
               Protocol conn = (Protocol)connection;
               boolean foundMatchingType = false;
               synchronized (super._superConnections) {
                  for (int i = super._superConnections.length - 1; i >= 0; i--) {
                     WeakReference w = super._superConnections[i];
                     if (w != null) {
                        DatagramConnection wc = (DatagramConnection)w.get();
                        if (wc instanceof Protocol) {
                           Protocol c = (Protocol)wc;
                           if (c.getLinkType() == conn.getLinkType() && c.getConnectionType() == conn.getConnectionType()) {
                              foundMatchingType = true;
                              break;
                           }
                        }
                     }
                  }
               }

               if (!foundMatchingType) {
                  this._connectionManager.close(conn.getLinkType(), conn.getConnectionType(), true);
               }
            }
         }
      }

      if (connection instanceof Protocol) {
         Protocol conn = (Protocol)connection;
         boolean foundMatchingType = false;
         synchronized (super._superConnections) {
            for (int i = super._superConnections.length - 1; i >= 0; i--) {
               WeakReference w = super._superConnections[i];
               if (w != null) {
                  DatagramConnection wc = (DatagramConnection)w.get();
                  if (wc instanceof Protocol) {
                     Protocol c = (Protocol)wc;
                     if (c.getLinkType() == conn.getLinkType() && c.getConnectionType() == conn.getConnectionType()) {
                        foundMatchingType = true;
                        break;
                     }
                  }
               }
            }
         }

         if (!foundMatchingType) {
            this._connectionManager.close(conn.getLinkType(), conn.getConnectionType(), true);
            return;
         }
      }
   }

   @Override
   public final void addConnection(DatagramConnection connection) {
      super.addConnection(connection);
      if (connection instanceof Protocol) {
         Protocol conn = (Protocol)connection;
         boolean foundMatchingType = false;
         synchronized (super._superConnections) {
            for (int i = super._superConnections.length - 1; i >= 0; i--) {
               WeakReference w = super._superConnections[i];
               if (w != null) {
                  DatagramConnection wc = (DatagramConnection)w.get();
                  if (wc instanceof Protocol) {
                     Protocol c = (Protocol)wc;
                     if (c != connection && c.getLinkType() == conn.getLinkType() && c.getConnectionType() == conn.getConnectionType()) {
                        foundMatchingType = true;
                        break;
                     }
                  }
               }
            }
         }

         if (!foundMatchingType) {
            this._connectionManager.getRoutingInfo(conn.getLinkType(), conn.getConnectionType());
         }
      }
   }

   private final void timeoutDatagram(SrpUtils$DatagramInfo srpDatagram, int index, int errorCode) {
      if (srpDatagram != null) {
         EventLogger.logEvent(super.GUID, 1414423649, srpDatagram.superReference, 10, 3);
         this._sentConfirmationDatagrams.removeElementAt(index);
         this._sentDatagrams.removeValue(srpDatagram);
         if (this._eventThread == null) {
            this._eventThread = new Transport$SrpConnectionThread(this, null);
            ProtocolDaemon.getInstance().startThread(this._eventThread);
         }

         this._eventThread.add(new Transport$SrpConnectionEvent(srpDatagram, errorCode, null));
      }
   }
}
