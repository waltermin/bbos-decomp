package net.rim.device.api.browser.push;

import com.sun.cldc.i18n.Helper;
import java.io.IOException;
import javax.microedition.io.ServerSocketConnection;
import javax.microedition.io.SocketConnection;
import net.rim.device.api.crypto.SHA1Digest;
import net.rim.device.api.hrt.DAC;
import net.rim.device.api.hrt.GprsHRI;
import net.rim.device.api.hrt.HostRoutingInfo;
import net.rim.device.api.hrt.HostRoutingTable;
import net.rim.device.api.hrt.IPv4UdpDAC;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.cldc.io.waphttp.WAPConnectionRegistry;

public final class WAPPushSource extends PushSource implements PushEventLogger {
   private String _uid;
   private String _cid;
   private int _type;
   private int _slFilterMode;
   private String _slFilter;
   private int _siFilterMode;
   private String _siFilter;
   private int _otherFilterMode;
   private String _otherFilter;
   private int _slAcceptMode;
   private int _siAcceptMode;
   private int _otherAcceptMode;
   private int _tlsVersion;
   private ServerSocketConnection _notifier;
   private SocketConnection _ppgConnection;
   private boolean _processing;
   private Pushlet _listener;
   private Object _certificate = null;
   private Object _privateKey = null;
   private int _clientIdType;
   private String _ppgAddress;
   private int _retryMode;
   private int _retryCount;
   private int _threadPoolSize;
   private WAPPushSource$WAP20RunThread _wap2Thread;
   public static final String WAP_PUSH_SERVICE_CID = "WAPPushConfig";
   public static final int TYPE_SMS_CONNECTION = 6;
   public static final int TYPE_UDP_CONNECTION = 7;
   public static final int TYPE_TCPIP_CONNECTION = 8;
   public static final int TLS_VERSION_NONE = 0;
   public static final int TLS_VERSION_TLS_10 = 1;
   private static final long TCP_PUSH_PORT_KEYS = -477495422972569712L;
   private static final byte VERSION = 1;
   private static final byte ENCODED_TYPE_TYPE = 1;
   private static final byte ENCODED_TYPE_OUT_PORT = 2;
   private static final byte ENCODED_TYPE_WTLS_MODE = 3;
   private static final byte ENCODED_TYPE_WTLS_CLIENT_ID_TYPE = 4;
   private static final byte ENCODED_TYPE_WTLS_CLIENT_ID_VALUE = 5;
   private static final byte ENCODED_TYPE_SL_FILTER_MODE = 6;
   private static final byte ENCODED_TYPE_SL_FILTER_VALUE = 7;
   private static final byte ENCODED_TYPE_SI_FILTER_MODE = 8;
   private static final byte ENCODED_TYPE_SI_FILTER_VALUE = 9;
   private static final byte ENCODED_TYPE_OTHER_FILTER_MODE = 10;
   private static final byte ENCODED_TYPE_OTHER_FILTER_VALUE = 11;
   private static final byte ENCODED_TYPE_SL_ACCEPT_MODE = 12;
   private static final byte ENCODED_TYPE_SI_ACCEPT_MODE = 13;
   private static final byte ENCODED_TYPE_OTHER_ACCEPT_MODE = 14;
   private static final byte ENCODED_TYPE_TLS_VERSION = 15;
   private static final byte ENCODED_TYPE_CLIENT_ID_TYPE = 16;
   public static final byte ENCODED_TYPE_PPG_ADDRESS = 17;
   private static final byte ENCODED_TYPE_RETRY_MODE = 18;
   private static final byte ENCODED_TYPE_RETRY_COUNT = 19;
   private static final byte ENCODED_TYPE_THREAD_POOL_SIZE = 20;
   private static final String CPI_TAG = "X-Wap-CPITag";
   private static final String WAP_PUSH_STATUS = "X-Wap-Push-Status";
   private static final int CPI_VALUES_ACCEPT = 0;
   private static final int CPI_VALUES_ACCEPT_CHARSET = 1;
   private static final int CPI_VALUES_ACCEPT_LANGUAGE = 2;
   private static final int CPI_VALUES_ACCEPT_APPID = 3;
   private static final int CPI_VALUES_ACCEPT_MSG_SIZE = 4;
   private static final int CPI_VALUES_ACCEPT_MAX_REQ = 5;
   private static final String[] CPI_VALUES = new String[]{
      "X-Wap-Push-Accept",
      "X-Wap-Push-Accept-Charset",
      "X-Wap-Push-Accept-Language",
      "X-Wap-Push-Accept-AppID",
      "X-Wap-Push-MsgSize",
      "X-Wap-Push-Accept-MaxPushReq"
   };
   private static final int FIVE_MINUTES = 300000;
   public static final int RETRY_MODE_COUNT = 1;
   public static final int RETRY_MODE_BACKOFF = 2;

   WAPPushSource(
      int port,
      String apn,
      String apnUsername,
      String apnPassword,
      int type,
      int slFilterMode,
      String slFilter,
      int siFilterMode,
      String siFilter,
      int otherFilterMode,
      String otherFilter,
      int slAcceptMode,
      int siAcceptMode,
      int otherAcceptMode,
      int tlsVersion,
      int clientIdType,
      String ppgAddress,
      int retryMode,
      int retryCount,
      int threadPoolSize,
      String uid,
      String cid
   ) {
      super(port, apn, apnUsername, apnPassword);
      this._uid = uid;
      this._cid = cid;
      this._type = type;
      this._slFilter = slFilter;
      this._siFilter = siFilter;
      this._otherFilter = otherFilter;
      this._slFilterMode = slFilterMode;
      this._siFilterMode = siFilterMode;
      this._otherFilterMode = otherFilterMode;
      this._siAcceptMode = siAcceptMode;
      this._slAcceptMode = slAcceptMode;
      this._otherAcceptMode = otherAcceptMode;
      this._tlsVersion = tlsVersion;
      this._clientIdType = clientIdType;
      this._ppgAddress = ppgAddress;
      this._retryMode = retryMode;
      this._retryCount = retryCount;
      if ((this._retryMode & 2) != 0 && this._retryCount == -1) {
         this._retryCount = Integer.MAX_VALUE;
      }

      if (this._retryCount == -1) {
         this._retryCount = 1;
      }

      if (threadPoolSize > 0 && threadPoolSize <= 6 && this._type != 7 && this._type != 6) {
         this._threadPoolSize = threadPoolSize;
      } else {
         this._threadPoolSize = 1;
      }
   }

   @Override
   public final int getSourceType() {
      return 2;
   }

   public final int getConnectionType() {
      return this._type;
   }

   public final boolean saveData() {
      byte[] data = getEncodedData(
         this._type,
         this._slFilterMode,
         this._slFilter,
         this._siFilterMode,
         this._siFilter,
         this._otherFilterMode,
         this._otherFilter,
         this._slAcceptMode,
         this._siAcceptMode,
         this._otherAcceptMode,
         this._tlsVersion,
         this._clientIdType,
         this._ppgAddress,
         this._retryMode,
         this._retryCount,
         this._threadPoolSize
      );
      ServiceBook sb = ServiceBook.getSB();
      ServiceRecord rec = sb.getRecordByUidAndCid(this._uid, this._cid);
      if (rec == null) {
         return false;
      }

      rec.setApplicationData(data);
      sb.commit();
      return true;
   }

   public final String getPropertyAsString(int property) {
      switch (property) {
         case 17:
            return this._ppgAddress;
         default:
            throw new IllegalArgumentException();
      }
   }

   public final void setProperty(int property, String value) {
      switch (property) {
         case 17:
            this._ppgAddress = value;
            return;
         default:
            throw new IllegalArgumentException();
      }
   }

   public static final byte[] getEncodedData(
      int type,
      int slFilterMode,
      String slFilter,
      int siFilterMode,
      String siFilter,
      int otherFilterMode,
      String otherFilter,
      int slAcceptMode,
      int siAcceptMode,
      int otherAcceptMode,
      int tlsVersion,
      int clientIdType,
      String ppgAddress,
      int retryMode,
      int retryCount,
      int threadPoolSize
   ) throws IOException {
      byte[] encodedData = null;
      DataBuffer tmpDataBuffer = new DataBuffer();
      SyncBuffer tmpSyncBuffer = new SyncBuffer(tmpDataBuffer, 0, 0);
      tmpDataBuffer.writeByte(1);
      tmpSyncBuffer.addInt(1, type, 1);
      if (slFilterMode >= 0 && slFilterMode <= 2 && slFilter != null) {
         tmpSyncBuffer.addInt(6, slFilterMode, 1);
         tmpSyncBuffer.addField(7, slFilter);
      }

      if (siFilterMode >= 0 && siFilterMode <= 2 && siFilter != null) {
         tmpSyncBuffer.addInt(8, siFilterMode, 1);
         tmpSyncBuffer.addField(9, siFilter);
      }

      if (otherFilterMode >= 0 && otherFilterMode <= 2 && otherFilter != null) {
         tmpSyncBuffer.addInt(10, otherFilterMode, 1);
         tmpSyncBuffer.addField(11, otherFilter);
      }

      if (slAcceptMode >= 0 && slAcceptMode <= 2) {
         tmpSyncBuffer.addInt(12, slAcceptMode, 1);
      }

      if (siAcceptMode >= 0 && siAcceptMode <= 2) {
         tmpSyncBuffer.addInt(13, siAcceptMode, 1);
      }

      if (otherAcceptMode >= 0 && otherAcceptMode <= 2) {
         tmpSyncBuffer.addInt(14, otherAcceptMode, 1);
      }

      if (tlsVersion >= 0 && tlsVersion <= 1) {
         tmpSyncBuffer.addInt(15, tlsVersion, 1);
      }

      if (clientIdType >= 0 && clientIdType <= 10) {
         tmpSyncBuffer.addInt(16, clientIdType, 1);
      }

      if (ppgAddress != null) {
         tmpSyncBuffer.addField(17, ppgAddress);
      }

      if (retryMode >= 0) {
         tmpSyncBuffer.addInt(18, retryMode, 1);
      }

      if (retryCount >= 0) {
         tmpSyncBuffer.addInt(19, retryCount, 4);
      }

      if (threadPoolSize > 1) {
         tmpSyncBuffer.addInt(20, threadPoolSize, 1);
      }

      encodedData = tmpDataBuffer.toArray();
      if (encodedData.length > 1023) {
         throw new IOException("Encoded WAPPush ServiceBook data (" + encodedData.length + " bytes) exceeds 1023 bytes");
      } else {
         return encodedData;
      }
   }

   public static final WAPPushSource getService(ServiceRecord record) {
      if (record == null) {
         return null;
      }

      int type = 0;
      int port = 0;
      int slFilterMode = 0;
      int siFilterMode = 0;
      int otherFilterMode = 0;
      int slAcceptMode = 0;
      int siAcceptMode = 0;
      int otherAcceptMode = 0;
      int clientIdType = -1;
      int retryMode = 1;
      int retryCount = -1;
      int threadPoolSize = 1;
      String apn = null;
      String apnPassword = null;
      String apnUsername = null;
      String slFilter = null;
      String siFilter = null;
      String otherFilter = null;
      String ppgAddress = null;
      String uid = null;
      String cid = null;
      int tlsVersion = 0;
      HostRoutingTable hrt = record.getAttachedHrt();
      if (hrt != null) {
         HostRoutingInfo hri = hrt.getHris()[0];
         if (hri instanceof GprsHRI) {
            GprsHRI gHri = (GprsHRI)hri;
            apn = gHri.getApn();
            apnUsername = gHri.getApnUsername();
            apnPassword = gHri.getApnPassword();
         }

         DAC dac = hri.getDac();
         if (!(dac instanceof IPv4UdpDAC)) {
            return null;
         }

         long[] addresses = ((IPv4UdpDAC)dac).getAddresses();
         if (addresses == null || addresses.length == 0) {
            return null;
         }

         port = IPv4UdpDAC.addr2SrcPort(addresses[0]);
      }

      try {
         uid = record.getUid();
         cid = record.getCid();
         byte[] data = record.getApplicationData();
         DataBuffer tmpDataBuffer = new DataBuffer(data, 0, data.length, true);
         SyncBuffer tmpSyncBuffer = new SyncBuffer(tmpDataBuffer, 0, 0);
         tmpDataBuffer.readByte();

         while (!tmpSyncBuffer.isEmpty()) {
            switch (tmpSyncBuffer.getFieldType()) {
               case 0:
               case 2:
               case 3:
               case 4:
               case 5:
                  tmpSyncBuffer.skipField();
                  break;
               case 1:
               default:
                  type = tmpSyncBuffer.getInt();
                  break;
               case 6:
                  slFilterMode = tmpSyncBuffer.getInt();
                  break;
               case 7:
                  slFilter = tmpSyncBuffer.getString();
                  break;
               case 8:
                  siFilterMode = tmpSyncBuffer.getInt();
                  break;
               case 9:
                  siFilter = tmpSyncBuffer.getString();
                  break;
               case 10:
                  otherFilterMode = tmpSyncBuffer.getInt();
                  break;
               case 11:
                  otherFilter = tmpSyncBuffer.getString();
                  break;
               case 12:
                  slAcceptMode = tmpSyncBuffer.getInt();
                  break;
               case 13:
                  siAcceptMode = tmpSyncBuffer.getInt();
                  break;
               case 14:
                  otherAcceptMode = tmpSyncBuffer.getInt();
                  break;
               case 15:
                  tlsVersion = tmpSyncBuffer.getInt();
                  break;
               case 16:
                  clientIdType = tmpSyncBuffer.getInt();
                  break;
               case 17:
                  ppgAddress = tmpSyncBuffer.getString();
                  break;
               case 18:
                  retryMode = tmpSyncBuffer.getInt();
                  break;
               case 19:
                  retryCount = tmpSyncBuffer.getInt();
                  break;
               case 20:
                  threadPoolSize = tmpSyncBuffer.getInt();
            }
         }
      } finally {
         ;
      }

      try {
         return new WAPPushSource(
            port,
            apn,
            apnUsername,
            apnPassword,
            type,
            slFilterMode,
            slFilter,
            siFilterMode,
            siFilter,
            otherFilterMode,
            otherFilter,
            slAcceptMode,
            siAcceptMode,
            otherAcceptMode,
            tlsVersion,
            clientIdType,
            ppgAddress,
            retryMode,
            retryCount,
            threadPoolSize,
            uid,
            cid
         );
      } finally {
         ;
      }
   }

   public static final PushSource[] getAllServices() {
      return !PushOptions.getOptions().getWAPEnablePush() ? null : getAllServicesInternal();
   }

   static final PushSource[] getAllServicesInternal() {
      ServiceRecord[] records = ServiceBook.getSB().findRecordsByCid("WAPPushConfig");
      int count = records.length;
      WAPPushSource[] sources = null;
      if (count > 0) {
         sources = new WAPPushSource[count];

         for (int i = 0; i < count; i++) {
            sources[i] = getService(records[i]);
         }
      }

      return sources;
   }

   public final String getSIFilter() {
      return this._siFilter;
   }

   public final int getSIFilterMode() {
      return this._siFilterMode;
   }

   public final String getSLFilter() {
      return this._slFilter;
   }

   public final int getSLFilterMode() {
      return this._slFilterMode;
   }

   public final String getOtherFilter() {
      return this._otherFilter;
   }

   public final int getOtherFilterMode() {
      return this._otherFilterMode;
   }

   public final int getSIAcceptMode() {
      return this._siAcceptMode;
   }

   public final int getSLAcceptMode() {
      return this._slAcceptMode;
   }

   public final int getOtherAcceptMode() {
      return this._otherAcceptMode;
   }

   @Override
   public final void startPPGConnection(Pushlet listener) {
      if (this._type == 8 && this._ppgAddress != null) {
         WAPPushSource$WAP20RunThread rt = new WAPPushSource$WAP20RunThread(this, true);
         this._listener = listener;
         if (WAPConnectionRegistry.isWAPInstalled()) {
            rt.start();
         }
      }
   }

   @Override
   public final void startListening(Pushlet listener) {
      Thread rt;
      synchronized (this) {
         switch (this._type) {
            case 5:
               throw new IllegalArgumentException();
            case 6:
               rt = new WAPPushSource$WAP1xRunThread(this, "sms");
               break;
            case 7:
            default:
               rt = new WAPPushSource$WAP1xRunThread(this, "udp");
               break;
            case 8:
               this._wap2Thread = new WAPPushSource$WAP20RunThread(this, false);
               rt = this._wap2Thread;
         }

         this._listener = listener;
         this._processing = WAPConnectionRegistry.isWAPInstalled();
      }

      rt.start();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void close() {
      try {
         synchronized (this) {
            this._processing = false;
         }

         if (this._notifier != null) {
            this._notifier.close();
            this._notifier = null;
         }

         synchronized (this) {
            this.notifyAll();
         }
      } catch (Throwable var9) {
         EventLogger.logEvent(-1133226195824034738L, ("PPex\n" + ioe.toString()).getBytes(), 0);
         return;
      }
   }

   @Override
   public final void dataNetworkChanged(boolean dataAvailable) {
      if (this._type == 8) {
         if (dataAvailable) {
            synchronized (this) {
               if (this._wap2Thread != null) {
                  this._wap2Thread.resetRetries();
                  this.notify();
               } else {
                  this._wap2Thread = new WAPPushSource$WAP20RunThread(this, false);
                  this._wap2Thread.start();
               }
            }
         }
      }
   }

   private static final String[] getCPIValues() {
      String[] values = new String[CPI_VALUES.length];
      String[] encodings = Helper.getSupportedEncodings();
      StringBuffer buf = new StringBuffer(encodings[0]);

      for (int i = 1; i < encodings.length; i++) {
         buf.append(',');
         buf.append(encodings[i]);
      }

      values[1] = buf.toString();
      values[2] = Locale.getDefault().getLanguage();
      values[3] = "*";
      values[5] = Integer.toString(1);
      values[4] = Integer.toString(1400);
      return values;
   }

   private static final String getCPIHash(String[] values) {
      SHA1Digest digest = new SHA1Digest();

      for (int i = 0; i < CPI_VALUES.length; i++) {
         if (values[i] != null) {
            digest.update(values[i].getBytes());
         }
      }

      byte[] digestBytes = digest.getDigest();
      return Integer.toHexString((digestBytes[0] & 255) << 24 | (digestBytes[1] & 255) << 16 | (digestBytes[2] & 255) << 8 | digestBytes[3] & 255);
   }

   public static final boolean doWAPPushServiceRecordsExist() {
      return ServiceBook.getSB().findRecordsByCid("WAPPushConfig").length > 0;
   }
}
