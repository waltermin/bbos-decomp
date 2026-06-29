package net.rim.device.api.crypto.tls.ssl30;

import java.io.InputStream;
import java.io.OutputStream;
import net.rim.device.api.crypto.BlockDecryptorEngine;
import net.rim.device.api.crypto.BlockEncryptorEngine;
import net.rim.device.api.crypto.DecryptorInputStream;
import net.rim.device.api.crypto.EncryptorOutputStream;
import net.rim.device.api.crypto.InitializationVector;
import net.rim.device.api.crypto.MAC;
import net.rim.device.api.crypto.SymmetricKey;
import net.rim.device.api.crypto.tls.ConnectionState;

public class SSLConnectionState extends ConnectionState implements SSLRecordProtocolConstants {
   protected SymmetricKey _cipherKey;
   protected InitializationVector _iv;
   protected BlockEncryptorEngine _encryptor;
   protected BlockDecryptorEngine _decryptor;

   public SSLConnectionState() {
      super._mac = (MAC)(new Object());
   }

   public SSLConnectionState(InputStream inputStream) {
      this();
      super._decryption = (DecryptorInputStream)(new Object(inputStream));
   }

   public SSLConnectionState(OutputStream outputStream) {
      this();
      super._encryption = (EncryptorOutputStream)(new Object(outputStream));
   }

   public SymmetricKey getCipherKey() {
      return this._cipherKey;
   }

   public InitializationVector getIV() {
      return this._iv;
   }

   public BlockEncryptorEngine getEncryptorEngine() {
      return this._encryptor;
   }

   public BlockDecryptorEngine getDecryptorEngine() {
      return this._decryptor;
   }

   public void setCipherKey(SymmetricKey key) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public void setIV(InitializationVector iv) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public void setEncryptorEngine(BlockEncryptorEngine engine) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public void setDecryptorEngine(BlockDecryptorEngine engine) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }
}
