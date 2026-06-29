package net.rim.device.apps.internal.security;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.Phone;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Trackball;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.internal.profiles.ProfileQuickToggle;
import net.rim.device.internal.system.PersistentContentTest;
import net.rim.vm.Memory;

final class SecurityApp$MyDialog extends Dialog {
   private int _defaultChoice;
   private final SecurityApp this$0;

   public SecurityApp$MyDialog(SecurityApp _1, String message, Object[] choices, int[] values, int defaultChoice, Bitmap bitmap, long style) {
      super(message, choices, values, defaultChoice, bitmap, style, new SecurityApp$MyDialogFieldManager(_1));
      this.this$0 = _1;
      this._defaultChoice = defaultChoice;
   }

   public SecurityApp$MyDialog(SecurityApp _1, Field messageField, Object[] choices, int[] values, int defaultChoice, Bitmap bitmap, long style) {
      super(_1._rb.getString(710), choices, values, defaultChoice, bitmap, style, new SecurityApp$MyDialogFieldManager(_1));
      this.this$0 = _1;
      this._defaultChoice = defaultChoice;
      this.add(messageField);
   }

   @Override
   protected final void onHotkeySelected(char key) {
      String logString = ((StringBuffer)(new Object("dlg hk slctd "))).append(key).toString();
      EventLogger.logEvent(-1148210079122251014L, logString.getBytes(), 0);
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key == 17) {
         if (this._defaultChoice != -1) {
            this.select(this._defaultChoice);
         }

         return true;
      } else {
         return super.keyChar(key, status, time);
      }
   }

   @Override
   public final boolean keyRepeat(int keycode, int time) {
      int key = Keypad.key(keycode);
      return !Phone.isPhoneActive() && (key == 273 || key == 261) && !Trackball.isSupported() ? ProfileQuickToggle.handleKeyRepeat(time) : false;
   }

   @Override
   public final boolean keyDown(int keycode, int time) {
      int key = Keypad.key(keycode);
      char keyChar = Keypad.map(keycode);
      if (!Phone.isPhoneActive() && (key == 273 || key == 261) && !Trackball.isSupported()) {
         ProfileQuickToggle.handleKeyDown(time);
         return true;
      }

      if (key == 17 && this.this$0._hotkeyInit) {
         this.this$0._keyLockSequence = true;
         this.this$0.unlock();
      } else if (!Keypad.hasSendEndKeys() || key != 20 && keyChar != '*' && Keypad.getAltedChar(keyChar) != '*') {
         this.this$0._hotkeyInit = false;
      } else {
         this.this$0._hotkeyInit = true;
      }

      return super.keyDown(keycode, time);
   }

   @Override
   public final boolean openDevelopmentBackdoor(int backdoorCode) {
      return false;
   }

   @Override
   public final boolean openProductionBackdoor(int backdoorCode) {
      StringBuffer sb = (StringBuffer)(new Object());
      switch (backdoorCode) {
         case 1398032195:
            Memory.secureThoroughGC();
         case 1514488402:
            if (!PersistentContent.isEncryptionEnabled()) {
               sb.append("Content Protection is not enabled.\n");
            } else {
               int contentProtectionStrength = ITPolicy.getInteger(24, 18, -1);
               if (contentProtectionStrength != -1) {
                  sb.append(
                     ((StringBuffer)(new Object("Content Protection Strength IT Policy Setting is ")))
                        .append(contentProtectionStrength)
                        .append(".\n")
                        .toString()
                  );
               }

               if (PersistentContent.isCompressionEnabled()) {
                  sb.append("Content Compression must be disabled for this test.\n");
               } else {
                  if (PersistentContentTest.search()) {
                     sb.append("Found");
                  } else {
                     sb.append("Did not find");
                  }

                  sb.append(" \"Zebr\u200ba\" and/or \"555\u200b1234\".\n");
               }
            }
         case 1347174729:
            if (PersistentContent.isTicketInUse()) {
               sb.append("Apps are still using Content Protection key.\n");
            }

            int numPlaintext = Memory.numPlaintext() - Memory.numPlaintextSpecial();
            if (numPlaintext != 0) {
               int numPersistentPlaintext = Memory.numPersistentPlaintext();
               sb.append(
                  ((StringBuffer)(new Object("Found ")))
                     .append(numPlaintext)
                     .append(" plaintext objects (")
                     .append(numPersistentPlaintext)
                     .append(" are persisted).")
                     .toString()
               );
            } else {
               sb.append("Did not find any plaintext objects.");
            }

            Dialog.inform(sb.toString());
            return true;
         default:
            return false;
      }
   }

   @Override
   public final void close() {
      this.this$0._promptDialog = null;
      super.close();
   }

   final void cancelDialog() {
      if (this.this$0._promptDialog != null) {
         this.inHolster();
      }
   }
}
