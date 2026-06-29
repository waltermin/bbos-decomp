package net.rim.device.api.smartcard;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.Application;
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
import net.rim.device.internal.ui.component.BackgroundDialog;
import net.rim.device.internal.ui.component.PopupDialog;
import net.rim.tid.awt.im.InputContext;
import net.rim.tid.im.layout.SLKeyLayout;

public class SmartCardPromptDialog extends PopupDialog implements FieldChangeListener, ReaderStatusListener {
   private ButtonField _okButton;
   private ButtonField _cancelButton;
   private DialogFieldManager _dfm;
   private SmartCardReader[] _smartCardReaders;
   private Application _app;
   private boolean _dialogOpen = false;

   public static void promptUserToInsertSmartCard(String message, String[] messageParameters, SmartCardReader smartCardReader) {
      promptUserToInsertSmartCardPrivate(message, messageParameters, new SmartCardReader[]{smartCardReader}, false);
   }

   public static void promptUserToInsertSmartCard(String message, String[] messageParameters, SmartCardReader smartCardReader, boolean forceShowDialog) {
      promptUserToInsertSmartCardPrivate(message, messageParameters, new SmartCardReader[]{smartCardReader}, forceShowDialog);
   }

   public static void promptUserToInsertSmartCard(String message, String[] messageParameters, SmartCardReader[] smartCardReaders) {
      promptUserToInsertSmartCardPrivate(message, messageParameters, smartCardReaders, false);
   }

   public static void promptUserToInsertSmartCard(String message, String[] messageParameters, SmartCardReader[] smartCardReaders, boolean forceShowDialog) {
      promptUserToInsertSmartCardPrivate(message, messageParameters, smartCardReaders, forceShowDialog);
   }

   private static void promptUserToInsertSmartCardPrivate(
      String message, String[] messageParameters, SmartCardReader[] smartCardReaders, boolean forceShowDialog
   ) throws SmartCardCancelException {
      if (!forceShowDialog && smartCardReaders != null) {
         for (int i = 0; i < smartCardReaders.length; i++) {
            if (smartCardReaders[i].isSmartCardPresent()) {
               return;
            }
         }
      }

      SmartCardPromptDialog dialog = new SmartCardPromptDialog(message, messageParameters, smartCardReaders, 134217728);
      BackgroundDialog.show(dialog);
      if (dialog.getCloseReason() == -1) {
         throw new SmartCardCancelException();
      }
   }

   public SmartCardPromptDialog(String message, String[] messageParameters, SmartCardReader[] smartCardReaders, long style) {
      super(new DialogFieldManager(281474976710656L), style);
      this.setStatusPriority(-2147483644);
      this._smartCardReaders = smartCardReaders;
      this._app = Application.getApplication();
      if (this._smartCardReaders != null) {
         for (int i = 0; i < this._smartCardReaders.length; i++) {
            this._smartCardReaders[i].addListener(this);
         }
      }

      this._dfm = (DialogFieldManager)this.getDelegate();
      String fieldMessage;
      if (messageParameters != null) {
         fieldMessage = MessageFormat.format(message, messageParameters);
      } else {
         fieldMessage = message;
      }

      RichTextField field = new RichTextField(fieldMessage, 45035996273704960L);
      this._dfm.setMessage(field);
      String[] okCancelStrings = CommonResource.getStringArray(10041);
      this._okButton = new ButtonField(okCancelStrings[0], 12884901888L);
      this._okButton.setChangeListener(this);
      this._cancelButton = new ButtonField(okCancelStrings[1], 12884901888L);
      this._cancelButton.setChangeListener(this);
      this._dfm.addCustomField(this._okButton);
      this._dfm.addCustomField(this._cancelButton);
      this._dfm.setIcon(new BitmapField(Bitmap.getPredefinedBitmap(1)));
      this.setCancelAllowed(true);
   }

   @Override
   protected void onDisplay() {
      this._dfm.getCustomField(0).setFocus();
      super.onDisplay();
      this._dialogOpen = true;
   }

   @Override
   protected void onUndisplay() {
      if (this._smartCardReaders != null) {
         for (int i = 0; i < this._smartCardReaders.length; i++) {
            this._smartCardReaders[i].removeListener(this);
         }
      }

      super.onUndisplay();
   }

   @Override
   public void fieldChanged(Field field, int context) {
      if (field == this._okButton) {
         this.close(0);
      } else {
         if (field == this._cancelButton) {
            this.close(-1);
         }
      }
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      boolean handled = super.keyChar(key, status, time);
      if (!handled) {
         if (key == 27 && this.isCancelAllowed()) {
            this.close(-1);
            return true;
         }

         key = CharacterUtilities.toLowerCase(key, 1701707776);

         for (int i = 0; i < 2; i++) {
            String choice = i == 0 ? this._okButton.getLabel() : this._cancelButton.getLabel();
            String chars = null;
            if (InputContext.getInstance(false).isSureType()) {
               StringBuffer temp = Keypad.getLayout().getComplementaryChars(key, SLKeyLayout.convertStatusToModifiers(status));
               if (temp != null) {
                  chars = temp.toString();
               }
            }

            int hotposition = choice.indexOf(818);
            if (hotposition > 0) {
               char hotkey = CharacterUtilities.toLowerCase(choice.charAt(hotposition - 1), 1701707776);
               if (hotkey == key || chars != null && chars.indexOf(hotkey) != -1) {
                  this.close(i == 0 ? 0 : -1);
                  return true;
               }
            }
         }
      }

      return handled;
   }

   @Override
   protected void close(int closeReason) {
      this.close(closeReason, true);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void close(int closeReason, boolean checkForCardPresent) {
      if (closeReason == 0) {
         boolean var7 = false /* VF: Semaphore variable */;

         try {
            var7 = true;
            if (this._smartCardReaders != null) {
               if (checkForCardPresent) {
                  boolean cardPresent = false;

                  for (int i = 0; i < this._smartCardReaders.length; i++) {
                     if (this._smartCardReaders[i].isSmartCardPresent()) {
                        cardPresent = true;
                     }
                  }

                  if (!cardPresent) {
                     return;
                  }

                  var7 = false;
               } else {
                  var7 = false;
               }
            } else {
               var7 = false;
            }
         } catch (SmartCardException var8) {
            var7 = false;
         } finally {
            if (var7) {
               return;
            }
         }
      }

      this._dialogOpen = false;
      super.close(closeReason);
   }

   @Override
   public void readerStatus(SmartCardReader reader, int status) {
      if (status == 3) {
         this._app.invokeLater(new SmartCardPromptDialog$1(this));
      }
   }
}
