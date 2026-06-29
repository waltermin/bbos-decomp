package net.rim.device.api.crypto.tls.wtls20;

import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.SecurityInfo;
import net.rim.device.api.crypto.BlockDecryptorEngine;
import net.rim.device.api.crypto.BlockEncryptorEngine;
import net.rim.device.api.crypto.CBCDecryptorEngine;
import net.rim.device.api.crypto.CBCEncryptorEngine;
import net.rim.device.api.crypto.EncryptorOutputStream;
import net.rim.device.api.crypto.HMACKey;
import net.rim.device.api.crypto.InitializationVector;
import net.rim.device.api.crypto.MAC;
import net.rim.device.api.crypto.SymmetricKey;
import net.rim.device.api.crypto.SymmetricKeyFactory;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.tls.AlertProtocolMethods;
import net.rim.device.api.crypto.tls.ChangeCipherSpecProtocol;
import net.rim.device.api.crypto.tls.RecordProtocol;
import net.rim.device.api.crypto.tls.TLSAlertException;
import net.rim.device.api.crypto.tls.TLSBlockUnformatterEngine;
import net.rim.device.api.crypto.tls.TLSByteArrayInputStream;
import net.rim.device.api.crypto.tls.TLSInputStream;
import net.rim.device.api.crypto.tls.TLSNoCopyByteArrayOutputStream;
import net.rim.device.api.crypto.tls.TLSOutputStream;
import net.rim.device.api.crypto.tls.TLSUtilities;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;

final class WTLSRecordProtocol extends RecordProtocol {
   private WTLSAlertProtocol _alertProtocol;
   private ChangeCipherSpecProtocol _changeCipherSpecProtocol;
   private DataBuffer _lastReadCipherTextMessage;
   private DataBuffer _lastWrittenCipherTextMessage = new DataBuffer();
   private DataBuffer _pendingData = new DataBuffer();
   private byte[] _lastDataSent = new byte[1492];
   private int _lastDataSentSize;
   private int[] _receivedWindow = new int[32];
   private TLSNoCopyByteArrayOutputStream _cipherOut;
   private TLSByteArrayInputStream _cipherIn;
   private byte[] _tempReadHeader = new byte[5];
   private boolean _autoflush = true;
   private int _keyAgreementSize;
   private boolean _piggyBack;
   private DataBuffer _receivedBuffer = new DataBuffer();
   protected WTLSConnectionState _currentRead;
   protected WTLSConnectionState _currentWrite;
   protected WTLSConnectionState _pendingState;
   private int _cipherSuite;
   private Certificate _certificate;
   private int _keyRefresh;
   private WTLSDataTransport _udpLayer;
   private String _name;
   private int _hostIP;
   private int _hostPort;
   private byte[] _openwaveSignatureData;
   private static final int MAX_PACKET_SIZE = 1400;
   private static final int MAX_WTLS_DATAGRAM = 1492;
   private static final int _timeout = 5000;
   private static final long EVENT_LOGGER_GUID = -6885353762573497192L;
   private static final int EVENT_LOG_UPDATE_KEY = 1465021803;
   private static final int EVENT_LOG_SEQUENCE_TOO_LARGE = 1467184236;
   private static final int EVENT_LOG_NO_SEQUENCE_NO = 1466856302;
   private static final int EVENT_LOG_MAC_TOO_SHORT = 1466791027;
   private static final int EVENT_LOG_NO_CIPHER = 1465020003;
   private static final int EVENT_LOG_BAD_MAC = 1465016941;
   private static final int EVENT_LOG_CRYPTO_EXCEPTION = 1465017189;
   private static final boolean DEBUG = false;
   private static final byte[] CLIENT_EXPANSION = new byte[]{99, 108, 105, 101, 110, 116, 32, 101, 120, 112, 97, 110, 115, 105, 111, 110};
   private static final byte[] SERVER_EXPANSION = new byte[]{115, 101, 114, 118, 101, 114, 32, 101, 120, 112, 97, 110, 115, 105, 111, 110};
   private static final byte[] CLIENT_WRITE_KEY = new byte[]{99, 108, 105, 101, 110, 116, 32, 119, 114, 105, 116, 101, 32, 107, 101, 121};
   private static final byte[] CLIENT_WRITE_IV = new byte[]{99, 108, 105, 101, 110, 116, 32, 119, 114, 105, 116, 101, 32, 73, 86};
   private static final byte[] SERVER_WRITE_KEY = new byte[]{115, 101, 114, 118, 101, 114, 32, 119, 114, 105, 116, 101, 32, 107, 101, 121};
   private static final byte[] SERVER_WRITE_IV = new byte[]{115, 101, 114, 118, 101, 114, 32, 119, 114, 105, 116, 101, 32, 73, 86};
   protected static final int LOCAL_VERSION = 1;
   private static final int WINDOW_SIZE = 32;
   private static final int HEADER_SIZE = 5;

   public WTLSRecordProtocol(
      WTLSDataTransport subConnection, String apn, String name, int flags, int clientIdType, String clientIdValue, int ipAddress, int port
   ) {
      this._udpLayer = subConnection;
      this._name = name;
      this._hostIP = ipAddress;
      this._hostPort = port;
      this._alertProtocol = new WTLSAlertProtocol(this);
      this._changeCipherSpecProtocol = new ChangeCipherSpecProtocol(this);
      this._cipherOut = new TLSNoCopyByteArrayOutputStream();
      this._cipherIn = new TLSByteArrayInputStream();
      this._currentRead = new WTLSConnectionState(this._cipherIn);
      this._currentWrite = new WTLSConnectionState(this._cipherOut);
      this._pendingState = new WTLSConnectionState(this._cipherIn, this._cipherOut);

      for (int i = 0; i < 32; i++) {
         this._receivedWindow[i] = 65536;
      }

      WTLSHandshakeProtocol handshakeProtocol = new WTLSHandshakeProtocol(this, apn, flags, clientIdType, clientIdValue);
      handshakeProtocol.connect();
   }

   @Override
   public final void connect() {
   }

   @Override
   public final int read(DataBuffer buffer) {
      return this.read(buffer, false);
   }

   public final int read(DataBuffer buffer, boolean timeout) {
      try {
         while (true) {
            if (this._receivedBuffer.getLength() == this._receivedBuffer.getPosition()) {
               byte[] readData = this._udpLayer.read(timeout ? 5000 : 0);
               this._receivedBuffer.setData(readData, 0, readData.length);
            }

            buffer.reset();
            int type = this._receivedBuffer.readByte() & 255;
            int returnType = type & 15;
            int length = this._receivedBuffer.available();
            int packetSequenceNum = 0;
            if ((type & 64) != 0) {
               packetSequenceNum = this._receivedBuffer.readUnsignedShort();
            }

            if ((type & 128) != 0) {
               length = this._receivedBuffer.readUnsignedShort();
            } else {
               length = this._receivedBuffer.available();
            }

            if (returnType != 2) {
               if ((type & 64) == 0) {
                  EventLogger.logEvent(-6885353762573497192L, 1466856302, 5);
                  this._receivedBuffer.skipBytes(length);
                  TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)50);
               } else {
                  if (packetSequenceNum < super._readSequenceNumber - 32) {
                     this._receivedBuffer.skipBytes(length);
                     continue;
                  }

                  if (this._receivedWindow[packetSequenceNum & 31] == packetSequenceNum) {
                     this._receivedBuffer.skipBytes(length);
                     continue;
                  }

                  this._receivedWindow[packetSequenceNum & 31] = packetSequenceNum;
                  if (this._keyRefresh != 0
                     && (
                        super._readSequenceNumber / this._keyRefresh * this._keyRefresh != packetSequenceNum / this._keyRefresh * this._keyRefresh
                           || packetSequenceNum % this._keyRefresh == 0
                     )) {
                     EventLogger.logEvent(-6885353762573497192L, 1465021803, 5);
                     super._readSequenceNumber = packetSequenceNum;
                     this.updateReadKeyMaterial(this._currentRead);
                  }

                  super._readSequenceNumber = packetSequenceNum + 1;
                  if (super._readSequenceNumber > 65536) {
                     EventLogger.logEvent(-6885353762573497192L, 1467184236, 5);
                     this._receivedBuffer.skipBytes(length);
                     TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)50);
                  }
               }
            }

            if ((type & 32) == 0) {
               byte[] payloadData = new byte[Math.min(length, this._receivedBuffer.available())];
               this._receivedBuffer.read(payloadData);
               if (returnType == 1) {
                  DataBuffer tempBuffer = new DataBuffer(payloadData, 0, payloadData.length, true);
                  this._changeCipherSpecProtocol.processChangeCipherSpecMessage(tempBuffer);
                  continue;
               }

               if (returnType == 2) {
                  DataBuffer tempBuffer = new DataBuffer(payloadData, 0, payloadData.length, true);

                  try {
                     this._alertProtocol.processAlertMessage(tempBuffer);
                     continue;
                  } catch (TLSAlertException tlsAlert) {
                     if (tlsAlert.getAlertLevel() == 1 && tlsAlert.getAlertDescription() == 57) {
                        this._pendingData.reset();
                        continue;
                     }

                     throw tlsAlert;
                  }
               }

               buffer.write(payloadData);
               this._lastReadCipherTextMessage = buffer;
            } else {
               int macLength = this._currentRead.getHashSize();
               MAC mac = this._currentRead.getServerMAC();
               TLSBlockUnformatterEngine cipher = null;
               if (length < macLength) {
                  EventLogger.logEvent(-6885353762573497192L, 1466791027, 5);
                  TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)22);
               }

               String bulkAlgorithm = this._currentRead.getBulkCipherAlgorithm();
               if (bulkAlgorithm != null) {
                  byte[] serverIV = this._currentRead.getServerIV();
                  byte[] tempIV = new byte[serverIV.length];

                  for (int i = 0; i < tempIV.length; i += 2) {
                     tempIV[i] = (byte)(serverIV[i] ^ packetSequenceNum >> 8);
                     tempIV[i + 1] = (byte)(serverIV[i + 1] ^ packetSequenceNum);
                  }

                  BlockDecryptorEngine engine = this._currentRead.getBlockDecryptor();
                  if (engine instanceof CBCDecryptorEngine) {
                     ((CBCDecryptorEngine)engine).setIV(new InitializationVector(tempIV));
                     cipher = new TLSBlockUnformatterEngine(engine);
                  }
               }

               if (cipher == null) {
                  EventLogger.logEvent(-6885353762573497192L, 1465020003, 5);
                  TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)47);
               }

               DataBuffer temp = new DataBuffer(this._receivedBuffer.getArray(), this._receivedBuffer.getArrayPosition(), length, true);
               this._receivedBuffer.skipBytes(length);
               DataBuffer outData = new DataBuffer();
               int dataLength = cipher.decryptAndUnformat(temp, outData);
               byte[] data = outData.getArray();
               dataLength -= macLength;
               mac.reset();
               this._tempReadHeader[0] = (byte)(packetSequenceNum >> 8);
               this._tempReadHeader[1] = (byte)packetSequenceNum;
               this._tempReadHeader[2] = (byte)type;
               this._tempReadHeader[3] = (byte)(dataLength >> 8);
               this._tempReadHeader[4] = (byte)dataLength;
               mac.update(this._tempReadHeader, 0, 5);
               mac.update(data, 0, dataLength);
               if (!Arrays.equals(data, dataLength, mac.getMAC(), 0, macLength)) {
                  EventLogger.logEvent(-6885353762573497192L, 1465016941, 5);
                  TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)20);
               }

               this._pendingData.reset();
               buffer.write(data, 0, dataLength);
               this._lastReadCipherTextMessage = buffer;
            }

            buffer.rewind();
            return returnType;
         }
      } finally {
         EventLogger.logEvent(-6885353762573497192L, 1465017189, 5);
         TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)51);
         return -1;
      }
   }

   @Override
   public final void write(int type, DataBuffer buffer) {
      this.write(type, buffer.getArray(), buffer.getArrayPosition(), buffer.available());
   }

   @Override
   public final void write(int type, byte[] data, int offset, int length) {
      if (data != null && offset >= 0 && length >= 0 && data.length - length >= offset) {
         try {
            if (this._keyRefresh != 0 && super._writeSequenceNumber % this._keyRefresh == 0) {
               this.updateWriteKeyMaterial(this._currentWrite);
            }

            if (type != 1 && type != 2) {
               int headerLength = 0;
               int macLength = this._currentWrite.getHashSize();
               MAC mac = this._currentWrite.getClientMAC();
               EncryptorOutputStream cipher = this._currentWrite.getEncryptor();
               String bulkAlgorithm = this._currentWrite.getBulkCipherAlgorithm();
               if (bulkAlgorithm != null) {
                  byte[] clientIV = this._currentWrite.getClientIV();
                  byte[] tempIV = new byte[clientIV.length];

                  for (int i = 0; i < tempIV.length; i += 2) {
                     tempIV[i] = (byte)(clientIV[i] ^ super._writeSequenceNumber >> 8);
                     tempIV[i + 1] = (byte)(clientIV[i + 1] ^ super._writeSequenceNumber);
                  }

                  BlockEncryptorEngine engine = this._currentWrite.getBlockEncryptor();
                  if (engine instanceof CBCEncryptorEngine) {
                     ((CBCEncryptorEngine)engine).setIV(new InitializationVector(tempIV));
                  }
               }

               int payloadLength = length + macLength;
               int fields = 192;
               if (this._currentWrite.getBulkCipherAlgorithm() != null) {
                  fields |= 32;
               }

               mac.reset();
               byte[] macData = new byte[]{
                  (byte)(super._writeSequenceNumber >> 8), (byte)super._writeSequenceNumber, (byte)(type | fields), (byte)(length >> 8), (byte)length
               };
               mac.update(macData, 0, 5);
               mac.update(data, 0, length);
               byte[] macBytes = mac.getMAC();
               this._cipherOut.reset();
               cipher.write(data, 0, length);
               cipher.write(macBytes, 0, macLength);
               cipher.flush(true);
               byte[] cipherData = this._cipherOut.getByteArray();
               int cipherLength = this._cipherOut.size();
               byte[] encryptedData = new byte[5 + cipherLength];
               encryptedData[0] = (byte)(type | fields);
               encryptedData[1] = (byte)(super._writeSequenceNumber >> 8);
               encryptedData[2] = (byte)super._writeSequenceNumber;
               encryptedData[3] = (byte)(cipherLength >> 8);
               encryptedData[4] = (byte)cipherLength;
               System.arraycopy(cipherData, 0, encryptedData, 5, cipherLength);
               super._writeSequenceNumber++;
               if ((super._writeSequenceNumber & 65536) != 0) {
                  super._writeSequenceNumber = 0;
               }

               this._lastWrittenCipherTextMessage.reset();
               this._lastWrittenCipherTextMessage.setData(encryptedData, 0, encryptedData.length);
               if (this._autoflush && !this._piggyBack) {
                  if (this._pendingData.getArrayLength() > 0) {
                     if (this._pendingData.getArrayLength() + encryptedData.length > this._lastDataSent.length) {
                        TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)22);
                     }

                     System.arraycopy(this._pendingData.getArray(), 0, this._lastDataSent, 0, this._pendingData.getArrayLength());
                     this._lastDataSentSize = this._pendingData.getArrayLength();
                     System.arraycopy(encryptedData, 0, this._lastDataSent, this._lastDataSentSize, encryptedData.length);
                     this._lastDataSentSize += encryptedData.length;
                  } else {
                     if (encryptedData.length > this._lastDataSent.length) {
                        TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)22);
                     }

                     System.arraycopy(encryptedData, 0, this._lastDataSent, 0, encryptedData.length);
                     this._lastDataSentSize = encryptedData.length;
                  }

                  this._udpLayer.write(this._lastDataSent, 0, this._lastDataSentSize);
               } else {
                  this._pendingData.write(encryptedData);
               }
            } else {
               byte[] outData = new byte[5 + length];
               outData[0] = (byte)(type | 192);
               outData[1] = (byte)(super._writeSequenceNumber >> 8);
               outData[2] = (byte)super._writeSequenceNumber;
               outData[3] = (byte)(length >> 8);
               outData[4] = (byte)length;
               System.arraycopy(data, offset, outData, 5, length);
               super._writeSequenceNumber++;
               if ((super._writeSequenceNumber & 65536) != 0) {
                  super._writeSequenceNumber = 0;
               }

               this._lastWrittenCipherTextMessage.reset();
               this._lastWrittenCipherTextMessage.setData(outData, 0, outData.length);
               if ((!this._autoflush || this._piggyBack) && type != 2) {
                  this._pendingData.write(outData);
               } else {
                  if (outData.length > this._lastDataSent.length) {
                     TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)22);
                  }

                  System.arraycopy(outData, 0, this._lastDataSent, 0, outData.length);
                  this._lastDataSentSize = outData.length;
                  this._udpLayer.write(outData, 0, outData.length);
               }
            }
         } finally {
            TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)51);
            return;
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public final void setAutoflush(boolean value) {
      this._autoflush = value;
      if (this._autoflush) {
         try {
            this.flush();
         } finally {
            return;
         }
      }
   }

   @Override
   public final void flush() {
      if (this._pendingData.getArrayLength() > 0) {
         this._udpLayer.write(this._pendingData.getArray(), 0, this._pendingData.getArrayLength());
         System.arraycopy(this._pendingData.getArray(), 0, this._lastDataSent, 0, this._pendingData.getArrayLength());
         this._lastDataSentSize = this._pendingData.getArrayLength();
         this._pendingData.reset();
      }
   }

   public final void retransmit() {
      this._udpLayer.write(this._lastDataSent, 0, this._lastDataSentSize);
   }

   @Override
   public final void close() {
   }

   @Override
   public final void closeInput() {
   }

   @Override
   public final void closeOutput() {
   }

   public final DataBuffer getLastReadCipherTextMessage() {
      return this._lastReadCipherTextMessage;
   }

   public final DataBuffer getLastWrittenCipherTextMessage() {
      return this._lastWrittenCipherTextMessage;
   }

   @Override
   public final InputStream getInputStream() {
      return new TLSInputStream(this);
   }

   @Override
   public final OutputStream getOutputStream() {
      return new TLSOutputStream(this);
   }

   @Override
   public final byte getChangeCipherSpecConstant() {
      return 1;
   }

   @Override
   public final byte getApplicationProtocolConstant() {
      return 4;
   }

   @Override
   public final AlertProtocolMethods getAlertProtocol() {
      return this._alertProtocol;
   }

   @Override
   public final ChangeCipherSpecProtocol getChangeCipherSpecProtocol() {
      return this._changeCipherSpecProtocol;
   }

   @Override
   public final void changeCipherSpec() {
      this._currentRead = this._pendingState;
      super._readSequenceNumber = 0;

      for (int i = 0; i < 32; i++) {
         this._receivedWindow[i] = 65536;
      }

      this.updateReadKeyMaterial(this._currentRead);
   }

   public final void changeWriteCipherSpec() {
      this._currentWrite = this._pendingState;
      super._writeSequenceNumber = 0;
      this.updateWriteKeyMaterial(this._currentWrite);
   }

   public final WTLSConnectionState getPendingState() {
      return this._pendingState;
   }

   public final void setPendingState(WTLSConnectionState pending) {
      this._pendingState = pending;
   }

   public final String getProtocol() {
      return "WTLS";
   }

   public final InputStream getUnderlyingInputStream() {
      return this._cipherIn;
   }

   public final OutputStream getUnderlyingOutputStream() {
      return this._cipherOut;
   }

   final void setKeyRefresh(int refresh) {
      this._keyRefresh = refresh;
   }

   final void setCipherSuite(int cipherSuite) {
      this._cipherSuite = cipherSuite;
   }

   final void setCertificate(Certificate certificate) {
      this._certificate = certificate;
   }

   public final void updateWriteKeyMaterial(WTLSConnectionState write) {
      try {
         int seqNum = super._writeSequenceNumber - super._writeSequenceNumber % this._keyRefresh;
         byte[] random = new byte[2 + super._serverRandom.length + super._clientRandom.length];
         random[0] = (byte)(seqNum >> 8);
         random[1] = (byte)seqNum;
         System.arraycopy(super._serverRandom, 0, random, 2, super._serverRandom.length);
         System.arraycopy(super._clientRandom, 0, random, 2 + super._serverRandom.length, super._clientRandom.length);
         WTLSPRF prf = new WTLSPRF(this._cipherSuite & 0xFF, super._masterSecret, CLIENT_EXPANSION, random);
         HMACKey writeHMACKey = new HMACKey(prf.getBytes(write.getMACKeySize()));
         int keyLength = write.getKeySize();
         SymmetricKey writeKey = null;
         byte[] keyBytes = null;
         if (keyLength > 0) {
            keyBytes = prf.getBytes(keyLength);
         }

         InitializationVector writeIV = null;
         if (write.getCipherType() == 2) {
            writeIV = new InitializationVector(prf.getBytes(write.getIVSize()));
         }

         this._openwaveSignatureData = prf.getBytes(12);
         WTLSPRF exportIV = null;
         byte[] exportRandom = null;
         if (!write.getIsExportable()) {
            writeKey = SymmetricKeyFactory.getInstance(write.getBulkCipherAlgorithm() + "_" + (keyLength << 3), keyBytes, 0, keyLength);
         } else {
            exportRandom = new byte[super._clientRandom.length + super._serverRandom.length];
            System.arraycopy(super._clientRandom, 0, exportRandom, 0, super._clientRandom.length);
            System.arraycopy(super._serverRandom, 0, exportRandom, super._clientRandom.length, super._serverRandom.length);
            keyLength = write.getKeyMaterialLength();
            if (keyLength > 0) {
               WTLSPRF writeExportPRF = new WTLSPRF(this._cipherSuite & 0xFF, keyBytes, CLIENT_WRITE_KEY, exportRandom);
               writeKey = SymmetricKeyFactory.getInstance(
                  write.getBulkCipherAlgorithm() + "_" + (keyLength << 3), writeExportPRF.getBytes(keyLength), 0, keyLength
               );
            }

            if (write.getCipherType() == 2) {
               byte[] ivExportRandom = new byte[2 + super._clientRandom.length + super._serverRandom.length];
               ivExportRandom[0] = (byte)(seqNum >> 8);
               ivExportRandom[1] = (byte)seqNum;
               System.arraycopy(super._clientRandom, 0, ivExportRandom, 2, super._clientRandom.length);
               System.arraycopy(super._serverRandom, 0, ivExportRandom, 2 + super._clientRandom.length, super._serverRandom.length);
               exportIV = new WTLSPRF(this._cipherSuite & 0xFF, null, CLIENT_WRITE_IV, ivExportRandom);
               writeIV = new InitializationVector(exportIV.getBytes(write.getIVSize()));
            }
         }

         WTLSCipherSuiteFactory.updateWriteConnectionState(write, writeKey, writeHMACKey, writeIV, this._cipherOut);
      } finally {
         TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)51);
         return;
      }
   }

   public final void updateReadKeyMaterial(WTLSConnectionState read) {
      try {
         int seqNum = super._readSequenceNumber - super._readSequenceNumber % this._keyRefresh;
         byte[] random = new byte[2 + super._serverRandom.length + super._clientRandom.length];
         random[0] = (byte)(seqNum >> 8);
         random[1] = (byte)seqNum;
         System.arraycopy(super._serverRandom, 0, random, 2, super._serverRandom.length);
         System.arraycopy(super._clientRandom, 0, random, 2 + super._serverRandom.length, super._clientRandom.length);
         WTLSPRF prf = new WTLSPRF(this._cipherSuite & 0xFF, super._masterSecret, SERVER_EXPANSION, random);
         HMACKey readHMACKey = new HMACKey(prf.getBytes(read.getMACKeySize()));
         int keyLength = read.getKeySize();
         SymmetricKey readKey = null;
         byte[] keyBytes = null;
         if (keyLength > 0) {
            keyBytes = prf.getBytes(keyLength);
         }

         InitializationVector readIV = null;
         if (read.getCipherType() == 2) {
            readIV = new InitializationVector(prf.getBytes(read.getIVSize()));
         }

         WTLSPRF exportIV = null;
         byte[] exportRandom = null;
         if (!read.getIsExportable()) {
            readKey = SymmetricKeyFactory.getInstance(read.getBulkCipherAlgorithm() + "_" + (keyLength << 3), keyBytes, 0, keyLength);
         } else {
            exportRandom = new byte[super._clientRandom.length + super._serverRandom.length];
            System.arraycopy(super._clientRandom, 0, exportRandom, 0, super._clientRandom.length);
            System.arraycopy(super._serverRandom, 0, exportRandom, super._clientRandom.length, super._serverRandom.length);
            keyLength = read.getKeyMaterialLength();
            if (keyLength > 0) {
               WTLSPRF readExportPRF = new WTLSPRF(this._cipherSuite & 0xFF, keyBytes, SERVER_WRITE_KEY, exportRandom);
               readKey = SymmetricKeyFactory.getInstance(
                  read.getBulkCipherAlgorithm() + "_" + (keyLength << 3), readExportPRF.getBytes(keyLength), 0, keyLength
               );
            }

            if (read.getCipherType() == 2) {
               byte[] ivExportRandom = new byte[2 + super._clientRandom.length + super._serverRandom.length];
               ivExportRandom[0] = (byte)(seqNum >> 8);
               ivExportRandom[1] = (byte)seqNum;
               System.arraycopy(super._clientRandom, 0, ivExportRandom, 2, super._clientRandom.length);
               System.arraycopy(super._serverRandom, 0, ivExportRandom, 2 + super._clientRandom.length, super._serverRandom.length);
               exportIV = new WTLSPRF(this._cipherSuite & 0xFF, null, SERVER_WRITE_IV, ivExportRandom);
               readIV = new InitializationVector(exportIV.getBytes(read.getIVSize()));
            }
         }

         WTLSCipherSuiteFactory.updateReadConnectionState(read, readKey, readHMACKey, readIV, this._cipherIn);
      } finally {
         TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)51);
         return;
      }
   }

   final void updateStatus(int status) {
      this._udpLayer.status(status);
   }

   public final void setPiggybackMode(boolean value) {
      this._piggyBack = value;
   }

   public final int getMaxPacketSize() {
      return 1400;
   }

   public final Certificate getRIMServerCertificate() {
      return this._certificate;
   }

   public final SecurityInfo getSecurityInfo() {
      return new WTLSSecurityInfo(
         this._certificate, this._currentWrite.getKeyExchangeAlgorithm(), this._cipherSuite, this._keyAgreementSize, this._currentWrite.getKeySize() * 8
      );
   }

   public final void setKeyAgreementSize(int keyAgreementSize) {
      this._keyAgreementSize = keyAgreementSize;
   }

   public final int getIPAddress() {
      return this._hostIP;
   }

   public final int getPort() {
      return this._hostPort;
   }

   public final String getDomainName() {
      return this._name;
   }

   public final byte[] getLastOpenwaveSignature() {
      return this._openwaveSignatureData;
   }

   static {
      EventLogger.register(-6885353762573497192L, "net.rim.wtls", 2);
   }
}
