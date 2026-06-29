package net.rim.device.apps.internal.messaging.search;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.ChoiceField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.internal.messaging.search.resources.SearchResources;

public final class KeyChoiceField extends ChoiceField {
   private Object[] _otherChoices;
   private Character _startChoice;
   private int _startIndex;
   private static MenuItem _clearFieldItem = new KeyChoiceField$1(
      ResourceBundle.getBundle(-8414468493733347764L, "net.rim.device.apps.internal.resource.Common"), 400, 16865536, Integer.MAX_VALUE
   );

   public KeyChoiceField(String label, Object[] otherChoices, Character initialValue) {
      super(label, otherChoices.length + 1, 0);
      this._otherChoices = otherChoices;
      this._startChoice = initialValue;

      for (int i = 1; i < this._otherChoices.length; i++) {
         Character ch = (Character)this._otherChoices[i];
         if (initialValue < ch) {
            this._startIndex = i;
            this.setSelectedIndex(i);
            return;
         }
      }

      this._startIndex = this._otherChoices.length;
      this.setSelectedIndex(this._startIndex);
   }

   public KeyChoiceField(String label, Object[] keyChoices, int initialIx) {
      super(label, keyChoices.length, 0);
      this._otherChoices = keyChoices;
      this.setSelectedIndex(initialIx);
      this._startIndex = keyChoices.length + 1;
   }

   @Override
   public final Object getChoice(int index) {
      if (index < this._startIndex) {
         return this._otherChoices[index];
      } else {
         return index == this._startIndex ? this._startChoice : this._otherChoices[index - 1];
      }
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (super.keyChar(key, status, time)) {
         return true;
      }

      if ((status & 17) == 0) {
         key = Character.toLowerCase(key);
         if (key >= 'a' && key <= 'z' && key != Character.toLowerCase(this.getChoice(this.getSelectedIndex()).toString().charAt(0))) {
            Dialog.inform(SearchResources.getString(43));
            return true;
         }
      }

      return false;
   }

   @Override
   protected final void makeContextMenu(ContextMenu contextMenu) {
      super.makeContextMenu(contextMenu);
      contextMenu.addItem(_clearFieldItem);
   }
}
