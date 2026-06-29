package net.rim.device.apps.api.messaging.util;

import java.util.Enumeration;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.LongKeyProviderAdaptor;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Persistable;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.messaging.DateSortKeyProviderIndirection;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.vm.Array;

public class SimpleFolder implements Folder, Persistable {
   protected long _applicationFamily;
   protected long _uid;
   protected Object[] _subFolders;
   protected Object _friendlyNameEncoding;
   private long _rbId;
   private String _rbName;
   private int _rbKey;
   private int _cachedLocaleCode;
   protected String _collectionClassName;
   protected SimpleFolder _parentFolder;
   protected PersistentObject _persistentObject;
   protected int _flags;
   protected static final long FOLDER_PERSISTENT_STORE_NAME;
   public static final int ENABLE_SEARCH;
   protected static LongKeyProviderAdaptor _longKeyProviderAdaptor = new DateSortKeyProviderIndirection();

   public void initializeOnSystemStart() {
      this.getContainedItems();
   }

   protected void commit() {
      if (this._parentFolder != null) {
         this._parentFolder.commit();
      } else {
         this._persistentObject.commit();
      }
   }

   public void removeSubFolder(Folder folder) {
      long luid = folder.getLUID();
      synchronized (this._subFolders) {
         int length = this._subFolders.length;

         for (int i = length - 1; i >= 0; i--) {
            if (luid == ((Folder)this._subFolders[i]).getLUID()) {
               this._subFolders[i] = this._subFolders[length - 1];
               Array.resize(this._subFolders, length - 1);
               this.commit();
               return;
            }
         }
      }
   }

   public void setLUID(long newLUID) {
      this._uid = newLUID;
   }

   public void putFolder(Folder folder) {
      long luid = folder.getLUID();
      synchronized (this._subFolders) {
         int length = this._subFolders.length;

         for (int i = length - 1; i >= 0; i--) {
            if (luid == ((Folder)this._subFolders[i]).getLUID()) {
               return;
            }
         }

         Array.resize(this._subFolders, length + 1);
         this._subFolders[length] = folder;
      }

      this.commit();
   }

   public void setFriendlyName(String newName) {
      this._friendlyNameEncoding = PersistentContent.encode(newName);
      this._rbName = null;
   }

   public void setParentFolder(SimpleFolder newParentFolder) {
      this._parentFolder = newParentFolder;
   }

   @Override
   public Folder getParentFolder() {
      return this._parentFolder;
   }

   @Override
   public Folder getFolder(long folderUid) {
      synchronized (this._subFolders) {
         for (int i = this._subFolders.length - 1; i >= 0; i--) {
            Folder tempFolder = (Folder)this._subFolders[i];
            if (folderUid == tempFolder.getLUID()) {
               return tempFolder;
            }

            tempFolder = tempFolder.getFolder(folderUid);
            if (tempFolder != null) {
               return tempFolder;
            }
         }

         return null;
      }
   }

   @Override
   public Folder getBaseFolder() {
      if (this._parentFolder == null) {
         synchronized (this._subFolders) {
            return this._subFolders.length > 0 ? (Folder)this._subFolders[0] : this;
         }
      } else {
         Folder f = this;
         Folder parent = this._parentFolder;

         for (Folder parent2 = parent.getParentFolder(); parent2 != null; parent2 = parent.getParentFolder()) {
            f = parent;
            parent = parent2;
         }

         return f;
      }
   }

   @Override
   public String getFriendlyName() {
      if (this._rbName != null) {
         int currentCode = Locale.getDefault().getCode();
         if (this._cachedLocaleCode != currentCode) {
            this._cachedLocaleCode = currentCode;
            ResourceBundleFamily family = ResourceBundle.getBundle(this._rbId, this._rbName);
            this._friendlyNameEncoding = PersistentContent.encode(family.getString(this._rbKey));
         }
      }

      return PersistentContent.decodeString(this._friendlyNameEncoding);
   }

   @Override
   public Enumeration getSubFolders() {
      synchronized (this._subFolders) {
         int length = this._subFolders.length;
         Object[] objects = new Object[length];
         System.arraycopy(this._subFolders, 0, objects, 0, length);
         return (Enumeration)(new Object(objects));
      }
   }

   @Override
   public boolean containsSubFolders() {
      return this._subFolders.length != 0;
   }

   @Override
   public long getLUID() {
      return this._uid;
   }

   @Override
   public boolean canContainItems() {
      return true;
   }

   @Override
   public Collection getContainedItems() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/apps/api/messaging/util/SimpleFolder._collectionClassName Ljava/lang/String;
      // 04: ifnull 5d
      // 07: invokestatic net/rim/device/api/system/ApplicationRegistry.getApplicationRegistry ()Lnet/rim/device/api/system/ApplicationRegistry;
      // 0a: astore 2
      // 0b: aload 2
      // 0c: aload 0
      // 0d: getfield net/rim/device/apps/api/messaging/util/SimpleFolder._applicationFamily J
      // 10: aload 0
      // 11: getfield net/rim/device/apps/api/messaging/util/SimpleFolder._uid J
      // 14: ladd
      // 15: invokevirtual net/rim/device/api/system/ApplicationRegistry.getOrWaitFor (J)Ljava/lang/Object;
      // 18: checkcast net/rim/device/apps/api/messaging/util/FolderCollection
      // 1b: astore 1
      // 1c: aload 1
      // 1d: ifnonnull 48
      // 20: aload 0
      // 21: getfield net/rim/device/apps/api/messaging/util/SimpleFolder._collectionClassName Ljava/lang/String;
      // 24: invokestatic java/lang/Class.forName (Ljava/lang/String;)Ljava/lang/Class;
      // 27: invokevirtual java/lang/Class.newInstance ()Ljava/lang/Object;
      // 2a: checkcast net/rim/device/apps/api/messaging/util/FolderCollection
      // 2d: astore 1
      // 2e: goto 3a
      // 31: astore 3
      // 32: aconst_null
      // 33: areturn
      // 34: astore 3
      // 35: aconst_null
      // 36: areturn
      // 37: astore 3
      // 38: aconst_null
      // 39: areturn
      // 3a: aload 2
      // 3b: aload 0
      // 3c: getfield net/rim/device/apps/api/messaging/util/SimpleFolder._applicationFamily J
      // 3f: aload 0
      // 40: getfield net/rim/device/apps/api/messaging/util/SimpleFolder._uid J
      // 43: ladd
      // 44: aload 1
      // 45: invokevirtual net/rim/device/api/system/ApplicationRegistry.put (JLjava/lang/Object;)V
      // 48: aload 1
      // 49: aload 0
      // 4a: getfield net/rim/device/apps/api/messaging/util/SimpleFolder._applicationFamily J
      // 4d: aload 0
      // 4e: getfield net/rim/device/apps/api/messaging/util/SimpleFolder._uid J
      // 51: getstatic net/rim/device/apps/api/messaging/util/SimpleFolder._longKeyProviderAdaptor Lnet/rim/device/api/collection/LongKeyProviderAdaptor;
      // 54: aload 0
      // 55: invokeinterface net/rim/device/apps/api/messaging/util/FolderCollection.initialize (JJLnet/rim/device/api/collection/LongKeyProviderAdaptor;Ljava/lang/Object;)Z 7
      // 5a: pop
      // 5b: aload 1
      // 5c: areturn
      // 5d: aconst_null
      // 5e: areturn
      // try (16 -> 22): 23 null
      // try (16 -> 22): 26 null
      // try (16 -> 22): 29 null
   }

   @Override
   public boolean isVisible(Object context) {
      return ContextObject.getFlag(context, 22) ? (this._flags & 1) != 0 : true;
   }

   private static SimpleFolder createInstance(long applicationFamily, long hierarchyId, ResourceBundleFamily rb, int key, String friendlyName, int flags) {
      SimpleFolder hierarchy = null;
      PersistentObject persistentObject = RIMPersistentStore.getPersistentObject(4848635152656490553L + applicationFamily + hierarchyId);
      synchronized (persistentObject) {
         hierarchy = (SimpleFolder)persistentObject.getContents();
         if (hierarchy == null) {
            hierarchy = new SimpleFolder(applicationFamily, hierarchyId, rb, key, friendlyName, persistentObject, flags);
            persistentObject.setContents(hierarchy, 51);
            persistentObject.commit();
         }

         return hierarchy;
      }
   }

   public SimpleFolder(long applicationFamily, long uid, String friendlyName, String collectionClassName, SimpleFolder parentFolder) {
      this(applicationFamily, uid, friendlyName, collectionClassName, parentFolder, 0);
   }

   private SimpleFolder(long applicationFamily, long uid, ResourceBundleFamily rb, int key, String friendlyName, PersistentObject persistentObject, int flags) {
      this(applicationFamily, uid, rb, key, friendlyName, null, null, flags);
      this._persistentObject = persistentObject;
   }

   protected SimpleFolder(long applicationFamily, long uid, String friendlyName, PersistentObject persistentObject, int flags) {
      this(applicationFamily, uid, null, 0, friendlyName, persistentObject, flags);
   }

   @Override
   public String toString() {
      return this.getFriendlyName();
   }

   public SimpleFolder(long applicationFamily, long uid, String friendlyName, String collectionClassName, SimpleFolder parentFolder, int flags) {
      this(applicationFamily, uid, null, 0, friendlyName, collectionClassName, parentFolder, flags);
   }

   public static SimpleFolder createInstance(long applicationFamily, long hierarchyId, ResourceBundleFamily rb, int key) {
      return createInstance(applicationFamily, hierarchyId, rb, key, null, 0);
   }

   private SimpleFolder(
      long applicationFamily,
      long uid,
      ResourceBundleFamily rb,
      int rbKey,
      String friendlyName,
      String collectionClassName,
      SimpleFolder parentFolder,
      int flags
   ) {
      if (rb != null && friendlyName == null) {
         this._rbId = rb.getId();
         this._rbName = rb.getName();
         this._rbKey = rbKey;
         this._cachedLocaleCode = Locale.getDefault().getCode();
         friendlyName = rb.getString(rbKey);
      }

      this._friendlyNameEncoding = PersistentContent.encode(friendlyName);
      this._applicationFamily = applicationFamily;
      this._uid = uid;
      this._subFolders = new Object[0];
      this._collectionClassName = collectionClassName;
      this._parentFolder = parentFolder;
      this._flags = flags;
   }

   public static SimpleFolder createInstance(long applicationFamily, long hierarchyId, ResourceBundleFamily rb, int key, int flags) {
      return createInstance(applicationFamily, hierarchyId, rb, key, null, flags);
   }

   public static SimpleFolder createInstance(long applicationFamily, long hierarchyId, String friendlyName, int flags) {
      return createInstance(applicationFamily, hierarchyId, null, 0, friendlyName, flags);
   }

   public static SimpleFolder createInstance(long applicationFamily, long hierarchyId, String friendlyName) {
      return createInstance(applicationFamily, hierarchyId, friendlyName, 0);
   }

   public static SimpleFolder getInstance(long applicationFamily, long hierarchyId) {
      PersistentObject persistentObject = RIMPersistentStore.getPersistentObject(4848635152656490553L + applicationFamily + hierarchyId);
      return (SimpleFolder)persistentObject.getContents();
   }

   public SimpleFolder(long applicationFamily, long uid, ResourceBundleFamily rb, int rbKey, String collectionClassName, SimpleFolder parentFolder, int flags) {
      this(applicationFamily, uid, rb, rbKey, null, collectionClassName, parentFolder, flags);
   }
}
