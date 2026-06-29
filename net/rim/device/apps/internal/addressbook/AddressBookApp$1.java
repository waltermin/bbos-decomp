package net.rim.device.apps.internal.addressbook;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.addressbook.resources.AddressBookResources;
import net.rim.device.apps.internal.addressbook.ui.AddressBookListUI;

final class AddressBookApp$1 implements Runnable {
   private final String val$arg;
   private final AddressBookApp this$0;

   AddressBookApp$1(AddressBookApp _1, String _2) {
      this.this$0 = _1;
      this.val$arg = _2;
   }

   @Override
   public final void run() {
      if (this.val$arg != null && this.val$arg.equals("compose")) {
         VerbRepository useOnceVerbRepository = VerbRepository.getVerbRepository(8016149483483360697L);
         Verb[] useOnceVerbs = useOnceVerbRepository.getVerbs(null);
         this.this$0.pushScreen(AddressBookListUI.getInstance(AddressBookResources.getString(53), useOnceVerbs, new ContextObject(7)));
      } else {
         this.this$0.pushScreen(AddressBookListUI.getInstance(null));
      }
   }
}
