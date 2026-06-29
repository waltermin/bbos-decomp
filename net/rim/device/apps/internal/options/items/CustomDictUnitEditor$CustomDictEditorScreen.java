package net.rim.device.apps.internal.options.items;

import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.device.internal.ui.component.SimpleInputDialog;

final class CustomDictUnitEditor$CustomDictEditorScreen extends SimpleInputDialog {
   private final CustomDictUnitEditor this$0;

   CustomDictUnitEditor$CustomDictEditorScreen(CustomDictUnitEditor _1, String title, String initialString) {
      super(0, title, 0, Integer.MAX_VALUE, 4503601774854144L);
      this.this$0 = _1;
      BasicEditField editField = this.getEditField();
      editField.setCookie(editField);
      editField.setFilter(new CustomDictUnitEditor$CustomDictEditorScreen$1(this, _1));
      this.setText(initialString);
      editField.setFocus();
   }

   @Override
   protected final boolean cancel() {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   @Override
   protected final boolean accept() {
      String newEntry = this.this$0._mainScreen.getText();
      String originalReplacedString = "";
      if (newEntry.equals(originalReplacedString)) {
         Dialog.inform(OptionsResources.getString(1462));
         return false;
      }

      if (this.this$0._model != null) {
         originalReplacedString = (String)this.this$0._model.getEntry();
      }

      if (!originalReplacedString.equals(newEntry) && this.this$0._customDict.contains(newEntry)) {
         Dialog.inform(OptionsResources.getString(1463));
         return false;
      }

      if (!this.this$0._context.getFlag(6) && this.this$0._context.getFlag(0)) {
         this.this$0._customDict.remove(originalReplacedString);
      }

      this.this$0._customDict.add(newEntry);
      return super.accept();
   }

   @Override
   public final void setType(int type) {
      BasicEditField editField = this.getEditField();
      if (editField != null) {
         this.delete(editField);
      }

      editField = (BasicEditField)(new Object("", null, 127, 4503601774870528L));
      this.setEditField(editField);
      this.add(editField);
   }
}
