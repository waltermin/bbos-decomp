package net.rim.device.api.crypto.keystore;

import java.util.Hashtable;
import net.rim.device.api.crypto.RandomSource;
import net.rim.device.api.crypto.SHA256Digest;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncEventListener;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.internal.system.FIPSPolicy;
import net.rim.device.internal.system.Security;

final class KeyStoreManagerSyncConverter implements SyncConverter, SyncObject, SyncEventListener {
   private int _uid;
   private byte[] _lastChallenge;
   private static final long KEY_STORE_MANAGER_SYNC_CONVERTER = 7188919585381272614L;
   private static final int PASSWORD_PRESENT = 1;
   private static final int ACCESS_GRANTED = 4;
   private static final int PASSWORD_ATTEMPTS = 7;
   private static final int PASSWORD_ATTEMPTS_THRESHOLD = 8;
   private static final int ALL_RECORDS_SYNCED_WITH_BES = 9;
   private static final int PASSWORD_MIN_LENGTH = 10;
   private static final int PASSWORD_COMPLEXITY = 11;
   private static final int LOW_SECURITY_DISABLED = 12;
   private static final int TRUSTED_KEYSTORE_PROTECTED = 13;
   private static final int CURRENT_PASSWORD_VERSION = 14;
   private static final int KEY_STORE_DATA_FORMAT_VERSION = 15;
   private static final int SIGN_MIN_KEYSTORE_SECURITY_LEVEL = 16;
   private static final int ENCRYPT_MIN_KEYSTORE_SECURITY_LEVEL = 17;
   private static final int PASSWORD_CHALLENGE = 18;
   private static final int PASSWORD_RESPONSE = 19;
   private static final int CURRENT_KEY_STORE_DATA_FORMAT_VERSION = 1;
   private static final int PASSWORD_CHALLENGE_LENGTH = 32;

   private KeyStoreManagerSyncConverter() {
      SyncManager.getInstance().addSyncEventListener(this);
   }

   public static final KeyStoreManagerSyncConverter getInstance() {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      KeyStoreManagerSyncConverter converter = (KeyStoreManagerSyncConverter)registry.getOrWaitFor(7188919585381272614L);
      if (converter == null) {
         converter = new KeyStoreManagerSyncConverter();
         registry.put(7188919585381272614L, converter);
      }

      return converter;
   }

   @Override
   public final boolean convert(SyncObject object, DataBuffer buffer, int version) {
      if (!(object instanceof KeyStoreManagerHelper)) {
         return false;
      }

      KeyStoreManagerHelper keyStoreHelper = (KeyStoreManagerHelper)object;
      ConverterUtilities.convertInt(buffer, 1, keyStoreHelper.getHash() == null ? 0 : 1, 1);
      ConverterUtilities.convertInt(buffer, 4, keyStoreHelper.getAccess() ? 1 : 0, 1);
      ConverterUtilities.convertInt(buffer, 7, keyStoreHelper.getPasswordAttempts(), 1);
      ConverterUtilities.convertInt(buffer, 8, keyStoreHelper.getPasswordAttemptsThreshold(), 1);
      ConverterUtilities.convertInt(buffer, 10, FIPSPolicy.getMaxInteger(8, 4, 5), 1);
      ConverterUtilities.convertInt(buffer, 11, ITPolicy.getByte(13, (byte)0), 1);
      String thumbprints = ITPolicy.getString(24, 24);
      ConverterUtilities.convertInt(buffer, 13, thumbprints == null ? 0 : 1, 1);
      ConverterUtilities.convertInt(buffer, 14, keyStoreHelper.getPasswordVersion(), 4);
      ConverterUtilities.convertInt(buffer, 15, 1, 1);
      ConverterUtilities.convertInt(buffer, 16, KeyStoreUtilitiesInternal.getAppropriateSecurityLevel(1, 1), 1);
      ConverterUtilities.convertInt(buffer, 17, KeyStoreUtilitiesInternal.getAppropriateSecurityLevel(1, 4), 1);
      this._lastChallenge = RandomSource.getBytes(32);
      ConverterUtilities.writeByteArray(buffer, 18, this._lastChallenge);
      return true;
   }

   @Override
   public final SyncObject convert(DataBuffer buffer, int version, int uid) {
      KeyStoreManagerHelper keyStoreHelper = KeyStoreManagerHelper.getInstance();

      try {
         buffer.rewind();
         if (ConverterUtilities.findType(buffer, 19)) {
            byte[] response = ConverterUtilities.readByteArray(buffer);
            if (keyStoreHelper.getHash() != null) {
               if (this._lastChallenge == null) {
                  return null;
               }

               SHA256Digest digest = new SHA256Digest();
               digest.update(this._lastChallenge);
               digest.update(keyStoreHelper.getHash());
               byte[] hash = digest.getDigest();
               byte[] password = new byte[32];

               int passwordIndex;
               for (passwordIndex = 0; passwordIndex < 32; passwordIndex++) {
                  password[passwordIndex] = (byte)(hash[passwordIndex] ^ response[passwordIndex]);
                  if (password[passwordIndex] == 0) {
                     break;
                  }
               }

               digest.reset();
               digest.update(password, 0, passwordIndex);
               byte[] passwordHash = digest.getDigest();
               if (Arrays.equals(passwordHash, keyStoreHelper.getHash())) {
                  keyStoreHelper.setAccess(true);
                  keyStoreHelper.setPasswordAttempts(1);
                  this._uid = uid;
                  return this;
               }

               keyStoreHelper.incrementPasswordAttempts();
               int passwordThreshold = keyStoreHelper.getPasswordAttemptsThreshold();
               int passwordAttempts = keyStoreHelper.getPasswordAttempts();
               if (passwordAttempts > passwordThreshold) {
                  Security.getInstance().deviceUnderAttack();
               }

               return null;
            }

            keyStoreHelper.setHash(response);
            keyStoreHelper.setAccess(true);
            keyStoreHelper.setPasswordAttempts(1);
            keyStoreHelper.setPasswordVersion(KeyStoreUtilitiesInternal.getMinutes(System.currentTimeMillis()));
         }

         buffer.rewind();
         if (ConverterUtilities.findType(buffer, 9) && keyStoreHelper.getAccess()) {
            keyStoreHelper.setSyncedWithBESHashtable(new Hashtable());
         }
      } finally {
         ;
      }

      this._uid = uid;
      return this;
   }

   @Override
   public final void syncEventOccurred(int eventId, Object object) {
      switch (eventId) {
         case 1:
         default:
            KeyStoreManagerHelper.getInstance().setAccess(false);
            return;
         case 2:
            KeyStoreManagerHelper.getInstance().setAccess(false);
         case 0:
      }
   }

   @Override
   public final int getUID() {
      return this._uid;
   }
}
