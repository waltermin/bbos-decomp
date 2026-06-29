package net.rim.device.apps.internal.phone.api.verbs;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.ObjectUtilities;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.model.PhoneNumberModel;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

public final class InitiateCallVerb extends Verb implements PhoneVerb {
   private static final int DEFAULT_DESCRIPTION = 421;
   private static final int DEFAULT_ORDERING = 1266768;
   private static final long GUID = -7067766573164335547L;
   private static Verb _instance;

   private InitiateCallVerb() {
      this(421, 1266768);
   }

   public InitiateCallVerb(int descriptionResId, int menuOrdering) {
      super(menuOrdering, PhoneResources.getResourceBundle(), descriptionResId);
   }

   public static final Verb getSingleton() {
      return _instance;
   }

   @Override
   public final Object invoke(Object parameter) {
      if (parameter instanceof PhoneNumberModel) {
         Object connectionParams = PhoneUtilities.getCallConnectionParameters(parameter, null, null, null);
         OutgoingCallConnector.startCall(connectionParams);
         return null;
      } else {
         AddressBookServices.setLastSelectedAddress(ContextObject.get(parameter, 250));
         PhoneUtilities.callNumberFromAddressBook(null, null, false);
         return null;
      }
   }

   @Override
   public final boolean equals(Object o) {
      if (this == o) {
         return true;
      } else {
         return o instanceof InitiateCallVerb ? ObjectUtilities.classesEqual(this, o) : false;
      }
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _instance = (InitiateCallVerb)ar.getOrWaitFor(-7067766573164335547L);
      if (_instance == null) {
         _instance = new InitiateCallVerb();
         ar.put(-7067766573164335547L, _instance);
      }
   }
}
