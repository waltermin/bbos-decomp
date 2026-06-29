package net.rim.device.apps.internal.passwordkeeper;

import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.util.CollectionListenerManager;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.OTASyncCapable;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncCollectionSchema;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.util.DataBuffer;

public final class PasswordKeeperSync implements SyncCollection, SyncConverter, OTASyncCapable, CollectionEventSource {
   private PasswordKeeperList _source;
   private CollectionListenerManager _collectionListenerManager = (CollectionListenerManager)(new Object());
   private SyncCollectionSchema _schema;
   private static final int TITLE = 1;
   private static final int USERNAME = 2;
   private static final int PASSWORD = 3;
   private static final int NOTES = 4;
   private static final int SALT = 5;
   private static final int WEBSITE = 6;
   private static final int CREATION_TIME = 7;
   private static final int USERNAME_LABEL = 8;
   private static final int PASSWORD_LABEL = 9;
   private static final int WEBSITE_LABEL = 10;
   private static final int NOTES_LABEL = 11;
   private static final byte[] EMPTY = new byte[0];
   private static final int[] KEY_FIELD_IDS = new int[]{7, -805044219, 1718183726, 10};
   private static final int DEFAULT_RECORD_TYPE = 1;

   public final void updateOTASync(boolean enable) {
      SyncManager.getInstance().allowOTASync(this, enable);
   }

   public final PasswordKeeperList getSource() {
      return this._source;
   }

   @Override
   public final void removeCollectionListener(Object listener) {
      this._collectionListenerManager.removeCollectionListener(listener);
   }

   @Override
   public final boolean addSyncObject(SyncObject object) {
      if (!(object instanceof PasswordKeeperElement)) {
         return false;
      }

      PasswordKeeperElement element = (PasswordKeeperElement)object;
      this._source.add(element);
      this._collectionListenerManager.fireElementAdded(this, element);
      return true;
   }

   @Override
   public final void beginTransaction() {
   }

   @Override
   public final void clearSyncObjectDirty(SyncObject object) {
   }

   @Override
   public final boolean removeSyncObject(SyncObject object) {
      if (!(object instanceof PasswordKeeperElement)) {
         return false;
      }

      PasswordKeeperElement element = (PasswordKeeperElement)object;
      this._source.remove(element);
      this._collectionListenerManager.fireElementRemoved(this, element);
      return true;
   }

   @Override
   public final void endTransaction() {
   }

   @Override
   public final void addCollectionListener(Object listener) {
      this._collectionListenerManager.addCollectionListener(listener);
   }

   @Override
   public final SyncConverter getSyncConverter() {
      return this;
   }

   @Override
   public final String getSyncName() {
      return "PasswordKeeper";
   }

   @Override
   public final String getSyncName(Locale locale) {
      return null;
   }

   @Override
   public final int getSyncObjectCount() {
      return this._source.size();
   }

   @Override
   public final SyncObject[] getSyncObjects() {
      int size = this._source.size();
      SyncObject[] array = new Object[size];

      for (int i = 0; i < size; i++) {
         array[i] = (SyncObject)this._source.getAt(i);
      }

      return array;
   }

   @Override
   public final SyncObject getSyncObject(int uid) {
      int size = this._source.size();

      for (int i = 0; i < size; i++) {
         PasswordKeeperElement element = (PasswordKeeperElement)this._source.getAt(i);
         if (element.getUID() == uid) {
            return element;
         }
      }

      return null;
   }

   @Override
   public final int getSyncVersion() {
      return 1;
   }

   @Override
   public final boolean isSyncObjectDirty(SyncObject object) {
      return false;
   }

   @Override
   public final boolean removeAllSyncObjects() {
      while (this._source.size() != 0) {
         this.removeSyncObject((SyncObject)this._source.getAt(0));
      }

      return true;
   }

   @Override
   public final void setSyncObjectDirty(SyncObject object) {
   }

   @Override
   public final boolean updateSyncObject(SyncObject oldObject, SyncObject newObject) {
      if (oldObject instanceof PasswordKeeperElement && newObject instanceof PasswordKeeperElement) {
         PasswordKeeperElement oldElement = (PasswordKeeperElement)oldObject;
         PasswordKeeperElement newElement = (PasswordKeeperElement)newObject;
         this._source.remove(oldElement);
         this._source.add(newElement);
         this._collectionListenerManager.fireElementUpdated(this, oldElement, newElement);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final SyncObject convert(DataBuffer buffer, int version, int uid) {
      byte[] usernameLabel = null;
      byte[] passwordLabel = null;
      byte[] websiteLabel = null;
      byte[] notesLabel = null;
      byte[] title = null;
      byte[] username = EMPTY;
      byte[] password = EMPTY;
      byte[] website = EMPTY;
      byte[] notes = EMPTY;
      byte[] salt = null;
      long creationTime = 0;

      try {
         buffer.rewind();
         if (ConverterUtilities.findType(buffer, 1)) {
            title = ConverterUtilities.readByteArray(buffer);
            buffer.rewind();
            if (ConverterUtilities.findType(buffer, 8)) {
               usernameLabel = ConverterUtilities.readByteArray(buffer);
            }

            buffer.rewind();
            if (ConverterUtilities.findType(buffer, 2)) {
               username = ConverterUtilities.readByteArray(buffer);
            }

            buffer.rewind();
            if (ConverterUtilities.findType(buffer, 9)) {
               passwordLabel = ConverterUtilities.readByteArray(buffer);
            }

            buffer.rewind();
            if (ConverterUtilities.findType(buffer, 3)) {
               password = ConverterUtilities.readByteArray(buffer);
            }

            buffer.rewind();
            if (ConverterUtilities.findType(buffer, 10)) {
               websiteLabel = ConverterUtilities.readByteArray(buffer);
            }

            buffer.rewind();
            if (ConverterUtilities.findType(buffer, 6)) {
               website = ConverterUtilities.readByteArray(buffer);
            }

            buffer.rewind();
            if (ConverterUtilities.findType(buffer, 11)) {
               notesLabel = ConverterUtilities.readByteArray(buffer);
            }

            buffer.rewind();
            if (ConverterUtilities.findType(buffer, 4)) {
               notes = ConverterUtilities.readByteArray(buffer);
            }

            buffer.rewind();
            if (ConverterUtilities.findType(buffer, 5)) {
               salt = ConverterUtilities.readByteArray(buffer);
            }

            buffer.rewind();
            if (ConverterUtilities.findType(buffer, 7)) {
               creationTime = ConverterUtilities.readLong(buffer);
            } else {
               creationTime = System.currentTimeMillis();
            }

            if (salt == null) {
               return null;
            }

            byte[][] labels = new byte[][]{usernameLabel, passwordLabel, websiteLabel, notesLabel};
            byte[][] fields = new byte[][]{title, username, password, website, notes};
            return new PasswordKeeperElement(labels, fields, salt, creationTime, uid);
         } else {
            return null;
         }
      } finally {
         ;
      }
   }

   @Override
   public final boolean convert(SyncObject object, DataBuffer buffer, int version) {
      if (!(object instanceof PasswordKeeperElement)) {
         return false;
      }

      PasswordKeeperElement element = (PasswordKeeperElement)object;
      byte[] title = element.getEncryptedField(0);
      byte[] username = element.getEncryptedField(1);
      byte[] password = element.getEncryptedField(2);
      byte[] website = element.getEncryptedField(3);
      byte[] notes = element.getEncryptedField(4);
      byte[] salt = element.getSalt();
      byte[] usernameLabel = element.getEncryptedLabel(0);
      byte[] passwordLabel = element.getEncryptedLabel(1);
      byte[] websiteLabel = element.getEncryptedLabel(2);
      byte[] notesLabel = element.getEncryptedLabel(3);
      if (title != null) {
         ConverterUtilities.writeByteArray(buffer, 1, title);
      }

      if (username != null) {
         ConverterUtilities.writeByteArray(buffer, 2, username);
      }

      if (password != null) {
         ConverterUtilities.writeByteArray(buffer, 3, password);
      }

      if (website != null) {
         ConverterUtilities.writeByteArray(buffer, 6, website);
      }

      if (notes != null) {
         ConverterUtilities.writeByteArray(buffer, 4, notes);
      }

      if (salt == null) {
         return false;
      }

      ConverterUtilities.writeByteArray(buffer, 5, salt);
      ConverterUtilities.writeLong(buffer, 7, element.getCreationTime());
      if (usernameLabel != null) {
         ConverterUtilities.writeByteArray(buffer, 8, usernameLabel);
      }

      if (passwordLabel != null) {
         ConverterUtilities.writeByteArray(buffer, 9, passwordLabel);
      }

      if (websiteLabel != null) {
         ConverterUtilities.writeByteArray(buffer, 10, websiteLabel);
      }

      if (notesLabel != null) {
         ConverterUtilities.writeByteArray(buffer, 11, notesLabel);
      }

      return true;
   }

   @Override
   public final SyncCollectionSchema getSchema() {
      return this._schema;
   }

   public PasswordKeeperSync() {
      this._source = new PasswordKeeperList();
      PasswordKeeperOptions options = PasswordKeeperOptions.getOptions();
      this._schema = (SyncCollectionSchema)(new Object());
      this._schema.setDefaultRecordType(1);
      this._schema.setKeyFieldIds(1, KEY_FIELD_IDS);
      SyncManager manager = SyncManager.getInstance();
      manager.enableSynchronization(this, options.getOTASync());
   }
}
