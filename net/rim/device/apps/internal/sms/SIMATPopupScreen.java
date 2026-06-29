package net.rim.device.apps.internal.sms;

import net.rim.device.api.system.SIMCard;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.RadioButtonField;
import net.rim.device.api.ui.component.RadioButtonGroup;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.sms.ui.SMSFilter;
import net.rim.device.internal.ui.component.ImageField;

class SIMATPopupScreen extends PopupScreen implements FieldChangeListener {
   private RichTextField _label;
   private ButtonField _okButton;
   private ButtonField _backButton;
   private ButtonField _cancelButton;
   private ButtonField _helpButton;
   private ButtonField _yesButton;
   private ButtonField _noButton;
   private BasicEditField _editField;
   private int _minLength;
   protected int _type;
   private RadioButtonGroup _radioField;
   private int[] _radioIds;
   private SIMToolkit _stk;
   protected boolean _immediateResponse;
   private int _inputMessageCoding;
   protected static final int DIALOG_DISPLAY_TEXT;
   private static final int DIALOG_GET_INKEY;
   private static final int DIALOG_GET_INPUT;
   private static final int DIALOG_SELECT_ITEM;
   private static final int DIALOG_SELECT_MENU;
   private static final int DIALOG_PLAY_TONE;
   private static final int DIALOG_LAUNCH_BROWSER;
   private static final int DIALOG_DISPLAY_ALPHA_ID;
   protected static final int OK_BUTTON;
   protected static final int BACK_BUTTON;
   protected static final int CANCEL_BUTTON;
   private static final int HELP_BUTTON;
   private static final int YES_BUTTON;
   private static final int NO_BUTTON;

   void getInkey(int allowedKeys, boolean helpAvailable, int inputMessageCoding) {
      this.getInput(2, null, allowedKeys, 1, 2, true, helpAvailable, inputMessageCoding);
   }

   void getInput(String defaultText, int allowedKeys, int minLength, int maxLength, boolean echo, boolean helpAvailable, int inputMessageCoding) {
      this.getInput(3, defaultText, allowedKeys, minLength, maxLength, echo, helpAvailable, inputMessageCoding);
   }

   void selectItem(Object[] items, int[] ids, int defaultId, boolean helpAvailable) {
      this.select(4, items, ids, defaultId, helpAvailable);
   }

   void selectMenu(Object[] items, int[] ids, boolean helpAvailable) {
      this.select(5, items, ids, -1, helpAvailable);
   }

   void playTone() {
      this._type = 6;
      this.go(5);
   }

   void displayAlphaID() {
      this._type = 8;
      this.go(1);
   }

   void launchBrowser() {
      this._type = 7;
      this.go(1);
   }

   protected void go(int buttons) {
      if ((buttons & 1) != 0) {
         this._okButton = (ButtonField)(new Object(CommonResources.getString(117), 12884901888L));
         this.add(this._okButton);
      }

      if ((buttons & 16) != 0) {
         this._yesButton = (ButtonField)(new Object(CommonResources.getString(100), 12884901888L));
         this.add(this._yesButton);
      }

      if ((buttons & 32) != 0) {
         this._noButton = (ButtonField)(new Object(CommonResources.getString(101), 12884901888L));
         this.add(this._noButton);
      }

      if ((buttons & 2) != 0) {
         this._backButton = (ButtonField)(new Object(CommonResources.getString(9033), 12884901888L));
         this.add(this._backButton);
      }

      if ((buttons & 4) != 0) {
         this._cancelButton = (ButtonField)(new Object(CommonResources.getString(9042), 12884901888L));
         this.add(this._cancelButton);
      }

      if ((buttons & 8) != 0) {
         this._helpButton = (ButtonField)(new Object(CommonResources.getString(9034), 12884901888L));
         this.add(this._helpButton);
      }

      Ui.getUiEngine().pushGlobalScreen(this, -2147483645, 2);
      if (this.getFieldCount() > 1) {
         Field f1 = this.getField(1);
         if (this.getField(0).getHeight() + f1.getHeight() <= this.getDelegate().getHeight()) {
            f1.setFocus();
         }
      }
   }

   void dismiss() {
      this._stk.popupDone();
      Ui.getUiEngine().popScreen(this);
   }

   @Override
   public void fieldChanged(Field f, int context) {
      if (this._type == 2 && this._editField.getText().length() >= 1) {
         this.process(this.getLeafFieldWithFocus());
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   SIMATPopupScreen(SIMToolkit stk, String text) {
      super((Manager)(new Object(299067162755072L)));
      this._stk = stk;
      HorizontalFieldManager hfm = (HorizontalFieldManager)(new Object());

      label20:
      try {
         ImageField imageField = (ImageField)(new Object(51539607552L));
         imageField.setImage(ThemeManager.getThemeAwareImage(SetupMenuEntryPoint.UNIQUE_NAME));
         hfm.add(imageField);
      } catch (Throwable var6) {
         SIMATEventLogger.log(16, ex);
         break label20;
      }

      this._label = (RichTextField)(new Object(text, 18014450049089536L));
      hfm.add(this._label);
      this.add(hfm);
   }

   private void select(int type, Object[] items, int[] ids, int defaultId, boolean helpAvailable) {
      this._type = type;
      this._radioField = (RadioButtonGroup)(new Object());
      this._radioIds = ids;
      int selectedIndex = 0;

      for (int i = 0; i < items.length; i++) {
         if (items[i] != null) {
            if (ids[i] == defaultId) {
               selectedIndex = i;
            }

            this.add((Field)(new Object(SIMCard.decodeAlphaId((byte[])items[i]), this._radioField, i == 0)));
         }
      }

      this._radioField.setSelectedIndex(selectedIndex);
      int buttons = 5;
      if (this._type == 4) {
         buttons |= 2;
      }

      if (helpAvailable) {
         buttons |= 8;
      }

      this.go(buttons);
   }

   @Override
   protected boolean navigationClick(int status, int time) {
      super.navigationClick(status, time);
      Field f = this.getLeafFieldWithFocus();
      if (f != this._label) {
         if (f instanceof Object) {
            RadioButtonField rb = (RadioButtonField)f;
            rb.setSelected(true);
         }

         this.process(this.getLeafFieldWithFocus());
      }

      return true;
   }

   private void process(Field param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 1
      // 001: aload 0
      // 002: getfield net/rim/device/apps/internal/sms/SIMATPopupScreen._backButton Lnet/rim/device/api/ui/component/ButtonField;
      // 005: if_acmpne 00e
      // 008: invokestatic net/rim/device/api/system/SIMCard.atBack ()V
      // 00b: goto 192
      // 00e: aload 1
      // 00f: aload 0
      // 010: getfield net/rim/device/apps/internal/sms/SIMATPopupScreen._cancelButton Lnet/rim/device/api/ui/component/ButtonField;
      // 013: if_acmpne 032
      // 016: aload 0
      // 017: getfield net/rim/device/apps/internal/sms/SIMATPopupScreen._type I
      // 01a: lookupswitch 18 1 5 376
      // 02c: invokestatic net/rim/device/api/system/SIMCard.atCancel ()V
      // 02f: goto 192
      // 032: aload 1
      // 033: aload 0
      // 034: getfield net/rim/device/apps/internal/sms/SIMATPopupScreen._helpButton Lnet/rim/device/api/ui/component/ButtonField;
      // 037: if_acmpne 068
      // 03a: bipush 0
      // 03b: istore 2
      // 03c: aload 0
      // 03d: getfield net/rim/device/apps/internal/sms/SIMATPopupScreen._radioField Lnet/rim/device/api/ui/component/RadioButtonGroup;
      // 040: ifnull 050
      // 043: aload 0
      // 044: getfield net/rim/device/apps/internal/sms/SIMATPopupScreen._radioIds [I
      // 047: aload 0
      // 048: getfield net/rim/device/apps/internal/sms/SIMATPopupScreen._radioField Lnet/rim/device/api/ui/component/RadioButtonGroup;
      // 04b: invokevirtual net/rim/device/api/ui/component/RadioButtonGroup.getSelectedIndex ()I
      // 04e: iaload
      // 04f: istore 2
      // 050: aload 0
      // 051: getfield net/rim/device/apps/internal/sms/SIMATPopupScreen._type I
      // 054: bipush 5
      // 056: if_icmpne 061
      // 059: iload 2
      // 05a: bipush 1
      // 05b: invokestatic net/rim/device/api/system/SIMCard.atMenuSelected (IZ)V
      // 05e: goto 192
      // 061: iload 2
      // 062: invokestatic net/rim/device/api/system/SIMCard.atHelp (I)V
      // 065: goto 192
      // 068: aload 0
      // 069: getfield net/rim/device/apps/internal/sms/SIMATPopupScreen._type I
      // 06c: tableswitch 44 0 6 294 257 44 152 205 223 271
      // 098: aload 0
      // 099: getfield net/rim/device/apps/internal/sms/SIMATPopupScreen._editField Lnet/rim/device/api/ui/component/BasicEditField;
      // 09c: ifnonnull 0b5
      // 09f: aload 1
      // 0a0: aload 0
      // 0a1: getfield net/rim/device/apps/internal/sms/SIMATPopupScreen._yesButton Lnet/rim/device/api/ui/component/ButtonField;
      // 0a4: if_acmpne 0ae
      // 0a7: bipush 1
      // 0a8: invokestatic net/rim/device/api/system/SIMCard.atGetInkeyAck (I)V
      // 0ab: goto 192
      // 0ae: bipush 0
      // 0af: invokestatic net/rim/device/api/system/SIMCard.atGetInkeyAck (I)V
      // 0b2: goto 192
      // 0b5: aload 0
      // 0b6: getfield net/rim/device/apps/internal/sms/SIMATPopupScreen._editField Lnet/rim/device/api/ui/component/BasicEditField;
      // 0b9: invokevirtual net/rim/device/api/ui/component/BasicEditField.getText ()Ljava/lang/String;
      // 0bc: astore 2
      // 0bd: aload 2
      // 0be: invokevirtual java/lang/String.length ()I
      // 0c1: bipush 1
      // 0c2: if_icmpge 0c6
      // 0c5: return
      // 0c6: aload 2
      // 0c7: aload 0
      // 0c8: getfield net/rim/device/apps/internal/sms/SIMATPopupScreen._inputMessageCoding I
      // 0cb: invokestatic net/rim/device/apps/internal/sms/SMSService.getSmsEncoder (I)Ljava/lang/String;
      // 0ce: invokevirtual java/lang/String.getBytes (Ljava/lang/String;)[B
      // 0d1: astore 4
      // 0d3: aload 0
      // 0d4: getfield net/rim/device/apps/internal/sms/SIMATPopupScreen._inputMessageCoding I
      // 0d7: bipush 2
      // 0d9: if_icmpne 0ee
      // 0dc: aload 4
      // 0de: bipush 0
      // 0df: baload
      // 0e0: bipush 8
      // 0e2: ishl
      // 0e3: aload 4
      // 0e5: bipush 1
      // 0e6: baload
      // 0e7: ior
      // 0e8: invokestatic net/rim/device/api/system/SIMCard.atGetInkeyAck (I)V
      // 0eb: goto 192
      // 0ee: aload 4
      // 0f0: bipush 0
      // 0f1: baload
      // 0f2: invokestatic net/rim/device/api/system/SIMCard.atGetInkeyAck (I)V
      // 0f5: goto 192
      // 0f8: astore 4
      // 0fa: bipush 14
      // 0fc: aload 4
      // 0fe: invokestatic net/rim/device/apps/internal/sms/SIMATEventLogger.log (ILjava/lang/Exception;)V
      // 101: goto 192
      // 104: aload 0
      // 105: getfield net/rim/device/apps/internal/sms/SIMATPopupScreen._editField Lnet/rim/device/api/ui/component/BasicEditField;
      // 108: invokevirtual net/rim/device/api/ui/component/BasicEditField.getText ()Ljava/lang/String;
      // 10b: astore 2
      // 10c: aload 2
      // 10d: invokevirtual java/lang/String.length ()I
      // 110: aload 0
      // 111: getfield net/rim/device/apps/internal/sms/SIMATPopupScreen._minLength I
      // 114: if_icmpge 118
      // 117: return
      // 118: aload 2
      // 119: aload 0
      // 11a: getfield net/rim/device/apps/internal/sms/SIMATPopupScreen._inputMessageCoding I
      // 11d: invokestatic net/rim/device/apps/internal/sms/SMSService.getSmsEncoder (I)Ljava/lang/String;
      // 120: invokevirtual java/lang/String.getBytes (Ljava/lang/String;)[B
      // 123: astore 4
      // 125: aload 4
      // 127: invokestatic net/rim/device/api/system/SIMCard.atGetInputAck ([B)V
      // 12a: goto 192
      // 12d: astore 4
      // 12f: bipush 14
      // 131: aload 4
      // 133: invokestatic net/rim/device/apps/internal/sms/SIMATEventLogger.log (ILjava/lang/Exception;)V
      // 136: goto 192
      // 139: aload 0
      // 13a: getfield net/rim/device/apps/internal/sms/SIMATPopupScreen._radioIds [I
      // 13d: aload 0
      // 13e: getfield net/rim/device/apps/internal/sms/SIMATPopupScreen._radioField Lnet/rim/device/api/ui/component/RadioButtonGroup;
      // 141: invokevirtual net/rim/device/api/ui/component/RadioButtonGroup.getSelectedIndex ()I
      // 144: iaload
      // 145: invokestatic net/rim/device/api/system/SIMCard.atSelectItemAck (I)V
      // 148: goto 192
      // 14b: aload 0
      // 14c: getfield net/rim/device/apps/internal/sms/SIMATPopupScreen._radioField Lnet/rim/device/api/ui/component/RadioButtonGroup;
      // 14f: invokevirtual net/rim/device/api/ui/component/RadioButtonGroup.getSelectedIndex ()I
      // 152: istore 3
      // 153: iload 3
      // 154: iflt 192
      // 157: iload 3
      // 158: aload 0
      // 159: getfield net/rim/device/apps/internal/sms/SIMATPopupScreen._radioIds [I
      // 15c: arraylength
      // 15d: if_icmpge 192
      // 160: aload 0
      // 161: getfield net/rim/device/apps/internal/sms/SIMATPopupScreen._radioIds [I
      // 164: iload 3
      // 165: iaload
      // 166: bipush 0
      // 167: invokestatic net/rim/device/api/system/SIMCard.atMenuSelected (IZ)V
      // 16a: goto 192
      // 16d: aload 0
      // 16e: getfield net/rim/device/apps/internal/sms/SIMATPopupScreen._immediateResponse Z
      // 171: ifne 192
      // 174: bipush 0
      // 175: invokestatic net/rim/device/api/system/SIMCard.atDisplayTextAck (Z)V
      // 178: goto 192
      // 17b: invokestatic net/rim/device/api/system/SIMCard.atPlayToneAck ()V
      // 17e: goto 192
      // 181: astore 2
      // 182: bipush 14
      // 184: aload 2
      // 185: invokestatic net/rim/device/apps/internal/sms/SIMATEventLogger.log (ILjava/lang/Exception;)V
      // 188: goto 192
      // 18b: astore 2
      // 18c: bipush 14
      // 18e: aload 2
      // 18f: invokestatic net/rim/device/apps/internal/sms/SIMATEventLogger.log (ILjava/lang/Exception;)V
      // 192: aload 0
      // 193: invokevirtual net/rim/device/apps/internal/sms/SIMATPopupScreen.dismiss ()V
      // 196: return
      // try (45 -> 66): 93 null
      // try (67 -> 92): 93 null
      // try (108 -> 116): 117 null
      // try (0 -> 66): 156 null
      // try (67 -> 107): 156 null
      // try (108 -> 156): 156 null
      // try (0 -> 66): 161 null
      // try (67 -> 107): 161 null
      // try (108 -> 156): 161 null
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void getInput(
      int type, String defaultText, int allowedKeys, int minLength, int maxLength, boolean echo, boolean helpAvailable, int inputMessageCoding
   ) {
      this._minLength = minLength;
      this._type = type;
      this._inputMessageCoding = inputMessageCoding;
      int buttons = 6;
      if (allowedKeys == 2) {
         buttons |= 48;
      } else {
         if (echo) {
            this._editField = (BasicEditField)(new Object(null, null, maxLength, 0));
         } else {
            this._editField = (BasicEditField)(new Object(null, null, maxLength, 0));
         }

         if (allowedKeys != 0 && echo) {
            this._editField.setFilter(new SMSFilter(false, inputMessageCoding));
         } else {
            this._editField.setFilter(new SIMATNumericFilter(echo));
         }

         this._editField.setChangeListener(this);
         if (defaultText != null) {
            label54:
            try {
               this._editField.setText(defaultText);
            } catch (Throwable var12) {
               SIMATEventLogger.log(15, ex);
               break label54;
            }
         }

         this.add(this._editField);
      }

      if (this._type == 3) {
         buttons |= 1;
      }

      if (helpAvailable) {
         buttons |= 8;
      }

      this.go(buttons);
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      boolean ret = super.keyChar(key, status, time);
      if (key == 27) {
         this.process(this._cancelButton);
         return true;
      }

      if (key == '\n') {
         Field f = this.getLeafFieldWithFocus();
         if (f != this._label) {
            this.process(f);
         }

         return true;
      } else {
         return ret;
      }
   }
}
