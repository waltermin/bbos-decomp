package net.rim.device.apps.internal.options.items;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.i18n.CommonResource;

final class AutoTextOptionsItem$NewAutoTextUnitVerb extends Verb {
   private final AutoTextOptionsItem this$0;

   public AutoTextOptionsItem$NewAutoTextUnitVerb(AutoTextOptionsItem _1) {
      super(628224, CommonResource.getBundle(), 13);
      this.this$0 = _1;
   }

   @Override
   public final Object invoke(Object parameter) {
      AutoTextUnitEditor editor = new AutoTextUnitEditor();
      return editor.open(this.this$0._autoTextScreen.getFinderFieldText());
   }
}
