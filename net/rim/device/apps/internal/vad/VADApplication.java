package net.rim.device.apps.internal.vad;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationProcess;
import net.rim.device.api.system.Backlight;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.GaugeField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.ui.component.VerticalSpacerField;
import net.rim.device.internal.vad.VADLanguageSetting;
import net.rim.device.internal.vad.VADUserEvents;
import net.rim.vm.Process;

public final class VADApplication extends UiApplication {
   private RichTextField _rtf1;
   private RichTextField _rtf2;
   private VADApplication$VADMainScreen _mainScreen;
   private VADApplication$ChoiceListField _listField;
   private VADApplication$ListeningScreen _listeningScreen;
   private GaugeField _gaugeField;
   private GaugeField _progressField;
   private int _uiState;
   private String _context;
   private String[] _listeningChoices;
   private VADEngineManager _manager = VADEngineManager.getInstance();
   private static final int UI_STATE_NONE = 0;
   private static final int UI_STATE_COMMAND = 1;
   private static final int UI_STATE_NAME_OR_NUMBER = 2;
   private static final int UI_STATE_REPEAT_NAME_OR_NUMBER = 3;
   private static final int UI_STATE_NO_MATCH_FOUND = 4;
   private static final int UI_STATE_CALL = 5;
   private static final int UI_STATE_WHICH_NUMBER = 6;
   private static final int UI_STATE_VOICE_PROMPTS_OFF = 7;
   private static final int UI_STATE_VOICE_PROMPTS_ON = 8;
   private static final int UI_STATE_STATUS = 9;
   private static final int UI_STATE_CALLING = 10;
   private static final int UI_STATE_STATUS_DISPLAY = 11;
   private static final int UI_STATE_ADAPT_DIGITS_START = 12;
   private static final int UI_STATE_ADAPT_DIGITS_1 = 13;
   private static final int UI_STATE_ADAPT_DIGITS_2 = 14;
   private static final int UI_STATE_ADAPT_DIGITS_3 = 15;
   private static final int UI_STATE_ADAPT_DIGITS_4 = 16;
   private static final int UI_STATE_ADAPT_DIGITS_5 = 17;
   private static final int UI_STATE_ADAPT_DIGITS_6 = 18;
   private static final int UI_STATE_ADAPT_DIGITS_7 = 19;
   private static final int UI_STATE_ADAPT_DIGITS_8 = 20;
   private static final int UI_STATE_ADAPT_DIGITS_9 = 21;
   private static final int UI_STATE_ADAPT_DIGITS_10 = 22;
   private static final int UI_STATE_ADAPT_DIGITS_OK = 23;
   private static final int UI_STATE_ADAPT_DIGITS_COMPLETE = 24;
   private static final int UI_STATE_ADAPT_DIGITS_PLAYBACK = 25;
   private static final int UI_STATE_STATUS_PHONE_NUMBER = 28;
   private static final int UI_STATE_REBUILDING_ADDRESS_BOOK = 29;
   private static final int UI_STATE_ADAPT_DIGITS_TIME_EXPIRED = 30;
   private static final int UI_STATE_NO_NUMBER_AVAILABLE = 31;
   private static final int UI_STATE_NO_ENTRY_STORED = 32;
   private static final int UI_STATE_INITIALIZING = 33;
   private static final boolean DEBUG_UI = false;

   public static final void main(String[] args) {
      if (args != null && args.length != 0) {
         if (InternalServices.isSoftwareCapable(11)) {
            UiApplication app = new VADApplication();
            app.enterEventDispatcher();
         }
      } else {
         if (!DeviceInfo.isInHolster()) {
            VADUserEvents.sendEvent(0, 0);
         }
      }
   }

   VADApplication() {
      this._manager.setApplication(this);
      ApplicationProcess applicationProcess = (ApplicationProcess)Process.currentProcess();
      if (applicationProcess != null) {
         applicationProcess.addCleanupRunnable(new VADApplication$VADCleanupRunnable(this));
      }

      this._mainScreen = new VADApplication$VADMainScreen(this);
      this._rtf1 = new RichTextField(36028797018963968L);
      this._mainScreen.add(this._rtf1);
      this._mainScreen.add(new VerticalSpacerField(this._mainScreen.getFont().getHeight()));
      this._rtf2 = new RichTextField(36028848558833664L);
      this._mainScreen.add(this._rtf2);
      this._gaugeField = new GaugeField(null, 0, 10, 1, 8);
      this.pushScreen(this._mainScreen);
   }

   @Override
   public final boolean acceptsForeground() {
      return this._uiState != 0;
   }

   @Override
   public final void deactivate() {
      super.deactivate();
      this._rtf1.setText(null);
      this._rtf2.setText(null);
      this._mainScreen.setStatus(null);
      this.clearFields();
      this._uiState = 0;
      this._manager.sendEvent(28);
   }

   private final void clearFields() {
      if (this._listeningScreen != null) {
         this.popScreen(this._listeningScreen);
         this._listeningScreen = null;
      }

      if (this._listField != null) {
         this._mainScreen.delete(this._listField);
         this._listField = null;
      }

      if (this._progressField != null) {
         this._mainScreen.delete(this._progressField);
         this._progressField = null;
      }

      this._rtf2.setText(null);
   }

   final void setUIProgress(int percent) {
      if (this._progressField != null) {
         synchronized (this.getAppEventLock()) {
            this._progressField.setValue(percent);
         }
      }
   }

   final void setUIState(int state) {
      this.setUIState(state, false);
   }

   final void setUIState(int state, boolean listening) {
      synchronized (this.getAppEventLock()) {
         if (listening) {
            if (this._listeningScreen != null) {
               this.popScreen(this._listeningScreen);
            }

            this._listeningScreen = new VADApplication$ListeningScreen(this, state);
            this.pushScreen(this._listeningScreen);
         } else {
            if (this._listeningScreen != null) {
               this.popScreen(this._listeningScreen);
               this._listeningScreen = null;
            }

            if (state == this._uiState) {
               return;
            }

            if (this._uiState == 1) {
               this._mainScreen.setStatus(null);
            }

            switch (state) {
               case -1:
               case 26:
               case 27:
                  break;
               case 0:
               default:
                  this.requestBackground();
                  this.clearFields();
                  break;
               case 1:
                  if (this._uiState == 0) {
                     this.requestForeground();
                  }

                  this.clearFields();
                  this._rtf1.setText(this._manager.getVSTString(40));
                  break;
               case 2:
                  this.clearFields();
                  this._rtf1.setText(this._manager.getVSTString(45));
                  if (this._manager.isAddressBookEmpty()) {
                     this._rtf2.setText(this._manager.getResourceString(41));
                  }
                  break;
               case 3:
                  this.clearFields();
                  this._rtf1.setText(this._manager.getVSTString(46));
                  if (this._manager.isAddressBookEmpty()) {
                     this._rtf2.setText(this._manager.getResourceString(41));
                  }
                  break;
               case 4:
                  this.clearFields();
                  this._rtf1.setText(this._manager.getResourceString(8));
                  break;
               case 5:
                  if (this._listeningScreen != null) {
                     this.popScreen(this._listeningScreen);
                     this._listeningScreen = null;
                  }

                  this._rtf1.setText(this._manager.getVSTString(4));
                  this._rtf2.setText(null);
                  break;
               case 6:
                  this.clearFields();
                  Object[] args = new Object[]{this._context};
                  this._rtf1.setText(MessageFormat.format(this._manager.getResourceString(27), args));
                  break;
               case 7:
                  this.clearFields();
                  this._rtf1.setText(this._manager.getVSTString(18));
                  this._manager.getParameters()._playPrompts = false;
                  this._manager.commitPersistentData();
                  break;
               case 8:
                  this.clearFields();
                  this._rtf1.setText(this._manager.getVSTString(17));
                  this._manager.getParameters()._playPrompts = true;
                  this._manager.commitPersistentData();
                  break;
               case 9:
                  this.clearFields();
                  this._rtf1.setText(this._manager.getVSTString(6));
                  break;
               case 10:
                  this.clearFields();
                  this._rtf1.setText(this._manager.getVSTString(1));
                  break;
               case 11:
                  this.clearFields();
                  this._rtf1.setText(this._context);
                  break;
               case 12:
                  if (this._uiState == 0) {
                     this.requestForeground();
                  }

                  this.clearFields();
                  this._rtf1.setText(this._manager.getResourceString(29));
                  this._rtf2.setText(this._manager.getResourceString(30));
                  this._mainScreen.setStatus(new LabelField(this._context, 1152921504606846980L));
                  break;
               case 13:
               case 14:
               case 15:
               case 16:
               case 17:
               case 18:
               case 19:
               case 20:
               case 21:
               case 22:
                  this.clearFields();
                  this._rtf1.setText(this._manager.getVSTString(39));
                  this._rtf2.setText(this._context);
                  int index = state - 13;
                  this._gaugeField.setValue(index + 1);
                  this._mainScreen.setStatus(this._gaugeField);
                  break;
               case 23:
                  this.clearFields();
                  this._rtf1.setText(this._manager.getVSTString(35));
                  break;
               case 24:
                  this.clearFields();
                  this._rtf1.setText(this._manager.getResourceString(34));
                  break;
               case 25:
                  this.clearFields();
                  this._rtf1.setText(this._manager.getVSTString(38));
                  break;
               case 28:
                  this.clearFields();
                  String number = PhoneUtilities.getDevicePhoneNumber(true);
                  if (number == null) {
                     number = this._manager.getResourceString(28);
                  }

                  this._rtf1.setText(this._manager.getVSTString(14) + ": " + number);
                  break;
               case 29:
                  if (this._uiState == 0) {
                     this.requestForeground();
                  }

                  this._mainScreen.setTitle(new LabelField(this._manager.getResourceString(0)));
                  this.clearFields();
                  this._rtf1.setText(this._manager.getResourceString(35));
                  this._progressField = new GaugeField(null, 0, 100, 0, 4);
                  this._mainScreen.add(this._progressField);
                  break;
               case 30:
                  this.clearFields();
                  this._rtf1.setText(this._manager.getVSTString(43));
                  break;
               case 31:
                  this.clearFields();
                  this._rtf1.setText(this._manager.getVSTString(41));
                  break;
               case 32:
                  this.clearFields();
                  this._rtf1.setText(this._manager.getVSTString(2));
                  break;
               case 33:
                  if (this._uiState == 0) {
                     this.requestForeground();
                  }

                  this._mainScreen.setTitle(new LabelField(this._manager.getResourceString(0)));
                  this.clearFields();
                  this._rtf1.setText(this._manager.getResourceString(32));
                  this._rtf2.setText("");
                  LabelField lf = new LabelField(this._manager.getResourceString(40), 12884901888L);
                  Font f = lf.getFont().derive(0, 15);
                  lf.setFont(f);
                  this._mainScreen.setStatus(lf);
            }
         }
      }

      this._uiState = state;
   }

   final void setChoices(String[] choices) {
      synchronized (this.getAppEventLock()) {
         if (this._listField == null) {
            this._listField = new VADApplication$ChoiceListField(this, choices);
            this._mainScreen.insert(this._listField, 1);
         } else {
            this._listField.incrementSelectedIndex();
         }
      }
   }

   final void setListeningChoices(String[] choices) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   final void setContext(String context) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   final void uiExit() {
      this.setUIState(0);
   }

   final void uiInitializing() {
      if (!ApplicationManager.getApplicationManager().isSystemLocked()) {
         Backlight.enable(true);
      }

      this.setUIState(33);
   }

   final void uiRebuildingAddressBook() {
      this.setUIState(29);
   }

   final void uiError(int error) {
      int rc = 0;
      String prompt = null;
      switch (error) {
         case 1:
         case 4:
         case 6:
            rc = 37;
            break;
         case 2:
         default:
            rc = 31;
            break;
         case 3:
            rc = 33;
            break;
         case 5:
            rc = 39;
            break;
         case 7:
            String[] info = VADLanguageSetting.getInstance().getVersionInfo();
            if (info != null && info.length > 2) {
               prompt = "VSuite version mismatch; expected 0238 got " + info[2];
            } else {
               prompt = "VSuite version mismatch; no version number available";
            }
            break;
         case 8:
            rc = 42;
      }

      if (prompt == null) {
         prompt = this._manager.getResourceString(rc);
      }

      Status.show(prompt, Bitmap.getPredefinedBitmap(2), 3000, 33554432, true, false, -2147483646);
      this.uiExit();
   }

   private final boolean strEquals(String s, int index) {
      return s.equals(this._manager.getVSTString(index));
   }

   final void uiUpdate(String param1, byte[][] param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: invokestatic net/rim/device/api/system/Backlight.resetElapsedTime ()V
      // 003: aload 1
      // 004: ifnonnull 00a
      // 007: goto 48a
      // 00a: aload 1
      // 00b: invokevirtual java/lang/String.length ()I
      // 00e: ifne 014
      // 011: goto 48a
      // 014: aload 2
      // 015: ifnull 018
      // 018: bipush -1
      // 01a: istore 3
      // 01b: aload 0
      // 01c: aload 1
      // 01d: bipush 19
      // 01f: invokespecial net/rim/device/apps/internal/vad/VADApplication.strEquals (Ljava/lang/String;I)Z
      // 022: ifeq 02a
      // 025: bipush 0
      // 026: istore 3
      // 027: goto 41c
      // 02a: aload 0
      // 02b: aload 1
      // 02c: bipush 40
      // 02e: invokespecial net/rim/device/apps/internal/vad/VADApplication.strEquals (Ljava/lang/String;I)Z
      // 031: ifeq 039
      // 034: bipush 1
      // 035: istore 3
      // 036: goto 41c
      // 039: aload 0
      // 03a: aload 1
      // 03b: bipush 45
      // 03d: invokespecial net/rim/device/apps/internal/vad/VADApplication.strEquals (Ljava/lang/String;I)Z
      // 040: ifeq 049
      // 043: bipush 2
      // 045: istore 3
      // 046: goto 41c
      // 049: aload 0
      // 04a: aload 1
      // 04b: bipush 46
      // 04d: invokespecial net/rim/device/apps/internal/vad/VADApplication.strEquals (Ljava/lang/String;I)Z
      // 050: ifeq 059
      // 053: bipush 3
      // 055: istore 3
      // 056: goto 41c
      // 059: aload 0
      // 05a: aload 1
      // 05b: bipush 37
      // 05d: invokespecial net/rim/device/apps/internal/vad/VADApplication.strEquals (Ljava/lang/String;I)Z
      // 060: ifeq 069
      // 063: bipush 4
      // 065: istore 3
      // 066: goto 41c
      // 069: aload 0
      // 06a: aload 1
      // 06b: bipush 20
      // 06d: invokespecial net/rim/device/apps/internal/vad/VADApplication.strEquals (Ljava/lang/String;I)Z
      // 070: ifeq 08c
      // 073: aload 2
      // 074: arraylength
      // 075: bipush 6
      // 077: if_icmpgt 086
      // 07a: aload 0
      // 07b: aload 0
      // 07c: aload 2
      // 07d: bipush 0
      // 07e: bipush 0
      // 07f: bipush 0
      // 080: invokespecial net/rim/device/apps/internal/vad/VADApplication.extractChoices ([[BIIZ)[Ljava/lang/String;
      // 083: invokevirtual net/rim/device/apps/internal/vad/VADApplication.setChoices ([Ljava/lang/String;)V
      // 086: bipush 5
      // 088: istore 3
      // 089: goto 41c
      // 08c: aload 0
      // 08d: aload 1
      // 08e: bipush 6
      // 090: invokespecial net/rim/device/apps/internal/vad/VADApplication.strEquals (Ljava/lang/String;I)Z
      // 093: ifne 099
      // 096: goto 193
      // 099: aload 2
      // 09a: ifnonnull 0a3
      // 09d: bipush 9
      // 09f: istore 3
      // 0a0: goto 41c
      // 0a3: aload 2
      // 0a4: arraylength
      // 0a5: bipush 2
      // 0a7: if_icmpgt 0ad
      // 0aa: goto 142
      // 0ad: new java/lang/String
      // 0b0: dup
      // 0b1: aload 2
      // 0b2: bipush 0
      // 0b3: aaload
      // 0b4: bipush 0
      // 0b5: aload 2
      // 0b6: bipush 0
      // 0b7: aaload
      // 0b8: arraylength
      // 0b9: ldc_w "UnicodeLittleUnmarked"
      // 0bc: invokespecial java/lang/String.<init> ([BIILjava/lang/String;)V
      // 0bf: astore 4
      // 0c1: aload 0
      // 0c2: aload 4
      // 0c4: bipush 36
      // 0c6: invokespecial net/rim/device/apps/internal/vad/VADApplication.strEquals (Ljava/lang/String;I)Z
      // 0c9: ifeq 0d2
      // 0cc: bipush 9
      // 0ce: istore 3
      // 0cf: goto 41c
      // 0d2: bipush 11
      // 0d4: istore 3
      // 0d5: new java/lang/StringBuffer
      // 0d8: dup
      // 0d9: invokespecial java/lang/StringBuffer.<init> ()V
      // 0dc: astore 5
      // 0de: bipush 0
      // 0df: istore 6
      // 0e1: iload 6
      // 0e3: aload 2
      // 0e4: arraylength
      // 0e5: if_icmpge 136
      // 0e8: aload 5
      // 0ea: new java/lang/String
      // 0ed: dup
      // 0ee: aload 2
      // 0ef: iload 6
      // 0f1: aaload
      // 0f2: bipush 0
      // 0f3: aload 2
      // 0f4: iload 6
      // 0f6: aaload
      // 0f7: arraylength
      // 0f8: ldc_w "UnicodeLittleUnmarked"
      // 0fb: invokespecial java/lang/String.<init> ([BIILjava/lang/String;)V
      // 0fe: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 101: pop
      // 102: aload 5
      // 104: ldc_w ": "
      // 107: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 10a: pop
      // 10b: iinc 6 1
      // 10e: aload 5
      // 110: new java/lang/String
      // 113: dup
      // 114: aload 2
      // 115: iload 6
      // 117: aaload
      // 118: bipush 0
      // 119: aload 2
      // 11a: iload 6
      // 11c: aaload
      // 11d: arraylength
      // 11e: ldc_w "UnicodeLittleUnmarked"
      // 121: invokespecial java/lang/String.<init> ([BIILjava/lang/String;)V
      // 124: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 127: pop
      // 128: aload 5
      // 12a: bipush 10
      // 12c: invokevirtual java/lang/StringBuffer.append (C)Ljava/lang/StringBuffer;
      // 12f: pop
      // 130: iinc 6 1
      // 133: goto 0e1
      // 136: aload 0
      // 137: aload 5
      // 139: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 13c: invokevirtual net/rim/device/apps/internal/vad/VADApplication.setContext (Ljava/lang/String;)V
      // 13f: goto 41c
      // 142: bipush 11
      // 144: istore 3
      // 145: new java/lang/StringBuffer
      // 148: dup
      // 149: invokespecial java/lang/StringBuffer.<init> ()V
      // 14c: astore 4
      // 14e: aload 4
      // 150: new java/lang/String
      // 153: dup
      // 154: aload 2
      // 155: bipush 0
      // 156: aaload
      // 157: bipush 0
      // 158: aload 2
      // 159: bipush 0
      // 15a: aaload
      // 15b: arraylength
      // 15c: ldc_w "UnicodeLittleUnmarked"
      // 15f: invokespecial java/lang/String.<init> ([BIILjava/lang/String;)V
      // 162: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 165: pop
      // 166: aload 4
      // 168: ldc_w ": "
      // 16b: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 16e: pop
      // 16f: aload 4
      // 171: new java/lang/String
      // 174: dup
      // 175: aload 2
      // 176: bipush 1
      // 177: aaload
      // 178: bipush 0
      // 179: aload 2
      // 17a: bipush 1
      // 17b: aaload
      // 17c: arraylength
      // 17d: ldc_w "UnicodeLittleUnmarked"
      // 180: invokespecial java/lang/String.<init> ([BIILjava/lang/String;)V
      // 183: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 186: pop
      // 187: aload 0
      // 188: aload 4
      // 18a: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 18d: invokevirtual net/rim/device/apps/internal/vad/VADApplication.setContext (Ljava/lang/String;)V
      // 190: goto 41c
      // 193: aload 0
      // 194: aload 1
      // 195: bipush 47
      // 197: invokespecial net/rim/device/apps/internal/vad/VADApplication.strEquals (Ljava/lang/String;I)Z
      // 19a: ifeq 20d
      // 19d: bipush 11
      // 19f: istore 3
      // 1a0: new java/lang/StringBuffer
      // 1a3: dup
      // 1a4: invokespecial java/lang/StringBuffer.<init> ()V
      // 1a7: astore 4
      // 1a9: bipush 0
      // 1aa: istore 5
      // 1ac: iload 5
      // 1ae: aload 2
      // 1af: arraylength
      // 1b0: if_icmpge 201
      // 1b3: aload 4
      // 1b5: new java/lang/String
      // 1b8: dup
      // 1b9: aload 2
      // 1ba: iload 5
      // 1bc: aaload
      // 1bd: bipush 0
      // 1be: aload 2
      // 1bf: iload 5
      // 1c1: aaload
      // 1c2: arraylength
      // 1c3: ldc_w "UnicodeLittleUnmarked"
      // 1c6: invokespecial java/lang/String.<init> ([BIILjava/lang/String;)V
      // 1c9: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 1cc: pop
      // 1cd: aload 4
      // 1cf: ldc_w ": "
      // 1d2: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 1d5: pop
      // 1d6: iinc 5 1
      // 1d9: aload 4
      // 1db: new java/lang/String
      // 1de: dup
      // 1df: aload 2
      // 1e0: iload 5
      // 1e2: aaload
      // 1e3: bipush 0
      // 1e4: aload 2
      // 1e5: iload 5
      // 1e7: aaload
      // 1e8: arraylength
      // 1e9: ldc_w "UnicodeLittleUnmarked"
      // 1ec: invokespecial java/lang/String.<init> ([BIILjava/lang/String;)V
      // 1ef: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 1f2: pop
      // 1f3: aload 4
      // 1f5: bipush 10
      // 1f7: invokevirtual java/lang/StringBuffer.append (C)Ljava/lang/StringBuffer;
      // 1fa: pop
      // 1fb: iinc 5 1
      // 1fe: goto 1ac
      // 201: aload 0
      // 202: aload 4
      // 204: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 207: invokevirtual net/rim/device/apps/internal/vad/VADApplication.setContext (Ljava/lang/String;)V
      // 20a: goto 41c
      // 20d: aload 0
      // 20e: aload 1
      // 20f: bipush 14
      // 211: invokespecial net/rim/device/apps/internal/vad/VADApplication.strEquals (Ljava/lang/String;I)Z
      // 214: ifeq 21d
      // 217: bipush 28
      // 219: istore 3
      // 21a: goto 41c
      // 21d: aload 0
      // 21e: aload 1
      // 21f: bipush 42
      // 221: invokespecial net/rim/device/apps/internal/vad/VADApplication.strEquals (Ljava/lang/String;I)Z
      // 224: ifeq 259
      // 227: aload 2
      // 228: arraylength
      // 229: bipush 1
      // 22a: if_icmpne 246
      // 22d: aload 0
      // 22e: new java/lang/String
      // 231: dup
      // 232: aload 2
      // 233: bipush 0
      // 234: aaload
      // 235: bipush 0
      // 236: aload 2
      // 237: bipush 0
      // 238: aaload
      // 239: arraylength
      // 23a: ldc_w "UnicodeLittleUnmarked"
      // 23d: invokespecial java/lang/String.<init> ([BIILjava/lang/String;)V
      // 240: invokevirtual net/rim/device/apps/internal/vad/VADApplication.setContext (Ljava/lang/String;)V
      // 243: goto 253
      // 246: aload 0
      // 247: aload 0
      // 248: aload 2
      // 249: bipush 2
      // 24b: bipush 0
      // 24c: bipush 1
      // 24d: invokespecial net/rim/device/apps/internal/vad/VADApplication.extractChoices ([[BIIZ)[Ljava/lang/String;
      // 250: invokevirtual net/rim/device/apps/internal/vad/VADApplication.setListeningChoices ([Ljava/lang/String;)V
      // 253: bipush 6
      // 255: istore 3
      // 256: goto 41c
      // 259: aload 0
      // 25a: aload 1
      // 25b: bipush 1
      // 25c: invokespecial net/rim/device/apps/internal/vad/VADApplication.strEquals (Ljava/lang/String;I)Z
      // 25f: ifeq 268
      // 262: bipush 10
      // 264: istore 3
      // 265: goto 41c
      // 268: aload 0
      // 269: aload 1
      // 26a: bipush 16
      // 26c: invokespecial net/rim/device/apps/internal/vad/VADApplication.strEquals (Ljava/lang/String;I)Z
      // 26f: ifeq 28a
      // 272: aload 0
      // 273: getfield net/rim/device/apps/internal/vad/VADApplication._manager Lnet/rim/device/apps/internal/vad/VADEngineManager;
      // 276: invokevirtual net/rim/device/apps/internal/vad/VADEngineManager.getParameters ()Lnet/rim/device/internal/vad/VADParameters;
      // 279: getfield net/rim/device/internal/vad/VADParameters._playPrompts Z
      // 27c: ifeq 284
      // 27f: bipush 7
      // 281: goto 286
      // 284: bipush 8
      // 286: istore 3
      // 287: goto 41c
      // 28a: aload 0
      // 28b: aload 1
      // 28c: bipush 32
      // 28e: invokespecial net/rim/device/apps/internal/vad/VADApplication.strEquals (Ljava/lang/String;I)Z
      // 291: ifeq 2c2
      // 294: bipush 12
      // 296: istore 3
      // 297: aload 2
      // 298: arraylength
      // 299: ifgt 29f
      // 29c: goto 41c
      // 29f: new java/lang/String
      // 2a2: dup
      // 2a3: aload 2
      // 2a4: aload 2
      // 2a5: arraylength
      // 2a6: bipush 1
      // 2a7: isub
      // 2a8: aaload
      // 2a9: bipush 0
      // 2aa: aload 2
      // 2ab: aload 2
      // 2ac: arraylength
      // 2ad: bipush 1
      // 2ae: isub
      // 2af: aaload
      // 2b0: arraylength
      // 2b1: ldc_w "UnicodeLittleUnmarked"
      // 2b4: invokespecial java/lang/String.<init> ([BIILjava/lang/String;)V
      // 2b7: astore 4
      // 2b9: aload 0
      // 2ba: aload 4
      // 2bc: invokevirtual net/rim/device/apps/internal/vad/VADApplication.setContext (Ljava/lang/String;)V
      // 2bf: goto 41c
      // 2c2: aload 0
      // 2c3: aload 1
      // 2c4: bipush 39
      // 2c6: invokespecial net/rim/device/apps/internal/vad/VADApplication.strEquals (Ljava/lang/String;I)Z
      // 2c9: ifne 2cf
      // 2cc: goto 39d
      // 2cf: aload 2
      // 2d0: arraylength
      // 2d1: bipush 2
      // 2d3: if_icmpge 2d9
      // 2d6: goto 41c
      // 2d9: new java/lang/String
      // 2dc: dup
      // 2dd: aload 2
      // 2de: bipush 0
      // 2df: aaload
      // 2e0: bipush 0
      // 2e1: aload 2
      // 2e2: bipush 0
      // 2e3: aaload
      // 2e4: arraylength
      // 2e5: ldc_w "UnicodeLittleUnmarked"
      // 2e8: invokespecial java/lang/String.<init> ([BIILjava/lang/String;)V
      // 2eb: astore 4
      // 2ed: aload 0
      // 2ee: aload 4
      // 2f0: invokevirtual net/rim/device/apps/internal/vad/VADApplication.setContext (Ljava/lang/String;)V
      // 2f3: aload 0
      // 2f4: aload 4
      // 2f6: bipush 22
      // 2f8: invokespecial net/rim/device/apps/internal/vad/VADApplication.strEquals (Ljava/lang/String;I)Z
      // 2fb: ifeq 304
      // 2fe: bipush 13
      // 300: istore 3
      // 301: goto 41c
      // 304: aload 0
      // 305: aload 4
      // 307: bipush 23
      // 309: invokespecial net/rim/device/apps/internal/vad/VADApplication.strEquals (Ljava/lang/String;I)Z
      // 30c: ifeq 315
      // 30f: bipush 14
      // 311: istore 3
      // 312: goto 41c
      // 315: aload 0
      // 316: aload 4
      // 318: bipush 24
      // 31a: invokespecial net/rim/device/apps/internal/vad/VADApplication.strEquals (Ljava/lang/String;I)Z
      // 31d: ifeq 326
      // 320: bipush 15
      // 322: istore 3
      // 323: goto 41c
      // 326: aload 0
      // 327: aload 4
      // 329: bipush 25
      // 32b: invokespecial net/rim/device/apps/internal/vad/VADApplication.strEquals (Ljava/lang/String;I)Z
      // 32e: ifeq 337
      // 331: bipush 16
      // 333: istore 3
      // 334: goto 41c
      // 337: aload 0
      // 338: aload 4
      // 33a: bipush 26
      // 33c: invokespecial net/rim/device/apps/internal/vad/VADApplication.strEquals (Ljava/lang/String;I)Z
      // 33f: ifeq 348
      // 342: bipush 17
      // 344: istore 3
      // 345: goto 41c
      // 348: aload 0
      // 349: aload 4
      // 34b: bipush 27
      // 34d: invokespecial net/rim/device/apps/internal/vad/VADApplication.strEquals (Ljava/lang/String;I)Z
      // 350: ifeq 359
      // 353: bipush 18
      // 355: istore 3
      // 356: goto 41c
      // 359: aload 0
      // 35a: aload 4
      // 35c: bipush 28
      // 35e: invokespecial net/rim/device/apps/internal/vad/VADApplication.strEquals (Ljava/lang/String;I)Z
      // 361: ifeq 36a
      // 364: bipush 19
      // 366: istore 3
      // 367: goto 41c
      // 36a: aload 0
      // 36b: aload 4
      // 36d: bipush 29
      // 36f: invokespecial net/rim/device/apps/internal/vad/VADApplication.strEquals (Ljava/lang/String;I)Z
      // 372: ifeq 37b
      // 375: bipush 20
      // 377: istore 3
      // 378: goto 41c
      // 37b: aload 0
      // 37c: aload 4
      // 37e: bipush 30
      // 380: invokespecial net/rim/device/apps/internal/vad/VADApplication.strEquals (Ljava/lang/String;I)Z
      // 383: ifeq 38c
      // 386: bipush 21
      // 388: istore 3
      // 389: goto 41c
      // 38c: aload 0
      // 38d: aload 4
      // 38f: bipush 31
      // 391: invokespecial net/rim/device/apps/internal/vad/VADApplication.strEquals (Ljava/lang/String;I)Z
      // 394: ifeq 41c
      // 397: bipush 22
      // 399: istore 3
      // 39a: goto 41c
      // 39d: aload 0
      // 39e: aload 1
      // 39f: bipush 38
      // 3a1: invokespecial net/rim/device/apps/internal/vad/VADApplication.strEquals (Ljava/lang/String;I)Z
      // 3a4: ifeq 3ad
      // 3a7: bipush 25
      // 3a9: istore 3
      // 3aa: goto 41c
      // 3ad: aload 0
      // 3ae: aload 1
      // 3af: bipush 34
      // 3b1: invokespecial net/rim/device/apps/internal/vad/VADApplication.strEquals (Ljava/lang/String;I)Z
      // 3b4: ifeq 3bd
      // 3b7: bipush 23
      // 3b9: istore 3
      // 3ba: goto 41c
      // 3bd: aload 0
      // 3be: aload 1
      // 3bf: bipush 33
      // 3c1: invokespecial net/rim/device/apps/internal/vad/VADApplication.strEquals (Ljava/lang/String;I)Z
      // 3c4: ifeq 3cd
      // 3c7: bipush 24
      // 3c9: istore 3
      // 3ca: goto 41c
      // 3cd: aload 0
      // 3ce: aload 1
      // 3cf: bipush 43
      // 3d1: invokespecial net/rim/device/apps/internal/vad/VADApplication.strEquals (Ljava/lang/String;I)Z
      // 3d4: ifeq 3dd
      // 3d7: bipush 30
      // 3d9: istore 3
      // 3da: goto 41c
      // 3dd: aload 0
      // 3de: aload 1
      // 3df: bipush 41
      // 3e1: invokespecial net/rim/device/apps/internal/vad/VADApplication.strEquals (Ljava/lang/String;I)Z
      // 3e4: ifeq 3ed
      // 3e7: bipush 31
      // 3e9: istore 3
      // 3ea: goto 41c
      // 3ed: aload 0
      // 3ee: aload 1
      // 3ef: bipush 2
      // 3f1: invokespecial net/rim/device/apps/internal/vad/VADApplication.strEquals (Ljava/lang/String;I)Z
      // 3f4: ifeq 3fd
      // 3f7: bipush 32
      // 3f9: istore 3
      // 3fa: goto 41c
      // 3fd: aload 0
      // 3fe: aload 1
      // 3ff: bipush 44
      // 401: invokespecial net/rim/device/apps/internal/vad/VADApplication.strEquals (Ljava/lang/String;I)Z
      // 404: ifeq 40d
      // 407: bipush 31
      // 409: istore 3
      // 40a: goto 41c
      // 40d: aload 0
      // 40e: aload 1
      // 40f: bipush 21
      // 411: invokespecial net/rim/device/apps/internal/vad/VADApplication.strEquals (Ljava/lang/String;I)Z
      // 414: ifeq 41c
      // 417: aload 0
      // 418: getfield net/rim/device/apps/internal/vad/VADApplication._uiState I
      // 41b: istore 3
      // 41c: iload 3
      // 41d: bipush -1
      // 41f: if_icmpeq 48a
      // 422: bipush 0
      // 423: istore 4
      // 425: aload 2
      // 426: ifnull 46b
      // 429: aload 0
      // 42a: getfield net/rim/device/apps/internal/vad/VADApplication._manager Lnet/rim/device/apps/internal/vad/VADEngineManager;
      // 42d: bipush 36
      // 42f: invokevirtual net/rim/device/apps/internal/vad/VADEngineManager.getVSTString (I)Ljava/lang/String;
      // 432: astore 5
      // 434: aload 2
      // 435: arraylength
      // 436: bipush 1
      // 437: isub
      // 438: istore 6
      // 43a: iload 6
      // 43c: iflt 46b
      // 43f: new java/lang/String
      // 442: dup
      // 443: aload 2
      // 444: iload 6
      // 446: aaload
      // 447: bipush 0
      // 448: aload 2
      // 449: iload 6
      // 44b: aaload
      // 44c: arraylength
      // 44d: ldc_w "UnicodeLittleUnmarked"
      // 450: invokespecial java/lang/String.<init> ([BIILjava/lang/String;)V
      // 453: astore 7
      // 455: aload 5
      // 457: aload 7
      // 459: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 45c: ifeq 465
      // 45f: bipush 1
      // 460: istore 4
      // 462: goto 46b
      // 465: iinc 6 -1
      // 468: goto 43a
      // 46b: aload 0
      // 46c: iload 3
      // 46d: iload 4
      // 46f: invokevirtual net/rim/device/apps/internal/vad/VADApplication.setUIState (IZ)V
      // 472: return
      // 473: astore 3
      // 474: return
      // 475: astore 3
      // 476: new java/lang/StringBuffer
      // 479: dup
      // 47a: ldc_w "UI NPE on: "
      // 47d: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 480: aload 1
      // 481: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 484: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 487: invokestatic net/rim/device/apps/internal/vad/VADEventLog.log (Ljava/lang/String;)V
      // 48a: return
      // try (0 -> 591): 592 null
      // try (0 -> 591): 594 null
   }

   private final String[] extractChoices(byte[][] list, int numStart, int numEnd, boolean skip) {
      int numChoices = list.length - numStart - numEnd;
      String[] choices = new String[0];

      for (int i = 0; i < numChoices; i++) {
         byte[] data = list[numStart + i];
         Arrays.add(choices, new String(data, 0, data.length, "UnicodeLittleUnmarked"));
         if (skip) {
            i++;
         }
      }

      return choices;
   }
}
