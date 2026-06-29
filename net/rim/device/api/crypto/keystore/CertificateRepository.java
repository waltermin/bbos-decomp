package net.rim.device.api.crypto.keystore;

import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateFactory;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Persistable;
import net.rim.vm.Array;

final class CertificateRepository implements Persistable {
   private Object[] _certificates = new Object[0];
   private String[] _types = new Object[0];
   private int[] _sequences = new int[0];
   private static final long CERTIFICATE_REPOSITORY = -775587297036677177L;
   private static final long IDLE_THRESHOLD = 10L;
   private static final long IDLE_SLEEP = 10000L;
   private static PersistentObject _persist;
   private static CertificateRepository _repository;
   private static final long SINGLETON_ID = 8427816956763268686L;

   public static final CertificateRepository getInstance() {
      if (_repository == null) {
         ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
         Object lockObject = registry.getOrWaitFor(8427816956763268686L);
         if (lockObject == null) {
            lockObject = new Object();
            registry.put(8427816956763268686L, lockObject);
         }

         synchronized (lockObject) {
            if (_repository == null) {
               _persist = RIMPersistentStore.getPersistentObject(-775587297036677177L);
               Object repository;
               synchronized (_persist) {
                  repository = _persist.getContents();
                  if (repository == null) {
                     repository = new CertificateRepository();
                     _persist.setContents(repository, 4801362);
                     _persist.commit();
                  }
               }

               _repository = (CertificateRepository)repository;
            }
         }
      }

      return _repository;
   }

   private CertificateRepository() {
   }

   public final synchronized long addCertificate(byte[] certificateEncoding, String certificateType) {
      return this.addCertificateInternal(certificateEncoding, certificateType);
   }

   public final synchronized long addCertificate(Certificate certificate) {
      return this.addCertificateInternal(certificate, certificate == null ? null : certificate.getType());
   }

   private final synchronized long addCertificateInternal(Object certificate, String certificateType) {
      if (certificate != null && certificateType != null) {
         int emptyPosition = this.findPosition();
         int position = 0;
         if (emptyPosition == -1) {
            int length = this._certificates.length + 1;
            Array.resize(this._certificates, length);
            Array.resize(this._types, length);
            Array.resize(this._sequences, length);
            position = length - 1;
         } else {
            position = emptyPosition;
         }

         this._certificates[position] = certificate;
         this._types[position] = certificateType;
         int sequence = ++this._sequences[position];
         _persist.commit();
         return ((long)sequence << 32) + position;
      } else {
         throw new Object();
      }
   }

   public final synchronized Certificate getCertificate(long index) {
      int sequence = (int)(index >> 32);
      int newIndex = (int)(index >> 0);
      if (newIndex < 0 || newIndex >= this._certificates.length) {
         return null;
      }

      if (this._certificates[newIndex] != null && sequence == this._sequences[newIndex]) {
         Object var10000 = this._certificates[newIndex];
         if (!(this._certificates[newIndex] instanceof Certificate)) {
            try {
               Certificate certificate = CertificateFactory.getInstance(this._types[newIndex], (byte[])this._certificates[newIndex]);
               this._certificates[newIndex] = certificate;
               _persist.commit();
               return certificate;
            } finally {
               ;
            }
         } else {
            return (Certificate)var10000;
         }
      } else {
         return null;
      }
   }

   public final synchronized void removeCertificate(long index) {
      int sequence = (int)(index >> 32);
      int newIndex = (int)(index >> 0);
      if (newIndex >= 0 && newIndex < this._certificates.length) {
         if (this._sequences[newIndex] == sequence) {
            this._certificates[newIndex] = null;
            this._types[newIndex] = null;
            _persist.commit();
         }
      }
   }

   final void expandCertificates() {
      int numCerts = this._certificates.length;
      int i = 0;

      while (i < numCerts) {
         while (DeviceInfo.getIdleTime() < 10) {
            try {
               Thread.sleep(10000);
            } finally {
               continue;
            }
         }

         this.expandCertificate(i++);
      }

      _persist.commit();
   }

   private final synchronized void expandCertificate(int index) {
      if (index < this._certificates.length && this._certificates[index] instanceof byte[]) {
         try {
            this._certificates[index] = CertificateFactory.getInstance(this._types[index], (byte[])this._certificates[index]);
         } finally {
            return;
         }
      }
   }

   private final int findPosition() {
      int size = this._certificates.length;

      for (int i = 0; i < size; i++) {
         if (this._certificates[i] == null) {
            return i;
         }
      }

      return -1;
   }
}
