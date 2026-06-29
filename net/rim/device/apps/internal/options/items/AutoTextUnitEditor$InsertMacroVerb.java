package net.rim.device.apps.internal.options.items;

import net.rim.device.api.ui.autotext.AutoText;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.options.resources.OptionsResources;

final class AutoTextUnitEditor$InsertMacroVerb extends Verb {
   private EditField _editField;

   public AutoTextUnitEditor$InsertMacroVerb(EditField editField) {
      super(16867584, OptionsResources.getResourceBundle(), 310);
      this._editField = editField;
   }

   @Override
   public final Object invoke(Object parameter) {
      int macroIndex = Dialog.ask(OptionsResources.getString(312), AutoText.getAutoText().getMacroChoices(), 0);
      if (macroIndex != -1) {
         this._editField.insert(AutoText.getAutoText().getMacroText((short)macroIndex));
         this._editField.setDirty(true);
      }

      return null;
   }
}
