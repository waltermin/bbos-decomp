package net.rim.device.apps.internal.phone.api.ui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.apps.api.framework.verb.ConditionalVerb;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.ButtonContainer;
import net.rim.device.apps.api.utility.general.Copyable;
import net.rim.device.apps.api.utility.general.SetParameter;
import net.rim.vm.Array;

public final class ConditionalVerbButtonContainer extends ButtonContainer {
   private int _paddingTop;
   private int _paddingRight;
   private int _paddingBottom;
   private int _paddingLeft;
   protected ButtonField[] _buttons;

   public ConditionalVerbButtonContainer(int top, int right, int bottom, int left) {
      super(0, getButtonFont());
      this._paddingTop = top;
      this._paddingRight = right;
      this._paddingBottom = bottom;
      this._paddingLeft = left;
      this._buttons = new Object[0];
   }

   public final void setActions(Verb[] actions) {
      this.deleteAllButtons();
      this._buttons = new Object[0];

      for (int i = 0; i < actions.length; i++) {
         Verb verb = actions[i];
         if (!(verb instanceof Object) && verb instanceof Object) {
            int index = this._buttons.length;
            Array.resize(this._buttons, index + 1);
            this._buttons[index] = this.createButton(verb);
         }
      }
   }

   private static final Font getButtonFont() {
      return Font.getDefault().derive(0, 12);
   }

   private final ButtonField createButton(Verb verb) {
      String label = verb.toString().trim();
      ButtonField button = (ButtonField)(new Object(label));
      if (verb instanceof Object) {
         button.setCookie(((Copyable)verb).copy());
      } else {
         button.setCookie(verb);
      }

      button.setPadding(this._paddingTop, this._paddingRight, this._paddingBottom, this._paddingLeft);
      return button;
   }

   public final boolean updateButtons(Object context) {
      boolean updated = false;
      int buttonIndex = 0;

      for (int idx = 0; idx < this._buttons.length; idx++) {
         Verb verb = (Verb)this._buttons[idx].getCookie();
         boolean canInvoke = ((ConditionalVerb)verb).canInvoke(context);
         if (this._buttons[idx].getManager() != null) {
            if (canInvoke) {
               buttonIndex++;
            } else {
               this.deleteButton(this._buttons[idx]);
               updated = true;
            }
         } else if (canInvoke) {
            this.insertButtonAt(this._buttons[idx], buttonIndex);
            buttonIndex++;
            updated = true;
         }
      }

      return updated;
   }

   public final Object invoke(Object context) {
      Field field = this.getLeafFieldWithFocus();
      if (field != null) {
         Object cookie = field.getCookie();
         if (cookie instanceof Object) {
            Verb verb = (Verb)cookie;
            if (verb instanceof Object) {
               ((SetParameter)verb).setParameter(context);
            }

            return verb.invoke(context);
         }
      }

      return null;
   }
}
