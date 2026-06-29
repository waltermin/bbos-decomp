package net.rim.device.api.crypto.keystore;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.util.CollectionListenerManager;
import net.rim.device.api.crypto.Key;
import net.rim.device.api.crypto.PrivateKey;
import net.rim.device.api.crypto.PublicKey;
import net.rim.device.api.crypto.SymmetricKey;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateKeyStoreIndex;
import net.rim.device.api.crypto.certificate.CertificateStatus;
import net.rim.device.api.system.CodeSigningKey;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.EmptyEnumeration;
import net.rim.device.api.util.IntMultiMap;
import net.rim.device.api.util.LongHashtable;

public class RIMKeyStore implements KeyStore, Collection, CollectionEventSource {
   private LongHashtable _hashtable;
   private LongHashtable _indexHashtable;
   private Vector _vector;
   private KeyStore _keyStore;
   private String _name;
   private CollectionListenerManager _listeners = new CollectionListenerManager();

   void ungroupData(RIMKeyStoreData data) {
      data.ungroupData();
   }

   void groupData(RIMKeyStoreData data) {
      data.groupData();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   void removeAllKeys(KeyStoreTicket ticket) {
      if (this._vector != null && this._vector.size() != 0) {
         if (!this.checkTicket(ticket)) {
            ticket = this.getTicket();
         }

         EventLogger.logStackTrace(3915475930975345450L, "Removing all certs from keystore: " + this.getName());
         this._hashtable.clear();

         for (int i = this._vector.size() - 1; i >= 0; i--) {
            boolean var6 = false /* VF: Semaphore variable */;

            try {
               var6 = true;
               RIMKeyStoreData e = (RIMKeyStoreData)this._vector.elementAt(i);
               e.removeCertificate();
               this._vector.removeElement(e);
               this._listeners.fireElementRemoved(this, e);
               var6 = false;
            } finally {
               if (var6) {
                  KeyStoreData data = (KeyStoreData)this._vector.elementAt(i);
                  this._vector.removeElement(data);
                  this._listeners.fireElementRemoved(this, data);
                  continue;
               }
            }
         }
      }
   }

   protected final void deleteKey(KeyStoreData data) {
      if (this._vector != null && this._vector.size() != 0) {
         Enumeration maps = this._hashtable.elements();

         while (maps.hasMoreElements()) {
            Object obj = maps.nextElement();
            if (obj instanceof IntMultiMap) {
               IntMultiMap map = (IntMultiMap)obj;
               map.removeValue(data);
            }
         }

         if (data instanceof RIMKeyStoreData) {
            ((RIMKeyStoreData)data).removeCertificate();
         }

         this._vector.removeElement(data);
         this._listeners.fireElementRemoved(this, data);
      }
   }

   void reinitializeIndices(KeyStoreData data) {
      Enumeration maps = this._hashtable.elements();

      while (maps.hasMoreElements()) {
         Object obj = maps.nextElement();
         if (obj instanceof IntMultiMap) {
            IntMultiMap map = (IntMultiMap)obj;
            map.removeValue(data);
         }
      }

      this.addKeyStoreDataToIndices(data);
   }

   protected KeyStoreData set(
      AssociatedData[] associatedData,
      String label,
      PrivateKey privateKey,
      String privateKeyEncodingAlgorithm,
      int securityLevel,
      PublicKey publicKey,
      long keyUsage,
      Certificate certificate,
      CertificateStatus status,
      KeyStoreTicket ticket
   ) {
      if (!this.checkTicket(ticket)) {
         ticket = this.getTicket();
      }

      RIMKeyStoreData data = new RIMKeyStoreData(
         associatedData, label, privateKey, privateKeyEncodingAlgorithm, securityLevel, publicKey, keyUsage, certificate, status, ticket
      );
      this.set(associatedData, data);
      this.groupData(data);
      return data;
   }

   protected synchronized void set(AssociatedData[] associatedData, KeyStoreData data) {
      if (this._vector == null) {
         this._vector = new Vector();
      }

      this._vector.addElement(data);
      this.addKeyStoreDataToIndices(data);
      this._listeners.fireElementAdded(this, data);
   }

   @Override
   public KeyStoreData set(
      AssociatedData[] associatedData, String label, SymmetricKey symmetricKey, String symmetricKeyEncodingAlgorithm, int securityLevel, KeyStoreTicket ticket
   ) {
      if (!this.checkTicket(ticket)) {
         ticket = this.getTicket();
      }

      RIMKeyStoreData data = new RIMKeyStoreData(associatedData, label, symmetricKey, symmetricKeyEncodingAlgorithm, securityLevel, ticket);
      this.set(associatedData, data);
      this.groupData(data);
      return data;
   }

   @Override
   public KeyStoreData set(AssociatedData[] associatedData, String label, Certificate certificate, CertificateStatus certStatus, KeyStoreTicket ticket) {
      return (KeyStoreData)this.set(associatedData, label, null, null, 2, null, -1, certificate, certStatus, ticket);
   }

   @Override
   public synchronized Enumeration elements(long index, Object target) {
      return this.elements(index, target, false);
   }

   @Override
   public synchronized Enumeration elements(long index, Object target, boolean backingKeyStore) {
      IntMultiMap map = (IntMultiMap)this._hashtable.get(index);
      KeyStoreIndex indexInstance = (KeyStoreIndex)this._indexHashtable.get(index);
      if (backingKeyStore && this._keyStore != null) {
         if (map != null && indexInstance != null) {
            Enumeration[] enumeration = new Enumeration[2];
            int hashValue = indexInstance.getHash(target);
            Enumeration returnValue = map.elements(hashValue);
            enumeration[0] = new RIMKeyStore$FilteredKeyEnumeration(returnValue, indexInstance, target);
            enumeration[1] = this._keyStore.elements(index, target, backingKeyStore);
            return new MultipleKeyStoreEnumeration(enumeration);
         } else {
            return this._keyStore.elements(index, target, backingKeyStore);
         }
      } else if (map != null && indexInstance != null) {
         int hashValue = indexInstance.getHash(target);
         Enumeration returnValue = map.elements(hashValue);
         RIMKeyStore$FilteredKeyEnumeration enumeration = new RIMKeyStore$FilteredKeyEnumeration(returnValue, indexInstance, target);
         return !enumeration.hasMoreElements() && this._keyStore != null ? this._keyStore.elements(index, target) : enumeration;
      } else {
         return this._keyStore == null ? new EmptyEnumeration() : this._keyStore.elements(index, target);
      }
   }

   @Override
   public void removeKey(KeyStoreData data, KeyStoreTicket ticket) {
      if (this._vector != null && this._vector.size() != 0) {
         if (!this.checkTicket(ticket)) {
            ticket = this.getTicket();
         }

         this.deleteKey(data);
         byte[][] historicalId = data.getAssociatedData(3198502480206239397L);
         if (historicalId != null) {
            AssociatedDataKeyStoreIndex index = new AssociatedDataKeyStoreIndex(3198502480206239397L);
            this.addIndex(index);
            Enumeration historicalKeys = this.elements(index.getID(), historicalId[0]);
            Vector keysToRemove = new Vector();

            while (historicalKeys.hasMoreElements()) {
               keysToRemove.addElement(historicalKeys.nextElement());
            }

            for (int i = keysToRemove.size() - 1; i >= 0; i--) {
               this.deleteKey((KeyStoreData)keysToRemove.elementAt(i));
            }
         }
      }
   }

   @Override
   public KeyStoreData set(AssociatedData[] associatedData, String label, PublicKey publicKey, long keyUsage, KeyStoreTicket ticket) {
      return (KeyStoreData)this.set(associatedData, label, null, null, 2, publicKey, keyUsage, null, null, ticket);
   }

   @Override
   public KeyStoreData set(
      AssociatedData[] associatedData,
      String label,
      PrivateKey privateKey,
      String privateKeyEncodingAlgorithm,
      int securityLevel,
      Certificate certificate,
      CertificateStatus certStatus,
      KeyStoreTicket ticket
   ) {
      return (KeyStoreData)this.set(associatedData, label, privateKey, privateKeyEncodingAlgorithm, securityLevel, null, -1, certificate, certStatus, ticket);
   }

   @Override
   public Enumeration elements() {
      return this.elements(false);
   }

   @Override
   public Enumeration elements(boolean backingKeyStore) {
      if (backingKeyStore && this._keyStore != null) {
         Enumeration[] enumeration;
         if (this._vector != null) {
            enumeration = new Enumeration[]{this._vector.elements(), this._keyStore.elements(backingKeyStore)};
         } else {
            enumeration = new Enumeration[1];
            enumeration[0] = this._keyStore.elements(backingKeyStore);
         }

         return new MultipleKeyStoreEnumeration(enumeration);
      } else {
         return this._vector != null ? this._vector.elements() : new EmptyEnumeration();
      }
   }

   @Override
   public synchronized Enumeration elements(long index) {
      return this.elements(index, false);
   }

   @Override
   public synchronized Enumeration elements(long index, boolean backingKeyStore) {
      IntMultiMap map = (IntMultiMap)this._hashtable.get(index);
      if (backingKeyStore && this._keyStore != null) {
         if (map == null) {
            return this._keyStore.elements(index, backingKeyStore);
         }

         Enumeration[] enumeration = new Enumeration[]{map.elements(), this._keyStore.elements(index, backingKeyStore)};
         return new MultipleKeyStoreEnumeration(enumeration);
      } else {
         return map == null ? new EmptyEnumeration() : map.elements();
      }
   }

   @Override
   public synchronized boolean exists(long index, Object target) {
      Enumeration enumeration = this.elements(index, target, false);
      return enumeration.hasMoreElements();
   }

   @Override
   public boolean addIndex(KeyStoreIndex index) {
      if (this.existsIndex(index.getID())) {
         return false;
      }

      synchronized (this) {
         if (this._indexHashtable == null) {
            this._indexHashtable = new LongHashtable();
         }

         if (this._indexHashtable.containsKey(index.getID())) {
            return false;
         }

         IntMultiMap map = new IntMultiMap();
         KeyStoreDataMap dataMap = new KeyStoreDataMap(map);
         long id = index.getID();
         if (this._vector != null) {
            Enumeration values = this._vector.elements();

            while (values.hasMoreElements()) {
               KeyStoreData data = (KeyStoreData)values.nextElement();
               if (!this.notUsedDataExists(data, id)) {
                  if (this.indexDataExists(data, id)) {
                     this.addToIndex(data, id, dataMap);
                  } else {
                     index.addToIndex(data, dataMap);
                  }

                  this._hashtable.put(id, map);
               }
            }
         }

         this._indexHashtable.put(index.getID(), index);
         if (this._keyStore != null) {
            this._keyStore.addIndex(index);
         }

         return true;
      }
   }

   @Override
   public synchronized void addIndices(KeyStoreIndex[] indices) {
      if (this._indexHashtable == null) {
         this._indexHashtable = new LongHashtable();
      }

      KeyStoreIndex[] realIndices = new KeyStoreIndex[indices.length];
      int count = 0;
      int length = indices.length;

      for (int i = 0; i < length; i++) {
         if (indices[i] != null && !this._indexHashtable.containsKey(indices[i].getID())) {
            realIndices[count++] = indices[i];
            this._indexHashtable.put(indices[i].getID(), indices[i]);
         }
      }

      IntMultiMap[] maps = new IntMultiMap[count];
      KeyStoreDataMap[] dataMap = new KeyStoreDataMap[count];

      for (int i = 0; i < count; i++) {
         maps[i] = new IntMultiMap();
         this._hashtable.put(realIndices[i].getID(), maps[i]);
         dataMap[i] = new KeyStoreDataMap(maps[i]);
      }

      if (this._vector != null) {
         Enumeration values = this._vector.elements();

         while (values.hasMoreElements()) {
            KeyStoreData data = (KeyStoreData)values.nextElement();

            for (int j = 0; j < count; j++) {
               long indexID = realIndices[j].getID();
               if (!this.notUsedDataExists(data, indexID)) {
                  if (this.indexDataExists(data, indexID)) {
                     this.addToIndex(data, indexID, dataMap[j]);
                  } else {
                     realIndices[j].addToIndex(data, dataMap[j]);
                  }

                  this._hashtable.put(indexID, maps[j]);
               }
            }
         }
      }

      if (this._keyStore != null) {
         this._keyStore.addIndices(realIndices);
      }
   }

   @Override
   public synchronized void removeIndex(long index) {
      if (this._hashtable != null) {
         this._hashtable.remove(index);
      }

      if (this._indexHashtable != null) {
         this._indexHashtable.remove(index);
      }
   }

   @Override
   public boolean existsIndex(long index) {
      return this._indexHashtable != null ? this._indexHashtable.containsKey(index) : false;
   }

   @Override
   public int size() {
      return this._vector != null ? this._vector.size() : 0;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void changePassword() {
      PrivateKeysKeyStoreIndex index = new PrivateKeysKeyStoreIndex();
      this.addIndex(index);
      Hashtable dataToBeDeleted = new Hashtable();
      RIMKeyStoreData data = null;
      Enumeration enumeration = this.elements(index.getID());

      while (enumeration.hasMoreElements()) {
         data = (RIMKeyStoreData)enumeration.nextElement();
         this.ungroupData(data);
         boolean var10 = false /* VF: Semaphore variable */;

         label67: {
            KeyStoreDecodeRuntimeException e;
            try {
               var10 = true;
               data.changePassword();
               var10 = false;
               break label67;
            } catch (KeyStoreDecodeRuntimeException var12) {
               e = var12;
               var10 = false;
            } finally {
               if (var10) {
                  dataToBeDeleted.put(data, data);
                  break label67;
               }
            }

            throw e;
         }

         this.groupData(data);
      }

      if (dataToBeDeleted.size() > 0) {
         RIMKeyStoreTicket ticket = new RIMKeyStoreTicket(this, KeyStoreManagerHelper.getInstance().getHash());
         enumeration = dataToBeDeleted.elements();

         while (enumeration.hasMoreElements()) {
            try {
               data = (RIMKeyStoreData)enumeration.nextElement();
               this.removeKey(data, ticket);
            } catch (KeyStoreCancelException var11) {
            }
         }
      }
   }

   @Override
   public String getName() {
      return this._name;
   }

   @Override
   public boolean isMember(Certificate certificate) {
      if (certificate != null && this._vector != null) {
         this.addIndex(new CertificateKeyStoreIndex());
         return this.elements(-2038609988711824737L, certificate).hasMoreElements();
      } else {
         return false;
      }
   }

   @Override
   public boolean isMember(byte[] certificateEncoding) {
      if (certificateEncoding != null && this._vector != null) {
         Enumeration enumeration = this._vector.elements();

         while (enumeration.hasMoreElements()) {
            KeyStoreData data = (KeyStoreData)enumeration.nextElement();
            Certificate certificate = data.getCertificate();
            if (certificate != null && certificate.equals(certificateEncoding)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   @Override
   public boolean isMember(Key key) {
      if (key != null && this._vector != null) {
         if (key instanceof PublicKey) {
            Enumeration enumeration = this._vector.elements();

            while (enumeration.hasMoreElements()) {
               KeyStoreData data = (KeyStoreData)enumeration.nextElement();
               PublicKey publicKey = data.getPublicKey();
               if (publicKey != null && publicKey.equals(key)) {
                  return true;
               }
            }
         } else {
            if (!(key instanceof PrivateKey) && !(key instanceof SymmetricKey)) {
               return false;
            }

            boolean privateKey = true;
            if (key instanceof SymmetricKey) {
               privateKey = false;
            }

            Hashtable hashtable = new Hashtable();
            Enumeration enumeration = this._vector.elements();

            while (enumeration.hasMoreElements()) {
               Object obj = enumeration.nextElement();
               if (obj instanceof RIMKeyStoreData) {
                  RIMKeyStoreData data = (RIMKeyStoreData)obj;
                  String encodingAlgorithm;
                  if (privateKey && data.isPrivateKeySet()) {
                     encodingAlgorithm = data._payload._privateKeyEncodingAlgorithm;
                  } else {
                     if (privateKey || !data.isSymmetricKeySet()) {
                        continue;
                     }

                     encodingAlgorithm = data._payload._symmetricKeyEncodingAlgorithm;
                  }

                  byte[] hash = (byte[])hashtable.get(encodingAlgorithm);
                  if (hash == null) {
                     try {
                        hash = data.getHash(key);
                     } finally {
                        continue;
                     }

                     if (hash == null) {
                        continue;
                     }

                     hashtable.put(encodingAlgorithm, hash);
                  }

                  try {
                     if (privateKey) {
                        if (Arrays.equals(hash, KeyStoreUtilitiesInternal.getHash((byte[])data._payload._privateKey))) {
                           return true;
                        }
                     } else if (Arrays.equals(hash, KeyStoreUtilitiesInternal.getHash((byte[])data._payload._symmetricKey))) {
                        return true;
                     }
                  } catch (KeyStoreDecodeException e) {
                  }
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   @Override
   public boolean isMember(KeyStoreData data) {
      return this._vector == null ? false : this._vector.indexOf(data) != -1;
   }

   @Override
   public KeyStoreData set(
      AssociatedData[] associatedData,
      String label,
      PrivateKey privateKey,
      String privateKeyEncodingAlgorithm,
      int securityLevel,
      PublicKey publicKey,
      long keyUsage,
      KeyStoreTicket ticket
   ) {
      return (KeyStoreData)this.set(associatedData, label, privateKey, privateKeyEncodingAlgorithm, securityLevel, publicKey, keyUsage, null, null, ticket);
   }

   @Override
   public KeyStoreData set(
      AssociatedData[] associatedData, String label, PrivateKey privateKey, String privateKeyEncodingAlgorithm, int securityLevel, KeyStoreTicket ticket
   ) {
      return (KeyStoreData)this.set(associatedData, label, privateKey, privateKeyEncodingAlgorithm, securityLevel, null, -1, null, null, ticket);
   }

   @Override
   public KeyStoreTicket getTicket() {
      return new RIMKeyStoreTicket(null, this);
   }

   @Override
   public KeyStoreTicket getTicket(String prompt) {
      return new RIMKeyStoreTicket(prompt, this);
   }

   @Override
   public boolean checkTicket(KeyStoreTicket ticket) {
      return !(ticket instanceof RIMKeyStoreTicket) ? false : ((RIMKeyStoreTicket)ticket).access(this);
   }

   @Override
   public void addCollectionListener(Object listener) {
      this._listeners.addCollectionListener(listener);
   }

   @Override
   public void removeCollectionListener(Object listener) {
      this._listeners.removeCollectionListener(listener);
   }

   @Override
   public KeyStore getBackingKeyStore() {
      return this._keyStore;
   }

   private void addKeyStoreDataToIndices(KeyStoreData data) {
      if (this._indexHashtable != null) {
         Enumeration keys = this._indexHashtable.elements();

         while (keys.hasMoreElements()) {
            KeyStoreIndex index = (KeyStoreIndex)keys.nextElement();
            IntMultiMap map = (IntMultiMap)this._hashtable.get(index.getID());
            if (map == null) {
               map = new IntMultiMap();
            }

            KeyStoreDataMap dataMap = new KeyStoreDataMap(map);
            long id = index.getID();
            if (!this.notUsedDataExists(data, id)) {
               if (this.indexDataExists(data, id)) {
                  this.addToIndex(data, id, dataMap);
               } else {
                  index.addToIndex(data, dataMap);
               }

               this._hashtable.put(id, map);
            }
         }
      }
   }

   public RIMKeyStore(String name, KeyStore keyStore) {
      this(name, null, 0, null, keyStore);
   }

   public RIMKeyStore(String name) {
      this(name, null, 0, null, null);
   }

   private boolean notUsedDataExists(KeyStoreData data, long id) {
      return this.findId(data, false, id);
   }

   private boolean indexDataExists(KeyStoreData data, long id) {
      return this.findId(data, true, id);
   }

   private boolean findId(KeyStoreData data, boolean searchIndices, long id) {
      if (!(data instanceof RIMKeyStoreData)) {
         return false;
      }

      RIMKeyStoreData keyStoreData = (RIMKeyStoreData)data;
      long[] searchArray;
      if (searchIndices) {
         searchArray = keyStoreData._payload._indices;
      } else {
         searchArray = keyStoreData._payload._notUsed;
      }

      return searchArray == null ? false : Arrays.binarySearch(searchArray, id, 0, searchArray.length) >= 0;
   }

   private boolean addToIndex(KeyStoreData data, long id, KeyStoreDataMap dataMap) {
      if (!(data instanceof RIMKeyStoreData)) {
         return false;
      }

      RIMKeyStoreData keyStoreData = (RIMKeyStoreData)data;
      if (keyStoreData._payload._indices == null) {
         return false;
      }

      int length = keyStoreData._payload._indices.length;
      int position = Arrays.binarySearch(keyStoreData._payload._indices, id, 0, length);
      if (keyStoreData._payload._indices[position] != id) {
         throw new IllegalArgumentException();
      }

      while (position < length && keyStoreData._payload._indices[position] == id) {
         dataMap.add(keyStoreData._payload._hashes[position++], keyStoreData);
      }

      return true;
   }

   protected RIMKeyStore(String name, String className, long id, CodeSigningKey key, KeyStore keyStore, Vector vector) {
      this._hashtable = new LongHashtable();
      this._indexHashtable = new LongHashtable();
      this._name = name;
      this._keyStore = keyStore;
      this._vector = vector;
      KeyStoreManager.getInstance().register(className, id, key, this);
   }

   protected RIMKeyStore(String name, String className, long id, CodeSigningKey key, KeyStore keyStore) {
      this(name, className, id, key, keyStore, null);
   }
}
