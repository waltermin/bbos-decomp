package net.rim.device.cldc.impl.softtoken;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.GaugeField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.cldc.impl.api.SoftToken;
import net.rim.device.cldc.impl.api.SoftTokenDialog;
import net.rim.device.cldc.impl.api.SoftTokenManager;
import net.rim.device.cldc.impl.softtoken.rimsecuridlib.RimSecurIDLib;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.ui.RichTextFieldUtilities;
import net.rim.device.internal.ui.container.VerticalIndentFieldManager;

public class SoftTokenDialogImpl extends SoftTokenDialog implements FieldChangeListener {
   private Object _eventLock;
   private RimSecurIDLib _rimSecurIDLib;
   private SoftTokenManagerImpl _tokenManager;
   private SoftTokenImpl _softToken;
   private SoftTokenDialogImpl$GaugeUpdateThread _gaugeUpdateThread;
   private long _constructionTime;
   private RichTextField _promptField;
   private EditField _usernameField;
   private EditField _promptResponseField;
   private PasswordEditField _pinField;
   private BasicEditField _currentCodeField;
   private ObjectChoiceField _typeChoiceField;
   private GaugeField _gaugeField;
   private ButtonField _useCurrentCodeButton;
   private ButtonField _okButton;
   private ButtonField _cancelButton;
   private String _usernameText;
   private String _responseText;
   private static ResourceBundle _rb = ResourceBundle.getBundle(390461180289309471L, "net.rim.device.internal.resource.SoftTokenResources");
   private static final int GENERATE_TOKENCODE_INDEX = 0;
   private static final int GENERATE_NEXT_TOKENCODE_INDEX = 1;
   private static final int GENERATE_NEXT_NEXT_TOKENCODE_INDEX = 2;
   private static final int GENERATE_PASSCODE_INDEX = 3;
   private static final int GENERATE_NEXT_PASSCODE_INDEX = 4;
   private static final int GENERATE_NEXT_NEXT_PASSCODE_INDEX = 5;
   private static String[] _passcodeUseChoices = new String[]{
      _rb.getString(4), _rb.getString(5), _rb.getString(6), _rb.getString(1), _rb.getString(2), _rb.getString(3)
   };
   private static String[] _tokencodeUseChoices = new String[]{_rb.getString(4), _rb.getString(5), _rb.getString(6)};

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public SoftTokenDialogImpl(String serialNum, String prompt, boolean showPromptResponseField, boolean showUsernameField, String username) {
      super(new VerticalIndentFieldManager(1153220571769602048L));
      VerticalIndentFieldManager vifm = (VerticalIndentFieldManager)this.getDelegate();
      this._constructionTime = System.currentTimeMillis();
      this._eventLock = Application.getApplication();
      if (this._eventLock instanceof UiApplication) {
         this._eventLock = ((UiApplication)this._eventLock).getAppEventLock();
      } else {
         Proxy.getInstance();
         this._eventLock = Application.getEventLock();
      }

      this._tokenManager = (SoftTokenManagerImpl)SoftTokenManager.getInstance();
      this._softToken = (SoftTokenImpl)this._tokenManager.getSoftToken(serialNum);
      this._rimSecurIDLib = new RimSecurIDLib();
      if (this._softToken != null) {
         boolean var10 = false /* VF: Semaphore variable */;

         label59:
         try {
            var10 = true;
            this._rimSecurIDLib.setActiveToken(this._softToken._tag);
            var10 = false;
         } finally {
            if (var10) {
               this._softToken = null;
               SoftTokenManager.logEvent(1163084116, 2);
               break label59;
            }
         }
      }

      if (this._softToken != null) {
         this._promptField = RichTextFieldUtilities.getBoldFormattedRichTextField(prompt, 45035996273704960L);
         vifm.add(this._promptField);
         vifm.add(new SeparatorField());
         this._usernameField = new EditField(CommonResource.getString(10026), username, 255, 4503599627370496L);
         if (showUsernameField) {
            vifm.add(this._usernameField);
         }

         this._promptResponseField = new EditField(_rb.getString(18), null, 255, 4503599627370496L);
         if (showPromptResponseField) {
            vifm.add(this._promptResponseField);
            vifm.add(new SeparatorField());
         }

         String PIN = this._softToken.getPIN();
         this._pinField = new PasswordEditField(_rb.getString(8), this._softToken.getPIN(), 8, 16777216);
         this._pinField.setChangeListener(this);
         vifm.add(this._pinField);
         vifm.add(new SeparatorField());
         this._currentCodeField = new BasicEditField(_rb.getString(15), null, 16, 9007199254740992L);
         if (PIN.length() == 0) {
            String[] useChoices = _tokencodeUseChoices;
            this._typeChoiceField = new ObjectChoiceField(_rb.getString(16), useChoices, 0);
         } else {
            String[] useChoices = _passcodeUseChoices;
            this._typeChoiceField = new ObjectChoiceField(_rb.getString(16), useChoices, 3);
         }

         this._typeChoiceField.setChangeListener(this);
         vifm.add(this._currentCodeField);
         vifm.add(this._typeChoiceField);
         this.updateCurrentCode();
         this._gaugeField = new GaugeField(null, 0, 9, 9, 36028797018963970L);
         vifm.add(this._gaugeField);
         this._gaugeUpdateThread = new SoftTokenDialogImpl$GaugeUpdateThread(this);
         this._gaugeUpdateThread.start();
         if (showPromptResponseField) {
            this._useCurrentCodeButton = new ButtonField(_rb.getString(17));
            this._useCurrentCodeButton.setChangeListener(this);
            vifm.add(this._useCurrentCodeButton);
         }

         vifm.add(new SeparatorField());
         HorizontalFieldManager buttonManager = new HorizontalFieldManager(12884901888L);
         this._okButton = new ButtonField(CommonResource.getString(100));
         this._okButton.setChangeListener(this);
         buttonManager.add(this._okButton);
         this._cancelButton = new ButtonField(CommonResource.getString(10005));
         this._cancelButton.setChangeListener(this);
         buttonManager.add(this._cancelButton);
         vifm.add(buttonManager);
         this.setFieldWithFocus(this._promptResponseField);
      }
   }

   private int getRimSecurIDLibPasscodeOffset() {
      int returnValue = 0;
      switch (this._typeChoiceField.getSelectedIndex()) {
         case 0:
         case 3:
         default:
            return 0;
         case 1:
         case 4:
            return 1;
         case 2:
         case 5:
            returnValue = 2;
         case -1:
            return returnValue;
      }
   }

   private boolean updateCurrentCode() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: getfield net/rim/device/cldc/impl/softtoken/SoftTokenDialogImpl._eventLock Ljava/lang/Object;
      // 004: dup
      // 005: astore 1
      // 006: monitorenter
      // 007: ldc_w ""
      // 00a: astore 2
      // 00b: bipush 0
      // 00c: istore 3
      // 00d: bipush 0
      // 00e: istore 4
      // 010: aload 0
      // 011: getfield net/rim/device/cldc/impl/softtoken/SoftTokenDialogImpl._rimSecurIDLib Lnet/rim/device/cldc/impl/softtoken/rimsecuridlib/RimSecurIDLib;
      // 014: aload 0
      // 015: getfield net/rim/device/cldc/impl/softtoken/SoftTokenDialogImpl._softToken Lnet/rim/device/cldc/impl/softtoken/SoftTokenImpl;
      // 018: getfield net/rim/device/cldc/impl/softtoken/SoftTokenImpl._tag I
      // 01b: invokevirtual net/rim/device/cldc/impl/softtoken/rimsecuridlib/RimSecurIDLib.setActiveToken (I)V
      // 01e: aload 0
      // 01f: getfield net/rim/device/cldc/impl/softtoken/SoftTokenDialogImpl._typeChoiceField Lnet/rim/device/api/ui/component/ObjectChoiceField;
      // 022: invokevirtual net/rim/device/api/ui/component/ChoiceField.getSelectedIndex ()I
      // 025: istore 5
      // 027: aload 0
      // 028: getfield net/rim/device/cldc/impl/softtoken/SoftTokenDialogImpl._pinField Lnet/rim/device/api/ui/component/PasswordEditField;
      // 02b: invokevirtual net/rim/device/api/ui/component/PasswordEditField.getText ()Ljava/lang/String;
      // 02e: astore 6
      // 030: aload 6
      // 032: invokevirtual java/lang/String.length ()I
      // 035: ifeq 04a
      // 038: iload 5
      // 03a: ifeq 04a
      // 03d: iload 5
      // 03f: bipush 1
      // 040: if_icmpeq 04a
      // 043: iload 5
      // 045: bipush 2
      // 047: if_icmpne 06b
      // 04a: aload 0
      // 04b: getfield net/rim/device/cldc/impl/softtoken/SoftTokenDialogImpl._rimSecurIDLib Lnet/rim/device/cldc/impl/softtoken/rimsecuridlib/RimSecurIDLib;
      // 04e: aload 0
      // 04f: getfield net/rim/device/cldc/impl/softtoken/SoftTokenDialogImpl._softToken Lnet/rim/device/cldc/impl/softtoken/SoftTokenImpl;
      // 052: getfield net/rim/device/cldc/impl/softtoken/SoftTokenImpl._tag I
      // 055: aload 0
      // 056: getfield net/rim/device/cldc/impl/softtoken/SoftTokenDialogImpl._softToken Lnet/rim/device/cldc/impl/softtoken/SoftTokenImpl;
      // 059: invokevirtual net/rim/device/cldc/impl/softtoken/SoftTokenImpl.getPassphrase ()Ljava/lang/String;
      // 05c: aload 0
      // 05d: invokespecial net/rim/device/cldc/impl/softtoken/SoftTokenDialogImpl.getRimSecurIDLibPasscodeOffset ()I
      // 060: ldc_w ""
      // 063: invokevirtual net/rim/device/cldc/impl/softtoken/rimsecuridlib/RimSecurIDLib.getTokenPasscode (ILjava/lang/String;ILjava/lang/String;)Ljava/lang/String;
      // 066: astore 7
      // 068: goto 088
      // 06b: aload 0
      // 06c: getfield net/rim/device/cldc/impl/softtoken/SoftTokenDialogImpl._rimSecurIDLib Lnet/rim/device/cldc/impl/softtoken/rimsecuridlib/RimSecurIDLib;
      // 06f: aload 0
      // 070: getfield net/rim/device/cldc/impl/softtoken/SoftTokenDialogImpl._softToken Lnet/rim/device/cldc/impl/softtoken/SoftTokenImpl;
      // 073: getfield net/rim/device/cldc/impl/softtoken/SoftTokenImpl._tag I
      // 076: aload 0
      // 077: getfield net/rim/device/cldc/impl/softtoken/SoftTokenDialogImpl._softToken Lnet/rim/device/cldc/impl/softtoken/SoftTokenImpl;
      // 07a: invokevirtual net/rim/device/cldc/impl/softtoken/SoftTokenImpl.getPassphrase ()Ljava/lang/String;
      // 07d: aload 0
      // 07e: invokespecial net/rim/device/cldc/impl/softtoken/SoftTokenDialogImpl.getRimSecurIDLibPasscodeOffset ()I
      // 081: aload 6
      // 083: invokevirtual net/rim/device/cldc/impl/softtoken/rimsecuridlib/RimSecurIDLib.getTokenPasscode (ILjava/lang/String;ILjava/lang/String;)Ljava/lang/String;
      // 086: astore 7
      // 088: aload 0
      // 089: getfield net/rim/device/cldc/impl/softtoken/SoftTokenDialogImpl._currentCodeField Lnet/rim/device/api/ui/component/BasicEditField;
      // 08c: invokevirtual net/rim/device/api/ui/component/BasicEditField.getText ()Ljava/lang/String;
      // 08f: aload 7
      // 091: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 094: ifne 0e8
      // 097: aload 0
      // 098: getfield net/rim/device/cldc/impl/softtoken/SoftTokenDialogImpl._promptResponseField Lnet/rim/device/api/ui/component/EditField;
      // 09b: invokevirtual net/rim/device/api/ui/component/BasicEditField.getText ()Ljava/lang/String;
      // 09e: invokevirtual java/lang/String.length ()I
      // 0a1: ifle 0c1
      // 0a4: aload 0
      // 0a5: getfield net/rim/device/cldc/impl/softtoken/SoftTokenDialogImpl._currentCodeField Lnet/rim/device/api/ui/component/BasicEditField;
      // 0a8: invokevirtual net/rim/device/api/ui/component/BasicEditField.getText ()Ljava/lang/String;
      // 0ab: aload 0
      // 0ac: getfield net/rim/device/cldc/impl/softtoken/SoftTokenDialogImpl._promptResponseField Lnet/rim/device/api/ui/component/EditField;
      // 0af: invokevirtual net/rim/device/api/ui/component/BasicEditField.getText ()Ljava/lang/String;
      // 0b2: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 0b5: ifeq 0c1
      // 0b8: aload 0
      // 0b9: getfield net/rim/device/cldc/impl/softtoken/SoftTokenDialogImpl._promptResponseField Lnet/rim/device/api/ui/component/EditField;
      // 0bc: aload 7
      // 0be: invokevirtual net/rim/device/api/ui/component/BasicEditField.setText (Ljava/lang/String;)V
      // 0c1: aload 0
      // 0c2: getfield net/rim/device/cldc/impl/softtoken/SoftTokenDialogImpl._currentCodeField Lnet/rim/device/api/ui/component/BasicEditField;
      // 0c5: aload 7
      // 0c7: invokevirtual net/rim/device/api/ui/component/BasicEditField.setText (Ljava/lang/String;)V
      // 0ca: bipush 1
      // 0cb: istore 4
      // 0cd: goto 0e8
      // 0d0: astore 5
      // 0d2: ldc_w 1162892105
      // 0d5: istore 3
      // 0d6: goto 0e8
      // 0d9: astore 5
      // 0db: ldc_w 1162892100
      // 0de: istore 3
      // 0df: goto 0e8
      // 0e2: astore 5
      // 0e4: ldc_w 1162892112
      // 0e7: istore 3
      // 0e8: iload 3
      // 0e9: ifeq 0f8
      // 0ec: aload 0
      // 0ed: getfield net/rim/device/cldc/impl/softtoken/SoftTokenDialogImpl._tokenManager Lnet/rim/device/cldc/impl/softtoken/SoftTokenManagerImpl;
      // 0f0: pop
      // 0f1: iload 3
      // 0f2: aload 2
      // 0f3: bipush 2
      // 0f5: invokestatic net/rim/device/cldc/impl/api/SoftTokenManager.logEvent (ILjava/lang/String;I)V
      // 0f8: iload 4
      // 0fa: aload 1
      // 0fb: monitorexit
      // 0fc: ireturn
      // 0fd: astore 8
      // 0ff: aload 1
      // 100: monitorexit
      // 101: aload 8
      // 103: athrow
      // try (11 -> 92): 93 null
      // try (11 -> 92): 97 null
      // try (11 -> 92): 101 null
      // try (5 -> 116): 117 null
      // try (117 -> 120): 117 null
   }

   public SoftToken getSoftToken() {
      return this._softToken;
   }

   @Override
   public String getUserName() {
      return this._usernameText;
   }

   @Override
   public String getChallengeResponse() {
      return this._responseText;
   }

   @Override
   public void fieldChanged(Field field, int context) {
      synchronized (this._eventLock) {
         if (this._softToken != null) {
            if (field != this._pinField) {
               if (field == this._typeChoiceField) {
                  this.updateCurrentCode();
                  this._promptResponseField.setText(this._currentCodeField.getText());
               } else if (field == this._useCurrentCodeButton) {
                  this._promptResponseField.setText(this._currentCodeField.getText());
               } else if (field == this._okButton) {
                  this.doOkButton();
               } else if (field == this._cancelButton) {
                  this.doCancelButton();
               }
            } else {
               int pinLength = this._pinField.getText().length();
               int selectedIndex = this._typeChoiceField.getSelectedIndex();
               if (pinLength >= 4 && pinLength <= 8) {
                  String[] useChoices = _passcodeUseChoices;
                  this._typeChoiceField.setChoices(useChoices);
                  if (selectedIndex == 0) {
                     this._typeChoiceField.setSelectedIndex(3);
                  } else if (selectedIndex == 1) {
                     this._typeChoiceField.setSelectedIndex(4);
                  } else if (selectedIndex == 2) {
                     this._typeChoiceField.setSelectedIndex(5);
                  } else {
                     this._typeChoiceField.setSelectedIndex(selectedIndex);
                  }
               } else {
                  String[] useChoices = _tokencodeUseChoices;
                  this._typeChoiceField.setChoices(useChoices);
                  if (selectedIndex == 3) {
                     this._typeChoiceField.setSelectedIndex(0);
                  } else if (selectedIndex == 4) {
                     this._typeChoiceField.setSelectedIndex(1);
                  } else if (selectedIndex == 5) {
                     this._typeChoiceField.setSelectedIndex(2);
                  } else {
                     this._typeChoiceField.setSelectedIndex(selectedIndex);
                  }
               }
            }
         }
      }
   }

   private void doOkButton() {
      String PIN = this._pinField.getText();
      int pinLength = PIN.length();
      if (pinLength <= 0 || pinLength >= 4) {
         if (pinLength == 0 || pinLength >= 4 && !PIN.equals(this._softToken.getPIN())) {
            this._softToken.setPIN(PIN);
            this._tokenManager.saveSecrets();
         }

         this._usernameText = this._usernameField.getText();
         this._responseText = this._promptResponseField.getText();
         this.close(0);
      }
   }

   private void doCancelButton() {
      this._usernameText = null;
      this._responseText = null;
      this.close(-1);
   }

   @Override
   protected boolean keyChar(char c, int status, int time) {
      synchronized (this._eventLock) {
         switch (c) {
            case '\n':
               Field field = this.getFieldWithFocus();
               if (field == this._usernameField) {
                  this._promptResponseField.setFocus();
               } else if (field == this._promptResponseField) {
                  this._pinField.setFocus();
               } else if (field == this._pinField) {
                  this._currentCodeField.setFocus();
               } else if (field == this._currentCodeField) {
                  this._typeChoiceField.setFocus();
               } else if (field == this._typeChoiceField) {
                  if (this._useCurrentCodeButton != null) {
                     this._useCurrentCodeButton.setFocus();
                  } else {
                     this._okButton.setFocus();
                  }
               } else if (field == this._useCurrentCodeButton) {
                  this._promptResponseField.setText(this._currentCodeField.getText());
               } else if (field instanceof HorizontalFieldManager) {
                  field = ((HorizontalFieldManager)field).getFieldWithFocus();
                  if (field == this._okButton) {
                     this.doOkButton();
                  } else if (field == this._cancelButton) {
                     this.doCancelButton();
                  }
               }

               return true;
            case '\u001b':
               if (System.currentTimeMillis() - this._constructionTime >= 3000) {
                  this.doCancelButton();
               }

               return true;
            default:
               return super.keyChar(c, status, time);
         }
      }
   }

   @Override
   protected void close(int closeReason) {
      this._gaugeUpdateThread._run = false;
      super.close(closeReason);
   }
}
