package net.rim.device.apps.internal.secureemail.encodings.pgp.server;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.internal.api.crypto.CryptoApplicationProperties;
import net.rim.device.apps.internal.ldap.LDAPBrowserContextFactory;
import net.rim.device.apps.internal.secureemail.encodings.SecureEmailEncodingManager;
import net.rim.device.apps.internal.secureemail.encodings.pgp.PGPFactory;
import net.rim.device.apps.internal.secureemail.encodings.pgp.PGPUtilities;
import net.rim.device.apps.internal.secureemail.server.SecureEmailPolicyServer;
import net.rim.device.apps.internal.secureemail.server.SecureEmailServerManager;
import net.rim.device.internal.proxy.Proxy;

public class PGPUniversalAuthenticationListener implements GlobalEventListener {
   private Object _threadWaitingLock = new Object();
   private boolean _threadWaiting;

   public synchronized void verifyRequiredServerThread() {
      synchronized (this._threadWaitingLock) {
         this._threadWaiting = false;
      }

      String[] serviceUIDArray = new Object[0];
      String serviceUIDString = ITPolicy.getString(24, 51);
      if (serviceUIDString != null) {
         StringTokenizer tokenizer = (StringTokenizer)(new Object(serviceUIDString, ','));

         while (tokenizer.hasMoreTokens()) {
            Arrays.add(serviceUIDArray, tokenizer.nextToken());
         }
      }

      PGPUtilities pgpUtilities = (PGPUtilities)PGPFactory.getInstance().getUtilities();
      String requiredServerAddress = pgpUtilities.getUniversalServerAddress(null);
      PGPUniversalServer requiredServer = null;
      SecureEmailServerManager secureEmailServerManager = SecureEmailServerManager.getInstance();
      SecureEmailPolicyServer[] secureEmailPolicyServers = secureEmailServerManager.getPolicyServers();
      int numSecureEmailPolicyServers = secureEmailPolicyServers.length;

      for (int i = 0; i < numSecureEmailPolicyServers; i++) {
         SecureEmailPolicyServer var10000 = secureEmailPolicyServers[i];
         if (secureEmailPolicyServers[i] instanceof PGPUniversalServer) {
            PGPUniversalServer currentUniversalServer = (PGPUniversalServer)var10000;
            if (StringUtilities.strEqualIgnoreCase(requiredServerAddress, currentUniversalServer.getURL(), 1701707776)
               && this.arrayContentsEqualIgnoreCase(serviceUIDArray, currentUniversalServer.getServiceUIDs())) {
               requiredServer = currentUniversalServer;
            } else {
               EventLogger.logEvent(234044482576569793L, 1431662194);
               currentUniversalServer.uninitialize();
               secureEmailServerManager.unregisterPolicyServer(currentUniversalServer);
               secureEmailServerManager.unregisterCertificateServer(currentUniversalServer);
               SecureEmailEncodingManager secureEmailEncodingManager = SecureEmailEncodingManager.getInstance();
               if (secureEmailEncodingManager.isRegistered(5942148136637320404L)) {
                  LDAPBrowserContextFactory.addLDAPBrowserEntryPoints("X509");
               }

               if (secureEmailEncodingManager.isRegistered(3681505275764314063L)) {
                  LDAPBrowserContextFactory.addLDAPBrowserEntryPoints("PGP");
               }

               CryptoApplicationProperties.getInstance().clearFlags(1);
            }
         }
      }

      if (requiredServer == null && requiredServerAddress != null && requiredServerAddress.length() != 0) {
         EventLogger.logEvent(234044482576569793L, 1431463271);
         requiredServer = new PGPUniversalServer(requiredServerAddress, serviceUIDArray);
         secureEmailServerManager.registerPolicyServer(requiredServer);
         secureEmailServerManager.registerCertificateServer(requiredServer);
         LDAPBrowserContextFactory.removeLDAPBrowserEntryPoints("X509");
         LDAPBrowserContextFactory.removeLDAPBrowserEntryPoints("PGP");
         CryptoApplicationProperties.getInstance().setFlags(1);
         requiredServer.initialize(null);
      }
   }

   public void verifyRequiredServer() {
      synchronized (this._threadWaitingLock) {
         if (!this._threadWaiting) {
            this._threadWaiting = true;
            Proxy.getInstance().startThread(new PGPUniversalAuthenticationListener$1(this));
         }
      }
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 8508406279413621091L || guid == -594020114676189989L) {
         this.verifyRequiredServer();
      }
   }

   private boolean arrayContentsEqualIgnoreCase(String[] array1, String[] array2) {
      int length = array1.length;
      if (length != array2.length) {
         return false;
      }

      for (int i = 0; i < length; i++) {
         if (!this.containsIgnoreCase(array1[i], array2)) {
            return false;
         }
      }

      return true;
   }

   private boolean containsIgnoreCase(String string, String[] array) {
      int length = array.length;

      for (int i = 0; i < length; i++) {
         if (StringUtilities.strEqualIgnoreCase(string, array[i], 1701707776)) {
            return true;
         }
      }

      return false;
   }
}
