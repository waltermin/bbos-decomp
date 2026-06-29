package net.rim.device.cldc.impl.hrt;

import java.io.IOException;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import net.rim.device.api.crypto.RandomSource;
import net.rim.device.api.crypto.RegistrationUtilities;
import net.rim.device.api.crypto.SHA1Digest;
import net.rim.device.api.hrt.GprsHRI;
import net.rim.device.api.hrt.HRUtils;
import net.rim.device.api.hrt.HostRoutingInfo;
import net.rim.device.api.hrt.HostRoutingTable;
import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.io.DatagramBase;
import net.rim.device.api.io.DatagramConnectionBase;
import net.rim.device.api.io.IOCancelledException;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.CDMAInfo;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.GPRSInfo;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.IDENInfo;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.QOSInfo;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.api.system.RealtimeClockListener;
import net.rim.device.api.system.SIMCard;
import net.rim.device.api.system.SIMCardException;
import net.rim.device.api.system.SIMCardStatusListener;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.api.util.TLEUtilities;
import net.rim.device.cldc.impl.gcmp.Gcmp;
import net.rim.device.cldc.io.datarecovery.DataRecovery;
import net.rim.device.cldc.io.gme.GMEDatagram;
import net.rim.device.cldc.io.mdp.MdpAddress;
import net.rim.device.cldc.io.tunnel.TunnelApnList;
import net.rim.device.cldc.io.tunnel.TunnelApnListFactory;
import net.rim.device.internal.system.NvStore;
import net.rim.device.internal.system.RadioInternal;
import net.rim.vm.Array;
import net.rim.vm.PersistentInteger;

public final class HRTRequestThread extends Thread implements SIMCardStatusListener, GlobalEventListener, Comparator, RadioStatusListener, RealtimeClockListener {
   private Gcmp _gcmp;
   private HostRoutingTable _defHrt;
   private HostRoutingTable _regHrt;
   private ServiceBook _sb;
   private DatagramConnectionBase _mdpConn;
   private DatagramConnectionBase _gmeConn;
   private HRTUpdaterThread _updaterThread;
   private Vector _eventQueue;
   private HRTRequestThread$ConnInfo[] _conns;
   private TunnelApnList _apnList;
   private int _lastActiveWaf = 0;
   private int _delayHint;
   private int _attemptCount;
   private int _currentProgress;
   private int _currentRequestId;
   private byte[] _requestKey;
   private int _registrationVersion;
   private int _currentCmd;
   private int _currentCmdFlashId;
   private long _lastNPCRegistered;
   private HRTReqThreadState _myState;
   private int _regCause;
   private byte[] _lastGid1;
   private byte[] _lastSpn;
   private Object _sendLock;
   private boolean _cancelled;
   private DatagramBase _sendDG;
   private DatagramConnectionBase _sendConn;
   private int _pendingSBHash;
   private long _dynamicPINTimeout;
   private boolean DYNAMIC_PIN_ENABLED = false;
   private HostRoutingInfo _reg_IIF_HRI;
   private boolean _sentTTLRegReq = false;
   private static final String HR_REG_THREAD_STR = "net.rim.hrtRT";
   public static final long HR_REQ_THREAD_GUID = 4019666953250015899L;
   private static final long HR_REQ_THREAD_COMMAND_GUID = 8565489477584473616L;
   private static final int VERSION_ID = PersistentInteger.getId(-4312012720738996190L, 5);
   private static final int SEND_EF_SPN_ID = PersistentInteger.getId(6723686775904720738L, 0);
   private static final int CMD_NONE = 0;
   private static final int CMD_GME_SYS_CHECK = 1;
   private static final int CMD_REG_INFO_PACKET = 2;
   private static final int CMD_SYS_CHECK_THEN_REG_INFO_PACKET = 3;
   private static final int CMD_REG_REQ = 5;
   private static final int CMD_DYNAMIC_PIN_REQ = 6;
   private static final int PROG_READY = 0;
   private static final int PROG_WAIT_FOR_CONTEXT = 1;
   private static final int PROG_WAIT_FOR_REG_RESP = 2;
   private static final int PROG_DONE = 3;
   private static final int PROG_NO_DATA_COVERAGE = 4;
   private static final int PROG_WAIT_FOR_KICK = 5;
   private static final int PROG_NO_ICCID_IMSI = 6;
   private static final int PROG_WAIT_ON_SEND_FAILURE = 7;
   private static final int PROG_NO_HOME_NPC = 8;
   private static final int EVENT_NONE = 0;
   private static final int EVENT_NEW_NPC = 1;
   private static final int EVENT_REGISTER_NOW = 3;
   private static final int EVENT_NEW_ACTIVE_HRI = 4;
   private static final int EVENT_ACTIVE_HRI_MODIFIED = 5;
   private static final int EVENT_NO_ACTIVE_HRI = 6;
   private static final int EVENT_SIM = 7;
   private static final int EVENT_SERVICE_BOOK_CHANGED = 8;
   private static final int EVENT_REG_RESP_RECEIVED = 9;
   private static final int EVENT_CONNECTION_ACTIVATED = 10;
   private static final int EVENT_CONNECTION_REFUSED = 11;
   private static final int EVENT_SEND_SYS_CHECK = 12;
   private static final int EVENT_ABORT_EXISTING = 13;
   private static final int EVENT_NEW_HOME_NPC = 14;
   private static final int EVENT_NEW_MDN = 15;
   private static final int EVENT_NEW_SPN = 16;
   private static final int EVENT_DEVICE_HAS_NO_PIN = 17;
   private static final int EVENT_TRY_NEXT_APN = 18;
   private static final int EVENT_NEW_IMSI = 19;
   private static final int EVENT_TTL_EXPIRED = 20;
   private static final int PB_ERROR = 0;
   private static final int PB_NEW_DEFAULT_HRT = 1;
   private static final int PB_EMPTY_RESPONSE = 2;
   private static final int PB_BAD_REF_ID = 3;
   private static final int PB_REG_ERROR = 4;
   private static final int PB_DYNAMIC_PIN_RESPONSE = 5;
   private static final int DELAY_SHORT = 500;
   private static final int DELAY_MEDIUM = 5000;
   private static final int DELAY_LONG = 30000;
   private static final int DELAY_SUPER_LONG = 90000;
   private static final int DELAY_NO_HINT = Integer.MAX_VALUE;
   private static final int CAUSE_NONE = 0;
   private static final int CAUSE_RESET = 1;
   private static final int CAUSE_NPC_CHANGE = 2;
   private static final int CAUSE_USER_REQUEST = 4;
   private static final int CAUSE_NO_ACTIVE_HRI = 8;
   private static final int CAUSE_SIM_CHANGE = 16;
   private static final int CAUSE_SB_CHANGE = 32;
   private static final int CAUSE_BAD_REG_RESPONSE = 64;
   private static final int CAUSE_FIRST_TIME_EVER = 128;
   private static final int CAUSE_BAD_REG_ID = 256;
   private static final int CAUSE_REG_RESP_TIMEOUT = 512;
   private static final int CAUSE_HOME_NPC_CHANGE = 1024;
   private static final int CAUSE_MDN_CHANGE = 2048;
   private static final int CAUSE_SPN_CHANGE = 4096;
   private static final int CAUSE_DOWNGRADE = 8192;
   private static final int CAUSE_DEVICE_HAS_NO_PIN = 16384;
   private static final int CAUSE_IMSI_CHANGE = 32768;
   private static final int CAUSE_TTL_EXPIRED = 65536;
   private static final int MAX_ATTEMPTS = 1;
   private static final int CONN_RELAY = 0;
   private static final int CONN_REG = 1;
   private static int SUPPORTED_WAFS = RadioInfo.getSupportedWAFs();
   private static String CDMA_IIF_APN;
   private static String CDMA_IIF_USERNAME;
   private static String CDMA_IIF_PASSWORD;
   private static final byte V3_REG_REQ_VERSION = 50;
   private static final byte V4_REG_REQ_VERSION = 64;
   private static final byte V5_REG_REQ_VERSION = 80;
   private static final byte REGISTRATION_REQUEST_PACKET = 7;
   private static final int REGISTRATION_RESPONSE_PACKET = 8;
   private static final byte DYNAMIC_PIN_REQUEST_PACKET = 9;
   private static final int DYNAMIC_PIN_RESPONSE_PACKET = 10;
   private static final int CRYPTO_SCHEMA_RSA_AES = 1;
   private static final int FIELD_DEVICE_INFORMATION = 128;
   private static final int FIELD_DEFAULT_HRT = 130;
   private static final int FIELD_REGISTRATION_HRT = 131;
   private static final int FIELD_SERVICE_LIST = 132;
   private static final int FIELD_SERVICE_ROUTING_INFO = 133;
   private static final int FIELD_HAVE_ROUTING_INFO_FLAG = 134;
   private static final int FIELD_REFERENCE_ID = 135;
   private static final int FIELD_REG_CAUSE = 136;
   private static final int FIELD_ASSIGNED_PIN = 137;
   private static final int FIELD_PENDING_LIFETIME = 138;
   private static final int FIELD_RESPONSE_KEY = 140;
   private static final int FIELD_SECURE_PIN_IP_KEY = 142;
   private static final int FIELD_TIME_TO_LIVE = 144;
   private static final int FIELD_REGISTRATION_ERROR = 224;
   private static final int FIELD_REGISTRATION_ERROR_MESSAGE = 225;
   private static final int ERROR_PROTOCOL_VERSION = 1;
   private static final int ERROR_PROVISIONING_SYSTEM = 2;
   private static final int ERROR_INVALID_FORMAT = 130;
   private static final int ERROR_INVALID_DATA = 131;
   private static final int ERROR_DECRYPTION_FAILED = 132;
   private static final int ERROR_INVALID_ENCRYPTION_KEY = 133;
   private static final int ERROR_ACCESS_DENIED = 134;
   private static final int ERROR_INVALID_PIN = 135;
   private static final int SERVICE_INFO_UID = 1;
   private static final int SERVICE_INFO_HRT = 2;
   private static final int SERVICE_INFO_DISABLED = 4;
   private static final int EL_REQT_ON = 1381257038;
   private static final int EL_REQT_OFF = 1381257030;
   private static final int EL_CANT_OPEN_CONNECTIONS = 1313034062;
   private static final int EL_CLOSE_REG_CONNECTION = 1128492103;
   private static final int EL_CLOSE_RELAY_CONNECTION = 1128492121;
   private static final int EL_REG_SERVER_PRESENT = 1381191534;
   private static final int EL_REG_SERVER_ABSENT = 1381191279;
   private static final int EL_COMMAND_DONE = 1128687982;
   private static final int EL_COMMAND_FAILED = 1128423780;
   private static final int EL_NEW_COMMAND = 1129211255;
   private static final int EL_COMMAND_DROPPED = 1128559216;
   private static final int EL_NO_COMMAND = 1129213806;
   private static final int EL_EQUAL_COMMAND = 1128624492;
   private static final int EL_DROPPED_REG_COMMAND = 1128551011;
   private static final int EL_EVENT_NEW_NPC = 1162768483;
   private static final int EL_EVENT_REG_NOW = 1163027815;
   private static final int EL_EVENT_NEW_ACTIVE_HRI = 1162375713;
   private static final int EL_EVENT_ACTIVE_HRI_MODIFIED = 1162375722;
   private static final int EL_EVENT_NO_ACTIVE_HRI = 1162375723;
   private static final int EL_EVENT_SIM = 1163094381;
   private static final int EL_EVENT_SB_CHANGED = 1163092587;
   private static final int EL_EVENT_REG_RESP = 1163022963;
   private static final int EL_EVENT_DYN_PIN_RESP = 1162891891;
   private static final int EL_EVENT_CONN_UP = 1162896496;
   private static final int EL_EVENT_TIMEOUT = 1163161460;
   private static final int EL_EVENT_CONN_REFUSED = 1162891882;
   private static final int EL_EVENT_SEND_SYS_CHECK = 1163098483;
   private static final int EL_EVENT_ABORT_EXISTING = 1161912948;
   private static final int EL_EVENT_NEW_HOME_NPC = 1162374478;
   private static final int EL_EVENT_NEW_MDN = 1162699886;
   private static final int EL_EVENT_NEW_IMSI = 1162440051;
   private static final int EL_EVENT_NEW_SPN = 1163096174;
   private static final int EL_EVENT_DEVICE_HAS_NO_PIN = 1162889550;
   private static final int EL_EVENT_TTL_EXPIRED = 1165259884;
   private static final int EL_NEW_SIM_HASH = 1399418145;
   private static final int EL_NEW_UID_HASH = 1432970273;
   private static final int EL_NEW_HOME_NPC_1 = 1215122987;
   private static final int EL_NEW_HOME_NPC_2 = 1215122977;
   private static final int EL_PENDING_SB_DROPPED = 1396845869;
   private static final int EL_PROG_NO_DATA_COVERAGE = 1347306563;
   private static final int EL_PROG_NO_HRI = 1347307634;
   private static final int EL_PROG_WAIT_FOR_CONTEXT = 1347892323;
   private static final int EL_PROG_NO_ICCID_IMSI = 1347307849;
   private static final int EL_PROG_WAIT_FOR_KICK = 1347892331;
   private static final int EL_PROG_NO_HOME_NPC = 1347307598;
   private static final int EL_EXECUTE_ABORT = 1480680052;
   private static final int EL_EXECUTE_FAILED = 1480745316;
   private static final int EL_EXECUTE_SYS_CHECK = 1481865587;
   private static final int EL_EXECUTE_V3_REG_REQ = 1482044274;
   private static final int EL_EXECUTE_V3_INFO = 1482044265;
   private static final int EL_EXECUTE_V4_REG_REQ = 1482044530;
   private static final int EL_EXECUTE_V4_INFO = 1482044521;
   private static final int EL_EXECUTE_V5_REG_REQ = 1482044786;
   private static final int EL_EXECUTE_V5_INFO = 1482044777;
   private static final int EL_EXECUTE_DYN_PIN_REQ = 1481658993;
   private static final int EL_EXECUTE_MAX_ATTEMPTS = 1481466232;
   private static final int EL_EXECUTE_CANCELED = 1480810862;
   private static final int EL_XMIT_MDP = 1483568205;
   private static final int EL_XMIT_GME = 1483568199;
   private static final int EL_CONN_ACTIVE = 1414885408;
   private static final int EL_CONN_INACTIVE = 1413773166;
   private static final int EL_CONN_EXCEPTION = 1413636452;
   private static final int EL_CONN_ACTIVATE = 1414686055;
   private static final int EL_WHOOPS_NO_PHONE_NO = 1315917902;
   private static final int EL_WHOOPS_NO_ICCID_IMSI = 1315916105;
   private static final int EL_WHOOPS_ICCID_IMSI_CHANGED = 1229546344;
   private static final int EL_WHOOPS_PHONE_NO_CHANGED = 1347314536;
   private static final int EL_WHOOPS_PHONE_NO_EXISTS = 1347315064;
   private static final int EL_WHOOPS_NO_HOME_NPC = 1315915854;
   private static final int EL_PDP_ACTIVATE_IGNORE = 1347776585;
   private static final int EL_PDP_REJ_IGNORE = 1347578441;
   private static final int EL_PDP_REJ_NON_FATAL = 1347578446;
   private static final int EL_PDP_REJ_FATAL = 1347578456;
   private static final int EL_PDP_TRY_NEXT_APN = 1347702337;
   private static final int EL_CRAZY_SIM = 1131575929;
   private static final int EL_SIM_INVALID = 1399418185;
   private static final int EL_DYNAMIC_PIN_RESPONSE_ERROR = 1347580741;
   private static final int EL_RESP_BAD_LENGTH = 1382835566;
   private static final int EL_RESP_BAD_SCHEMA = 1383293805;
   private static final int EL_RESP_UNMATCHED = 1383427693;
   private static final int EL_RESP_BAD_SIG = 1383295335;
   private static final int EL_RESP_ERROR_PROTOCOL_VERSION = 1380282486;
   private static final int EL_RESP_ERROR_PROVISIONING_SYSTEM = 1380282483;
   private static final int EL_RESP_ERROR_INVALID_FORMAT = 1380280678;
   private static final int EL_RESP_ERROR_INVALID_DATA = 1380280676;
   private static final int EL_RESP_ERROR_DECRYPTION_FAILED = 1380279398;
   private static final int EL_RESP_ERROR_INVALID_ENCRYPTION_KEY = 1380280683;
   private static final int EL_RESP_ERROR_ACCESS_DENIED = 1380278628;
   private static final int EL_RESP_ERROR_INVALID_PIN = 1380280688;
   private static final int EL_RESET_FIRST_AFTER_RESET = 1382441330;

   public final boolean isIdle() {
      return this._currentCmd == 0;
   }

   final void registerNow(boolean info) {
      this.addTriggerEvent(3, info ? 1 : 0, null);
   }

   final void enableThread(boolean enable) {
      if (enable) {
         this._myState.setFlag(1);
         this.logEvent(1381257038, 0);
      } else {
         this._myState.clearFlag(1);
         this.logEvent(1381257030, 2);
      }

      PersistentObject.commit(this._myState);
   }

   final void setRegServerPresent(boolean present) {
      int code;
      if (present) {
         this._myState.setFlag(2);
         code = 1381191534;
      } else {
         this._myState.clearFlag(2);
         code = 1381191279;
      }

      PersistentObject.commit(this._myState);
      this.logEvent(code, 0);
   }

   final void requestThreadAbort() {
      this.addTriggerEvent(13, 0, null);
      this.cancelSend();
   }

   final void useRegistrationVersion(int version) {
      if (version >= 3 && version <= 5 && this._registrationVersion != version) {
         this._registrationVersion = version;
         PersistentInteger.set(VERSION_ID, version);
      }
   }

   public final boolean toggleSendEFSPN() {
      int newState = PersistentInteger.get(SEND_EF_SPN_ID) == 1 ? 0 : 1;
      PersistentInteger.set(SEND_EF_SPN_ID, newState);
      return newState == 1;
   }

   public final void receivedRegResponse(Datagram dg) {
      this.addTriggerEvent(9, 0, dg);
   }

   @Override
   public final void cardReady() {
      this.addTriggerEvent(7, 1, null);
   }

   @Override
   public final void cardUpdated() {
      this.addTriggerEvent(7, 1, null);
   }

   @Override
   public final void cardInvalid(int reason, int subReason) {
      int event;
      if (reason == 20) {
         event = 1131575929;
      } else {
         event = 1399418185;
      }

      this.logEvent(event, 0);
   }

   @Override
   public final void cardFault(int reason) {
   }

   @Override
   public final void smsEFFull() {
   }

   @Override
   public final void responseDeleteSMS(int status, int packetId) {
   }

   @Override
   public final void responseMarkSMSAsRead(int status, int packetId) {
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      long param = 0;
      int kick;
      if (guid == -4220058463650496006L || guid == 2522898683889177438L || guid == 8288627527798139133L) {
         kick = 8;
      } else if (guid == -6531073315810526672L) {
         if (object0 != this._defHrt) {
            return;
         }

         kick = 4;
      } else if (guid == 2200641410611652722L) {
         if (object0 != this._defHrt) {
            return;
         }

         kick = 5;
      } else if (guid == -3864212166794284297L) {
         if (object0 != this._defHrt) {
            return;
         }

         kick = 6;
         param = data0;
      } else if (guid == -254931370837867202L) {
         kick = 1;
         param = data0 & 4294967295L | (long)data1 << 32;
      } else {
         if (guid != -8896339270692810071L) {
            return;
         }

         kick = 14;
      }

      this.addTriggerEvent(kick, param, null);
      if (guid == -254931370837867202L) {
         this.cancelSend();
      }
   }

   @Override
   public final void clockUpdated() {
      if (this._defHrt.getTtl() > 0 && !this._sentTTLRegReq && this._defHrt.getTtlExpiry() - System.currentTimeMillis() <= 0) {
         this._sentTTLRegReq = true;
         this.addTriggerEvent(20, 0, null);
      }
   }

   @Override
   public final void cardInserted() {
   }

   @Override
   public final void pdpStateChange(int apn, int state, int cause) {
      if (state == 0) {
         this.addTriggerEvent(10, apn, null);
      } else {
         if (state == 2) {
            long cookie = apn & 4294967295L;
            cookie |= (long)cause << 32;
            this.addTriggerEvent(11, cookie, null);
         }
      }
   }

   @Override
   public final void networkServiceChange(int networkId, int service) {
      this.checkWorldPhoneWafChange();
   }

   @Override
   public final void networkStarted(int networkId, int service) {
      this.checkWorldPhoneWafChange();
   }

   @Override
   public final void radioTurnedOff() {
   }

   @Override
   public final void signalLevel(int level) {
   }

   @Override
   public final void baseStationChange() {
   }

   @Override
   public final void networkStateChange(int state) {
   }

   @Override
   public final void networkScanComplete(boolean success) {
   }

   @Override
   public final int compare(Object o1, Object o2) {
      return ((String)o1).compareTo((String)o2);
   }

   private final int generateServiceBookHash() {
      SHA1Digest digest = new SHA1Digest();
      int newHash = 0;
      Vector uidVec = new Vector(10);
      ServiceRecord[] records = this._sb.findRecordsByType(0);

      for (int i = records.length - 1; i >= 0; i--) {
         String uid = records[i].getUid();
         String homeAddr = records[i].getHomeAddress();
         if (isValidUid(uid)) {
            uidVec.addElement(StringUtilities.toLowerCase(uid, 1701707776));
            if (homeAddr != null && homeAddr.length() != 0) {
               uidVec.addElement(homeAddr);
            }
         }
      }

      String[] uids = new String[uidVec.size()];
      uidVec.copyInto(uids);
      Arrays.sort(uids, this);

      for (int i = uids.length - 1; i >= 0; i--) {
         digest.update(uids[i].getBytes());
      }

      byte[] hash = digest.getDigest();

      for (int i = 0; i < 4; i++) {
         newHash <<= 8;
         newHash |= hash[i] & 255;
      }

      return newHash;
   }

   private final int parseBuffer(DataBuffer param1, HRTRequestThread$RegistrationInfo param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 2
      // 001: ifnonnull 00e
      // 004: new net/rim/device/cldc/impl/hrt/HRTRequestThread$RegistrationInfo
      // 007: dup
      // 008: invokespecial net/rim/device/cldc/impl/hrt/HRTRequestThread$RegistrationInfo.<init> ()V
      // 00b: goto 00f
      // 00e: aload 2
      // 00f: astore 3
      // 010: bipush 0
      // 011: istore 4
      // 013: aload 1
      // 014: bipush 1
      // 015: invokevirtual net/rim/device/api/util/DataBuffer.setBigEndian (Z)V
      // 018: aload 1
      // 019: invokevirtual net/rim/device/api/util/DataBuffer.readUnsignedByte ()I
      // 01c: sipush 240
      // 01f: iand
      // 020: istore 5
      // 022: aload 1
      // 023: invokevirtual net/rim/device/api/util/DataBuffer.readUnsignedByte ()I
      // 026: istore 6
      // 028: aload 0
      // 029: getfield net/rim/device/cldc/impl/hrt/HRTRequestThread._currentCmd I
      // 02c: bipush 6
      // 02e: if_icmpne 038
      // 031: iload 6
      // 033: bipush 10
      // 035: if_icmpne 048
      // 038: aload 0
      // 039: getfield net/rim/device/cldc/impl/hrt/HRTRequestThread._currentCmd I
      // 03c: bipush 6
      // 03e: if_icmpeq 050
      // 041: iload 6
      // 043: bipush 8
      // 045: if_icmpeq 050
      // 048: new java/lang/IllegalArgumentException
      // 04b: dup
      // 04c: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 04f: athrow
      // 050: iload 5
      // 052: lookupswitch 90 5 16 50 32 50 48 72 64 81 80 81
      // 084: aload 1
      // 085: aload 1
      // 086: invokevirtual net/rim/device/api/util/DataBuffer.getPosition ()I
      // 089: bipush 2
      // 08b: isub
      // 08c: invokevirtual net/rim/device/api/util/DataBuffer.setPosition (I)V
      // 08f: aload 3
      // 090: aload 1
      // 091: invokestatic net/rim/device/api/hrt/HRUtils.parseBuffer (Lnet/rim/device/api/util/DataBuffer;)[Lnet/rim/device/api/hrt/HostRoutingInfo;
      // 094: putfield net/rim/device/cldc/impl/hrt/HRTRequestThread$RegistrationInfo.defaultHris [Lnet/rim/device/api/hrt/HostRoutingInfo;
      // 097: goto 0b4
      // 09a: aload 0
      // 09b: aload 1
      // 09c: aload 3
      // 09d: invokespecial net/rim/device/cldc/impl/hrt/HRTRequestThread.parseVersionThree (Lnet/rim/device/api/util/DataBuffer;Lnet/rim/device/cldc/impl/hrt/HRTRequestThread$RegistrationInfo;)V
      // 0a0: goto 0b4
      // 0a3: aload 0
      // 0a4: aload 1
      // 0a5: aload 3
      // 0a6: invokespecial net/rim/device/cldc/impl/hrt/HRTRequestThread.parseVersionFour (Lnet/rim/device/api/util/DataBuffer;Lnet/rim/device/cldc/impl/hrt/HRTRequestThread$RegistrationInfo;)V
      // 0a9: goto 0b4
      // 0ac: new java/lang/IllegalArgumentException
      // 0af: dup
      // 0b0: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 0b3: athrow
      // 0b4: aload 0
      // 0b5: aconst_null
      // 0b6: putfield net/rim/device/cldc/impl/hrt/HRTRequestThread._requestKey [B
      // 0b9: aload 3
      // 0ba: getfield net/rim/device/cldc/impl/hrt/HRTRequestThread$RegistrationInfo.refId I
      // 0bd: ifeq 0ce
      // 0c0: aload 3
      // 0c1: getfield net/rim/device/cldc/impl/hrt/HRTRequestThread$RegistrationInfo.refId I
      // 0c4: aload 0
      // 0c5: getfield net/rim/device/cldc/impl/hrt/HRTRequestThread._currentRequestId I
      // 0c8: if_icmpeq 0ce
      // 0cb: bipush 3
      // 0cd: ireturn
      // 0ce: aload 3
      // 0cf: getfield net/rim/device/cldc/impl/hrt/HRTRequestThread$RegistrationInfo.regHris [Lnet/rim/device/api/hrt/HostRoutingInfo;
      // 0d2: ifnull 0e4
      // 0d5: aload 0
      // 0d6: getfield net/rim/device/cldc/impl/hrt/HRTRequestThread._regHrt Lnet/rim/device/api/hrt/HostRoutingTable;
      // 0d9: aload 3
      // 0da: getfield net/rim/device/cldc/impl/hrt/HRTRequestThread$RegistrationInfo.regHris [Lnet/rim/device/api/hrt/HostRoutingInfo;
      // 0dd: invokevirtual net/rim/device/api/hrt/HostRoutingTable.setHris ([Lnet/rim/device/api/hrt/HostRoutingInfo;)V
      // 0e0: aload 0
      // 0e1: invokespecial net/rim/device/cldc/impl/hrt/HRTRequestThread.initializeIIF_HRI ()V
      // 0e4: iload 6
      // 0e6: bipush 10
      // 0e8: if_icmpne 117
      // 0eb: aload 3
      // 0ec: getfield net/rim/device/cldc/impl/hrt/HRTRequestThread$RegistrationInfo.regError I
      // 0ef: ifne 10c
      // 0f2: aload 3
      // 0f3: getfield net/rim/device/cldc/impl/hrt/HRTRequestThread$RegistrationInfo.dynamicPIN I
      // 0f6: ifeq 10c
      // 0f9: aload 3
      // 0fa: getfield net/rim/device/cldc/impl/hrt/HRTRequestThread$RegistrationInfo.pendingLifetimeDynPIN I
      // 0fd: ifge 105
      // 100: aload 3
      // 101: bipush 0
      // 102: putfield net/rim/device/cldc/impl/hrt/HRTRequestThread$RegistrationInfo.pendingLifetimeDynPIN I
      // 105: bipush 5
      // 107: istore 4
      // 109: goto 1dd
      // 10c: aload 0
      // 10d: ldc_w 1347580741
      // 110: bipush 2
      // 112: invokespecial net/rim/device/cldc/impl/hrt/HRTRequestThread.logEvent (II)V
      // 115: bipush 0
      // 116: ireturn
      // 117: aload 3
      // 118: getfield net/rim/device/cldc/impl/hrt/HRTRequestThread$RegistrationInfo.defaultHris [Lnet/rim/device/api/hrt/HostRoutingInfo;
      // 11b: ifnonnull 140
      // 11e: bipush 2
      // 120: istore 4
      // 122: aload 0
      // 123: getfield net/rim/device/cldc/impl/hrt/HRTRequestThread._currentCmd I
      // 126: bipush 5
      // 128: if_icmpeq 12e
      // 12b: goto 1ca
      // 12e: aload 3
      // 12f: getfield net/rim/device/cldc/impl/hrt/HRTRequestThread$RegistrationInfo.regError I
      // 132: ifne 138
      // 135: goto 1ca
      // 138: aload 0
      // 139: bipush 0
      // 13a: invokespecial net/rim/device/cldc/impl/hrt/HRTRequestThread.setTimeToLive (I)V
      // 13d: goto 1ca
      // 140: aload 0
      // 141: aload 3
      // 142: getfield net/rim/device/cldc/impl/hrt/HRTRequestThread$RegistrationInfo.timeToLive I
      // 145: invokespecial net/rim/device/cldc/impl/hrt/HRTRequestThread.setTimeToLive (I)V
      // 148: aload 3
      // 149: getfield net/rim/device/cldc/impl/hrt/HRTRequestThread$RegistrationInfo.defaultHris [Lnet/rim/device/api/hrt/HostRoutingInfo;
      // 14c: invokestatic net/rim/device/api/hrt/HRUtils.checkPretendCDMA ([Lnet/rim/device/api/hrt/HostRoutingInfo;)V
      // 14f: aload 0
      // 150: getfield net/rim/device/cldc/impl/hrt/HRTRequestThread._defHrt Lnet/rim/device/api/hrt/HostRoutingTable;
      // 153: aload 3
      // 154: getfield net/rim/device/cldc/impl/hrt/HRTRequestThread$RegistrationInfo.defaultHris [Lnet/rim/device/api/hrt/HostRoutingInfo;
      // 157: bipush 1
      // 158: invokevirtual net/rim/device/api/hrt/HostRoutingTable.setHris ([Lnet/rim/device/api/hrt/HostRoutingInfo;Z)V
      // 15b: aload 0
      // 15c: getfield net/rim/device/cldc/impl/hrt/HRTRequestThread._regCause I
      // 15f: bipush 4
      // 161: iand
      // 162: ifne 17a
      // 165: aload 0
      // 166: getfield net/rim/device/cldc/impl/hrt/HRTRequestThread._regCause I
      // 169: bipush 16
      // 16b: iand
      // 16c: ifne 17a
      // 16f: aload 0
      // 170: getfield net/rim/device/cldc/impl/hrt/HRTRequestThread._regCause I
      // 173: sipush 128
      // 176: iand
      // 177: ifeq 181
      // 17a: ldc2_w -8927980184023446756
      // 17d: invokestatic net/rim/device/api/system/RIMGlobalMessagePoster.postGlobalEvent (J)Z
      // 180: pop
      // 181: bipush 1
      // 182: istore 4
      // 184: bipush 0
      // 185: istore 7
      // 187: aload 0
      // 188: getfield net/rim/device/cldc/impl/hrt/HRTRequestThread._myState Lnet/rim/device/cldc/impl/hrt/HRTReqThreadState;
      // 18b: bipush 4
      // 18d: invokevirtual net/rim/device/cldc/impl/hrt/HRTReqThreadState.isFlagSet (I)Z
      // 190: ifeq 19f
      // 193: aload 0
      // 194: getfield net/rim/device/cldc/impl/hrt/HRTRequestThread._myState Lnet/rim/device/cldc/impl/hrt/HRTReqThreadState;
      // 197: bipush 4
      // 199: invokevirtual net/rim/device/cldc/impl/hrt/HRTReqThreadState.clearFlag (I)V
      // 19c: bipush 1
      // 19d: istore 7
      // 19f: aload 0
      // 1a0: getfield net/rim/device/cldc/impl/hrt/HRTRequestThread._myState Lnet/rim/device/cldc/impl/hrt/HRTReqThreadState;
      // 1a3: getfield net/rim/device/cldc/impl/hrt/HRTReqThreadState.pendingLifetimeDynPIN J
      // 1a6: bipush 0
      // 1a7: i2l
      // 1a8: lcmp
      // 1a9: ifle 1be
      // 1ac: aload 0
      // 1ad: bipush 0
      // 1ae: i2l
      // 1af: putfield net/rim/device/cldc/impl/hrt/HRTRequestThread._dynamicPINTimeout J
      // 1b2: aload 0
      // 1b3: getfield net/rim/device/cldc/impl/hrt/HRTRequestThread._myState Lnet/rim/device/cldc/impl/hrt/HRTReqThreadState;
      // 1b6: bipush 0
      // 1b7: i2l
      // 1b8: putfield net/rim/device/cldc/impl/hrt/HRTReqThreadState.pendingLifetimeDynPIN J
      // 1bb: bipush 1
      // 1bc: istore 7
      // 1be: iload 7
      // 1c0: ifeq 1ca
      // 1c3: aload 0
      // 1c4: getfield net/rim/device/cldc/impl/hrt/HRTRequestThread._myState Lnet/rim/device/cldc/impl/hrt/HRTReqThreadState;
      // 1c7: invokestatic net/rim/device/api/system/PersistentObject.commit (Ljava/lang/Object;)V
      // 1ca: aload 0
      // 1cb: invokespecial net/rim/device/cldc/impl/hrt/HRTRequestThread.resetCommand ()V
      // 1ce: aload 0
      // 1cf: bipush 0
      // 1d0: putfield net/rim/device/cldc/impl/hrt/HRTRequestThread._regCause I
      // 1d3: goto 1dd
      // 1d6: astore 5
      // 1d8: goto 1dd
      // 1db: astore 5
      // 1dd: iload 4
      // 1df: ireturn
      // try (10 -> 78): 206 null
      // try (79 -> 112): 206 null
      // try (113 -> 205): 206 null
      // try (10 -> 78): 208 null
      // try (79 -> 112): 208 null
      // try (113 -> 205): 208 null
   }

   private final void parseVersionThree(DataBuffer buf, HRTRequestThread$RegistrationInfo regInfo) {
      int fieldType;
      while (!buf.eof() && (fieldType = buf.readUnsignedByte()) != 0) {
         int fieldSize = buf.readCompressedInt();
         int oldSize = buf.getLength();
         buf.setLength(buf.getPosition() + fieldSize);
         switch (fieldType) {
            case 130:
               regInfo.defaultHris = HRUtils.parseBuffer(buf);
               break;
            case 133:
               this.parseServiceRoutingInfo(buf);
               break;
            case 135:
               regInfo.refId = buf.readInt();
               break;
            case 137:
               regInfo.dynamicPIN = TLEUtilities.readIntegerFieldWithLength(buf, fieldSize);
               break;
            case 138:
               regInfo.pendingLifetimeDynPIN = TLEUtilities.readIntegerFieldWithLength(buf, fieldSize);
               break;
            case 144:
               regInfo.timeToLive = TLEUtilities.readIntegerFieldWithLength(buf, fieldSize);
               break;
            case 224:
               regInfo.regError = TLEUtilities.readIntegerFieldWithLength(buf, fieldSize);
               break;
            default:
               buf.skipBytes(fieldSize);
         }

         buf.setLength(oldSize);
      }
   }

   private final void parseVersionFour(DataBuffer buf, HRTRequestThread$RegistrationInfo regInfo) {
      int length = buf.readCompressedInt();
      if (length != buf.available()) {
         EventLogger.logEvent(4019666953250015899L, 1382835566, 3);
      }

      if (buf.readUnsignedByte() != 1) {
         EventLogger.logEvent(4019666953250015899L, 1383293805, 3);
         throw new IllegalArgumentException();
      }

      if (this._requestKey == null) {
         EventLogger.logEvent(4019666953250015899L, 1383427693, 3);
         throw new IllegalArgumentException();
      }

      int cryptoLength = buf.readCompressedInt();
      if (cryptoLength != RegistrationUtilities.getMACLength()) {
         EventLogger.logEvent(4019666953250015899L, 1382835566, 3);
      }

      byte[] sigResponse = new byte[cryptoLength];
      buf.readFully(sigResponse);
      byte[] cipher = new byte[buf.available()];
      buf.readFully(cipher);
      byte[] plain = new byte[cipher.length];
      int len = RegistrationUtilities.decryptBulkData(NvStore.readData(32), cipher, plain);
      Array.resize(plain, len);
      if (!RegistrationUtilities.checkMAC(plain, this._requestKey, sigResponse)) {
         EventLogger.logEvent(4019666953250015899L, 1383295335, 3);
         throw new IllegalArgumentException();
      }

      this.parseVersionThree(new DataBuffer(plain, 0, len, true), regInfo);
   }

   private final void parseServiceRoutingInfo(DataBuffer buf) {
      ServiceBook sb = ServiceBook.getSB();
      Vector uidList = new Vector();
      int hrisPos = -1;
      int hrisSize = 0;
      boolean disabled = false;

      int fieldType;
      while (!buf.eof() && (fieldType = buf.readUnsignedByte()) != 0) {
         int fieldSize = buf.readCompressedInt();
         switch (fieldType) {
            case 0:
            case 3:
               break;
            case 1:
            default:
               uidList.addElement(StringUtilities.cStr2String(buf.getArray(), buf.getArrayPosition(), fieldSize));
               break;
            case 2:
               hrisPos = buf.getPosition();
               hrisSize = fieldSize;
               break;
            case 4:
               disabled = true;
         }

         buf.skipBytes(fieldSize);
      }

      int oldPos = buf.getPosition();
      int oldSize = buf.getLength();

      for (int i = uidList.size() - 1; i >= 0; i--) {
         String uid = (String)uidList.elementAt(i);
         ServiceRecord[] srs = sb.findRecordsByUid(uid);

         for (int j = srs.length - 1; j >= 0; j--) {
            ServiceRecord sr = srs[j];
            HostRoutingTable hrt = null;
            if (sr != null) {
               if (hrisPos != -1) {
                  buf.setPosition(hrisPos);
                  buf.setLength(hrisPos + hrisSize);
                  HostRoutingInfo[] hris = HRUtils.parseBuffer(buf);
                  hrt = new HostRoutingTable();
                  hrt.setHris(hris);
               }

               sr.setAttachedHrt(hrt);
               sr.setDisabledState(disabled);
            }
         }
      }

      sb.commit();
      buf.setLength(oldSize);
      buf.setPosition(oldPos);
   }

   private final boolean isConnectionActive(HostRoutingInfo hri, HRTRequestThread$ConnInfo conn, boolean toRegServer) {
      String password = null;
      String username = null;
      QOSInfo qos = null;
      if (hri instanceof GprsHRI) {
         GprsHRI ghri = (GprsHRI)hri;
         password = ghri.getApnPassword();
         username = ghri.getApnUsername();
         qos = ghri.getQos();
      }

      return this.activateConnection(conn, this._apnList.getFirst(), qos, username, password, hri.getAddressBase(), toRegServer);
   }

   private final boolean activateConnection(
      HRTRequestThread$ConnInfo conn, String apn, QOSInfo qos, String apnUsername, String apnPassword, DatagramAddressBase addressBase, boolean toRegServer
   ) {
      if (apn == null) {
         return false;
      }

      if (conn.tunnel != null && !conn.tunnel.getConfig().getName().equalsIgnoreCase(apn)) {
         this.closeConnection(conn, true);
      }

      int apnId = conn.open(apn, qos, apnUsername, apnPassword);
      this.logEvent(1414686055, apnId, 5);
      conn.apnId = apnId;
      if (RadioInfo.isPDPContextActive(apnId)) {
         this.logEvent(1414885408, 5);
         if (!toRegServer) {
            int newId = this._gcmp.register(addressBase, "Relay");
            if (conn.connId != -1 && newId != conn.connId) {
               this._gcmp.deregister(conn.connId);
            }

            conn.connId = newId;
         }

         return true;
      } else {
         this.logEvent(1413773166, 5);
         return false;
      }
   }

   private final MdpAddress createMdpAddress(HostRoutingInfo hri) {
      return new MdpAddress(hri.getAddressBase(), 3, 3);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void initialize() {
      Application app = Application.getApplication();
      EventLogger.register(4019666953250015899L, "net.rim.hrtRT", 2);
      if (!this._myState.isFlagSet(1)) {
         this.logEvent(1381257030, 2);
      }

      this._regCause |= 1;
      app.addRadioListener(this);
      this._defHrt = HRUtils.getDefaultHRT();
      this._regHrt = HRUtils.getRegistrationHRT();
      this._apnList = TunnelApnListFactory.getTunnelApnListFactory().createTunnelApnList();
      this._sb = ServiceBook.getSB();
      app.addGlobalEventListener(this);
      app.addRealtimeClockListener(this);
      this._gcmp = Gcmp.getInstance();
      if (SIMCard.isSupported()) {
         SIMCard.addListener(app, this);
      }

      if ((SUPPORTED_WAFS & 1) != 0 && (SUPPORTED_WAFS & 2) != 0) {
         byte[] data = Branding.getData(13824);
         if (data != null) {
            CDMA_IIF_APN = new String(data);
         }

         if (CDMA_IIF_APN != null) {
            data = Branding.getData(13825);
            if (data != null) {
               CDMA_IIF_USERNAME = new String(data);
            }

            data = Branding.getData(13826);
            if (data != null) {
               CDMA_IIF_PASSWORD = new String(data);
            }

            this.initializeIIF_HRI();
         }
      }

      boolean var12 = false /* VF: Semaphore variable */;

      try {
         var12 = true;
         this._mdpConn = (DatagramConnectionBase)Connector.open("mdp::;3");
         this._gmeConn = (DatagramConnectionBase)Connector.open("gme:gme");
         this._gmeConn.setFlag(64, true);
         var12 = false;
      } finally {
         if (var12) {
            this.logEvent(1313034062, 1);
            throw new RuntimeException();
         }
      }

      this._updaterThread = new HRTUpdaterThread(this, this._mdpConn, this._defHrt);
      this._updaterThread.start();
      int deviceId = DeviceInfo.getDeviceId();
      int temporaryPIN = NvStore.readInt(21, 0);
      if (!this.DYNAMIC_PIN_ENABLED || deviceId != -1 && ((temporaryPIN & 4278190080L) != 4043309056L || deviceId != temporaryPIN)) {
         if (this._myState.pendingLifetimeDynPIN > 0) {
            this._dynamicPINTimeout = this._myState.pendingLifetimeDynPIN * 60000 + System.currentTimeMillis();
         }

         long npc = HRUtils.getNpcForActiveNetwork();
         if (npc != -1) {
            this.addTriggerEvent(1, npc, null);
            this._defHrt.setActiveIndex(npc);
            int event;
            if (this._defHrt.getActiveHri() != null) {
               event = 4;
            } else {
               event = 6;
            }

            this.addTriggerEvent(event, 0, null);
         }

         if (SIMCard.isSupported()) {
            label207:
            try {
               int simHash = this.generateSimHash();
               if (simHash != this._myState.simHash) {
                  this.addTriggerEvent(7, 0, null);
               }
            } finally {
               break label207;
            }
         }

         boolean queueHomeNPCChangedEvent = false;
         if ((SUPPORTED_WAFS & 1) != 0) {
            npc = HRUtils.getNpcForHomeNetwork(1);
            if (npc != -1 && this._myState.homeNPC_3GPP != npc) {
               queueHomeNPCChangedEvent = true;
            }
         }

         if ((SUPPORTED_WAFS & 2) != 0) {
            npc = HRUtils.getNpcForHomeNetwork(2);
            if (npc != -1 && this._myState.homeNPC_CDMA != npc) {
               queueHomeNPCChangedEvent = true;
            }
         }

         if ((SUPPORTED_WAFS & 8) != 0) {
            npc = HRUtils.getNpcForHomeNetwork(8);
            if (npc != -1 && this._myState.homeNPC_IDEN != npc) {
               queueHomeNPCChangedEvent = true;
            }
         }

         if (queueHomeNPCChangedEvent) {
            this.addTriggerEvent(14, 0, null);
         }
      } else {
         this.addTriggerEvent(17, 0, null);
      }

      if (this._currentCmd != 0) {
         this._delayHint = 30000;
      }
   }

   private final void initializeIIF_HRI() {
      if ((SUPPORTED_WAFS & 1) != 0 && (SUPPORTED_WAFS & 2) != 0 && CDMA_IIF_APN != null) {
         HostRoutingInfo[] hris = this._regHrt.getHris();

         for (int i = hris.length - 1; i >= 0; i--) {
            if (hris[i].getNpc() == 64) {
               this._reg_IIF_HRI = new GprsHRI();
               this._reg_IIF_HRI.setName("IIF GPRS to CDMA");
               this._reg_IIF_HRI.setNpc((long)48);
               this._reg_IIF_HRI.setApn(CDMA_IIF_APN);
               this._reg_IIF_HRI.setApnUsername(CDMA_IIF_USERNAME);
               this._reg_IIF_HRI.setApnPassword(CDMA_IIF_PASSWORD);
               this._reg_IIF_HRI.setDac(hris[i].getDac());
               return;
            }
         }
      }
   }

   private final boolean continueExecution() {
      if (this._myState.isFlagSet(1) && this._currentProgress != 5) {
         if (this._myState.isFlagSet(2)) {
            return true;
         }

         switch (this._currentCmd) {
            case 1:
            case 3:
            case 5:
               this._currentCmd = 1;
               return true;
            default:
               this.logEvent(1128551011, 0);
         }
      } else {
         this.logEvent(1480680052, 3);
      }

      this.resetCommand();
      return false;
   }

   @Override
   public final void run() {
      this.initialize();

      while (true) {
         this.delay();
         if (this.processEventQueue()) {
            if (this.continueExecution()) {
               HostRoutingInfo hri = this.prepareCommand();
               if (this._currentProgress == 0) {
                  synchronized (this._eventQueue) {
                     if (this._eventQueue.size() != 0) {
                        continue;
                     }
                  }

                  this.executeCommand(hri);
               }

               if (this._currentProgress == 3) {
                  this.logEvent(1128687982, 0);
                  this.resetCommand();
               }

               Object var5 = null;
            }
            continue;
         }
      }
   }

   private final void delay() {
      long timeout = 0;
      if (this._currentCmd != 0) {
         switch (this._currentProgress) {
            case -1:
            case 3:
            case 4:
            case 5:
               break;
            case 0:
               if (this._delayHint != Integer.MAX_VALUE) {
                  timeout = this._delayHint;
               } else {
                  timeout = 15000;
               }
               break;
            case 1:
            default:
               timeout = 900000;
               break;
            case 2:
            case 6:
            case 8:
               timeout = 300000;
               break;
            case 7:
               timeout = 300000;
               this._currentProgress = 0;
         }
      } else {
         this._delayHint = Integer.MAX_VALUE;
      }

      if (this._dynamicPINTimeout > 0) {
         long timeoutDiff = this._dynamicPINTimeout - System.currentTimeMillis();
         if (timeoutDiff > 0) {
            timeout = Math.min(timeout, timeoutDiff);
         }
      }

      synchronized (this._eventQueue) {
         if (this._eventQueue.size() == 0) {
            label81:
            try {
               this._eventQueue.wait(timeout);
            } finally {
               break label81;
            }

            if (this.DYNAMIC_PIN_ENABLED && this._dynamicPINTimeout > 0 && System.currentTimeMillis() - this._dynamicPINTimeout >= 0) {
               NvStore.deleteData(21);
               this._dynamicPINTimeout = 0;
               this._myState.pendingLifetimeDynPIN = 0;
               PersistentObject.commit(this._myState);
               this.addTriggerEvent(17, 0, null);
            } else if (this._eventQueue.size() == 0 && this._currentProgress == 2) {
               this._regCause |= 512;
            }
         }
      }
   }

   private final boolean processEventQueue() {
      boolean closeRegConn = false;
      boolean executeNow = false;
      synchronized (this._eventQueue) {
         if (this._eventQueue.size() == 0) {
            if (this._currentCmd != 0) {
               this.logEvent(1163161460, 0);
               return true;
            }

            return false;
         }
      }

      while (true) {
         label886: {
            int newDelayHint = Integer.MAX_VALUE;
            int newCmd = 0;
            int simHash = 0;
            int sbHash = 0;
            int extraEventInfo = 0;
            int oldRequestId = this._currentRequestId;
            HRTRequestThread$TriggerEvent event;
            synchronized (this._eventQueue) {
               if (this._eventQueue.size() == 0) {
                  break label886;
               }

               event = (HRTRequestThread$TriggerEvent)this._eventQueue.elementAt(0);
               this._eventQueue.removeElementAt(0);
            }

            label832:
            switch (event.event) {
               case 0:
               case 2:
                  break;
               case 1:
               default:
                  this.logEvent(1162768483, event.context, 0);
                  if (event.context != -1) {
                     newCmd = 1;
                     if (event.context != this._lastNPCRegistered) {
                        newCmd = 3;
                        this._lastNPCRegistered = event.context;
                        if (this._currentProgress == 2) {
                           this._currentProgress = 0;
                        }

                        if (this._currentCmd == 5 && newCmd == 3 && (this._regCause & 101524) == 0) {
                           this.resetCommand();
                           this._regCause = 0;
                        }

                        this._currentRequestId++;
                        this._regCause |= 2;
                     }

                     if (this._currentProgress == 4) {
                        this._currentProgress = 0;
                     }

                     newDelayHint = 5000;
                  } else if (this._currentCmd != 0 && this._currentProgress != 5) {
                     this._currentProgress = 4;
                  }
                  break;
               case 3:
                  this.logEvent(1163027815, 0);
                  newCmd = event.context == 0 ? 5 : 2;
                  newDelayHint = 500;
                  this._currentRequestId++;
                  this._regCause |= 4;
                  break;
               case 4:
                  this.logEvent(1162375713, 0);
                  newCmd = 1;
                  newDelayHint = 90000;
                  break;
               case 5:
                  this.logEvent(1162375722, 0);
                  newCmd = 1;
                  newDelayHint = 5000;
                  break;
               case 6:
                  if (event.context == 0 && HRUtils.getNpcForActiveNetwork() != -1) {
                     this.logEvent(1162375723, 0);
                     newCmd = 5;
                     newDelayHint = 5000;
                     this.closeConnection(this._conns[0], false);
                     this._currentRequestId++;
                     this._regCause |= 8;
                  } else {
                     this.logEvent(1162375723, 3);
                  }
                  break;
               case 7:
                  if (event.context == 1) {
                     this._lastGid1 = readEF(9);
                     this._lastSpn = readEF(11);
                  }

                  try {
                     simHash = this.generateSimHash();
                     if (simHash != this._myState.simHash) {
                        this.logEvent(1399418145, 5);
                        this._myState.simHash = simHash;
                        PersistentObject.commit(this._myState);
                        newCmd = 5;
                        newDelayHint = 30000;
                        extraEventInfo = 1;
                        HRUtils.setPretendCDMA(false);
                        this._defHrt.setHris(null);
                        this._apnList.resetList();

                        for (int i = this._conns.length - 1; i >= 0; i--) {
                           this.closeConnection(this._conns[i], true);
                        }

                        this._currentRequestId++;
                        this._regCause |= 16;
                     }

                     if (this._currentProgress == 6 && this.areICCIDAndIMSIPopulated()) {
                        this._currentProgress = 0;
                     }
                  } finally {
                     ;
                  }

                  this.logEvent(1163094381, extraEventInfo, 0);
                  break;
               case 8:
                  sbHash = this.generateServiceBookHash();
                  byte var46;
                  if (sbHash != this._myState.uidHash) {
                     if (this._currentProgress != 2) {
                        this.logEvent(1432970273, 5);
                        this._myState.uidHash = sbHash;
                        PersistentObject.commit(this._myState);
                        newCmd = 2;
                        newDelayHint = 30000;
                        var46 = 1;
                        this._currentRequestId++;
                        this._regCause |= 32;
                     } else {
                        var46 = 2;
                        this._pendingSBHash = sbHash;
                     }
                  } else {
                     var46 = 0;
                  }

                  this.logEvent(1163092587, var46, 0);
                  break;
               case 9:
                  DatagramBase dg = (DatagramBase)event.obj;
                  HostRoutingInfo hri = this._regHrt.getActiveHri();
                  if (HRUtils.isWorldPhone() && (RadioInfo.getActiveWAFs() & 1) != 0) {
                     label767:
                     try {
                        String apn = RadioInfo.getAccessPointName(this._conns[1].apnId);
                        if (apn != null && CDMA_IIF_APN != null && apn.equalsIgnoreCase(CDMA_IIF_APN)) {
                           hri = this._reg_IIF_HRI;
                        }
                     } finally {
                        break label767;
                     }
                  }

                  if (hri != null && hri.rcvdFromAddress(dg.getAddressBase().getSubAddressBase())) {
                     HRTRequestThread$RegistrationInfo regInfo = new HRTRequestThread$RegistrationInfo();
                     int parsedRetCode = this.parseBuffer(dg, regInfo);
                     switch (parsedRetCode) {
                        case -1:
                        case 4:
                           throw new RuntimeException();
                        case 0:
                           this._regCause |= 64;
                           extraEventInfo = -1;
                           break;
                        case 1:
                           HostRoutingTable hrt = HRUtils.getDefaultHRT();
                           if (hrt != null) {
                              int numHris = hrt.getNumHris();
                              extraEventInfo = numHris > 0 ? numHris : -4;
                           } else {
                              extraEventInfo = -3;
                           }

                           newCmd = 1;
                           newDelayHint = 5000;
                           executeNow = true;
                           if (this._pendingSBHash != 0) {
                              this._pendingSBHash = 0;
                              this.addTriggerEvent(8, 0, null);
                           }
                           break;
                        case 2:
                        default:
                           extraEventInfo = 0;
                           closeRegConn = true;
                           if (this._pendingSBHash != 0) {
                              this._pendingSBHash = 0;
                              this.addTriggerEvent(8, 0, null);
                           }
                           break;
                        case 3:
                           this._regCause |= 256;
                           extraEventInfo = -2;
                           break;
                        case 5:
                           this.setDeviceId(regInfo.dynamicPIN);
                           if (regInfo.pendingLifetimeDynPIN > 0) {
                              this._myState.pendingLifetimeDynPIN = regInfo.pendingLifetimeDynPIN;
                              PersistentObject.commit(this._myState);
                              this._dynamicPINTimeout = this._myState.pendingLifetimeDynPIN * 60000 + System.currentTimeMillis();
                           } else {
                              this._dynamicPINTimeout = 0;
                           }

                           StringBuffer tempBuffer = new StringBuffer();
                           tempBuffer.append(StringUtilities.intToString(1162891891));
                           tempBuffer.append(":");
                           tempBuffer.append(StringUtilities.toUpperCase(Integer.toHexString(DeviceInfo.getDeviceId()), 1701707776));
                           tempBuffer.append(";");
                           tempBuffer.append(String.valueOf(this._myState.pendingLifetimeDynPIN));
                           EventLogger.logEvent(4019666953250015899L, tempBuffer.toString().getBytes(), 0);
                           this.resetCommand();
                           newCmd = 5;
                           newDelayHint = 500;
                           this._regCause |= 128;
                           executeNow = true;
                     }

                     int elLog = 0;
                     switch (regInfo.regError) {
                        case 1:
                           elLog = 1380282486;
                           break;
                        case 2:
                           elLog = 1380282483;
                           break;
                        case 130:
                           elLog = 1380280678;
                           break;
                        case 131:
                           parsedRetCode = 4;
                           elLog = 1380280676;
                           break;
                        case 132:
                           elLog = 1380279398;
                           break;
                        case 133:
                           elLog = 1380280683;
                           break;
                        case 134:
                           elLog = 1380278628;
                           break;
                        case 135:
                           elLog = 1380280688;
                     }

                     if (elLog != 0) {
                        this.logEvent(elLog, (regInfo.regError & 128) != 0 ? 2 : 3);
                        if (parsedRetCode == 4) {
                           DataRecovery.getInstance().fileReport(2);
                        }
                     }

                     HRTRequestThread$RegistrationInfo var53 = null;
                     this.logEvent(1163022963, extraEventInfo, 0);
                  }
                  break;
               case 10:
                  int apn = (int)event.context;
                  this.logEvent(1162896496, event.context, 0);
                  if (isCDMAConnection(apn)) {
                     this.checkCDMAParams();
                  }

                  int i = this._conns.length - 1;

                  while (i >= 0 && this._conns[i].apnId != apn) {
                     i--;
                  }

                  if (i < 0) {
                     this.logEvent(1347776585, 0);
                  } else {
                     int activeWAFs = RadioInfo.getActiveWAFs();
                     if ((activeWAFs & 1) != 0 && (!Arrays.equals(this._lastGid1, readEF(9)) || !Arrays.equals(this._lastSpn, readEF(11)))) {
                        this.addTriggerEvent(16, 0, null);
                     }

                     newCmd = this._currentCmd;
                     if (this._currentProgress == 1) {
                        this._currentProgress = 0;
                        synchronized (this._eventQueue) {
                           if (this._eventQueue.size() == 0) {
                              executeNow = true;
                           }
                        }
                     }
                  }
                  break;
               case 11:
                  int apn = (int)event.context;
                  int cause = (int)(event.context >> 32);
                  this.logEvent(1162891882, event.context, 0);
                  newCmd = 0;
                  if (this._currentCmd == 0) {
                     this.logEvent(1347578441, 5);
                  } else {
                     int i = this._conns.length - 1;

                     while (i >= 0 && this._conns[i].apnId != apn) {
                        i--;
                     }

                     if (i < 0) {
                        this.logEvent(1347578441, 5);
                     } else {
                        switch (cause) {
                           case 26:
                           case 34:
                           case 35:
                           case 36:
                           case 38:
                           case 39:
                           case 102:
                           case 111:
                              this.logEvent(1347578446, 3);
                              break label832;
                           default:
                              this.logEvent(1347578456, 3);
                              this._currentProgress = 5;
                              this._apnList.removeFirst();
                              if (cause != 201) {
                                 for (int var60 = this._conns.length - 1; var60 >= 0; var60--) {
                                    if (this._conns[var60].apnId == apn) {
                                       this.closeConnection(this._conns[var60], true);
                                    }
                                 }
                              }

                              if (this._apnList.getSize() > 0) {
                                 this.addTriggerEvent(18, 0, null);
                              }
                        }
                     }
                  }
                  break;
               case 12:
                  this.logEvent(1163098483, 0);
                  newCmd = 1;
                  newDelayHint = 5000;
                  break;
               case 13:
                  this.logEvent(1161912948, 0);
                  this.resetCommand();
                  synchronized (this._eventQueue) {
                     this._eventQueue.removeAllElements();
                     return false;
                  }
               case 14:
                  this.logEvent(1162374478, 0);
                  boolean homeNpcChanged = false;
                  boolean validHomeNpcFound = false;
                  if ((SUPPORTED_WAFS & 1) != 0) {
                     long newNpc = HRUtils.getNpcForHomeNetwork(1);
                     if (newNpc != -1) {
                        validHomeNpcFound = true;
                        if (newNpc != this._myState.homeNPC_3GPP) {
                           this._myState.homeNPC_3GPP = newNpc;
                           this.logEvent(1215122987, this._myState.homeNPC_3GPP, 0);
                           homeNpcChanged = true;
                        }
                     }
                  }

                  if ((SUPPORTED_WAFS & 2) != 0) {
                     long newNpc = HRUtils.getNpcForHomeNetwork(2);
                     if (newNpc != -1) {
                        validHomeNpcFound = true;
                        if (newNpc != this._myState.homeNPC_CDMA) {
                           this._myState.homeNPC_CDMA = newNpc;
                           this.logEvent(1215122987, this._myState.homeNPC_CDMA, 0);
                           homeNpcChanged = true;
                        }
                     }
                  }

                  if ((SUPPORTED_WAFS & 8) != 0) {
                     long newNpc = HRUtils.getNpcForHomeNetwork(8);
                     if (newNpc != -1) {
                        validHomeNpcFound = true;
                        if (newNpc != this._myState.homeNPC_IDEN) {
                           this._myState.homeNPC_IDEN = newNpc;
                           this.logEvent(1215122987, this._myState.homeNPC_IDEN, 0);
                           homeNpcChanged = true;
                        }
                     }
                  }

                  if (validHomeNpcFound) {
                     if (this._currentProgress == 8) {
                        this._currentProgress = 0;
                     }

                     if (homeNpcChanged) {
                        this._defHrt.setHris(null);
                        newCmd = 5;
                        newDelayHint = 500;
                        this._currentRequestId++;
                        this._regCause |= 1024;
                     }
                  }
                  break;
               case 15:
                  this.logEvent(1162699886, 0);
                  newCmd = 5;
                  newDelayHint = 500;
                  this._currentRequestId++;
                  this._regCause |= 2048;
                  break;
               case 16:
                  if (!Arrays.equals(this._lastGid1, readEF(9)) || !Arrays.equals(this._lastSpn, readEF(11))) {
                     this.logEvent(1163096174, 0);
                     this._lastGid1 = readEF(9);
                     this._lastSpn = readEF(11);
                     newCmd = 5;
                     newDelayHint = 500;
                     this._currentRequestId++;
                     this._regCause |= 4096;
                  }
                  break;
               case 17:
                  this.logEvent(1162889550, 0);
                  if (this._registrationVersion >= 4) {
                     this._defHrt.removeAll();
                     newCmd = 6;
                     newDelayHint = 500;
                     this._regCause = 16384;
                     this._currentRequestId++;
                  }
                  break;
               case 18:
                  this.logEvent(1347702337, 3);
                  newCmd = 5;
                  newDelayHint = 500;
                  break;
               case 19:
                  this.logEvent(1162440051, 0);
                  newCmd = 5;
                  newDelayHint = 500;
                  this._currentRequestId++;
                  this._regCause |= 32768;
                  break;
               case 20:
                  this.logEvent(1165259884, this._defHrt.getTtl(), 0);
                  newCmd = 5;
                  newDelayHint = 500;
                  this._currentRequestId++;
                  this._regCause |= 65536;
            }

            if (this._currentCmd == 1 && newCmd == 2 || this._currentCmd == 2 && newCmd == 1) {
               newCmd = 3;
            }

            if (this._currentProgress == 5 && newCmd != 0) {
               this._currentProgress = 0;
               this._attemptCount = 0;
            }

            if (newCmd == 2 || newCmd == 3 || newCmd == 5 || newCmd == 6) {
               closeRegConn = false;
            }

            int logMe;
            if (newCmd > this._currentCmd) {
               logMe = 1129211255;
               this._currentCmd = newCmd;
               this._currentProgress = 0;
               this._attemptCount = 0;
               PersistentInteger.set(this._currentCmdFlashId, this._currentCmd);
            } else if (newCmd == 0) {
               logMe = 1129213806;
            } else if (newCmd == this._currentCmd) {
               logMe = 1128624492;
               this._currentProgress = 0;
            } else {
               logMe = 1128559216;
               this._currentRequestId = oldRequestId;
            }

            this.logEvent(logMe, 5);
            if (newDelayHint < this._delayHint) {
               this._delayHint = newDelayHint;
            }
            continue;
         }

         if (closeRegConn) {
            this.closeConnection(this._conns[1], true);
         }

         return executeNow;
      }
   }

   private final boolean validateHomeNpc() {
      int activeWAFs = RadioInfo.getActiveWAFs();
      boolean homeNPCAvailable = false;
      if ((SUPPORTED_WAFS & 1) != 0) {
         long npc = HRUtils.getNpcForHomeNetwork(1);
         if (npc != -1) {
            homeNPCAvailable = true;
         } else if ((activeWAFs & 1) != 0) {
            return false;
         }
      }

      if ((SUPPORTED_WAFS & 2) != 0) {
         long npc = HRUtils.getNpcForHomeNetwork(2);
         if (npc != -1) {
            homeNPCAvailable = true;
         } else if ((activeWAFs & 2) != 0) {
            return false;
         }
      }

      if ((SUPPORTED_WAFS & 8) != 0) {
         long npc = HRUtils.getNpcForHomeNetwork(8);
         if (npc != -1) {
            return true;
         }

         if ((activeWAFs & 8) != 0) {
            return false;
         }
      }

      return homeNPCAvailable;
   }

   private final void executeCommand(HostRoutingInfo param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: getfield net/rim/device/cldc/impl/hrt/HRTRequestThread._currentCmd I
      // 004: tableswitch 44 0 6 283 44 98 65 283 155 212
      // 030: aload 0
      // 031: ldc_w 1481865587
      // 034: bipush 0
      // 035: invokespecial net/rim/device/cldc/impl/hrt/HRTRequestThread.logEvent (II)V
      // 038: aload 0
      // 039: invokespecial net/rim/device/cldc/impl/hrt/HRTRequestThread.sendSysCheck ()V
      // 03c: aload 0
      // 03d: bipush 3
      // 03f: putfield net/rim/device/cldc/impl/hrt/HRTRequestThread._currentProgress I
      // 042: goto 11f
      // 045: aload 0
      // 046: ldc_w 1481865587
      // 049: bipush 0
      // 04a: invokespecial net/rim/device/cldc/impl/hrt/HRTRequestThread.logEvent (II)V
      // 04d: aload 0
      // 04e: invokespecial net/rim/device/cldc/impl/hrt/HRTRequestThread.sendSysCheck ()V
      // 051: aload 0
      // 052: bipush 2
      // 054: putfield net/rim/device/cldc/impl/hrt/HRTRequestThread._currentCmd I
      // 057: aload 0
      // 058: bipush 0
      // 059: putfield net/rim/device/cldc/impl/hrt/HRTRequestThread._currentProgress I
      // 05c: aload 0
      // 05d: sipush 5000
      // 060: putfield net/rim/device/cldc/impl/hrt/HRTRequestThread._delayHint I
      // 063: goto 11f
      // 066: aload 0
      // 067: aload 1
      // 068: bipush 0
      // 069: bipush 0
      // 06a: invokespecial net/rim/device/cldc/impl/hrt/HRTRequestThread.sendRegReq (Lnet/rim/device/api/hrt/HostRoutingInfo;ZZ)V
      // 06d: aload 0
      // 06e: bipush 2
      // 070: putfield net/rim/device/cldc/impl/hrt/HRTRequestThread._currentProgress I
      // 073: aload 0
      // 074: getfield net/rim/device/cldc/impl/hrt/HRTRequestThread._pendingSBHash I
      // 077: ifne 07d
      // 07a: goto 11f
      // 07d: aload 0
      // 07e: ldc_w 1396845869
      // 081: bipush 0
      // 082: invokespecial net/rim/device/cldc/impl/hrt/HRTRequestThread.logEvent (II)V
      // 085: aload 0
      // 086: getfield net/rim/device/cldc/impl/hrt/HRTRequestThread._myState Lnet/rim/device/cldc/impl/hrt/HRTReqThreadState;
      // 089: aload 0
      // 08a: getfield net/rim/device/cldc/impl/hrt/HRTRequestThread._pendingSBHash I
      // 08d: putfield net/rim/device/cldc/impl/hrt/HRTReqThreadState.uidHash I
      // 090: aload 0
      // 091: getfield net/rim/device/cldc/impl/hrt/HRTRequestThread._myState Lnet/rim/device/cldc/impl/hrt/HRTReqThreadState;
      // 094: invokestatic net/rim/device/api/system/PersistentObject.commit (Ljava/lang/Object;)V
      // 097: aload 0
      // 098: bipush 0
      // 099: putfield net/rim/device/cldc/impl/hrt/HRTRequestThread._pendingSBHash I
      // 09c: goto 11f
      // 09f: aload 0
      // 0a0: aload 1
      // 0a1: bipush 1
      // 0a2: bipush 0
      // 0a3: invokespecial net/rim/device/cldc/impl/hrt/HRTRequestThread.sendRegReq (Lnet/rim/device/api/hrt/HostRoutingInfo;ZZ)V
      // 0a6: aload 0
      // 0a7: bipush 2
      // 0a9: putfield net/rim/device/cldc/impl/hrt/HRTRequestThread._currentProgress I
      // 0ac: aload 0
      // 0ad: getfield net/rim/device/cldc/impl/hrt/HRTRequestThread._pendingSBHash I
      // 0b0: ifne 0b6
      // 0b3: goto 11f
      // 0b6: aload 0
      // 0b7: ldc_w 1396845869
      // 0ba: bipush 0
      // 0bb: invokespecial net/rim/device/cldc/impl/hrt/HRTRequestThread.logEvent (II)V
      // 0be: aload 0
      // 0bf: getfield net/rim/device/cldc/impl/hrt/HRTRequestThread._myState Lnet/rim/device/cldc/impl/hrt/HRTReqThreadState;
      // 0c2: aload 0
      // 0c3: getfield net/rim/device/cldc/impl/hrt/HRTRequestThread._pendingSBHash I
      // 0c6: putfield net/rim/device/cldc/impl/hrt/HRTReqThreadState.uidHash I
      // 0c9: aload 0
      // 0ca: getfield net/rim/device/cldc/impl/hrt/HRTRequestThread._myState Lnet/rim/device/cldc/impl/hrt/HRTReqThreadState;
      // 0cd: invokestatic net/rim/device/api/system/PersistentObject.commit (Ljava/lang/Object;)V
      // 0d0: aload 0
      // 0d1: bipush 0
      // 0d2: putfield net/rim/device/cldc/impl/hrt/HRTRequestThread._pendingSBHash I
      // 0d5: goto 11f
      // 0d8: aload 0
      // 0d9: invokespecial net/rim/device/cldc/impl/hrt/HRTRequestThread.setTemporaryPIN ()V
      // 0dc: new java/lang/StringBuffer
      // 0df: dup
      // 0e0: ldc_w 1481658993
      // 0e3: invokestatic net/rim/device/api/util/StringUtilities.intToString (I)Ljava/lang/String;
      // 0e6: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 0e9: astore 2
      // 0ea: aload 2
      // 0eb: ldc_w ":"
      // 0ee: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0f1: pop
      // 0f2: aload 2
      // 0f3: invokestatic net/rim/device/api/system/DeviceInfo.getDeviceId ()I
      // 0f6: invokestatic java/lang/Integer.toHexString (I)Ljava/lang/String;
      // 0f9: ldc_w 1701707776
      // 0fc: invokestatic net/rim/device/api/util/StringUtilities.toUpperCase (Ljava/lang/String;I)Ljava/lang/String;
      // 0ff: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 102: pop
      // 103: ldc2_w 4019666953250015899
      // 106: aload 2
      // 107: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 10a: invokevirtual java/lang/String.getBytes ()[B
      // 10d: bipush 0
      // 10e: invokestatic net/rim/device/api/system/EventLogger.logEvent (J[BI)Z
      // 111: pop
      // 112: aload 0
      // 113: aload 1
      // 114: bipush 0
      // 115: bipush 1
      // 116: invokespecial net/rim/device/cldc/impl/hrt/HRTRequestThread.sendRegReq (Lnet/rim/device/api/hrt/HostRoutingInfo;ZZ)V
      // 119: aload 0
      // 11a: bipush 2
      // 11c: putfield net/rim/device/cldc/impl/hrt/HRTRequestThread._currentProgress I
      // 11f: aload 0
      // 120: aload 0
      // 121: getfield net/rim/device/cldc/impl/hrt/HRTRequestThread._attemptCount I
      // 124: bipush 1
      // 125: iadd
      // 126: putfield net/rim/device/cldc/impl/hrt/HRTRequestThread._attemptCount I
      // 129: return
      // 12a: astore 2
      // 12b: aload 0
      // 12c: bipush 0
      // 12d: putfield net/rim/device/cldc/impl/hrt/HRTRequestThread._cancelled Z
      // 130: aload 0
      // 131: ldc_w 1480810862
      // 134: bipush 0
      // 135: invokespecial net/rim/device/cldc/impl/hrt/HRTRequestThread.logEvent (II)V
      // 138: return
      // 139: astore 2
      // 13a: aload 0
      // 13b: ldc_w 1480745316
      // 13e: bipush 3
      // 140: invokespecial net/rim/device/cldc/impl/hrt/HRTRequestThread.logEvent (II)V
      // 143: aload 0
      // 144: bipush 7
      // 146: putfield net/rim/device/cldc/impl/hrt/HRTRequestThread._currentProgress I
      // 149: aload 0
      // 14a: aload 0
      // 14b: getfield net/rim/device/cldc/impl/hrt/HRTRequestThread._attemptCount I
      // 14e: bipush 1
      // 14f: iadd
      // 150: dup_x1
      // 151: putfield net/rim/device/cldc/impl/hrt/HRTRequestThread._attemptCount I
      // 154: bipush 1
      // 155: if_icmplt 1b2
      // 158: aload 0
      // 159: ldc_w 1481466232
      // 15c: bipush 3
      // 15e: invokespecial net/rim/device/cldc/impl/hrt/HRTRequestThread.logEvent (II)V
      // 161: aload 0
      // 162: getfield net/rim/device/cldc/impl/hrt/HRTRequestThread._currentCmd I
      // 165: bipush 1
      // 166: if_icmpne 16f
      // 169: aload 0
      // 16a: bipush 0
      // 16b: putfield net/rim/device/cldc/impl/hrt/HRTRequestThread._currentCmd I
      // 16e: return
      // 16f: aload 0
      // 170: bipush 5
      // 172: putfield net/rim/device/cldc/impl/hrt/HRTRequestThread._currentProgress I
      // 175: aload 0
      // 176: getfield net/rim/device/cldc/impl/hrt/HRTRequestThread._currentCmd I
      // 179: bipush 5
      // 17b: if_icmpeq 187
      // 17e: aload 0
      // 17f: getfield net/rim/device/cldc/impl/hrt/HRTRequestThread._currentCmd I
      // 182: bipush 2
      // 184: if_icmpne 1b2
      // 187: aload 0
      // 188: getfield net/rim/device/cldc/impl/hrt/HRTRequestThread._currentCmd I
      // 18b: bipush 2
      // 18d: if_icmpeq 1a7
      // 190: aload 0
      // 191: getfield net/rim/device/cldc/impl/hrt/HRTRequestThread._defHrt Lnet/rim/device/api/hrt/HostRoutingTable;
      // 194: invokevirtual net/rim/device/api/hrt/HostRoutingTable.getActiveHri ()Lnet/rim/device/api/hrt/HostRoutingInfo;
      // 197: ifnull 1a7
      // 19a: aload 0
      // 19b: invokespecial net/rim/device/cldc/impl/hrt/HRTRequestThread.resetCommand ()V
      // 19e: aload 0
      // 19f: bipush 12
      // 1a1: bipush 0
      // 1a2: i2l
      // 1a3: aconst_null
      // 1a4: invokespecial net/rim/device/cldc/impl/hrt/HRTRequestThread.addTriggerEvent (IJLjava/lang/Object;)V
      // 1a7: aload 0
      // 1a8: aload 0
      // 1a9: getfield net/rim/device/cldc/impl/hrt/HRTRequestThread._conns [Lnet/rim/device/cldc/impl/hrt/HRTRequestThread$ConnInfo;
      // 1ac: bipush 1
      // 1ad: aaload
      // 1ae: bipush 1
      // 1af: invokespecial net/rim/device/cldc/impl/hrt/HRTRequestThread.closeConnection (Lnet/rim/device/cldc/impl/hrt/HRTRequestThread$ConnInfo;Z)V
      // 1b2: return
      // try (0 -> 125): 126 null
      // try (0 -> 125): 135 null
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void sendRegPacket(DatagramConnectionBase conn, DatagramBase dg, HostRoutingInfo hri, int evlEvent) {
      boolean sendOk = false;
      boolean var19 = false /* VF: Semaphore variable */;

      try {
         var19 = true;
         synchronized (this._eventQueue) {
            synchronized (this._sendLock) {
               if (this._eventQueue.size() != 0 || this._cancelled) {
                  throw new IOCancelledException();
               }

               this._sendConn = conn;
               this._sendDG = dg;
            }
         }

         this.logEvent(evlEvent, 5);
         conn.send(dg);
         sendOk = true;
         var19 = false;
      } finally {
         if (var19) {
            synchronized (this._sendLock) {
               this._sendDG = null;
               this._sendConn = null;
               this._cancelled = false;
            }

            if (hri != null) {
               if (sendOk) {
                  hri.sendSuccessful();
               } else {
                  hri.handleSendError(this._attemptCount + 1);
               }
            }
         }
      }

      synchronized (this._sendLock) {
         this._sendDG = null;
         this._sendConn = null;
         this._cancelled = false;
      }

      if (hri != null) {
         if (sendOk) {
            hri.sendSuccessful();
         } else {
            hri.handleSendError(this._attemptCount + 1);
         }
      }
   }

   private final void cancelSend() {
      try {
         synchronized (this._sendLock) {
            if (this._sendDG != null) {
               this._cancelled = true;
               this._sendConn.cancel(this._sendDG);
            }
         }
      } finally {
         return;
      }
   }

   private final void sendRegReq(HostRoutingInfo hri, boolean requestDefaultHrt, boolean isDynamicPINRequest) {
      switch (this._registrationVersion) {
         case 3:
            this.logEvent(requestDefaultHrt ? 1482044274 : 1482044265, this._regCause, 0);
            this.sendLegacyRegReq(hri, requestDefaultHrt);
         case 2:
            return;
         case 4:
            this.logEvent(requestDefaultHrt ? 1482044530 : 1482044521, this._regCause, 0);
            this.sendRegReq(hri, (byte)64, (byte)(!isDynamicPINRequest ? 7 : 9), requestDefaultHrt);
            return;
         case 5:
         default:
            this.logEvent(requestDefaultHrt ? 1482044786 : 1482044777, this._regCause, 0);
            this.sendRegReq(hri, (byte)80, (byte)(!isDynamicPINRequest ? 7 : 9), requestDefaultHrt);
      }
   }

   private final void sendLegacyRegReq(HostRoutingInfo hri, boolean requestDefaultHrt) {
      HRTRequestThread$RequestFieldWriter w = new HRTRequestThread$RequestFieldWriter(0, this._conns[1].apnId, this._sb);
      DatagramBase regRequest = (DatagramBase)this._mdpConn.newDatagram(256);
      regRequest.writeByte(50);
      regRequest.writeByte(7);
      TLEUtilities.writeIntegerField(regRequest, 135, ++this._currentRequestId, true);
      if (!requestDefaultHrt) {
         TLEUtilities.writeIntegerField(regRequest, 134, 1, false);
      }

      TLEUtilities.writeField(regRequest, 128, w);
      if (this._sb.getNumRecords(0) > 0) {
         w.setMode(1);
         TLEUtilities.writeField(regRequest, 132, w);
      }

      TLEUtilities.writeIntegerField(regRequest, 136, this._regCause, true);
      regRequest.writeByte(0);
      regRequest.setAddressBase(this.createMdpAddress(hri));
      this.sendRegPacket(this._mdpConn, regRequest, hri, 1483568205);
   }

   private final void sendRegReq(HostRoutingInfo hri, byte version, byte type, boolean requestDefaultHrt) {
      this._requestKey = RegistrationUtilities.generateRandomSessionKey();
      DatagramBase dgram = (DatagramBase)this._mdpConn.newDatagram(256);
      dgram.setAddressBase(this.createMdpAddress(hri));
      dgram.setLength(7);
      dgram.writeByte(version);
      dgram.writeByte(type);
      dgram.skipBytes(5);
      TLEUtilities.writeDataField(dgram, 1, RegistrationUtilities.encryptSessionKey(this._requestKey));
      DataBuffer buf = new DataBuffer(256, true);
      TLEUtilities.writeIntegerField(buf, 135, ++this._currentRequestId, true);
      TLEUtilities.writeIntegerField(buf, 136, this._regCause, true);
      if (!requestDefaultHrt) {
         TLEUtilities.writeIntegerField(buf, 134, 1, false);
      }

      TLEUtilities.writeDataField(buf, 140, NvStore.readData(32));
      if (this._gcmp != null) {
         TLEUtilities.writeDataField(buf, 142, this._gcmp.getSecurePinIpKeyData());
      }

      HRTRequestThread$RequestFieldWriter w = new HRTRequestThread$RequestFieldWriter(0, this._conns[1].apnId, this._sb);
      TLEUtilities.writeField(buf, 128, w);
      if (this._sb.getNumRecords(0) > 0) {
         w.setMode(1);
         TLEUtilities.writeField(buf, 132, w);
      }

      buf.writeByte(0);
      byte[] data = new byte[RegistrationUtilities.getCipherTextLength(buf.getLength())];
      RegistrationUtilities.encryptBulkData(this._requestKey, buf.toArray(), data);
      dgram.write(data);
      int size = dgram.getLength() - 7;
      dgram.setPosition(2);
      dgram.writeByte(size >> 28 | 128);
      dgram.writeByte(size >> 21 | 128);
      dgram.writeByte(size >> 14 | 128);
      dgram.writeByte(size >> 7 | 128);
      dgram.writeByte(size & 127);
      dgram.setPosition(dgram.getLength());
      this.sendRegPacket(this._mdpConn, dgram, hri, 1483568205);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void sendSysCheck() {
      GMEDatagram dg = (GMEDatagram)this._gmeConn.newDatagram(0);
      dg.setCommandByte(10);
      boolean var15 = false /* VF: Semaphore variable */;

      try {
         var15 = true;
         synchronized (this._eventQueue) {
            synchronized (this._sendLock) {
               if (this._eventQueue.size() != 0 || this._cancelled) {
                  throw new IOCancelledException();
               }

               this._sendConn = this._gmeConn;
               this._sendDG = dg;
            }
         }

         this.logEvent(1483568199, 5);
         this._gmeConn.send(dg);
         var15 = false;
      } finally {
         if (var15) {
            synchronized (this._sendLock) {
               this._sendDG = null;
               this._sendConn = null;
               this._cancelled = false;
            }
         }
      }

      synchronized (this._sendLock) {
         this._sendDG = null;
         this._sendConn = null;
         this._cancelled = false;
      }
   }

   private final void closeConnection(HRTRequestThread$ConnInfo conn, boolean deregisterPdp) {
      boolean closeGcmp = conn.connId != -1;
      int event;
      if (conn == this._conns[1]) {
         event = 1128492103;
      } else {
         event = 1128492121;
      }

      if (closeGcmp) {
         this._gcmp.deregister(conn.connId);
      }

      conn.connId = -1;
      if (deregisterPdp && conn.apnId != -1) {
         this.logEvent(event, conn.apnId, 0);
         if (conn.tunnel != null) {
            conn.tunnel.close();
            conn.tunnel = null;
         }

         conn.apnId = -1;
      }
   }

   private final void setTimeToLive(int ttl) {
      this._defHrt.setTtl(ttl > 0 ? ttl + RandomSource.getInt(86400) : 0);
      this._sentTTLRegReq = false;
      this._defHrt.commit();
   }

   public HRTRequestThread(HRTReqThreadState state) {
      this._currentProgress = 0;
      this._delayHint = Integer.MAX_VALUE;
      this._myState = state;
      this._lastNPCRegistered = -1;
      this._regCause = this._myState.isFlagSet(4) ? 128 : 0;
      this._sendLock = new Object();
      this._conns = new HRTRequestThread$ConnInfo[2];

      for (int i = this._conns.length - 1; i >= 0; i--) {
         this._conns[i] = new HRTRequestThread$ConnInfo();
      }

      this._eventQueue = new Vector();
      this._currentCmdFlashId = PersistentInteger.getId(8565489477584473616L, 0);
      this._currentCmd = PersistentInteger.get(this._currentCmdFlashId);
      if (NvStore.readData(32) == null) {
         NvStore.writeData(32, RegistrationUtilities.generateRandomSessionKey());
      }

      this._registrationVersion = PersistentInteger.get(VERSION_ID);
   }

   private final HostRoutingInfo prepareCommand() {
      HostRoutingTable hrt = null;
      HRTRequestThread$ConnInfo conn = null;
      boolean regServerContact = false;
      if (HRUtils.getNpcForActiveNetwork() == -1) {
         this.logEvent(1347306563, 3);
         this._currentProgress = 4;
         return null;
      }

      switch (this._currentCmd) {
         case 0:
         case 4:
            throw new RuntimeException();
         case 1:
         case 3:
         default:
            hrt = this._defHrt;
            conn = this._conns[0];
            break;
         case 2:
         case 5:
         case 6:
            hrt = this._regHrt;
            conn = this._conns[1];
            regServerContact = true;
      }

      HostRoutingInfo hri;
      if (hrt != null && (hri = hrt.getActiveHri()) != null) {
         this._apnList.initializeList(hri);
         if (HRUtils.isWorldPhone()
            && (RadioInfo.getActiveWAFs() & 1) != 0
            && regServerContact
            && CDMA_IIF_APN != null
            && CDMA_IIF_APN.equalsIgnoreCase(this._apnList.getFirst())) {
            hri = this._reg_IIF_HRI;
         }

         if (!this.isConnectionActive(hri, conn, regServerContact)) {
            this.logEvent(1347892323, 5);
            this._currentProgress = 1;
         } else {
            label98:
            try {
               hri.setApn(RadioInfo.getAccessPointName(conn.apnId));
            } finally {
               break label98;
            }

            this._currentProgress = 0;
         }

         if (this._currentCmd == 1) {
            this.closeConnection(this._conns[1], true);
         }

         if (this._currentProgress == 1) {
            return null;
         }

         if (regServerContact) {
            if (!this.areICCIDAndIMSIPopulated()) {
               this.logEvent(1347307849, 3);
               this._currentProgress = 6;
               return null;
            }

            if (this._currentProgress == 6) {
               this._currentProgress = 0;
            }

            if (!this.validateHomeNpc()) {
               this.logEvent(1347307598, 3);
               this._currentProgress = 8;
               return null;
            }

            if (this._currentProgress == 8) {
               this._currentProgress = 0;
            }
         }

         return hri;
      } else {
         this.logEvent(1347307634, 3);
         this._currentProgress = 5;
         return null;
      }
   }

   private static final boolean isCDMAConnection(int apnId) {
      if (!RadioInfo.areWAFsSupported(3)) {
         return false;
      }

      if ((RadioInfo.getActiveWAFs() & 2) != 0) {
         return true;
      }

      try {
         String apn = RadioInfo.getAccessPointName(apnId);
         return apn != null && CDMA_IIF_APN != null && apn.equalsIgnoreCase(CDMA_IIF_APN);
      } finally {
         return false;
      }
   }

   private final void logEvent(int event, int level) {
      EventLogger.logEvent(4019666953250015899L, event, level);
   }

   private final void logEvent(int event, long extra, int level) {
      EventLogger.logEvent(4019666953250015899L, event, extra, 16, level);
   }

   private final void addTriggerEvent(int event, long context, Object obj) {
      synchronized (this._eventQueue) {
         this._eventQueue.addElement(new HRTRequestThread$TriggerEvent(event, context, obj));
         this._eventQueue.notify();
      }
   }

   private final void checkWorldPhoneWafChange() {
      int activeWaf = RadioInfo.getActiveWAFs();
      if (HRUtils.isWorldPhone() && activeWaf != 0) {
         if (this._lastActiveWaf != 0 && activeWaf != this._lastActiveWaf) {
            this._apnList.resetList();

            for (int i = this._conns.length - 1; i >= 0; i--) {
               this.closeConnection(this._conns[i], true);
            }
         }

         this._lastActiveWaf = activeWaf;
      }
   }

   private final void checkCDMAParams() {
      if ((SUPPORTED_WAFS & 2) != 0) {
         boolean commitChanges = false;

         label36:
         try {
            String number = Phone.getInstance().getNumber(0);
            if (!StringUtilities.strEqual(this._myState.lastMdn, number)) {
               this._myState.lastMdn = number;
               commitChanges = true;
               this.addTriggerEvent(15, 0, null);
            }
         } finally {
            break label36;
         }

         byte[] imsi = CDMAInfo.getIMSI();
         if (!Arrays.equals(imsi, this._myState.lastCDMA_IMSI)) {
            this._myState.lastCDMA_IMSI = imsi;
            commitChanges = true;
            this.addTriggerEvent(19, 0, null);
         }

         if (commitChanges) {
            PersistentObject.commit(this._myState);
         }
      }
   }

   private final void resetCommand() {
      this._currentCmd = 0;
      this._currentProgress = 0;
      this._delayHint = Integer.MAX_VALUE;
      this._attemptCount = 0;
      PersistentInteger.set(this._currentCmdFlashId, this._currentCmd);
   }

   private final void setDeviceId(int deviceId) {
      NvStore.writeInt(21, deviceId);
   }

   private final void setTemporaryPIN() throws IOException {
      int temporaryPIN = 0;
      SHA1Digest digest = new SHA1Digest();
      byte[] hwIDBytes = null;
      int primaryWAF = RadioInternal.getPrimaryWAF();
      if (primaryWAF == 1) {
         hwIDBytes = GPRSInfo.getIMEI();
      } else if (primaryWAF == 2) {
         hwIDBytes = new byte[4];
         int esn = CDMAInfo.getESN();

         for (int i = 0; i < 4; i++) {
            int offset = (3 - i) * 8;
            hwIDBytes[i] = (byte)(esn >>> offset & 0xFF);
         }
      } else if (primaryWAF == 8) {
         hwIDBytes = IDENInfo.getIMEI();
      }

      if (hwIDBytes != null) {
         digest.update(hwIDBytes);
         byte[] var9 = digest.getDigest();
         temporaryPIN = -251658240 | (var9[0] & 255) << 16 | (var9[1] & 255) << 8 | var9[2] & 255;
         this.setDeviceId(temporaryPIN);
      } else {
         throw new IOException("No hardware identifier present!");
      }
   }

   private final boolean areICCIDAndIMSIPopulated() {
      switch (RadioInfo.getActiveWAFs()) {
         default:
            if (SIMCard.isSupported()) {
               try {
                  if ((SUPPORTED_WAFS & 8) == 0 && SIMCard.getIMSI() == null) {
                     return false;
                  } else if (SIMCard.getICCID() == null) {
                     return false;
                  }
               } finally {
                  ;
               }
            }
         case 2:
            return true;
      }
   }

   private final int generateSimHash() throws SIMCardException {
      int newSimHash = 0;
      boolean enabled = SIMCard.isReady() && SIMCard.isValid();
      if (!enabled) {
         throw new SIMCardException(null);
      }

      if (!this.areICCIDAndIMSIPopulated()) {
         this.logEvent(1315916105, 3);
      }

      SHA1Digest digest = new SHA1Digest();
      if ((SUPPORTED_WAFS & 8) == 0) {
         digest.update(SIMCard.getIMSI());
      }

      String str;
      if ((str = Phone.getInstance().getNumber(0)) != null) {
         digest.update(str.getBytes());
         this.logEvent(1347315064, 5);
      } else {
         this.logEvent(1315917902, 3);
      }

      digest.update(SIMCard.getICCID());
      byte[] hash = digest.getDigest();

      for (int i = 0; i < 4; i++) {
         newSimHash <<= 8;
         newSimHash |= hash[i] & 255;
      }

      return newSimHash;
   }

   public static final boolean isValidUid(String uid) {
      return uid != null && uid.length() > 0 && uid.indexOf(32) == -1;
   }

   private static final byte[] readEF(int id) {
      byte[] data = new byte[128];

      try {
         if (SIMCard.isReady() && SIMCard.isValid()) {
            int i = 0;

            while (true) {
               int length = SIMCard.requestEFRead(id, 0, 0, data);
               if (length >= 0) {
                  Array.resize(data, length);
                  return data;
               }

               if (++i >= 5) {
                  return null;
               }

               try {
                  Thread.sleep(500);
               } finally {
                  continue;
               }
            }
         } else {
            return null;
         }
      } finally {
         ;
      }
   }
}
