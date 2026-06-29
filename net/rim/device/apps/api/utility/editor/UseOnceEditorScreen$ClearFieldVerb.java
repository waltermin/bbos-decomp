package net.rim.device.apps.api.utility.editor;

import net.rim.device.api.ui.component.EditField;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;

class UseOnceEditorScreen$ClearFieldVerb extends Verb {
   private final UseOnceEditorScreen this$0;

   public UseOnceEditorScreen$ClearFieldVerb(UseOnceEditorScreen _1) {
      super(16865536);
      this.this$0 = _1;
   }

   @Override
   public String toString() {
      return CommonResources.getString(400);
   }

   @Override
   public Object invoke(Object parameter) {
      if (this.this$0._modelField instanceof EditField) {
         ((EditField)this.this$0._modelField).setText("");
      }

      return null;
   }
}
