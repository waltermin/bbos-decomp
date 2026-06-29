package net.rim.device.apps.internal.bis.launch.config;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.Hashtable;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.util.StringTokenizer;

public final class BISClientConfigRecord {
   private String _serverURL;
   private String _transportCID;
   private String _transportUID;
   private int _serviceTimeout;
   private String _brandName;
   private Hashtable _ribbonTitles;
   private boolean _autoStart;
   private boolean _showOnRibbon;
   private int _brandingIconIndex;
   public static final String CID = "BISClientConfig";
   public static final byte ENCODED_TYPE_XML_SERVER = 1;
   public static final byte ENCODED_TYPE_TRANSPORT_CID = 2;
   public static final byte ENCODED_TYPE_TRANSPORT_UID = 3;
   public static final byte ENCODED_TYPE_SERVICE_TIMEOUT = 4;
   public static final byte ENCODED_TYPE_BRAND_NAME = 5;
   public static final byte ENCODED_TYPE_AUTOSTART = 6;
   public static final byte ENCODED_TYPE_BRANDINGICON = 7;
   public static final byte ENCODED_TYPE_SHOW_ON_RIBBON = 8;
   public static final byte ENCODED_TYPE_LOCALES = 9;
   public static final byte ENCODED_TYPE_RIBBON_TITLE = 10;
   public static final int BRANDING_ICON_VALUE_NONE = -1;

   private BISClientConfigRecord() {
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final BISClientConfigRecord getBISClientConfigRecord() {
      BISClientConfigRecord bisClientConfigRecord = null;
      ServiceBook serviceBook = ServiceBook.getSB();
      ServiceRecord[] allBisClientConfigRecords = serviceBook.findRecordsByCid("BISClientConfig");
      if (allBisClientConfigRecords != null && allBisClientConfigRecords.length > 0) {
         ServiceRecord serviceRecord = allBisClientConfigRecords[0];
         byte[] applicationData = serviceRecord.getApplicationData();
         if (applicationData == null || applicationData.length == 0) {
            return null;
         }

         ByteArrayInputStream bais = new ByteArrayInputStream(applicationData);
         DataInputStream dis = new DataInputStream(bais);

         try {
            BISClientConfigRecord configRecord = new BISClientConfigRecord();
            dis.readByte();
            configRecord._serverURL = readLTEString(dis, (byte)1, true);
            configRecord._transportCID = readLTEString(dis, (byte)2, true);
            configRecord._transportUID = readLTEString(dis, (byte)3, true);
            configRecord._serviceTimeout = readLTEInteger(dis, (byte)4);
            configRecord._brandName = readLTEString(dis, (byte)5, true);
            configRecord._autoStart = readLTEBoolean(dis, (byte)6);
            configRecord._brandingIconIndex = readLTEInteger(dis, (byte)7);
            configRecord._showOnRibbon = readLTEBoolean(dis, (byte)8);
            configRecord._ribbonTitles = new Hashtable();
            String locales = readLTEString(dis, (byte)9, true);
            StringTokenizer tokenizer = new StringTokenizer(locales, ",");

            while (tokenizer.hasMoreTokens()) {
               String locale = tokenizer.nextToken();
               String ribbonTitle = readLTEString(dis, (byte)10, true);
               configRecord._ribbonTitles.put(locale, ribbonTitle);
            }

            return configRecord;
         } catch (Throwable var13) {
            System.out.println("Exception occurred" + e.toString());
            return bisClientConfigRecord;
         }
      } else {
         return bisClientConfigRecord;
      }
   }

   public final String getServerURL() {
      return this._serverURL;
   }

   public final String getTransportCID() {
      return this._transportCID;
   }

   public final String getTransportUID() {
      return this._transportUID;
   }

   public final int getServiceTimeout() {
      return this._serviceTimeout;
   }

   private static final String readLTEString(DataInputStream dis, byte expectedType, boolean stripNull) {
      short length = dis.readShort();
      byte type = dis.readByte();
      if (type != expectedType) {
         throw new IllegalArgumentException();
      }

      byte[] stringBytes = new byte[length];
      dis.read(stringBytes);
      String result = new String(stringBytes, "UTF-8");
      if (stripNull && length > 0 && result.charAt(result.length() - 1) == 0) {
         result = result.substring(0, result.length() - 1);
      }

      return result;
   }

   private static final int readLTEInteger(DataInputStream dis, byte expectedType) {
      dis.readShort();
      byte type = dis.readByte();
      if (type != expectedType) {
         throw new IllegalArgumentException();
      } else {
         return dis.readInt();
      }
   }

   private static final boolean readLTEBoolean(DataInputStream dis, byte expectedType) {
      int intValue = readLTEInteger(dis, expectedType);
      return intValue == 1;
   }
}
