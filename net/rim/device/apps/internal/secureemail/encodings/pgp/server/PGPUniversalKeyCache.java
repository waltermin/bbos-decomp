package net.rim.device.apps.internal.secureemail.encodings.pgp.server;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateUtilities;
import net.rim.device.api.crypto.certificate.pgp.PGPCertificate;
import net.rim.device.api.crypto.certificate.x509.X509Certificate;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.crypto.keystore.KeyStoreIndex;
import net.rim.device.api.crypto.keystore.KeyStoreTicket;
import net.rim.device.api.crypto.keystore.RIMKeyStore;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.ObjectUtilities;
import net.rim.device.api.util.SimpleSortingVector;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.api.util.ToIntHashtable;
import net.rim.device.apps.internal.secureemail.KeyUsageEmailAddressKeyStoreIndex;

class PGPUniversalKeyCache extends RIMKeyStore {
   private ToIntHashtable _cachedKeyProperties;
   private SimpleSortingVector _cachedKeyExpiryData;
   public static final int CACHED_KEY_INVALID = 1;
   public static final int CACHED_KEY_NO_ENCRYPTION = 2;
   private static final long DEFAULT_KEY_CACHE_TIME = 86400000L;
   private static final boolean DEBUG = false;

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static PGPUniversalKeyCache createInstance() {
      try {
         return new PGPUniversalKeyCache();
      } catch (Throwable var2) {
         throw new Object(e.toString());
      }
   }

   private PGPUniversalKeyCache() {
      super("PGP Universal Key Cache");
      this.addIndex((KeyStoreIndex)(new Object()));
      this.addIndex((KeyStoreIndex)(new Object()));
      this.addIndex((KeyStoreIndex)(new Object()));
      this._cachedKeyProperties = (ToIntHashtable)(new Object());
      this._cachedKeyExpiryData = (SimpleSortingVector)(new Object());
      this._cachedKeyExpiryData.setSort(true);
      this._cachedKeyExpiryData.setSortComparator(new PGPUniversalKeyCache$CachedKeyExpiryComparator(null));
   }

   @Override
   public KeyStoreTicket getTicket() {
      return null;
   }

   @Override
   public boolean checkTicket(KeyStoreTicket ticket) {
      return true;
   }

   public synchronized Certificate getCachedKey(String emailAddress) {
      this.removeExpiredCachedKeys();
      Enumeration locallyCachedKeys = this.elements(3687411874034296952L, KeyUsageEmailAddressKeyStoreIndex.getAlias(1, emailAddress));

      while (locallyCachedKeys.hasMoreElements()) {
         KeyStoreData currentKeyStoreData = (KeyStoreData)locallyCachedKeys.nextElement();
         Certificate currentKey = currentKeyStoreData.getCertificate();
         int currentKeyProperties = this._cachedKeyProperties.get(currentKey);
         if ((currentKeyProperties & 1) == 0 && (currentKeyProperties & 2) == 0) {
            return currentKey;
         }
      }

      return null;
   }

   public synchronized Certificate getCachedKey(byte[] keyID, String emailAddress) {
      this.removeExpiredCachedKeys();
      Enumeration locallyCachedKeys = this.elements(-2737350786039236692L, keyID);

      while (locallyCachedKeys.hasMoreElements()) {
         KeyStoreData currentKeyStoreData = (KeyStoreData)locallyCachedKeys.nextElement();
         Certificate currentKey = currentKeyStoreData.getCertificate();
         if (emailAddress == null || this.keyStoreDataContainsAddress(currentKeyStoreData, emailAddress)) {
            return currentKey;
         }
      }

      return null;
   }

   public synchronized int getCachedKeyProperties(Certificate certificate) {
      this.removeExpiredCachedKeys();
      return this._cachedKeyProperties.get(certificate);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public synchronized void cacheKey(Certificate certificate, int certificateProperties) {
      this.removeExpiredCachedKeys();
      if (!(certificate instanceof Object)) {
         if (certificate instanceof Object) {
            X509Certificate x509Certificate = (X509Certificate)certificate;
            this._cachedKeyProperties.put(x509Certificate, certificateProperties);
            this._cachedKeyExpiryData.addElement(new PGPUniversalKeyCache$CachedKeyExpiryData(x509Certificate, null));
         }
      } else {
         PGPCertificate pgpCertificate = (PGPCertificate)certificate;
         Vector matchingKeysVector = (Vector)(new Object());
         Enumeration matchingKeysEnumeration = this.elements(-2737350786039236692L, pgpCertificate.getKeyID());

         while (matchingKeysEnumeration.hasMoreElements()) {
            matchingKeysVector.addElement(matchingKeysEnumeration.nextElement());
         }

         int numMatchingKeys = matchingKeysVector.size();

         for (int i = 0; i < numMatchingKeys; i++) {
            KeyStoreData currentKeyStoreData = (KeyStoreData)matchingKeysVector.elementAt(i);

            label113:
            try {
               this.removeKey(currentKeyStoreData, null);
            } finally {
               break label113;
            }

            this._cachedKeyProperties.remove(currentKeyStoreData.getCertificate());
            int numCachedKeyExpiryData = this._cachedKeyExpiryData.size();

            for (int j = 0; j < numCachedKeyExpiryData; j++) {
               PGPUniversalKeyCache$CachedKeyExpiryData currentCachedKeyExpiryData = (PGPUniversalKeyCache$CachedKeyExpiryData)this._cachedKeyExpiryData
                  .elementAt(j);
               if (ObjectUtilities.objEqual(currentKeyStoreData, currentCachedKeyExpiryData.getKeyStoreData())) {
                  this._cachedKeyExpiryData.removeElementAt(j);
                  break;
               }
            }
         }

         try {
            KeyStoreData keyStoreData = this.set(
               CertificateUtilities.getEmailAssociatedDataArray(pgpCertificate), pgpCertificate.getSubjectFriendlyName(), pgpCertificate, null, null
            );
            this._cachedKeyProperties.put(pgpCertificate, certificateProperties);
            this._cachedKeyExpiryData.addElement(new PGPUniversalKeyCache$CachedKeyExpiryData(pgpCertificate, keyStoreData));
         } catch (Throwable var16) {
            EventLogger.logEvent(234044482576569793L, e.toString().getBytes());
            return;
         }
      }
   }

   public synchronized void clear() {
      Vector allKeysVector = (Vector)(new Object());
      Enumeration allKeysEnumeration = this.elements();

      while (allKeysEnumeration.hasMoreElements()) {
         allKeysVector.addElement(allKeysEnumeration.nextElement());
      }

      int numAllKeys = allKeysVector.size();

      for (int i = 0; i < numAllKeys; i++) {
         KeyStoreData currentKeyStoreData = (KeyStoreData)allKeysVector.elementAt(i);

         try {
            this.removeKey(currentKeyStoreData, null);
         } finally {
            continue;
         }
      }

      this._cachedKeyProperties.clear();
      this._cachedKeyExpiryData.removeAllElements();
   }

   private synchronized void removeExpiredCachedKeys() {
      long currentTime = System.currentTimeMillis();

      while (this._cachedKeyExpiryData.size() > 0) {
         PGPUniversalKeyCache$CachedKeyExpiryData nextCachedKeyExpiryData = (PGPUniversalKeyCache$CachedKeyExpiryData)this._cachedKeyExpiryData.elementAt(0);
         if (nextCachedKeyExpiryData.getExpiryTime() > currentTime) {
            return;
         }

         KeyStoreData cachedKeyStoreData = nextCachedKeyExpiryData.getKeyStoreData();
         if (cachedKeyStoreData != null) {
            label35:
            try {
               this.removeKey(cachedKeyStoreData, null);
            } finally {
               break label35;
            }
         }

         this._cachedKeyProperties.remove(nextCachedKeyExpiryData.getCertificate());
         this._cachedKeyExpiryData.removeElementAt(0);
      }
   }

   private boolean keyStoreDataContainsAddress(KeyStoreData keyStoreData, String emailAddress) {
      Object[] emailAddressObjects = keyStoreData.getAssociatedData(-1124699153917633064L);
      if (emailAddressObjects instanceof byte[][]) {
         byte[][][] emailAddresses = (byte[][][])((byte[][])emailAddressObjects);
         int numEmailAddresses = emailAddresses.length;
         byte[] emailAddressForSearch = StringUtilities.toLowerCase(emailAddress, 1701707776).getBytes();

         for (int i = 0; i < numEmailAddresses; i++) {
            if (Arrays.equals((byte[])emailAddresses[i], emailAddressForSearch)) {
               return true;
            }
         }
      }

      return false;
   }
}
