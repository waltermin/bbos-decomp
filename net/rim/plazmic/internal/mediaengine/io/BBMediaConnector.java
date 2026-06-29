package net.rim.plazmic.internal.mediaengine.io;

import java.io.InputStream;
import javax.microedition.io.Connection;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.file.FileConnection;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.resources.Resource;
import net.rim.device.resources.Resource$Internal;
import net.rim.plazmic.internal.mediaengine.MediaFactory;
import net.rim.plazmic.mediaengine.MediaException;
import net.rim.plazmic.mediaengine.io.ConnectionInfo;
import net.rim.plazmic.mediaengine.io.Connector;

public class BBMediaConnector implements Connector {
   private int _retry = 3;
   private int _timeOut = -1;
   private String connectionTimeout = null;
   private String[] _requestHeaders;
   private HttpConnection _previousConnection;
   protected static String USER_AGENT = "User-Agent";
   protected static String MEUSER_AGENT = "MediaEngine/1.6.1";
   public static final boolean STORE_ENABLED = true;
   public static final String STORE_PROTOCOL = "store:/";
   public static final boolean SDCARD_ENABLED = true;
   public static final String SDCARD_PROTOCOL = "file:/";
   public static final String JAR_PROTOCOL = "jar://";
   protected static final boolean JAR_ENABLED = true;
   private static final String UAPROF_NAME = "profile";
   private static final String UAPROF_VALUE = ((StringBuffer)(new Object("http://www.blackberry.net/go/mobile/profiles/uaprof/")))
      .append(DeviceInfo.getDeviceName())
      .append('/')
      .append("4.0.0")
      .append(".rdf")
      .toString();
   private static String CONNECTION_TIMEOUT = ";ConnectionTimeout=";
   protected static final boolean COD_ENABLED = true;
   public static final String COD_PROTOCOL = "cod://";

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public String getResponseHeader(String name) {
      try {
         return this._previousConnection == null ? null : this._previousConnection.getHeaderField(name);
      } catch (Throwable var4) {
         MediaFactory.getPlatform().logDebug(this, 22, -1, e);
         return null;
      }
   }

   public void setRequestHeader(String name, String value) {
      if (name != null && value != null) {
         if (this._requestHeaders == null) {
            this._requestHeaders = new Object[]{name, value};
            return;
         }

         String[] temp = new Object[this._requestHeaders.length + 2];
         System.arraycopy(this._requestHeaders, 0, temp, 0, this._requestHeaders.length);
         temp[temp.length - 2] = name;
         temp[temp.length - 1] = value;
         this._requestHeaders = temp;
      }
   }

   protected InputStream getCodInputStream(String uri, ConnectionInfo info) {
      InputStream is = null;
      String resource = uri.substring(6);
      int codIndex = resource.indexOf(47);
      if (codIndex < 0) {
         throw new Object(uri);
      }

      String codname = resource.substring(0, codIndex);
      resource = resource.substring(codIndex + 1);
      int questionIndex = resource.indexOf(63);
      if (questionIndex != -1) {
         resource = resource.substring(0, questionIndex);
      }

      Resource r = Resource$Internal.getResourceClass(codname);
      if (r != null) {
         byte[] resourceBytes = r.getResource(resource);
         if (resourceBytes != null) {
            is = (InputStream)(new Object(resourceBytes));
         }
      }

      if (is == null) {
         throw new Object(uri);
      } else {
         return is;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   protected InputStream getHttpInputStream(String uri, ConnectionInfo info) throws MediaException {
      InputStream is = null;
      this._previousConnection = null;
      if (this.connectionTimeout != null) {
         uri = ((StringBuffer)(new Object())).append(uri).append(this.connectionTimeout).toString();
      }

      HttpConnection c = (HttpConnection)javax.microedition.io.Connector.open(uri, 1, true);
      if (c != null) {
         info.setConnection(c);
         boolean timedOut = false;
         boolean var14 = false /* VF: Semaphore variable */;

         try {
            try {
               var14 = true;
               c.setRequestProperty(USER_AGENT, MEUSER_AGENT);
               c.setRequestProperty("profile", UAPROF_VALUE);
               if (this._requestHeaders != null) {
                  for (int i = this._requestHeaders.length - 2; i >= 0; i -= 2) {
                     c.setRequestProperty(this._requestHeaders[i], this._requestHeaders[i + 1]);
                  }

                  this._requestHeaders = null;
               }

               int count = 0;

               while (true) {
                  try {
                     is = c.openInputStream();
                     info.setContentType(c.getType());
                     info.setLength((int)c.getLength());
                     var14 = false;
                     break;
                  } catch (Throwable var21) {
                     if (count == this._retry) {
                        throw e;
                     }

                     MediaFactory.getPlatform().logDebug(this, 22, -1, e);
                     if (count++ >= this._retry) {
                        var14 = false;
                        break;
                     }
                     continue;
                  }
               }
            } catch (Throwable var22) {
               MediaFactory.getPlatform().logDebug(this, 22, -1, e);
               timedOut = true;
               throw new MediaException(7);
            }
         } finally {
            if (var14) {
               if (!timedOut) {
                  int response = c.getResponseCode();
                  String message = c.getResponseMessage();
                  if (response != 200) {
                     if (response < 200 || response > 600) {
                        response = 600;
                     }

                     throw new MediaException(response, message);
                  }

                  this._previousConnection = c;
               }
            }
         }

         if (!timedOut) {
            int response = c.getResponseCode();
            String message = c.getResponseMessage();
            if (response == 200) {
               this._previousConnection = c;
               return is;
            }

            if (response < 200 || response > 600) {
               response = 600;
            }

            throw new MediaException(response, message);
         }
      }

      return is;
   }

   protected InputStream getStoreInputStream(String uri, ConnectionInfo info) {
      InputStream is = null;
      String resource = uri.substring(7);
      FileConnection file = (FileConnection)javax.microedition.io.Connector.open(((StringBuffer)(new Object("file:///store"))).append(resource).toString());
      if (file != null && file.exists()) {
         return file.openInputStream();
      } else {
         throw new Object(uri);
      }
   }

   protected InputStream getSDCardInputStream(String uri, ConnectionInfo info) {
      InputStream is = null;
      String resource = uri.substring(6);
      FileConnection file = (FileConnection)javax.microedition.io.Connector.open(((StringBuffer)(new Object("file:///SDCard"))).append(resource).toString());
      if (file != null && file.exists()) {
         return file.openInputStream();
      } else {
         throw new Object(uri);
      }
   }

   protected InputStream getJarInputStream(String uri, ConnectionInfo info) {
      InputStream is = null;
      String resource = uri.substring(6);
      int questionIndex = resource.indexOf(63);
      if (questionIndex != -1) {
         resource = resource.substring(0, questionIndex);
      }

      is = this.getClass().getResourceAsStream(resource);
      if (is == null) {
         throw new Object(uri);
      } else {
         return is;
      }
   }

   @Override
   public InputStream getInputStream(String uri, ConnectionInfo info) {
      boolean handled = false;
      InputStream is = null;
      if (uri.startsWith("cod://")) {
         handled = true;
         is = this.getCodInputStream(uri, info);
      }

      if (!handled && uri.startsWith("jar://")) {
         handled = true;
         is = this.getJarInputStream(uri, info);
      }

      if (!handled && uri.startsWith("store:/")) {
         handled = true;
         is = this.getStoreInputStream(uri, info);
      }

      if (!handled && uri.startsWith("file:/")) {
         handled = true;
         is = this.getSDCardInputStream(uri, info);
      }

      if (!handled) {
         is = this.getHttpInputStream(uri, info);
      }

      return is;
   }

   @Override
   public void releaseConnection(ConnectionInfo info) {
      if (info != null) {
         Object con = info.getConnection();
         if (con instanceof Object) {
            ((Connection)con).close();
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void setProperty(String name, String value) {
      if ("Timeout".equals(name)) {
         int intValue = 0;
         boolean var10 = false /* VF: Semaphore variable */;

         try {
            var10 = true;
            intValue = Integer.parseInt(value);
            var10 = false;
         } finally {
            if (var10) {
               throw new Object();
            }
         }

         if (this._timeOut != intValue && intValue > 0) {
            this._timeOut = intValue;
            this.connectionTimeout = ((StringBuffer)(new Object())).append(CONNECTION_TIMEOUT).append(intValue).toString();
         }
      } else if ("Retry".equals(name)) {
         int intValue = 0;
         boolean var7 = false /* VF: Semaphore variable */;

         try {
            var7 = true;
            intValue = Integer.parseInt(value);
            var7 = false;
         } finally {
            if (var7) {
               throw new Object();
            }
         }

         if (intValue < 0) {
            this._retry = 0;
         } else {
            this._retry = intValue;
         }
      } else {
         throw new Object();
      }
   }
}
