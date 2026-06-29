package net.rim.device.apps.internal.contentinjector;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.file.FileConnection;
import net.rim.device.api.crypto.RIMSignature;
import net.rim.device.api.io.Base64InputStream;
import net.rim.device.api.io.IOUtilities;
import net.rim.device.api.io.NoCopyByteArrayOutputStream;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.servicebook.selector.SRSelector;
import net.rim.device.api.servicebook.selector.SRSelectorCallback;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.api.system.SIMCard;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.TLEUtilities;
import net.rim.device.apps.api.utility.general.AsciiStringInputStream;
import net.rim.device.apps.internal.api.quincy.QuincyManager;
import net.rim.device.apps.internal.browser.options.GeneralProperty;
import net.rim.device.apps.internal.browser.util.ReregistrationListener;
import net.rim.device.apps.internal.browser.util.ReregistrationManager;
import net.rim.device.internal.proxy.Proxy;

public final class ContentInjector implements SRSelectorCallback, RadioStatusListener, Runnable, ReregistrationListener {
   private StringBuffer _utilityBuffer = new StringBuffer();
   private ContentInjector$ContentData _contentData = new ContentInjector$ContentData();
   private ContentInjector$Cache _cache;
   private Object _lock = new Object();
   private boolean _checkServiceBooks;
   private Thread _workThread;
   private static final long ID = 2962258110575299323L;
   private static final long LOGGER_GUID = -1639924839295312060L;
   private static final long SR_SELECTOR_GUID = 5429069385946712194L;
   private static String EXCEPTION_CONTENT_STORE = "Exception adding content to ContentStore\n";
   private static String CONTENT_FETCH_FAILURE = "Failed to fetch content from provided url\n";
   private static String CONTENT_TYPE = "Content-Type";
   private static String SIG_HTTP_HEADER_NAME = "X-ContentSig";
   private static String LOCATION_HTTP_HEADER_NAME = "x-rim-content-location";
   private static String CONTENT_LOCATION = "/appdata/rim/idlescreen/carrier/";
   public static String CID = "CONTENTINJECTOR";
   public static String APP_NAME = "ContentInjector";
   public static final int URL = 1;
   public static final int LOCATIONINCS = 2;
   public static final int CONNECTIONPARAMETERS = 3;

   public final void mobilityManagementEvent(int eventCode, int cause) {
   }

   @Override
   public final int chooseNewDefault(ServiceBook sb, String cid, int oldDefaultId, boolean userSet) {
      this.checkServiceBooks();
      return -1;
   }

   @Override
   public final void defaultChanged(int id) {
   }

   @Override
   public final void baseStationChange() {
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      while (true) {
         try {
            synchronized (this._lock) {
               if (!this._checkServiceBooks) {
                  this._workThread = null;
                  return;
               }

               this._checkServiceBooks = false;
            }

            ServiceBook sb = ServiceBook.getSB();
            ServiceRecord[] srs = sb.findRecordsByCid(CID);
            ContentInjector$ContentEntry ce = new ContentInjector$ContentEntry();

            for (int i = srs.length - 1; i >= 0; i--) {
               ce._url = null;
               ce._connectionParams = null;
               ce._location = null;
               this.parseServiceRecord(srs[i], ce);
               this.fetchContent(ce);
            }
         } catch (Throwable var8) {
            EventLogger.logEvent(-1639924839295312060L, e.toString().getBytes(), 2);
            continue;
         }
      }
   }

   @Override
   public final void networkScanComplete(boolean success) {
   }

   @Override
   public final void networkServiceChange(int networkId, int service) {
   }

   @Override
   public final void networkStarted(int networkId, int service) {
      this.checkServiceBooks();
   }

   @Override
   public final void networkStateChange(int state) {
   }

   @Override
   public final void pdpStateChange(int apn, int state, int cause) {
   }

   @Override
   public final void radioTurnedOff() {
   }

   @Override
   public final void signalLevel(int level) {
   }

   @Override
   public final boolean handleReregistration() {
      EventLogger.logEvent(-1639924839295312060L, "Erasing existing sb entries, sim has changed".getBytes(), 0);
      ServiceBook sb = ServiceBook.getSB();
      ServiceRecord[] records = sb.findRecordsByCid(CID);

      for (int i = records.length - 1; i >= 0; i--) {
         sb.removeRecord(records[i]);
      }

      return records.length > 0;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void readAndVerifyContent(HttpConnection http, ContentInjector$ContentData content) {
      byte[] carrierKey = Branding.getData(16387);
      boolean verified = false;
      long returnVal = 0;
      content._data = null;
      content._timestamp = -1;
      if (carrierKey == null) {
         EventLogger.logEvent(-1639924839295312060L, "No public key in the branding sector".getBytes(), 2);
      } else {
         byte[] data;
         int dataLength;
         try {
            String contentHeader = http.getHeaderField(SIG_HTTP_HEADER_NAME);
            if (contentHeader == null) {
               EventLogger.logEvent(-1639924839295312060L, "Required http header X-ContentSig not set".getBytes(), 2);
               return;
            }

            Base64InputStream sigValue = new Base64InputStream(new AsciiStringInputStream(contentHeader));
            NoCopyByteArrayOutputStream sigByteArrayStream = new NoCopyByteArrayOutputStream();
            NoCopyByteArrayOutputStream dataByteArrayStream = new NoCopyByteArrayOutputStream();
            copyStreams(sigValue, sigByteArrayStream);
            copyStreams(http.openInputStream(), dataByteArrayStream);
            byte[] sig = sigByteArrayStream.getByteArray();
            data = dataByteArrayStream.getByteArray();
            dataLength = dataByteArrayStream.size();
            returnVal = RIMSignature.verify(data, 0, dataLength, sig, 0, carrierKey);
            verified = returnVal > 0;
         } catch (Throwable var15) {
            EventLogger.logEvent(-1639924839295312060L, e.toString().getBytes(), 2);
            return;
         }

         if (verified) {
            content._data = new ByteArrayInputStream(data, 0, dataLength);
            content._timestamp = returnVal;
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void addToContentStore(HttpConnection http, InputStream is, long timestamp, String location) {
      FileConnection fc = null;
      OutputStream out = null;
      boolean var68 = false /* VF: Semaphore variable */;

      label487: {
         try {
            try {
               var68 = true;
               fc = (FileConnection)Connector.open("file:///store" + location);
               if (fc.exists() && fc.lastModified() > timestamp) {
                  EventLogger.logEvent(-1639924839295312060L, "Signature time on new content older than existing content, rejecting".getBytes(), 3);
                  var68 = false;
                  break label487;
               }

               if (fc.exists()) {
                  fc.delete();
               }

               fc.create();
               out = fc.openOutputStream();
               byte[] data = IOUtilities.streamToBytes(is);
               out.write(data);
               var68 = false;
            } catch (Throwable var83) {
               String msg = EXCEPTION_CONTENT_STORE + e.toString();
               EventLogger.logEvent(-1639924839295312060L, msg.getBytes(), 2);
               throw e;
            }
         } finally {
            if (var68) {
               if (out != null) {
                  label452:
                  try {
                     out.close();
                  } finally {
                     break label452;
                  }
               }

               if (fc != null) {
                  label448:
                  try {
                     fc.close();
                  } finally {
                     break label448;
                  }
               }
            }
         }

         if (out != null) {
            label467:
            try {
               out.close();
            } finally {
               break label467;
            }
         }

         if (fc != null) {
            try {
               fc.close();
               return;
            } finally {
               return;
            }
         }

         return;
      }

      if (out != null) {
         label463:
         try {
            out.close();
         } finally {
            break label463;
         }
      }

      if (fc != null) {
         try {
            fc.close();
         } finally {
            return;
         }
      }
   }

   private final void checkServiceBooks() {
      EventLogger.logEvent(-1639924839295312060L, "checking service books".getBytes(), 0);
      synchronized (this._lock) {
         if (this.isSimCardDifferent()) {
            EventLogger.logEvent(-1639924839295312060L, "skipping fetch, sims have changed".getBytes(), 0);
         } else {
            this._checkServiceBooks = true;
            if (this._workThread == null) {
               this._workThread = new Thread(this);
               Proxy p = Proxy.getInstance();
               p.startThread(this._workThread);
            }
         }
      }
   }

   private final boolean isSimCardDifferent() {
      if (!SIMCard.isSupported()) {
         return false;
      }

      String currentSimId = null;

      label36:
      try {
         currentSimId = SIMCard.iccidToString(SIMCard.getICCID());
      } finally {
         break label36;
      }

      if (currentSimId == null) {
         return true;
      }

      String previousSimId = GeneralProperty.getPreviousSimId();
      return !currentSimId.equals(previousSimId);
   }

   private ContentInjector() {
      this._cache = ContentInjector$Cache.loadCache();
      EventLogger.register(-1639924839295312060L, APP_NAME, 2);
      SRSelector.getInstance().register(APP_NAME, 5429069385946712194L, CID, this);
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      ReregistrationManager rrm = (ReregistrationManager)ar.getOrWaitFor(8461344725264262391L);
      if (rrm != null) {
         rrm.addReregistrationListener(this);
      }

      Proxy p = Proxy.getInstance();
      p.addRadioListener(this);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void fetchContent(ContentInjector$ContentEntry ce) {
      StringBuffer connection = this._utilityBuffer;
      boolean var9 = false /* VF: Semaphore variable */;

      label82:
      try {
         var9 = true;
         String t = ce._url;
         connection.setLength(0);
         connection.append(t);
         if (ce._connectionParams != null) {
            connection.append(';');
            connection.append(ce._connectionParams);
         }

         String fullurl = connection.toString();
         if (this._cache.hit(t)) {
            connection.setLength(0);
            return;
         }

         EventLogger.logEvent(-1639924839295312060L, fullurl.getBytes(), 0);
         HttpConnection http = (HttpConnection)Connector.open(fullurl);
         if (http.getResponseCode() == 200) {
            String location = http.getHeaderField(LOCATION_HTTP_HEADER_NAME);
            if (location == null) {
               location = ce._location;
            }

            if (location == null || !location.startsWith(CONTENT_LOCATION)) {
               connection.setLength(0);
               return;
            }

            ContentInjector$ContentData content = this._contentData;
            content._data = null;
            content._timestamp = 0;
            this.readAndVerifyContent(http, content);
            if (content._data == null) {
               connection.setLength(0);
               return;
            }

            this.addToContentStore(http, content._data, content._timestamp, location);
            this._cache.add(t);
            var9 = false;
         } else {
            String msg = CONTENT_FETCH_FAILURE + fullurl;
            EventLogger.logEvent(-1639924839295312060L, msg.getBytes(), 3);
            var9 = false;
         }
      } finally {
         if (var9) {
            if (EventLogger.getMinimumLevel() >= 5) {
               QuincyManager.sendUncaughtException("ContentInjector");
            }
            break label82;
         }
      }

      connection.setLength(0);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void parseServiceRecord(ServiceRecord sr, ContentInjector$ContentEntry ce) {
      byte[] rawdata = sr.getApplicationData();
      if (rawdata != null) {
         DataBuffer data = new DataBuffer();
         data.write(rawdata);
         data.rewind();

         try {
            while (data.available() > 0) {
               int type = data.readByte();
               data.setPosition(data.getPosition() - 1);
               switch (type) {
                  case 0:
                     TLEUtilities.readStringField(data, type, true);
                     break;
                  case 1:
                  default:
                     String url = TLEUtilities.readStringField(data, type, true);
                     if (url.indexOf("http://") == -1) {
                        url = "http://" + url;
                     }

                     ce._url = url;
                     break;
                  case 2:
                     ce._location = TLEUtilities.readStringField(data, type, true);
                     break;
                  case 3:
                     ce._connectionParams = TLEUtilities.readStringField(data, type, true);
               }
            }
         } catch (Throwable var8) {
            throw new IllegalStateException(e.toString());
         }
      }
   }

   public static final ContentInjector getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      synchronized (ar) {
         ContentInjector ci = (ContentInjector)ar.get(2962258110575299323L);
         if (ci == null) {
            ci = new ContentInjector();
            ar.put(2962258110575299323L, ci);
         }

         return ci;
      }
   }

   static final int copyStreams(InputStream is, OutputStream os) {
      byte[] data = new byte[500];
      int totalBytes = 0;

      while (true) {
         int bytesRead = is.read(data);
         if (bytesRead <= 0) {
            return totalBytes;
         }

         os.write(data, 0, bytesRead);
         totalBytes += bytesRead;
      }
   }
}
