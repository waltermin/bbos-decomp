package net.rim.device.internal.ui.autotext;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.ReadableSet;
import net.rim.device.api.collection.util.CollectionListenerManager;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.OTASyncCapable;
import net.rim.device.api.synchronization.OTASyncPriorityAndDependencyProvider;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncCollectionSchema;
import net.rim.device.api.synchronization.SyncCollectionStatistics;
import net.rim.device.api.synchronization.SyncCollectionStatisticsManager;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentListener;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.internal.proxy.Proxy;
import net.rim.vm.Array;

final class AutoTextCollection
   implements ReadableSet,
   CollectionEventSource,
   SyncCollection,
   GlobalEventListener,
   OTASyncCapable,
   PersistentContentListener,
   SyncCollectionStatistics,
   OTASyncPriorityAndDependencyProvider {
   private PersistentObject _persistentObject;
   private AutoTextDatabase _database;
   private CollectionListenerManager _listeners = (CollectionListenerManager)(new Object());
   private boolean _inTransaction;
   private boolean _syncRemoveAllDone;
   private AutoTextCollection$AutoTextSyncConverter _autoTextSyncConverter;
   private SyncCollectionSchema _schema;
   private String _lang;
   private static final int AUTOTEXT_VERSION;
   private static final int AUTOTEXT_VERSION_MIN;
   private static final int AUTOTEXT_VERSION_MAX;
   private static final int AUTOTEXT_VERSION_DESKTOP;
   private static final long PERSISTENT_NAME;
   private static boolean _encryptAutoTextEntries = true;
   private static final int[] KEY_FIELD_IDS = new int[]{1, 4, 6, -805044223, 3, -804651007, 51, -805044221, 10740707, 1829528321, -1989393782, -1566170261};
   private static final int DEFAULT_RECORD_TYPE;

   AutoTextCollection() {
      this._persistentObject = RIMPersistentStore.getPersistentObject(-4626390048222336213L);
      synchronized (this._persistentObject) {
         this._database = (AutoTextDatabase)this._persistentObject.getContents();
         if (this._database == null) {
            this._database = new AutoTextDatabase();
            this._database.populate(true);
            this._persistentObject.setContents(this._database, 51, false);
            this._persistentObject.commit();
         } else {
            this._database.populateIfEmpty();
         }
      }

      this._schema = (SyncCollectionSchema)(new Object());
      this._schema.setDefaultRecordType(1);
      this._schema.setKeyFieldIds(1, KEY_FIELD_IDS);
      Proxy.getInstance().addGlobalEventListener(this);
      this._autoTextSyncConverter = new AutoTextCollection$AutoTextSyncConverter();
      PersistentContent.addWeakListener(this);
   }

   private final void commit() {
      if (!this._inTransaction) {
         this._persistentObject.commit();
      }
   }

   private final void commit(Object entry) {
      if (!this._inTransaction) {
         if (entry != null) {
            PersistentObject.commit(this._database);
            return;
         }

         this._persistentObject.commit();
      }
   }

   final Object add(String pattern, String replace, int flags, int localeCode) {
      AutoTextEntry entry = new AutoTextEntry(pattern, replace, flags, localeCode);
      this.add(entry);
      return entry;
   }

   private final void add(AutoTextEntry entry) {
      synchronized (this._database) {
         if (!ObjectGroup.isInGroup(entry)) {
            ObjectGroup.createGroupIgnoreTooBig(entry);
         }

         this._database.getEntries(entry.getLocaleCode()).put(entry.getFindString(), entry);
         this.commit(entry);
      }

      if (!this._syncRemoveAllDone) {
         this._listeners.fireElementAdded(this, entry);
      }
   }

   final void remove(AutoTextEntry find) {
      int localeCode = find.getLocaleCode();
      String pattern = find.getFindString();
      this.remove(pattern, localeCode);
   }

   final void remove(String pattern, int localeCode) {
      boolean found = false;
      AutoTextEntry entry = null;
      synchronized (this._database) {
         Hashtable hashtable = this._database.getEntries(localeCode);
         found = hashtable.containsKey(pattern);
         if (found) {
            entry = (AutoTextEntry)hashtable.get(pattern);
            hashtable.remove(pattern);
            this.commit();
         }
      }

      if (found && !this._syncRemoveAllDone) {
         this._listeners.fireElementRemoved(this, entry);
      }
   }

   final Object get(String pattern) {
      int localeCode = Locale.getDefaultInputForSystem().getCode();
      Object result = null;

      while (true) {
         Hashtable table = this._database.getEntries(localeCode);
         if (table != null) {
            result = table.get(pattern);
         }

         if (result != null) {
            return result;
         }

         if (localeCode == 0) {
            return result;
         }

         if ((localeCode & 65535) != 0) {
            localeCode &= -65536;
         } else if ((localeCode & -65536) != 0) {
            localeCode = 0;
         }
      }
   }

   final Enumeration getAllKeys() {
      int localeCode = Locale.getDefaultInputForSystem().getCode();
      Hashtable table = null;
      int result = 0;

      while (true) {
         table = this._database.getEntries(localeCode);
         if (table != null) {
            result = table.size();
         }

         if (result != 0 || localeCode == 0) {
            return table.elements();
         }

         if ((localeCode & 65535) != 0) {
            localeCode &= -65536;
         } else if ((localeCode & -65536) != 0) {
            localeCode = 0;
         }
      }
   }

   public final int getWordCount() {
      int localeCode = Locale.getDefaultInputForSystem().getCode();
      int result = 0;

      while (true) {
         Hashtable table = this._database.getEntries(localeCode);
         if (table != null) {
            result = table.size();
         }

         if (result != 0) {
            return result;
         }

         if (localeCode == 0) {
            return result;
         }

         if ((localeCode & 65535) != 0) {
            localeCode &= -65536;
         } else if ((localeCode & -65536) != 0) {
            localeCode = 0;
         }
      }
   }

   final void enableSynchronization() {
      SyncManager syncManager = SyncManager.getInstance();
      if (syncManager != null) {
         syncManager.enableSynchronization(this);
      }
   }

   @Override
   public final int size() {
      int size = 0;
      Enumeration tables = this._database.enumEntryTables();

      while (tables.hasMoreElements()) {
         Hashtable table = (Hashtable)tables.nextElement();
         size += table.size();
      }

      return size;
   }

   @Override
   public final boolean contains(Object element) {
      if (element instanceof AutoTextEntry) {
         AutoTextEntry entry = (AutoTextEntry)element;
         Hashtable table = this._database.getEntries(entry.getLocaleCode());
         if (table != null) {
            return table.contains(entry.getFindString());
         }
      }

      return false;
   }

   @Override
   public final Enumeration getElements() {
      return this._database.getElements(Locale.getDefaultInputForSystem());
   }

   @Override
   public final int getElements(Object[] elements) {
      int size = 0;
      synchronized (this._database) {
         Array.resize(elements, this.size());
         Enumeration enumeration = this.getElements();

         while (enumeration.hasMoreElements()) {
            elements[size++] = enumeration.nextElement();
         }

         Array.resize(elements, size);
         return size;
      }
   }

   @Override
   public final void addCollectionListener(Object listener) {
      this._listeners.addCollectionListener(listener);
   }

   @Override
   public final void removeCollectionListener(Object listener) {
      this._listeners.removeCollectionListener(listener);
   }

   @Override
   public final boolean addSyncObject(SyncObject object) {
      if (object instanceof AutoTextEntry) {
         this.add((AutoTextEntry)object);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean updateSyncObject(SyncObject oldObject, SyncObject newObject) {
      if (oldObject instanceof AutoTextEntry && newObject instanceof AutoTextEntry) {
         this.remove((AutoTextEntry)oldObject);
         this.add((AutoTextEntry)newObject);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean removeSyncObject(SyncObject object) {
      if (object instanceof AutoTextEntry) {
         this.remove((AutoTextEntry)object);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean removeAllSyncObjects() {
      synchronized (this._database) {
         this._database.clear();
         this.commit();
      }

      this._syncRemoveAllDone = true;
      return true;
   }

   @Override
   public final SyncObject[] getSyncObjects() {
      synchronized (this._database) {
         SyncObject[] objects = new Object[this.getSyncObjectCount()];
         int dest = 0;
         Enumeration enumeration = this._database.getAllElements();

         while (enumeration.hasMoreElements()) {
            Object element = enumeration.nextElement();
            if (element instanceof Object) {
               objects[dest++] = (SyncObject)element;
            }
         }

         return objects;
      }
   }

   @Override
   public final SyncObject getSyncObject(int uid) {
      synchronized (this._database) {
         Enumeration e = this._database.getAllElements();

         while (e.hasMoreElements()) {
            Object element = e.nextElement();
            if (element instanceof Object) {
               SyncObject syncObject = (SyncObject)element;
               if (syncObject.getUID() == uid) {
                  return syncObject;
               }
            }
         }

         return null;
      }
   }

   @Override
   public final boolean isSyncObjectDirty(SyncObject object) {
      return true;
   }

   @Override
   public final void setSyncObjectDirty(SyncObject object) {
   }

   @Override
   public final void clearSyncObjectDirty(SyncObject object) {
   }

   @Override
   public final int getSyncObjectCount() {
      return this.size();
   }

   @Override
   public final int getSyncVersion() {
      return 2;
   }

   @Override
   public final String getSyncName() {
      return "AutoText";
   }

   @Override
   public final String getSyncName(Locale locale) {
      return null;
   }

   @Override
   public final SyncConverter getSyncConverter() {
      return this._autoTextSyncConverter;
   }

   @Override
   public final void beginTransaction() {
      this._inTransaction = true;
      this._syncRemoveAllDone = false;
   }

   @Override
   public final void endTransaction() {
      if (this._inTransaction) {
         this._inTransaction = false;
         this.commit();
         if (this._syncRemoveAllDone && this._database.isEmpty()) {
            this._database.populate(true);
         }

         this._syncRemoveAllDone = false;
         this._listeners.fireReset(this);
      }
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -8040378802380461050L) {
         this._database.populate(false);
         String lang = Locale.getDefaultInputForSystem().getLanguage();
         if (!lang.equals(this._lang)) {
            this._listeners.fireReset(this);
            this._lang = lang;
         }
      }
   }

   @Override
   public final void persistentContentStateChanged(int newState) {
   }

   @Override
   public final void persistentContentModeChanged(int generation) {
      this._database.reCrypt(_encryptAutoTextEntries);
      this.commit();
      this._listeners.fireReset(this);
   }

   static final boolean encryptAutoTextEntries() {
      return _encryptAutoTextEntries;
   }

   @Override
   public final SyncCollectionSchema getSchema() {
      return this._schema;
   }

   @Override
   public final int getSyncPriority() {
      return 1;
   }

   @Override
   public final int getDependencyLevel() {
      return 2;
   }

   @Override
   public final synchronized int getSyncCollectionSize() {
      return SyncCollectionStatisticsManager.getSyncCollectionSize(this);
   }
}
