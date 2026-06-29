package net.rim.device.apps.internal.memo;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.i18n.CommonResource;

final class MemoList$MemoOptionsVerb extends Verb {
   MemoList$MemoOptionsVerb() {
      super(16986368, CommonResource.getBundle(), 20);
   }

   @Override
   public final Object invoke(Object context) {
      MemoOptions.editOptions();
      return null;
   }
}
