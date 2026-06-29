package net.rim.device.apps.internal.phone;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.ConditionalVerb;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.api.quickcontact.QuickContactList;
import net.rim.device.apps.api.ui.ExitVerb;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.internal.phone.api.PTTKeyHandler;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.api.ui.PhoneAwareScreen;
import net.rim.device.apps.internal.phone.api.ui.ThemedBannerCache;
import net.rim.device.apps.internal.phone.api.verbs.OutgoingCallConnector;
import net.rim.device.apps.internal.phone.api.verbs.PhoneOptionsVerb;
import net.rim.device.apps.internal.phone.api.verbs.VoiceMailVerb;
import net.rim.device.apps.internal.phone.options.PhoneOptions;
import net.rim.device.internal.system.Security;
import net.rim.tid.im.SLControlObject;

class PhoneAppScreen extends PhoneAwareScreen implements PhoneInputScreen {
   private VerticalFieldManager _titleFieldManager;
   private PhoneStatusField _myNumberField;
   protected PhoneNumberInput _phoneNumberInput;
   private PhoneNumberKeywordFinder _finderField;
   protected PhoneListManager _phoneListManager;
   private boolean _systemLocked;
   private boolean _contentProtected;
   private boolean _contentProtectedAddressBook;
   private boolean _isMultiLine = PhoneUtilities.getAvailableLineIds().length > 1;
   private boolean _isOutgoingCallAllowed = false;
   private boolean _newCallScreen = false;
   private long _onActivationTime = -1;
   private boolean _inputReceived = false;
   private VoiceMailVerb _voicemailVerb = (VoiceMailVerb)(new Object(131585));
   private boolean _ignoreKeyRepeat = false;
   private boolean _ignoreKeyUp = false;
   private int _savedMode = 0;
   private boolean _pttKeyHeld = false;
   private boolean _pttKeyDown = false;
   protected VoiceApp _voiceApp;
   private char _initialKey;
   private static ThemedBannerCache _idleBannerCache;
   private static final int DIALING_FIELD_SPACING;
   private static PhoneOptionsVerb _phoneOptionsVerb = (PhoneOptionsVerb)(new Object());
   private static PhoneAppScreen$PhoneInfoVerb _phoneInfoVerb = new PhoneAppScreen$PhoneInfoVerb();

   static void initializeOnceOnSystemStart() {
      getCachedIdleBanner();
      PhoneNumberInput.getReturnKeyImage(0);
   }

   protected PhoneAppScreen(VoiceApp voiceApp, boolean newCallScreen) {
      this(voiceApp, voiceApp, newCallScreen, null);
   }

   PhoneAppScreen(UiApplication uiApplication, VoiceApp voiceApp, boolean newCallScreen, Object context) {
      super(uiApplication, context, 2147483648L);
      this._voiceApp = voiceApp;
      this._newCallScreen = newCallScreen;
      this.setId("phone-main");
      this.setCloseVerb(ExitVerb.createCloseVerb(2, null));
      this._titleFieldManager = (VerticalFieldManager)(new Object(1152921504606846976L));
      this._myNumberField = new PhoneStatusField();
      this._finderField = new PhoneNumberKeywordFinder(this);
      this._phoneNumberInput = new PhoneNumberInput(this, uiApplication, this._finderField);
      this._phoneListManager = new PhoneListManager(this, this._finderField, this._phoneNumberInput, uiApplication);
      this.populateScreen(context);
      this._phoneNumberInput.setFocus();
      this.setSupportClickAndHoldKeyEvents(true);
   }

   @Override
   protected void startListening() {
      super._app.addGlobalEventListener(this._phoneListManager);
      super._app.addGlobalEventListener(this._myNumberField);
      QuickContactList.getInstance().addListener(this._phoneListManager);
      super.startListening();
   }

   @Override
   public void stopListening() {
      QuickContactList.getInstance().removeListener(this._phoneListManager);
      super._app.removeGlobalEventListener(this._phoneListManager);
      super._app.removeGlobalEventListener(this._myNumberField);
      super.stopListening();
   }

   private static Field getCachedIdleBanner() {
      if (_idleBannerCache == null) {
         _idleBannerCache = new PhoneAppScreen$1();
      }

      return _idleBannerCache.getBanner();
   }

   protected void populateScreen(Object context) {
      this.setTitle(this._titleFieldManager);
      this._titleFieldManager.setTag(null);
      this._titleFieldManager.getManager().setTag(null);
      Field titleField = this.getTitleField();
      if (titleField != null) {
         this._titleFieldManager.add(titleField);
      }

      this.setMyNumberField(true);
      this.add((Field)(new Object(4)));
      this.add(this._phoneNumberInput);
      this.add((Field)(new Object(4)));
      this.add((Field)(new Object()));
      this.add(this._phoneListManager);
   }

   private void setMyNumberField(boolean add) {
      if (add) {
         if (!this._isMultiLine && PhoneOptions.getOptions().getBooleanOption(65536)) {
            return;
         }

         if (this._myNumberField.getManager() == null) {
            this.insert((Field)(new Object()), this._titleFieldManager.getIndex());
            this.insert(this._myNumberField, this._titleFieldManager.getIndex());
            return;
         }
      } else if (this._myNumberField.getManager() != null) {
         int index = this._myNumberField.getIndex();
         this.deleteRange(index, 2);
      }
   }

   private void setFindField(boolean add) {
      if (add) {
         if (this._finderField.getManager() == null) {
            this.insert(this._finderField, this._phoneListManager.getIndex());
            this.insert((Field)(new Object()), this._phoneListManager.getIndex());
            return;
         }
      } else if (this._finderField.getManager() != null) {
         this.deleteRange(this._finderField.getIndex(), 2);
      }
   }

   private void setInputMode(int mode, boolean save) {
      if (this.getInputContext().getActiveInputMethodID() == 4096) {
         SLControlObject co = (SLControlObject)this.getInputContext().getInputMethodControlObject();
         int currentMode = co.getInputMode();
         if (currentMode != 1 && save) {
            this._savedMode = currentMode;
         }

         if (currentMode != mode) {
            SLControlObject cObj = (SLControlObject)this._phoneNumberInput.getInputContext().getInputMethodControlObject();
            cObj.actionPerformed(106, new Object(mode));
         }
      }
   }

   private void setupVariables() {
      this._systemLocked = ApplicationManager.getApplicationManager().isSystemLocked();
      this._contentProtected = this._systemLocked && PersistentContent.isEncryptionEnabled();
      this._contentProtectedAddressBook = !Security.getInstance().isAddressBookExcludedFromContentProtection();
      this._isOutgoingCallAllowed = OutgoingCallConnector.outgoingCallPermitted() && this._voiceApp.isPhoneEnabled();
      this._ignoreKeyRepeat = false;
      this._inputReceived = false;
      this.setMyNumberField(this._isMultiLine || !this._newCallScreen && !PhoneOptions.getOptions().getBooleanOption(65536));
   }

   @Override
   public void onAppActivated(Object context, char key) {
      this._initialKey = key;
      if (key == 0) {
         if (ContextObject.getFlag(context, 119) && PhoneUtilities.getPrivateFlag(context, 79) && this._isOutgoingCallAllowed) {
            PhoneUtilities.callNumberFromAddressBook(null, null, true);
         }
      }
   }

   @Override
   protected void onVisibilityChange(boolean visible) {
      if (visible) {
         this.setInputMode(0, true);
         this.setupVariables();
         this._onActivationTime = System.currentTimeMillis();
         if (this._initialKey != 0 && (PhoneUtilities.isQwertyReducedKeyboard() || this._initialKey != ' ')) {
            this._phoneNumberInput.setFocus();
            this._phoneNumberInput.echoInitialKey(this._initialKey);
            this._initialKey = 0;
         }
      } else {
         if (!this._systemLocked) {
            this.setInputMode(this._savedMode, false);
         }

         this._onActivationTime = -1;
      }

      super.onVisibilityChange(visible);
   }

   protected void cancel() {
      super._closeVerb.invoke(null);
   }

   boolean isForeground() {
      return super._app.getActiveScreen() == this && VoiceServices.getVoiceApplication().inForeground();
   }

   protected Field getTitleField() {
      return getCachedIdleBanner();
   }

   @Override
   protected ContextObject getMenuContextObject() {
      ContextObject context = (ContextObject)(new Object(20));
      if (!this._systemLocked) {
         context.put(244, "phone");
      }

      PhoneUtilities.setPrivateFlag(context, 43);
      PhoneUtilities.setPrivateFlag(context, 69);
      return context;
   }

   @Override
   protected void makeMenu(SystemEnabledMenu menu, int instance) {
      super.makeMenu(menu, instance);
      Field field = this.getLeafFieldWithFocus();
      if (instance == 0) {
         if (this._isOutgoingCallAllowed) {
            menu.add(this._voicemailVerb);
         }

         if (this._systemLocked) {
            menu.add((Verb)(new Object()));
         } else if (!(field instanceof PhoneStatusField)) {
            if (this._isOutgoingCallAllowed) {
               Verb callFromAddressBookVerb = (Verb)(new Object(6238, 131072));
               menu.add(callFromAddressBookVerb);
               menu.setDefault(callFromAddressBookVerb);
            }

            if (!this._newCallScreen) {
               menu.add(_phoneOptionsVerb);
               menu.add(_phoneInfoVerb);
            }

            if (PhoneUtilities.canExit911CallbackMode() && (RadioInfo.getNetworkService() & 256) != 0) {
               menu.add(new PhoneAppScreen$Exit911CallbackModeVerb());
            }
         }
      }

      this.loadRepositoryMenuItems(menu);
      if (field == this._phoneNumberInput) {
         this._phoneNumberInput.addToMenu(menu, instance, this._systemLocked, this._isOutgoingCallAllowed);
      } else if (field == this._myNumberField) {
         this._myNumberField.addToMenu(menu, instance, this._systemLocked);
      } else {
         this._phoneListManager.addToMenu(menu, instance, this._systemLocked, this._isOutgoingCallAllowed);
      }
   }

   private boolean processPTTKeyEvent(int event, PTTKeyHandler pttKeyHandler) {
      switch (event) {
         case 513:
         default:
            if (pttKeyHandler.isPTTKeyContext(this._phoneListManager.getSelectedItem())) {
               this._pttKeyDown = true;
               this._pttKeyHeld = false;
               return true;
            }
         case 515:
            if (this._pttKeyDown) {
               this._pttKeyHeld = false;
               this._pttKeyDown = false;
               pttKeyHandler.onPTTKeyReleased();
               return true;
            }
         case 514:
            Object selectedItem = this._phoneListManager.getSelectedItem();
            if (!this._pttKeyHeld && pttKeyHandler.isPTTKeyContext(selectedItem)) {
               this._pttKeyHeld = true;
               PTTKeyHandler finalPTTKeyHdler = pttKeyHandler;
               super._app.invokeLater(new PhoneAppScreen$2(this, finalPTTKeyHdler, selectedItem));
               return true;
            }
         case 512:
            return false;
      }
   }

   void updateFields() {
      this._phoneNumberInput.updateField();
      this._phoneListManager.updateField();
   }

   private boolean doRepost(int keycode) {
      if (this.getFieldWithFocus() != this._phoneListManager) {
         return false;
      }

      switch (Keypad.key(keycode)) {
         case 8:
         case 127:
            if (this._phoneNumberInput.getTextLength() == 0) {
               return false;
            }
         case 18:
         case 27:
            return true;
         default:
            char key = Keypad.map(keycode);
            if (PhoneUtilities.isQwertyReducedKeyboard()) {
               char altKey = Keypad.getAltedChar(key);
               if (altKey == '*' || altKey == '#') {
                  return true;
               }
            }

            return CharacterUtilities.isDigit(key) || CharacterUtilities.isLetter(key) || CharacterUtilities.isSpaceChar(key);
      }
   }

   @Override
   public int processKeyEvent(int event, char key, int keycode, int time) {
      PTTKeyHandler pttKeyHandler = ActivePhoneScreen.getPTTKeyHandler();
      if (pttKeyHandler != null && pttKeyHandler.isPTTKey(keycode) && this.processPTTKeyEvent(event, pttKeyHandler)) {
         return 65536;
      }

      if (!this.blockInputEvents(event, keycode) && !PhoneUtilities.isMuteKey(keycode) && Keypad.key(keycode) != 19 && Keypad.key(keycode) != 21) {
         if (keycode == 0) {
            int testCode;
            if (Keypad.getUnaltedChar(key) != 0) {
               testCode = Keypad.keycode(Keypad.getUnaltedChar(key), 0);
            } else {
               testCode = Keypad.keycode(key, 0);
            }

            if (testCode == 0) {
               if (key == '0') {
                  keycode = key << 16;
               }
            } else {
               keycode = testCode;
            }
         }

         boolean repost = this.doRepost(keycode);
         if (repost && this._phoneNumberInput.getTextLength() == 0 && event == 515) {
            this._phoneNumberInput.setFocus();
            repost = false;
         }

         int result = super.processKeyEvent(event, key, keycode, time);
         return repost && this._phoneNumberInput.repostProcessKeyEvent(event, key, keycode, time) ? result | 65536 : result;
      } else {
         return 0;
      }
   }

   @Override
   protected boolean handleEndKey() {
      if (this._phoneNumberInput.getTextLength() == 0) {
         this.cancel();
         return true;
      } else {
         this._phoneNumberInput.clear(true);
         return true;
      }
   }

   @Override
   protected boolean keyDown(int keycode, int time) {
      this._ignoreKeyRepeat = false;
      if (super.keyDown(keycode, time)) {
         return true;
      } else if ((Keypad.key(keycode) == 18 || Keypad.key(keycode) == 27) && this._phoneNumberInput.getTextLength() == 0) {
         this.cancel();
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected boolean keyUp(int keycode, int time) {
      this._ignoreKeyRepeat = false;
      if (this._ignoreKeyUp) {
         this._ignoreKeyUp = false;
         return true;
      } else if ((Keypad.key(keycode) == 10 || Keypad.key(keycode) == 17) && this.getElapsedTimeSinceDisplay() < 500) {
         return true;
      } else if (super.keyUp(keycode, time)) {
         return true;
      } else if (Keypad.map(keycode) == '?' && this.getFieldWithFocus() == this._phoneListManager) {
         _phoneInfoVerb.invoke(null);
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected boolean keyRepeat(int keycode, int time) {
      if (this._ignoreKeyRepeat) {
         return true;
      }

      this._ignoreKeyRepeat = true;
      if (this._systemLocked) {
         this._ignoreKeyUp = true;
         return true;
      }

      if (super.keyRepeat(keycode, time)) {
         return true;
      }

      if (Keypad.key(keycode) == 17) {
         if (this._isOutgoingCallAllowed) {
            PhoneUtilities.callNumberFromAddressBook(null, null, true);
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   protected boolean keyClickedAndHeld(int keycode, int time) {
      if (!this._contentProtected && this._isOutgoingCallAllowed) {
         if (this.getFieldWithFocus() == this._phoneListManager) {
            if (this._phoneListManager.keyClickedAndHeld(keycode)) {
               return true;
            }
         } else if (this.getFieldWithFocus() == this._phoneNumberInput && this._phoneNumberInput.keyClickedAndHeld(keycode)) {
            return true;
         }

         return super.keyClickedAndHeld(keycode, time);
      } else {
         return true;
      }
   }

   @Override
   protected void onEvent(int eventId, int callId, Object context) {
      switch (eventId) {
         case 2101:
            this._phoneNumberInput.clear(false);
            return;
         case 5000:
            this._phoneNumberInput.clear(false);
            break;
         case 100200:
         case 100203:
         case 150090:
            if (this._myNumberField != null) {
               this._myNumberField.setDevicePhoneNumber(PhoneUtilities.getCurrentLineId());
            }

            this._isMultiLine = PhoneUtilities.getAvailableLineIds().length > 1;
            return;
         case 100201:
            if (this._myNumberField != null) {
               this._myNumberField.setDevicePhoneNumber(-1);
               return;
            }
            break;
         case 100300:
            UiApplication uiApp = this.getApp();
            if (uiApp.isForeground() && uiApp.getActiveScreen() == this) {
               uiApp.requestBackground();
               return;
            }
            break;
         case 150130:
            this._myNumberField.setDevicePhoneNumber(PhoneUtilities.getCurrentLineId());
            return;
         case 190000:
            if (this._phoneNumberInput != null) {
               this.refreshOnCorrectThread(new PhoneAppScreen$3(this));
               return;
            }
            break;
         case 123456789:
            this.keyChar('\u001b', 0, 0);
            return;
      }
   }

   private long getElapsedTimeSinceDisplay() {
      return this._onActivationTime == 0 ? -1 : System.currentTimeMillis() - this._onActivationTime;
   }

   @Override
   protected boolean openProductionBackdoor(int backdoorCode) {
      switch (backdoorCode) {
         case 1263291725:
            return super.openProductionBackdoor(backdoorCode);
         case 1263291726:
         default:
            this.getApp().pushModalScreen(new PhoneBackdoorHelpScreen(this));
            return true;
      }
   }

   @Override
   public void onInputFieldCleared() {
      int view = 1;
      if (this._contentProtected) {
         if (this._contentProtectedAddressBook) {
            view = 0;
         } else {
            view = 4;
         }
      }

      this._phoneListManager.updateView(view, null);
      this.setFindField(false);
      if (!this._contentProtected && !this._inputReceived) {
         this._phoneListManager.setFocus();
      } else {
         this._phoneNumberInput.setFocus();
      }
   }

   @Override
   public void onInputFieldNonEmpty(int mode) {
      this._inputReceived = true;
      if (this._contentProtected && this._contentProtectedAddressBook) {
         this._phoneListManager.updateView(0, null);
      } else {
         if (mode == 1) {
            this._phoneListManager.updateView(3, null);
            if (!(this.getFieldWithFocus() instanceof PhoneNumberInput)) {
               this._phoneNumberInput.setFocus();
            }
         } else {
            this._phoneListManager.updateView(2, null);
            if (mode == 2) {
               if (!Locale.getDefaultInputForSystem().getLanguage().equals("ja")) {
                  this._phoneListManager.setFocus();
               }
            } else {
               this._phoneNumberInput.setFocus();
            }
         }

         this.setFindField(mode == 0 && this._phoneListManager.hasEntries());
      }
   }

   @Override
   public void onRollOffPhoneNumberInput() {
      this._phoneListManager.onRollOffPhoneNumberInput();
   }

   private void loadRepositoryMenuItems(SystemEnabledMenu menu) {
      if (!this._systemLocked) {
         VerbRepository vr = VerbRepository.getVerbRepository(7558275355255656232L);
         Verb[] verbs = vr.getVerbs(null);
         if (verbs != null) {
            for (Verb v : verbs) {
               if (v instanceof Object) {
                  ConditionalVerb cv = (ConditionalVerb)v;
                  if (!cv.canInvoke(null)) {
                     continue;
                  }
               }

               menu.add(v);
            }
         }
      }
   }
}
