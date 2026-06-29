package net.rim.device.apps.internal.passwordkeeper;

import java.util.Random;
import net.rim.device.api.crypto.RandomSource;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;
import net.rim.device.api.util.StringUtilities;

public final class PasswordKeeperElement implements Persistable, SyncObject {
   private byte[][] _labels;
   private byte[][] _fields;
   private byte[] _salt;
   private long _creationTime;
   private int _uid;
   private static final int SALT_LENGTH = 8;
   public static final int TITLE = 0;
   public static final int USERNAME = 1;
   public static final int PASSWORD = 2;
   public static final int WEBSITE = 3;
   public static final int NOTES = 4;
   public static final int USERNAME_LABEL = 0;
   public static final int PASSWORD_LABEL = 1;
   public static final int WEBSITE_LABEL = 2;
   public static final int NOTES_LABEL = 3;

   public final String getField(int offset) {
      return PasswordKeeperManager.getInstance().decrypt(this._fields[offset], this._salt);
   }

   public final String getTitle() {
      return PasswordKeeperManager.getInstance().decrypt(this._fields[0], this._salt);
   }

   public final String getLabel(int offset) {
      return this._labels != null && this._labels.length > offset ? PasswordKeeperManager.getInstance().decrypt(this._labels[offset], this._salt) : null;
   }

   @Override
   public final int getUID() {
      return this._uid;
   }

   public final long getCreationTime() {
      return this._creationTime;
   }

   final byte[] getEncryptedField(int offset) {
      return this._fields[offset];
   }

   final byte[] getEncryptedLabel(int offset) {
      return this._labels[offset];
   }

   final byte[] getSalt() {
      return this._salt;
   }

   public final boolean checkForMatch(String[] words) {
      try {
         String[] titleWords = StringUtilities.stringToWords(this.getTitle());
         int numWords = words.length;

         for (int i = 0; i < numWords; i++) {
            int numTitleWords = titleWords.length;

            for (int j = 0; j < numTitleWords; j++) {
               if (StringUtilities.startsWithIgnoreCaseAndAccents(titleWords[j], words[i])) {
                  return true;
               }
            }
         }
      } catch (DecryptionException var7) {
         return false;
      } catch (PasswordKeeperLockedException var8) {
      }

      return false;
   }

   public final int getKeywords(String[] keywords) {
      try {
         return StringUtilities.stringToWords(this.getTitle(), keywords, 0);
      } catch (DecryptionException var3) {
         return 0;
      } catch (PasswordKeeperLockedException var4) {
         return 0;
      }
   }

   public final boolean equalFields(Object obj) {
      if (this == obj) {
         return true;
      }

      if (!(obj instanceof PasswordKeeperElement)) {
         return false;
      }

      PasswordKeeperElement element = (PasswordKeeperElement)obj;
      int numLabels = this._labels.length;
      if (numLabels != element._labels.length) {
         return false;
      }

      for (int i = 0; i < numLabels; i++) {
         if (!element.getLabel(i).equals(this.getLabel(i))) {
            return false;
         }
      }

      int numFields = this._fields.length;
      if (numFields != element._fields.length) {
         return false;
      }

      for (int i = 0; i < numFields; i++) {
         if (!element.getField(i).equals(this.getField(i))) {
            return false;
         }
      }

      return true;
   }

   final void changePassword(byte[] newPassword) {
      PasswordKeeperManager manager = PasswordKeeperManager.getInstance();
      int numLabels = this._labels.length;

      for (int i = 0; i < numLabels; i++) {
         this._labels[i] = manager.changePassword(this._labels[i], newPassword, this._salt);
      }

      int numFields = this._fields.length;

      for (int i = 0; i < numFields; i++) {
         this._fields[i] = manager.changePassword(this._fields[i], newPassword, this._salt);
      }
   }

   final void changePassword(byte[] oldPassword, byte[] newPassword) {
      PasswordKeeperManager manager = PasswordKeeperManager.getInstance();
      int numLabels = this._labels.length;

      for (int i = 0; i < numLabels; i++) {
         this._labels[i] = manager.changePassword(this._labels[i], oldPassword, newPassword, this._salt);
      }

      int numFields = this._fields.length;

      for (int i = 0; i < numFields; i++) {
         this._fields[i] = manager.changePassword(this._fields[i], oldPassword, newPassword, this._salt);
      }
   }

   final boolean checkPassword(byte[] password) {
      try {
         PasswordKeeperManager.getInstance().decrypt(this._fields[0], password, this._salt);
         return true;
      } catch (DecryptionException e) {
         return false;
      } catch (PasswordKeeperLockedException e) {
         return false;
      }
   }

   PasswordKeeperElement(byte[][] labels, byte[][] fields, byte[] salt, long creationTime, int uid) {
      this._labels = labels;
      this._fields = fields;
      this._salt = salt;
      this._creationTime = creationTime;
      this._uid = uid;
   }

   @Override
   public final boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (!(obj instanceof PasswordKeeperElement)) {
         return false;
      }

      PasswordKeeperElement element = (PasswordKeeperElement)obj;
      int numLabels = this._labels.length;
      if (numLabels != element._labels.length) {
         return false;
      }

      for (int i = 0; i < numLabels; i++) {
         if (!Arrays.equals(element._labels[i], this._labels[i])) {
            return false;
         }
      }

      int numFields = this._fields.length;
      if (numFields != element._fields.length) {
         return false;
      }

      for (int i = 0; i < numFields; i++) {
         if (!Arrays.equals(element._fields[i], this._fields[i])) {
            return false;
         }
      }

      return this._uid == element._uid;
   }

   public PasswordKeeperElement(String[] labels, String[] fields) {
      try {
         PasswordKeeperManager manager = PasswordKeeperManager.getInstance();
         this._salt = RandomSource.getBytes(8);
         if (labels != null) {
            int length = labels.length;
            this._labels = new byte[length][];

            for (int i = 0; i < length; i++) {
               this._labels[i] = labels[i] == null ? null : manager.encrypt(labels[i], this._salt);
            }
         }

         int length = fields.length;
         this._fields = new byte[length][];

         for (int i = 0; i < length; i++) {
            this._fields[i] = manager.encrypt(fields[i], this._salt);
         }

         this._creationTime = System.currentTimeMillis();
         this._uid = new Random().nextInt();
      } catch (PasswordKeeperLockedException e) {
         throw new RuntimeException();
      }
   }
}
