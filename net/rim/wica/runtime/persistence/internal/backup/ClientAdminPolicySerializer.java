package net.rim.wica.runtime.persistence.internal.backup;

public final class ClientAdminPolicySerializer extends AbstractSerializer {
   private static ClientAdminPolicySerializer _instance;

   public static final ClientAdminPolicySerializer getInstance() {
      if (_instance == null) {
         _instance = new ClientAdminPolicySerializer();
      }

      return _instance;
   }

   public static final void nullInstance() {
      _instance = null;
   }

   private ClientAdminPolicySerializer() {
   }
}
