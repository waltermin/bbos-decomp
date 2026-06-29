package net.rim.device.apps.internal.options;

import net.rim.device.api.ui.component.Dialog;
import net.rim.tid.im.spellcheck.SpellCheckUtilities;

class CustomDictUnitEditor$EuropeanCustomDictEditorScreen extends CustomDictUnitEditor$CustomDictEditorScreen {
   private final CustomDictUnitEditor this$0;

   public CustomDictUnitEditor$EuropeanCustomDictEditorScreen(CustomDictUnitEditor _1, String title, String initialString) {
      super(title, initialString);
      this.this$0 = _1;
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      return key == ' ' ? this.accept() : super.keyChar(key, status, time);
   }

   @Override
   protected boolean accept() {
      if (!super.accept()) {
         return false;
      }

      String newEntry = this.this$0._mainScreen.getText().trim();
      String originalReplacedString = "";
      if (newEntry.length() > 46) {
         Dialog.inform(OptionsResources.getString(2001));
         return false;
      }

      if (newEntry.equals(originalReplacedString)) {
         Dialog.inform(OptionsResources.getString(1462));
         return false;
      }

      if (this.this$0._model != null) {
         originalReplacedString = (String)this.this$0._model.getEntry();
      }

      if ((originalReplacedString.equals(newEntry) || !CustomWordlistScreen.getCustomDictionary().contains(newEntry))
         && !CustomWordlistScreen.getCustomDictionary().isInRepository(newEntry)) {
         if (!this.this$0._context.getFlag(6) && this.this$0._context.getFlag(0)) {
            CustomWordlistScreen.getCustomDictionary().remove(originalReplacedString);
         }

         CustomWordlistScreen.getCustomDictionary().add(newEntry);
         if (SpellCheckUtilities.activateSpellCheckIM()) {
            SpellCheckUtilities.getSpellCheckInputMethod().getCustomDictionary(1).add(newEntry);
            SpellCheckUtilities.deactivateSpellCheckIM();
         }

         this.close();
         return true;
      } else {
         Dialog.inform(OptionsResources.getString(1463));
         return false;
      }
   }
}
