package net.rim.device.api.servicebook;

import java.util.Vector;
import net.rim.device.api.crypto.SHA1Digest;
import net.rim.device.api.hrt.DAC;
import net.rim.device.api.hrt.GprsHRI;
import net.rim.device.api.hrt.HRUtils;
import net.rim.device.api.hrt.HostRoutingInfo;
import net.rim.device.api.hrt.HostRoutingTable;
import net.rim.device.api.hrt.IPv4UdpDAC;
import net.rim.device.api.hrt.IntDAC;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.util.CRC32;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.Persistable;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.api.util.TLEUtilities;
import net.rim.device.cldc.io.utility.URLParameters;
import net.rim.vm.Array;
import net.rim.vm.Memory;
import net.rim.vm.TraceBack;

public final class ServiceRecord implements Persistable, GlobalEventListener {
   private boolean _dirty;
   private int _id;
   private int _type;
   private int _lastCountedType;
   private String _name;
   private String _uid;
   private String _cid;
   private int _nameHash;
   private int _uidHash;
   private int _cidHash;
   private byte[] _appData;
   private int _encryptMode;
   private int _compMode;
   private String _caRealm;
   private String _caAddress;
   private short _caPort;
   private String _description;
   private HostRoutingTable _hrt;
   private int _flags;
   private int _source;
   private String _homeAddress;
   private byte[] _cryptoKeyBuffer;
   private int _cryptoKeyStart;
   private int _cryptoKeyLength;
   private String _dataSourceId;
   private int _userId = -1;
   private String[] _bbrHosts;
   private int[] _bbrPorts;
   private String[][] _bbrParameterKeys;
   private String[][] _bbrParameterValues;
   private long _lastUpdated;
   private int _serviceIdentifier;
   public static final int MOBITEX_WIRELESS_NET_TYPE = 2;
   public static final int GPRS_WIRELESS_NET_TYPE = 3;
   public static final int CDMA_WIRELESS_NET_TYPE = 4;
   public static final int IDEN_WIRELESS_NET_TYPE = 5;
   public static final int MAJOR_VERSION = 16;
   public static final int MINOR_VERSION = 1;
   public static final int VERSION = 17;
   public static final int SRT_NO_CHANGE = -1;
   public static final int SRT_ACTIVE = 0;
   public static final int SRT_PENDING = 1;
   public static final int SRT_GHOST = 2;
   public static final int SRT_OBSOLETE = 3;
   public static final int SRT_UNKNOWN = 4;
   public static final int SRT_ORPHAN = 5;
   public static final int SRT_DISALLOWED = 6;
   public static final int SRT_NUM_TYPES = 7;
   public static final int ENCRYPT_NONE = 1;
   public static final int ENCRYPT_RIM = 2;
   public static final int ENCRYPT_RIM_WEAK = 4;
   public static final int INVALID_ENCRYPTION_MODES = -8;
   public static final int COMPRESS_NONE = 1;
   public static final int COMPRESS_RIM = 2;
   public static final int INVALID_COMPRESSION_MODES = -4;
   private static final int PROTECTED_FLAG = 1;
   private static final int DISABLED_FLAG = 2;
   private static final int RESTORED_FLAG = 4;
   private static final int DISABLE_RESTORE_FLAG = 8;
   private static final int ENABLE_RESTORE_FLAG = 16;
   private static final int INVISIBLE_FLAG = 32;
   public static final int INVALID_RECORD_ID = -1;
   public static final int INVALID_USER_ID = -1;
   public static final int SOURCE_UNKNOWN = 0;
   public static final int SOURCE_SERIAL = 1;
   public static final int SOURCE_OTA = 2;
   public static final int SOURCE_CODE = 3;
   public static final int SOURCE_EDITOR = 4;
   public static final int SOURCE_OTA_SECURE = 5;
   public static final int NUM_SOURCES = 6;
   static final int DT_SERVICE_NAME = 1;
   static final int DT_ACCOUNT_ADDRESS = 2;
   static final int DT_HOME_ADDRESS = 3;
   static final int DT_NETWORK_ADDRESS = 4;
   static final int DT_TRANSPORT_LEVEL_PORT = 5;
   static final int DT_UID = 6;
   static final int DT_CID = 8;
   static final int DT_APPLICATION_DATA = 9;
   static final int DT_COMP_MODE = 10;
   static final int DT_ENCRYPT_MODE = 11;
   static final int DT_CA_REALM = 12;
   static final int DT_CA_ADDRESS = 13;
   static final int DT_CA_PORT = 14;
   static final int DT_CERTIFICATE_ID = 15;
   static final int DT_SERVICE_KEY_DATA = 20;
   static final int DT_HANDHELD_KEY_DATA = 30;
   static final int DT_HOST_ROUTING_INFO = 40;
   static final int DT_SERVICE_DESCRIPTION = 50;
   static final int DT_CURRENT_HOST_ROUTING_INFO = 41;
   static final int DT_HOST_ROUTING_INFOS = 42;
   static final int DT_RECORD_SOURCE = 43;
   static final int DT_RECORD_TYPE = 250;
   static final int DT_PROTECTED_FLAG = 251;
   static final int DT_FLAGS_FLAG = 252;
   static final int DT_SERVICE_APPLICATION = 160;
   static final int DT_DATASOURCE_ID = 161;
   static final int DT_BBR_ROUTING_INFO = 162;
   static final int DT_USER_ID = 163;
   static final int DT_ACKNOWLEDGEMENT_DATA_ID = 164;
   static final int DT_SERVICE_IDENTIFIER = 165;
   static final int PARSE_BAD_VERSION = 1346532978;
   static final int PARSE_MISSING_NAME = 1347251809;
   static final int PARSE_MISSING_UID = 1347253604;
   static final int PARSE_MISSING_CID = 1347248996;
   static final int SECURE_SEVICE_RECORD_DROPPED = 1397969476;

   public ServiceRecord() {
      this(3);
   }

   public ServiceRecord(int source) {
      this._id = -1;
      this._type = 4;
      this._lastCountedType = 4;
      this._encryptMode = 2;
      this._compMode = 2;
      this.setSource(source);
      this._dirty = false;
      this._lastUpdated = System.currentTimeMillis();
      this._serviceIdentifier = 0;
   }

   public final int getServiceIdentifierValue() {
      return this._serviceIdentifier;
   }

   final void setServiceIdentifierValue(int serviceIdentifier) {
      this._serviceIdentifier = serviceIdentifier;
   }

   public final void setServiceIdentifierType(short serviceIdentifierType) {
      this._serviceIdentifier &= -65536;
      this._serviceIdentifier |= serviceIdentifierType;
   }

   public final short getServiceIdentifierType() {
      return (short)(this._serviceIdentifier & 65535);
   }

   public final void setServiceIdentifierSubType(short serviceIdentifierSubType) {
      this._serviceIdentifier &= 65535;
      this._serviceIdentifier |= serviceIdentifierSubType << 16;
   }

   public final short getServiceIdentifierSubType() {
      return (short)(this._serviceIdentifier >> 16);
   }

   public final long getLastUpdated() {
      return this._lastUpdated;
   }

   public final void setLastUpdated(long time) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      this._lastUpdated = time;
   }

   public final int getId() {
      return this._id;
   }

   final void setId(int id) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      this._id = id;
      this._dirty = true;
   }

   public final int getType() {
      return this._type;
   }

   public final void setType(int type) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      if (type >= 0 && type < 7 && type != 4) {
         this._type = type;
         this._dirty = true;
      } else {
         throw new Object();
      }
   }

   public final String getName() {
      return this._name;
   }

   public final int getNameHash() {
      return this._nameHash;
   }

   public final void setName(String name) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      if (name != null && name.length() != 0) {
         this._name = name;
         this._nameHash = this.hashBytes(name.getBytes());
         this._dirty = true;
      } else {
         throw new Object();
      }
   }

   public final String getUid() {
      return this._uid;
   }

   public final int getUidHash() {
      return this._uidHash;
   }

   public final void setUid(String uid) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      if (uid == null) {
         throw new Object();
      }

      if (uid.length() > 0 && uid.length() < 128) {
         this._uid = Memory.stringIntern(uid);
         this._uidHash = this.hashBytes(StringUtilities.toLowerCase(uid, 1701707776).getBytes());
         this._dirty = true;
      } else {
         throw new Object();
      }
   }

   public final String getCid() {
      return this._cid;
   }

   public final int getCidHash() {
      return this._cidHash;
   }

   public final void setCid(String cid) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      if (cid == null) {
         throw new Object();
      }

      if (cid.length() > 0 && cid.length() < 128) {
         this._cid = Memory.stringIntern(cid);
         this._cidHash = this.hashBytes(StringUtilities.toLowerCase(cid, 1701707776).getBytes());
         this._dirty = true;
      } else {
         throw new Object();
      }
   }

   public final byte[] getApplicationData() {
      return this._appData;
   }

   public final void setApplicationData(byte[] appData) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      if (appData != null && appData.length == 0) {
         appData = null;
      }

      this._appData = appData;
      this._dirty = true;
   }

   public final int getEncryptionMode() {
      return this._encryptMode;
   }

   public final void setEncryptionMode(int mode) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      if (mode != 0 && (mode & -8) == 0) {
         this._encryptMode = mode;
         this._dirty = true;
      } else {
         throw new Object();
      }
   }

   public final int getCompressionMode() {
      return this._compMode;
   }

   public final void setCompressionMode(int mode) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      if (mode != 0 && (mode & -4) == 0) {
         this._compMode = mode;
         this._dirty = true;
      } else {
         throw new Object();
      }
   }

   public final String getCARealm() {
      return this._caRealm;
   }

   public final void setCARealm(String realm) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      if (realm != null) {
         if (realm.length() == 0) {
            realm = null;
         } else if (realm.length() > 127) {
            throw new Object();
         }
      }

      this._caRealm = realm;
      this._dirty = true;
   }

   public final String getCAAddress() {
      return this._caAddress;
   }

   public final void setCAAddress(String address) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      if (address != null) {
         if (address.length() == 0) {
            address = null;
         } else if (address.length() > 127) {
            throw new Object();
         }
      }

      this._caAddress = address;
      this._dirty = true;
   }

   public final short getCAPort() {
      return this._caPort;
   }

   public final void setCAPort(short port) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      this._caPort = port;
      this._dirty = true;
   }

   public final HostRoutingTable getAttachedHrt() {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      return this._hrt;
   }

   public final void setAttachedHrt(HostRoutingTable hrt) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      this._hrt = hrt;
      if (hrt != null) {
         hrt.setActiveIndex();
      }

      this._dirty = true;
   }

   public final String getNetworkAddress() {
      HostRoutingTable hrt = this.getAttachedHrt();

      try {
         HostRoutingInfo hri = hrt.getActiveHri();
         DAC d = hri.getDac();
         String address = this.extractAddress(d);
         if (address == null) {
            hrt = HRUtils.getDefaultHRT();
            hri = hrt.getActiveHri();
            d = hri.getDac();
            address = this.extractAddress(HRUtils.getDefaultHRT().getActiveHri().getDac());
         }

         return address;
      } finally {
         ;
      }
   }

   private final String extractAddress(DAC d) {
      if (!(d instanceof IntDAC)) {
         if (d instanceof IPv4UdpDAC) {
            IPv4UdpDAC id = (IPv4UdpDAC)d;
            long[] a = id.getAddresses();
            if (a != null && a.length > 0) {
               return IPv4UdpDAC.addr2String(a[0]);
            }
         }
      } else {
         IntDAC id = (IntDAC)d;
         int[] a = id.getAddresses();
         if (a != null && a.length > 0) {
            return id.addr2String(a[0]);
         }
      }

      return null;
   }

   public final int getNetworkType() {
      HostRoutingTable hrt = this.getAttachedHrt();

      try {
         HostRoutingInfo hri = hrt.getActiveHri();
         return hri.getWirelessNetType();
      } finally {
         ;
      }
   }

   public final String getAPN() {
      HostRoutingTable hrt = this.getAttachedHrt();

      try {
         HostRoutingInfo hri = hrt.getActiveHri();
         if (hri instanceof GprsHRI) {
            GprsHRI gh = (GprsHRI)hri;
            return gh.getApn();
         }
      } finally {
         return null;
      }

      return null;
   }

   public final String getDescription() {
      return this._description;
   }

   public final void setDescription(String descrip) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      if (descrip != null) {
         if (descrip.length() > 127) {
            descrip = descrip.substring(0, 126);
         } else if (descrip.length() == 0) {
            descrip = null;
         }
      }

      this._description = descrip;
      this._dirty = true;
   }

   public final boolean isRecordProtected() {
      return (this._flags & 1) != 0;
   }

   public final void setRecordProtected(boolean p) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      int i = 0;
      if (p) {
         i = 1;
      }

      this._flags = this._flags & -2 | i;
      this._dirty = true;
   }

   public final boolean isRestoredFromBackup() {
      return (this._flags & 4) != 0;
   }

   public final void setRestoredFromBackup(boolean p) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      int i = 0;
      if (p) {
         i = 4;
      }

      this._flags = this._flags & -5 | i;
      this._dirty = true;
   }

   public final boolean isDirty() {
      return this._hrt != null ? this._dirty | this._hrt.isDirty() : this._dirty | false;
   }

   public final void setDirty(boolean b) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      this._dirty = b;
      if (!b && this._hrt != null) {
         this._hrt.setDirty(false);
      }
   }

   public final DataBuffer getCryptoKey() {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      return (DataBuffer)(this._cryptoKeyBuffer != null ? new Object(this._cryptoKeyBuffer, this._cryptoKeyStart, this._cryptoKeyLength, true) : null);
   }

   public final void setCryptoKey(byte[] cryptoKey) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      this._cryptoKeyBuffer = cryptoKey;
      this._cryptoKeyStart = 0;
      this._cryptoKeyLength = cryptoKey.length;
   }

   public final void clearCryptoKey() {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      this._cryptoKeyBuffer = null;
      this._cryptoKeyStart = 0;
      this._cryptoKeyLength = 0;
   }

   final int getLastCountedType() {
      return this._lastCountedType;
   }

   final void setLastCountedType(int type) {
      this._lastCountedType = type;
   }

   public final String getHomeAddress() {
      return this._homeAddress;
   }

   public final void setHomeAddress(String homeAddr) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      if (homeAddr == null) {
         this._homeAddress = null;
      } else {
         if (homeAddr.length() >= 128) {
            throw new Object();
         }

         this._homeAddress = homeAddr;
      }

      this._dirty = true;
   }

   public final boolean getDisabledState() {
      return (this._flags & 2) > 0;
   }

   public final void setDisabledState(boolean disabled) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      if (disabled) {
         this._flags |= 2;
      } else {
         this._flags &= -3;
      }

      this._dirty = true;
   }

   final int getFlags() {
      return this._flags;
   }

   final void setFlags(int f) {
      this._flags = f;
   }

   public final int getSource() {
      return this._source;
   }

   public final void setSource(int source) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      if (source >= 0 && source < 6) {
         this._source = source;
         this._dirty = true;
      } else {
         throw new Object();
      }
   }

   public final String getDataSourceId() {
      return this._dataSourceId;
   }

   public final void setDataSourceId(String dataSourceId) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      if (dataSourceId != null) {
         dataSourceId = dataSourceId.trim();
         if (dataSourceId.length() == 0) {
            dataSourceId = null;
         }
      }

      this._dataSourceId = dataSourceId;
   }

   public final int getUserId() {
      return this._userId;
   }

   public final void setUserId(int userId) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      this._userId = userId;
   }

   public final String[] getBBRHosts() {
      return this._bbrHosts;
   }

   public final void setBBRHosts(String[] bbrHosts) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      this._bbrHosts = bbrHosts;
      this._dirty = true;
   }

   public final int[] getBBRPorts() {
      return this._bbrPorts;
   }

   public final void setBBRPorts(int[] bbrPorts) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      this._bbrPorts = bbrPorts;
      this._dirty = true;
   }

   public final URLParameters[] getBBRParameters() {
      URLParameters[] urlParameters = new Object[0];
      if (this._bbrHosts != null && this._bbrHosts.length > 0) {
         Array.resize(urlParameters, this._bbrHosts.length);

         for (int i = 0; i < this._bbrHosts.length; i++) {
            urlParameters[i] = (URLParameters)(new Object());
            if (this._bbrParameterKeys != null
               && this._bbrParameterValues != null
               && this._bbrParameterKeys.length == this._bbrHosts.length
               && this._bbrParameterValues.length == this._bbrHosts.length
               && this._bbrParameterKeys[i] != null
               && this._bbrParameterValues[i] != null) {
               for (int j = 0; j < this._bbrParameterKeys[i].length; j++) {
                  urlParameters[i].setParameter(this._bbrParameterKeys[i][j], this._bbrParameterValues[i][j]);
               }
            }
         }
      }

      return urlParameters;
   }

   public final void setBBRParameters(URLParameters[] urlParameters) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      this._dirty = true;
      if (urlParameters != null && urlParameters.length != 0) {
         this._bbrParameterKeys = new Object[urlParameters.length][0];
         this._bbrParameterValues = new Object[urlParameters.length][0];

         for (int i = 0; i < urlParameters.length; i++) {
            Vector keys = urlParameters[i].getKeys();
            if (keys.size() > 0) {
               Array.resize(this._bbrParameterKeys[i], keys.size());
               Array.resize(this._bbrParameterValues[i], keys.size());

               for (int j = 0; j < keys.size(); j++) {
                  this._bbrParameterKeys[i][j] = (String)keys.elementAt(j);
                  this._bbrParameterValues[i][j] = urlParameters[i].getValue(this._bbrParameterKeys[i][j]);
               }
            }
         }
      } else {
         this._bbrParameterKeys = (Object[][])null;
         this._bbrParameterValues = (Object[][])null;
      }
   }

   public final Transport getTransport() {
      TransportRegistry tr = TransportRegistry.getInstance();
      Transport t = tr.get(this._cid);
      if (t == null) {
         try {
            throw new Object(((StringBuffer)(new Object("SRNT: "))).append(this._cid).toString());
         } finally {
            return t;
         }
      } else {
         return t;
      }
   }

   private final int hashBytes(byte[] data) {
      SHA1Digest digest = (SHA1Digest)(new Object());
      digest.update(data);
      byte[] result = digest.getDigest();
      int ret = 0;

      for (int i = 3; i >= 0; i--) {
         ret <<= 8;
         ret |= result[i] & 255;
      }

      if (ret == 0) {
         ret++;
      }

      return ret;
   }

   public final boolean isDisabled() {
      if ((this._flags & 2) != 0) {
         return true;
      }

      HostRoutingTable hrt = this._hrt;
      if (hrt == null) {
         hrt = HRUtils.getDefaultHRT();
      }

      return hrt.getActiveIndex() == -1;
   }

   public final boolean isEncrypted() {
      return (this._encryptMode & 1) == 0;
   }

   public final boolean isSecureService() {
      return (this._encryptMode & 2) != 0;
   }

   public final boolean isWeakSecureService() {
      return (this._encryptMode & 4) != 0;
   }

   public final boolean isRestoreDisabled() {
      return (this._flags & 8) != 0;
   }

   public final boolean isRestoreEnabled() {
      return (this._flags & 16) != 0;
   }

   public final void setRestoreEnabled(boolean p) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      int i = 0;
      if (p) {
         i = 16;
      }

      this._flags = this._flags & -17 | i;
      this._dirty = true;
   }

   public final void setRestoreDisabled(boolean p) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      int i = 0;
      if (p) {
         i = 8;
      }

      this._flags = this._flags & -9 | i;
      this._dirty = true;
   }

   public final boolean isInvisible() {
      return (this._flags & 32) != 0;
   }

   public final void setInvisible(boolean invisibleFlag) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      int i = 0;
      if (invisibleFlag) {
         i = 32;
      }

      this._flags = this._flags & -33 | i;
      this._dirty = true;
   }

   public final int getKeyHashForService() {
      int result = 0;
      String uid = this.getUid();
      String cid = this.getCid();
      if (uid != null && cid != null) {
         byte[] uidBytes = uid.getBytes();
         byte[] cidBytes = cid.getBytes();
         if (uidBytes != null && uidBytes.length > 0) {
            result = CRC32.update(result, uidBytes);
         }

         if (cidBytes != null && cidBytes.length > 0) {
            result = CRC32.update(result, cidBytes);
         }

         int userid = this.getUserId();

         for (int i = 0; i < 4; i++) {
            result = CRC32.update(result, userid & 0xFF);
            userid >>= 8;
         }
      }

      return result;
   }

   public final void copyInto(ServiceRecord sr) {
      sr._type = this._type;
      sr._lastCountedType = this._lastCountedType;
      sr._name = this._name;
      sr._nameHash = this._nameHash;
      sr._homeAddress = this._homeAddress;
      sr._uid = this._uid;
      sr._uidHash = this._uidHash;
      sr._cid = this._cid;
      sr._cidHash = this._cidHash;
      sr._appData = this._appData;
      sr._encryptMode = this._encryptMode;
      sr._compMode = this._compMode;
      sr._caRealm = this._caRealm;
      sr._caAddress = this._caAddress;
      sr._caPort = this._caPort;
      sr._description = this._description;
      sr._hrt = this._hrt;
      sr._bbrHosts = this._bbrHosts;
      sr._bbrPorts = this._bbrPorts;
      sr._flags = this._flags;
      sr._source = this._source;
      sr._cryptoKeyBuffer = this._cryptoKeyBuffer;
      sr._cryptoKeyStart = this._cryptoKeyStart;
      sr._cryptoKeyLength = this._cryptoKeyLength;
      sr._dataSourceId = this._dataSourceId;
      sr._userId = this._userId;
      sr._serviceIdentifier = this._serviceIdentifier;
      sr._dirty = true;
   }

   public final void copyIntoServerLevel(ServiceRecord sr) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      sr._type = this._type;
      sr._lastCountedType = this._lastCountedType;
      sr._name = this._name;
      sr._nameHash = this._nameHash;
      sr._homeAddress = this._homeAddress;
      sr._uid = this._uid;
      sr._uidHash = this._uidHash;
      sr._encryptMode = this._encryptMode;
      sr._compMode = this._compMode;
      sr._caRealm = this._caRealm;
      sr._caAddress = this._caAddress;
      sr._caPort = this._caPort;
      sr._description = this._description;
      sr._hrt = this._hrt;
      sr._bbrHosts = this._bbrHosts;
      sr._bbrPorts = this._bbrPorts;
      sr._flags = this._flags;
      sr._source = this._source;
      sr._cryptoKeyBuffer = this._cryptoKeyBuffer;
      sr._cryptoKeyStart = this._cryptoKeyStart;
      sr._cryptoKeyLength = this._cryptoKeyLength;
      sr._dataSourceId = this._dataSourceId;
      sr._userId = this._userId;
      sr._serviceIdentifier = this._serviceIdentifier;
      sr._dirty = true;
   }

   public final boolean isDuplicate(ServiceRecord rec, int newType, String newName, String newUid, String newCid, String newDataSourceId, int newUserId) {
      if (rec != null) {
         String uid = newUid == null ? rec.getUid() : newUid;
         String cid = newCid == null ? rec.getCid() : newCid;
         String name = newName == null ? rec.getName() : newName;
         String dataSourceId = newName == null ? rec.getDataSourceId() : newDataSourceId;
         int userId = newUserId == -1 ? rec.getUserId() : newUserId;
         int type = newType == -1 ? rec.getType() : newType;
         if (rec != this && (type == this._type || type == 0 && this._type == 3) && StringUtilities.strEqualIgnoreCase(cid, this._cid, 1701707776)) {
            boolean checkSecure = this.isSecureService() && rec.isSecureService();
            boolean checkInsecure = !this.isSecureService() && !rec.isSecureService();
            if (ServiceBook.getSB().isCIDRegisteredAsSingleton(this._cid, checkSecure, checkInsecure)) {
               return true;
            }

            String sourceDataSourceId = this.getDataSourceId();
            if (sourceDataSourceId != null && dataSourceId != null && sourceDataSourceId.trim().length() > 0 && dataSourceId.trim().length() > 0) {
               if (StringUtilities.strEqualIgnoreCase(this.getDataSourceId(), dataSourceId, 1701707776)) {
                  return true;
               }

               if (this.getUserId() != -1 && userId != -1 && userId == this.getUserId()) {
                  return true;
               }
            } else if (this.getUserId() != -1 && userId != -1) {
               if (userId == this.getUserId()) {
                  return true;
               }
            } else if (StringUtilities.strEqualIgnoreCase(uid, this._uid, 1701707776) || name.equals(this._name)) {
               return true;
            }
         }
      }

      return false;
   }

   public final boolean isValid() {
      return this._name != null && this._name.length() != 0 && this._uid != null && this._uid.length() != 0 && this._cid != null && this._cid.length() != 0;
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      if (guid == -254931370837867202L && this._hrt != null) {
         this._hrt.eventOccurred(guid, data0, data1, object0, object1);
      }
   }

   public static final ServiceRecord[] parse(DataBuffer buf, int source) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      ServiceRecord[] serviceRecords = null;
      int logMe = 0;
      buf.setBigEndian(true);

      label98:
      try {
         int version = buf.readUnsignedByte();
         switch (version & 240) {
            case 0: {
               ServiceRecord sr = parseVersionZero(buf, source);
               serviceRecords = new ServiceRecord[]{sr};
               break;
            }
            case 16: {
               ServiceRecord sr = new ServiceRecord(source);
               ServiceRecord$SRParser p = new ServiceRecord$SRParser(sr);
               TLEUtilities.parseBuffer(buf, p);
               serviceRecords = new ServiceRecord[]{sr};
               break;
            }
            case 32: {
               serviceRecords = new ServiceRecord[0];
               ServiceRecord$SRParser p = new ServiceRecord$SRParser(serviceRecords, source);
               TLEUtilities.parseBuffer(buf, p);
               break;
            }
            default:
               logMe = 1346532978;
         }

         for (int index = 0; serviceRecords != null && index < serviceRecords.length; index++) {
            ServiceRecord var12 = serviceRecords[index];
            if ((source == 2 || source == 5) && var12.getCryptoKey() != null) {
               var12.setEncryptionMode(4);
            }

            if (var12 != null && var12.getName() == null) {
               logMe = 1347251809;
               serviceRecords = null;
            }

            if (var12 != null && var12.getUid() == null) {
               logMe = 1347253604;
               serviceRecords = null;
            }

            if (var12 != null && var12.getCid() == null) {
               logMe = 1347248996;
               serviceRecords = null;
            }
         }
      } finally {
         break label98;
      }

      if (logMe != 0) {
         EventLogger.logEvent(-863050508581563378L, logMe, 2);
      }

      return serviceRecords;
   }

   private static final ServiceRecord parseVersionZero(DataBuffer buf, int source) {
      ServiceRecord rec = new ServiceRecord(source);
      ServiceRecord$SRParser p = new ServiceRecord$SRParser(rec);
      rec.setName(ServiceRecord$SRParser.makeString(buf, 16));

      int type;
      while (!buf.eof() && (type = buf.readUnsignedByte()) != 0) {
         buf.setBigEndian(false);
         int length = buf.readInt();
         buf.setBigEndian(true);
         p.processField(type, length, buf);
      }

      rec.setCid("CMIME");
      return rec;
   }

   @Override
   public final String toString() {
      String result = "";
      return this._dataSourceId != null && this._userId != -1
         ? ((StringBuffer)(new Object())).append(this._dataSourceId).append("[").append(this._userId).append("]").toString()
         : ((StringBuffer)(new Object())).append(this._uid).append("[").append(this._name).append("]").toString();
   }
}
