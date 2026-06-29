package net.rim.device.apps.internal.phone.api.verbs;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.quickcontact.QuickContactUtil;
import net.rim.device.apps.api.ui.ButtonContainer;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.device.internal.i18n.CommonResource;

final class AddSpeedDialConfirmationDialog extends Dialog {
   private ButtonContainer _buttons;
   private ButtonField _okButton;
   private ButtonField _cancelButton;
   private int _answer;
   private String[] _dialogStrings;

   AddSpeedDialConfirmationDialog(String message, int keycode, Object sdData) {
      super(message, null, null, 0, Bitmap.getPredefinedBitmap(1));
      Field callerIDField = null;
      Field keyField = null;
      if (sdData instanceof FieldProvider && sdData instanceof FieldProvider) {
         callerIDField = ((FieldProvider)sdData).getField(new ContextObject(58, 34, 26));
         if (callerIDField != null) {
            this.add(callerIDField);
         }
      }

      if (keycode != 0) {
         char key = QuickContactUtil.convertKeyForCurrentKeyboard(keycode);
         key = CharacterUtilities.toUpperCase(key, 1701707776);
         String keyString = PhoneResources.getString(6080);
         String keyMsg = MessageFormat.format(keyString, new String[]{"" + key});
         keyField = new LabelField(keyMsg);
         this.add(keyField);
      }

      this._buttons = new ButtonContainer(0, null);
      this._dialogStrings = CommonResource.getStringArray(10041);
      String okString = this._dialogStrings[0];
      String cancelString = this._dialogStrings[1];
      this._okButton = new ButtonField(okString);
      this._cancelButton = new ButtonField(cancelString);
      this._buttons.addButton(this._okButton);
      this._buttons.addButton(this._cancelButton);
      this.add(this._buttons);

      try {
         FontFamily family = FontFamily.forName("BBMillbank");
         Font currentFont = Font.getDefault();
         int currentFontHeight = currentFont.getHeight();
         Font font = null;
         if (PhoneUtilities.smallScreen()) {
            font = family.getFont(0, Math.min(9, currentFontHeight));
         } else {
            font = family.getFont(0, Math.min(16, currentFontHeight));
         }

         if (font != null) {
            PhoneUtilities.updateFont(callerIDField, font);
            PhoneUtilities.updateFont(keyField, font);
            return;
         }
      } finally {
         return;
      }
   }

   private final boolean acceptInput() {
      return this.acceptInput(-1);
   }

   private final boolean acceptInput(int selectedIndex) {
      if (selectedIndex == -1) {
         Field field = this.getLeafFieldWithFocus();
         if (field == this._okButton) {
            this._answer = 1;
         }
      } else if (selectedIndex == 0) {
         this._answer = 1;
      }

      this.close();
      return true;
   }

   @Override
   public final boolean trackwheelClick(int status, int time) {
      return this.invokeAction(1);
   }

   @Override
   protected final boolean invokeAction(int action) {
      switch (action) {
         case 1:
            return this.acceptInput();
         default:
            return false;
      }
   }

   @Override
   public final boolean keyChar(char key, int status, int time) {
      if (key == 27) {
         this.close();
         return true;
      }

      if (key == '\n') {
         return this.acceptInput();
      }

      key = CharacterUtilities.toLowerCase(key, 1701707776);

      for (int i = 0; i < this._dialogStrings.length; i++) {
         String choice = this._dialogStrings[i].toString();
         int hotKeyIndex = choice.indexOf(818);
         if (hotKeyIndex > 0) {
            char hotkey = CharacterUtilities.toLowerCase(CharacterUtilities.getOriginal(choice.charAt(hotKeyIndex - 1)), 1701707776);
            if (hotkey == key) {
               return this.acceptInput(i);
            }
         }
      }

      return super.keyChar(key, status, time);
   }

   public static final boolean ask(String msg, int keycode, Object sdData) {
      AddSpeedDialConfirmationDialog dlg = new AddSpeedDialConfirmationDialog(msg, keycode, sdData);
      UiApplication.getUiApplication().pushModalScreen(dlg);
      return dlg._answer == 1;
   }
}
