package net.rim.device.apps.internal.secureemail.encodings.pgp.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.SocketConnection;
import net.rim.device.api.crypto.tls.tls10.TLS10Connection;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRouting;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.PersistentContentListener;
import net.rim.device.api.xml.XMLHashtable;
import net.rim.device.api.xml.parsers.SAXParser;
import net.rim.device.apps.internal.secureemail.server.SecureEmailServerOperationListener;
import net.rim.device.cldc.io.utility.URL;

public final class PGPUniversalServerSOAPHandler implements PersistentContentListener {
   SAXParser _saxParser;
   String _hostName;
   String[] _serviceUIDs;
   TLS10Connection _tlsConnection;
   private long _lastConnectionUseTimeStamp;
   private boolean _deviceLocking;
   String _httpCookie;
   private final byte[] _buffer;
   private static final long LOCK_CONNECTION_TIME_OUT = 60000L;
   private static final String VERSION = "<version>\n<major>1</major>\n<minor>5</minor>\n</version>\n";
   private static final String CLIENT_VERSION = "<client-version>\n<major>9</major>\n<minor>0</minor>\n<subminor>0</subminor>\n<subsubminor>0</subsubminor>\n</client-version>\n";
   private static final int BUFFER_LENGTH = 64;

   public PGPUniversalServerSOAPHandler(String param1, String[] param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: invokespecial java/lang/Object.<init> ()V
      // 04: aload 0
      // 05: bipush 64
      // 07: newarray 8
      // 09: putfield net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServerSOAPHandler._buffer [B
      // 0c: aload 0
      // 0d: aload 1
      // 0e: putfield net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServerSOAPHandler._hostName Ljava/lang/String;
      // 11: aload 0
      // 12: aload 2
      // 13: putfield net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServerSOAPHandler._serviceUIDs [Ljava/lang/String;
      // 16: aload 0
      // 17: invokestatic net/rim/device/api/system/PersistentContent.addWeakListener (Lnet/rim/device/api/system/PersistentContentListener;)V
      // 1a: aload 0
      // 1b: invokestatic net/rim/device/api/xml/parsers/SAXParserFactory.newInstance ()Lnet/rim/device/api/xml/parsers/SAXParserFactory;
      // 1e: invokevirtual net/rim/device/api/xml/parsers/SAXParserFactory.newSAXParser ()Lnet/rim/device/api/xml/parsers/SAXParser;
      // 21: putfield net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServerSOAPHandler._saxParser Lnet/rim/device/api/xml/parsers/SAXParser;
      // 24: return
      // 25: astore 3
      // 26: new java/lang/Object
      // 29: dup
      // 2a: aload 3
      // 2b: invokevirtual net/rim/device/api/xml/parsers/ParserConfigurationException.toString ()Ljava/lang/String;
      // 2e: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 31: athrow
      // 32: astore 3
      // 33: new java/lang/Object
      // 36: dup
      // 37: aload 3
      // 38: invokevirtual org/xml/sax/SAXException.toString ()Ljava/lang/String;
      // 3b: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 3e: athrow
      // try (14 -> 18): 19 null
      // try (14 -> 18): 26 null
   }

   public final boolean isAuthenticated() {
      return this._httpCookie != null;
   }

   public final boolean requestEnrollment(String email, String ephemeralPublicKey, String userValue, SecureEmailServerOperationListener listener) {
      EventLogger.logEvent(234044482576569793L, 1431455086);
      XMLHashtable response = this.sendSOAPMessage(
         ((StringBuffer)(new Object("<ovidsrv:Enroll xmlns:ovidsrv=\"http://www.pgp.com/\">\n<email>")))
            .append(email)
            .append("</email>")
            .append("<keyblock>")
            .append(ephemeralPublicKey)
            .append("</keyblock>")
            .append("<user-value>")
            .append(userValue)
            .append("</user-value>")
            .append("<version>\n<major>1</major>\n<minor>5</minor>\n</version>\n")
            .append("</ovidsrv:Enroll>")
            .toString(),
         listener
      );
      return response.isPresent("/EnrollResponse");
   }

   public final XMLHashtable authenticateInternalWithUsernamePassword(String username, String password, SecureEmailServerOperationListener listener) {
      EventLogger.logEvent(234044482576569793L, 1430341973);
      return this.sendSOAPMessage(
         ((StringBuffer)(new Object("<ovidsrv:AuthenticateInternalPassphrase xmlns:ovidsrv=\"http://www.pgp.com/\"><username>")))
            .append(username)
            .append("</username>")
            .append("<passphrase>")
            .append(password)
            .append("</passphrase>")
            .append("<version>\n<major>1</major>\n<minor>5</minor>\n</version>\n")
            .append("<client-version>\n<major>9</major>\n<minor>0</minor>\n<subminor>0</subminor>\n<subsubminor>0</subsubminor>\n</client-version>\n")
            .append("</ovidsrv:AuthenticateInternalPassphrase>")
            .toString(),
         listener
      );
   }

   public final XMLHashtable authenticateInternalWithCookie(String authenticationCookie, SecureEmailServerOperationListener listener) {
      EventLogger.logEvent(234044482576569793L, 1430341955);
      return this.sendSOAPMessage(
         ((StringBuffer)(new Object("<ovidsrv:AuthenticateInternal xmlns:ovidsrv=\"http://www.pgp.com/\"><cookie>")))
            .append(authenticationCookie)
            .append("</cookie>")
            .append("<version>\n<major>1</major>\n<minor>5</minor>\n</version>\n")
            .append("<client-version>\n<major>9</major>\n<minor>0</minor>\n<subminor>0</subminor>\n<subsubminor>0</subsubminor>\n</client-version>\n")
            .append("</ovidsrv:AuthenticateInternal>")
            .toString(),
         listener
      );
   }

   public final XMLHashtable getAuthenticationCookie(SecureEmailServerOperationListener listener) {
      EventLogger.logEvent(234044482576569793L, 1430733123);
      return this.sendSOAPMessage("<ovidsrv:GetNonExpiringCookie xmlns:ovidsrv=\"http://www.pgp.com/\"/>", listener);
   }

   public final XMLHashtable getPreferences(SecureEmailServerOperationListener listener) {
      EventLogger.logEvent(234044482576569793L, 1430737010);
      return this.sendSOAPMessage("<ovidsrv:GetPrefs xmlns:ovidsrv=\"http://www.pgp.com/\"/>", listener);
   }

   public final XMLHashtable getPolicy(SecureEmailServerOperationListener listener) {
      EventLogger.logEvent(234044482576569793L, 1430737004);
      return this.sendSOAPMessage("<ovidsrv:GetPolicy xmlns:ovidsrv=\"http://www.pgp.com/\"/>", listener);
   }

   public final XMLHashtable getGranularPolicy(SecureEmailServerOperationListener listener) {
      EventLogger.logEvent(234044482576569793L, 1430734672);
      return this.sendSOAPMessage("<ovidsrv:GetGranularPolicy xmlns:ovidsrv=\"http://www.pgp.com/\"/>", listener);
   }

   public final XMLHashtable getKeyByEmail(String email, SecureEmailServerOperationListener listener) {
      EventLogger.logEvent(234044482576569793L, 1430735685);
      return this.sendSOAPMessage(
         ((StringBuffer)(new Object("<ovidsrv:GetKeyByEmail xmlns:ovidsrv=\"http://www.pgp.com/\"><email>")))
            .append(email)
            .append("</email>")
            .append("</ovidsrv:GetKeyByEmail>")
            .toString(),
         listener
      );
   }

   public final XMLHashtable getKeyByID(String keyID, SecureEmailServerOperationListener listener) {
      EventLogger.logEvent(234044482576569793L, 1430735684);
      return this.sendSOAPMessage(
         ((StringBuffer)(new Object("<ovidsrv:GetKeyByKeyID xmlns:ovidsrv=\"http://www.pgp.com/\"><keyid>")))
            .append(keyID)
            .append("</keyid>")
            .append("</ovidsrv:GetKeyByKeyID>")
            .toString(),
         listener
      );
   }

   public final XMLHashtable getKeyByID(String keyID, String email, SecureEmailServerOperationListener listener) {
      EventLogger.logEvent(234044482576569793L, 1430735689);
      return this.sendSOAPMessage(
         ((StringBuffer)(new Object("<ovidsrv:GetKeyByKeyID xmlns:ovidsrv=\"http://www.pgp.com/\"><keyid>")))
            .append(keyID)
            .append("</keyid>")
            .append("<email>")
            .append(email)
            .append("</email>")
            .append("</ovidsrv:GetKeyByKeyID>")
            .toString(),
         listener
      );
   }

   public final XMLHashtable getKeyValidity(String key, SecureEmailServerOperationListener listener) {
      EventLogger.logEvent(234044482576569793L, 1430735702);
      return this.sendSOAPMessage(
         ((StringBuffer)(new Object("<ovidsrv:CheckKeyValidity xmlns:ovidsrv=\"http://www.pgp.com/\"><keyblock>")))
            .append(key)
            .append("</keyblock>")
            .append("</ovidsrv:CheckKeyValidity>")
            .toString(),
         listener
      );
   }

   public final XMLHashtable getCertificateChain(String certificateEncoding, SecureEmailServerOperationListener listener) {
      EventLogger.logEvent(234044482576569793L, 1430733635);
      return this.sendSOAPMessage(
         ((StringBuffer)(new Object("<ovidsrv:GetCertificateChain xmlns:ovidsrv=\"http://www.pgp.com/\"><certificate>")))
            .append(certificateEncoding)
            .append("</certificate>")
            .append("</ovidsrv:GetCertificateChain>")
            .toString(),
         listener
      );
   }

   public final XMLHashtable downloadKey(SecureEmailServerOperationListener listener) {
      EventLogger.logEvent(234044482576569793L, 1430550347);
      return this.sendSOAPMessage("<ovidsrv:DownloadPrivateKey xmlns:ovidsrv=\"http://www.pgp.com/\"/>", listener);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final synchronized XMLHashtable sendSOAPMessage(String request, SecureEmailServerOperationListener listener) throws IOException {
      if (Application.isEventDispatchThread()) {
         throw new Object();
      }

      StringBuffer connectionStringBuffer = (StringBuffer)(new Object());
      connectionStringBuffer.append(this._hostName).append(':').append("443");
      connectionStringBuffer.append("/pgpuniversaldesktop");
      connectionStringBuffer.append(";deviceside=false;endtoenddesired");
      int numServiceUIDs = this._serviceUIDs.length;
      if (numServiceUIDs > 0) {
         String chosenUID = null;

         for (int i = 0; i < numServiceUIDs; i++) {
            if (this.isDataRoutable(this._serviceUIDs[i])) {
               chosenUID = this._serviceUIDs[i];
               break;
            }
         }

         if (chosenUID == null) {
            EventLogger.logEvent(234044482576569793L, 1430867794);
            throw new Object();
         }

         connectionStringBuffer.append(((StringBuffer)(new Object(";connectionuid="))).append(chosenUID).toString());
      }

      String connectionString = connectionStringBuffer.toString();
      boolean tlsConnectionOpened = false;

      while (true) {
         HttpConnection connection;
         while (true) {
            connection = null;
            if (this._tlsConnection == null) {
               this.openTLSConnection(connectionString);
               tlsConnectionOpened = true;
            }

            this._lastConnectionUseTimeStamp = System.currentTimeMillis();

            try {
               DataInputStream tlsDataInputStream = this._tlsConnection.openDataInputStream();
               DataOutputStream tlsDataOutputStream = this._tlsConnection.openDataOutputStream();
               URL httpsURL = (URL)(new Object("https", connectionString));
               connection = (HttpConnection)(new Object(
                  null, httpsURL, this._tlsConnection, tlsDataInputStream, tlsDataOutputStream, false, true, 3, false, true, null, false
               ));
               break;
            } catch (Throwable var30) {
               this._tlsConnection = null;
               if (tlsConnectionOpened) {
                  EventLogger.logEvent(234044482576569793L, e.toString().getBytes());
                  throw e;
               }
               continue;
            }
         }

         connection.setRequestMethod("POST");
         connection.setRequestProperty("User-Agent", "PGP (Unix)");
         connection.setRequestProperty("Content-Type", "text/xml; charset=\"utf-8\"");
         connection.setRequestProperty("SOAPAction", "");
         if (this._httpCookie != null) {
            connection.setRequestProperty("Cookie", this._httpCookie);
         }

         if (listener != null) {
            listener.setServerConnection(connection);
         }

         boolean var17 = false /* VF: Semaphore variable */;

         StringBuffer faultStringBuffer;
         label455: {
            try {
               label476: {
                  var17 = true;
                  OutputStream var37 = connection.openOutputStream();
                  this.write(
                     var37,
                     "<se:Envelope xmlns:se=\"http://schemas.xmlsoap.org/soap/envelope/\" se:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n<se:Header>\n<version>\n<major>1</major>\n<minor>5</minor>\n</version>\n</se:Header>\n<se:Body>\n"
                  );
                  this.write(var37, request);
                  this.write(var37, "</se:Body></se:Envelope>");

                  try {
                     int responseCode = connection.getResponseCode();
                     if (responseCode != 200) {
                        throw new Object(
                           ((StringBuffer)(new Object("HTTP response code: ")))
                              .append(responseCode)
                              .append(" ")
                              .append(connection.getHeaderField("Location"))
                              .append(" ")
                              .append(connection.getResponseMessage())
                              .toString()
                        );
                     }

                     String type = connection.getType();
                     if (type == null || !type.startsWith("text/xml")) {
                        throw new Object("Unexpected Content Type");
                     }

                     String cookie = connection.getHeaderField("Set-Cookie");
                     if (cookie != null) {
                        this._httpCookie = cookie;
                     }
                  } catch (Throwable var32) {
                     EventLogger.logEvent(234044482576569793L, 1431456079);
                     this._tlsConnection = null;
                     if (listener != null && listener.wasServerConnectionAborted()) {
                        EventLogger.logEvent(234044482576569793L, 1431650627);
                        throw e;
                     }

                     if (tlsConnectionOpened) {
                        EventLogger.logEvent(234044482576569793L, e.toString().getBytes());
                        throw e;
                     }

                     var17 = false;
                     break label476;
                  }

                  try {
                     XMLHashtable response = (XMLHashtable)(new Object(this._saxParser, connection.openInputStream(), false, false, "/Envelope/Body"));
                     if (response.isPresent("/Fault")) {
                        faultStringBuffer = (StringBuffer)(new Object());
                        faultStringBuffer.append("SOAP Fault: ");
                        faultStringBuffer.append(response.getString("/Fault/faultstring", null));
                        faultStringBuffer.append(',').append(' ');
                        faultStringBuffer.append(response.getString("/Fault/detail/fault-details/error-code", null));
                        faultStringBuffer.append(',').append(' ');
                        faultStringBuffer.append(response.getString("/Fault/detail/fault-details/error-message", null));
                        EventLogger.logEvent(234044482576569793L, faultStringBuffer.toString().getBytes());
                        throw new PGPUniversalServerSoapFaultException(response);
                     }

                     faultStringBuffer = response;
                     var17 = false;
                     break label455;
                  } catch (Throwable var31) {
                     String detailMessage = e.toString();
                     EventLogger.logEvent(234044482576569793L, detailMessage.getBytes());
                     throw new Object(detailMessage);
                  }
               }
            } finally {
               if (var17) {
                  if (listener != null) {
                     listener.clearServerConnection();
                  }
               }
            }

            if (listener != null) {
               listener.clearServerConnection();
            }
            continue;
         }

         if (listener != null) {
            listener.clearServerConnection();
         }

         return faultStringBuffer;
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void openTLSConnection(String connectionString) {
      EventLogger.logEvent(234044482576569793L, 1431262275);

      try {
         SocketConnection socketConnection = (SocketConnection)Connector.open(((StringBuffer)(new Object("socket://"))).append(connectionString).toString());
         this._tlsConnection = (TLS10Connection)(new Object(socketConnection, ((StringBuffer)(new Object("tls://"))).append(connectionString).toString(), false));
      } catch (Throwable var4) {
         throw new Object(e.toString());
      }
   }

   private final boolean isDataRoutable(String serviceUID) {
      return ServiceBook.getSB().getRecordByUidAndCid(serviceUID, "IPPP") == null ? false : ServiceRouting.getInstance().isServiceRoutable(serviceUID, -1);
   }

   private final void write(OutputStream output, String string) {
      int length = string.length();
      int offset = 0;

      while (offset + 64 < length) {
         for (int i = 0; i < 64; i++) {
            this._buffer[i] = (byte)string.charAt(offset++);
         }

         output.write(this._buffer, 0, 64);
      }

      length -= offset;

      for (int i = 0; i < length; i++) {
         this._buffer[i] = (byte)string.charAt(offset++);
      }

      output.write(this._buffer, 0, length);
   }

   @Override
   public final void persistentContentStateChanged(int state) {
      switch (state) {
         case 1:
            this._deviceLocking = false;
         case 0:
            return;
         case 2:
         default:
            this._deviceLocking = true;
            Application.getApplication().invokeLater(new PGPUniversalServerSOAPHandler$PGPTLSConnectionTerminator(this, null), 60000, false);
      }
   }

   @Override
   public final void persistentContentModeChanged(int generation) {
   }
}
