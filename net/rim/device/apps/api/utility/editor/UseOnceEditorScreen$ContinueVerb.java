package net.rim.device.apps.api.utility.editor;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;

class UseOnceEditorScreen$ContinueVerb extends Verb {
   private final UseOnceEditorScreen this$0;

   UseOnceEditorScreen$ContinueVerb(UseOnceEditorScreen _1) {
      super(327936);
      this.this$0 = _1;
   }

   @Override
   public String toString() {
      return CommonResources.getString(800);
   }

   @Override
   public Object invoke(Object parameter) {
      if (this.this$0._fieldProvider.grabDataFromField(this.this$0._modelField, null)) {
         this.this$0._modelComplete = true;
         UiApplication.getUiApplication().popScreen(this.this$0);
         return null;
      } else {
         this.this$0._modelComplete = false;
         return null;
      }
   }
}
