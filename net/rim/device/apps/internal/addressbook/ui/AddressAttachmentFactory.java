package net.rim.device.apps.internal.addressbook.ui;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;
import net.rim.device.apps.api.framework.registration.RIMModelFactoryRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.blackberryemail.email.EmailTransport;

final class AddressAttachmentFactory extends RIMModelFactory {
   private Verb[] _attachAddressVerbs;

   @Override
   public final Object createInstance(Object initialData) {
      return null;
   }

   @Override
   public final boolean recognize(Object object) {
      RIMModelFactory[] factories = RIMModelFactoryRepository.getModelFactories(-7921492803965144520L);
      int count = factories.length;

      for (int i = 0; i < count; i++) {
         if (factories[i].recognize(object)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public final int getMinimumCount(Object context) {
      return Integer.MIN_VALUE;
   }

   @Override
   public final Verb[] getVerbs(Object context) {
      if (ContextObject.getFlag(context, 43)) {
         EmailTransport et = (EmailTransport)ContextObject.get(context, -7627884060408300435L);
         if (et != null && !et.isAttachmentSupported(252)) {
            return null;
         }

         if (this._attachAddressVerbs == null) {
            this._attachAddressVerbs = new Verb[]{new AttachAddressVerb()};
         }

         return this._attachAddressVerbs;
      } else {
         return null;
      }
   }
}
