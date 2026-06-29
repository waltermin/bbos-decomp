package net.rim.device.api.crypto.certificate;

import net.rim.device.api.crypto.asn1.ASN1EncodingException;
import net.rim.device.api.crypto.certificate.x509.X509DistinguishedName;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.crypto.keystore.KeyStoreDataMap;
import net.rim.device.api.crypto.keystore.KeyStoreIndex;

public class SerialNumberIssuerKeyStoreIndex implements KeyStoreIndex {
   public static final long ID = -6470299966859493514L;

   @Override
   public void addToIndex(KeyStoreData data, KeyStoreDataMap dataMap) {
      Certificate certificate = data.getCertificate();
      if (certificate != null) {
         SerialNumberIssuerKeyStoreIndex$Alias newAlias = new SerialNumberIssuerKeyStoreIndex$Alias(certificate.getSerialNumber(), certificate.getIssuer());
         dataMap.add(newAlias.hashCode(), data);
      } else {
         SerialNumberIssuerKeyStoreIndex$Alias newAlias = this.getAlias(data);
         if (newAlias != null) {
            dataMap.add(newAlias.hashCode(), data);
         }
      }
   }

   @Override
   public int getHash(Object target) {
      if (target instanceof SerialNumberIssuerKeyStoreIndex$Alias) {
         return target.hashCode();
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public boolean matches(KeyStoreData data, Object target) {
      if (target instanceof SerialNumberIssuerKeyStoreIndex$Alias) {
         SerialNumberIssuerKeyStoreIndex$Alias alias = (SerialNumberIssuerKeyStoreIndex$Alias)target;
         Certificate cert = data.getCertificate();
         if (cert != null) {
            SerialNumberIssuerKeyStoreIndex$Alias newAlias = new SerialNumberIssuerKeyStoreIndex$Alias(cert.getSerialNumber(), cert.getIssuer());
            return alias.equals(newAlias);
         }

         SerialNumberIssuerKeyStoreIndex$Alias newAlias = this.getAlias(data);
         if (newAlias != null) {
            return alias.equals(newAlias);
         }
      }

      return false;
   }

   private SerialNumberIssuerKeyStoreIndex$Alias getAlias(KeyStoreData data) {
      try {
         byte[][] aData = data.getAssociatedData(5689852616259641725L);
         if (aData != null) {
            X509DistinguishedName issuer = new X509DistinguishedName(aData[0]);
            byte[][] bData = data.getAssociatedData(7970222113131699770L);
            if (bData != null) {
               return new SerialNumberIssuerKeyStoreIndex$Alias(bData[0], issuer);
            }
         }
      } catch (ASN1EncodingException var5) {
      }

      return null;
   }

   @Override
   public long getID() {
      return -6470299966859493514L;
   }

   public static Object getAlias(byte[] serialNumber, DistinguishedName issuer) {
      return new SerialNumberIssuerKeyStoreIndex$Alias(serialNumber, issuer);
   }
}
