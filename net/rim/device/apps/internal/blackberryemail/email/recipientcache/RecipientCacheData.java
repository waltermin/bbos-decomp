package net.rim.device.apps.internal.blackberryemail.email.recipientcache;

import net.rim.device.api.crypto.RandomSource;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.Persistable;
import net.rim.device.apps.api.framework.model.EncryptableProvider;

public final class RecipientCacheData implements Persistable, SyncObject, EncryptableProvider {
   private Object _recipientEncoding;
   private int _serviceUIDHash;
   private int _serviceUserID;
   private int _messageClassification;
   private long _encodingUID;
   private int _uid;
   private int _flags;
   private int _hashCode;
   public static final int SMIME_SUPPORTS_AES_256_FLAG;
   public static final int SMIME_SUPPORTS_AES_192_FLAG;
   public static final int SMIME_SUPPORTS_AES_128_FLAG;
   public static final int SMIME_SUPPORTS_CAST_128_FLAG;
   public static final int PGP_SUPPORTS_AES_256_FLAG;
   public static final int PGP_SUPPORTS_AES_192_FLAG;
   public static final int PGP_SUPPORTS_AES_128_FLAG;
   public static final int PGP_SUPPORTS_CAST_128_FLAG;
   public static final int LAST_SENT_SIGNED_FLAG;
   public static final int LAST_SENT_ENCRYPTED_FLAG;
   public static final int CIPHER_SUPPORT_FLAGS;
   public static final int LAST_SENT_ACTION_FLAGS;

   final void setRecipient(String recipient) {
      this._hashCode = recipient.hashCode();
      this._recipientEncoding = PersistentContent.encode(recipient, true, true);
   }

   public final String getRecipient() {
      try {
         return PersistentContent.decodeString(this._recipientEncoding);
      } finally {
         ;
      }
   }

   public final int getServiceRecordID() {
      return this._serviceUIDHash;
   }

   @Override
   public final int getUID() {
      return this._uid;
   }

   public final int getServiceUserID() {
      return this._serviceUserID;
   }

   public final int getMessageClassification() {
      return this._messageClassification;
   }

   public final long getEncodingUID() {
      return this._encodingUID;
   }

   public final int getServiceUIDHash() {
      return this._serviceUIDHash;
   }

   public final int getFlags() {
      return this._flags;
   }

   public final void setFlag(int flag) {
      this._flags |= flag;
   }

   public final void clearFlag(int flag) {
      this._flags &= ~flag;
   }

   @Override
   public final boolean checkCrypt(boolean compress, boolean encrypt) {
      return PersistentContent.checkEncoding(this._recipientEncoding, compress, encrypt);
   }

   @Override
   public final Object reCrypt(boolean compress, boolean encrypt) {
      this._recipientEncoding = PersistentContent.reEncode(this._recipientEncoding, compress, encrypt);
      return null;
   }

   @Override
   public final boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (!(obj instanceof Object)) {
         if (!(obj instanceof RecipientCacheData)) {
            return false;
         }

         RecipientCacheData data = (RecipientCacheData)obj;
         return data.getRecipient().equals(this.getRecipient());
      } else {
         String recipient = (String)obj;
         return recipient.equals(this.getRecipient());
      }
   }

   @Override
   public final int hashCode() {
      return this._hashCode;
   }

   public RecipientCacheData(String recipient, int serviceUserID, int serviceUIDHash, int messageClassification, long encodingUID, int flags, int uid) {
      if (recipient == null) {
         throw new Object();
      }

      this.setRecipient(recipient);
      this._serviceUIDHash = serviceUIDHash;
      this._serviceUserID = serviceUserID;
      this._messageClassification = messageClassification;
      this._encodingUID = encodingUID;
      this._flags = flags;
      this._uid = uid;
   }

   public RecipientCacheData(String recipient, int serviceUserID, int serviceUIDHash, int messageClassification, long encodingUID, int flags) {
      this(recipient, serviceUserID, serviceUIDHash, messageClassification, encodingUID, flags, RandomSource.getInt());
   }
}
