package net.rim.device.api.crypto.tls;

import net.rim.device.api.crypto.DecryptorInputStream;
import net.rim.device.api.crypto.EncryptorOutputStream;
import net.rim.device.api.crypto.MAC;

public class ConnectionState implements RecordProtocolConstants {
   protected String _bulkCipherAlgorithm;
   protected byte _cipherType;
   protected int _keySize;
   protected int _keyMaterialLength;
   protected boolean _isExportable;
   protected String _macAlgorithm;
   protected int _hashSize;
   protected String _compressionAlgorithm;
   protected byte _keyExchangeAlgorithm;
   protected int _ivSize;
   protected MAC _mac;
   protected DecryptorInputStream _decryption;
   protected EncryptorOutputStream _encryption;

   public String getBulkCipherAlgorithm() {
      return this._bulkCipherAlgorithm;
   }

   public byte getCipherType() {
      return this._cipherType;
   }

   public int getKeySize() {
      return this._keySize;
   }

   public int getKeyMaterialLength() {
      return this._keyMaterialLength;
   }

   public boolean getIsExportable() {
      return this._isExportable;
   }

   public String getMacAlgorithm() {
      return this._macAlgorithm;
   }

   public int getHashSize() {
      return this._hashSize;
   }

   public String getCompressionAlgorithm() {
      return this._compressionAlgorithm;
   }

   public byte getKeyExchangeAlgorithm() {
      return this._keyExchangeAlgorithm;
   }

   public int getIVSize() {
      return this._ivSize;
   }

   public MAC getMAC() {
      return this._mac;
   }

   public EncryptorOutputStream getEncryptor() {
      return this._encryption;
   }

   public DecryptorInputStream getDecryptor() {
      return this._decryption;
   }

   public void setBulkCipherAlgorithm(String bulkCipherAlgorithm) {
      this._bulkCipherAlgorithm = bulkCipherAlgorithm;
   }

   public void setCipherType(byte cipherType) {
      this._cipherType = cipherType;
   }

   public void setKeySize(int keySize) {
      this._keySize = keySize;
   }

   public void setKeyMaterialLength(int keyMaterialLength) {
      this._keyMaterialLength = keyMaterialLength;
   }

   public void setIsExportable(boolean exportable) {
      this._isExportable = exportable;
   }

   public void setMacAlgorithm(String macAlgorithm) {
      this._macAlgorithm = macAlgorithm;
   }

   public void setHashSize(int hashSize) {
      this._hashSize = hashSize;
   }

   public void setCompressionAlgorithm(String compressionAlgorithm) {
      this._compressionAlgorithm = compressionAlgorithm;
   }

   public void setKeyExchangeAlgorithm(byte keyExchangeAlgorithm) {
      this._keyExchangeAlgorithm = keyExchangeAlgorithm;
   }

   public void setIVSize(int size) {
      this._ivSize = size;
   }

   public void setMAC(MAC mac) {
      this._mac = mac;
   }

   public void setEncryptor(EncryptorOutputStream encryption) {
      this._encryption = encryption;
   }

   public void setDecryptor(DecryptorInputStream decryption) {
      this._decryption = decryption;
   }
}
