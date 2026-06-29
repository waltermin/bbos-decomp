package net.rim.device.apps.api.setupwizard;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.api.ui.theme.ThemeManager;

public class WizardDialog extends Dialog {
   boolean _warnOnSoftKeys;
   boolean _isWaitingDialog = false;
   private static final int DIALOG_FONT_SIZE = 7;

   public WizardDialog(int type, String message, int defaultChoice, Bitmap bitmap, long style, boolean warnOnSoftKeys) {
      super(type, "", defaultChoice, bitmap, style);
      this._warnOnSoftKeys = warnOnSoftKeys;
      DialogFieldManager dialogManager = (DialogFieldManager)this.getDelegate();
      dialogManager.setMessage(createDialogRichTextField(message, true, 36028797019226112L));
      dialogManager.getButtonManager().setFont(defineFont());
   }

   public WizardDialog(String message, Object[] choices, int[] values, int defaultChoice, Bitmap bitmap, boolean warnOnSoftKeys) {
      super("", null, values, defaultChoice, bitmap);
      this._warnOnSoftKeys = warnOnSoftKeys;
      DialogFieldManager dialogManager = (DialogFieldManager)this.getDelegate();
      dialogManager.setMessage(createDialogRichTextField(message, true, 36028797019226112L));
      if (choices != null && choices.length > 0) {
         ButtonField[] buttons = new Object[choices.length];

         for (int i = 0; i < buttons.length; i++) {
            if (choices[i] != null) {
               buttons[i] = createDialogButtonField((String)choices[i]);
               buttons[i].setChangeListener(this);
               dialogManager.addCustomField(buttons[i]);
            }
         }
      } else {
         this.setIcon(ThemeManager.getThemeAwareImage("dialog_information"));
      }
   }

   @Override
   public boolean keyDown(int keycode, int time) {
      return WizardKeyEventManager.processKeyEvent(513, keycode, time, this._warnOnSoftKeys) ? true : super.keyDown(keycode, time);
   }

   @Override
   public boolean keyRepeat(int keycode, int time) {
      return WizardKeyEventManager.processKeyEvent(514, keycode, time, this._warnOnSoftKeys) ? true : super.keyRepeat(keycode, time);
   }

   @Override
   public boolean keyUp(int keycode, int time) {
      return WizardKeyEventManager.processKeyEvent(515, keycode, time, this._warnOnSoftKeys) ? true : super.keyUp(keycode, time);
   }

   public static int ask(int type, String message, boolean warnOnSoftKeys) {
      return ask(type, message, Dialog.getResourceDefaultValue(type), warnOnSoftKeys);
   }

   public static int ask(int type, String message, int defaultChoice, boolean warnOnSoftKeys) {
      WizardDialog d = new WizardDialog(type, message, defaultChoice, null, 0, warnOnSoftKeys);
      d.setIcon(ThemeManager.getThemeAwareImage("dialog_question"));
      return d.doModal();
   }

   public static void inform(String message, boolean warnOnSoftKeys) {
      WizardDialog d = new WizardDialog(0, message, 0, null, 0, warnOnSoftKeys);
      d.setIcon(ThemeManager.getThemeAwareImage("dialog_information"));
      d.doModal();
   }

   public static WizardDialog showWaitingDialog(String message, boolean warnOnSoftKeys) {
      WizardDialog d = new WizardDialog(message, null, new int[]{0, -804651006, 0, 0}, 0, null, warnOnSoftKeys);
      d._isWaitingDialog = true;
      d.show();
      return d;
   }

   public boolean dismissWaitingDialog() {
      if (this.isDisplayed() && this._isWaitingDialog) {
         this._isWaitingDialog = false;
         this.cancel();
         return true;
      } else {
         return false;
      }
   }

   public static ButtonField createDialogButtonField(String buttonText) {
      ButtonField field = (ButtonField)(new Object(buttonText, 12884901888L));
      field.setFont(defineFont());
      return field;
   }

   public static RichTextField createDialogRichTextField(String text, boolean boldFont, long style) {
      Font fieldFont = Font.getDefault().derive(boldFont ? 1 : 0, Ui.convertSize(7, 3, 0));
      RichTextField field = (RichTextField)(new Object(text, style));
      field.setFont(fieldFont);
      return field;
   }

   private static Font defineFont() {
      Font font = Font.getDefault().derive(1, Ui.convertSize(7, 3, 0));
      int screenWidth = Display.getWidth();
      if (screenWidth <= 240) {
         int mask = -65536;
         int currentLocale = Locale.getDefault().getCode() & mask;
         if ((1701707776 & mask) != currentLocale && (1701726018 & mask) != currentLocale && (1701729619 & mask) != currentLocale) {
            font = Font.getDefault().derive(1, Ui.convertSize(6, 3, 0));
         }
      }

      return font;
   }
}
