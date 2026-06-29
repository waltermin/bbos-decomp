package net.rim.device.api.servicebook;

import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.util.CollectionListenerManager;
import net.rim.device.api.hrt.DAC;
import net.rim.device.api.hrt.GprsHRI;
import net.rim.device.api.hrt.HRUtils;
import net.rim.device.api.hrt.HostRoutingInfo;
import net.rim.device.api.hrt.HostRoutingTable;
import net.rim.device.api.hrt.IPv4UdpDAC;
import net.rim.device.api.hrt.IntDAC;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.OTASyncCapable;
import net.rim.device.api.synchronization.OTASyncListener;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncCollectionSchema;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.GPRSQOSInfo;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.internal.proxy.Proxy;
import net.rim.vm.Array;

public final class ServiceBookSyncCollection
   implements GlobalEventListener,
   OTASyncCapable,
   SyncCollection,
   SyncConverter,
   OTASyncListener,
   CollectionEventSource {
   private ServiceBook _sb;
   private CollectionListenerManager _collectionListenerManager;
   private boolean _legacyRestoreMode = false;
   private SyncCollectionSchema _schema;
   private IntHashtable _serviceRecords;
   private IntHashtable _hriRecords;
   private Hashtable _blockedCIDTable;
   public static final long SB_SYNC_GUID = -7781445676016247535L;
   static final int SB_EVENT_REPORTED_NULL_OBJECT = 1396854348;
   private static final int BACKUP_RESTORE_RECORD_TAG = 1;
   private static final int SERVICE_RECORD_TYPE = 2;
   private static final int SERVICE_NAME = 3;
   private static final int HOME_ADDRESS = 4;
   private static final int DATASOURCE_ID = 5;
   private static final int USER_ID = 6;
   private static final int SERVICE_UID = 7;
   private static final int SERVICE_CID = 8;
   private static final int APPLICATION_DATA = 9;
   private static final int COMP_MODE = 10;
   private static final int ENCRYPT_MODE = 11;
   private static final int CA_REALM = 12;
   private static final int CA_ADDRESS = 13;
   private static final int CA_PORT = 14;
   private static final int SERVICE_DESCRIPTION = 15;
   private static final int FLAGS_FLAG = 16;
   private static final int RECORD_SOURCE = 17;
   private static final int SERVICE_IDENTIFIER = 18;
   private static final int BBR_ROUTING_INFO = 19;
   private static final int BBR_PORT = 20;
   private static final int BBR_HOST = 21;
   private static final int HOST_ROUTING_INFO = 22;
   private static final int SERVICE_RECORD_RESTORE_ID = 23;
   private static final int LAST_UPDATED = 24;
   private static final int HRI_APN_PASSWORD = 1;
   private static final int HRI_APN_USERNAME = 2;
   private static final int HRI_APN = 3;
   private static final int HRI_RADIO_NETWORK_ID = 4;
   private static final int HRI_HPID_VALUE = 5;
   private static final int HRI_NPC = 6;
   private static final int HRI_NPC_EXTENSION = 7;
   private static final int HRI_LOAD_SHARING = 8;
   private static final int HRI_NETWORK_NAME = 9;
   private static final int HRI_WIRELESS_NETWORK = 10;
   private static final int HRI_IP_LIST = 16;
   private static final int HRI_UDP_PORT_LIST = 17;
   private static final int HRI_QOS = 18;
   private static final int BACKUP_RESTORE_RECORD_TYPE = 1;
   private static final int[] KEY_FIELD_IDS_FOR_SERVICE_RECORD = new int[]{
      5,
      6,
      7,
      8,
      3,
      -805044222,
      3344909,
      1129447424,
      1129447497,
      1381105746,
      84,
      -805044208,
      1667329090,
      1919238763,
      1933867378,
      1886352491,
      -805044106,
      944130375,
      942393,
      11730960
   };

   public final void registerCIDForRestoreDisable(String cid) {
      if (cid != null) {
         this._blockedCIDTable.put(cid, cid);
      }
   }

   public final void deRegisterCIDForRestoreDisable(String cid) {
      if (cid != null) {
         this._blockedCIDTable.remove(cid);
      }
   }

   public final boolean isServiceRestorable(ServiceRecord sr) {
      boolean canRestore = this._legacyRestoreMode;
      if (!canRestore) {
         if (sr.isRestoreDisabled()) {
            return false;
         }

         if (sr.isRestoreEnabled()) {
            return true;
         }

         if (this._blockedCIDTable.containsKey(sr.getCid())) {
            return false;
         }

         canRestore = !sr.isSecureService();
      }

      return canRestore;
   }

   public final void sbEventOccurred(long guid, ServiceRecord sr1, ServiceRecord sr2, boolean forceEvent) {
      if (this.isServiceRestorable(sr1) || forceEvent) {
         SyncObject syncObject0 = null;
         SyncObject syncObject1 = null;
         if (guid == -4220058463650496006L || guid == 8288627527798139133L) {
            syncObject0 = this.getSyncObjectForServiceRecord(sr1);
            syncObject1 = this.getSyncObjectForServiceRecord(sr2);
         } else if (guid == 2522898683889177438L) {
            syncObject0 = new ServiceBookSyncCollection$SRSyncObject(sr1, this.generateHash(sr1));
         }

         if (syncObject0 != null) {
            if (guid == 8288627527798139133L) {
               this._collectionListenerManager.fireElementUpdated(this, syncObject1, syncObject0);
               return;
            }

            if (guid == 2522898683889177438L) {
               this._collectionListenerManager.fireElementRemoved(this, syncObject0);
               return;
            }

            if (guid == -4220058463650496006L) {
               this._collectionListenerManager.fireElementAdded(this, syncObject0);
               return;
            }
         } else {
            EventLogger.logEvent(-863050508581563378L, 1396854348, 5);
         }
      }
   }

   public final Object convertVersion2(DataBuffer buffer, int UID, boolean noSyncType) {
      try {
         buffer.rewind();
         ServiceRecord sri = new ServiceRecord(0);
         sri.setRestoredFromBackup(true);

         while (buffer.available() > 0) {
            int tag = ConverterUtilities.getType(buffer);
            switch (tag) {
               case 1:
               case 20:
               case 21:
               case 23:
                  buffer.skipBytes(buffer.readShort() + 1);
                  break;
               case 2:
               default:
                  sri.setType(ConverterUtilities.readInt(buffer));
                  break;
               case 3:
                  sri.setName(ConverterUtilities.readString(buffer));
                  break;
               case 4:
                  sri.setHomeAddress(ConverterUtilities.readString(buffer));
                  break;
               case 5:
                  sri.setDataSourceId(ConverterUtilities.readString(buffer));
                  break;
               case 6:
                  sri.setUserId(ConverterUtilities.readInt(buffer));
                  break;
               case 7:
                  sri.setUid(ConverterUtilities.getBinaryString(buffer));
                  break;
               case 8:
                  sri.setCid(ConverterUtilities.getBinaryString(buffer));
                  break;
               case 9:
                  sri.setApplicationData(ConverterUtilities.readByteArray(buffer));
                  break;
               case 10:
                  sri.setCompressionMode(ConverterUtilities.readInt(buffer));
                  break;
               case 11:
                  sri.setEncryptionMode(ConverterUtilities.readInt(buffer));
                  break;
               case 12:
                  sri.setCARealm(ConverterUtilities.getBinaryString(buffer));
                  break;
               case 13:
                  sri.setCAAddress(ConverterUtilities.getBinaryString(buffer));
                  break;
               case 14:
                  sri.setCAPort((short)this.htons(ConverterUtilities.readInt(buffer)));
                  break;
               case 15:
                  sri.setDescription(ConverterUtilities.readString(buffer));
                  break;
               case 16:
                  sri.setFlags(ConverterUtilities.readInt(buffer));
                  break;
               case 17:
                  sri.setSource(ConverterUtilities.readInt(buffer));
                  break;
               case 18:
                  sri.setServiceIdentifierValue(ConverterUtilities.readInt(buffer));
                  break;
               case 19:
                  int len = buffer.readShort();
                  buffer.skipBytes(1);
                  if (buffer.available() < len || len < 3) {
                     return null;
                  }

                  String[] hosts = new String[0];
                  int[] ports = new int[0];

                  while (!ConverterUtilities.isType(buffer, 0)) {
                     switch (ConverterUtilities.getType(buffer)) {
                        case 19:
                           buffer.skipBytes(buffer.readShort() + 1);
                           break;
                        case 20:
                           Arrays.add(ports, ConverterUtilities.readInt(buffer));
                           break;
                        case 21:
                        default:
                           Arrays.add(hosts, ConverterUtilities.getBinaryString(buffer));
                     }
                  }

                  buffer.skipBytes(buffer.readShort() + 1);
                  if (hosts.length > 0 && hosts.length == ports.length) {
                     sri.setBBRHosts(hosts);
                     sri.setBBRPorts(ports);
                  }
                  break;
               case 22:
                  byte[] hrtData = ConverterUtilities.readByteArray(buffer);
                  DataBuffer hrtBuff = new DataBuffer(hrtData, 0, hrtData.length, false);
                  HostRoutingInfo hri = this.parseHostRoutingInformation(hrtBuff);
                  if (hri != null) {
                     HostRoutingTable hrt = sri.getAttachedHrt();
                     if (hrt == null) {
                        hrt = new HostRoutingTable();
                     }

                     if (hrt.addHri(hri)) {
                        sri.setAttachedHrt(hrt);
                     }
                  }
                  break;
               case 24:
                  sri.setLastUpdated(ConverterUtilities.readLong(buffer));
            }
         }

         return new ServiceBookSyncCollection$SRSyncObject(sri, UID);
      } finally {
         ;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final Object convert(DataBuffer buffer, int version, int UID, boolean noSyncType) {
      if (version == 2) {
         return this.convertVersion2(buffer, UID, noSyncType);
      }

      if (version != 1) {
         return null;
      }

      boolean isHRI = false;
      int networkType = 0;

      try {
         buffer.rewind();
         if (ConverterUtilities.findType(buffer, 1) && buffer.readShort() == 1) {
            buffer.skipBytes(1);
            networkType = buffer.readByte();
            isHRI = true;
            switch (networkType) {
               case 2:
                  isHRI = false;
                  break;
               case 3:
               default:
                  networkType = 3;
                  break;
               case 4:
                  networkType = 4;
                  break;
               case 5:
                  networkType = 5;
            }
         }

         if (!isHRI) {
            buffer.rewind();
            if (!ConverterUtilities.findType(buffer, 6)) {
               isHRI = true;
               networkType = RadioInfo.getNetworkType();
            }
         }

         buffer.rewind();
         if (isHRI) {
            HostRoutingInfo hri = HRUtils.newHriByNetType(networkType);
            int sruid = 0;

            while (buffer.available() > 0) {
               int fieldType = ConverterUtilities.getType(buffer);
               switch (fieldType) {
                  case 2:
                     hri.setName(ConverterUtilities.readString(buffer));
                     break;
                  case 3:
                     ((DAC)hri.getDac()).setLoadSharingCode(ConverterUtilities.readInt(buffer));
                     break;
                  case 11:
                  case 32:
                     long npc = ConverterUtilities.readInt(buffer) & 4294967295L;
                     if (npc == 0) {
                        switch (hri.getWirelessNetType()) {
                           case 2:
                              break;
                           case 3:
                           default:
                              npc = 48;
                              break;
                           case 4:
                              npc = 64;
                              break;
                           case 5:
                              npc = 80;
                        }
                     }

                     hri.setNpc(hri.getNpc() | npc);
                     break;
                  case 16:
                  case 20:
                     if (networkType != 3 && networkType != 7 && networkType != 4 && networkType != 5) {
                        return null;
                     }

                     IPv4UdpDAC dac = (IPv4UdpDAC)hri.getDac();
                     int len = buffer.readShort();
                     buffer.skipBytes(1);
                     if (buffer.available() < len || len % 4 != 0) {
                        return null;
                     }

                     buffer.setBigEndian(true);
                     len /= 4;
                     long[] addrs;
                     if ((addrs = dac.getAddresses()) == null) {
                        addrs = new long[len];
                     }

                     for (int i = 0; i < len; i++) {
                        int srcPort;
                        int ipAddr;
                        int dstPort;
                        if (fieldType == 16) {
                           ipAddr = buffer.readInt();
                           dstPort = IPv4UdpDAC.addr2DstPort(addrs[i]);
                           srcPort = IPv4UdpDAC.addr2SrcPort(addrs[i]);
                        } else {
                           ipAddr = IPv4UdpDAC.addr2IpAddress(addrs[i]);
                           dstPort = buffer.readUnsignedShort();
                           srcPort = buffer.readUnsignedShort();
                        }

                        addrs[i] = IPv4UdpDAC.makeAddr(ipAddr, dstPort, srcPort);
                     }

                     buffer.setBigEndian(false);
                     dac.setAddresses(addrs);
                     break;
                  case 18:
                     if (networkType != 3 && networkType != 7) {
                        return null;
                     }

                     ((GprsHRI)hri).setApn(ConverterUtilities.getBinaryString(buffer));
                     break;
                  case 19:
                     if (networkType != 3 && networkType != 7) {
                        return null;
                     }

                     byte[] qosBuf = ConverterUtilities.readByteArray(buffer);
                     GPRSQOSInfo qos = new GPRSQOSInfo(qosBuf[0] & 255, qosBuf[1] & 255, qosBuf[2] & 255, qosBuf[3] & 255, qosBuf[4] & 255);
                     ((GprsHRI)hri).setQos(qos);
                     break;
                  case 33:
                     long var28 = ConverterUtilities.readInt(buffer) & 4294967295L;
                     hri.setNpc(hri.getNpc() | var28 << 32);
                     break;
                  case 34:
                     if (networkType != 3 && networkType != 7) {
                        return null;
                     }

                     ((GprsHRI)hri).setApnUsername(ConverterUtilities.getBinaryString(buffer));
                     break;
                  case 35:
                     if (networkType != 3 && networkType != 7) {
                        return null;
                     }

                     ((GprsHRI)hri).setApnPassword(ConverterUtilities.getBinaryString(buffer));
                     break;
                  case 202:
                     sruid = ConverterUtilities.readInt(buffer);
                     break;
                  default:
                     buffer.skipBytes(buffer.readShort() + 1);
               }
            }

            return noSyncType ? hri : new ServiceBookSyncCollection$HRISyncObject(hri, UID, sruid);
         } else {
            ServiceRecord sri = new ServiceRecord(0);
            sri.setRestoredFromBackup(true);
            int[] assocHRI = null;

            while (buffer.available() > 0) {
               switch (ConverterUtilities.getType(buffer)) {
                  case 1:
                     sri.setName(ConverterUtilities.readString(buffer));
                     break;
                  case 3:
                     sri.setHomeAddress(ConverterUtilities.readString(buffer));
                     break;
                  case 6:
                     sri.setUid(ConverterUtilities.getBinaryString(buffer));
                     break;
                  case 8:
                     sri.setCid(ConverterUtilities.getBinaryString(buffer));
                     break;
                  case 9:
                     sri.setApplicationData(ConverterUtilities.readByteArray(buffer));
                     break;
                  case 10:
                     sri.setCompressionMode(ConverterUtilities.readInt(buffer));
                     break;
                  case 11:
                     sri.setEncryptionMode(ConverterUtilities.readInt(buffer));
                     break;
                  case 12:
                     sri.setCARealm(ConverterUtilities.getBinaryString(buffer));
                     break;
                  case 13:
                     sri.setCAAddress(ConverterUtilities.getBinaryString(buffer));
                     break;
                  case 14:
                     sri.setCAPort((short)this.htons(ConverterUtilities.readInt(buffer)));
                     break;
                  case 42:
                     int len = buffer.readShort();
                     buffer.skipBytes(1);
                     if (buffer.available() < len) {
                        return null;
                     }

                     int num = buffer.readUnsignedByte();
                     if (len != num * 4 + 1) {
                        return null;
                     }

                     assocHRI = new int[num];

                     while (num > 0) {
                        assocHRI[--num] = buffer.readInt();
                     }
                     break;
                  case 43:
                     sri.setSource(ConverterUtilities.readInt(buffer));
                     break;
                  case 50:
                     sri.setDescription(ConverterUtilities.readString(buffer));
                     break;
                  case 161:
                     sri.setDataSourceId(ConverterUtilities.readString(buffer));
                     break;
                  case 162:
                     int len = buffer.readShort();
                     buffer.skipBytes(1);
                     if (buffer.available() < len || len < 3) {
                        return null;
                     }

                     String[] hosts = new String[0];
                     int[] ports = new int[0];

                     while (!ConverterUtilities.isType(buffer, 0)) {
                        switch (ConverterUtilities.getType(buffer)) {
                           case 19:
                              buffer.skipBytes(buffer.readShort() + 1);
                              break;
                           case 20:
                              Arrays.add(ports, ConverterUtilities.readInt(buffer));
                              break;
                           case 21:
                           default:
                              Arrays.add(hosts, ConverterUtilities.getBinaryString(buffer));
                        }
                     }

                     buffer.skipBytes(buffer.readShort() + 1);
                     if (hosts.length > 0 && hosts.length == ports.length) {
                        sri.setBBRHosts(hosts);
                        sri.setBBRPorts(ports);
                     }
                     break;
                  case 163:
                     int userID = -1;
                     boolean var23 = false /* VF: Semaphore variable */;

                     label444:
                     try {
                        var23 = true;
                        userID = ConverterUtilities.readInt(buffer);
                        var23 = false;
                     } finally {
                        if (var23) {
                           ConverterUtilities.readString(buffer);
                           break label444;
                        }
                     }

                     sri.setUserId(userID);
                     break;
                  case 165:
                     sri.setServiceIdentifierValue(ConverterUtilities.readInt(buffer));
                     break;
                  case 250:
                     sri.setType(ConverterUtilities.readInt(buffer));
                     break;
                  case 251:
                     sri.setRecordProtected(true);
                     buffer.skipBytes(buffer.readShort() + 1);
                     break;
                  case 252:
                     sri.setFlags(ConverterUtilities.readInt(buffer));
                     break;
                  default:
                     buffer.skipBytes(buffer.readShort() + 1);
               }
            }

            return noSyncType ? sri : new ServiceBookSyncCollection$SRSyncObject(sri, UID, assocHRI);
         }
      } finally {
         ;
      }
   }

   public final Object convert(DataBuffer buffer, int version) {
      return this.convert(buffer, version, -1, true);
   }

   public final boolean convertServices(Object object, DataBuffer buffer, int version) {
      if (version != 2) {
         return false;
      }

      ServiceRecord sr = null;
      if (!(object instanceof ServiceBookSyncCollection$SRSyncObject)) {
         if (object instanceof ServiceRecord) {
            sr = (ServiceRecord)object;
         }
      } else {
         ServiceBookSyncCollection$SRSyncObject srsync = (ServiceBookSyncCollection$SRSyncObject)object;
         sr = srsync.getServiceRecord();
      }

      if (sr == null) {
         return false;
      }

      HostRoutingTable hrTable = sr.getAttachedHrt();
      HostRoutingInfo[] attachedHRTs = null;
      if (hrTable != null) {
         attachedHRTs = hrTable.getHris();
      }

      ConverterUtilities.convertInt(buffer, 1, 1, 4);
      ConverterUtilities.convertInt(buffer, 2, sr.getType(), 4);
      ConverterUtilities.convertInt(buffer, 23, this.generateHash(sr), 4);
      this.convertString(buffer, 3, sr.getName());
      this.convertString(buffer, 4, sr.getHomeAddress());
      String dsid = sr.getDataSourceId();
      if (dsid != null && dsid.length() > 0) {
         this.convertString(buffer, 5, dsid);
      } else {
         this.convertString(buffer, 5, "");
      }

      this.convertInt(buffer, 6, sr.getUserId(), 4);
      this.convertBinary(buffer, 7, sr.getUid());
      this.convertBinary(buffer, 8, sr.getCid());
      this.convertBinary(buffer, 9, sr.getApplicationData());
      ConverterUtilities.convertInt(buffer, 10, sr.getCompressionMode(), 4);
      ConverterUtilities.convertInt(buffer, 11, sr.getEncryptionMode(), 4);
      this.convertBinary(buffer, 12, sr.getCARealm());
      this.convertBinary(buffer, 13, sr.getCAAddress());
      this.convertInt(buffer, 14, this.htons(sr.getCAPort()), 2);
      this.convertString(buffer, 15, sr.getDescription());
      this.convertInt(buffer, 16, sr.getFlags(), 4);
      this.convertInt(buffer, 17, sr.getSource(), 1);
      ConverterUtilities.convertInt(buffer, 18, sr.getServiceIdentifierValue(), 4);
      ConverterUtilities.convertInt(buffer, 24, (int)(sr.getLastUpdated() / 1000), 4);
      String[] hosts = sr.getBBRHosts();
      int[] ports = sr.getBBRPorts();
      if (hosts != null && ports != null && hosts.length == ports.length) {
         DataBuffer buf = new DataBuffer(64, false);
         int numHosts = hosts.length;

         for (int i = 0; i < numHosts; i++) {
            ConverterUtilities.convertBinary(buf, 21, hosts[i]);
            ConverterUtilities.convertInt(buf, 20, ports[i], 4);
         }

         ConverterUtilities.writeEmptyField(buf, 0);
         buf.rewind();
         ConverterUtilities.writeByteArray(buffer, 19, buf.toArray());
      }

      int num = attachedHRTs == null ? 0 : attachedHRTs.length;
      if (num != 0) {
         for (int i = 0; i < num; i++) {
            HostRoutingInfo hri = attachedHRTs[i];
            DataBuffer HRTBuffer = new DataBuffer(false);
            ConverterUtilities.convertInt(HRTBuffer, 10, hri.getWirelessNetType(), 1);
            this.convertString(HRTBuffer, 9, hri.getName());
            this.convertInt(HRTBuffer, 8, ((DAC)hri.getDac()).getLoadSharingCode(), 1);
            long npc = hri.getNpc();
            this.convertInt(HRTBuffer, 6, (int)npc, 4);
            this.convertInt(HRTBuffer, 7, (int)(npc >> 32), 4);
            if (hri instanceof GprsHRI) {
               GprsHRI ghri = (GprsHRI)hri;
               this.convertBinary(HRTBuffer, 3, ghri.getApn());
               this.convertBinary(HRTBuffer, 2, ghri.getApnUsername());
               this.convertBinary(HRTBuffer, 1, ghri.getApnPassword());
               GPRSQOSInfo qos = (GPRSQOSInfo)ghri.getQos();
               byte[] qosBuf = new byte[]{
                  (byte)qos.getPrecedenceClass(),
                  (byte)qos.getReliabilityClass(),
                  (byte)qos.getDelayClass(),
                  (byte)qos.getPeakThroughputClass(),
                  (byte)qos.getMeanThroughputClass()
               };
               this.convertBinary(HRTBuffer, 18, qosBuf);
            }

            this.dumpDac((DAC)hri.getDac(), HRTBuffer);
            HRTBuffer.trim();
            this.convertBinary(buffer, 22, HRTBuffer.toArray());
         }
      }

      return true;
   }

   public final boolean enableLegacyBackupRestore(boolean legacyRestoreMode, boolean pingOTASync) {
      boolean oldState = this._legacyRestoreMode;
      if (this._legacyRestoreMode != legacyRestoreMode) {
         this._legacyRestoreMode = legacyRestoreMode;
         if (pingOTASync) {
            SyncManager.getInstance().allowOTASync(this, !this._legacyRestoreMode);
         }
      }

      return oldState;
   }

   @Override
   public final String getSyncName() {
      return "Service Book";
   }

   @Override
   public final String getSyncName(Locale locale) {
      return null;
   }

   @Override
   public final SyncConverter getSyncConverter() {
      return this;
   }

   @Override
   public final void otaSyncOperationStarted(SyncCollection syncCollection, int type) {
      if (type == 1) {
         this.beginTransaction();
      }
   }

   @Override
   public final void otaSyncOperationStopped(SyncCollection syncCollection, int type) {
      if (type == 1) {
         this.endTransaction();
      }
   }

   @Override
   public final void beginTransaction() {
      this._serviceRecords = new IntHashtable();
      this._hriRecords = new IntHashtable();
      this._sb.setBrInProgress(true);
   }

   @Override
   public final void endTransaction() {
      this._serviceRecords = null;
      this._hriRecords = null;
      this._sb.setBrInProgress(false);
   }

   @Override
   public final boolean isSyncObjectDirty(SyncObject object) {
      return false;
   }

   @Override
   public final void setSyncObjectDirty(SyncObject object) {
   }

   @Override
   public final void clearSyncObjectDirty(SyncObject object) {
   }

   @Override
   public final boolean updateSyncObject(SyncObject oldObject, SyncObject newObject) {
      ServiceRecord sr = this.getServiceRecordFromSyncObject(oldObject);
      if (sr != null) {
         ServiceRecord trueOldSR = this.findMatchingServiceRecord(sr);
         if (trueOldSR != null) {
            this.addSyncObject(newObject);
            return true;
         }
      }

      return false;
   }

   @Override
   public final boolean removeSyncObject(SyncObject object) {
      ServiceRecord sr = this.getServiceRecordFromSyncObject(object);
      if (sr != null) {
         ServiceRecord trueSR = this.findMatchingServiceRecord(sr);
         if (trueSR != null) {
            this._sb.removeRecord(sr);
            return true;
         }
      }

      return false;
   }

   @Override
   public final int getSyncVersion() {
      return 2;
   }

   @Override
   public final boolean convert(SyncObject object, DataBuffer buffer, int version) {
      return this.convertServices(object, buffer, version);
   }

   @Override
   public final int getSyncObjectCount() {
      ServiceRecord[] records = this._sb.getRecords();
      int actual = 0;
      synchronized (records) {
         for (int i = records.length - 1; i >= 0; i--) {
            ServiceRecord sr = records[i];
            if (this.isServiceRestorable(sr)) {
               actual++;
            }
         }

         return actual;
      }
   }

   @Override
   public final SyncObject getSyncObject(int uid) {
      SyncObject so = null;
      int srUID = uid;
      ServiceRecord[] records = this._sb.getRecords();
      synchronized (records) {
         for (int i = records.length - 1; i >= 0; i--) {
            ServiceRecord sr = records[i];
            if (this.isServiceRestorable(sr) && srUID == this.generateHash(sr)) {
               so = new ServiceBookSyncCollection$SRSyncObject(sr, srUID);
               break;
            }
         }

         return so;
      }
   }

   @Override
   public final SyncObject convert(DataBuffer buffer, int version, int UID) {
      return (SyncObject)this.convert(buffer, version, UID, false);
   }

   @Override
   public final SyncObject[] getSyncObjects() {
      ServiceRecord[] records = this._sb.getRecords();
      synchronized (records) {
         int count = records.length;
         int actual = 0;
         SyncObject[] array = new SyncObject[this.getSyncObjectCount()];

         for (int i = 0; i < count; i++) {
            ServiceRecord sr = records[i];
            if (this.isServiceRestorable(sr)) {
               int srUID = this.generateHash(sr);
               array[actual++] = new ServiceBookSyncCollection$SRSyncObject(sr, srUID);
            }
         }

         Array.resize(array, actual);
         return array;
      }
   }

   @Override
   public final boolean removeAllSyncObjects() {
      this._sb.removeAllRecords(false);
      return true;
   }

   @Override
   public final SyncCollectionSchema getSchema() {
      return this._schema;
   }

   @Override
   public final void addCollectionListener(Object listener) {
      this._collectionListenerManager.addCollectionListener(listener);
   }

   @Override
   public final void removeCollectionListener(Object listener) {
      this._collectionListenerManager.removeCollectionListener(listener);
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if ((guid == 8288627527798139133L || guid == 2522898683889177438L || guid == -4220058463650496006L)
         && object0 instanceof ServiceRecord
         && (object1 == null || object1 instanceof ServiceRecord)) {
         ServiceRecord sr1 = (ServiceRecord)object0;
         ServiceRecord sr2 = (ServiceRecord)object1;
         this.sbEventOccurred(guid, sr1, sr2, false);
      }
   }

   @Override
   public final boolean addSyncObject(SyncObject object) {
      if (!(object instanceof ServiceBookSyncCollection$SRSyncObject)) {
         if (!(object instanceof ServiceBookSyncCollection$HRISyncObject)) {
            return false;
         }

         ServiceBookSyncCollection$HRISyncObject hrisync = (ServiceBookSyncCollection$HRISyncObject)object;
         Vector hris = this.getHRIRecords(hrisync.getSRUID());
         hris.addElement(hrisync);
         this.checkAndAdd(hrisync.getSRUID());
         return true;
      } else {
         ServiceBookSyncCollection$SRSyncObject srsync = (ServiceBookSyncCollection$SRSyncObject)object;
         int uid = srsync.getUID();
         if (this.isServiceRestorable(srsync.getServiceRecord())) {
            this._serviceRecords.put(uid, srsync);
            this.checkAndAdd(uid);
            return true;
         } else {
            EventLogger.logEvent(-863050508581563378L, 1397969476, 3);
            this.sbEventOccurred(2522898683889177438L, srsync.getServiceRecord(), null, true);
            return true;
         }
      }
   }

   private final void convertInt(DataBuffer buffer, int type, int value, int length) {
      if (value != 0) {
         ConverterUtilities.convertInt(buffer, type, value, length);
      }
   }

   private final int htons(int value) {
      return value >> 8 & 0xFF | (value & 0xFF) << 8;
   }

   private final int htonl(int value) {
      return value >> 24 & 0xFF | value >> 8 & 0xFF00 | (value & 0xFF00) << 8 | (value & 0xFF) << 24;
   }

   private final void dumpDac(DAC dac, DataBuffer buffer) {
      if (!(dac instanceof IntDAC)) {
         if (dac instanceof IPv4UdpDAC) {
            long[] addrs = ((IPv4UdpDAC)dac).getAddresses();
            if (addrs != null) {
               int num = addrs.length;
               buffer.writeShort(4 * num);
               buffer.writeByte(16);
               boolean endian = buffer.isBigEndian();
               buffer.setBigEndian(true);

               for (int i = 0; i < num; i++) {
                  buffer.writeInt(IPv4UdpDAC.addr2IpAddress(addrs[i]));
               }

               buffer.setBigEndian(endian);
               buffer.writeShort(4 * num);
               buffer.writeByte(17);
               buffer.setBigEndian(true);

               for (int i = 0; i < num; i++) {
                  buffer.writeInt(IPv4UdpDAC.addr2DstPort(addrs[i]) << 16 | IPv4UdpDAC.addr2SrcPort(addrs[i]));
               }

               buffer.setBigEndian(endian);
            }
         }
      } else {
         int[] addrs = ((IntDAC)dac).getAddresses();
         if (addrs != null) {
            int num = addrs.length;
            buffer.writeShort(4 * num);
            buffer.writeByte(((IntDAC)dac).getFieldCode());
            buffer.setBigEndian(true);

            for (int i = 0; i < num; i++) {
               buffer.writeInt(addrs[i]);
            }

            buffer.setBigEndian(false);
            return;
         }
      }
   }

   private final ServiceRecord getServiceRecordFromSyncObject(Object object) {
      ServiceRecord sr = null;
      if (!(object instanceof ServiceBookSyncCollection$SRSyncObject)) {
         if (object instanceof ServiceRecord) {
            sr = (ServiceRecord)object;
         }

         return sr;
      } else {
         ServiceBookSyncCollection$SRSyncObject srsync = (ServiceBookSyncCollection$SRSyncObject)object;
         return srsync.getServiceRecord();
      }
   }

   private final void checkAndAdd(int serviceUID) {
      ServiceBookSyncCollection$SRSyncObject srsync = (ServiceBookSyncCollection$SRSyncObject)this._serviceRecords.get(serviceUID);
      if (srsync != null) {
         int[] assocHRI = srsync.getAssocHRI();
         Vector hris = this.getHRIRecords(serviceUID);
         boolean finished = assocHRI == null;
         if (assocHRI != null && hris.size() >= assocHRI.length) {
            this.prune(assocHRI, hris);
            if (assocHRI.length == hris.size()) {
               this._hriRecords.remove(serviceUID);
               int size = hris.size();
               HostRoutingInfo[] hriArray = new HostRoutingInfo[size];

               for (int i = 0; i < size; i++) {
                  hriArray[i] = ((ServiceBookSyncCollection$HRISyncObject)hris.elementAt(i)).getHRI();
               }

               HostRoutingTable hrt = new HostRoutingTable();
               hrt.setHris(hriArray);
               srsync.getServiceRecord().setAttachedHrt(hrt);
               finished = true;
            }
         }

         if (finished) {
            this._serviceRecords.remove(serviceUID);
            this._sb.addRecord(srsync.getServiceRecord());
         }
      }
   }

   private final void prune(int[] assocHRI, Vector hris) {
      int size = hris.size();
      int num = assocHRI.length;

      for (int i = 0; i < size; i++) {
         ServiceBookSyncCollection$HRISyncObject hrisync = (ServiceBookSyncCollection$HRISyncObject)hris.elementAt(i);
         int uid = hrisync.getUID();
         boolean found = false;

         for (int j = 0; j < num; j++) {
            if (assocHRI[j] == uid) {
               found = true;
               break;
            }
         }

         if (!found) {
            hris.removeElementAt(i);
            i--;
            size--;
         }
      }
   }

   private final void convertBinary(DataBuffer buffer, int type, byte[] value) {
      if (value != null) {
         ConverterUtilities.writeByteArray(buffer, type, value);
      }
   }

   private final Vector getHRIRecords(int serviceUID) {
      Vector result = (Vector)this._hriRecords.get(serviceUID);
      if (result == null) {
         result = new Vector();
         this._hriRecords.put(serviceUID, result);
      }

      return result;
   }

   public ServiceBookSyncCollection(ServiceBook sb) {
      this._sb = sb;
      this._schema = new SyncCollectionSchema();
      this._schema.setDefaultRecordType(1);
      this._schema.setRecordTypeTag(1);
      this._schema.setKeyFieldIds(1, KEY_FIELD_IDS_FOR_SERVICE_RECORD);
      this._collectionListenerManager = new CollectionListenerManager();
      this._blockedCIDTable = new Hashtable();
      Proxy.getInstance().addGlobalEventListener(this);
   }

   private final HostRoutingInfo parseHostRoutingInformation(DataBuffer buffer) {
      try {
         buffer.rewind();
         if (!ConverterUtilities.findType(buffer, 10)) {
            return null;
         }

         int tag = ConverterUtilities.getType(buffer);
         if (tag != 10) {
            return null;
         }

         int networkType = ConverterUtilities.readInt(buffer);
         HostRoutingInfo hri = HRUtils.newHriByNetType(networkType);
         buffer.rewind();

         while (buffer.available() > 0) {
            tag = ConverterUtilities.getType(buffer);
            switch (tag) {
               case 0:
               case 4:
               case 5:
               case 10:
               case 11:
               case 12:
               case 13:
               case 14:
               case 15:
                  buffer.skipBytes(buffer.readShort() + 1);
                  break;
               case 1:
                  if (networkType != 3 && networkType != 7) {
                     return null;
                  }

                  ((GprsHRI)hri).setApnPassword(ConverterUtilities.getBinaryString(buffer));
                  break;
               case 2:
                  if (networkType != 3 && networkType != 7) {
                     return null;
                  }

                  ((GprsHRI)hri).setApnUsername(ConverterUtilities.getBinaryString(buffer));
                  break;
               case 3:
                  if (networkType != 3 && networkType != 7) {
                     return null;
                  }

                  ((GprsHRI)hri).setApn(ConverterUtilities.getBinaryString(buffer));
                  break;
               case 6: {
                  long npc = ConverterUtilities.readInt(buffer) & 4294967295L;
                  if (npc == 0) {
                     switch (hri.getWirelessNetType()) {
                        case 2:
                           break;
                        case 3:
                        default:
                           npc = 48;
                           break;
                        case 4:
                           npc = 64;
                           break;
                        case 5:
                           npc = 80;
                     }
                  }

                  hri.setNpc(hri.getNpc() | npc);
                  break;
               }
               case 7: {
                  long npc = ConverterUtilities.readInt(buffer) & 4294967295L;
                  hri.setNpc(hri.getNpc() | npc << 32);
                  break;
               }
               case 8:
               default:
                  ((DAC)hri.getDac()).setLoadSharingCode(ConverterUtilities.readInt(buffer));
                  break;
               case 9:
                  hri.setName(ConverterUtilities.readString(buffer));
                  break;
               case 16:
               case 17:
                  if (networkType != 3 && networkType != 7 && networkType != 4 && networkType != 5) {
                     return null;
                  }

                  IPv4UdpDAC dac = (IPv4UdpDAC)hri.getDac();
                  int len = buffer.readShort();
                  buffer.skipBytes(1);
                  if (buffer.available() < len || len % 4 != 0) {
                     return null;
                  }

                  buffer.setBigEndian(true);
                  len /= 4;
                  long[] addrs;
                  if ((addrs = dac.getAddresses()) == null) {
                     addrs = new long[len];
                  }

                  for (int i = 0; i < len; i++) {
                     int srcPort;
                     int ipAddr;
                     int dstPort;
                     if (tag == 16) {
                        ipAddr = buffer.readInt();
                        dstPort = IPv4UdpDAC.addr2DstPort(addrs[i]);
                        srcPort = IPv4UdpDAC.addr2SrcPort(addrs[i]);
                     } else {
                        ipAddr = IPv4UdpDAC.addr2IpAddress(addrs[i]);
                        dstPort = buffer.readUnsignedShort();
                        srcPort = buffer.readUnsignedShort();
                     }

                     addrs[i] = IPv4UdpDAC.makeAddr(ipAddr, dstPort, srcPort);
                  }

                  buffer.setBigEndian(false);
                  dac.setAddresses(addrs);
                  break;
               case 18:
                  if (networkType != 3 && networkType != 7) {
                     return null;
                  }

                  byte[] qosBuf = ConverterUtilities.readByteArray(buffer);
                  GPRSQOSInfo qos = new GPRSQOSInfo(qosBuf[0] & 255, qosBuf[1] & 255, qosBuf[2] & 255, qosBuf[3] & 255, qosBuf[4] & 255);
                  ((GprsHRI)hri).setQos(qos);
            }
         }

         return hri;
      } finally {
         ;
      }
   }

   private final int generateHash(ServiceRecord sr) {
      return sr.getKeyHashForService();
   }

   private final ServiceRecord findMatchingServiceRecord(ServiceRecord sr) {
      ServiceRecord[] records = this._sb.getRecords();
      synchronized (records) {
         for (int i = records.length - 1; i >= 0; i--) {
            if (sr == records[i] || sr.isDuplicate(records[i], -1, null, null, null, null, -1)) {
               return records[i];
            }
         }

         return null;
      }
   }

   private final void convertString(DataBuffer buffer, int type, String value) {
      if (value != null) {
         ConverterUtilities.writeStringIntellisync(buffer, type, value);
      }
   }

   private final SyncObject getSyncObjectForServiceRecord(ServiceRecord sr) {
      if (sr == null) {
         return null;
      }

      int srUID = this.generateHash(sr);
      return this.getSyncObject(srUID);
   }

   private final void convertBinary(DataBuffer buffer, int type, String value) {
      if (value != null) {
         ConverterUtilities.convertBinary(buffer, type, value);
      }
   }

   public static final ServiceBookSyncCollection getInstance() {
      return (ServiceBookSyncCollection)ApplicationRegistry.getApplicationRegistry().waitForStartup(-7781445676016247535L);
   }
}
