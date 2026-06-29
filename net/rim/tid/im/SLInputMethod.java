package net.rim.tid.im;

import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.text.TextFilter;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.ui.StringBufferGap;
import net.rim.tid.awt.Event;
import net.rim.tid.awt.event.KeyEvent;
import net.rim.tid.awt.event.NavigationEvent;
import net.rim.tid.awt.im.repository.CustomDictionary;
import net.rim.tid.awt.im.repository.CustomWordsRepository;
import net.rim.tid.awt.im.spi.InputMethod;
import net.rim.tid.awt.im.spi.InputMethodContext;
import net.rim.tid.awt.im.spi.InputModeChangeListener;
import net.rim.tid.im.conv.ConversionEvent;
import net.rim.tid.im.conv.IConversion;
import net.rim.tid.im.conv.SLComposedText;
import net.rim.tid.im.conv.SLVariants;
import net.rim.tid.im.layout.SLKeyLayout;
import net.rim.tid.im.ui.Lookup;
import net.rim.tid.im.ui.LookupIf;
import net.rim.tid.im.ui.Toolbar;
import net.rim.tid.im.util.InputMethodHelper;
import net.rim.tid.itie.IComponent;
import net.rim.tid.itie.LinguisticData;
import net.rim.tid.text.AttributedString;
import net.rim.tid.text.TextHitInfo;
import net.rim.vm.WeakReference;

public class SLInputMethod implements InputMethod, InputMethodConstants {
   protected byte _predictionMode = 127;
   protected boolean iKeyRepeateProcessed;
   protected int _rollerCharacterIndex = -1;
   protected boolean _isLookupEnabled = true;
   protected int[] modifiersKey = new int[]{137, 1064304896, -1117257139, 1064304896};
   protected Locale[] availableLocals = new Object[]{Locale.getDefault()};
   protected SLComposedText composedText;
   protected Locale locale = Locale.getDefault();
   protected InputMethodContext context;
   protected Hashtable layouts = (Hashtable)(new Object());
   protected IConversion convEngine;
   protected Toolbar toolbar;
   protected SLKeyLayout lnkLayout;
   protected Locale[] _directLocals = new Object[]{Locale.get("en")};
   protected MinimalInputMethod _directInput = (MinimalInputMethod)(new Object(this._directLocals));
   protected boolean compositionEnabled = true;
   protected int learningWordlistSize;
   protected SLControlObject controlObject;
   boolean isDebug;
   private boolean _isDirectInput;
   protected boolean learning;
   protected boolean isAutoCorrectionEnabled;
   protected int lastKeyPressed = -1;
   protected Vector wordlistsDataCache = (Vector)(new Object());
   protected boolean isCaps;
   TextFilter _textFilter;
   private boolean keepSPUpdated;
   protected ConversionEvent cEvent = new ConversionEvent(null, null, 0, null, false);
   protected StringBuffer iKeyCharsCache = (StringBuffer)(new Object());
   protected XYRect _lookupBounds = (XYRect)(new Object());
   protected XYRect _lookupBounds2 = (XYRect)(new Object());
   protected int _keyboardID = Keypad.getHardwareLayout();
   protected ResourceBundleFamily _bundleFamily;
   protected SLInputMethod$IMOptions _optionsResource;
   byte[] _IMProp;
   protected boolean _keyUpProcessOnly;
   private int[] _keyDownKeycodes = new int[2];
   private int[] _keyDownModifiers = new int[2];
   private long _keyDownTime;
   private boolean[] _isKeyDownEventsWasConsumed = new boolean[2];
   private boolean _isComplexInputAllowed = true;
   private Object _imCookieCache;
   protected String _lookupLeftLabel;
   protected String _lookupRightLabel;
   protected boolean _debug = false;
   public static final byte FAST_REGULAR_MODE;
   public static final byte FAST_PREDICTIVE_MODE;
   public static final byte FAST_AND_PREDICTIVE_MODE;
   public static final byte EDIT_MODE;
   public static final byte UNDEFINED_MODE;
   protected static WeakReference lookup = (WeakReference)(new Object(null));
   public static long _tlTime;
   public static long _startTimer;
   private static final int ACCEPTIBLE_EVENTS_MASK = Event.FOCUS_EVENT_MASK | Event.KEY_EVENT_MASK | Event.NAVIGATION_EVENT_MASK;

   protected void verifyLookupReset() {
      this.verifyLookupReset(lookup);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   protected void verifyLookupReset(WeakReference lookupIfWr) {
      LookupIf lookupIf = (LookupIf)lookupIfWr.get();
      if (lookupIf != null) {
         label25:
         try {
            if (lookupIf.isDisplayed()) {
               lookupIf.setVisible(false);
            }
         } catch (Throwable var5) {
            e.printStackTrace();
            break label25;
         }

         lookupIfWr.set(null);
      }
   }

   protected void createComposedText(int aNumberOfVariants, int aVariantArraysSize, int type) {
      this.composedText = new SLComposedText(aNumberOfVariants, aVariantArraysSize, type);
   }

   protected void createComposedText(int aNumberOfVariants, int aVariantArraysSize) {
      this.createComposedText(aNumberOfVariants, aVariantArraysSize, 1);
   }

   protected void setOptionsBundle(ResourceBundleFamily bundleFamily, SLInputMethod$IMOptions optionsResource) {
      this._bundleFamily = bundleFamily;
      this._optionsResource = optionsResource;
   }

   public void persistentContentModeChanged(int generation) {
      this.protectContent(2);
   }

   public void persistentContentStateChanged(int state) {
      switch (state) {
         case 1:
            this.protectContent(1);
         case 0:
            return;
         case 2:
         default:
            this.protectContent(0);
      }
   }

   protected void focusLost(IComponent comp) {
   }

   protected void onStatusEvent(KeyEvent anEvent) {
   }

   protected int getModifier(int aModifier, KeyEvent anEvent) {
      return aModifier;
   }

   protected boolean onStartKeyDownEvent(KeyEvent anEvent) {
      return false;
   }

   protected void protectContent(int flag) {
      if (this.convEngine != null) {
         this.convEngine.protectContent(flag);
      }
   }

   protected void dispatchNavigationEvent(NavigationEvent event) {
      if (this.isDirectInput()) {
         this._directInput.dispatchEvent(event);
      } else {
         this.dispatchNavigationEvent(event, this.convEngine);
      }
   }

   protected void dispatchNavigationEvent(NavigationEvent event, IConversion conversionEngine) {
      if (conversionEngine != null) {
         conversionEngine.dispatchNavigationEvent(event);
      }

      if (event.getEventConsumptionId() != 0) {
         this.sendComposedText(this.composedText, 0);
      } else {
         this.setLookupVisible(this.composedText.isLookupVisible());
      }
   }

   protected void dispatchKeyEvent(KeyEvent event) {
      if (!this.isIgnorableFunctionalKeyEvent(event)) {
         if (this.isDirectInput() && event.isInputEvent()) {
            this._directInput.dispatchEvent(event);
         } else if (this.lnkLayout != null) {
            int eventID = event.getID();
            if (event.isInputEvent()) {
               switch (eventID) {
                  case 513:
                  case 517:
                  case 518:
                     break;
                  case 514:
                  default:
                     this.processKeyRepeate(event);
                     return;
                  case 515:
                     if (!this.isKeyUpProcessOnly(event)) {
                        this.processKeyUp(event);
                        if (!event.isConsumed() && this.isKeyUpEventShouldBeConsumed(event)) {
                           event.consume();
                        }

                        return;
                     }
                     break;
                  case 516:
                     this.processThumbClick(event);
                     return;
                  case 519:
                     this.processRollEvent(event);
                     return;
               }
            }

            this._keyDownKeycodes[0] = this._keyDownKeycodes[1];
            this._keyDownKeycodes[1] = event.getKeyCode();
            this._keyDownModifiers[0] = this._keyDownModifiers[1];
            int modif = this._keyDownModifiers[1] = event.getModifiers();
            this._keyDownTime = event.getWhen();
            this._isKeyDownEventsWasConsumed[0] = this._isKeyDownEventsWasConsumed[1];
            this._isKeyDownEventsWasConsumed[1] = false;
            if (eventID == 520) {
               this.onStatusEvent(event);
            } else {
               if (eventID == 513 && !this.isKeyUpProcessOnly(event) || eventID == 515 && this.isKeyUpProcessOnly(event)) {
                  this.iKeyRepeateProcessed = false;
                  this.lastKeyPressed = this._keyDownKeycodes[1];
                  if (this.isModifiersKey(this.lastKeyPressed)) {
                     return;
                  }

                  StringBuffer keyChars;
                  if (modif != 32768) {
                     modif = this.getModifier(modif, event);
                     keyChars = this.lnkLayout.getKeyChars(this._keyDownKeycodes[1], modif, this.isCaps);
                     event.setKeyChar(keyChars.charAt(0));
                  } else {
                     this.iKeyCharsCache.setLength(0);
                     this.iKeyCharsCache.append(event.getKeyChar());
                     keyChars = this.iKeyCharsCache;
                  }

                  if (!event.isInputEvent()) {
                     return;
                  }

                  if (this.onStartKeyDownEvent(event)) {
                     return;
                  }

                  char newKeyCode = keyChars.charAt(0);
                  if (this._textFilter != null) {
                     char tmpNewKeyCode = newKeyCode;
                     newKeyCode = this._textFilter.convert(newKeyCode, SLKeyLayout.convertModifiersToStatus(modif));
                     if (newKeyCode == 0 && this.convEngine.isControl(tmpNewKeyCode)) {
                        newKeyCode = tmpNewKeyCode;
                     }
                  }

                  if (this.isModifiersKey(newKeyCode)) {
                     return;
                  }

                  this.cEvent.init(event, keyChars, newKeyCode, event.getSource(), this.lnkLayout.isReduced());
                  this.dispatchConversionEvent(this.cEvent);
               }

               if (this.isKeyUpProcessOnly(event) && eventID == 513) {
                  StringBuffer keyChars = this.lnkLayout.getKeyChars(this._keyDownKeycodes[1], modif, this.isCaps);
                  char ch = keyChars.charAt(0);
                  event.setKeyChar(ch);
                  if (ch != 27 && ch != '\n' && ch != ' ' && ch != '\b' && ch != 127 || !this.composedText.isEmpty()) {
                     event.consume();
                  }
               }

               this._isKeyDownEventsWasConsumed[1] = event.isConsumed();
            }
         }
      }
   }

   protected boolean isIgnorableFunctionalKeyEvent(KeyEvent event) {
      return InputMethodHelper.isIgnorableFunctionalKeyEvent(event);
   }

   public Object getRecentIMCookie() {
      Object result = this._imCookieCache;
      this._imCookieCache = null;
      return result;
   }

   protected void sendRawEvent(KeyEvent aEvent) {
      this.iKeyCharsCache.setLength(0);
      this.cEvent.init(aEvent, this.iKeyCharsCache, 0, aEvent.getSource(), this.lnkLayout.isReduced());
      this.dispatchConversionEvent(this.cEvent);
   }

   protected void processThumbClick(KeyEvent evt) {
      if (!this.composedText.isEmpty()) {
         this.iKeyCharsCache.setLength(0);
         this.iKeyCharsCache.append('\n');
         this.cEvent.init(evt, this.iKeyCharsCache, 10, evt.getSource(), this.lnkLayout.isReduced());
         this.dispatchConversionEvent(this.cEvent);
      }
   }

   protected void processKeyUp(KeyEvent evt) {
      this.sendRawEvent(evt);
   }

   protected void processKeyRepeate(KeyEvent evt) {
      int code = evt.getKeyCode();
      StringBuffer keyChars = this.lnkLayout.getKeyChars(code, evt.getModifiers(), this.isCaps);
      evt.setKeyChar(keyChars.charAt(0));
      if (!this.isModifiersKey(evt.getKeyChar())) {
         this.sendRawEvent(evt);
         if (!evt.isConsumed() && this.convEngine.isControl(evt.getKeyChar())) {
            keyChars.setLength(0);
            keyChars.append(evt.getKeyChar());
            this.cEvent.init(evt, keyChars, evt.getKeyChar(), evt.getSource(), this.lnkLayout.isReduced());
            this.dispatchConversionEvent(this.cEvent);
         } else {
            if (!this.composedText.isEmpty()) {
               evt.consume();
            }
         }
      }
   }

   protected void processRollEvent(KeyEvent evt) {
      this.sendRawEvent(evt);
      if (!evt.isConsumed()) {
         int status = evt.getKeyChar() & 3;
         int amount = evt.getKeyCode();
         int keycode = amount > 0 ? 130 : 129;
         if (amount < 0) {
            amount *= -1;
         }

         this.iKeyCharsCache.setLength(0);
         this.iKeyCharsCache.append("");
         this.cEvent.init(evt, this.iKeyCharsCache, keycode, evt.getSource(), this.lnkLayout.isReduced());
         switch (status) {
            case 0:
               break;
            case 1:
            default:
               this.cEvent.setModifiers(8);
               break;
            case 2:
               this.cEvent.setModifiers(1);
         }

         for (int i = 0; i < amount; i++) {
            this.dispatchConversionEvent(this.cEvent);
         }
      }
   }

   protected void dispatchConversionEvent(ConversionEvent event) {
      if (this.convEngine != null) {
         this.convEngine.dispatchConversionEvent(event);
      }

      if (event.getID() != 0) {
         this.sendComposedText(this.composedText, event.getModifiers());
      } else {
         this.setLookupVisible(this.composedText.isLookupVisible());
      }
   }

   protected boolean isModifiersKey(int key) {
      return Arrays.getIndex(this.modifiersKey, key) != -1;
   }

   public void join(SLInputMethod aJoinedInputMethod) {
   }

   public Object getAdditionalSymbolData(int type) {
      return null;
   }

   public int getInputMode() {
      return -1;
   }

   public boolean setFilter(TextFilter filter) {
      this._textFilter = filter;
      boolean res = this._directInput.setFilter(filter);
      if (this.isDirectInput()) {
         return res;
      } else {
         return this.convEngine != null ? this.convEngine.setFilter(filter) : false;
      }
   }

   public boolean isComplexInputAllowed() {
      return this._isComplexInputAllowed;
   }

   public char getPeriodSymbol() {
      return '.';
   }

   public boolean isDirectInput() {
      return this._isDirectInput || !this._isComplexInputAllowed;
   }

   public boolean isLookupExist() {
      return lookup.get() != null;
   }

   public int setActionPerformedToLookup(Object src, int action, Object parameter) {
      WeakReference ref = this.getLookupRef();
      Object l = ref.get();
      if (l == null) {
         l = this.getViewComponent(3);
      }

      return ((LookupIf)l).actionPerformed(src, action, parameter);
   }

   public void setVariantsToLookup(SLVariants aVariant) {
      WeakReference ref = this.getLookupRef();
      Object l = ref.get();
      if (l == null) {
         l = this.getViewComponent(3);
      }

      ((LookupIf)l).setVariants(aVariant);
   }

   public void setVariantsToLookup() {
      SLVariants variant = this._predictionMode == 3 ? this.composedText.getEditModeVariant() : this.composedText.getCurrentVariant(false);
      this.setVariantsToLookup(variant);
   }

   public int addShortcut(String replacedString, String replacementStringPattern) {
      return this.convEngine != null ? this.convEngine.addShortcut(replacedString, replacementStringPattern) : 4;
   }

   public Locale[] getAvailableLocals() {
      return this.availableLocals;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   protected void loadConversionEngine() {
      if (this.convEngine == null) {
         try {
            String conversionEngine = this.getConvEngineName();
            if (conversionEngine != null) {
               this.convEngine = (IConversion)Class.forName(conversionEngine).newInstance();
               this.convEngine.setInputMethod(this);
               return;
            }
         } catch (Throwable var3) {
            e.printStackTrace();
            return;
         }
      }
   }

   protected String getConvEngineName() {
      throw null;
   }

   public SLKeyLayout getKeyLayout() {
      return this.isDirectInput() ? this._directInput.getKeyLayout() : this.lnkLayout;
   }

   public int getLastKeyPressed() {
      return this.lastKeyPressed;
   }

   public InputMethodContext getInputMethodContext() {
      return this.context;
   }

   public boolean isPredictive() {
      return this._predictionMode == 1;
   }

   public void setPredictionMode(byte mode) {
   }

   protected boolean isReducedkeyboardSupported(ResourceBundle prop) {
      String[] types = prop.getStringArray(this._optionsResource.KEYBOARD_TYPE);

      for (int i = types.length - 1; i >= 0; i--) {
         if (StringUtilities.toUpperCase(types[i], 1701707776).equals("REDUCED")) {
            return true;
         }
      }

      return false;
   }

   public boolean isKeyUpProcessOnly() {
      return this._keyUpProcessOnly;
   }

   public int removeShortcut(String replacedString, String replacementString) {
      return this.convEngine != null ? this.convEngine.removeShortcut(replacedString, replacementString) : 4;
   }

   public String[] getShortcuts() {
      return this.convEngine != null ? this.convEngine.getShortcuts() : null;
   }

   protected boolean loadKeyLayout(Locale locale) {
      Locale keyboardLocale = Locale.getDefaultForKeyboard();
      String keyboardType = SLKeyLayout.getKeyboardType(keyboardLocale.getCode());
      int keyboardId = keyboardLocale.isKeyboardIDSet() ? keyboardLocale.getKeyboardID() : this._keyboardID;
      ResourceBundle properties = this._bundleFamily.getBundle(locale);
      SLKeyLayout res = null;
      String mapLocation = this.getMapLocation(properties);
      if (mapLocation != null) {
         boolean reduced = this.isReduced(mapLocation, properties);
         res = SLKeyLayout.getLayout(locale, reduced, keyboardId, keyboardType, locale, mapLocation, false);
         if (res == null && locale.getVariant().length() > 0) {
            res = SLKeyLayout.getLayout(locale, reduced, keyboardId, keyboardType, Locale.get(locale.getCode()), mapLocation, false);
         }

         if (res == null) {
            String language = locale.getLanguage();
            res = SLKeyLayout.getLayout(locale, reduced, keyboardId, keyboardType, Locale.get(language, ""), mapLocation, false);
            if (res == null) {
               if (language.equals("es")) {
                  res = SLKeyLayout.getLayout(locale, reduced, keyboardId, keyboardType, Locale.get("ca", ""), mapLocation, false);
               } else if ((language.equals("ar") || language.equals("ru")) && !keyboardType.equals("qwerty")) {
                  res = SLKeyLayout.getLayout(locale, reduced, keyboardId, "qwerty", locale, mapLocation, false);
                  if (res == null && locale.getVariant().length() > 0) {
                     res = SLKeyLayout.getLayout(locale, reduced, keyboardId, "qwerty", Locale.get(locale.getCode()), mapLocation, false);
                  }

                  if (res == null) {
                     res = SLKeyLayout.getLayout(locale, reduced, keyboardId, "qwerty", Locale.get(language, ""), mapLocation, false);
                  }
               }
            }

            if (res == null) {
               res = SLKeyLayout.getLayout(locale, reduced, keyboardId, keyboardType, Locale.get(language, ""), mapLocation, true);
            }
         }
      }

      if (res != null) {
         this.lnkLayout = res;
         this._keyboardID = keyboardId;
         return true;
      } else {
         return false;
      }
   }

   boolean setLocaleToCE(Locale newLocale) {
      return this.convEngine != null ? this.convEngine.setLocale(newLocale) : true;
   }

   protected String getMapLocation(ResourceBundle properties) {
      String result = null;
      String[] types = properties.getStringArray(this._optionsResource.KEYBOARD_TYPE);
      String[] locations = properties.getStringArray(this._optionsResource.KEY_LAYOUT_LOCATION);
      if (types.length == 1) {
         return locations[0];
      }

      String pattern = InternalServices.isReducedFormFactor() ? "REDUCED" : "FULL";
      int index = this.getElementIndex(types, pattern);
      if (index != -1 && index < locations.length) {
         result = locations[index];
      }

      return result;
   }

   protected boolean isReduced(String mapLocation, ResourceBundle properties) {
      int index = Arrays.getIndex(properties.getStringArray(this._optionsResource.KEY_LAYOUT_LOCATION), mapLocation);
      return StringUtilities.toUpperCase(properties.getStringArray(this._optionsResource.KEYBOARD_TYPE)[index], 1701707776).equals("REDUCED");
   }

   public void setToolbarVisible(boolean isVisible) {
      if (this.toolbar == null) {
         this.toolbar = new Toolbar(this, this.getLocale());
      }

      if (this.toolbar.isVisible() != isVisible) {
         this.toolbar.setVisible(isVisible);
      }
   }

   public boolean isToolBarVisible() {
      return this.toolbar == null ? false : this.toolbar.isVisible();
   }

   public void setLookupVisible(boolean isVisible) {
      WeakReference ref = this.getLookupRef();
      Object l = ref.get();
      if (l == null) {
         if (!isVisible) {
            return;
         }

         l = this.getViewComponent(3);
      }

      LookupIf lp = (LookupIf)l;
      if (this._isLookupEnabled || lp.isVisible() || !isVisible) {
         if (lp.isDisplayed() != isVisible) {
            lp.setVisible(isVisible);
         }

         if (isVisible) {
            lp.setLabels(this._lookupLeftLabel, this._lookupRightLabel);
            this.setLookupToRelativePos();
         }
      }
   }

   protected WeakReference getLookupRef() {
      return lookup;
   }

   public Object getViewComponent(int component) {
      switch (component) {
         case 2:
            return null;
         case 3:
            WeakReference ref = this.getLookupRef();
            Object l = ref.get();
            if (l == null) {
               l = this.createLookup();
               ref.set(l);
            }

            return l;
         case 4:
         default:
            if (this.toolbar == null) {
               this.setToolbarVisible(false);
            }

            return this.toolbar;
      }
   }

   protected Object createLookup() {
      return Lookup.getInstance(this, this.getLocale(), 0);
   }

   public int sendComposedText(SLComposedText composedText, int modifier) {
      return this.sendComposedText(composedText.getOutput(), composedText.getAttributeMask(), modifier);
   }

   protected int sendComposedText(AttributedString as, long attributeMask, int modifier) {
      if (this._debug) {
         _startTimer = System.currentTimeMillis();
      }

      ISupplementaryInputData supplementaryInputData = null;
      if (this.convEngine != null) {
         supplementaryInputData = this.convEngine.getSuplementaryInputData();
      }

      int committedCcount = this.composedText.getCommittedCharactersCountL();
      if (committedCcount > 0 && this._textFilter != null) {
         this.convertCommittedCharacters(as, committedCcount);
      }

      int convertedCount = this.composedText.getConvertedCharacterCount();
      int caretPosition = this.composedText.getCaretPosition();
      if (this.composedText.getCommittedVariantsCount() != 0) {
         this.composedText.deleteCommittedVariants();
      }

      int result = 1;
      if (this.context != null) {
         byte caretShape;
         if (this.getInputMode() == 0 && this._predictionMode != 127) {
            SLVariants variant = this.composedText.getCurrentVariant(false);
            if (this._predictionMode == 3) {
               caretShape = 1;
            } else if (variant != null && caretPosition < variant.getHighlightLength()) {
               caretShape = 3;
            } else {
               caretShape = 4;
            }
         } else {
            caretShape = 4;
         }

         result = this.context
            .dispatchInputMethodEvent(
               1100, modifier, as, attributeMask, committedCcount, convertedCount, TextHitInfo.leading(caretPosition), null, supplementaryInputData, caretShape
            );
      }

      if (this.keepSPUpdated && as.length() == committedCcount) {
         this.keepSPUpdated = false;
      }

      if (result == 1) {
         this.composedText.setLookVisible(false);
         this.setLookupVisible(false);
         if (this.convEngine != null) {
            this.convEngine.reset();
         }
      } else {
         if (this.composedText.getCommittedVariantsCount() != 0) {
            this.composedText.deleteCommittedVariants();
         }

         this.setLookupVisible(this.composedText.isLookupVisible());
      }

      if (this._debug) {
         _tlTime = _tlTime + (System.currentTimeMillis() - _startTimer);
      }

      return result;
   }

   public int sendComposedText(SLComposedText composedText) {
      return this.sendComposedText(composedText, 0);
   }

   public void sendCaretPositionChanged(boolean forCommitted, int position) {
      if (this.context != null) {
         this.context.dispatchInputMethodEvent(forCommitted ? 1102 : 1101, null, 0, 0, 0, TextHitInfo.leading(position), null);
      }
   }

   public void sendCaretShapeChanged(byte shape) {
      if (this.context != null) {
         this.context.dispatchInputMethodEvent(1104, shape, null, 0, 0, 0, null, null, null);
      }
   }

   public boolean isLearningEnabled() {
      return this.learning;
   }

   public boolean isAutoCorrectionEnabled() {
      return this.isAutoCorrectionEnabled;
   }

   public void setDirectInputState(boolean state) {
      if (this._isDirectInput != state) {
         if (this.context != null) {
            this.endComposition();
         }

         this._isDirectInput = state;
      }
   }

   protected int actionPerformedToCE(Object src, int action, Object parameter) {
      return this.convEngine != null && action != 0 ? this.convEngine.actionPerformed(action, parameter) : -1;
   }

   public boolean getIMStyleAsBoolean(int styleId) {
      return false;
   }

   protected void setLookupToRelativePos() {
      WeakReference ref = this.getLookupRef();
      Object l = ref.get();
      if (l != null) {
         LookupIf lu = (LookupIf)l;
         int[] range = this.composedText.getLookupRange(this._predictionMode == 3);
         this.context.getTextLocation(TextHitInfo.leading(range[1]), this._lookupBounds2);
         this.context.getTextLocation(TextHitInfo.leading(range[0]), this._lookupBounds);
         int left;
         int right;
         int top;
         int height;
         if (this._lookupBounds.y == this._lookupBounds2.y) {
            if (this._lookupBounds.x > this._lookupBounds2.x) {
               left = this._lookupBounds2.x;
               right = this._lookupBounds.x + this._lookupBounds.width;
            } else {
               left = this._lookupBounds.x;
               right = this._lookupBounds2.x + this._lookupBounds2.width;
            }

            top = this._lookupBounds2.y;
            height = Math.max(this._lookupBounds2.height, this._lookupBounds.height);
         } else {
            left = 0;
            right = this._lookupBounds2.x + this._lookupBounds2.width;
            height = this._lookupBounds2.height;
            top = this._lookupBounds2.y - height;
            height *= 2;
         }

         this._lookupBounds.set(left, top, right - left, height);
         lu.composedTextChanged(this._lookupBounds);
      }
   }

   public int getLearningWordlistSize() {
      return this.learningWordlistSize;
   }

   public SLComposedText getComposedText() {
      return this.composedText;
   }

   public void actionPerformedCallback(int action, Object parameter) {
      if (this.context != null) {
         this.context.actionPerformed(action, parameter);
      }
   }

   @Override
   public Object getControlObject() {
      if (this.controlObject == null) {
         this.controlObject = new SLControlObjectImpl(this);
      }

      return this.controlObject;
   }

   @Override
   public int actionPerformed(Object src, int action, Object parameter) {
      Object l = lookup.get();
      if (src != null && src == l) {
         SLVariants v = this.composedText.getCurrentVariant(true);
         v.setVariantIndex(((Lookup)l).getSelectedIndex());
         this.sendComposedText(this.composedText);
         return 0;
      }

      switch (action) {
         case 5:
            this.setDirectInputState(true);
            return 0;
         case 6:
            this.setDirectInputState(false);
            return 0;
         case 36:
            this._isLookupEnabled = true;
            return 0;
         case 37:
            this._isLookupEnabled = false;
            return 0;
         case 120:
         case 128:
            return 0;
         case 155:
            if (this.isDirectInput()) {
               return 156;
            }

            return 157;
         default:
            return this.actionPerformedToCE(src, action, parameter);
      }
   }

   @Override
   public byte[] getIMProperties(byte propID) {
      switch (propID) {
         case 1:
            return this._IMProp;
         case 3:
            if (this.convEngine != null) {
               return this.convEngine.getIMProperties(propID);
            }
         default:
            return null;
      }
   }

   @Override
   public void setIMProperties(byte propID, byte[] IMProperties) {
      switch (propID) {
         case 1:
            this._IMProp = IMProperties;
            return;
         case 3:
            if (this.convEngine != null) {
               this.convEngine.setIMProperties(propID, IMProperties);
            }
      }
   }

   @Override
   public boolean setLocale(Locale aLocale, int state) {
      boolean result = false;
      this.loadConversionEngine();
      if (aLocale != null) {
         if (aLocale.equals(this.locale) && aLocale.getKeyboardID() == this.locale.getKeyboardID() && this.lnkLayout != null) {
            return this.setLocaleToCE(aLocale);
         }

         result = this.setLocaleToCE(aLocale);
         if (result) {
            result = false;

            for (int i = 0; i < this.availableLocals.length; i++) {
               if (this.availableLocals[i].equals(aLocale)) {
                  if (this.loadKeyLayout(aLocale)) {
                     if (this.lnkLayout == null) {
                        throw new Object("Key layout is null after loadKeyLayout");
                     }

                     this.locale = aLocale;
                     this.notifyKeyLayoutChanged(this.lnkLayout);
                     if (this.toolbar == null) {
                        this.getViewComponent(4);
                     }

                     if (this.toolbar != null) {
                        this.toolbar.setLocaleIMCallback(this.locale);
                     }

                     result = true;
                     if (this.context != null) {
                        this.endComposition();
                        return result;
                     }
                  }
                  break;
               }
            }
         }
      }

      return result;
   }

   @Override
   public boolean setLocale(Locale aLocale) {
      return this.setLocale(aLocale, 0);
   }

   @Override
   public int setTextInputStyle(int style) {
      this._isLookupEnabled = (style & 536870912) == 0;
      this._keyUpProcessOnly = (style & 8192) != 0;
      this._isComplexInputAllowed = (style & 1073741824) == 0;
      this._directInput.setTextInputStyle(style);
      return this.convEngine != null ? this.convEngine.setTextInputStyle(style) : -1;
   }

   @Override
   public void setInputMethodContext(InputMethodContext newContext) {
      this.context = newContext;
      this._directInput.setInputMethodContext(newContext);
      this._directInput.setLocale(this._directLocals[0]);
      this.loadConversionEngine();
      if (this.locale == null && this.availableLocals != null && this.availableLocals.length > 0) {
         this.setLocale(this.availableLocals[0]);
      }
   }

   @Override
   public void setCompositionEnabled(boolean enable) {
   }

   @Override
   public void removeNotify() {
      if (this.isDirectInput()) {
         this._directInput.removeNotify();
      } else {
         this.endComposition();
         this.hideWindows();
      }
   }

   @Override
   public void reconvert() {
   }

   @Override
   public int loadLinguisticData(LinguisticData data) {
      return this.convEngine == null ? 1 : this.convEngine.loadLinguisticData(data);
   }

   @Override
   public void notifyClientWindowChange(XYRect bounds) {
   }

   @Override
   public int unloadLinguisticData(int id) {
      return this.convEngine != null ? this.convEngine.unloadLinguisticData(id) : 1;
   }

   @Override
   public boolean isCompositionEnabled() {
      return this.isDirectInput() ? this._directInput.isCompositionEnabled() : this.compositionEnabled;
   }

   @Override
   public void hideWindows() {
      if (this.isDirectInput()) {
         this._directInput.hideWindows();
      } else {
         this.setLookupVisible(false);
         this.setToolbarVisible(false);
         this._lookupBounds.set(0, 0, 0, 0);
         this._lookupBounds2.set(0, 0, 0, 0);
         Object l = lookup.get();
         if (l != null) {
            ((Lookup)l).reset();
         }
      }
   }

   @Override
   public Locale getLocale() {
      return this.locale;
   }

   @Override
   public boolean isCorrectWord(StringBufferGap sbg, int startIndex, int length) {
      return false;
   }

   @Override
   public void reset(int type) {
      if (type == 1) {
         if (this.isDirectInput()) {
            this._directInput.reset(type);
         } else if (this.convEngine != null) {
            this.convEngine.reset();
         }

         if (this.composedText != null) {
            this.composedText.setLookVisible(false);
         }
      }
   }

   @Override
   public int setListener(InputModeChangeListener listener) {
      return 3;
   }

   @Override
   public InputModeChangeListener getListener() {
      return null;
   }

   @Override
   public CustomWordsRepository getRepository(int type) {
      return null;
   }

   @Override
   public CustomDictionary getCustomDictionary(int type) {
      return null;
   }

   @Override
   public void endComposition() {
      this.hideWindows();
      if (!this.composedText.isEmpty()) {
         if (this.isDirectInput()) {
            this._directInput.endComposition();
         } else {
            if (this.convEngine != null) {
               this.convEngine.endComposition();
            }

            this.composedText.commitAll();
            if (this.context != null) {
               this.sendComposedText(this.composedText);
            }
         }
      }
   }

   @Override
   public void dispose() {
      if (this.isDirectInput()) {
         this._directInput.dispose();
      } else {
         this.endComposition();
         this.hideWindows();
      }
   }

   @Override
   public void dispatchEvent(Event event) {
      if (this.compositionEnabled && (ACCEPTIBLE_EVENTS_MASK & event.getEventMask()) != 0) {
         if (event instanceof Object) {
            if (event.getID() == 1005) {
               this.focusLost(event.getSource());
            }
         } else if (event instanceof Object) {
            this.dispatchKeyEvent((KeyEvent)event);
         } else {
            if (event instanceof Object) {
               this.dispatchNavigationEvent((NavigationEvent)event);
            }
         }
      }
   }

   @Override
   public void deactivate(boolean isTemporary) {
      lookup.set(null);
   }

   @Override
   public void activate() {
      this.setLookupVisible(false);
      lookup.set(null);
      this.updateCookieCache();
      if (this.isDirectInput()) {
         this._directInput.activate();
      } else {
         this.setToolbarVisible(true);
      }
   }

   private boolean isKeyUpEventShouldBeConsumed(KeyEvent anEvent) {
      boolean ret = false;
      switch (anEvent.getKeyCode()) {
         default:
            if (this._isKeyDownEventsWasConsumed[1]
               && (
                  this._keyDownModifiers[1] == 32768
                     || anEvent.getKeyCode() == this._keyDownKeycodes[1]
                        && anEvent.getModifiers() == this._keyDownModifiers[1]
                        && Math.abs(anEvent.getWhen() - this._keyDownTime) < 1000
               )) {
               return true;
            } else if (this._isKeyDownEventsWasConsumed[0]
               && anEvent.getKeyCode() == this._keyDownKeycodes[0]
               && anEvent.getModifiers() == this._keyDownModifiers[0]) {
               ret = true;
            }
         case 10:
         case 27:
         case 127:
         case 131:
         case 132:
            return ret;
      }
   }

   private int getElementIndex(String[] array, String pattern) {
      for (int i = array.length - 1; i >= 0; i--) {
         if (StringUtilities.toUpperCase(array[i], 1701707776).equals(pattern)) {
            return i;
         }
      }

      return -1;
   }

   private void updateCookieCache() {
      this._imCookieCache = this.context.getIMCookieCache();
   }

   private boolean isKeyUpProcessOnly(KeyEvent event) {
      return this._keyUpProcessOnly && event.isInputEvent();
   }

   private void convertCommittedCharacters(AttributedString text, int count) {
      count--;
      int composedTextPosition = this.context.getComposedTextStart();

      while (count >= 0) {
         char ch = this._textFilter.convert(text.charAt(count), null, composedTextPosition + count, 0);
         if (!CharacterUtilities.isPrintable(ch)) {
            ch = '?';
         }

         text.replace(count, count + 1, ch);
         count--;
      }
   }

   private void notifyKeyLayoutChanged(SLKeyLayout newLayout) {
      IConversion convEng = this.convEngine;
      if (convEng != null) {
         convEng.inputMethodKeyLayoutChanged(newLayout);
      }
   }

   public SLInputMethod(ResourceBundleFamily bundleFamily, SLInputMethod$IMOptions optionsResource) {
      this.setOptionsBundle(bundleFamily, optionsResource);
      this.verifyLookupReset();
   }
}
