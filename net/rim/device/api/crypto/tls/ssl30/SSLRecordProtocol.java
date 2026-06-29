package net.rim.device.api.crypto.tls.ssl30;

import java.io.DataInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.SecurityInfo;
import javax.microedition.io.StreamConnection;
import net.rim.device.api.crypto.BlockDecryptorEngine;
import net.rim.device.api.crypto.BlockEncryptorEngine;
import net.rim.device.api.crypto.BlockFormatterEngine;
import net.rim.device.api.crypto.DecryptorInputStream;
import net.rim.device.api.crypto.EncryptorOutputStream;
import net.rim.device.api.crypto.MAC;
import net.rim.device.api.crypto.PrivateKey;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.crypto.tls.AlertProtocolMethods;
import net.rim.device.api.crypto.tls.ChangeCipherSpecProtocol;
import net.rim.device.api.crypto.tls.RecordProtocol;
import net.rim.device.api.crypto.tls.TLSByteArrayInputStream;
import net.rim.device.api.crypto.tls.TLSNoCopyByteArrayOutputStream;
import net.rim.device.api.crypto.tls.TLSSecurityInfo;
import net.rim.device.api.crypto.tls.TLSUtilities;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.cldc.io.ippp.SocketOutputStream;
import net.rim.device.cldc.io.ssl.SSLConnectionOptions;
import net.rim.device.cldc.io.utility.PacketLogger;
import net.rim.device.cldc.io.utility.URL;

public class SSLRecordProtocol extends RecordProtocol implements SSLRecordProtocolConstants {
   protected boolean _clientMode = true;
   private byte[] _readHeader = new byte[5];
   private byte[] _sequence = new byte[8];
   protected int _remoteVersion;
   protected int _cipherSuite;
   protected int _keyExchangeSize;
   protected int _state = 0;
   protected StreamConnection _subConnection;
   protected String _connectionName;
   protected URL _connectionURL;
   protected Certificate _certificate;
   protected Certificate[] _certificatePool;
   protected Certificate _localCertificate;
   protected PrivateKey _localPrivateKey;
   protected KeyStoreData _localKeyStoreData;
   protected DataInputStream _input;
   protected OutputStream _output;
   protected TLSByteArrayInputStream _readStream;
   protected TLSNoCopyByteArrayOutputStream _writeStream;
   protected SSLConnectionState _currentRead;
   protected SSLConnectionState _currentWrite;
   protected SSLConnectionState _pendingRead;
   protected SSLConnectionState _pendingWrite;
   protected AlertProtocolMethods _alertProtocol;
   protected SSLHandshakeProtocol _handshakeProtocol;
   protected ChangeCipherSpecProtocol _changeCipherSpecProtocol;
   private PacketLogger _packetLogger = PacketLogger.getInstance();
   private boolean _headerValid;
   private boolean _inRead;
   private boolean _closedByServer;
   public static final int LOCAL_VERSION = 768;
   private static final byte[] V2_ERROR = new byte[]{-128, 3, 0, 0, 1};
   protected static final boolean DEBUG = false;
   private static final int SEQUENCE_LENGTH = 8;
   protected static final int HEADER_LENGTH = 5;
   protected static final ResourceBundle _rb = ResourceBundle.getBundle(5710659227867441061L, "net.rim.device.internal.resource.crypto.SSL");

   protected SSLRecordProtocol() {
   }

   public SSLRecordProtocol(StreamConnection subConnection, String name) {
      this._subConnection = subConnection;
      this._connectionName = name;

      label20:
      try {
         this._connectionURL = (URL)(new Object(this._connectionName));
      } finally {
         break label20;
      }

      this._readStream = (TLSByteArrayInputStream)(new Object());
      this._writeStream = (TLSNoCopyByteArrayOutputStream)(new Object());
      this._currentRead = new SSLConnectionState(this._readStream);
      this._currentWrite = new SSLConnectionState(this._writeStream);
      this._pendingRead = new SSLConnectionState(this._readStream);
      this._pendingWrite = new SSLConnectionState(this._writeStream);
      this._alertProtocol = new SSLAlertProtocol(this);
      this._changeCipherSpecProtocol = (ChangeCipherSpecProtocol)(new Object(this));
      this._handshakeProtocol = new SSLHandshakeProtocol(this);
   }

   @Override
   public synchronized void connect() {
      if (this._input == null) {
         this._input = this._subConnection.openDataInputStream();
      }

      if (this._output == null) {
         this._output = this._subConnection.openOutputStream();
      }

      if (this._state != 1) {
         this._handshakeProtocol.connect();
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public int read(DataBuffer buffer) {
      boolean var31 = false /* VF: Semaphore variable */;
      boolean var41 = false /* VF: Semaphore variable */;

      label325: {
         int bytesRead;
         label326: {
            int var13;
            label327: {
               label328: {
                  try {
                     try {
                        label341: {
                           var41 = true;
                           var31 = true;
                           synchronized (this) {
                              this._inRead = true;
                           }

                           if (this._headerValid) {
                              this._headerValid = false;
                           } else {
                              bytesRead = -1;
                              if (!this._closedByServer) {
                                 bytesRead = this._input.read(this._readHeader);
                              }

                              if (bytesRead == -1) {
                                 throw new Object();
                              }
                           }

                           if (!this._clientMode && this._state == 2) {
                              this._state = 0;
                              if ((this._readHeader[0] & 128) != 0
                                 && this._readHeader[2] == 1
                                 && (this._readHeader[3] == 0 && this._readHeader[4] == 2 || this._readHeader[3] == 3)) {
                                 bytesRead = this.handleV2Message(this._readHeader, buffer);
                                 var31 = false;
                                 var41 = false;
                                 break label326;
                              }
                           }

                           bytesRead = this._readHeader[0];
                           this._remoteVersion = this._readHeader[1] << 8 | this._readHeader[2];
                           int length = (this._readHeader[3] & 255) << 8 | this._readHeader[4] & 255;
                           if (length > 18432) {
                              TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)22);
                           }

                           int macLength = this._currentRead.getHashSize();
                           MAC mac = this._currentRead.getMAC();
                           DecryptorInputStream decryptor = this.generateDecryptor(this._currentRead);
                           if (length < macLength) {
                              TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)50);
                           }

                           int dataLength = length - macLength;
                           byte[] data = new byte[dataLength + macLength];
                           this._input.read(data);
                           this._readStream.setData(data);
                           length = decryptor.read(data);
                           if (this._packetLogger._highLoggingEnabled) {
                              this._packetLogger.logPacket(data, 0, data.length, "SSL", false);
                           }

                           dataLength = length - macLength;
                           if (dataLength < 0) {
                              TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)50);
                           }

                           byte pos3 = this._readHeader[3];
                           byte pos4 = this._readHeader[4];
                           this._readHeader[3] = (byte)(dataLength >>> 8 & 0xFF);
                           this._readHeader[4] = (byte)(dataLength & 0xFF);
                           mac.reset();
                           this._sequence[0] = 0;
                           this._sequence[1] = 0;
                           this._sequence[2] = 0;
                           this._sequence[3] = 0;
                           this._sequence[4] = (byte)(super._readSequenceNumber >>> 24);
                           this._sequence[5] = (byte)(super._readSequenceNumber >>> 16);
                           this._sequence[6] = (byte)(super._readSequenceNumber >>> 8);
                           this._sequence[7] = (byte)(super._readSequenceNumber++);
                           mac.update(this._sequence);
                           this.macHeader(mac, this._readHeader, 0, this._readHeader.length);
                           mac.update(data, 0, dataLength);
                           this._readHeader[3] = pos3;
                           this._readHeader[4] = pos4;
                           if (!mac.checkMAC(data, dataLength)) {
                              TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)20);
                           }

                           buffer.setData(data, 0, dataLength);
                           DataBuffer newBuffer = null;
                           int newType = 0;
                           switch (bytesRead) {
                              case 19:
                                 break;
                              case 20:
                              default:
                                 this._changeCipherSpecProtocol.processChangeCipherSpecMessage(buffer);
                                 newBuffer = (DataBuffer)(new Object());
                                 newType = this.read(newBuffer);
                                 buffer.setData(newBuffer.getArray(), 0, newBuffer.getArrayLength());
                                 var13 = newType;
                                 var31 = false;
                                 var41 = false;
                                 break label341;
                              case 21:
                                 this._alertProtocol.processAlertMessage(buffer);
                                 break;
                              case 22:
                                 if (this._state == 1) {
                                    this._state = 0;
                                    this._handshakeProtocol = this.createNewHandshakeProtocol(this);
                                    this._handshakeProtocol.helloRequest(buffer);
                                    newBuffer = (DataBuffer)(new Object());
                                    newType = this.read(newBuffer);
                                    buffer.setData(newBuffer.getArray(), 0, newBuffer.getArrayLength());
                                    var13 = newType;
                                    var31 = false;
                                    var41 = false;
                                    break label327;
                                 }
                           }

                           var13 = bytesRead;
                           var31 = false;
                           var41 = false;
                           break label328;
                        }
                     } finally {
                        if (var41) {
                           TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)21);
                           var31 = false;
                           break label325;
                        }
                     }
                  } finally {
                     if (var31) {
                        synchronized (this) {
                           this._inRead = false;
                        }
                     }
                  }

                  synchronized (this) {
                     this._inRead = false;
                     return var13;
                  }
               }

               synchronized (this) {
                  this._inRead = false;
                  return var13;
               }
            }

            synchronized (this) {
               this._inRead = false;
               return var13;
            }
         }

         synchronized (this) {
            this._inRead = false;
            return bytesRead;
         }
      }

      synchronized (this) {
         this._inRead = false;
         return -1;
      }
   }

   public boolean peekForAlert() {
      try {
         synchronized (this) {
            if (this._inRead || this._headerValid || this._input == null) {
               return false;
            }
         }

         if (this._input.available() > 5) {
            int bytesRead = this._input.read(this._readHeader);
            if (bytesRead == -1) {
               return false;
            }

            this._headerValid = true;
            if (this._readHeader[0] == 21) {
               return true;
            }
         }
      } finally {
         return false;
      }

      return false;
   }

   private int handleV2Message(byte[] header, DataBuffer buffer) {
      int length = (header[0] & 127) << 8 & 0xFF | header[1] & 255;
      if (length < 0) {
         TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)40);
      }

      int type = header[2];
      if (type != 1) {
         TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)40);
      }

      this._remoteVersion = header[3] << 8 | header[4];
      if (this._remoteVersion < 768) {
         this.writeV2Error();
         throw new Object((Exception)(new Object((byte)3, (byte)40)));
      } else {
         byte[] data = new byte[length];
         System.arraycopy(header, 2, data, 0, 3);
         this._input.read(data, 3, length - 3);
         buffer.setData(data, 0, length);
         return 24;
      }
   }

   private DecryptorInputStream generateDecryptor(SSLConnectionState state) {
      if (state.getBulkCipherAlgorithm() == null) {
         return (DecryptorInputStream)(new Object(this._readStream));
      } else if (state.getCipherType() == 2) {
         BlockDecryptorEngine engine = state.getDecryptorEngine();
         return new SSLBlockDecryptor(engine, this._readStream, this._remoteVersion != 768);
      } else {
         return state.getDecryptor();
      }
   }

   @Override
   public void write(int type, DataBuffer buffer) {
      this.write(type, buffer.getArray(), buffer.getArrayPosition(), buffer.getArrayLength());
   }

   @Override
   public void write(int type, byte[] data, int offset, int length) {
      if (this._output != null) {
         if (data != null && offset >= 0 && length >= 0 && data.length - length >= offset) {
            try {
               if (length > 16384) {
                  throw new Object((byte)3, (byte)22);
               }

               int macLength = this._currentWrite.getHashSize();
               MAC mac = this._currentWrite.getMAC();
               EncryptorOutputStream encryptor = this.generateEncryptor(this._currentWrite);
               int totalLength = length + macLength;
               byte[] encodedData = new byte[13 + totalLength];
               int version = this._remoteVersion != 0 ? this._remoteVersion : this.getLocalVersion();
               encodedData[0] = 0;
               encodedData[1] = 0;
               encodedData[2] = 0;
               encodedData[3] = 0;
               encodedData[4] = (byte)(super._writeSequenceNumber >>> 24);
               encodedData[5] = (byte)(super._writeSequenceNumber >>> 16);
               encodedData[6] = (byte)(super._writeSequenceNumber >>> 8);
               encodedData[7] = (byte)(super._writeSequenceNumber++);
               encodedData[8] = (byte)type;
               encodedData[9] = (byte)(version >>> 8);
               encodedData[10] = (byte)version;
               encodedData[11] = (byte)(length >>> 8);
               encodedData[12] = (byte)length;
               if (this._packetLogger._highLoggingEnabled) {
                  this._packetLogger.logPacket(data, 0, length, "SSL", true);
               }

               System.arraycopy(data, offset, encodedData, 13, length);
               mac.reset();
               mac.update(encodedData, 0, 8);
               this.macHeader(mac, encodedData, 8, 5);
               mac.update(encodedData, 13, length);
               mac.getMAC(encodedData, 13 + length);
               if (macLength > 0) {
                  encodedData[11] = (byte)(totalLength >>> 8);
                  encodedData[12] = (byte)totalLength;
               }

               encryptor.write(encodedData, 13, totalLength);
               encryptor.close();
               byte[] ciphertext = this._writeStream.getByteArray();
               int cipherLength = this._writeStream.size();
               if (cipherLength != totalLength) {
                  encodedData[11] = (byte)(cipherLength >>> 8);
                  encodedData[12] = (byte)cipherLength;
               }

               if (this._output instanceof Object) {
                  ((SocketOutputStream)this._output).setAutoFlushMode(false);
               }

               this._output.write(encodedData, 8, 5);
               this._output.write(ciphertext, 0, cipherLength);
               this._writeStream.reset();
            } finally {
               TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)21);
               return;
            }
         } else {
            throw new Object();
         }
      }
   }

   private void writeV2Error() {
      this._output.write(V2_ERROR);
   }

   @Override
   public void flush() {
      if (this._output != null) {
         this._output.flush();
      }
   }

   private EncryptorOutputStream generateEncryptor(SSLConnectionState state) {
      if (this._currentWrite.getBulkCipherAlgorithm() == null) {
         return (EncryptorOutputStream)(new Object(this._writeStream));
      } else if (this._currentWrite.getCipherType() == 2) {
         BlockEncryptorEngine engine = this._currentWrite.getEncryptorEngine();
         return (EncryptorOutputStream)(new Object((BlockFormatterEngine)(new Object(engine)), this._writeStream));
      } else {
         return this._currentWrite.getEncryptor();
      }
   }

   public void macHeader(MAC mac, byte[] data, int offset, int length) {
      mac.update(data, offset, 1);
      mac.update(data, offset + 3, 2);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void close() {
      try {
         label64:
         try {
            if (this._output != null) {
               this._output.flush();
               this._output.close();
            }
         } finally {
            break label64;
         }

         label61:
         try {
            if (this._input != null) {
               this._input.close();
            }
         } finally {
            break label61;
         }

         this._subConnection.close();
      } catch (Throwable var13) {
         throw new Object(e);
      }
   }

   @Override
   public void closeInput() {
      try {
         this._input.close();
      } finally {
         TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)40);
         return;
      }
   }

   public void closeInput(boolean closedByServer) {
      this._closedByServer = closedByServer;
      this.closeInput();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void closeOutput() {
      if (this._output != null) {
         try {
            this._output.flush();
            this._output.close();
         } catch (Throwable var3) {
            throw new Object(e);
         }
      }
   }

   @Override
   public byte getChangeCipherSpecConstant() {
      return 20;
   }

   @Override
   public byte getApplicationProtocolConstant() {
      return 23;
   }

   @Override
   public void changeCipherSpec() {
      this._currentRead = this._pendingRead;
      this._pendingRead = new SSLConnectionState(this._readStream);
      super._readSequenceNumber = 0;
   }

   public void changeWriteCipherSpec() {
      this._currentWrite = this._pendingWrite;
      this._pendingWrite = new SSLConnectionState(this._writeStream);
      super._writeSequenceNumber = 0;
   }

   @Override
   public InputStream getInputStream() {
      throw new Object();
   }

   @Override
   public OutputStream getOutputStream() {
      throw new Object();
   }

   public InputStream getUnderlyingInputStream() {
      return this._readStream;
   }

   public OutputStream getUnderlyingOutputStream() {
      return this._writeStream;
   }

   @Override
   public AlertProtocolMethods getAlertProtocol() {
      return this._alertProtocol;
   }

   @Override
   public ChangeCipherSpecProtocol getChangeCipherSpecProtocol() {
      return this._changeCipherSpecProtocol;
   }

   public SSLConnectionState getPendingWrite() {
      return this._pendingWrite;
   }

   public SSLConnectionState getPendingRead() {
      return this._pendingRead;
   }

   public void setPendingRead(SSLConnectionState pendingRead) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public void setPendingWrite(SSLConnectionState pendingWrite) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public int getRemoteVersion() {
      return this._remoteVersion;
   }

   public void setRemoteVersion(int remoteVersion) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public String getDomainName() {
      return this._connectionName;
   }

   public String getRemoteHostName() {
      return this._connectionURL != null ? this._connectionURL.getHost() : null;
   }

   public String getProtocol() {
      return "SSL";
   }

   public int getLocalVersion() {
      return 768;
   }

   public SSLHandshakeProtocol createNewHandshakeProtocol(SSLRecordProtocol recordProtocol) {
      return new SSLHandshakeProtocol(recordProtocol);
   }

   public void setState(int state) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public void setClientMode(boolean mode) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public boolean getClientMode() {
      return this._clientMode;
   }

   public void setLocalCertificateAndKey(Certificate cert, PrivateKey key) {
      this._localCertificate = cert;
      this._localPrivateKey = key;
      this._localKeyStoreData = null;
   }

   public void setLocalKeyStoreData(KeyStoreData keyStoreData) {
      this._localKeyStoreData = keyStoreData;
      this._localCertificate = null;
      this._localPrivateKey = null;
   }

   public Certificate getLocalCertificate() {
      if (this._localCertificate == null && this._localKeyStoreData != null) {
         this._localCertificate = this._localKeyStoreData.getCertificate();
      }

      return this._localCertificate;
   }

   public PrivateKey getLocalPrivateKey() {
      if (this._localPrivateKey == null && this._localKeyStoreData != null) {
         try {
            this._localPrivateKey = this._localKeyStoreData.getPrivateKey(null);
         } finally {
            return this._localPrivateKey;
         }
      }

      return this._localPrivateKey;
   }

   public SecurityInfo getSecurityInfo() {
      return new TLSSecurityInfo(this._certificate, this._remoteVersion, this._cipherSuite, this._keyExchangeSize);
   }

   void saveCertificate(Certificate certificate) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   void saveCertificatePool(Certificate[] certificatePool) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   void setCipherSuite(int cipherSuite) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   void setKeyExchangeSize(int keyExchangeSize) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public Certificate getRIMServerCertificate() {
      return this._certificate;
   }

   public Certificate[] getCertificatePool() {
      return this._certificatePool;
   }

   public void setOverrideConnectionOptions(SSLConnectionOptions connectionOptions) {
      this._handshakeProtocol.setOverrideConnectionOptions(connectionOptions);
   }
}
