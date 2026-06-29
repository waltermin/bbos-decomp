package net.rim.device.api.crypto.keystore;

import net.rim.device.api.crypto.PrivateKey;
import net.rim.device.api.crypto.PublicKey;
import net.rim.device.api.crypto.SymmetricKey;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.util.Persistable;

public interface KeyStoreData extends Persistable {
   SymmetricKey getSymmetricKey(KeyStoreDataTicket var1);

   PrivateKey getPrivateKey(KeyStoreDataTicket var1);

   boolean isPrivateKeySet();

   boolean isSymmetricKeySet();

   PublicKey getPublicKey();

   Certificate getCertificate();

   void setLabel(String var1);

   String getLabel();

   void changePassword();

   int queryKeyUsage(long var1);

   byte[][][] getAssociatedData(long var1);

   AssociatedData[] getAssociatedData();

   KeyStoreDataTicket getTicket();

   KeyStoreDataTicket getTicket(String var1);

   boolean checkTicket(KeyStoreDataTicket var1);

   int getSecurityLevel();

   int getPasswordVersion();
}
