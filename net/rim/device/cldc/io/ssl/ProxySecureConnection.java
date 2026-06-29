package net.rim.device.cldc.io.ssl;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.SecureConnection;
import javax.microedition.io.SecurityInfo;
import javax.microedition.io.SocketConnection;
import javax.microedition.pki.Certificate;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.cldc.io.ippp.SocketBaseIOException;
import net.rim.device.cldc.io.utility.URL;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.component.BackgroundDialog;
import net.rim.device.internal.ui.component.SimpleChoiceDialog;
import net.rim.vm.Array;

public final class ProxySecureConnection implements SecureConnection {
   private SocketConnection _subConnection;
   private String _name;
   private int _mode;
   private boolean _timeouts;
   private InputStream _input;
   private OutputStream _output;
   private DataBuffer _buffer;
   private boolean _isClosed;
   private static final boolean DEBUG = false;
   public static final int INIT_BYTE = 0;
   public static final byte INIT_ADDRESS = 1;
   public static final byte CERT_NOT_TRUST_ALL = 2;
   public static final byte CERT_TRUST_ALL = 3;
   public static final int CONNECTION_HOSTNAME_NOT_VERIFIED = 128;
   public static final int CONNECTION_NOT_TRUSTED_BUT_ALLOWED = 126;
   public static final int CONNECTION_NOT_TRUSTED_AND_NOT_ALLOWED = 125;
   public static final int GENERIC_TLS_EXCEPTION = 124;
   private static ResourceBundle _rb = ResourceBundle.getBundle(-320500590281765934L, "net.rim.device.internal.resource.SSL");

   @Override
   public final void close() {
      if (!this._isClosed) {
         this._subConnection.close();
         this._isClosed = true;
      }
   }

   @Override
   public final OutputStream openOutputStream() {
      if (this._isClosed) {
         throw new Object("Connection closed");
      }

      OutputStream outputStream = this._subConnection.openOutputStream();
      return new ProxySecureConnection$ProxyOutputStream(this, outputStream);
   }

   @Override
   public final DataInputStream openDataInputStream() {
      if (this._isClosed) {
         throw new Object("Connection closed");
      }

      InputStream inputStream = this._subConnection.openInputStream();
      return (DataInputStream)(new Object(new ProxySecureConnection$ProxyInputStream(this, inputStream)));
   }

   @Override
   public final DataOutputStream openDataOutputStream() {
      if (this._isClosed) {
         throw new Object("Connection closed");
      }

      OutputStream outputStream = this._subConnection.openOutputStream();
      return (DataOutputStream)(new Object(new ProxySecureConnection$ProxyOutputStream(this, outputStream)));
   }

   @Override
   public final InputStream openInputStream() {
      if (this._isClosed) {
         throw new Object("Connection closed");
      }

      InputStream inputStream = this._subConnection.openInputStream();
      return new ProxySecureConnection$ProxyInputStream(this, inputStream);
   }

   @Override
   public final void setSocketOption(byte option, int value) {
      if (this._isClosed) {
         throw new Object("Connection closed");
      }

      this._subConnection.setSocketOption(option, value);
   }

   @Override
   public final int getSocketOption(byte option) {
      if (this._isClosed) {
         throw new Object("Connection closed");
      } else {
         return this._subConnection.getSocketOption(option);
      }
   }

   @Override
   public final String getLocalAddress() {
      if (this._isClosed) {
         throw new Object("Connection closed");
      } else {
         return this._subConnection.getLocalAddress();
      }
   }

   @Override
   public final int getLocalPort() {
      if (this._isClosed) {
         throw new Object("Connection closed");
      } else {
         return this._subConnection.getLocalPort();
      }
   }

   @Override
   public final String getAddress() {
      if (this._isClosed) {
         throw new Object("Connection closed");
      } else {
         return this._subConnection.getAddress();
      }
   }

   @Override
   public final int getPort() {
      if (this._isClosed) {
         throw new Object("Connection closed");
      } else {
         return this._subConnection.getPort();
      }
   }

   @Override
   public final SecurityInfo getSecurityInfo() {
      return null;
   }

   public ProxySecureConnection(String name, int mode, boolean timeouts, boolean trustAll) {
      this._subConnection = (SocketConnection)Connector.open(
         ((StringBuffer)(new Object("socket:"))).append(name).append(";ConnectionHandler=tls").toString(), mode, timeouts
      );
      this._name = name;
      this._mode = mode;
      this._timeouts = timeouts;
      this.processTLSRequest(trustAll);
   }

   private final void processTLSRequest(boolean trustAll) {
      URL url = (URL)(new Object("ssl", this._name));
      String hostName = url.getHost();
      DataOutputStream tempOut = this._subConnection.openDataOutputStream();
      tempOut.writeByte(0);
      tempOut.writeByte(1);
      tempOut.writeInt(hostName.length());
      tempOut.write(hostName.getBytes());
      tempOut.writeInt(url.getPort());
      tempOut.writeByte(trustAll ? 3 : 2);
   }

   public static final void handleException(SocketBaseIOException e, String name) throws SocketBaseIOException, TLSIOException {
      switch (e.getExceptionCode()) {
         case 123:
         case 127:
            throw e;
         case 124:
            String otherSSLError = e.getMessage();
            throw new TLSIOException(new TLSException(e));
         case 125:
            String var11 = MessageFormat.format(_rb.getString(18), new Object[]{e.getMessage()});
            String[] ok = CommonResource.getStringArray(10004);
            String[] choices = new Object[]{ok[0], _rb.getString(15)};
            SimpleChoiceDialog dialog = (SimpleChoiceDialog)(new Object(var11, choices, 0, Bitmap.getPredefinedBitmap(0), 134217728));
            BackgroundDialog.show(dialog);
            int selection = dialog.getSelectedIndex();
            if (selection == 1) {
               if (SSLOptionsRegistration.doesDeviceSideExist()) {
                  var11 = MessageFormat.format(_rb.getString(9), new Object[]{_rb.getString(14)});
               } else {
                  var11 = MessageFormat.format(_rb.getString(9), new String[]{""});
               }

               RichTextField field = (RichTextField)(new Object(var11, 27021597764222976L));
               SimpleChoiceDialog infoDialog = (SimpleChoiceDialog)(new Object(
                  field, CommonResource.getStringArray(10004), 0, Bitmap.getPredefinedBitmap(0), 134217728
               ));
               field.setFocus();
               BackgroundDialog.show(infoDialog);
            }

            throw new TLSIOException(new TLSException(e, e.getMessage()));
         case 126:
            String var10 = _rb.getString(11);

            while (true) {
               int var13 = BackgroundDialog.getChoice(var10, _rb.getStringArray(12), 1);
               if (var13 != 2) {
                  if (var13 == 1) {
                     throw new TLSIOException(new TLSCancelException(e));
                  }

                  if (var13 == 3) {
                     addAsTrusted(name);
                     return;
                  }

                  return;
               }

               String certChainString = e.getMessage();
               Certificate[] certChain = parseCertChain(certChainString);
               if (certChain != null && certChain.length > 0) {
                  CertificateDisplayDialog dialog = new CertificateDisplayDialog(certChain, 0, 134217728);
                  BackgroundDialog.show(dialog);
               }
            }
         case 128:
         default:
            String message = _rb.getString(16);
            int choice = BackgroundDialog.getChoice(message, _rb.getStringArray(17), 1);
            if (choice == 1) {
               throw new TLSIOException(new TLSCancelException(e));
            } else if (choice == 2) {
               addAsTrusted(name);
            }
      }
   }

   private static final void addAsTrusted(String name) {
      try {
         TLSOptionStore optionStore = TLSOptionStore.getOptions();
         URL url = (URL)(new Object(name));
         optionStore.addTrustedHost(url.getHost());
      } finally {
         return;
      }
   }

   public static final Certificate[] parseCertChain(String certChainString) {
      if (certChainString.length() == 0) {
         return null;
      }

      ProxyCertificate[] certChain = new ProxyCertificate[0];
      String issuer = null;
      String subject = null;
      String validNotAfter = null;
      String validNotBefore = null;
      String serialNumber = null;
      String version = null;
      int indexTail = 0;
      int fieldNumber = 1;
      int certNumber = 0;
      String tempString = "";
      int currentOffset = 0;

      while (true) {
         if (certChainString.charAt(currentOffset) == ';' && certChainString.charAt(currentOffset + 1) == ';') {
            certNumber = certChain.length;
            Array.resize(certChain, certNumber + 1);
            certChain[certNumber] = new ProxyCertificate(issuer, subject, validNotAfter, validNotBefore, serialNumber, version);
            fieldNumber = 1;
            currentOffset += 2;
            if (certChainString.length() <= currentOffset) {
               return certChain;
            }
         }

         indexTail = certChainString.indexOf(59, currentOffset);
         tempString = certChainString.substring(currentOffset, indexTail);
         switch (fieldNumber) {
            case 0:
               break;
            case 1:
            default:
               issuer = tempString;
               break;
            case 2:
               subject = tempString;
               break;
            case 3:
               validNotAfter = tempString;
               break;
            case 4:
               validNotBefore = tempString;
               break;
            case 5:
               serialNumber = tempString;
               break;
            case 6:
               version = tempString;
         }

         currentOffset += tempString.length() + 1;
         fieldNumber++;
      }
   }

   private final void replay() {
      if (this._subConnection != null) {
         this._subConnection.close();
      }

      if (this._input != null) {
         this._input.close();
      }

      if (this._output != null) {
         this._output.close();
      }

      this._subConnection = (SocketConnection)Connector.open(
         ((StringBuffer)(new Object("socket:"))).append(this._name).append(";ConnectionHandler=tls").toString(), this._mode, this._timeouts
      );
      byte[] request = this._buffer.getArray();
      this.processTLSRequest(true);
      this._output = this._subConnection.openOutputStream();
      this._output.write(request);
      this._output.flush();
      this._buffer = (DataBuffer)(new Object());
      this._input = this._subConnection.openInputStream();
   }
}
