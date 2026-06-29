package net.rim.device.api.crypto.keystore;

import java.util.Enumeration;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.crypto.Key;
import net.rim.device.api.crypto.PrivateKey;
import net.rim.device.api.crypto.PublicKey;
import net.rim.device.api.crypto.SymmetricKey;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateStatus;

public interface KeyStore extends Collection, CollectionEventSource {
   int SECURITY_LEVEL_HIGH = 2;
   int SECURITY_LEVEL_LOW = 1;
   int SECURITY_LEVEL_MEDIUM = 3;
   int SECURITY_LEVEL_NOT_APPLICABLE = 0;

   KeyStoreData set(AssociatedData[] var1, String var2, PrivateKey var3, String var4, int var5, KeyStoreTicket var6);

   KeyStoreData set(AssociatedData[] var1, String var2, PrivateKey var3, String var4, int var5, PublicKey var6, long var7, KeyStoreTicket var9);

   KeyStoreData set(AssociatedData[] var1, String var2, PrivateKey var3, String var4, int var5, Certificate var6, CertificateStatus var7, KeyStoreTicket var8);

   KeyStoreData set(AssociatedData[] var1, String var2, PublicKey var3, long var4, KeyStoreTicket var6);

   KeyStoreData set(AssociatedData[] var1, String var2, Certificate var3, CertificateStatus var4, KeyStoreTicket var5);

   KeyStoreData set(AssociatedData[] var1, String var2, SymmetricKey var3, String var4, int var5, KeyStoreTicket var6);

   Enumeration elements(long var1, Object var3);

   Enumeration elements(long var1, Object var3, boolean var4);

   void removeKey(KeyStoreData var1, KeyStoreTicket var2);

   Enumeration elements();

   Enumeration elements(boolean var1);

   Enumeration elements(long var1);

   Enumeration elements(long var1, boolean var3);

   boolean exists(long var1, Object var3);

   boolean addIndex(KeyStoreIndex var1);

   void addIndices(KeyStoreIndex[] var1);

   void removeIndex(long var1);

   boolean existsIndex(long var1);

   int size();

   void changePassword();

   KeyStoreTicket getTicket();

   KeyStoreTicket getTicket(String var1);

   boolean checkTicket(KeyStoreTicket var1);

   String getName();

   boolean isMember(Certificate var1);

   boolean isMember(byte[] var1);

   boolean isMember(Key var1);

   boolean isMember(KeyStoreData var1);

   @Override
   void addCollectionListener(Object var1);

   @Override
   void removeCollectionListener(Object var1);

   KeyStore getBackingKeyStore();
}
