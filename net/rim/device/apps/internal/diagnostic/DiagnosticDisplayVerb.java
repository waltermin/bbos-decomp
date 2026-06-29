package net.rim.device.apps.internal.diagnostic;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;

final class DiagnosticDisplayVerb extends Verb {
   private static final int ORDER_VALUE = 1048576;

   DiagnosticDisplayVerb() {
      super(1048576, ResourceBundle.getBundle("net.rim.device.apps.internal.diagnostic.Diagnostics"), 1);
   }

   public final void registerVerb() {
      VerbRepository repository = VerbRepository.getVerbRepository(1479696779947759213L);
      repository.register(this, 4738722199580714034L);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final Object invoke(Object context) {
      ApplicationDescriptor descriptor = (ApplicationDescriptor)ApplicationRegistry.getApplicationRegistry().get(1548127712067364994L);
      ApplicationManager applicationManager = ApplicationManager.getApplicationManager();

      try {
         applicationManager.runApplication(descriptor, true);
         return null;
      } catch (Throwable var6) {
         System.out.println(e);
         return null;
      }
   }
}
