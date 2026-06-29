package net.rim.device.api.crypto.certificate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Hashtable;
import net.rim.device.api.crypto.NoSuchAlgorithmException;
import net.rim.device.api.crypto.RandomSource;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.CRC32;
import net.rim.device.internal.proxy.Proxy;

public class CertificateFactory {
   private static final int CACHE_RESIZE = 90;
   private static final long CACHE_CLEANUP_MILLIS = 86400000L;
   private static final int CACHE_SIZE_LIMIT = 256;
   private static final int CERTIFICATE_SIZE_LIMIT = 2048;
   private static final long HASHTABLE_ID = 6607158535140835391L;
   private static final long CONTAINER_ID = 2872272587378049473L;
   private static Hashtable _hashtable;
   private static CertificateFactoryContainer _container;

   protected CertificateFactory() {
   }

   public static Certificate getInstance(String type, InputStream stream) {
      synchronized (_container) {
         Certificate cert = createCertificateFactory(type).createCertificate(stream);
         byte[] encoding = cert.getEncoding();
         updateCache(cert, encoding, findInCache(type, encoding));
         return cert;
      }
   }

   public static Certificate getInstance(String type, byte[] encoding) {
      synchronized (_container) {
         int index = findInCache(type, encoding);
         Certificate cert;
         byte[] certEncoding;
         if (index >= 0) {
            cert = _container._certificates[index];
            certEncoding = _container._encodings[index];
         } else {
            cert = createCertificateFactory(type).createCertificate(encoding);
            certEncoding = encoding;
         }

         updateCache(cert, certEncoding, index);
         return cert;
      }
   }

   private static CertificateFactory createCertificateFactory(String type) throws NoSuchAlgorithmException {
      CertificateFactory factory = (CertificateFactory)_hashtable.get(type);
      if (factory == null) {
         throw new NoSuchAlgorithmException(type);
      } else {
         return factory;
      }
   }

   private static int findInCache(String type, byte[] encoding) {
      int checkValue = CRC32.update(-1, encoding);

      for (int i = 0; i < _container._numCerts; i++) {
         if (_container._crcs[i] == checkValue
            && _container._certificates[i] != null
            && Arrays.equals(_container._encodings[i], encoding)
            && _container._certificates[i].getType().equals(type)) {
            return i;
         }
      }

      return -1;
   }

   private static void updateCache(Certificate cert, byte[] encoding, int index) {
      if (index >= 256) {
         throw new IllegalArgumentException();
      }

      if (encoding.length <= 2048) {
         if (index < 0) {
            if (_container._numCerts <= 255) {
               index = _container._numCerts++;
            } else {
               index = 255;
            }
         }

         System.arraycopy(_container._certificates, 0, _container._certificates, 1, index);
         _container._certificates[0] = cert;
         System.arraycopy(_container._encodings, 0, _container._encodings, 1, index);
         _container._encodings[0] = encoding;
         System.arraycopy(_container._crcs, 0, _container._crcs, 1, index);
         _container._crcs[0] = CRC32.update(-1, encoding);
      }
   }

   static void cleanCache() {
      synchronized (_container) {
         if (_container._numCerts > 0) {
            int newSize = _container._numCerts * 90 / 100 + 1;

            for (int i = newSize; i < _container._numCerts; i++) {
               _container._certificates[i] = null;
               _container._encodings[i] = null;
               _container._crcs[i] = 0;
            }

            _container._numCerts = newSize;
         }
      }
   }

   private static void swapPosition(int i, int j) {
      Certificate cert = _container._certificates[i];
      byte[] encoding = _container._encodings[i];
      int crc = _container._crcs[i];
      _container._certificates[i] = _container._certificates[j];
      _container._encodings[i] = _container._encodings[j];
      _container._crcs[i] = _container._crcs[j];
      _container._certificates[j] = cert;
      _container._encodings[j] = encoding;
      _container._crcs[j] = crc;
   }

   static void randomizeCache() {
      synchronized (_container) {
         int numCerts = _container._numCerts;

         for (int i = 0; i < numCerts; i++) {
            int j = RandomSource.getInt(numCerts);
            swapPosition(i, j);
         }
      }
   }

   public static boolean register(CertificateFactory factory) {
      if (factory == null) {
         throw new IllegalArgumentException();
      }

      String type = factory.getType();
      if (_hashtable.containsKey(type)) {
         return false;
      }

      _hashtable.put(type, factory);
      return true;
   }

   protected String getType() {
      throw null;
   }

   protected Certificate createCertificate(InputStream _1) {
      throw null;
   }

   protected Certificate createCertificate(byte[] encoding) {
      return this.createCertificate(new ByteArrayInputStream(encoding));
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _hashtable = ar.getHashtable(6607158535140835391L);
      _container = (CertificateFactoryContainer)ar.getOrWaitFor(2872272587378049473L);
      if (_container == null) {
         _container = new CertificateFactoryContainer(256);
         ar.put(2872272587378049473L, _container);
         Proxy proxy = Proxy.getInstance();
         proxy.invokeLater(new CleanCache(), 86400000, true);
         proxy.addGlobalEventListener(new RandomizeCache());
      }
   }
}
