package net.rim.device.cldc.io.mdp;

import java.util.Random;
import javax.microedition.io.Datagram;
import net.rim.device.api.io.ConnEvent;
import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.io.DatagramBase;
import net.rim.device.api.io.DatagramStatusListener;
import net.rim.device.api.io.DatagramTransportBase;
import net.rim.device.api.io.IOProperties;
import net.rim.device.api.io.UdpAddress;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.api.system.SystemListener2;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.CRC32;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.api.util.TLEUtilities;
import net.rim.device.cldc.io.daemon.ProtocolDaemon;
import net.rim.device.cldc.io.datarecovery.DataRecovery;
import net.rim.device.cldc.io.fastdormancy.FastDormancyManager;
import net.rim.device.cldc.io.gme.GmeUtil;
import net.rim.device.cldc.io.udp.UdpInternalAddress;
import net.rim.device.internal.provisioning.ProvisioningHandler;
import net.rim.device.internal.provisioning.ProvisioningService;
import net.rim.device.internal.system.RadioInternal;
import net.rim.vm.PersistentInteger;

public final class Transport
   extends DatagramTransportBase
   implements MdpConstants,
   DatagramStatusListener,
   ConnEvent,
   RadioStatusListener,
   SystemListener2,
   ProvisioningHandler {
   private DatagramBase _txPacket;
   private DatagramStatusListener _txListener;
   private int _txDgramId;
   private int _maxNativeLength;
   private int _nextDgramId;
   private int[] _sendStatusIds;
   private int[] _sendStatusCodes;
   private IntHashtable _sendReceipts;
   private int _sendStatusHead;
   private int _sendStatusTail;
   private int[] _sendStatusReturn;
   private Object _sendChokeLock = new Object();
   private Datagram _txDatagram;
   private int _maxAttempts = 10;
   private int _ackTimeout = 6000;
   private int _backoffStart = 4000;
   private int _backoffMax = 600000;
   private int _requestPeriodThreshold = 600000;
   private WirelessTransportProfile[] _wtProfiles;
   private int _wtProfilesCount;
   private int _datagramTimeout = 244000;
   private int _datagramPingTimeout = 56000;
   private int _datagramPingMaxAttempts = 4;
   private int _datagramMaxAttempts = 1;
   private int _datagramAckTimeout = 120000;
   private int _maxPacketsOutstanding;
   private MdpWaitingQueueThread _mdpWaitingQueueThread;
   private MdpWaitingQueueNotificationThread _mdpWaitingQueueNotificationThread;
   private MFHPacketsTimerQueue _mdpMFHPacketsTimerQueueThread;
   private boolean _mfhOptimizedAcksFlag;
   private MdpSendTable _txTable;
   private MdpReceiveTable _rxTable;
   private FastDormancyManager _fastDormancy;
   private DataRecovery _dataRecovery;
   private static final long TRANSPORT_GUID = 4175103112525226476L;
   private static Transport _instance;
   private static final int MAX_SEND_STATUS = 16;
   private static final byte _numRetries = 8;
   private static final int _retriesBackoffMax = 600000;
   private static final int MFH_OPTIMIZED_ACKS_FLAG_PERSISTENT_ID = PersistentInteger.getId(5818824363781589698L, 1);

   final void superSendInternal(Datagram datagram) {
      this.superSend(datagram);
   }

   final void sendStatusInternal(DatagramAddressBase nativeAddress, int reference, boolean ackFlag, boolean swapAddress) {
      this.sendStatus(nativeAddress, reference, ackFlag, swapAddress);
   }

   final void addSendRequestInternal(Datagram datagram) {
      this.addSendRequest(super._subConnection, datagram);
   }

   final Object getTransportLock() {
      return this._rxTable;
   }

   final DatagramBase createParamRequest(DatagramAddressBase nativeAddress, boolean pAck) {
      try {
         DatagramBase datagram = (DatagramBase)super._subConnection.newDatagram();
         MdpUtil$DatagramInfo info = MdpUtil.makeDatagramInfo(13);
         info.reference = 0;
         info.packetAckFlag = pAck;
         MdpUtil.encode(datagram, info);
         datagram.setAddressBase(super._subConnection.newDatagramAddressBase(nativeAddress, true));
         datagram.setFlag(8, this._fastDormancy.getFastDormancy());
         return datagram;
      } finally {
         EventLogger.logEvent(super.GUID, 1413834337, 2);
         return null;
      }
   }

   final void cancelResentDatagramIfPossible(int reference, boolean cancelCurrent) {
      if (this._mdpWaitingQueueNotificationThread != null) {
         this._mdpWaitingQueueNotificationThread.cancelResentDatagram(reference, cancelCurrent);
      }
   }

   final void queueWaitingDatagramStatus(int reference, int status, MdpUtil$DatagramInfo info) {
      if (this._mdpWaitingQueueThread != null && !this._mdpWaitingQueueThread.isEmpty()) {
         this._mdpWaitingQueueNotificationThread.queueWaitingSendStatus(reference, status, info);
      }
   }

   final Datagram removeWaitingDatagram(int reference) {
      return this._mdpWaitingQueueThread != null ? this._mdpWaitingQueueThread.removeDatagram(reference) : null;
   }

   final Datagram resendWaitingDatagramIfNeeded(int reference, byte[] dataArray, int offset, int length) {
      return this._mdpWaitingQueueThread != null ? this._mdpWaitingQueueThread.resendDatagramIfNeeded(reference, dataArray, offset, length) : null;
   }

   final void sendReceipts(WirelessTransportProfile wtProfile, boolean sendAll) {
      try {
         int[] windowDatagramsKeys = wtProfile._windowDatagramsKeys;
         byte[] windowDatagramsPackets = wtProfile._windowDatagramsPackets;
         boolean useOptAcks = !wtProfile._legacyMode;
         int size = windowDatagramsKeys.length;

         for (int i = size - 1; i >= 0; i--) {
            boolean sendReceipt = (windowDatagramsPackets[i] & 128) == 0 || sendAll;
            int numOfPackets = windowDatagramsPackets[i] & 127;
            if (numOfPackets > 0 && sendReceipt) {
               MdpRxDatagram d = null;
               if ((windowDatagramsPackets[i] & 128) == 0) {
                  d = this._rxTable.findAssemblyDatagram(windowDatagramsKeys[i]);
               } else {
                  d = this._rxTable.findConfirmationDatagram(windowDatagramsKeys[i]);
               }

               if (d != null && (d._optimizedAckFlag || !d.packetReceived(0) && useOptAcks)) {
                  this.sendReceipt(
                     (DatagramAddressBase)(!(d._subAddress instanceof Object)
                        ? new MdpAddress((String)d._subAddress, d._destPort, d._srcPort, d._wtAddress).getSubAddressBase()
                        : d._subAddress),
                     d._reference,
                     d.makeStatusArray()
                  );
               }

               wtProfile._windowCount -= numOfPackets;
               windowDatagramsKeys[i] = windowDatagramsPackets[i] = 0;
            }
         }

         if (wtProfile._maxWindowSize < size) {
            size--;

            while (size >= 0 && windowDatagramsKeys[size] == 0 && windowDatagramsPackets[size] == 0) {
               size--;
            }

            if (size < 0) {
               windowDatagramsKeys = new int[wtProfile._maxWindowSize];
               windowDatagramsPackets = new byte[wtProfile._maxWindowSize];
               if (windowDatagramsKeys != null && windowDatagramsPackets != null) {
                  wtProfile._windowDatagramsKeys = windowDatagramsKeys;
                  wtProfile._windowDatagramsPackets = windowDatagramsPackets;
                  return;
               }
            }
         }
      } finally {
         return;
      }
   }

   final void updateWindowArrays(WirelessTransportProfile wtProfile, int datagramKey, byte actionToTake) {
      try {
         int[] windowDatagramsKeys = wtProfile._windowDatagramsKeys;
         byte[] windowDatagramsPackets = wtProfile._windowDatagramsPackets;

         for (int i = windowDatagramsKeys.length - 1; i >= 0; i--) {
            if (datagramKey == windowDatagramsKeys[i]) {
               int numOfPackets = windowDatagramsPackets[i] & 127;
               switch (actionToTake) {
                  case 0:
                     break;
                  case 1:
                  default:
                     boolean sendReceipt = (windowDatagramsPackets[i] & 128) == 0;
                     windowDatagramsPackets[i] = (byte)(numOfPackets - 1);
                     if (windowDatagramsPackets[i] == 0) {
                        windowDatagramsKeys[i] = 0;
                     } else if (!sendReceipt) {
                        windowDatagramsPackets[i] = (byte)(windowDatagramsPackets[i] | 128);
                     }

                     wtProfile._windowCount--;
                     return;
                  case 2:
                  case 3:
                     wtProfile._windowCount -= numOfPackets;
                     windowDatagramsKeys[i] = windowDatagramsPackets[i] = 0;
                     return;
                  case 4:
                     windowDatagramsPackets[i] = (byte)(windowDatagramsPackets[i] | 128);
                     return;
                  case 5:
                     windowDatagramsPackets[i] = 1;
                     return;
               }
            }
         }
      } finally {
         return;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   final void requestWirelessTransportProfileParam() {
      for (int i = this._wtProfilesCount - 1; i >= 0; i--) {
         WirelessTransportProfile wtProfile = this._wtProfiles[i];
         if (wtProfile != null && wtProfile.shouldRequestParam() && wtProfile._requestTimerRunnable != null) {
            wtProfile._requestTimerRunnable.reset(8);
            boolean var5 = false /* VF: Semaphore variable */;

            try {
               var5 = true;
               ProtocolDaemon.getInstance().submitRunnable(wtProfile._requestTimerRunnable);
               var5 = false;
            } finally {
               if (var5) {
                  EventLogger.logEvent(super.GUID, 1414022514, 3);
                  continue;
               }
            }
         }
      }
   }

   public final int sendSetupHeader(Datagram datagram) {
      return this._txDgramId != 0 ? this._txDgramId : this.getNextDatagramId(null);
   }

   protected final void queueSendStatus(int dgramId, int status, MdpUtil$DatagramInfo info) {
      synchronized (this._sendStatusIds) {
         this._sendStatusIds[this._sendStatusHead] = dgramId;
         this._sendStatusCodes[this._sendStatusHead] = status;
         this._sendStatusHead++;
         this._sendStatusHead &= 15;
         if (this._sendStatusTail == this._sendStatusHead) {
            this._sendStatusTail++;
            this._sendStatusTail &= 15;
         }

         if (info != null) {
            this._sendReceipts.put(dgramId, info);
         }

         this._sendStatusIds.notify();
      }
   }

   public final void mobilityManagementEvent(int eventCode, int cause) {
   }

   @Override
   public final void pdpStateChange(int apn, int state, int cause) {
   }

   @Override
   public final void networkStateChange(int state) {
   }

   @Override
   public final void networkScanComplete(boolean success) {
   }

   @Override
   public final void baseStationChange() {
   }

   @Override
   public final void networkServiceChange(int networkId, int service) {
   }

   @Override
   public final void signalLevel(int level) {
   }

   @Override
   public final void networkStarted(int networkId, int service) {
   }

   @Override
   public final void radioTurnedOff() {
      this.allowRetries();
   }

   @Override
   public final void usbConnectionStateChange(int state) {
   }

   @Override
   public final void backlightStateChange(boolean on) {
   }

   @Override
   public final void updateDatagramStatus(int subId, int event, Object context) {
      int index = this.lookupDgramIndexFromSubId(subId);
      if (index >= 0) {
         switch (event) {
            default:
               this.forwardDgslEvent(index, event, context);
            case 0:
         }
      }
   }

   @Override
   public final void cradleMismatch(boolean mismatch) {
   }

   @Override
   public final void powerOffRequested(int reason) {
   }

   @Override
   public final void batteryStatusChange(int status) {
   }

   @Override
   public final void batteryGood() {
   }

   @Override
   public final void batteryLow() {
   }

   @Override
   public final void powerUp() {
   }

   @Override
   public final void powerOff() {
   }

   @Override
   public final void fastReset() {
      this.allowRetries();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void updateProvisioningData(IntIntHashtable chunks, DataBuffer db) {
      boolean var5 = false /* VF: Semaphore variable */;

      try {
         var5 = true;
         if (chunks.containsKey(7)) {
            db.setPosition(chunks.get(7));
            boolean mfhOptAcksEnabled = 1 >= TLEUtilities.readIntegerField(db);
            if (this._mfhOptimizedAcksFlag != mfhOptAcksEnabled) {
               this._mfhOptimizedAcksFlag = mfhOptAcksEnabled;
               PersistentInteger.set(MFH_OPTIMIZED_ACKS_FLAG_PERSISTENT_ID, this._mfhOptimizedAcksFlag ? 1 : 0);
               return;
            }

            var5 = false;
         } else {
            var5 = false;
         }
      } finally {
         if (var5) {
            EventLogger.logEvent(super.GUID, 1347573317, 2);
            return;
         }
      }
   }

   @Override
   public final void init() {
      super.init(MdpUtil.makeNativeConnection());
      EventLogger.register(super.GUID, super.STR, 2);
      this._txPacket = (DatagramBase)super._subConnection.newDatagram();
      this._maxNativeLength = MdpUtil.getNominalLength();
      this._nextDgramId = Math.abs(((Random)(new Object())).nextInt() % 126 + 1);
      this._sendStatusIds = new int[16];
      this._sendStatusCodes = new int[16];
      this._sendReceipts = (IntHashtable)(new Object(16));
      this._sendStatusReturn = new int[2];
      this._fastDormancy = FastDormancyManager.getInstance();
      this._fastDormancy.setFastDormancy(true);
      this._dataRecovery = DataRecovery.getInstance();
      this._wtProfiles = new WirelessTransportProfile[6];
      ProtocolDaemon pd = ProtocolDaemon.getInstance();
      if (pd != null) {
         pd.addRadioListener(this);
         pd.addSystemListener(this);
      }

      this._rxTable = new MdpReceiveTable();
      this._txTable = new MdpSendTable();
      this._mfhOptimizedAcksFlag = PersistentInteger.get(MFH_OPTIMIZED_ACKS_FLAG_PERSISTENT_ID) != 0;
      ProvisioningService provService = ProvisioningService.getInstance();
      if (provService != null) {
         provService.addHandler(this);
      }

      this._maxPacketsOutstanding = MdpMFHUtil.getDefaultMFHOutstandingPacketsWindowSize();
      EventLogger.logEvent(super.GUID, 1229878386, 0);
   }

   private final int getBackoffTmo(int index) {
      int ret = (1 << index / 2) * this._backoffStart;
      return ret <= this._backoffMax ? ret : this._backoffMax;
   }

   private final void invokeFastDormancy(IOProperties properties, DatagramAddressBase nativeAddress) {
      if (this._fastDormancy.getFastDormancy()) {
         if (properties == null || !properties.isFlagSet(2048)) {
            if (nativeAddress instanceof Object) {
               String apn = ((UdpAddress)nativeAddress).getApn();

               try {
                  int apnId = RadioInfo.getAccessPointNumber(apn);
                  RadioInternal.setFastDormancy(apnId, true);
               } finally {
                  return;
               }
            }
         }
      }
   }

   private final void failDatagram(int reference) {
      EventLogger.logEvent(super.GUID, 1413836129, 2);
      this.xmitDgslEvent(this._txListener, this._txDgramId, 8321, null);
      this._dataRecovery.fileReport(1);
      this.cancelPacketTimers(reference);
      throw new Object();
   }

   @Override
   public final void cancel(Datagram datagram) {
      synchronized (this._sendChokeLock) {
         if (datagram == this._txDatagram) {
            this._txDatagram = null;
            super._subConnection.cancel(this._txPacket);
            this.clearSendStatus();
         }
      }
   }

   private final void sendPing(DatagramAddressBase nativeAddress, boolean ackFlag) {
      EventLogger.logEvent(super.GUID, 1414557801, 0);

      try {
         DatagramBase dgram = (DatagramBase)super._subConnection.newDatagram();
         MdpUtil$DatagramInfo info = MdpUtil.makeDatagramInfo(1);
         info.moreFlag = ackFlag;
         MdpUtil.encode(dgram, info);
         dgram.setAddressBase(super._subConnection.newDatagramAddressBase(nativeAddress, true));
         this.addSendRequest(super._subConnection, dgram);
      } finally {
         EventLogger.logEvent(super.GUID, 1413834337, 2);
         return;
      }
   }

   private final void sendPacketAck(DatagramAddressBase nativeAddress, int reference, int sequence, boolean eAckFlag) {
      EventLogger.logEvent(super.GUID, 1414557793, 4);

      try {
         DatagramBase dgram = (DatagramBase)super._subConnection.newDatagram();
         MdpUtil$DatagramInfo info = MdpUtil.makeDatagramInfo(4);
         info.reference = reference;
         info.sequence = sequence;
         info.extAckFlag = eAckFlag;
         MdpUtil.encode(dgram, info);
         dgram.setAddressBase(super._subConnection.newDatagramAddressBase(nativeAddress, true));
         dgram.setFlag(8, this._fastDormancy.getFastDormancy());
         this.addSendRequest(super._subConnection, dgram);
      } finally {
         EventLogger.logEvent(super.GUID, 1413834337, 2);
         return;
      }
   }

   private final void sendDatagramAck(DatagramAddressBase nativeAddress, int reference, boolean eAckFlag) {
      EventLogger.logEvent(super.GUID, 1414554721, 4);

      try {
         DatagramBase dgram = (DatagramBase)super._subConnection.newDatagram();
         MdpUtil$DatagramInfo info = MdpUtil.makeDatagramInfo(3);
         info.reference = reference;
         info.extAckFlag = eAckFlag;
         MdpUtil.encode(dgram, info);
         dgram.setAddressBase(super._subConnection.newDatagramAddressBase(nativeAddress, true));
         dgram.setFlag(8, this._fastDormancy.getFastDormancy());
         this.addSendRequest(super._subConnection, dgram);
      } finally {
         EventLogger.logEvent(super.GUID, 1413834337, 2);
         return;
      }
   }

   private final void sendRefuse(DatagramAddressBase nativeAddress, int type, int reference) {
      EventLogger.logEvent(super.GUID, 1414558310, 3);

      try {
         DatagramBase dgram = (DatagramBase)super._subConnection.newDatagram();
         MdpUtil$DatagramInfo info = MdpUtil.makeDatagramInfo(type);
         info.reference = reference;
         MdpUtil.encode(dgram, info);
         dgram.setAddressBase(super._subConnection.newDatagramAddressBase(nativeAddress, true));
         this.addSendRequest(super._subConnection, dgram);
      } finally {
         EventLogger.logEvent(super.GUID, 1413834337, 2);
         return;
      }
   }

   private final void sendStatus(DatagramAddressBase nativeAddress, int reference, boolean ackFlag, boolean swapAddress) {
      EventLogger.logEvent(super.GUID, 1414558580, 4);

      try {
         DatagramBase dgram = (DatagramBase)super._subConnection.newDatagram();
         MdpUtil$DatagramInfo info = MdpUtil.makeDatagramInfo(7);
         info.reference = reference;
         info.packetAckFlag = ackFlag;
         MdpUtil.encode(dgram, info);
         dgram.setAddressBase(super._subConnection.newDatagramAddressBase(nativeAddress, swapAddress));
         this.addSendRequest(super._subConnection, dgram);
      } finally {
         EventLogger.logEvent(super.GUID, 1413834337, 2);
         return;
      }
   }

   private final void sendReceipt(DatagramAddressBase nativeAddress, int reference, byte[] statusArray) {
      EventLogger.logEvent(super.GUID, 1414558307, 4);
      if (statusArray == null) {
         int datagramKey = MdpReceiveTable.calculateDatagramKey(nativeAddress, reference);
         MdpRxDatagram mdpDatagram = this._rxTable.findAssemblyDatagram(datagramKey);
         if (mdpDatagram == null) {
            mdpDatagram = this._rxTable.findConfirmationDatagram(datagramKey);
            if (mdpDatagram == null) {
               return;
            }
         }

         statusArray = mdpDatagram.makeStatusArray();
      }

      try {
         DatagramBase dgram = (DatagramBase)super._subConnection.newDatagram();
         MdpUtil$DatagramInfo info = MdpUtil.makeDatagramInfo(8);
         info.reference = reference;
         info.data = statusArray;
         info.offset = 0;
         info.length = statusArray != null ? statusArray.length : 0;
         MdpUtil.encode(dgram, info);
         dgram.setAddressBase(super._subConnection.newDatagramAddressBase(nativeAddress, true));
         if (RadioInfo.getNetworkType() != 7) {
            dgram.setFlag(8, this._fastDormancy.getFastDormancy());
         }

         this.addSendRequest(super._subConnection, dgram);
      } finally {
         EventLogger.logEvent(super.GUID, 1413834337, 2);
         return;
      }
   }

   private final void addWirelessTransportProfile(DatagramAddressBase key, DatagramAddressBase reverseKey, WirelessTransportProfile profile) {
      if (key != null && reverseKey != null && profile != null) {
         if (this._wtProfilesCount == 6) {
            this._wtProfilesCount--;
            WirelessTransportProfile wt = this._wtProfiles[this._wtProfilesCount];
            if (wt != null) {
               this.sendReceipts(wt, true);
               wt.cleanUp(true);
            }
         }

         for (int i = this._wtProfilesCount; i > 0; i--) {
            this._wtProfiles[i] = this._wtProfiles[i - 1];
         }

         profile._key = key;
         profile._reverseKey = reverseKey;
         profile._wtAddress = !(key instanceof Object) ? 0 : ((UdpInternalAddress)key).getGpakHostAddress();
         this._wtProfiles[0] = profile;
         this._wtProfilesCount++;
      }
   }

   private final WirelessTransportProfile getWirelessTransportProfile(DatagramAddressBase key, boolean directKey) {
      if (key != null) {
         boolean byWtAddress = directKey && key instanceof Object;
         int wtAddress = byWtAddress ? ((UdpInternalAddress)key).getGpakHostAddress() : 0;

         for (int i = this._wtProfilesCount - 1; i >= 0; i--) {
            if (this._wtProfiles[i] != null) {
               if (byWtAddress) {
                  if (wtAddress == this._wtProfiles[i]._wtAddress && key.equals(directKey ? this._wtProfiles[i]._key : this._wtProfiles[i]._reverseKey)) {
                     return this._wtProfiles[i];
                  }
               } else if (key.equals(directKey ? this._wtProfiles[i]._key : this._wtProfiles[i]._reverseKey)) {
                  return this._wtProfiles[i];
               }
            }
         }
      }

      return null;
   }

   private final void removeWirelessTransportProfile(DatagramAddressBase key, boolean removeAll) {
      if (key != null || removeAll) {
         boolean byWtAddress = key instanceof Object;
         int wtAddress = byWtAddress ? ((UdpInternalAddress)key).getGpakHostAddress() : 0;
         WirelessTransportProfile temp = null;

         for (int i = this._wtProfilesCount - 1; i >= 0; i--) {
            temp = this._wtProfiles[i];
            if (removeAll || temp != null && (byWtAddress ? temp._wtAddress == wtAddress && key.equals(temp._key) : key.equals(temp._key))) {
               this.sendReceipts(temp, true);
               temp.cleanUp(true);
               this._wtProfilesCount--;
               this._wtProfiles[i] = this._wtProfiles[this._wtProfilesCount];
               this._wtProfiles[this._wtProfilesCount] = null;
               if (!removeAll) {
                  return;
               }
            }
         }
      }
   }

   static final Transport getInstance() {
      if (_instance == null) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         _instance = (Transport)ar.getOrWaitFor(4175103112525226476L);
      }

      return _instance;
   }

   @Override
   public final int getMaximumLength() {
      return MdpUtil.getMaximumLength(this._maxNativeLength);
   }

   @Override
   protected final synchronized int getNextDatagramId(DatagramBase dgram) {
      if (++this._nextDgramId == 127) {
         this._nextDgramId = 1;
      }

      return this._nextDgramId;
   }

   @Override
   protected final void processReceivedDatagram(Datagram datagram) {
      EventLogger.logEvent(super.GUID, 1381528436, 4);
      DatagramAddressBase nativeAddress = null;
      if (datagram instanceof Object) {
         nativeAddress = ((DatagramBase)datagram).getAddressBase();
      }

      if (nativeAddress == null) {
         nativeAddress = super._subConnection.newDatagramAddressBase(datagram.getAddress(), false);
      }

      this._dataRecovery.fileReport(0);
      MdpUtil$DatagramInfo info = MdpUtil.decode(datagram.getData(), datagram.getOffset(), datagram.getLength());
      if (info == null) {
         EventLogger.logEvent(super.GUID, 1380279919, 3);
      } else {
         switch (info.type) {
            case -4:
            case -2:
            case 0:
            case 9:
            case 11:
            case 12:
            case 13:
               this.sendRefuse(nativeAddress, 6, info.reference);
               return;
            case -3:
               this.sendRefuse(nativeAddress, 10, info.reference);
               return;
            case -1:
               EventLogger.logEvent(super.GUID, 1381004651, 3);
               if (info.packetAckFlag) {
                  this.sendPacketAck(nativeAddress, info.reference, info.sequence, info.extAckFlag);
               }

               return;
            case 1:
            default:
               this.processPing(nativeAddress, info.moreFlag);
               return;
            case 2:
               this.processData(nativeAddress, info);
               return;
            case 3:
               this.processDatagramAck(nativeAddress, info);
               return;
            case 4:
               this.processPacketAck(nativeAddress, info);
               return;
            case 5:
               this.processAbort(nativeAddress, info);
               return;
            case 6:
            case 10:
               this.processRefuse(nativeAddress, info);
               return;
            case 7:
               this.processStatus(nativeAddress, info);
               return;
            case 8:
               this.processReceipt(nativeAddress, info);
               return;
            case 14:
               this.processParam(nativeAddress, info);
         }
      }
   }

   private final void processPing(DatagramAddressBase nativeAddress, boolean ackFlag) {
      EventLogger.logEvent(super.GUID, 1381003369, 0);
      if (ackFlag) {
         this.sendPing(nativeAddress, false);
      }
   }

   private final void processDatagramAck(DatagramAddressBase addressBase, MdpUtil$DatagramInfo info) {
      EventLogger.logEvent(super.GUID, 1381000289, 4);
      this.queueSendStatus(info.reference, 1, null);
      this.queueWaitingDatagramStatus(info.reference, 1, null);
      if (!info.moreFlag && this._fastDormancy.getFastDormancy() && addressBase instanceof Object) {
         String apn = ((UdpAddress)addressBase).getApn();

         try {
            int apnId = RadioInfo.getAccessPointNumber(apn);
            RadioInternal.setFastDormancy(apnId, true);
         } finally {
            return;
         }
      }
   }

   private final void processPacketAck(DatagramAddressBase addressBase, MdpUtil$DatagramInfo info) {
      EventLogger.logEvent(super.GUID, 1381003361, 4);
      this.queueSendStatus(info.reference | info.sequence << 8, 2, null);
      if (!info.moreFlag && this._fastDormancy.getFastDormancy() && addressBase instanceof Object) {
         String apn = ((UdpAddress)addressBase).getApn();

         try {
            int apnId = RadioInfo.getAccessPointNumber(apn);
            RadioInternal.setFastDormancy(apnId, true);
         } finally {
            return;
         }
      }
   }

   private final void processAbort(DatagramAddressBase nativeAddress, MdpUtil$DatagramInfo info) {
      EventLogger.logEvent(super.GUID, 1380999522, 3);
      synchronized (this._rxTable) {
         int datagramKey = MdpReceiveTable.calculateDatagramKey(nativeAddress, info.reference);
         this._rxTable.removeAssemblyDatagram(datagramKey);

         label43:
         try {
            WirelessTransportProfile wtProfile = this.getWirelessTransportProfile(nativeAddress, true);
            if (wtProfile != null && !wtProfile._legacyMode) {
               this.updateWindowArrays(wtProfile, datagramKey, (byte)2);
            }
         } finally {
            break label43;
         }
      }

      if (info.packetAckFlag) {
         this.sendPacketAck(nativeAddress, info.reference, info.sequence, info.extAckFlag);
      }
   }

   private final void processRefuse(DatagramAddressBase nativeAddress, MdpUtil$DatagramInfo info) {
      EventLogger.logEvent(super.GUID, 1381003878, 3);
      int status;
      int event;
      if (info.type == 10) {
         status = 256;
         event = 8338;
      } else {
         status = 512;
         event = 8337;
      }

      int index = this.lookupDgramIndexFromDgramId(info.reference);
      if (index >= 0) {
         this.queueSendStatus(info.reference, status, null);
         this.queueWaitingDatagramStatus(info.reference, status, null);
         this.forwardDgslEvent(index, event, null);
      }

      if (info.packetAckFlag) {
         this.sendPacketAck(nativeAddress, info.reference, info.sequence, info.extAckFlag);
      }
   }

   private final void processStatus(DatagramAddressBase nativeAddress, MdpUtil$DatagramInfo info) {
      EventLogger.logEvent(super.GUID, 1381004148, 4);
      synchronized (this._rxTable) {
         this.sendReceipt(nativeAddress, info.reference, null);
      }

      if (info.packetAckFlag) {
         this.sendPacketAck(nativeAddress, info.reference, info.sequence, info.extAckFlag);
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void processParam(DatagramAddressBase nativeAddress, MdpUtil$DatagramInfo info) {
      EventLogger.logEvent(super.GUID, 1381003378, 0);
      DataBuffer buf = (DataBuffer)(new Object(info.data, info.offset, info.length, true));
      int maxAckDelayTimeOut = -1;
      int maxWindowSize = -1;
      int datagramAckTimeout = -1;
      int flags = -1;
      int requestPeriod = 0;
      boolean valid = true;
      boolean var29 = false /* VF: Semaphore variable */;

      label579:
      try {
         var29 = true;
         byte ioe = 0;
         int wtProfile = 0;

         label575:
         while (true) {
            label573:
            while (true) {
               if (buf.eof()) {
                  var29 = false;
                  break label575;
               }

               ioe = buf.readByte();
               wtProfile = buf.readCompressedInt();
               boolean var35 = false /* VF: Semaphore variable */;
               boolean var41 = false /* VF: Semaphore variable */;

               try {
                  label569:
                  try {
                     var41 = true;
                     var35 = true;
                     switch (ioe) {
                        case 0:
                           buf.skipBytes(wtProfile);
                           var35 = false;
                           var41 = false;
                           break label573;
                        case 1:
                        default:
                           maxAckDelayTimeOut = TLEUtilities.readIntegerFieldWithLength(buf, wtProfile);
                           if (maxAckDelayTimeOut < 0) {
                              valid = false;
                              var35 = false;
                              var41 = false;
                           } else {
                              var35 = false;
                              var41 = false;
                           }
                           break label573;
                        case 2:
                           maxWindowSize = TLEUtilities.readIntegerFieldWithLength(buf, wtProfile);
                           if (maxWindowSize < 0) {
                              valid = false;
                              var35 = false;
                              var41 = false;
                           } else {
                              var35 = false;
                              var41 = false;
                           }
                           break label573;
                        case 3:
                           datagramAckTimeout = TLEUtilities.readIntegerFieldWithLength(buf, wtProfile);
                           if (datagramAckTimeout < 0) {
                              valid = false;
                              var35 = false;
                              var41 = false;
                           } else {
                              datagramAckTimeout *= 1000;
                              if (datagramAckTimeout == 0) {
                                 datagramAckTimeout = 1000;
                                 var35 = false;
                                 var41 = false;
                              } else {
                                 var35 = false;
                                 var41 = false;
                              }
                           }
                           break label573;
                        case 4:
                           requestPeriod = TLEUtilities.readIntegerFieldWithLength(buf, wtProfile);
                           if (requestPeriod < 0) {
                              valid = false;
                              var35 = false;
                              var41 = false;
                           } else {
                              requestPeriod *= 1000;
                              var35 = false;
                              var41 = false;
                           }
                           break label573;
                        case 5:
                           flags = TLEUtilities.readIntegerFieldWithLength(buf, wtProfile);
                           var35 = false;
                           var41 = false;
                           break label573;
                     }
                  } finally {
                     if (var41) {
                        valid = false;
                        buf.skipBytes(wtProfile);
                        var35 = false;
                        break label569;
                     }
                  }
               } finally {
                  if (var35) {
                     if (!valid) {
                        EventLogger.logEvent(super.GUID, 1380279919, 3);
                     }
                  }
               }

               if (!valid) {
                  EventLogger.logEvent(super.GUID, 1380279919, 3);
               }
            }

            if (!valid) {
               EventLogger.logEvent(super.GUID, 1380279919, 3);
            }
         }
      } finally {
         if (var29) {
            EventLogger.logEvent(super.GUID, 1380279919, 3);
            valid = false;
            break label579;
         }
      }

      synchronized (this._rxTable) {
         WirelessTransportProfile wtProfile = this.getWirelessTransportProfile(nativeAddress, true);
         boolean existInWirelessProfilesTable = wtProfile != null;
         if (valid) {
            if (!existInWirelessProfilesTable) {
               wtProfile = new WirelessTransportProfile();
            }

            wtProfile._maxAckDelayTimeOut = maxAckDelayTimeOut;
            if (wtProfile._maxAckDelayTimeOut > 0 && wtProfile._delayTimerThread != null) {
               wtProfile._delayTimerThread.setTimeout(wtProfile._maxAckDelayTimeOut);
            }

            int size = wtProfile._windowDatagramsKeys == null ? wtProfile._maxWindowSize : wtProfile._windowDatagramsKeys.length;
            if (size < maxWindowSize || maxWindowSize == 0) {
               size = maxWindowSize;
            }

            int[] windowDatagramsKeys = null;
            byte[] windowDatagramsPackets = null;
            if (size <= 0) {
               this.sendReceipts(wtProfile, true);
            } else {
               boolean var23 = false /* VF: Semaphore variable */;

               label541:
               try {
                  var23 = true;
                  windowDatagramsKeys = new int[size];
                  windowDatagramsPackets = new byte[size];
                  if (wtProfile._windowDatagramsKeys != null) {
                     if (wtProfile._windowDatagramsPackets != null) {
                        System.arraycopy(wtProfile._windowDatagramsKeys, 0, windowDatagramsKeys, 0, wtProfile._windowDatagramsKeys.length);
                        System.arraycopy(wtProfile._windowDatagramsPackets, 0, windowDatagramsPackets, 0, wtProfile._windowDatagramsPackets.length);
                        var23 = false;
                     } else {
                        var23 = false;
                     }
                  } else {
                     var23 = false;
                  }
               } finally {
                  if (var23) {
                     windowDatagramsKeys = wtProfile._windowDatagramsKeys;
                     windowDatagramsPackets = wtProfile._windowDatagramsPackets;
                     maxWindowSize = wtProfile._maxWindowSize;
                     break label541;
                  }
               }
            }

            wtProfile._windowDatagramsKeys = windowDatagramsKeys;
            wtProfile._windowDatagramsPackets = windowDatagramsPackets;
            wtProfile._maxWindowSize = maxWindowSize;
            wtProfile._legacyMode = maxWindowSize <= 0 || windowDatagramsKeys == null || windowDatagramsPackets == null;
            wtProfile._mfhLegacyMode = wtProfile._legacyMode || datagramAckTimeout <= 0;
            wtProfile._datagramAckTimeout = wtProfile._mfhLegacyMode ? -1 : datagramAckTimeout;
            wtProfile._maxOutstandingWindowSize = wtProfile._mfhLegacyMode
               ? MdpMFHUtil.getDefaultMFHOutstandingPacketsWindowSize()
               : wtProfile._maxWindowSize + (wtProfile._maxWindowSize << 1) / 3;
            wtProfile._flags = wtProfile._legacyMode ? -1 : flags;
            if (wtProfile._legacyMode) {
               wtProfile._requestPeriod = 0;
            }

            if (requestPeriod > 0) {
               requestPeriod = Math.max(this._requestPeriodThreshold, requestPeriod) / 60000;
            }

            wtProfile._timer = 0;
            wtProfile._requestPeriod = requestPeriod;
         }

         if (wtProfile != null) {
            if (wtProfile._legacyMode) {
               this.sendReceipts(wtProfile, true);
            } else if (!existInWirelessProfilesTable) {
               this.addWirelessTransportProfile(
                  super._subConnection.newDatagramAddressBase(nativeAddress, false),
                  super._subConnection.newDatagramAddressBase(nativeAddress, true),
                  wtProfile
               );
            }

            wtProfile.cleanUp(wtProfile._legacyMode);
         }
      }

      if (info.packetAckFlag) {
         this.sendPacketAck(nativeAddress, info.reference, info.sequence, info.extAckFlag);
      }
   }

   private final void processReceipt(DatagramAddressBase nativeAddress, MdpUtil$DatagramInfo info) {
      EventLogger.logEvent(super.GUID, 1381528163, 4);
      if (info.length < 1) {
         EventLogger.logEvent(super.GUID, 1381528163, 2);
      } else {
         if (info.length > 16) {
            EventLogger.logEvent(super.GUID, 1381528163, 3);
         }

         this.queueSendStatus(info.reference, 16, info);
         this.queueWaitingDatagramStatus(info.reference, 16, info);
         if (info.packetAckFlag) {
            this.sendPacketAck(nativeAddress, info.reference, info.sequence, info.extAckFlag);
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void processData(DatagramAddressBase subAddress, MdpUtil$DatagramInfo info) {
      int reference = info.reference;
      int sequence = info.sequence;
      if (sequence != 0 && sequence > info.maxSequence) {
         this.sendRefuse(subAddress, 6, reference);
      } else {
         byte[] data = info.data;
         int offset = info.offset;
         int length = info.length;
         int datagramKey = MdpReceiveTable.calculateDatagramKey(subAddress, reference);
         int crc = CRC32.update(-1, data, offset, length);
         DatagramBase dgram = null;
         WirelessTransportProfile wtProfile = null;
         boolean legacyMode = true;
         synchronized (this._rxTable) {
            if (info.optimizedAckFlag || this._wtProfilesCount > 0) {
               wtProfile = this.getWirelessTransportProfile(subAddress, true);
               if (wtProfile == null) {
                  if (info.optimizedAckFlag) {
                     wtProfile = new WirelessTransportProfile();
                     this.addWirelessTransportProfile(
                        super._subConnection.newDatagramAddressBase(subAddress, false),
                        super._subConnection.newDatagramAddressBase(subAddress, true),
                        wtProfile
                     );
                     legacyMode = wtProfile._legacyMode;
                  }
               } else {
                  legacyMode = wtProfile._legacyMode;
               }

               if (legacyMode && wtProfile != null && info.optimizedAckFlag) {
                  wtProfile.requestTimerRunnable(600000, 8, subAddress);
               }
            }

            int duplicate = this._rxTable.findDuplicateDatagram(datagramKey, sequence, crc, info.maxSequence);
            if (duplicate >= 0) {
               EventLogger.logEvent(super.GUID, info.maxSequence == 0 ? 1380213877 : 1380216932, 3);
               byte flags = this._rxTable.getDuplicateDatagramFlags(duplicate);
               boolean datagramAckFlag = (flags & 1) == 1;
               boolean optimizedAckFlag = (flags & 2) == 2;
               if ((legacyMode || !optimizedAckFlag) && info.packetAckFlag) {
                  this.sendPacketAck(subAddress, reference, sequence, info.extAckFlag);
               }

               if (datagramAckFlag || !legacyMode && optimizedAckFlag) {
                  if (!legacyMode && optimizedAckFlag && wtProfile._windowCount + 1 >= wtProfile._maxWindowSize) {
                     this.sendReceipts(wtProfile, false);
                  }

                  this.sendDatagramAck(subAddress, reference, info.extAckFlag);
               }

               return;
            }

            MdpRxDatagram confirming = this._rxTable.findConfirmationDatagram(datagramKey);
            if (!legacyMode) {
               wtProfile._windowCount++;
               int[] windowDatagramsKeys = wtProfile._windowDatagramsKeys;
               byte[] windowDatagramsPackets = wtProfile._windowDatagramsPackets;
               if (windowDatagramsKeys != null && windowDatagramsPackets != null) {
                  int size = windowDatagramsKeys.length;
                  int incremented = -2;

                  for (int i = size - 1; i >= 0; i--) {
                     if (windowDatagramsKeys[i] != 0) {
                        if (datagramKey == windowDatagramsKeys[i]) {
                           if (confirming == null) {
                              windowDatagramsPackets[i]++;
                           } else {
                              wtProfile._windowCount--;
                           }

                           incremented = -1;
                           break;
                        }
                     } else {
                        incremented = i;
                     }
                  }

                  if (incremented == -2) {
                     EventLogger.logEvent(super.GUID, 1381461093, 4);
                     int[] tmpWindowDatagramsKeys = new int[size + 1];
                     byte[] tmpWindowDatagramsPackets = new byte[size + 1];
                     if (tmpWindowDatagramsKeys != null && tmpWindowDatagramsPackets != null) {
                        System.arraycopy(windowDatagramsKeys, 0, tmpWindowDatagramsKeys, 0, size);
                        System.arraycopy(windowDatagramsPackets, 0, tmpWindowDatagramsPackets, 0, size);
                        windowDatagramsKeys = tmpWindowDatagramsKeys;
                        wtProfile._windowDatagramsKeys = tmpWindowDatagramsKeys;
                        windowDatagramsPackets = tmpWindowDatagramsPackets;
                        wtProfile._windowDatagramsPackets = tmpWindowDatagramsPackets;
                        incremented = size++;
                     } else {
                        EventLogger.logEvent(super.GUID, 1381461093, 2);
                        this.sendReceipts(wtProfile, true);
                        incremented = 0;
                        wtProfile._windowCount++;
                     }
                  }

                  if (incremented >= 0 && incremented < size) {
                     windowDatagramsKeys[incremented] = datagramKey;
                     windowDatagramsPackets[incremented] = 1;
                  }
               }

               if (wtProfile._maxAckDelayTimeOut > 0) {
                  if (wtProfile._delayTimerThread == null) {
                     wtProfile._delayTimerThread = new MDPMaxDelayTimeoutThread(wtProfile);
                     wtProfile._delayTimerThread.setTimeout(wtProfile._maxAckDelayTimeOut);
                     boolean var41 = false /* VF: Semaphore variable */;

                     label964:
                     try {
                        var41 = true;
                        ProtocolDaemon.getInstance().startThread(wtProfile._delayTimerThread);
                        var41 = false;
                     } finally {
                        if (var41) {
                           EventLogger.logEvent(super.GUID, 1414022514, 3);
                           break label964;
                        }
                     }
                  }

                  if (!wtProfile._delayTimerThread.isTimerRunning()) {
                     wtProfile._delayTimerThread.startTimer();
                  }
               }
            }

            if (confirming != null) {
               EventLogger.logEvent(super.GUID, info.maxSequence == 0 ? 1380213615 : 1381000047, 3);
               return;
            }

            MdpAddress upAddress;
            MdpRxDatagram upDatagram;
            if (info.maxSequence == 0) {
               upAddress = new MdpAddress(subAddress, info.destPort, info.srcPort);
               upDatagram = new MdpRxDatagram(subAddress, reference, datagramKey, info.maxSequence);
               upDatagram._datagramAckRequired = info.datagramAckFlag;
               upDatagram._optimizedAckFlag = info.optimizedAckFlag;
               upDatagram._crc = crc;
               upDatagram._packetAckRequired = info.packetAckFlag;
               upDatagram._extAckRequired = info.extAckFlag;
               this._rxTable.updateAssemblyDatagrams();
            } else {
               boolean receiptConfirmSent = false;
               upDatagram = this._rxTable.findAssemblyDatagram(datagramKey);
               if (upDatagram != null) {
                  if (info.maxSequence + 1 == upDatagram._crcs.length && upDatagram._crcs[sequence] == -1) {
                     if (sequence == 0 && (!legacyMode && info.optimizedAckFlag || GmeUtil.readContentIdInt(data, offset, length) == 3)) {
                        EventLogger.logEvent(super.GUID, 1380218222, 4);
                        upDatagram._unreliable = true;
                        this._rxTable.makeAssemblyDatagramUnreliable(datagramKey, upDatagram);
                     }
                  } else {
                     if (info.maxSequence + 1 == upDatagram._crcs.length && upDatagram._crcs[sequence] == crc) {
                        EventLogger.logEvent(super.GUID, 1380216932, 3);
                        if (info.packetAckFlag && (!upDatagram._optimizedAckFlag || legacyMode)) {
                           this.sendPacketAck(subAddress, reference, sequence, info.extAckFlag);
                        }

                        if (!legacyMode) {
                           if (upDatagram._optimizedAckFlag) {
                              this.sendReceipt(subAddress, reference, upDatagram.makeStatusArray());
                              this.updateWindowArrays(wtProfile, datagramKey, (byte)2);
                           } else {
                              this.updateWindowArrays(wtProfile, datagramKey, (byte)1);
                           }
                        }

                        return;
                     }

                     EventLogger.logEvent(super.GUID, 1380217453, 3);
                     boolean var35 = false /* VF: Semaphore variable */;

                     try {
                        var35 = true;
                        upDatagram = new MdpRxDatagram(subAddress, reference, datagramKey, info.maxSequence);
                        upDatagram._buffer = (DataBuffer)(new Object(MdpUtil.getLengthFromMaxSequence(info.maxSequence, this._maxNativeLength), true));
                        var35 = false;
                     } finally {
                        if (var35) {
                           this.sendRefuse(subAddress, 6, reference);
                           if (!legacyMode) {
                              this.updateWindowArrays(wtProfile, datagramKey, (byte)1);
                           }

                           return;
                        }
                     }

                     if (sequence != 0 || (legacyMode || !info.optimizedAckFlag) && GmeUtil.readContentIdInt(data, offset, length) != 3) {
                        upDatagram.stringDatagramAddress();
                     } else {
                        EventLogger.logEvent(super.GUID, 1380218222, 4);
                        upDatagram._unreliable = true;
                     }

                     this._rxTable.removeAssemblyDatagram(datagramKey);
                     this._rxTable.updateAssemblyDatagrams();
                     this._rxTable.addAssemblyDatagram(datagramKey, upDatagram);
                     if (!legacyMode) {
                        if (info.optimizedAckFlag) {
                           upDatagram._crcs[sequence] = crc;
                           this.sendReceipt(subAddress, reference, upDatagram.makeStatusArray());
                           this.updateWindowArrays(wtProfile, datagramKey, (byte)2);
                           receiptConfirmSent = true;
                        } else {
                           this.updateWindowArrays(wtProfile, datagramKey, (byte)5);
                        }
                     }
                  }
               } else {
                  boolean var29 = false /* VF: Semaphore variable */;

                  try {
                     var29 = true;
                     upDatagram = new MdpRxDatagram(subAddress, reference, datagramKey, info.maxSequence);
                     upDatagram._buffer = (DataBuffer)(new Object(MdpUtil.getLengthFromMaxSequence(info.maxSequence, this._maxNativeLength), true));
                     var29 = false;
                  } finally {
                     if (var29) {
                        this.sendRefuse(subAddress, 6, reference);
                        if (!legacyMode) {
                           this.updateWindowArrays(wtProfile, datagramKey, (byte)1);
                        }

                        return;
                     }
                  }

                  if (sequence != 0 || (legacyMode || !info.optimizedAckFlag) && GmeUtil.readContentIdInt(data, offset, length) != 3) {
                     upDatagram.stringDatagramAddress();
                  } else {
                     EventLogger.logEvent(super.GUID, 1380218222, 4);
                     upDatagram._unreliable = true;
                  }

                  this._rxTable.updateAssemblyDatagrams();
                  this._rxTable.addAssemblyDatagram(datagramKey, upDatagram);
               }

               if (sequence == 0) {
                  upDatagram._datagramAckRequired = info.datagramAckFlag;
                  upDatagram._optimizedAckFlag = info.optimizedAckFlag;
                  upDatagram._srcPort = info.srcPort;
                  upDatagram._destPort = info.destPort;
               }

               upDatagram._crcs[sequence] = crc;
               int start = MdpUtil.getOffsetFromSequence(sequence, this._maxNativeLength);
               upDatagram._buffer.ensureLength(start + 1);
               upDatagram._buffer.setPosition(start);
               upDatagram._buffer.write(data, offset, length);
               if (sequence == info.maxSequence) {
                  upDatagram._buffer.setLength(upDatagram._buffer.getPosition());
               }

               if (upDatagram.waitingForPackets()) {
                  EventLogger.logEvent(super.GUID, 1381005153, 4);
                  upDatagram._crc--;
                  if (!upDatagram._unreliable) {
                     this._rxTable.commitAssemblyDatagrams();
                  }

                  if (info.packetAckFlag) {
                     if (!upDatagram._optimizedAckFlag || legacyMode) {
                        this.sendPacketAck(subAddress, reference, sequence, info.extAckFlag);
                     } else if (!info.moreFlag) {
                        this.sendReceipt(subAddress, reference, upDatagram.makeStatusArray());
                        this.updateWindowArrays(wtProfile, datagramKey, (byte)2);
                        receiptConfirmSent = true;
                     }
                  }

                  if (!legacyMode) {
                     if (wtProfile._windowCount >= wtProfile._maxWindowSize) {
                        this.sendReceipts(wtProfile, false);
                     } else if (wtProfile.isSet(1)
                        && !receiptConfirmSent
                        && (upDatagram._optimizedAckFlag || !upDatagram.packetReceived(0))
                        && sequence - upDatagram._lastSequence > 1) {
                        EventLogger.logEvent(super.GUID, 1414296163, 4);
                        this.sendReceipt(subAddress, reference, upDatagram.makeStatusArray());
                        this.updateWindowArrays(wtProfile, datagramKey, (byte)2);
                     }

                     if (sequence > upDatagram._lastSequence) {
                        upDatagram._lastSequence = sequence;
                     }
                  }

                  return;
               }

               upDatagram._packetAckRequired = info.packetAckFlag;
               upDatagram._extAckRequired = info.extAckFlag;
               upDatagram._sequence = sequence;
               upDatagram._lastSequence = info.maxSequence;
               data = upDatagram._buffer.getArray();
               offset = upDatagram._buffer.getArrayStart();
               length = upDatagram._buffer.getLength();
               upAddress = new MdpAddress(subAddress, upDatagram._destPort, upDatagram._srcPort);
               this._rxTable.removeAssemblyDatagram(datagramKey);
            }

            dgram = (DatagramBase)(new Object(data, offset, length, upAddress));
            dgram.setDatagramId(datagramKey);
            this._rxTable.addConfirmationDatagram(datagramKey, upDatagram);
            if (!legacyMode && upDatagram._optimizedAckFlag) {
               upDatagram._datagramAckRequired = true;
               this.updateWindowArrays(wtProfile, datagramKey, (byte)4);
               upDatagram._wtProfile = wtProfile;
            }
         }

         EventLogger.logEvent(super.GUID, 1381527669, 5);
         if (!this.passUpDatagram(dgram)) {
            EventLogger.logEvent(super.GUID, 1381527152, 3);
            this.datagramProcessed(datagramKey);
         }

         synchronized (this._rxTable) {
            if (!legacyMode && wtProfile._windowCount >= wtProfile._maxWindowSize) {
               this.sendReceipts(wtProfile, false);
            }
         }
      }
   }

   @Override
   public final int getNominalLength() {
      return this._maxNativeLength - 8;
   }

   @Override
   public final void datagramProcessed(int datagramId) {
      synchronized (this._rxTable) {
         MdpRxDatagram dgram = this._rxTable.findConfirmationDatagram(datagramId);
         if (dgram != null) {
            DatagramAddressBase subAddress;
            if (!(dgram._subAddress instanceof Object)) {
               subAddress = new MdpAddress((String)dgram._subAddress, dgram._destPort, dgram._srcPort, dgram._wtAddress).getSubAddressBase();
            } else {
               subAddress = (DatagramAddressBase)dgram._subAddress;
            }

            WirelessTransportProfile wtProfile = (WirelessTransportProfile)dgram._wtProfile;
            boolean legacyMode = wtProfile == null || wtProfile._legacyMode;
            if ((legacyMode || !dgram._optimizedAckFlag) && dgram._packetAckRequired) {
               this.sendPacketAck(subAddress, dgram._reference, dgram._sequence, dgram._extAckRequired);
            }

            if (dgram._datagramAckRequired || !legacyMode && dgram._optimizedAckFlag) {
               if (!legacyMode && dgram._optimizedAckFlag) {
                  this.updateWindowArrays(wtProfile, datagramId, (byte)3);
               }

               this.sendDatagramAck(subAddress, dgram._reference, dgram._extAckRequired);
            }

            dgram._wtProfile = null;
            this._rxTable.removeConfirmationDatagram(datagramId);
            this._rxTable.addDuplicateDatagram(datagramId, dgram._reference, dgram._crc, dgram._crcs, dgram._datagramAckRequired, dgram._optimizedAckFlag);
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void addWaitingDatagram(
      Datagram datagram, DatagramStatusListener listener, DatagramAddressBase nativeAddress, int reference, int retriesCount, int datagramAckTimeout
   ) {
      if (this._mdpWaitingQueueThread == null) {
         this._mdpWaitingQueueThread = new MdpWaitingQueueThread();
         this._mdpWaitingQueueNotificationThread = new MdpWaitingQueueNotificationThread();
         boolean var9 = false /* VF: Semaphore variable */;

         label21:
         try {
            var9 = true;
            ProtocolDaemon.getInstance().startThread(this._mdpWaitingQueueThread);
            ProtocolDaemon.getInstance().startThread(this._mdpWaitingQueueNotificationThread);
            var9 = false;
         } finally {
            if (var9) {
               EventLogger.logEvent(super.GUID, 1414022514, 2);
               this._mdpWaitingQueueThread.kick(true, true);
               this._mdpWaitingQueueNotificationThread.kick(true, true);
               break label21;
            }
         }
      }

      this._mdpWaitingQueueThread.addDatagram(datagram, listener, nativeAddress, reference, retriesCount, datagramAckTimeout);
   }

   @Override
   public final DatagramAddressBase newDatagramAddressBase(String address, boolean swap) {
      MdpAddress ret = new MdpAddress(address);
      if (swap) {
         ret.swap();
      }

      return ret;
   }

   @Override
   public final DatagramAddressBase newDatagramAddressBase(DatagramAddressBase addressBase, boolean swap) {
      MdpAddress ret = new MdpAddress(addressBase);
      if (swap) {
         ret.swap();
      }

      return ret;
   }

   @Override
   public final void send(Datagram datagram) {
      this.send(datagram, null, null, null, 0);
   }

   @Override
   public final void send(Datagram datagram, DatagramAddressBase addressBase, IOProperties properties, DatagramStatusListener listener, int dgramId) {
      EventLogger.logEvent(super.GUID, 1415082868, 4);
      this._txListener = listener;
      this._txDgramId = dgramId;
      addressBase = this.sendVerify(addressBase, datagram);
      int reference = this.sendSetupHeader(datagram);
      synchronized (this._sendChokeLock) {
         this._txDatagram = datagram;
      }

      boolean datagramAck = false;
      boolean packetAck = false;
      if (properties != null) {
         datagramAck = properties.isFlagSet(128);
         packetAck = properties.isFlagSet(256);
      }

      this.sendDatagramAugmented(
         (MdpAddress)addressBase,
         properties,
         reference,
         datagramAck,
         packetAck,
         ((MdpAddress)addressBase).getDestPort(),
         ((MdpAddress)addressBase).getSrcPort(),
         datagram.getData(),
         datagram.getOffset(),
         datagram.getLength()
      );
      EventLogger.logEvent(super.GUID, 1415082867, 4);
      this.xmitDgslEvent(listener, dgramId, 0, null);
   }

   private final void clearSendStatus() {
      synchronized (this._sendStatusIds) {
         this._sendStatusHead = 0;
         this._sendStatusTail = 0;
         this._sendReceipts.clear();
         this._sendStatusIds.notify();
      }
   }

   public Transport() {
      super.GUID = 4080229686686977759L;
      super.STR = "net.rim.mdp";
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      if (ar != null) {
         ar.put(4175103112525226476L, this);
      }
   }

   private final int[] dequeueSendStatus(long tmo) {
      synchronized (this._sendStatusIds) {
         if (this._sendStatusHead == this._sendStatusTail) {
            label42:
            try {
               this._sendStatusIds.wait(tmo);
            } finally {
               break label42;
            }

            if (this._sendStatusHead == this._sendStatusTail) {
               this._sendStatusReturn[0] = -1;
               this._sendStatusReturn[1] = 8;
               return this._sendStatusReturn;
            }
         }

         this._sendStatusReturn[0] = this._sendStatusIds[this._sendStatusTail];
         this._sendStatusReturn[1] = this._sendStatusCodes[this._sendStatusTail];
         this._sendStatusTail++;
         this._sendStatusTail &= 15;
      }

      return this._sendStatusReturn;
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      super.eventOccurred(guid, data0, data1, object0, object1);
      if (guid == -8927980184023446756L) {
         synchronized (this._rxTable) {
            this.allowRetries();
         }
      }
   }

   private final MdpAddress sendVerify(DatagramAddressBase addressBase, Datagram datagram) {
      MdpAddress addr = null;
      if (addressBase instanceof MdpAddress) {
         addr = (MdpAddress)addressBase;
      }

      if (addr == null) {
         addr = new MdpAddress(datagram.getAddress());
      }

      if (datagram.getLength() <= this.getMaximumLength() && addr.getSubAddressBase() != null && addr.getDestPort() != -1 && addr.getSrcPort() != -1) {
         return addr;
      }

      EventLogger.logEvent(super.GUID, 1413834351, 2);
      this.xmitDgslEvent(this._txListener, this._txDgramId, 12674, null);
      throw new Object();
   }

   private final void allowRetries() {
      if (this._wtProfiles != null) {
         for (int i = this._wtProfilesCount - 1; i >= 0; i--) {
            if (this._wtProfiles[i] != null) {
               this._wtProfiles[i].allowRetries(true);
            }
         }
      }
   }

   private final void sendDatagramAugmented(
      MdpAddress addressBase,
      IOProperties properties,
      int reference,
      boolean datagramAck,
      boolean packetAck,
      int destPort,
      int srcPort,
      byte[] data,
      int offset,
      int length
   ) {
      int maxSequence = MdpUtil.getMaximumSequence(length, this._maxNativeLength);
      int count = 0;
      MdpUtil$DatagramInfo info = MdpUtil.makeDatagramInfo(2);
      info.reference = reference;
      info.packetAckFlag = packetAck;
      info.maxSequence = maxSequence;
      info.datagramAckFlag = datagramAck;
      info.srcPort = srcPort;
      info.destPort = destPort;
      info.data = data;
      info.offset = offset;
      info.length = length;
      info.nativeLength = this._maxNativeLength;
      DatagramAddressBase nativeAddress = addressBase.getSubAddressBase();
      WirelessTransportProfile wtProfile = null;
      synchronized (this._rxTable) {
         wtProfile = this.getWirelessTransportProfile(nativeAddress, false);
      }

      int datagramRetries = 0;
      int datagramAckTimeout = this._datagramAckTimeout;
      int maxPacketsOutstanding = this._maxPacketsOutstanding;
      int packetsInFlight = 0;
      int[] sendWaitArray = new int[2];
      boolean datagramResend = true;
      boolean firstPacket = true;
      int sequence = -1;
      boolean moreFlag = true;
      info.optimizedAckFlag = this._mfhOptimizedAcksFlag;
      if (wtProfile != null && !wtProfile._mfhLegacyMode) {
         datagramAckTimeout = wtProfile._datagramAckTimeout;
         maxPacketsOutstanding = wtProfile._maxOutstandingWindowSize;
      }

      if (info.optimizedAckFlag) {
         info.moreFlag = !packetAck;
      }

      moreFlag = info.moreFlag;
      this.initializeDatagramSend(wtProfile, reference, maxSequence);
      if (maxPacketsOutstanding > maxSequence + 1) {
         maxPacketsOutstanding = maxSequence + 1;
      }

      while (true) {
         if (datagramResend) {
            this.clearSendStatus();
            sendWaitArray[0] = maxSequence + 1;
            sendWaitArray[1] = 127;
            packetsInFlight = 0;
            int var40 = -1;
            this.resetPacketTimers(reference);
            synchronized (this._txTable) {
               this._txTable.resetPendingDatagram(reference);
            }

            datagramResend = false;
            firstPacket = true;
         }

         info.moreFlag = moreFlag;
         int sentPackets = 0;

         while (packetsInFlight < maxPacketsOutstanding) {
            sequence = this.getNextPacket(reference);
            if (sequence < 0) {
               break;
            }

            info.sequence = sequence;
            this.sendPacketAugmented(reference, info, nativeAddress);
            packetsInFlight++;
            sentPackets++;
            this.kickPacketTimer(reference, info.sequence, 0);
            if (firstPacket) {
               firstPacket = false;
               this.kickDatagramTimer(reference, this._datagramTimeout);
            }
         }

         boolean allPacketsSent = !this.packetsPending(reference);
         if (allPacketsSent && !datagramAck && !packetAck) {
            this.cancelPacketTimers(reference);
            this.invokeFastDormancy(properties, nativeAddress);
            return;
         }

         int retCode = this.sendWaitAugmented(true, reference, -1, sendWaitArray);
         switch (retCode) {
            case 0:
               break;
            case 1:
            default:
               this.cancelPacketTimers(reference);
               synchronized (this._txTable) {
                  this._txTable.removePendingDatagram(reference);
                  return;
               }
            case 2:
               if (this.packetsAcknowledged(reference)) {
                  if (!datagramAck) {
                     this.cancelPacketTimers(reference);
                     this.invokeFastDormancy(properties, nativeAddress);
                     return;
                  }
               } else {
                  packetsInFlight = Math.max(0, packetsInFlight - sendWaitArray[0]);
               }
               break;
            case 3:
            case 5:
               if (info.optimizedAckFlag) {
                  packetsInFlight = Math.max(0, packetsInFlight - sendWaitArray[0]);
               }
               break;
            case 4:
               if (info.optimizedAckFlag) {
                  this.cancelPacketTimers(reference);
                  synchronized (this._txTable) {
                     this._txTable.removePendingDatagram(reference);
                  }

                  if (datagramAck) {
                     this.addWaitingDatagram(this._txDatagram, this._txListener, addressBase, reference, count, datagramAckTimeout);
                  }

                  this.invokeFastDormancy(properties, nativeAddress);
                  return;
               }
               break;
            case 6:
               if (info.optimizedAckFlag) {
                  packetsInFlight = 0;
                  datagramRetries++;
                  EventLogger.logEvent(super.GUID, 1415082596, 3);
               }
               break;
            case 7:
               EventLogger.logEvent(super.GUID, 1414558829, 4);
               packetsInFlight = Math.max(0, packetsInFlight - sendWaitArray[0]);
               break;
            case 8:
               EventLogger.logEvent(super.GUID, 1413772397, 3);
               this.invokeFastDormancy(properties, nativeAddress);
               this.failDatagram(reference);
         }

         if (retCode != 0 && retCode != 7) {
            this.kickDatagramTimer(reference, this._datagramTimeout);
            count = 0;
         }

         if (!this.packetsPending(reference) || packetsInFlight >= maxPacketsOutstanding) {
            this.invokeFastDormancy(properties, nativeAddress);
            if (count >= this._datagramPingMaxAttempts || datagramRetries > this._datagramMaxAttempts) {
               this.failDatagram(reference);
            }

            if (allPacketsSent) {
               EventLogger.logEvent(super.GUID, 1414423649, 3);
               this.xmitDgslEvent(this._txListener, this._txDgramId, 1, null);
            }

            retCode = this.sendWaitAugmented(true, reference, -2, sendWaitArray);
            switch (retCode) {
               case -1:
                  break;
               case 0:
                  if (!this.packetsPending(reference) && !this.packetTimerRunning(reference, -1)) {
                     EventLogger.logEvent(super.GUID, 1413771367, 0);
                     info.sequence = maxSequence;
                     info.moreFlag = false;
                     this.sendPacketAugmented(reference, info, nativeAddress);
                  }
                  break;
               case 1:
               default:
                  this.cancelPacketTimers(reference);
                  synchronized (this._txTable) {
                     this._txTable.removePendingDatagram(reference);
                     return;
                  }
               case 2:
                  if (this.packetsAcknowledged(reference)) {
                     if (!datagramAck) {
                        this.cancelPacketTimers(reference);
                        this.invokeFastDormancy(properties, nativeAddress);
                        return;
                     }
                  } else {
                     packetsInFlight = Math.max(0, packetsInFlight - sendWaitArray[0]);
                  }
                  break;
               case 3:
               case 5:
                  if (info.optimizedAckFlag) {
                     packetsInFlight = Math.max(0, packetsInFlight - sendWaitArray[0]);
                  }
                  break;
               case 4:
                  if (info.optimizedAckFlag) {
                     this.cancelPacketTimers(reference);
                     synchronized (this._txTable) {
                        this._txTable.removePendingDatagram(reference);
                     }

                     if (datagramAck) {
                        this.addWaitingDatagram(this._txDatagram, this._txListener, addressBase, reference, count, datagramAckTimeout);
                     }

                     this.invokeFastDormancy(properties, nativeAddress);
                     return;
                  }
                  break;
               case 6:
                  if (info.optimizedAckFlag) {
                     packetsInFlight = 0;
                     datagramRetries++;
                     EventLogger.logEvent(super.GUID, 1415082596, 3);
                  }
                  break;
               case 7:
                  EventLogger.logEvent(super.GUID, 1414558829, 4);
                  packetsInFlight = Math.max(0, packetsInFlight - sendWaitArray[0]);
                  break;
               case 8:
                  EventLogger.logEvent(super.GUID, 1413772397, 3);
                  this.invokeFastDormancy(properties, nativeAddress);
                  this.failDatagram(reference);
            }

            if (retCode == 0) {
               count++;
            } else if (retCode != 7) {
               this.kickDatagramTimer(reference, this._datagramTimeout);
               count = 0;
            }
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void sendPacketAugmented(int reference, MdpUtil$DatagramInfo info, DatagramAddressBase nativeAddress) {
      MdpUtil.encode(this._txPacket, info);
      this._txPacket.setAddressBase(nativeAddress);
      this._txPacket.setDatagramStatusListener(this);
      EventLogger.logEvent(super.GUID, 1415082850, 5);
      boolean var6 = false /* VF: Semaphore variable */;

      try {
         var6 = true;
         this.subSend(this._txPacket, this._txListener, reference, this._txDatagram);
         var6 = false;
      } finally {
         if (var6) {
            this._txPacket.reset();
         }
      }

      this._txPacket.reset();
   }

   private final int sendWaitAugmented(boolean flag, int id, int count, int[] sendWaitArray) {
      long nextTime = System.currentTimeMillis();
      long maxTime = nextTime + (count == -1 ? this._ackTimeout : (count == -2 ? this._datagramPingTimeout : this.getBackoffTmo(count)));

      while (true) {
         long tmo = maxTime - nextTime;
         if (tmo <= 0) {
            return 0;
         }

         int[] result = this.dequeueSendStatus(tmo);
         synchronized (this._sendChokeLock) {
            if (this._txDatagram == null) {
               EventLogger.logEvent(super.GUID, 1415078753, 3);
               this.xmitDgslEvent(this._txListener, this._txDgramId, 129, null);
               throw new Object();
            }
         }

         switch (result[1]) {
            case 1:
               if (flag && result[0] == id || !flag && result[0] == (id & 0xFF)) {
                  this.processMFHdAck(result[0], sendWaitArray);
                  return 1;
               }

               if (count != -1) {
                  return this.sendWaitAugmented(flag, id, -1, sendWaitArray);
               }

               nextTime = System.currentTimeMillis();
               break;
            case 2:
               if ((flag || result[0] != id) && (!flag || id != (result[0] & 0xFF))) {
                  if (count != -1) {
                     return this.sendWaitAugmented(flag, id, -1, sendWaitArray);
                  }

                  nextTime = System.currentTimeMillis();
                  break;
               }

               this.processMFHpAck(result[0] & 0xFF, result[0] >> 8 & 0xFF, sendWaitArray, false);
               return 2;
            case 8:
               return 0;
            case 16:
               if (flag && result[0] == id || !flag && result[0] == (id & 0xFF)) {
                  Object o = null;
                  synchronized (this._sendStatusIds) {
                     o = this._sendReceipts.remove(result[0]);
                  }

                  if (o != null) {
                     MdpUtil$DatagramInfo temp = (MdpUtil$DatagramInfo)o;
                     int retCode = this.processMFHReceiptConfirm(result[0], temp.data, temp.offset, temp.length, sendWaitArray);
                     if (retCode != -1) {
                        return retCode;
                     }
                  }
               }

               if (count != -1) {
                  return this.sendWaitAugmented(flag, id, -1, sendWaitArray);
               }

               nextTime = System.currentTimeMillis();
               if (flag && result[0] == id) {
                  maxTime = nextTime + this._ackTimeout;
               }
               break;
            case 256:
               EventLogger.logEvent(super.GUID, 1413838437, 2);
               throw new Object();
            case 1024:
               if ((!flag && result[0] == id || flag && id == (result[0] & 0xFF))
                  && this.processMFHpAck(result[0] & 0xFF, result[0] >> 8 & 0xFF, sendWaitArray, true)) {
                  return 7;
               }
            case 4:
               if (count != -1) {
                  return this.sendWaitAugmented(flag, id, -1, sendWaitArray);
               }

               nextTime = System.currentTimeMillis();
               break;
            case 2048:
               if ((!flag || result[0] != id) && (flag || result[0] != (id & 0xFF))) {
                  if (count != -1) {
                     return this.sendWaitAugmented(flag, id, -1, sendWaitArray);
                  }

                  nextTime = System.currentTimeMillis();
                  break;
               }

               return 8;
            default:
               EventLogger.logEvent(super.GUID, 1413837414, 2);
               throw new Object();
         }
      }
   }

   private final void processMFHdAck(int reference, int[] retArray) {
      synchronized (this._txTable) {
         byte[] statusArray = this._txTable.findPendingDatagramPackets(reference);
         if (statusArray != null) {
            Arrays.fill(statusArray, (byte)4);
            retArray[0] = statusArray.length;
            retArray[1] = 127;
         }
      }
   }

   private final boolean processMFHpAck(int reference, int sequence, int[] retArray, boolean timeout) {
      synchronized (this._txTable) {
         int pState = this._txTable.getPendingDatagramPacketState(reference, sequence);
         switch (pState) {
            case 3:
               this._txTable.setPendingDatagramPacketState(reference, sequence, (byte)(timeout ? (pState == 3 ? 1 : 0) : 5));
               retArray[0] = 1;
               retArray[1] = sequence;
               return true;
            case 4:
            case 5:
            default:
               return false;
         }
      }
   }

   private final int processMFHReceiptConfirm(int reference, byte[] rc, int start, int length, int[] retArray) {
      int rcConfirmedPacketsCount = 0;
      synchronized (this._txTable) {
         byte[] statusArray = this._txTable.findPendingDatagramPackets(reference);
         if (statusArray != null && rc != null && start + length <= rc.length) {
            int maxSequence = statusArray.length - 1;
            int end = start + length;
            int lastDeliveredSequence = -1;
            int deliveredPacketsCount = 0;
            int missingPacketsCount = 0;
            int inFlightPacketsCount = 0;

            for (int sequence = maxSequence; sequence >= 0; sequence--) {
               byte pState = this._txTable.getPendingDatagramPacketState(reference, sequence);
               int index = start + (sequence >> 3);
               if (index >= end) {
                  EventLogger.logEvent(super.GUID, 1380152434, 3);
               } else if ((rc[index] & (byte)(1 << (sequence & 7))) == 0) {
                  switch (pState) {
                     case 1:
                        break;
                     case 2:
                        if (sequence < lastDeliveredSequence) {
                           if (this.packetTimerRunning(reference, sequence)) {
                              this.rollbackPacketTimer(reference, sequence);
                           }

                           this._txTable.setPendingDatagramPacketState(reference, sequence, (byte)1);
                           missingPacketsCount++;
                        }

                        inFlightPacketsCount++;
                        break;
                     case 3:
                     default:
                        inFlightPacketsCount++;
                        break;
                     case 4:
                     case 5:
                        this.cancelPacketTimer(reference, sequence);
                        this._txTable.setPendingDatagramPacketState(reference, sequence, (byte)0);
                        missingPacketsCount++;
                  }
               } else {
                  switch (pState) {
                     case 3:
                     default:
                        this.cancelPacketTimer(reference, sequence);
                        this._txTable.setPendingDatagramPacketState(reference, sequence, (byte)4);
                        rcConfirmedPacketsCount++;
                     case 4:
                     case 5:
                        lastDeliveredSequence = sequence;
                        deliveredPacketsCount++;
                  }
               }
            }

            if (deliveredPacketsCount + inFlightPacketsCount != maxSequence + 1) {
            }

            if (deliveredPacketsCount == maxSequence + 1 && missingPacketsCount == 0) {
               retArray[0] = deliveredPacketsCount;
               retArray[1] = 127;
               return 4;
            }

            if (inFlightPacketsCount == maxSequence + 1 && deliveredPacketsCount == 0) {
               retArray[0] = maxSequence + 1;
               retArray[1] = 127;
               return 6;
            }

            if (missingPacketsCount > 0) {
               retArray[0] = missingPacketsCount + rcConfirmedPacketsCount;
               retArray[1] = 127;
               return 5;
            }
         }
      }

      retArray[0] = rcConfirmedPacketsCount;
      retArray[1] = 127;
      return rcConfirmedPacketsCount > 0 ? 3 : -1;
   }

   private final int getNextPacket(int reference) {
      synchronized (this._txTable) {
         int sequence = this._txTable.findPendingDatagramPacketByState(reference, (byte)1);
         return sequence >= 0 ? sequence : this._txTable.findPendingDatagramPacketByState(reference, (byte)0);
      }
   }

   private final void cancelPacketTimer(int reference, int sequence) {
      if (this._mdpMFHPacketsTimerQueueThread != null) {
         this._mdpMFHPacketsTimerQueueThread.cancelPacket(reference, sequence);
      }
   }

   private final void rollbackPacketTimer(int reference, int sequence) {
      if (this._mdpMFHPacketsTimerQueueThread != null) {
         this._mdpMFHPacketsTimerQueueThread.rollbackPacket(reference, sequence);
      }
   }

   private final void cancelPacketTimers(int reference) {
      if (this._mdpMFHPacketsTimerQueueThread != null) {
         this._mdpMFHPacketsTimerQueueThread.cancelDatagram(reference);
      }
   }

   private final void resetPacketTimers(int reference) {
      if (this._mdpMFHPacketsTimerQueueThread != null) {
         this._mdpMFHPacketsTimerQueueThread.cancelPackets(reference);
      }
   }

   private final boolean packetTimerRunning(int reference, int sequence) {
      return this._mdpMFHPacketsTimerQueueThread != null ? this._mdpMFHPacketsTimerQueueThread.packetTimerRunning(reference, sequence) : false;
   }

   private final void kickPacketTimer(int reference, int sequence, long suggestedPacketTimer) {
      synchronized (this._txTable) {
         if (this._txTable.getPendingDatagramPacketState(reference, sequence) == 1) {
            this._txTable.setPendingDatagramPacketState(reference, sequence, (byte)3);
         } else {
            this._txTable.setPendingDatagramPacketState(reference, sequence, (byte)2);
         }
      }

      this._mdpMFHPacketsTimerQueueThread.addPacket(reference, sequence, suggestedPacketTimer);
   }

   private final void kickDatagramTimer(int reference, long suggestedDatagramTimer) {
      if (this._mdpMFHPacketsTimerQueueThread != null) {
         this._mdpMFHPacketsTimerQueueThread.addDatagram(reference, suggestedDatagramTimer);
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void initializeDatagramTimer(WirelessTransportProfile wt, int reference, int maxSequence) {
      if (this._mdpMFHPacketsTimerQueueThread == null) {
         this._mdpMFHPacketsTimerQueueThread = new MFHPacketsTimerQueue();
         boolean var6 = false /* VF: Semaphore variable */;

         label29:
         try {
            var6 = true;
            ProtocolDaemon.getInstance().startThread(this._mdpMFHPacketsTimerQueueThread);
            var6 = false;
         } finally {
            if (var6) {
               EventLogger.logEvent(super.GUID, 1414022514, 2);
               this._mdpMFHPacketsTimerQueueThread.shutdown();
               break label29;
            }
         }
      }

      this._mdpMFHPacketsTimerQueueThread
         .setup(reference, maxSequence, wt != null ? wt.getMFHRetransmissionConfig() : WirelessTransportProfile.getDefaultMFHRetransmissionConfig());
   }

   private final void initializeDatagramSend(WirelessTransportProfile wt, int reference, int maxSequence) {
      synchronized (this._txTable) {
         this._txTable.addPendingDatagram(reference, maxSequence);
      }

      this.initializeDatagramTimer(wt, reference, maxSequence);
   }

   private final boolean packetsPending(int reference) {
      return this.getNextPacket(reference) >= 0;
   }

   private final boolean packetsAcknowledged(int reference) {
      synchronized (this._txTable) {
         byte[] statusArray = this._txTable.findPendingDatagramPackets(reference);
         if (statusArray != null) {
            for (int i = statusArray.length - 1; i >= 0; i--) {
               if (statusArray[i] != 5) {
                  return false;
               }
            }

            return true;
         } else {
            return false;
         }
      }
   }

   private final byte[] getConfiguration(MdpInfo info) {
      if (info != null) {
         info.maxAttempts = this._maxAttempts;
         info.ackTimeout = this._ackTimeout;
         info.backoffStart = this._backoffStart;
         info.backoffMax = this._backoffMax;
         return null;
      }

      DataBuffer buf = (DataBuffer)(new Object(12 + 4 * this._maxAttempts, true));
      buf.writeInt(this._maxAttempts);
      buf.writeInt(this._ackTimeout);
      buf.writeInt(this._backoffMax);

      for (int i = 0; i < this._maxAttempts; i++) {
         buf.writeInt(this.getBackoffTmo(i));
      }

      return buf.toArray();
   }

   private final void setConfiguration(Object obj) {
      if (!(obj instanceof MdpInfo)) {
         if (obj instanceof byte[] && ((byte[])obj).length >= 16) {
            try {
               DataBuffer buf = (DataBuffer)(new Object((byte[])obj, 0, ((byte[])obj).length, true));
               this._maxAttempts = buf.readInt();
               this._ackTimeout = buf.readInt();
               this._backoffStart = buf.readInt();
               this._backoffMax = buf.readInt();
            } finally {
               return;
            }
         } else {
            this._maxAttempts = 10;
            this._ackTimeout = 6000;
            this._backoffStart = 4000;
            this._backoffMax = 600000;
         }
      } else {
         MdpInfo info = (MdpInfo)obj;
         this._maxAttempts = info.maxAttempts;
         this._ackTimeout = info.ackTimeout;
         this._backoffStart = info.backoffStart;
         this._backoffMax = info.backoffMax;
      }
   }

   @Override
   protected final byte[] setup(int callType, Object context) {
      switch (callType) {
         case -411201860:
            synchronized (this._rxTable) {
               this.removeWirelessTransportProfile(null, true);
               this._requestPeriodThreshold = 600000;
               return null;
            }
         case -152181575:
         case 1816098011:
            boolean kill = callType == 1816098011;
            if (this._mdpWaitingQueueThread != null) {
               this._mdpWaitingQueueThread.kick(true, kill);
            }

            if (this._mdpWaitingQueueNotificationThread != null) {
               this._mdpWaitingQueueNotificationThread.kick(true, kill);
               return null;
            }
            break;
         case 141447089:
            this.setConfiguration(context);
            return null;
         case 326476392:
            return this._rxTable.getDuplicateDatagrams();
         case 777286972:
            this._rxTable.removeDuplicateDatagrams();
            return null;
         case 1046579996:
         case 1744647225:
            if (this._mfhOptimizedAcksFlag != (callType == 1046579996)) {
               this._mfhOptimizedAcksFlag = callType == 1046579996;
               PersistentInteger.set(MFH_OPTIMIZED_ACKS_FLAG_PERSISTENT_ID, this._mfhOptimizedAcksFlag ? 1 : 0);
               return null;
            }
            break;
         case 1340373567:
            byte[] ret = new byte[4];
            int ref = this._nextDgramId + 1;
            if (ref >= 127) {
               ref = 1;
            }

            ret[3] = (byte)ref;
            ref >>>= 8;
            ret[2] = (byte)ref;
            ref >>>= 8;
            ret[1] = (byte)ref;
            ref >>>= 8;
            ret[0] = (byte)ref;
            return ret;
         case 1586951336:
            synchronized (this._rxTable) {
               this._requestPeriodThreshold = this._requestPeriodThreshold == 600000 ? 60000 : 600000;
               return null;
            }
         case 1700404097:
            return this.getConfiguration((MdpInfo)context);
         case 1739268219:
            this._rxTable.removeProcessingDatagrams();
            return null;
         case 2136222991:
            return this._rxTable.getProcessingDatagrams();
         default:
            return super.setup(callType, context);
      }

      return null;
   }
}
