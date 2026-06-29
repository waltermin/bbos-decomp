package net.rim.device.apps.internal.secureemail.server;

import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;

public class SecureEmailServerManager {
   private SecureEmailPolicyServer[] _policyServers = new SecureEmailPolicyServer[0];
   private SecureEmailCertificateServer[] _certificateServers = new SecureEmailCertificateServer[0];
   private static final long ID = 8438912290859323134L;
   private static SecureEmailServerManager _instance;

   public static SecureEmailServerManager getInstance() {
      if (_instance == null) {
         ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
         SecureEmailServerManager secureEmailServerManager = (SecureEmailServerManager)applicationRegistry.getOrWaitFor(8438912290859323134L);
         if (secureEmailServerManager == null) {
            secureEmailServerManager = new SecureEmailServerManager();
            applicationRegistry.put(8438912290859323134L, secureEmailServerManager);
         }

         _instance = secureEmailServerManager;
      }

      return _instance;
   }

   private SecureEmailServerManager() {
   }

   public void registerPolicyServer(SecureEmailPolicyServer policyServer) {
      Arrays.add(this._policyServers, policyServer);
   }

   public void registerCertificateServer(SecureEmailCertificateServer certificateServer) {
      Arrays.add(this._certificateServers, certificateServer);
   }

   public void unregisterPolicyServer(SecureEmailPolicyServer policyServer) {
      Arrays.remove(this._policyServers, policyServer);
   }

   public void unregisterCertificateServer(SecureEmailCertificateServer certificateServer) {
      Arrays.remove(this._certificateServers, certificateServer);
   }

   public SecureEmailPolicyServer[] getPolicyServers() {
      return this._policyServers;
   }

   public SecureEmailPolicyServer[] getPolicyServers(ServiceRecord serviceRecord) {
      SecureEmailPolicyServer[] relevantPolicyServers = new SecureEmailPolicyServer[0];
      int numPolicyServers = this._policyServers.length;

      for (int i = 0; i < numPolicyServers; i++) {
         SecureEmailPolicyServer currentPolicyServer = this._policyServers[i];
         if (currentPolicyServer.providesPolicyForService(serviceRecord)) {
            Arrays.add(relevantPolicyServers, currentPolicyServer);
         }
      }

      return relevantPolicyServers;
   }

   public SecureEmailCertificateServer[] getCertificateServers(ServiceRecord serviceRecord) {
      SecureEmailCertificateServer[] relevantCertificateServers = new SecureEmailCertificateServer[0];
      int numCertificateServers = this._certificateServers.length;

      for (int i = 0; i < numCertificateServers; i++) {
         SecureEmailCertificateServer currentCertificateServer = this._certificateServers[i];
         if (currentCertificateServer.providesCertificatesForService(serviceRecord)) {
            Arrays.add(relevantCertificateServers, currentCertificateServer);
         }
      }

      return relevantCertificateServers;
   }
}
