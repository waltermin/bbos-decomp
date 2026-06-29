package net.rim.device.apps.api.utility.editor;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.i18n.CommonResource;

class UseOnceEditorScreen$CancelVerb extends Verb {
   private final UseOnceEditorScreen this$0;

   UseOnceEditorScreen$CancelVerb(UseOnceEditorScreen _1) {
      super(268500992);
      this.this$0 = _1;
   }

   @Override
   public String toString() {
      return CommonResource.getString(19);
   }

   @Override
   public Object invoke(Object parameter) {
      this.this$0._model = null;
      this.this$0._modelComplete = false;
      UiApplication.getUiApplication().popScreen(this.this$0);
      return null;
   }
}
