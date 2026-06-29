package net.rim.device.api.crypto.tls;

import javax.microedition.io.SecureConnection;
import javax.microedition.io.StreamConnection;
import net.rim.device.api.crypto.PrivateKey;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.tls.server.TLSServer;
import net.rim.device.api.crypto.tls.ssl30.SSL30Connection;
import net.rim.device.api.crypto.tls.tls10.TLS10Connection;
import net.rim.device.cldc.io.ssl.ConnectionFactory;

public class TLSConnectionFactory implements ConnectionFactory {
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public SecureConnection createInstance(String algorithm, StreamConnection subConnection, String name, boolean startHandshake) {
      try {
         if (algorithm.equals("TLS")) {
            return new TLS10Connection(subConnection, name, startHandshake);
         } else if (algorithm.equals("SSL")) {
            return new SSL30Connection(subConnection, name, startHandshake);
         } else {
            throw new Object();
         }
      } catch (Throwable var7) {
         throw new Object(e);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public SecureConnection createServerInstance(StreamConnection subConnection, String name, Object certificate, Object privateKey) {
      try {
         return new TLSServer(subConnection, name, (Certificate)certificate, (PrivateKey)privateKey);
      } catch (Throwable var7) {
         throw new Object(e);
      }
   }
}
