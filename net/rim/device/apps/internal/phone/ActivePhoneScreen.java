package net.rim.device.apps.internal.phone;

import java.util.Vector;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.AudioRouter;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.KeyListener;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.api.ribbon.ConvenienceKeyOptionsProvider;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.api.utility.framework.ControllerUtilities;
import net.rim.device.apps.internal.phone.api.EnhanceCallAudioServices;
import net.rim.device.apps.internal.phone.api.Out;
import net.rim.device.apps.internal.phone.api.PTTKeyHandler;
import net.rim.device.apps.internal.phone.api.PhoneLogger;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.api.livecall.LiveCall;
import net.rim.device.apps.internal.phone.api.ui.PhoneAwareScreen;
import net.rim.device.apps.internal.phone.api.ui.gprs.GSM230Filter;
import net.rim.device.apps.internal.phone.api.verbs.DialVerb;
import net.rim.device.apps.internal.phone.api.verbs.OutgoingCallConnector;
import net.rim.device.apps.internal.phone.options.PhoneOptions;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.device.internal.system.Security;
import net.rim.device.internal.ui.UiSettings;

final class ActivePhoneScreen extends PhoneAwareScreen {
   private VoiceApp _voiceApp;
   private ActivePhoneUI _ui;
   private boolean _escKeyPressed;
   private boolean _pttKeyHeld;
   private boolean _ignoreKeyUp = true;
   private boolean _ignoreKeyRepeat = true;
   private int _oldKeyRepeatDelay = UiSettings.getKeypadRepeatDelay();
   private Verb _showHomeScreenVerb = new ActivePhoneScreen$ShowHomeScreenVerb(this);
   private Verb _newCallVerb = new ActivePhoneScreen$NewCallVerb(this);
   private Verb _showAddressBookVerb = (Verb)(new Object((Verb)(new Object(16777280)), null, 16777280));
   private boolean _pttKeyDownHappenedHere;
   static final int CALL_DISCONNECTION_DELAY = 1500;

   ActivePhoneScreen(VoiceApp app, LiveCall initialCall) {
      super(app, null, 0);
      this._voiceApp = app;
      int flags = 0;
      if (PhoneUtilities.isCharm240x260Screen()) {
         flags |= 2;
      }

      this._ui = new ColourScreenActivePhoneUI(this.getApp(), this, flags);
      this._ui.addFields(CallManager.getInstance().getCurrentCalls(), null, null);
      this.add(this._ui);
   }

   private final void lowerKeyRepeatDelay() {
      this._oldKeyRepeatDelay = UiSettings.getKeypadRepeatDelay();
      UiSettings.setKeypadRepeatDelay(200);
   }

   private final void restoreKeyRepeatDelay() {
      UiSettings.setKeypadRepeatDelay(this._oldKeyRepeatDelay);
   }

   @Override
   protected final void onVisibilityChange(boolean visible) {
      PTTKeyHandler hdlr = getPTTKeyHandler();
      if (hdlr != null) {
         if (visible) {
            this.lowerKeyRepeatDelay();
            return;
         }

         this.restoreKeyRepeatDelay();
      }
   }

   @Override
   protected final void onExposed() {
      this._ignoreKeyUp = true;
      this._ignoreKeyRepeat = true;
      super.onExposed();
   }

   final void onEndCallStatusDismissed() {
      this._ignoreKeyUp = true;
      this._ignoreKeyRepeat = true;
   }

   @Override
   protected final boolean navigationMovement(int dx, int dy, int status, int time) {
      return (status & 536870912) != 0;
   }

   @Override
   protected final boolean blockInputEvents(int event, int keycode) {
      PTTKeyHandler hdlr = getPTTKeyHandler();
      return hdlr != null && hdlr.isPTTKey(keycode) && hdlr.isPTTCallActive() ? false : super.blockInputEvents(event, keycode);
   }

   @Override
   public final int processKeyEvent(int event, char key, int keycode, int time) {
      switch (event) {
         case 513:
         case 520:
            this._ui.onKeypadStatusUpdated(Keypad.status(keycode));
         default:
            if (!Keypad.hasSendEndKeys()) {
               return super.processKeyEvent(event, key, keycode, time);
            } else {
               String promptString = "";
               if (this.isSystemLocked()) {
                  if (!this.isSystemLocked() || Keypad.key(keycode) != 27 || event != 513) {
                     return super.processKeyEvent(event, key, keycode, time);
                  }

                  promptString = PhoneResources.getString(6333);
               } else {
                  Object convenienceApplication = null;
                  String convenienceKeyOwner = null;
                  if (Keypad.key(keycode) == 19) {
                     convenienceApplication = ConvenienceKeyOptionsProvider.getInstance().getConvenienceKey1App(true);
                     convenienceKeyOwner = ConvenienceKeyOptionsProvider.getInstance().getConvenienceKey1Owner();
                  } else if (Keypad.key(keycode) == 21) {
                     convenienceApplication = ConvenienceKeyOptionsProvider.getInstance().getConvenienceKey2App(true);
                     convenienceKeyOwner = ConvenienceKeyOptionsProvider.getInstance().getConvenienceKey2Owner();
                  }

                  if (convenienceApplication == null) {
                     return super.processKeyEvent(event, key, keycode, time);
                  }

                  if (convenienceKeyOwner.startsWith("net_rim_quincy_speech_icon")
                     || convenienceKeyOwner.startsWith("app-switcher")
                     || convenienceKeyOwner.startsWith("net_rim_application_menu")
                     || convenienceKeyOwner.startsWith("net_rim_bb_profiles_app")) {
                     return 0;
                  }

                  if (convenienceKeyOwner.startsWith("net_rim_vad")) {
                     return 65536;
                  }

                  if (convenienceKeyOwner.indexOf("net_rim_bb_phone_ptt_app") != -1) {
                     return super.processKeyEvent(event, key, keycode, time);
                  }

                  promptString = MessageFormat.format(PhoneResources.getString(6295), new Object[]{convenienceApplication.toString()});
               }

               Dialog dlg = new ActivePhoneScreen$ExitFromActivePhoneScreenDlg(this, promptString, null);
               super._app.invokeLater(new ActivePhoneScreen$1(this, dlg), 7000, false);
               super._app.pushModalScreen(dlg);
               if (dlg.getSelectedValue() != 0) {
                  return 65536;
               } else if (Keypad.key(keycode) == 21) {
                  ConvenienceKeyOptionsProvider provider = ConvenienceKeyOptionsProvider.getInstance();
                  provider.invokeConvenienceKey2App();
                  return 65536;
               } else if (Keypad.key(keycode) == 27) {
                  Verb v = new ActivePhoneScreen$UnlockVerb();
                  v.invoke(null);
                  return 65536;
               } else {
                  return 0;
               }
            }
      }
   }

   @Override
   protected final boolean ignoreBacklightOffKeyEvent(int event, char key, int keycode, int time) {
      switch (Keypad.map(keycode)) {
         case '\u001b':
            return true;
         case ' ':
            if (!PhoneUtilities.isQwertyReducedKeyboard()) {
               return true;
            }
         default:
            return false;
      }
   }

   @Override
   protected final boolean handleSendKey() {
      int networkType = RadioInfo.getNetworkType();
      if (networkType != 3 && networkType != 7) {
         if (networkType == 4) {
            String dtmfTones = this._ui.getDTMFString();
            VoiceServices.flash(dtmfTones);
            this.clearDTMFBuffer();
            return true;
         }
      } else {
         String dtmfTones = this._ui.getDTMFString();
         if (dtmfTones != null && dtmfTones.length() > 0) {
            Out.p(((StringBuffer)(new Object("PHONE: SEND "))).append(dtmfTones).toString());
            if (this.invokeAudioSEND(dtmfTones)) {
               return true;
            }

            if (GSM230Filter.getCode(dtmfTones) != -1) {
               ((DialVerb)(new Object(dtmfTones, null))).invoke(null);
               return true;
            }
         }
      }

      int count = CallManager.getInstance().getNumCalls();
      switch (count) {
         case 1:
         default:
            if (!this.isSystemLocked() && OutgoingCallConnector.outgoingCallPermitted()) {
               this._newCallVerb.invoke(null);
            }

            return true;
         case 2:
            if (canSwap()) {
               VoiceServices.swapCalls();
               return true;
            }
         case 0:
            return false;
      }
   }

   private static final boolean canSwap() {
      CallManager callMgr = CallManager.getInstance();
      LiveCall activeCall = (LiveCall)callMgr.getCurrentCall();
      LiveCall heldCall = (LiveCall)callMgr.getInactiveCall();
      if (activeCall == null || !activeCall.isActive()) {
         return false;
      }

      if (heldCall != null && heldCall.isHeld()) {
         if (!(activeCall instanceof ConferenceCall)) {
            int callId = Phone.getInstance().getActiveCallId();
            if (callId != activeCall.getCallId()) {
               return false;
            }

            if (VoiceServices.getCallState(callId) != 3) {
               return false;
            }
         }

         if (!(heldCall instanceof ConferenceCall)) {
            int callId = Phone.getInstance().getHeldCallId();
            if (callId != heldCall.getCallId()) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   protected final boolean keyUp(int keycode, int time) {
      PTTKeyHandler hdlr = getPTTKeyHandler();
      boolean isPttKey = isPTTKey(keycode);
      if (isPttKey) {
         if (hdlr instanceof Object) {
            ((KeyListener)hdlr).keyUp(keycode, time);
         }

         if (!this._pttKeyHeld && hdlr != null) {
            hdlr.onPTTKeyReleased();
         }
      }

      if (isPttKey && this._pttKeyHeld && hdlr != null) {
         this._pttKeyHeld = false;
         this._pttKeyDownHappenedHere = false;
         hdlr.onPTTKeyReleased();
         return true;
      }

      if (this._ignoreKeyUp) {
         return true;
      }

      if (Keypad.key(keycode) == 36) {
         return true;
      }

      char c = Keypad.map(keycode);
      if (this._escKeyPressed && c == 27) {
         LiveCall call = getActiveLiveCall();
         if (call != null && !call.getFlag(65536)) {
            String msg = PhoneResources.getString(6056);
            int millis = 3000;
            new ActivePhoneScreen$EndCallStatusDialog(this, msg, millis, null);
            this._escKeyPressed = false;
         }

         return true;
      } else {
         return super.keyUp(keycode, time);
      }
   }

   static final PTTKeyHandler getPTTKeyHandler() {
      PTTKeyHandler handler = (PTTKeyHandler)ApplicationRegistry.getApplicationRegistry().get(-7975050928526187730L);
      return handler != null ? handler : null;
   }

   static final boolean isPTTKey(int keycode) {
      PTTKeyHandler hdlr = getPTTKeyHandler();
      return hdlr != null && hdlr.isPTTKey(keycode);
   }

   @Override
   protected final boolean keyRepeat(int keycode, int time) {
      if (isPTTKey(keycode) && !this._pttKeyHeld) {
         PTTKeyHandler hdlr = getPTTKeyHandler();
         if (hdlr != null) {
            this._pttKeyHeld = true;
            this._ignoreKeyRepeat = true;
            if (this._pttKeyDownHappenedHere) {
               LiveCall currCall = getActiveLiveCall();
               hdlr.onPTTKeyPressedAndHeld(currCall);
            }
         }

         return true;
      } else if (this._ignoreKeyRepeat) {
         return true;
      } else if (Keypad.key(keycode) == 36) {
         return true;
      } else {
         char key = Keypad.map(keycode);
         if (this._escKeyPressed && key == 27) {
            this._escKeyPressed = false;
            PhoneLogger.log("ESC=>endcall");
            this.endCurrentCall();
            this._ignoreKeyUp = true;
            this._ignoreKeyRepeat = true;
            return true;
         } else {
            return super.keyRepeat(keycode, time);
         }
      }
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key == 27 && !DeviceInfo.isInHolster()) {
         if (Keypad.hasSendEndKeys()) {
            if (!this.isSystemLocked()) {
               this._showHomeScreenVerb.invoke(null);
            }

            return true;
         }

         if (!this._escKeyPressed) {
            this._escKeyPressed = true;
            return true;
         }
      }

      if (key == ' ') {
         if (Character.isDigit(Keypad.getAltedChar(key))) {
            return true;
         }

         if (!this.isSystemLocked() && OutgoingCallConnector.outgoingCallPermitted()) {
            this._newCallVerb.invoke(null);
            return true;
         }
      }

      return super.keyChar(key, status, time);
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      this._ignoreKeyUp = false;
      this._ignoreKeyRepeat = false;
      PTTKeyHandler pttKeyHandler = getPTTKeyHandler();
      if (pttKeyHandler != null && pttKeyHandler.isPTTKey(keycode)) {
         this._pttKeyDownHappenedHere = true;
         return true;
      }

      if (Keypad.key(keycode) == 21) {
         LiveCall call = (LiveCall)CallManager.getInstance().getCurrentCall();
         if (ControllerUtilities.invokeApplicationKeyVerb(call)) {
            return true;
         }
      }

      LiveCall call = null;
      if (PhoneUtilities.isMuteKey(keycode)) {
         call = getActiveLiveCall();
         if (call != null) {
            call.mute();
         }

         return true;
      } else if (PhoneUtilities.isSpeakerPhoneKey(keycode)) {
         call = getActiveLiveCall();
         if (call != null) {
            call.toggleSpeakerphone();
            this.invalidate();
         }

         return true;
      } else {
         switch (Keypad.map(keycode)) {
            case '\n':
               String dtmfTones = this._ui.getDTMFString();
               switch (RadioInfo.getNetworkType()) {
                  case 3:
                  case 7:
                     Out.p(((StringBuffer)(new Object("PHONE: SEND "))).append(dtmfTones).toString());
                     if (dtmfTones != null && dtmfTones.length() > 0) {
                        this.invokeAudioSEND(dtmfTones);
                     }

                     return true;
                  case 4:
                     if (dtmfTones != null && dtmfTones.length() > 0) {
                        if (dtmfTones.endsWith("#4357*") || dtmfTones.endsWith("#HELP*")) {
                           label105:
                           try {
                              Object obj = Class.forName("net.rim.device.apps.internal.phone.api.ui.cdma.CDMAStatusScreen").newInstance();
                              if (obj != null) {
                                 UiApplication.getUiApplication().pushScreen((Screen)obj);
                                 VoiceServices.broadcastEvent(3006);
                                 return true;
                              }
                           } finally {
                              break label105;
                           }
                        }

                        VoiceServices.flash(dtmfTones);
                     } else {
                        VoiceServices.flash(null);
                     }

                     VoiceServices.broadcastEvent(3006);
                     return true;
                  default:
                     return false;
               }
            case 'L':
            case 'l':
               if (EnhanceCallAudioServices.getInstance().isECASupported() && AudioRouter.getInstance().getSink() == 0) {
                  call = getActiveLiveCall();
                  if (call != null) {
                     EnhanceCallAudioServices.getInstance().nextECASetting();
                     this._ui.ECAUpdated();
                  }
               }
            default:
               return super.keyDown(keycode, time);
         }
      }
   }

   static final LiveCall getActiveLiveCall() {
      LiveCall call = RIMPhone.getInstance().getIncomingCall();
      if (call == null) {
         CallManager callMgr = CallManager.getInstance();
         call = (LiveCall)callMgr.getCurrentCall();
      }

      return call;
   }

   private final void endCurrentCall() {
      LiveCall call = getActiveLiveCall();
      if (call != null && !call.getFlag(65536)) {
         call.endByUser();
      }
   }

   private final boolean invokeAudioSEND(String param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: invokestatic net/rim/device/api/system/Phone.getInstance ()Lnet/rim/device/api/system/Phone;
      // 03: aload 1
      // 04: invokevirtual net/rim/device/api/system/Phone.inCallDTMFDigitsEntered (Ljava/lang/String;)Z
      // 07: ifeq 38
      // 0a: aload 0
      // 0b: invokespecial net/rim/device/apps/internal/phone/ActivePhoneScreen.clearDTMFBuffer ()V
      // 0e: bipush 1
      // 0f: ireturn
      // 10: astore 2
      // 11: bipush 16
      // 13: invokestatic net/rim/device/internal/system/AudioInternal.startTone (I)Z
      // 16: istore 3
      // 17: aload 0
      // 18: invokevirtual net/rim/device/apps/internal/phone/api/ui/PhoneAwareScreen.getApp ()Lnet/rim/device/api/ui/UiApplication;
      // 1b: new net/rim/device/apps/internal/phone/ActivePhoneScreen$2
      // 1e: dup
      // 1f: aload 0
      // 20: invokespecial net/rim/device/apps/internal/phone/ActivePhoneScreen$2.<init> (Lnet/rim/device/apps/internal/phone/ActivePhoneScreen;)V
      // 23: bipush 100
      // 25: i2l
      // 26: bipush 0
      // 27: invokevirtual net/rim/device/api/system/Application.invokeLater (Ljava/lang/Runnable;JZ)I
      // 2a: pop
      // 2b: iload 3
      // 2c: ireturn
      // 2d: astore 2
      // 2e: aload 0
      // 2f: invokespecial net/rim/device/apps/internal/phone/ActivePhoneScreen.clearDTMFBuffer ()V
      // 32: ldc_w "PHONE: IllegalArg on SEND"
      // 35: invokestatic net/rim/device/apps/internal/phone/api/Out.p (Ljava/lang/String;)V
      // 38: bipush 0
      // 39: ireturn
      // try (0 -> 7): 8 null
      // try (0 -> 7): 25 null
   }

   final void updateCalls(Vector calls) {
      this._ui.updateCalls(calls);
   }

   final void setIncomingCall(LiveCall call) {
      this._ui.setIncomingCall(call);
   }

   final void callsEmpty() {
   }

   final void callsNonEmpty() {
   }

   final void callerIDUpdated(int callId, Object cidi) {
      if (this.isDisplayed()) {
         this._ui.callerIDUpdated(callId, cidi);
      }
   }

   @Override
   protected final void onEvent(int eventId, int param1, Object param2) {
      switch (eventId) {
         case 1001:
         case 1003:
         case 1004:
         case 3004:
         case 150040:
         case 150070:
            this.invalidate();
            return;
         case 1002:
         case 201010:
            this.clearDTMFBuffer();
            this.invalidate();
            return;
         case 1006:
            this.clearDTMFBuffer();
            return;
         case 2101:
            if (getPTTKeyHandler() != null) {
               this.restoreKeyRepeatDelay();
            }
         default:
            return;
         case 3006:
            this.clearDTMFBuffer();
            this.invalidate();
      }
   }

   final void onCallInitiated() {
      this.clearDTMFBuffer();
      this._ui.onCallInitiated();
   }

   final void preCallAnswer() {
      this._ui.onCallInitiated();
   }

   private final void clearDTMFBuffer() {
      synchronized (Application.getEventLock()) {
         this._ui.onDTMFBufferCleared();
      }
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      if (instance != 0) {
         if (instance == 65536) {
            ContextObject ctxt = ContextObject.clone(menu.getContext());
            ctxt.setFlag(90);
            ctxt.setFlag(20);
            PhoneUtilities.setPrivateFlag(ctxt, 43);
            PhoneUtilities.setPrivateFlag(ctxt, 37);
            PhoneUtilities.setPrivateFlag(ctxt, 88);
            PhoneUtilities.setPrivateFlag(ctxt, 89);
            menu.add(PhoneVerbManager.getInstance().getVerbs(ctxt));
            menu.setDefault((Verb)ctxt.get(-3185095355580406181L));
         }
      } else {
         super.makeMenu(menu, instance);
         Verb[] verbs = new Object[0];
         LiveCall call = (LiveCall)CallManager.getInstance().getCurrentCall();
         if (call != null) {
            Verb defaultVerb = call.getVerbs(null, verbs);
            if (verbs.length > 0) {
               menu.add(verbs);
               menu.setDefault(defaultVerb);
               return;
            }
         }

         if (this.isSystemLocked()) {
            menu.add(new ActivePhoneScreen$UnlockVerb());
            if (Security.getInstance().getAllowOutgoingCallWhileLocked()) {
               if (OutgoingCallConnector.outgoingCallPermitted()) {
                  menu.add(this._newCallVerb);
               }

               if (PhoneUtilities.cdmaTypeNetwork()) {
                  menu.add(this._newCallVerb);
                  return;
               }
            }
         } else {
            if (OutgoingCallConnector.outgoingCallPermitted()) {
               menu.add((Verb)(new Object(131585)));
               menu.add(this._newCallVerb);
            }

            menu.add(this._showAddressBookVerb);
            Verb showCalendarVerb = (Verb)ApplicationRegistry.getApplicationRegistry().get(8025740836317336000L);
            if (showCalendarVerb != null) {
               menu.add((Verb)(new Object(showCalendarVerb, null, 16777280)));
            }

            Verb showMessagesVerb = (Verb)ApplicationRegistry.getApplicationRegistry().get(-634672356903020959L);
            if (showMessagesVerb != null) {
               menu.add(showMessagesVerb);
            }

            menu.add(this._showHomeScreenVerb);
            PTTKeyHandler pttKeyHdlr = getPTTKeyHandler();
            if (pttKeyHdlr != null) {
               Verb[] pttVerbs = pttKeyHdlr.getActiveCallVerbs();
               if (pttVerbs != null) {
                  menu.add(pttVerbs);
               }
            }

            if (PhoneUtilities.cdmaTypeNetwork() && call != null) {
               menu.add(this._newCallVerb);
               return;
            }
         }
      }
   }

   @Override
   protected final ContextObject getMenuContextObject() {
      ContextObject context = (ContextObject)(new Object(20));
      if (!this.isSystemLocked()) {
         context.put(244, "phone");
      }

      LiveCall call = (LiveCall)CallManager.getInstance().getCurrentCall();
      if (call != null && !call.useStandardPhoneAppMenu()) {
         PhoneUtilities.setPrivateFlag(context, 51);
         return context;
      }

      PhoneUtilities.setPrivateFlag(context, 43);
      PhoneUtilities.setPrivateFlag(context, 69);
      String dtmfTones = this._ui.getDTMFString();
      if (dtmfTones != null && dtmfTones.length() > 0) {
         ContextObject.put(context, 7528018505720453076L, dtmfTones);
      }

      return context;
   }

   private final boolean isSystemLocked() {
      return ApplicationManager.getApplicationManager().isSystemLocked();
   }

   @Override
   protected final void onUiEngineAttached(boolean attached) {
      if (attached) {
         if (EnhanceCallAudioServices.getInstance().isECASupported()) {
            PhoneOptions opts = PhoneOptions.getOptions();
            int ecaOption = opts.getDefaultEnhanceCallAudio();
            if (ecaOption > 0) {
               EnhanceCallAudioServices.getInstance().setState(ecaOption - 1);
            } else {
               EnhanceCallAudioServices.getInstance().setState(opts.getPreviousEnhanceCallAudio());
            }

            this._ui.ECAUpdated();
         }
      } else {
         new ActivePhoneScreen$AudioSourceRemover().removeSource();
         if (getPTTKeyHandler() != null) {
            this.restoreKeyRepeatDelay();
         }

         this._ui.updateCalls(null);
      }

      super.onUiEngineAttached(attached);
   }

   public final void updateNumber() {
      if (this._ui != null) {
         this._ui.updateNumber();
      }
   }

   static final UiApplication access$300(ActivePhoneScreen x0) {
      return x0.getApp();
   }

   static final UiApplication access$400(ActivePhoneScreen x0) {
      return x0._app;
   }

   static final UiApplication access$500(ActivePhoneScreen x0) {
      return x0._app;
   }

   static final UiApplication access$600(ActivePhoneScreen x0) {
      return x0.getApp();
   }

   static final UiApplication access$800(ActivePhoneScreen x0) {
      return x0.getApp();
   }
}
