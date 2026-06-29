package net.rim.device.apps.internal.secureemail;

import java.util.Vector;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;

public class SecureEmailMailboxOptionsModelFactory extends RIMModelFactory {
   private static Vector _secureEmailEncodings = ApplicationRegistry.getApplicationRegistry().getVector(-6065380658396699831L);
   private static final long SECURE_EMAIL_OPTIONS_FACTORIES = -6065380658396699831L;

   SecureEmailMailboxOptionsModelFactory() {
   }

   @Override
   public Object createInstance(Object context) {
      int size = _secureEmailEncodings.size();
      SecureEmailOptionsModel[] optionsModels = new SecureEmailOptionsModel[size];

      for (int i = 0; i < size; i++) {
         SecureEmailFactory factory = (SecureEmailFactory)_secureEmailEncodings.elementAt(i);
         optionsModels[i] = factory.createOptionsModel(context);
      }

      return new SecureEmailMailBoxOptionsModel(optionsModels, context);
   }

   @Override
   public int getMaximumCount(Object context) {
      return 1;
   }

   @Override
   public boolean recognize(Object o) {
      return o instanceof SecureEmailMailBoxOptionsModel;
   }

   public static final void registerSecureEmailEncoding(SecureEmailFactory secureEmailFactory) {
      _secureEmailEncodings.addElement(secureEmailFactory);
   }
}
