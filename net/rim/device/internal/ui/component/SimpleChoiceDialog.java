package net.rim.device.internal.ui.component;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.i18n.MessageFormatUtilities;
import net.rim.tid.awt.im.InputContext;
import net.rim.tid.im.layout.SLKeyLayout;

public class SimpleChoiceDialog extends PopupDialog implements FieldChangeListener {
   private DialogFieldManager _dfm = (DialogFieldManager)this.getDelegate();
   private int _selectedIndex;
   private Object[] _choices;
   private int _defaultChoice;

   public void select(int index) {
      this._selectedIndex = index;
      this.close(0);
   }

   public int getSelectedIndex() {
      return this._selectedIndex;
   }

   protected void select() {
      Field field = this.getLeafFieldWithFocus();
      if (field != null) {
         this.select(field.getIndex());
      }
   }

   @Override
   public void fieldChanged(Field field, int context) {
      if (field instanceof ButtonField) {
         this.select();
      }
   }

   @Override
   protected void onUiEngineAttached(boolean attached) {
      if (attached && this._dfm.getCustomManager().getFieldCount() > 0) {
         this._dfm.getCustomField(Math.max(0, this._defaultChoice)).setFocus();
      }

      super.onUiEngineAttached(attached);
   }

   public SimpleChoiceDialog(String message, Object[] choices, int defaultChoice, Bitmap bitmap, long style) {
      this(new RichTextField(message, 36028797086072832L), choices, defaultChoice, bitmap, style);
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      boolean handled = super.keyChar(key, status, time);
      if (!handled) {
         if (key == '\n') {
            this.select();
            return true;
         }

         if (key == 27 && this.isCancelAllowed()) {
            this.close(-1);
         } else {
            key = Character.toLowerCase(key);
            if (this._choices != null) {
               String chars = null;
               if (InputContext.getInstance(false).isSureType()) {
                  StringBuffer temp = Keypad.getLayout().getComplementaryChars(key, SLKeyLayout.convertStatusToModifiers(status));
                  if (temp != null) {
                     chars = temp.toString();
                  }
               }

               for (int item = 0; item < this._choices.length; item++) {
                  String choice = this._choices[item].toString();
                  int hotposition = choice.indexOf(818);
                  if (hotposition > 0) {
                     char hotkey = Character.toLowerCase(CharacterUtilities.getOriginal(choice.charAt(hotposition - 1)));
                     if (hotkey == key || chars != null && chars.indexOf(hotkey) != -1) {
                        this.select(item);
                        break;
                     }
                  }
               }
            }
         }
      }

      return handled;
   }

   public SimpleChoiceDialog(RichTextField message, Object[] choices, int defaultChoice, Bitmap bitmap, long style) {
      super(new DialogFieldManager(), style);
      this._dfm.setMessage(message);
      if (choices != null) {
         this._choices = choices;
         int numChoices = choices.length;

         for (int i = 0; i < numChoices; i++) {
            ButtonField button = new ButtonField(choices[i].toString(), 12884901888L);
            button.setChangeListener(this);
            this._dfm.addCustomField(button);
         }
      }

      this._defaultChoice = defaultChoice;
      if (bitmap != null) {
         this._dfm.setIcon(new BitmapField(bitmap, 32));
      }
   }

   public SimpleChoiceDialog(String message, Object[] choices, int defaultChoice, Bitmap bitmap) {
      this(message, choices, defaultChoice, bitmap, 0);
   }

   @Override
   protected boolean trackwheelClick(int status, int time) {
      super.trackwheelClick(status, time);
      this.select();
      return true;
   }

   public static boolean askYesNoQuestion(String question) {
      return askYesNoQuestion(question, null);
   }

   public static boolean askYesNoQuestion(String question, String boldArgument) {
      SimpleChoiceDialog dialog = createYesNoQuestionDialog(question, boldArgument, 0);
      dialog.show();
      return dialog.getCloseReason() == -1 ? false : dialog.getSelectedIndex() == 0;
   }

   public static boolean askYesNoQuestionOnBackground(String question) {
      return askYesNoQuestionOnBackground(question, null);
   }

   public static boolean askYesNoQuestionOnBackground(String question, String boldArgument) {
      SimpleChoiceDialog dialog = createYesNoQuestionDialog(question, boldArgument, 134217728);
      BackgroundDialog.show(dialog);
      return dialog.getCloseReason() == -1 ? false : dialog.getSelectedIndex() == 0;
   }

   private static SimpleChoiceDialog createYesNoQuestionDialog(String question, String boldArgument, long style) {
      RichTextField messageField = boldArgument == null
         ? new RichTextField(question, 45035996273704960L)
         : MessageFormatUtilities.getBoldArgumentField(question, new String[]{boldArgument});
      String[] yesNo = CommonResource.getStringArray(10012);
      Bitmap bitmap = Bitmap.getPredefinedBitmap(1);
      return new SimpleChoiceDialog(messageField, yesNo, 1, bitmap, style);
   }
}
