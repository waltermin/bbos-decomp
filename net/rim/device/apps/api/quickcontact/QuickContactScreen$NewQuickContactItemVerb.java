package net.rim.device.apps.api.quickcontact;

import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;

final class QuickContactScreen$NewQuickContactItemVerb extends Verb {
   private char _key;
   private final QuickContactScreen this$0;

   QuickContactScreen$NewQuickContactItemVerb(QuickContactScreen _1, char key) {
      super(65536);
      this.this$0 = _1;
      this._key = key;
   }

   @Override
   public final String toString() {
      return CommonResources.getString(11);
   }

   @Override
   public final Object invoke(Object parameter) {
      VerbRepository vr = VerbRepository.getVerbRepository(3391562901592837683L);
      Object newItem = this.this$0._quickContactList.getNewQuickContactItem(null, vr.getVerbs(null));
      if (newItem instanceof QuickContactItem) {
         QuickContactItem qcItem = (QuickContactItem)newItem;
         qcItem.setKey(this._key);
         this.this$0._quickContactList.add(qcItem);
      }

      return null;
   }
}
