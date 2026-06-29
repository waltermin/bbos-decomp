package net.rim.blackberry.api.blackberrymessenger;

import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.ControlledAccess;

public class BlackBerryMessenger {
   private static final long GUID = 4927202520515498096L;

   protected BlackBerryMessenger() {
      ControlledAccess.assertRRISignatures(true);
   }

   public static BlackBerryMessenger getInstance() {
      return (BlackBerryMessenger)ApplicationRegistry.getApplicationRegistry().get(4927202520515498096L);
   }

   public MessengerContact chooseContact() {
      throw null;
   }

   public MessengerContact getMyContactInfo() {
      throw null;
   }

   public void registerService(Service _1, String _2, ApplicationDescriptor _3) {
      throw null;
   }

   public void deregisterService(Service _1) {
      throw null;
   }

   public void addSessionRequestListener(SessionRequestListener _1, ApplicationDescriptor _2) {
      throw null;
   }

   public void removeSessionRequestListener(SessionRequestListener _1) {
      throw null;
   }
}
