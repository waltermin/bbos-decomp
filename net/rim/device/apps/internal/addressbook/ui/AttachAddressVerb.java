package net.rim.device.apps.internal.addressbook.ui;

import net.rim.device.apps.api.addressbook.AddressSelectionContext;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.addressbook.resources.AddressBookResources;
import net.rim.device.apps.internal.blackberryemail.email.EmailEditorScreen$AttachSmallAttachmentVerb;

final class AttachAddressVerb extends Verb implements EmailEditorScreen$AttachSmallAttachmentVerb {
   AttachAddressVerb() {
      super(16864000);
   }

   @Override
   public final String toString() {
      return AddressBookResources.getString(1600);
   }

   @Override
   public final Object invoke(Object parameter) {
      AddressSelectionContext selectionContext = (AddressSelectionContext)(new Object(
         AddressBookResources.getString(1601), null, null, new RIMModelRecognizer(), null
      ));
      return new AddressSelectionVerb().invoke(selectionContext);
   }
}
