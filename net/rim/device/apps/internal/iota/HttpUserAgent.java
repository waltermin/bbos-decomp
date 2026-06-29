package net.rim.device.apps.internal.iota;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.StreamConnection;
import net.rim.device.api.browser.util.UAProf;
import net.rim.device.api.io.IOCancelledException;
import net.rim.device.api.io.http.AuthScheme;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.cldc.io.datarecovery.DataRecovery;
import net.rim.device.cldc.io.devicehttp.HttpConnectionManager;
import net.rim.device.cldc.io.devicehttps.ClientProtocol;
import net.rim.device.internal.browser.wap.WPTCPServiceRecord;
import net.rim.vm.Array;

final class HttpUserAgent {
   private boolean _cancelled;
   private ServiceRecord _serviceRecord;
   private String _contentType;
   private String _contentRange;
   private Hashtable _e2eHeaders;
   private String _url;
   private HttpConnection _connection;
   private InputStream _inputStream;
   private OutputStream _outputStream;
   private String _proxyUsername;
   private String _proxyPassword;
   private static final boolean DEBUG = false;
   private static final String USER_AGENT = "BlackBerry"
      + DeviceInfo.getDeviceName()
      + '/'
      + ApplicationDescriptor.currentApplicationDescriptor().getVersion()
      + " RIMOS/"
      + DeviceInfo.getPlatformVersion()
      + " MMP/2.0";
   private static final String X_WAP_PROFILE = '"' + UAProf.getFormattedUAProfURI("http://device.sprintpcs.com/RIM/BlackBerry{0}/{2}.rdf") + '"';
   private static final String ACCEPT = "application/vnd.phonecom.mmc-xml, multipart/related, application/octet-stream";
   private static final int MAX_REDIRECTS = 13;
   private static final String URL_PARAMETERS = ";apn=iota;DeviceSide=true;ConnectionSetup=delayed;ConnectionUID=IOTA Provisioning";
   private static final int BEFORE_QUOTED_HEADER = 0;
   private static final int FIELD_NAME = 1;
   private static final int ESCAPED_FIELD_VALUE = 2;
   private static final int AFTER_QUOTED_HEADER = 3;

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public HttpUserAgent(String username, String pwd, String proxyAddress) {
      EventLogger.logEvent(4411276428801970910L, 1213546857);
      String nameAndUid = "IOTA Provisioning";
      ServiceRecord serviceRecord = new ServiceRecord();
      serviceRecord.setType(0);
      serviceRecord.setName(nameAndUid);
      serviceRecord.setUid(nameAndUid);
      serviceRecord.setCid(WPTCPServiceRecord.SERVICE_CID);
      serviceRecord.setEncryptionMode(1);
      serviceRecord.setCompressionMode(1);

      try {
         WPTCPServiceRecord wptcpRecord = new WPTCPServiceRecord();
         wptcpRecord.setProperty(1, proxyAddress);
         wptcpRecord.setProperty(8, proxyAddress);
         wptcpRecord.setProperty(2, true);
         wptcpRecord.setProperty(3, true);
         wptcpRecord.setProperty(20, 0);
         serviceRecord.setApplicationData(wptcpRecord.getEncodedData());
      } catch (Throwable var12) {
         throw new RuntimeException("Exception creating IOTA service record: " + ioe.toString());
      }

      ServiceBook serviceBook = ServiceBook.getSB();
      if (serviceBook.addRecord(serviceRecord) == null) {
         throw new RuntimeException("Unable to add IOTA service record");
      }

      serviceBook.commit();

      label47:
      try {
         Connector.open("tcp://127.0.0.1:0;apn=iota;DeviceSide=true;connectiontimeout=z").close();
      } finally {
         break label47;
      }

      DataRecovery.getInstance().fileReport(0);
      this._serviceRecord = serviceRecord;
      this._proxyUsername = username;
      this._proxyPassword = pwd;
   }

   public final void cleanup() {
      if (!this._cancelled) {
         this._cancelled = true;
         EventLogger.logEvent(4411276428801970910L, 1213546851);
         ServiceBook.getSB().removeRecord(this._serviceRecord);
         if (Application.isEventDispatchThread()) {
            new HttpUserAgent$CloseThread(this).start();
            return;
         }

         this.closeConnections();
      }
   }

   private final void closeConnections() {
      EventLogger.logEvent(4411276428801970910L, 1214734691);
      if (this._outputStream != null) {
         label78:
         try {
            this._outputStream.close();
         } finally {
            break label78;
         }
      }

      if (this._inputStream != null) {
         label74:
         try {
            this._inputStream.close();
         } finally {
            break label74;
         }
      }

      if (this._connection != null) {
         label70:
         try {
            this._connection.close();
         } finally {
            break label70;
         }
      }

      HttpConnectionManager.getInstance().closeConnections();
      EventLogger.logEvent(4411276428801970910L, 1214538083);
   }

   public final String getURL() {
      return this._url;
   }

   public final String getContentType() {
      return this._contentType;
   }

   public final int getContentRangeStart() {
      String contentRange = this._contentRange;
      if (contentRange != null) {
         int space = contentRange.indexOf(32);
         if (space >= 0) {
            int hyphen = contentRange.indexOf(45, space + 1);
            if (hyphen > space + 1) {
               label25:
               try {
                  return Integer.parseInt(contentRange.substring(space + 1, hyphen));
               } finally {
                  break label25;
               }
            }
         }

         ProvisioningServiceAgent.logEvents("Content-Range: " + contentRange);
      }

      return -1;
   }

   public final byte[] get(String url) {
      return this.doGetOrPost(url, null, null, "GET");
   }

   public final byte[] get(String url, int offset, int size) {
      return this.doGetOrPost(url, null, null, "GET", offset, size);
   }

   public final byte[] post(String url, String contentType, byte[] content) {
      return this.doGetOrPost(url, contentType, content, "POST");
   }

   private final byte[] doGetOrPost(String url, String contentType, byte[] postData, String requestMethod) {
      return this.doGetOrPost(url, contentType, postData, requestMethod, -1, -1);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final byte[] doGetOrPost(String url, String contentType, byte[] postData, String requestMethod, int contentOffset, int contentSize) throws IOCancelledException {
      if (url == null) {
         throw new NullPointerException();
      }

      AuthScheme bas = AuthScheme.getAuthScheme("Basic");
      bas.setParameter("username", this._proxyUsername);
      bas.setParameter("password", this._proxyPassword);
      String authResponse = bas.getAuthResponse();
      int numRedirects = 0;

      while (numRedirects <= 13) {
         HttpConnection conn = null;
         InputStream is = null;
         OutputStream os = null;
         if (this._cancelled) {
            throw new IOCancelledException();
         }

         boolean var132 = false /* VF: Semaphore variable */;

         byte[] var162;
         label1538: {
            try {
               var132 = true;
               boolean httpsRequest = StringUtilities.startsWithIgnoreCase(url, "https:", 1701707776);
               EventLogger.logEvent(4411276428801970910L, httpsRequest ? 1215259507 : 1215259497);
               this._url = url;
               conn = this._connection = (HttpConnection)Connector.open(
                  url + ";apn=iota;DeviceSide=true;ConnectionSetup=delayed;ConnectionUID=IOTA Provisioning"
               );
               EventLogger.logEvent(4411276428801970910L, 1215262820, 5);
               conn.setRequestMethod(requestMethod);
               conn.setRequestProperty("User-Agent", USER_AGENT);
               conn.setRequestProperty("Accept", "application/vnd.phonecom.mmc-xml, multipart/related, application/octet-stream");
               conn.setRequestProperty("x-wap-profile", X_WAP_PROFILE);
               if (!httpsRequest) {
                  conn.setRequestProperty("Proxy-Authorization", authResponse);
               }

               if (contentOffset >= 0) {
                  EventLogger.logEvent(4411276428801970910L, 1215459953, 5);
                  StringBuffer rangeHeader = new StringBuffer("bytes=");
                  rangeHeader.append(contentOffset).append('-');
                  if (contentSize > 0) {
                     rangeHeader.append(contentOffset + contentSize - 1);
                  }

                  conn.setRequestProperty("Range", rangeHeader.toString());
               }

               if (contentType != null) {
                  conn.setRequestProperty("Content-Type", contentType);
               }

               if (conn instanceof ClientProtocol) {
                  ClientProtocol httpsConn = (ClientProtocol)conn;
                  String host = conn.getHost();
                  if (host != null) {
                     String originServer = StringUtilities.toLowerCase(host, 1701707776) + ':' + conn.getPort();
                     StreamConnection subConnection = httpsConn.getTLSSubConnection();
                     if (subConnection instanceof HttpConnection) {
                        HttpConnection proxyConnection = (HttpConnection)subConnection;
                        if ("CONNECT".equals(proxyConnection.getRequestMethod())) {
                           label1502:
                           try {
                              proxyConnection.setRequestProperty("Proxy-Authorization", authResponse);
                           } finally {
                              break label1502;
                           }

                           EventLogger.logEvent(4411276428801970910L, 1214591586, 5);
                           this.cacheE2EHeaders(proxyConnection.getHeaderField("x-up-tpd-e2e-headers"), originServer);
                           EventLogger.logEvent(4411276428801970910L, 1214591585, 5);
                        }
                     }

                     if (this._e2eHeaders != null) {
                        HttpHeader[] headers = (HttpHeader[])this._e2eHeaders.get(originServer);
                        if (headers != null) {
                           for (int i = 0; i < headers.length; i++) {
                              EventLogger.logEvent(4411276428801970910L, 1214591589, 5);
                              conn.setRequestProperty(headers[i].getName(), headers[i].getValue());
                           }
                        }
                     }
                  }
               }

               if (postData != null) {
                  EventLogger.logEvent(4411276428801970910L, 1215787108, 5);
                  os = this._outputStream = conn.openOutputStream();
                  os.write(postData);
                  os.flush();
                  EventLogger.logEvent(4411276428801970910L, 1215788144, 5);
               }

               EventLogger.logEvent(4411276428801970910L, 1215260521, 5);
               is = this._inputStream = conn.openInputStream();
               EventLogger.logEvent(4411276428801970910L, 1215262825, 5);
               byte[] bytes = new byte[512];
               int offset = 0;

               while (true) {
                  int length = bytes.length - offset;
                  if (length == 0) {
                     length = 512;
                     Array.resize(bytes, bytes.length + length);
                  }

                  int read = is.read(bytes, offset, length);
                  if (read < 0) {
                     length = conn.getResponseCode();
                     if (length != 200 && length != 206) {
                        ProvisioningServiceAgent.logEvents(String.valueOf(length) + ' ' + conn.getResponseMessage());
                     }

                     if (length >= 300 && length < 400 && numRedirects < 13 && length != 305) {
                        String location = conn.getHeaderField("Location");
                        if (location != null) {
                           url = location;
                           if (length != 302 && length != 303) {
                              var132 = false;
                           } else {
                              postData = null;
                              contentType = null;
                              requestMethod = "GET";
                              var132 = false;
                           }
                           break;
                        }
                     }

                     this._contentType = conn.getHeaderField("Content-Type");
                     this._contentRange = conn.getHeaderField("Content-Range");
                     Array.resize(bytes, offset);
                     EventLogger.logEvent(4411276428801970910L, 1215460208);
                     var162 = bytes;
                     var132 = false;
                     break label1538;
                  }

                  offset += read;
               }
            } finally {
               if (var132) {
                  EventLogger.logEvent(4411276428801970910L, 1214474083, 5);
                  if (os != null) {
                     label1450:
                     try {
                        os.close();
                     } finally {
                        break label1450;
                     }
                  }

                  if (is != null) {
                     label1446:
                     try {
                        is.close();
                     } finally {
                        break label1446;
                     }
                  }

                  if (conn != null) {
                     label1442:
                     try {
                        conn.close();
                     } finally {
                        break label1442;
                     }
                  }

                  EventLogger.logEvent(4411276428801970910L, 1214473315, 5);
               }
            }

            EventLogger.logEvent(4411276428801970910L, 1214474083, 5);
            if (os != null) {
               label1477:
               try {
                  os.close();
               } finally {
                  break label1477;
               }
            }

            if (is != null) {
               label1473:
               try {
                  is.close();
               } finally {
                  break label1473;
               }
            }

            if (conn != null) {
               label1469:
               try {
                  conn.close();
               } finally {
                  break label1469;
               }
            }

            EventLogger.logEvent(4411276428801970910L, 1214473315, 5);
            numRedirects++;
            continue;
         }

         EventLogger.logEvent(4411276428801970910L, 1214474083, 5);
         if (os != null) {
            label1464:
            try {
               os.close();
            } finally {
               break label1464;
            }
         }

         if (is != null) {
            label1460:
            try {
               is.close();
            } finally {
               break label1460;
            }
         }

         if (conn != null) {
            label1456:
            try {
               conn.close();
            } finally {
               break label1456;
            }
         }

         EventLogger.logEvent(4411276428801970910L, 1214473315, 5);
         return var162;
      }

      return null;
   }

   private final void cacheE2EHeaders(String e2eHeadersValue, String originServer) {
      if (e2eHeadersValue == null) {
         if (this._e2eHeaders != null) {
            this._e2eHeaders.remove(originServer);
            return;
         }
      } else {
         int state = 0;
         StringBuffer fieldName = new StringBuffer();
         StringBuffer fieldValue = new StringBuffer();
         HttpHeader[] headers = null;
         int length = e2eHeadersValue.length();

         for (int i = 0; i < length; i++) {
            char ch = e2eHeadersValue.charAt(i);
            switch (state) {
               case -1:
                  throw new IllegalStateException();
               case 0:
               default:
                  if (ch == '"') {
                     state = 1;
                     fieldName.setLength(0);
                  }
                  break;
               case 1:
                  if (ch == '"') {
                     state = 3;
                  } else if (ch == ':') {
                     state = 2;
                     fieldValue.setLength(0);
                  } else if (ch != ' ' && ch != '\t' && ch != '\r' && ch != '\n') {
                     fieldName.append(ch);
                  }
                  break;
               case 2:
                  if (ch == '"') {
                     if (headers == null) {
                        headers = new HttpHeader[1];
                     } else {
                        Array.resize(headers, headers.length + 1);
                     }

                     headers[headers.length - 1] = new HttpHeader(fieldName.toString(), fieldValue.toString().trim());
                     state = 3;
                  } else {
                     fieldValue.append(ch);
                  }
                  break;
               case 3:
                  if (ch == ',') {
                     state = 0;
                  }
            }
         }

         if (headers == null) {
            if (this._e2eHeaders != null) {
               this._e2eHeaders.remove(originServer);
               return;
            }
         } else {
            if (this._e2eHeaders == null) {
               this._e2eHeaders = new Hashtable(1);
            }

            this._e2eHeaders.put(originServer, headers);
         }
      }
   }
}
