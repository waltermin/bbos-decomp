package net.rim.device.api.crypto.certificate;

import java.util.Hashtable;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreTicket;

public class CertificateImporterFactory$CertificateImporterContext {
   public boolean _verifyRootsOnImport = true;
   public int _trustCertificate = 2;
   public int _importEmbeddedCertificates = 2;
   private Hashtable _keyStoreTickets = (Hashtable)(new Object());
   public static final int NO = 0;
   public static final int YES = 1;
   public static final int PROMPT = 2;

   protected CertificateImporterFactory$CertificateImporterContext() {
   }

   public void addKeyStoreTicket(KeyStore keyStore, KeyStoreTicket keyStoreTicket) {
      if (keyStoreTicket != null) {
         this._keyStoreTickets.put(keyStore, keyStoreTicket);
      }
   }

   public KeyStoreTicket obtainKeyStoreTicket(KeyStore keyStore) {
      KeyStoreTicket keyStoreTicket = (KeyStoreTicket)this._keyStoreTickets.get(keyStore);
      if (keyStoreTicket == null || !keyStore.checkTicket(keyStoreTicket)) {
         keyStoreTicket = keyStore.getTicket();
         this._keyStoreTickets.put(keyStore, keyStoreTicket);
      }

      return keyStoreTicket;
   }
}
