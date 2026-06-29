package net.rim.device.cldc.io.gme;

import java.io.IOException;
import java.util.Vector;
import javax.microedition.io.Datagram;
import net.rim.device.api.crypto.RandomSource;
import net.rim.device.api.hrt.HRUtils;
import net.rim.device.api.hrt.HostRoutingTable;
import net.rim.device.api.io.ConnEvent;
import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.io.DatagramBase;
import net.rim.device.api.io.DatagramConnectionBase;
import net.rim.device.api.io.DatagramStatusListener;
import net.rim.device.api.io.DatagramTransportBase;
import net.rim.device.api.io.IOCancelledException;
import net.rim.device.api.io.IONotRoutableException;
import net.rim.device.api.io.IOProperties;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.servicebook.ServiceRouting;
import net.rim.device.api.servicebook.ServiceRoutingListener2;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.WLAN;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.api.util.TLEUtilities;
import net.rim.device.cldc.io.daemon.ProtocolDaemon;
import net.rim.device.internal.crypto.CryptoBlock;
import net.rim.device.internal.firewall.Firewall;
import net.rim.device.internal.system.DataServices;
import net.rim.device.internal.system.ITPolicyInternal;
import net.rim.device.internal.timesync.SNTPPacket;
import net.rim.device.internal.timesync.TimeSync;
import net.rim.vm.PersistentInteger;

public final class Transport extends DatagramTransportBase implements DatagramStatusListener, GlobalEventListener, ServiceRoutingListener2, ConnEvent {
   private int _transactionId;
   private String _myPIN;
   private ServiceBook _theSB;
   private HostRoutingTable _defHrt;
   private boolean _useDefaultPinKey = true;
   private SNTPPacket _sntpRequest;
   private GmeRouter _router;
   private GMEDatagramInfo _xmitDInfo;
   private Datagram _subDg;
   private Vector _waitingForRoutingInfo = new Vector(1, 1);
   private Object _statusEventLock = new Object();
   protected GmeReceiveThread _receiveThread;
   protected ServiceAuthentication _serviceAuthentication;
   private DataServices _dataServices;
   private ServiceRouting _serviceRouting;
   public static final int MAJOR_VERSION = 32;
   public static final int MINOR_VERSION = 0;
   public static final int VERSION = 32;
   private static final int GME_TYPE_INTEGER = 48;
   private static final int GME_TYPE_OPAQUE = 64;
   private static final int GME_TYPE_SOURCE_ID = 16;
   private static final int GME_TYPE_GRP_SOURCE_ID = 96;
   private static final int GME_TYPE_DEST_ID = 32;
   private static final int GME_TYPE_GRP_DEST_ID = 112;
   private static final int GME_ACK_REQUEST_FLAG = 1;
   private static final int GME_TYPE_CONTENT_ID = 80;
   public static final int GME_CMD_DATA = 3;
   public static final int GME_CMD_SYSTEM_CHECK = 10;
   public static final int GME_CMD_INVALID_COMMAND_BYTE = 255;
   private static final int GME_CMD_SYSTEM_OK = 11;
   private static final int GME_CMD_RESERVED = 0;
   private static final int GME_CMD_DELIVERY = 252;
   private static final int GME_CMD_TRANSACTION_ERROR = 253;
   private static final int GME_TERROR_RESERVED = 0;
   private static final int GME_TERROR_FAILURE_AT_SERVICE = 48;
   private static final int GME_TERROR_UNHANDLED_GME_CMD = 64;
   private static final int GME_TERROR_BAD_GME_FORMAT = 65;
   private static final int GME_TERROR_TIMEOUT = 80;
   private static final int GME_TERROR_INVALID_SERVICE_IDENTIFIER = 96;
   private static final int GME_TERROR_DECRYPTION_ERROR = 112;
   private static final int GME_TERROR_GENERAL_FAILURE = -1;
   private static String PEER2PEER_CALIAS = "Key: 0+S+0";
   private static String ITADMIN = "ITADMIN";
   private static String APP_PUSH = "APPD";
   private static String CMIME = "CMIME";
   private static String OTAKEYGEN = "OTAKEYGEN";
   private static String SERVICE_BOOK = "SERVICE_BOOK";
   private static String BBR = "BBR";
   static final long MY_GUID = 1866032962523356178L;
   private static final long TRANS_ID = 3258554621977920140L;

   public Transport() {
      super.GUID = 1866032962523356178L;
      super.STR = "net.rim.gme";
   }

   @Override
   public final void init() {
      this._dataServices = DataServices.getInstance();
      super.init(null);
      EventLogger.register(super.GUID, super.STR, 2);
      this._receiveThread = new GmeReceiveThread(this);
      ProtocolDaemon.getInstance().startThread(this._receiveThread);
      ProtocolDaemon.getInstance().startThread(new Transport$GmeRegeneratePinToPinKeyThread(this, null));
      this._router = GmeRouter.getInstance();
      this._serviceAuthentication = new ServiceAuthentication();
      this._serviceRouting = ServiceRouting.getInstance();
      int supportedWafs = RadioInfo.getSupportedWAFs();
      if ((supportedWafs & 11) != 0) {
         this._router.addConnection(new MdpBridge(this));
      }

      if (WLAN.isSupported()) {
         this._router.addConnection(new RcpBridge(this, true));
         this._router.addConnection(new SrpBridge(this, true));
         if ((supportedWafs & -5) == 0) {
            HRUtils.getThunks().enableRequestThread(false);
         }
      }

      this._router.addConnection(new StpBridge(this));
      this._theSB = ServiceBook.getSB();
      this._defHrt = HRUtils.getDefaultHRT();
      int flashId = PersistentInteger.getId(3258554621977920140L, RandomSource.getInt());
      this._transactionId = PersistentInteger.get(flashId);
      PersistentInteger.set(flashId, this._transactionId + 9999991);
      this._myPIN = Long.toString(DeviceInfo.getDeviceId() & 4294967295L, 16);
      this._serviceRouting.addListener(this);
      EventLogger.logEvent(super.GUID, 1229878386, 0);
   }

   @Override
   public final DatagramAddressBase newDatagramAddressBase(String address, boolean swap) {
      GMEAddress ret = new GMEAddress(address);
      if (swap) {
         ret.swap();
      }

      return ret;
   }

   @Override
   public final DatagramAddressBase newDatagramAddressBase(DatagramAddressBase addressBase, boolean swap) {
      GMEAddress ret = new GMEAddress(addressBase);
      if (swap) {
         ret.swap();
      }

      return ret;
   }

   @Override
   public final void send(Datagram datagram) {
      this.send(datagram, null, null, null, 0);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void send(Datagram datagram, DatagramAddressBase addressBase, IOProperties properties, DatagramStatusListener listener, int dgramId) {
      EventLogger.logEvent(super.GUID, 1415082868, 4);
      GMEAddress addr = null;
      if (addressBase instanceof GMEAddress) {
         addr = (GMEAddress)addressBase;
      }

      if (addr == null) {
         addr = new GMEAddress(datagram.getAddress());
      }

      if (WLAN.isSupported() && properties != null) {
         properties.setFlag(32, true);
      }

      dgramId = dgramId != 0 ? dgramId : this.getNextDatagramId(null);
      GMEDatagramInfo di = this.getDatagramInfo(datagram, addr, properties, listener, dgramId);
      DatagramBase txDg = new DatagramBase();
      if (di.encrypt || di.compress) {
         this.xmitDgslEvent(di.listener, di.transId, 3, null);
      }

      boolean var11 = false /* VF: Semaphore variable */;

      label67:
      try {
         var11 = true;
         this.prepareDatagram(txDg, di);
         if (di.cmdByte == 10) {
            txDg.setFlag(2048, true);
            var11 = false;
         } else {
            var11 = false;
         }
      } finally {
         if (var11) {
            this.throwIOException(di, 1413696358, 4242);
            break label67;
         }
      }

      if (di.encrypt || di.compress) {
         this.xmitDgslEvent(di.listener, di.transId, 1, null);
      }

      this.sendDatagram(txDg, di, datagram);
   }

   @Override
   protected final void superSend(Datagram datagram) throws IOCancelledException, IONotRoutableException {
      boolean cancelled = false;

      while (true) {
         try {
            super.superSend(datagram);
            return;
         } catch (MissingRoutingInformationException var12) {
            MissingRoutingInformationException mri = var12;
            if (this._serviceRouting.isServiceRoutable(null, -1)) {
               DatagramBase dg = (DatagramBase)datagram;
               this.xmitDgslEvent(dg.getDatagramStatusListener(), dg.getDatagramId(), 4226, null);
               throw new IONotRoutableException();
            }

            var12.setThreadAsCurrent();
            var12.setDatagram(datagram);
            synchronized (this._waitingForRoutingInfo) {
               if (this._router.isSendChoked()) {
                  this._waitingForRoutingInfo.addElement(mri);

                  label51:
                  try {
                     this._waitingForRoutingInfo.wait();
                  } finally {
                     break label51;
                  }
               }

               cancelled = mri.isCancelled();
            }

            if (cancelled) {
               DatagramBase dg = (DatagramBase)datagram;
               this.xmitDgslEvent(dg.getDatagramStatusListener(), dg.getDatagramId(), 129, null);
               throw new IOCancelledException();
            }
         }
      }
   }

   @Override
   public final void superCancel(Datagram datagram) {
      synchronized (this._waitingForRoutingInfo) {
         for (int i = this._waitingForRoutingInfo.size() - 1; i >= 0; i--) {
            MissingRoutingInformationException mri = (MissingRoutingInformationException)this._waitingForRoutingInfo.elementAt(i);
            if (mri.getDatagram() == datagram) {
               this._waitingForRoutingInfo.removeElementAt(i);
               mri.setCancelled(true);
               return;
            }
         }
      }

      super.superCancel(datagram);
   }

   @Override
   public final void cancel(Datagram datagram) {
      Datagram datagramToCancel = null;
      synchronized (this._statusEventLock) {
         if (this._xmitDInfo != null
            && this._xmitDInfo.transId != 0
            && datagram instanceof DatagramBase
            && ((DatagramBase)datagram).getDatagramId() == this._xmitDInfo.transId) {
            this._xmitDInfo = null;
            datagramToCancel = this._subDg;
         }
      }

      if (datagramToCancel != null) {
         this._router.cancel(datagramToCancel);
      }
   }

   private final void prepareDatagram(DataBuffer buf, GMEDatagramInfo di) {
      DataBuffer in = di.dataBuffer;
      buf.ensureCapacity(Math.min(1024, di.headerLength + (in != null ? in.getLength() : 0) + 16));
      logDebugInfo(1415073128);
      makeHeader(buf, di);
      if (in != null) {
         switch (di.cmdByte) {
            case 3:
               logInformation(1415070533);
               dumpTlePayload(in, di, buf);
               break;
            case 10:
               if (TimeSync.getInstance().doLazySync()) {
                  synchronized (this) {
                     this._sntpRequest = new SNTPPacket(di.transId);
                     TLEUtilities.writeDataField(buf, 1, this._sntpRequest.getRequestData());
                     break;
                  }
               }
            default:
               logInformation(1415070576);
               buf.write(in.getArray(), in.getArrayStart(), in.getLength());
         }
      }

      buf.setLength(buf.getPosition());
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void sendDatagram(DatagramBase dg, GMEDatagramInfo di, Datagram superDG) {
      synchronized (this._statusEventLock) {
         this._xmitDInfo = di;
         this._subDg = dg;
      }

      dg.setDatagramStatusListener(this);
      boolean var14 = false /* VF: Semaphore variable */;

      try {
         var14 = true;
         this._router.send(dg, di, superDG);
         var14 = false;
      } finally {
         if (var14) {
            dg.reset();
            synchronized (this._statusEventLock) {
               this._xmitDInfo = null;
               this._subDg = null;
            }

            if (di.errorEventCode != -1) {
               this.xmitDgslEvent(di.listener, di.transId, di.errorEventCode, di.errorEventContext);
            }
         }
      }

      dg.reset();
      synchronized (this._statusEventLock) {
         this._xmitDInfo = null;
         this._subDg = null;
      }

      if (di.errorEventCode != -1) {
         this.xmitDgslEvent(di.listener, di.transId, di.errorEventCode, di.errorEventContext);
      }
   }

   protected final void subSend(DatagramConnectionBase conn, Datagram datagram, DatagramStatusListener listener, int datagramId, Datagram superDatagram) {
      if (datagramId != 0) {
         int subId = conn.allocateDatagramId(datagram);
         if (subId != 0) {
            this.addDgramId(listener, datagramId, subId);
         }
      }

      if (superDatagram instanceof DatagramBase && datagram instanceof DatagramBase) {
         ((DatagramBase)superDatagram).copyFlagsInto((DatagramBase)datagram);
      }

      conn.send(datagram);
   }

   private final GMEDatagramInfo getDatagramInfo(Datagram datagram, GMEAddress gmeAddr, IOProperties properties, DatagramStatusListener listener, int dgramId) throws IOException {
      logDebugInfo(1415071593);
      GMEDatagramInfo di = null;
      ServiceRecord[] srs = null;
      if (datagram instanceof GMEDatagram) {
         GMEDatagram gmeDg = (GMEDatagram)datagram;
         gmeDg.setDatagramId(dgramId);
         di = gmeDg.getDatagramInfo();
         di.dataBuffer = gmeDg;
         srs = gmeDg.getServiceRecordOverride();
      }

      if (di == null) {
         di = new GMEDatagramInfo();
         di.dataBuffer = new DataBuffer(datagram.getData(), datagram.getOffset(), datagram.getLength(), true);
      }

      int encryptMode = -1;
      int compMode = -1;
      di.transId = dgramId;
      di.address = gmeAddr;
      di.listener = listener;
      di.headerLength = 7;
      di.cryptoAliases.removeAllElements();
      di.peer2PeerKeyAdded = false;
      int numAddrs = gmeAddr.getNumTargets();
      String cid = gmeAddr.getCid();
      di.headerLength = di.headerLength + TLEUtilities.getFieldSize(cid.length());
      if (numAddrs == 0) {
         encryptMode &= 1;
         compMode &= 1;
      } else {
         for (int i = 0; i < numAddrs; i++) {
            GMETarget targ = gmeAddr.getTarget(i);
            switch (targ.type) {
               case 0:
                  throw new IOException();
               case 1:
               default:
                  logInformation(1414092132);
                  ServiceRecord sr;
                  if (srs != null && srs.length > i) {
                     sr = srs[i];
                  } else if (StringUtilities.strEqualIgnoreCase(cid, ITADMIN, 1701707776)) {
                     ServiceRecord[] records = this._theSB.findRecordsByUid(targ.address);
                     sr = records.length > 0 ? records[0] : null;
                  } else {
                     sr = this._theSB.getRecordByUidAndCid(targ.address, cid);
                  }

                  if (sr != null) {
                     di.setServiceRecord(sr, i);
                     logInformation(sr.getAttachedHrt() == null ? 1414087784 : 1414091624);
                     encryptMode &= sr.getEncryptionMode();
                     compMode &= sr.getCompressionMode();
                     di.cryptoAliases.addElement(targ.address);
                     break;
                  } else {
                     this.throwIOException(di, 1414090355, 4229);
                  }
               case 2:
                  di.setServiceRecord(null, i);
                  logInformation(1414090852);
                  encryptMode &= 2;
                  compMode &= 2;
                  if (!di.peer2PeerKeyAdded) {
                     if (!this._useDefaultPinKey && CryptoBlock.isKeyPresent(CryptoBlock.CURRENT_BES_SCRAMBLE_KEY)) {
                        di.cryptoAliases.addElement(CryptoBlock.CURRENT_BES_SCRAMBLE_KEY);
                        if (CryptoBlock.isKeyPresent(CryptoBlock.PREVIOUS_BES_SCRAMBLE_KEY)) {
                           di.cryptoAliases.addElement(CryptoBlock.PREVIOUS_BES_SCRAMBLE_KEY);
                        }
                     } else {
                        di.cryptoAliases.addElement(PEER2PEER_CALIAS);
                     }

                     di.peer2PeerKeyAdded = true;
                  }
            }

            di.headerLength = di.headerLength + TLEUtilities.getFieldSize(targ.address.length());
            if (targ.redirect != null) {
               di.headerLength = di.headerLength + TLEUtilities.getFieldSize(targ.redirect.length());
            }
         }
      }

      if (compMode == 0) {
         this.throwIOException(di, 1414090339, 4231);
      } else if (encryptMode == 0) {
         this.throwIOException(di, 1414090341, 4232);
      }

      di.wasWeaklyEncrypted = false;
      di.wasEnterpriseEncrypted = false;
      if ((encryptMode & 2) != 0) {
         logInformation(1413694821);
         di.encrypt = true;
         di.wasEnterpriseEncrypted = true;
      } else if ((encryptMode & 4) != 0) {
         logInformation(1413694839);
         di.encrypt = true;
         di.wasWeaklyEncrypted = true;
      } else if ((encryptMode & 1) != 0) {
         logInformation(1413694820);
         di.encrypt = false;
      }

      if ((compMode & 2) != 0) {
         logInformation(1413694309);
         di.compress = true;
      } else if ((compMode & 1) != 0) {
         logInformation(1413694308);
         di.compress = false;
      }

      if (properties != null) {
         di.failWhenMissing = properties.isFlagSet(64);
         if (properties.isFlagSet(32)) {
            di.headerLength = di.headerLength + TLEUtilities.getFieldSize(this._myPIN.length());
            di.source = this._myPIN;
            logInformation(1414091625);
         }

         if (properties.isFlagSet(16)) {
            di.requestACK = true;
            logInformation(1414087026);
         }
      }

      return di;
   }

   private static final void makeHeader(DataBuffer buf, GMEDatagramInfo di) {
      GMEAddress gmeAddr = di.address;
      buf.writeByte(32);
      if (di.source != null) {
         TLEUtilities.writeStringField(buf, 16, di.source, false);
      }

      for (int i = 0; i < gmeAddr.getNumTargets(); i++) {
         GMETarget targ = gmeAddr.getTarget(i);
         int fieldType = 32 | (di.requestACK ? 1 : 0);
         if (targ.redirect != null) {
            logDebugInfo(1414027122);
            TLEUtilities.writeDataField(buf, fieldType, targ.redirect, 0, targ.redirect.length(), false);
            fieldType = 112 | (di.requestACK ? 1 : 0);
         }

         logDebugInfo(1414027108);
         TLEUtilities.writeDataField(buf, fieldType, targ.address, 0, targ.address != null ? targ.address.length() : 0, false);
      }

      buf.writeByte(0);
      buf.writeInt(di.transId);
      String cid = gmeAddr.getCid();
      TLEUtilities.writeDataField(buf, 80, cid, 0, cid != null ? cid.length() : 0, false);
      buf.writeByte(di.cmdByte);
   }

   private static final void dumpTlePayload(DataBuffer in, GMEDatagramInfo di, DataBuffer out) {
      out.writeByte(64);
      int sizePos = out.getPosition();
      out.writeInt(0);
      out.writeByte(0);
      if (!di.encrypt && !di.compress) {
         logInformation(1413696373);
         out.writeInt(0);
         out.write(in.getArray(), in.getArrayStart(), in.getLength());
      } else {
         in.rewind();
         logInformation(1413696357);
         CryptoBlock.encode(di.cryptoAliases, in, out, di.compress, di.encrypt);
      }

      int tempPos = out.getPosition();
      int dgSize = tempPos - sizePos - 5;
      out.setPosition(sizePos);

      for (int s = 28; s > 0; s -= 7) {
         out.writeByte(dgSize >>> s | 128);
      }

      out.writeByte(dgSize & 127);
      out.setPosition(tempPos);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final boolean parseHeader(DatagramConnectionBase connection, DatagramBase dg, GMEDatagramInfo di) {
      byte[] dgArray = dg.getArray();
      GMETarget target = null;
      Vector targets = null;
      GMETarget src = null;

      try {
         int type = dg.readUnsignedByte();
         if ((type & 240) != 32) {
            logError(1380472422);
            return false;
         }

         int addrIndex = dg.getPosition();

         while (dg.readUnsignedByte() != 0) {
            int length = dg.readCompressedInt();
            dg.skipBytes(length);
         }

         di.transId = dg.readInt();
         type = dg.readUnsignedByte();
         if (type != 80) {
            logError(1380470371);
            return false;
         }

         int length = dg.readCompressedInt();
         String cid = StringUtilities.cStr2String(dgArray, dg.getArrayPosition(), length);
         dg.skipBytes(length);
         di.cmdByte = dg.readUnsignedByte();
         di.headerLength = dg.getPosition();
         dg.setPosition(addrIndex);
         type = dg.readUnsignedByte();
         boolean targettedForDevice = false;

         while (type != 0) {
            length = dg.readCompressedInt();
            String addr = StringUtilities.cStr2String(dgArray, dg.getArrayPosition(), length);
            dg.skipBytes(length);
            switch (type & 240) {
               case 16:
                  if (src != null) {
                     logError(1380470131);
                     return false;
                  }

                  src = new GMETarget(addr, 0);
                  break;
               case 32:
                  if (!targettedForDevice) {
                     if (StringUtilities.strEqualIgnoreCase(this._myPIN, addr, 1701707776)) {
                        targettedForDevice = true;
                        if ((type & 15) == 1) {
                           di.requestACK = true;
                        }
                     }
                  } else if ((type & 15) == 1 && StringUtilities.strEqualIgnoreCase(this._myPIN, addr, 1701707776)) {
                     di.requestACK = true;
                  }

                  int targetType = 1;

                  label403:
                  try {
                     long temp = this.string2PIN(addr);
                     targetType = 2;
                  } finally {
                     break label403;
                  }

                  if (target != null) {
                     targets = new Vector(2);
                     targets.addElement(target);
                     targets.addElement(new GMETarget(addr, targetType));
                     target = null;
                  } else if (targets == null) {
                     target = new GMETarget(addr, targetType);
                  } else {
                     targets.addElement(new GMETarget(addr, targetType));
                  }
                  break;
               case 96:
                  if (src == null || src.redirect != null) {
                     logError(1380471398);
                     return false;
                  }

                  src.redirect = addr;
            }

            type = dg.readUnsignedByte();
         }

         if (!targettedForDevice && di.cmdByte != 10 && di.cmdByte != 11 && di.cmdByte != 253) {
            Firewall.getInstance().incrementBlockedCount((byte)-3);
            logError(1380470372, src != null ? src.address : "<null>");
            return false;
         }

         if (src != null) {
            di.boundSr = this._theSB.getRecordByUidAndCid(src.address, cid);
            if (di.boundSr != null) {
               src.type = 1;
               di.fromPeer = false;
               logInformation(1380468339);
            } else {
               di.boundSrDisallowed = this._theSB.isRecordDisallowed(src.address, cid);
               boolean var33 = false /* VF: Semaphore variable */;

               label390:
               try {
                  var33 = true;
                  long var42 = this.string2PIN(src.address);
                  di.fromPeer = true;
                  src.type = 2;
                  logInformation(1380468336);
                  var33 = false;
               } finally {
                  if (var33) {
                     di.fromPeer = false;
                     logInformation(1380468341);
                     break label390;
                  }
               }
            }
         } else if (di.cmdByte == 3) {
            logError(1380470387);
            return false;
         }

         if (target != null) {
            di.address = new GMEAddress(cid, target, src);
         } else {
            di.address = new GMEAddress(cid, targets, src);
         }

         return true;
      } finally {
         try {
            if (di.address != null) {
               this.sendTransactionErrorDatagram(connection, di, di.transId, 65, "Error parsing GME header");
               return false;
            }
         } finally {
            return false;
         }

         return false;
      }
   }

   public final void superProcessReceivedDatagram(GmeRouterConnection connection, Datagram datagram) {
      if (super._tLogger != null) {
         super._tLogger.bytesReceived(this, 1, datagram.getAddress(), datagram.getLength(), datagram.getData());
      }

      this.processReceivedDatagram(connection, datagram);
   }

   @Override
   protected final void processReceivedDatagram(Datagram datagram) {
   }

   protected final void processReceivedDatagram(GmeRouterConnection routerConnection, Datagram datagram) {
      EventLogger.logEvent(super.GUID, 1381528436, 4);
      DatagramConnectionBase connection = routerConnection._subConnection;
      DatagramAddressBase subAddress = null;
      if (datagram instanceof DatagramBase) {
         subAddress = ((DatagramBase)datagram).getAddressBase();
      }

      if (subAddress == null) {
         subAddress = connection.newDatagramAddressBase(datagram.getAddress(), false);
      }

      DatagramBase dg = (DatagramBase)datagram;
      GMEDatagramInfo dgInfo = new GMEDatagramInfo();
      dgInfo.srcAddr = subAddress;
      logInformation(1381519464);
      if (this.parseHeader(connection, dg, dgInfo)) {
         dg.setData(dg.getData(), dg.getOffset() + dgInfo.headerLength, dg.getLength() - dgInfo.headerLength, true);
         switch (dgInfo.cmdByte) {
            case 3:
               logInformation(1381516132);
               dgInfo.subConnection = connection;
               boolean kickSub = true;
               String cid = dgInfo.address.getCid();
               if (StringUtilities.strEqualIgnoreCase(cid, "IPPP", 1701707776)) {
                  connection.datagramProcessed(dg.getDatagramId());
                  kickSub = false;
               }

               GMEDatagram gmeDg = this.handleTleDatagram(routerConnection, connection, dg, dgInfo);
               if (gmeDg != null) {
                  if (StringUtilities.strEqualIgnoreCase(cid, CMIME, 1701707776)) {
                     gmeDg.setDatagramId(dg.getDatagramId());
                     gmeDg.setFlag(512, true);
                     kickSub = false;
                  }

                  logDebugInfo(1380208757);
                  if (this.passUpDatagram(gmeDg)) {
                     if (dgInfo.requestACK) {
                        this.sendDeliveryConfirmationDatagram(connection, dgInfo, gmeDg.getTransactionId());
                     }
                  } else {
                     logWarning(1380205668);
                  }
               }

               if (kickSub) {
                  connection.datagramProcessed(dg.getDatagramId());
               }

               return;
            case 10:
               logAlways(1381516131);
               this.sendSystemOkDatagram(connection, dgInfo);
               break;
            case 11:
               logAlways(1381516143);
               this.handleSystemOk(dg, dgInfo);
               break;
            case 252:
               logAlways(1381516129);
               this.handleDeliveryCommand(dg, dgInfo);
               break;
            case 253:
               logWarning(1381516133);
               this.handleTransactionErrorCommand(dg, dgInfo);
               break;
            default:
               logError(1381516134);
               this.sendTransactionErrorDatagram(connection, dgInfo, dgInfo.transId, 64, "Unknown CMD Byte");
         }
      }

      connection.datagramProcessed(dg.getDatagramId());
   }

   // $VF: Could not inline inconsistent finally blocks
   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final GMEDatagram handleTleDatagram(GmeRouterConnection routerConnection, DatagramConnectionBase connection, DatagramBase dg, GMEDatagramInfo dgInfo) {
      try {
         if (dg.readUnsignedByte() != 64) {
            logError(1381516390);
            this.sendTransactionErrorDatagram(connection, dgInfo, dgInfo.transId, 65, "Invalid Payload Type");
            return null;
         }

         int payloadLength = dg.readCompressedInt();
         if (payloadLength != dg.available()) {
            logError(1381516396);
            this.sendTransactionErrorDatagram(connection, dgInfo, dgInfo.transId, 65, "Invalid Payload Size");
            return null;
         }

         dgInfo.wasEnterpriseEncrypted = false;
         dgInfo.wasWeaklyEncrypted = false;
         dgInfo.usedGlobalScramblingKey = false;
         dgInfo.keyId = null;
         GMEDatagram gmeDg;
         if (dg.readInt() == 0) {
            logInformation(1380205412);
            gmeDg = this.newDatagram(dg.getData(), dg.getArrayPosition(), dg.available(), dgInfo);
         } else {
            logInformation(1380205413);
            gmeDg = this.newDatagram(null, 0, 0, dgInfo);
            dg.setPosition(dg.getPosition() - 4);
            boolean var25 = false /* VF: Semaphore variable */;

            try {
               var25 = true;
               logInformation(1380207460);
               String t = CryptoBlock.decode(dg, gmeDg);
               logDebugInfo(1380207462);
               if (t != null) {
                  dgInfo.keyId = t;
                  dgInfo.usedGlobalScramblingKey = CryptoBlock.isGlobalPeerToPeerKey(t);
                  dgInfo.wasEnterpriseEncrypted = CryptoBlock.isEnterpriseClassKey(t, null);
                  dgInfo.wasWeaklyEncrypted = !dgInfo.wasEnterpriseEncrypted;
                  var25 = false;
               } else {
                  var25 = false;
               }
            } finally {
               if (var25) {
                  Firewall.getInstance().incrementBlockedCount((byte)-1);
                  String str = dgInfo.address != null ? dgInfo.address.getAddress() : "<null>";
                  EventLogger.logEvent(1866032962523356178L, 1380207462, str.getBytes(), 2);
                  return null;
               }
            }
         }

         try {
            this.verifyGMEDatagram(dgInfo);
         } catch (Throwable var27) {
            this.sendTransactionErrorDatagram(connection, dgInfo, dgInfo.transId, 48, e.getMessage());
            return null;
         }

         if (dgInfo.requestACK) {
            gmeDg.setFlag(16, true);
            logInformation(1380204914);
         }

         routerConnection.receivedFrom(dgInfo);
         return gmeDg;
      } finally {
         try {
            this.sendTransactionErrorDatagram(connection, dgInfo, dgInfo.transId, 65, "Error parsing GME payload");
            return null;
         } finally {
            ;
         }
      }
   }

   private final void verifyGMEDatagram(GMEDatagramInfo dgInfo) {
      ServiceRecord serviceRecord = dgInfo.boundSr;
      String contentID = dgInfo.address.getCid();
      boolean wasPeerToPeer = dgInfo.fromPeer;
      boolean wasScrambled = dgInfo.keyId != null && CryptoBlock.isPeerToPeerKey(dgInfo.keyId);
      boolean wasEnterpriseEncrypted = dgInfo.wasEnterpriseEncrypted && !wasScrambled;
      boolean wasWeaklyEncrypted = dgInfo.wasWeaklyEncrypted && !wasScrambled;
      boolean wasUnencrypted = dgInfo.keyId == null;
      if (serviceRecord == null) {
         if (wasPeerToPeer) {
            if (wasEnterpriseEncrypted || wasWeaklyEncrypted) {
               this.logAndRejectDatagram("DG encrypted with wrong key.", 1380207469, (byte)-5);
               return;
            }

            if (wasUnencrypted) {
               this.logAndRejectDatagram("DG should have been encrypted", 1380207470, (byte)-2);
               return;
            }
         } else {
            if (wasEnterpriseEncrypted
               && !StringUtilities.strEqualIgnoreCase(ITADMIN, contentID, 1701707776)
               && !StringUtilities.strEqualIgnoreCase(SERVICE_BOOK, contentID, 1701707776)
               && !StringUtilities.strEqualIgnoreCase(APP_PUSH, contentID, 1701707776)) {
               this.logAndRejectDatagram("DG encrypted with wrong key.", 1380207469, (byte)-5);
            }

            if (wasWeaklyEncrypted || wasScrambled) {
               this.logAndRejectDatagram("DG encrypted with wrong key.", 1380207469, (byte)-5);
               return;
            }

            if (wasUnencrypted
               && !StringUtilities.strEqualIgnoreCase(OTAKEYGEN, contentID, 1701707776)
               && !StringUtilities.strEqualIgnoreCase(SERVICE_BOOK, contentID, 1701707776)
               && !StringUtilities.strEqualIgnoreCase(BBR, contentID, 1701707776)
               && !StringUtilities.strEqualIgnoreCase(CMIME, contentID, 1701707776)) {
               this.logAndRejectDatagram("DG should have been encrypted", 1380207470, (byte)-2);
            }
         }
      } else if (serviceRecord.isSecureService()) {
         if (wasWeaklyEncrypted || wasScrambled) {
            this.logAndRejectDatagram("DG encrypted with wrong key.", 1380207469, (byte)-5);
            return;
         }

         if (wasUnencrypted && !StringUtilities.strEqualIgnoreCase(contentID, CMIME, 1701707776)) {
            this.logAndRejectDatagram("DG should have been encrypted", 1380207470, (byte)-2);
            return;
         }
      } else if (serviceRecord.isWeakSecureService()) {
         if (wasEnterpriseEncrypted || wasScrambled) {
            this.logAndRejectDatagram("DG encrypted with wrong key.", 1380207469, (byte)-5);
            return;
         }

         if (wasUnencrypted) {
            this.logAndRejectDatagram("DG should have been encrypted", 1380207470, (byte)-2);
            return;
         }
      } else if (!serviceRecord.isEncrypted() && (wasEnterpriseEncrypted || wasWeaklyEncrypted || wasScrambled)) {
         this.logAndRejectDatagram("DG should have been encrypted", 1380207470, (byte)-2);
         return;
      }
   }

   private final void logAndRejectDatagram(String msg, int logErrorCode, byte firewallErrorCode) {
      Firewall.getInstance().incrementBlockedCount(firewallErrorCode);
      logInformation(logErrorCode);
      logError(1380207475);
      throw new SecurityException(msg);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void handleDeliveryCommand(DatagramBase dg, GMEDatagramInfo dgInfo) {
      boolean var12 = false /* VF: Semaphore variable */;

      int transId;
      try {
         var12 = true;
         transId = TLEUtilities.readIntegerField(dg, 48);
         var12 = false;
      } finally {
         if (var12) {
            logError(1380011636);
            return;
         }
      }

      String[] addrs;
      if (dg.available() == 0) {
         logInformation(1380009587);
         addrs = new String[1];
         GMETarget src = dgInfo.address.getSrc();
         if (src == null) {
            logError(1380021619);
            return;
         }

         addrs[0] = src.address;
      } else {
         logInformation(1380009580);
         Vector v = new Vector();

         while (true) {
            boolean var9 = false /* VF: Semaphore variable */;

            try {
               var9 = true;
               if (dg.eof()) {
                  addrs = new String[v.size()];
                  v.copyInto(addrs);
                  var9 = false;
                  break;
               }

               v.addElement(TLEUtilities.readStringField(dg, 64));
            } finally {
               if (var9) {
                  logError(1380011110);
                  return;
               }
            }
         }
      }

      this.passUpTransactionEvent(transId, 5, addrs);
   }

   private final void handleSystemOk(DatagramBase dgram, GMEDatagramInfo info) {
      try {
         while (!dgram.eof()) {
            switch (TLEUtilities.getType(dgram)) {
               case -1:
                  TLEUtilities.skipField(dgram);
                  break;
               case 0:
               default:
                  return;
               case 1:
                  synchronized (this) {
                     if (this._sntpRequest != null) {
                        if (this._sntpRequest.processResponseData(info.transId, TLEUtilities.readDataField(dgram, 1), 0)) {
                           this._sntpRequest = null;
                        }
                     } else {
                        TLEUtilities.skipField(dgram);
                     }
                  }
            }
         }
      } finally {
         return;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void handleTransactionErrorCommand(DatagramBase dg, GMEDatagramInfo dgInfo) {
      String errorMsg = null;
      boolean var13 = false /* VF: Semaphore variable */;

      int transId;
      try {
         var13 = true;
         transId = TLEUtilities.readIntegerField(dg, 48);
         var13 = false;
      } finally {
         if (var13) {
            logError(1380273780);
            return;
         }
      }

      boolean var10 = false /* VF: Semaphore variable */;

      int errorCode;
      try {
         var10 = true;
         errorCode = TLEUtilities.readIntegerField(dg, 48);
         if (!dg.eof()) {
            errorMsg = TLEUtilities.readStringField(dg, 64);
            var10 = false;
         } else {
            var10 = false;
         }
      } finally {
         if (var10) {
            logError(1380271462);
            return;
         }
      }

      int eventCode;
      switch (errorCode) {
         case 0:
            eventCode = 1380282995;
            break;
         case 48:
            eventCode = 1380279923;
            break;
         case 64:
            eventCode = 1380283747;
            break;
         case 65:
            eventCode = 1380278886;
            break;
         case 80:
            eventCode = 1380283503;
            break;
         case 96:
            eventCode = 1380280693;
            break;
         case 112:
            eventCode = 1380279398;
            break;
         default:
            eventCode = 1380280166;
      }

      logError(eventCode);
      errorCode = errorCode & 0xFF | 4480;
      this.passUpTransactionEvent(transId, errorCode, errorMsg);
   }

   private final DatagramBase constructReturnDatagram(DatagramConnectionBase connection, GMEDatagramInfo info, int cmdByte) {
      info.requestACK = false;
      info.transId = this.getNextDatagramId(null);
      info.cmdByte = cmdByte;
      info.source = null;
      info.address = new GMEAddress(info.address, true);
      DatagramBase dg = (DatagramBase)connection.newDatagram(64);
      dg.setAddressBase(connection.newDatagramAddressBase(info.srcAddr, true));
      makeHeader(dg, info);
      return dg;
   }

   private final void sendSystemOkDatagram(DatagramConnectionBase connection, GMEDatagramInfo info) {
      logInformation(1196708719);

      try {
         DatagramBase dgram = this.constructReturnDatagram(connection, info, 11);
         this.addSendRequest(connection, dgram);
      } finally {
         logWarning(1196708710);
         return;
      }
   }

   private final void sendDeliveryConfirmationDatagram(DatagramConnectionBase connection, GMEDatagramInfo info, int transId) {
      logInformation(1196708705);

      try {
         DatagramBase dgram = this.constructReturnDatagram(connection, info, 252);
         TLEUtilities.writeIntegerField(dgram, 48, transId, false);
         TLEUtilities.writeStringField(dgram, 64, this._myPIN, false);
         this.addSendRequest(connection, dgram);
      } finally {
         logWarning(1380204902);
         return;
      }
   }

   protected final void sendTransactionErrorDatagram(DatagramConnectionBase connection, GMEDatagramInfo info, int transId, int error, String str) {
      logInformation(1196708709);

      try {
         DatagramBase dgram = this.constructReturnDatagram(connection, info, 253);
         TLEUtilities.writeIntegerField(dgram, 48, transId, false);
         TLEUtilities.writeIntegerField(dgram, 48, error, false);
         if (str != null) {
            TLEUtilities.writeStringField(dgram, 64, str, false);
         }

         this.addSendRequest(connection, dgram);
      } finally {
         logWarning(1196708710);
         return;
      }
   }

   private final void passUpTransactionEvent(int transId, int errorCode, Object errorMsg) {
      int index = this.lookupDgramIndexFromDgramId(transId);
      if (index >= 0) {
         synchronized (this._statusEventLock) {
            if (this._xmitDInfo != null && this._xmitDInfo.transId == transId) {
               this._xmitDInfo.passUpSubLayerEvents = false;
            }
         }

         this.forwardDgslEvent(index, errorCode, errorMsg);
      } else {
         this.xmitDgslEvent(null, transId, errorCode, errorMsg);
      }
   }

   @Override
   public final void datagramProcessed(int datagramId) {
      this._router.datagramProcessed(datagramId);
   }

   private final long string2PIN(String str) {
      long pin = Long.parseLong(str, 16);
      if ((pin & -536870912) == 0) {
         pin = Long.parseLong(str, 10);
      }

      return pin;
   }

   protected final void throwIOException(GMEDatagramInfo di, int eventLoggerCode, int dgslEvent) throws IOException {
      logError(eventLoggerCode);
      this.xmitDgslEvent(di.listener, di.transId, dgslEvent, null);
      throw new IOException();
   }

   private static final void logAlways(int code) {
      EventLogger.logEvent(1866032962523356178L, code, 0);
   }

   protected static final void logError(int code) {
      EventLogger.logEvent(1866032962523356178L, code, 2);
   }

   protected static final void logError(int code, String message) {
      EventLogger.logEvent(1866032962523356178L, code, message.getBytes(), 2);
   }

   protected static final void logWarning(int code) {
      EventLogger.logEvent(1866032962523356178L, code, 3);
   }

   private static final void logInformation(int code) {
      EventLogger.logEvent(1866032962523356178L, code, 4);
   }

   protected static final void logDebugInfo(int code) {
      EventLogger.logEvent(1866032962523356178L, code, 5);
   }

   @Override
   public final int getMaximumLength() {
      return this._router.getMaximumLength() - 1 - 18 - 18 - 1 - 4 - 18 - 1 - 6 - 4;
   }

   @Override
   public final int getNominalLength() {
      return this._router.getNominalLength() - 1 - 18 - 18 - 1 - 4 - 18 - 1 - 6 - 4;
   }

   @Override
   protected final synchronized int getNextDatagramId(DatagramBase dg) {
      if (++this._transactionId == 0) {
         this._transactionId = 1;
      }

      return this._transactionId;
   }

   @Override
   public final Datagram newDatagram(byte[] buf, int offset, int length, String addr) {
      return new GMEDatagram(buf, offset, length, addr);
   }

   private final GMEDatagram newDatagram(byte[] buf, int offset, int length, GMEDatagramInfo info) {
      return new GMEDatagram(buf, offset, length, info.address, info);
   }

   @Override
   public final void updateDatagramStatus(int subId, int event, Object context) {
      int index = this.lookupDgramIndexFromSubId(subId);
      if (index >= 0) {
         synchronized (this._statusEventLock) {
            if (this._xmitDInfo != null && this.getDgramIdByIndex(index) == this._xmitDInfo.transId) {
               if (!this._xmitDInfo.passUpSubLayerEvents) {
                  return;
               }

               if ((event & 128) != 0) {
                  this._xmitDInfo.errorEventCode = event;
                  this._xmitDInfo.errorEventContext = context;
                  return;
               }
            }
         }

         this.forwardDgslEvent(index, event, context);
      }
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      super.eventOccurred(guid, data0, data1, object0, object1);
      if (guid == -6531073315810526672L) {
         if (object0 == this._defHrt) {
            this.kickWaitingDatagrams();
            return;
         }
      } else {
         if (guid == 8508406279413621091L || guid == -594020114676189989L || guid == -2475029172703491550L) {
            this.regeneratePinToPinKeyInfo();
            return;
         }

         if (guid == -1270659756336956134L) {
            synchronized (this._waitingForRoutingInfo) {
               for (int i = this._waitingForRoutingInfo.size() - 1; i >= 0; i--) {
                  MissingRoutingInformationException mri = (MissingRoutingInformationException)this._waitingForRoutingInfo.elementAt(i);
                  if (!mri.isThreadAlive()) {
                     this._waitingForRoutingInfo.removeElementAt(i);
                  }
               }

               return;
            }
         }

         if (guid == -3556743465989743742L && (this._dataServices.isDataServicesEnabled(1) || this._dataServices.isDataServicesEnabled(2))) {
            this.kickWaitingDatagrams();
         }
      }
   }

   @Override
   public final void serviceRoutingStateChanged(String service, boolean serviceState) {
      if (serviceState) {
         this.kickWaitingDatagrams();
      }
   }

   @Override
   public final void serviceRouteStateChanged(int routeHandle, boolean routeState) {
      if (routeState) {
         this.kickWaitingDatagrams();
      }
   }

   @Override
   public final void serviceRoutingCapabilitiesChanged(String service) {
      if (this._serviceRouting.isServiceRoutable(service, -1)) {
         this.kickWaitingDatagrams();
      }
   }

   private final void kickWaitingDatagrams() {
      synchronized (this._waitingForRoutingInfo) {
         this._waitingForRoutingInfo.removeAllElements();
         this._waitingForRoutingInfo.notifyAll();
      }
   }

   private final void regeneratePinToPinKeyInfo() {
      this._useDefaultPinKey = true;
      byte[] keyInfo = ITPolicyInternal.getPinKey();
      if (keyInfo != null) {
         ITPolicyParser parser = new ITPolicyParser(keyInfo, 0, keyInfo.length);
         if (parser.parse()) {
            this._useDefaultPinKey = false;
         }
      }
   }

   @Override
   protected final byte[] setup(int callType, Object context) {
      switch (callType) {
         case 1589443843:
            return this._router.setup(callType, context);
         case 1589443844:
         default:
            byte[] ret = new byte[4];
            int tid = this._transactionId + 1;
            if (tid == 0) {
               tid = 1;
            }

            ret[3] = (byte)tid;
            tid >>>= 8;
            ret[2] = (byte)tid;
            tid >>>= 8;
            ret[1] = (byte)tid;
            tid >>>= 8;
            ret[0] = (byte)tid;
            return ret;
      }
   }
}
