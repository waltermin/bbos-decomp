package net.rim.device.api.crypto.tls.wtls20;

import java.io.InputStream;
import java.io.OutputStream;
import net.rim.device.api.crypto.BlockDecryptorEngine;
import net.rim.device.api.crypto.BlockEncryptorEngine;
import net.rim.device.api.crypto.MAC;
import net.rim.device.api.crypto.NullDecryptor;
import net.rim.device.api.crypto.NullEncryptor;
import net.rim.device.api.crypto.NullMAC;
import net.rim.device.api.crypto.tls.ConnectionState;

final class WTLSConnectionState extends ConnectionState {
   private BlockDecryptorEngine _blockDecryptor;
   private BlockEncryptorEngine _blockEncryptor;
   private byte[] _serverIV;
   private byte[] _clientIV;
   private byte _keyAlgorithmParameters;
   private MAC _clientMAC;
   private int _macKeySize;

   public WTLSConnectionState() {
      super._cipherType = 0;
      super._isExportable = true;
      super._keyExchangeAlgorithm = 2;
      super._mac = new NullMAC();
      this._clientMAC = new NullMAC();
   }

   public WTLSConnectionState(InputStream inputStream, OutputStream outputStream) {
      this();
      if (outputStream != null) {
         super._encryption = new NullEncryptor(outputStream);
      }

      if (inputStream != null) {
         super._decryption = new NullDecryptor(inputStream);
      }
   }

   public WTLSConnectionState(InputStream inputStream) {
      this(inputStream, null);
   }

   public WTLSConnectionState(OutputStream outputStream) {
      this(null, outputStream);
   }

   public final BlockDecryptorEngine getBlockDecryptor() {
      return this._blockDecryptor;
   }

   public final void setBlockDecryptor(BlockDecryptorEngine engine) {
      this._blockDecryptor = engine;
   }

   public final BlockEncryptorEngine getBlockEncryptor() {
      return this._blockEncryptor;
   }

   public final void setBlockEncryptor(BlockEncryptorEngine engine) {
      this._blockEncryptor = engine;
   }

   public final byte[] getClientIV() {
      return this._clientIV;
   }

   public final void setClientIV(byte[] iv) {
      this._clientIV = iv;
   }

   public final byte[] getServerIV() {
      return this._serverIV;
   }

   public final void setServerIV(byte[] iv) {
      this._serverIV = iv;
   }

   public final byte getKeyExchangeParameters() {
      return this._keyAlgorithmParameters;
   }

   public final void setKeyExchangeParameters(byte value) {
      this._keyAlgorithmParameters = value;
   }

   public final MAC getClientMAC() {
      return this._clientMAC;
   }

   public final MAC getServerMAC() {
      return super._mac;
   }

   public final void setServerMAC(MAC mac) {
      super._mac = mac;
   }

   public final void setClientMAC(MAC mac) {
      this._clientMAC = mac;
   }

   public final void setMACKeySize(int size) {
      this._macKeySize = size;
   }

   public final int getMACKeySize() {
      return this._macKeySize;
   }
}
