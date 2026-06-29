package net.rim.device.internal.crypto;

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import net.rim.device.api.crypto.CryptoTokenException;
import net.rim.device.api.crypto.CryptoUnsupportedOperationException;
import net.rim.device.api.crypto.HMAC;
import net.rim.device.api.crypto.HMACKey;
import net.rim.device.api.crypto.RandomSource;
import net.rim.device.api.crypto.SHA256Digest;
import net.rim.device.api.crypto.SHA512Digest;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.WeakReferenceUtilities;
import net.rim.vm.Array;
import net.rim.vm.WeakReference;

public final class OTAKeyGenCrypto {
   private byte[] _deviceSTPrivateKey;
   private byte[] _deviceSTPublicKey;
   private byte _confirmationDataCounter;
   private ByteArrayOutputStream _confirmationDataStream;
   private WeakReference _oneByteBufferWR = new WeakReference(null);
   private WeakReference _fourByteBufferWR = new WeakReference(null);
   public static String OTAKEYGEN_CID = "OTAKEYGEN";
   public static final int SUCCESS;
   public static final int BUFFER_TOO_SMALL;
   public static final int CONFIRMATION_FAILED;
   public static final int ILLEGAL_ARGUMENT;
   public static final int ERROR;
   public static final byte REMOVE_ONLY_UID;
   public static final byte REMOVE_ONLY_KEYID;
   public static final byte EXPIRE_KEY;
   private static final int FIELD_ELEMENT_BIT_LENGTH;
   private static final int FIELD_ELEMENT_BYTE_LENGTH;
   private static final int COMPRESSED_POINT_BYTE_LENGTH;
   private static final byte HIGH_BYTE_MASK;
   private static final byte LOW_BYTE_MASK;
   public static final int PRIVATE_KEY_BYTE_LENGTH;
   public static final int PUBLIC_KEY_BYTE_LENGTH;
   public static final int KEY_MATERIAL_BYTE_LENGTH;
   public static final byte REKEY_ALGORITHM_ECMQV;
   private static final byte[] DEVICE_CONFIRMATION_DATA_SUFFIX = new byte[]{65};
   private static final byte[] SERVER_CONFIRMATION_DATA_SUFFIX = new byte[]{66};

   public OTAKeyGenCrypto() {
      this._confirmationDataStream = new ByteArrayOutputStream();
   }

   public final int beginActivation(byte[] sharedSecret, byte[] deviceSTPublicKeyBuffer) {
      byte[] sharedSecretCurvePoint = EncryptionUtilities.generateCurvePointFromByteArray(2, sharedSecret);
      this._deviceSTPrivateKey = new byte[66];
      RandomSource.getBytes(this._deviceSTPrivateKey);
      this._deviceSTPrivateKey[0] = (byte)(this._deviceSTPrivateKey[0] & 0);
      this._deviceSTPrivateKey[65] = (byte)(this._deviceSTPrivateKey[65] | 2);
      byte[] deviceSTPublicKeyXCoordinate = new byte[66];
      EncryptionUtilities.calculateKey(2, sharedSecretCurvePoint, this._deviceSTPrivateKey, deviceSTPublicKeyXCoordinate);
      this._deviceSTPublicKey = new byte[67];
      this._deviceSTPublicKey[0] = 2;
      System.arraycopy(deviceSTPublicKeyXCoordinate, 0, this._deviceSTPublicKey, 1, deviceSTPublicKeyXCoordinate.length);
      Array.resize(deviceSTPublicKeyBuffer, 67);
      System.arraycopy(this._deviceSTPublicKey, 0, deviceSTPublicKeyBuffer, 0, this._deviceSTPublicKey.length);
      return 0;
   }

   public final int beginReKey(byte[] deviceSTPublicKeyBuffer) {
      this._deviceSTPrivateKey = new byte[66];
      this._deviceSTPublicKey = new byte[67];
      EncryptionUtilities.createKeyPair(2, this._deviceSTPublicKey, this._deviceSTPrivateKey);
      Array.resize(deviceSTPublicKeyBuffer, 67);
      System.arraycopy(this._deviceSTPublicKey, 0, deviceSTPublicKeyBuffer, 0, this._deviceSTPublicKey.length);
      return 0;
   }

   public final int continueActivation(
      byte[] serverLTPublicKey,
      byte[] serverSTPublicKey,
      byte[] deviceLTPrivateKey,
      byte[] serverConfirmationValue,
      byte[] masterKeyBuffer,
      byte[] deviceConfirmationValueBuffer
   ) {
      byte[] k1 = EncryptionUtilities.calculateKey(2, serverSTPublicKey, this._deviceSTPrivateKey);
      byte[] k2 = EncryptionUtilities.calculateKey(2, serverLTPublicKey, deviceLTPrivateKey);
      SHA512Digest sha512Digest = new SHA512Digest();
      sha512Digest.update(k1);
      sha512Digest.update(k2);
      byte[] k_kconf = new byte[sha512Digest.getDigestLength()];
      sha512Digest.getDigest(k_kconf, 0);
      Array.resize(masterKeyBuffer, 32);
      System.arraycopy(k_kconf, 0, masterKeyBuffer, 0, masterKeyBuffer.length);
      return this.doConfirmationValueWork(serverConfirmationValue, deviceConfirmationValueBuffer, k_kconf);
   }

   public final int continueReKey(
      byte[] serverLTPublicKey,
      byte[] serverSTPublicKey,
      byte[] deviceLTPrivateKey,
      byte[] serverConfirmationValue,
      byte[] masterKeyBuffer,
      byte[] deviceConfirmationValueBuffer
   ) {
      byte[] xz = EncryptionUtilities.generateECMQVSharedSecret(
         2, deviceLTPrivateKey, this._deviceSTPrivateKey, this._deviceSTPublicKey, serverLTPublicKey, serverSTPublicKey
      );
      SHA512Digest sha512Digest = new SHA512Digest();
      sha512Digest.update(xz);
      byte[] k_kconf = new byte[sha512Digest.getDigestLength()];
      sha512Digest.getDigest(k_kconf, 0);
      Array.resize(masterKeyBuffer, 32);
      System.arraycopy(k_kconf, 0, masterKeyBuffer, 0, masterKeyBuffer.length);
      return this.doConfirmationValueWork(serverConfirmationValue, deviceConfirmationValueBuffer, k_kconf);
   }

   private final int doConfirmationValueWork(byte[] serverConfirmationValue, byte[] deviceConfirmationValueBuffer, byte[] k_kconf) {
      try {
         HMACKey confirmationHMACKey = new HMACKey(k_kconf, 32, 32);
         HMAC confirmationHMAC = new HMAC(confirmationHMACKey, new SHA256Digest());
         byte[] confirmationDataBytes = this._confirmationDataStream.toByteArray();
         byte[] expectedServerConfirmationValue = new byte[confirmationHMAC.getLength()];
         confirmationHMAC.update(confirmationDataBytes, 0, confirmationDataBytes.length);
         confirmationHMAC.update(SERVER_CONFIRMATION_DATA_SUFFIX);
         confirmationHMAC.getMAC(expectedServerConfirmationValue, 0, true);
         if (!Arrays.equals(expectedServerConfirmationValue, serverConfirmationValue)) {
            return 2;
         }

         confirmationHMAC.update(confirmationDataBytes, 0, confirmationDataBytes.length);
         confirmationHMAC.update(DEVICE_CONFIRMATION_DATA_SUFFIX);
         Array.resize(deviceConfirmationValueBuffer, confirmationHMAC.getLength());
         confirmationHMAC.getMAC(deviceConfirmationValueBuffer, 0, true);
         return 0;
      } catch (CryptoTokenException e) {
         return 2;
      } catch (CryptoUnsupportedOperationException e) {
         return 2;
      }
   }

   public static final int generateKeyPair(byte[] deviceLTPublicKeyBuffer, byte[] deviceLTPrivateKeyBuffer) {
      Array.resize(deviceLTPublicKeyBuffer, 67);
      Array.resize(deviceLTPrivateKeyBuffer, 66);
      EncryptionUtilities.createKeyPair(2, deviceLTPublicKeyBuffer, deviceLTPrivateKeyBuffer);
      return 0;
   }

   public final int addDataToHash(byte[] dataToAdd) {
      this._confirmationDataStream.write(this._confirmationDataCounter);
      this._confirmationDataStream.write(dataToAdd, 0, dataToAdd.length);
      this._confirmationDataCounter++;
      return 0;
   }

   public final int addDataToHash(byte dataToAdd) {
      byte[] oneByteBuffer = WeakReferenceUtilities.getByteArray(this._oneByteBufferWR, 1);
      oneByteBuffer[0] = dataToAdd;
      return this.addDataToHash(oneByteBuffer);
   }

   public final int addDataToHash(int dataToAdd) {
      byte[] fourByteBuffer = WeakReferenceUtilities.getByteArray(this._fourByteBufferWR, 4);
      fourByteBuffer[0] = (byte)(dataToAdd >> 24 & 0xFF);
      fourByteBuffer[1] = (byte)(dataToAdd >> 16 & 0xFF);
      fourByteBuffer[2] = (byte)(dataToAdd >> 8 & 0xFF);
      fourByteBuffer[3] = (byte)(dataToAdd & 0xFF);
      return this.addDataToHash(fourByteBuffer);
   }

   public final int addDataToHash(String dataToAdd) {
      return this.addDataToHash(dataToAdd.getBytes());
   }

   public static final void addSymmetricKey(String uid, DataInput input) {
      CryptoBlock.addSymmetricKey(uid, input);
   }

   public static final void addSymmetricKey(String uid, DataInput input, long expireDate) {
      CryptoBlock.addSymmetricKey(uid, input, expireDate);
   }

   public static final void addSymmetricKeyAsSecondaryKey(String uid, DataInput input, long expireDate) {
      CryptoBlock.addSymmetricKeyAsSecondaryKey(uid, input, expireDate);
   }

   public static final byte[] getSymmetricKey(String uid) {
      return CryptoBlock.getSymmetricKey(uid);
   }

   public static final String getKeyIDForUID(String uid) {
      return CryptoBlock.getKeyIDForUID(uid);
   }

   public static final boolean moveKey(String oldUid, String newUid) {
      return CryptoBlock.moveKey(oldUid, newUid);
   }

   public static final boolean removeSymmetricKey(String uid, byte flags) {
      return CryptoBlock.removeSymmetricKey(uid, flags);
   }

   public static final boolean removeSymmetricKeyByKeyID(String keyid, byte flags) {
      return CryptoBlock.removeSymmetricKeyByKeyID(keyid, flags);
   }

   public static final boolean revertSymmetricKey(String keyid) {
      return CryptoBlock.revertSymmetricKey(keyid);
   }
}
