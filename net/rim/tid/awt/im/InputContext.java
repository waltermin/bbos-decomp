package net.rim.tid.awt.im;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontRegistry;
import net.rim.device.api.ui.InvokableAction;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.ui.component.TextInputDialog;
import net.rim.tid.awt.Event;
import net.rim.tid.awt.event.FocusEvent;
import net.rim.tid.awt.event.InputMethodEvent;
import net.rim.tid.awt.im.repository.CustomDictionary;
import net.rim.tid.awt.im.repository.CustomWordsRepository;
import net.rim.tid.awt.im.spi.InputMethod;
import net.rim.tid.awt.im.spi.InputModeChangeListener;
import net.rim.tid.im.spellcheck.SpellCheckUtilities;
import net.rim.tid.itie.EventHandler;
import net.rim.tid.itie.IComponent;
import net.rim.tid.itie.IMContext;
import net.rim.tid.itie.IMManager;
import net.rim.tid.itie.ISecureInputMethodBuffer;
import net.rim.tid.itie.LingDataRegistry;
import net.rim.tid.text.AttributedString;
import net.rim.vm.Monitor;
import net.rim.vm.WeakReference;

public class InputContext {
   protected InputMethod _inputMethod;
   protected InputMethod _previousIM;
   protected int _lastIMEventResult;
   protected boolean _isComponentAllowed = true;
   protected boolean _isIMSwitcherEnabled = true;
   protected IMManager _manager;
   protected Locale _lastUsedLocale;
   private Locale _cachedLocale;
   private boolean _iInitialised;
   private Thread _initializingThread;
   private int _focusGainedAppId = -1;
   private InvokableActionProducer _invokableActionProducer;
   private boolean _imSwitcherAllowed;
   private static final long REGISTRY_NAME = -141676089031756153L;
   protected static WeakReference _component = new WeakReference(null);
   private static InputContext _context;

   protected InputContext() {
   }

   protected synchronized boolean selectInputMethod(Locale locale, String imName, int state) {
      boolean ret = false;
      if (locale != null && (this.isUnicodeInputAllowed() || this._inputMethod == null || state == 2 || state == 1)) {
         this.endComposition();
         Locale lastUsedLocale;
         if (this._inputMethod != null) {
            lastUsedLocale = this._inputMethod.getLocale();
            if (this._inputMethod.setLocale(locale, state)) {
               ret = true;
            } else {
               this.enableClientWindowNotification(this._inputMethod, false);
               this._inputMethod.hideWindows();
               this._inputMethod.deactivate(false);
            }
         } else {
            lastUsedLocale = Locale.getDefaultInputForSystem();
         }

         if (!SpellCheckUtilities.isSpellCheckVariant(lastUsedLocale)) {
            this._lastUsedLocale = lastUsedLocale;
         }

         if (!ret) {
            InputMethod im = this._manager.getInputMethod(locale, imName);
            if (im != null) {
               this._previousIM = this._inputMethod;
               ret = this.changeIM(locale, im);
               if (!ret) {
                  this._previousIM = null;
               } else {
                  this._inputMethod = im;
                  int hanMask = this.getHanMaskForLocale(this.getLocale());
                  if (hanMask != 0) {
                     Font curFont = FontRegistry.getDefaultFont();
                     Font newFont = this.getFontForHanMask(curFont, hanMask);
                     if (curFont != newFont) {
                        FontRegistry.setDefaultFont(newFont);
                     }

                     Font appFont = Font.getDefault();
                     if (newFont != appFont && (appFont.getStyle() & hanMask) != hanMask) {
                        Font.setDefaultFont(this.getFontForHanMask(appFont, hanMask));
                     }
                  }

                  this.updateHanMask(this.getInputComponent(), locale);
               }
            }
         }

         return ret;
      } else {
         return ret;
      }
   }

   public boolean selectInputMethod(Locale locale) {
      return this.selectInputMethod(locale, null, 0);
   }

   public void releaseComponent() {
      this.endComposition();
      _component.set(null);
   }

   public IComponent getInputComponent() {
      return (IComponent)_component.get();
   }

   public long getAvailableInputMethods() {
      return this._manager.getAvailableInputMethodIDs();
   }

   public long getActiveInputMethodID() {
      return this._manager.getLatestRequestedInputMethodID();
   }

   public boolean isAutoPeriodOn() {
      switch (Locale.getDefaultInputForSystem().getCode() & -65536) {
         case 1784741887:
            return true;
         case 1784741888:
         default:
            return false;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static InputContext getInstance(boolean init) {
      if (init && !_context._iInitialised) {
         synchronized (_context) {
            if (!_context._iInitialised) {
               Thread currentThread = Thread.currentThread();
               if (_context._initializingThread == currentThread) {
                  throw new Error("Illegal recursive initialization request");
               }

               _context._initializingThread = Thread.currentThread();
               boolean var10 = false /* VF: Semaphore variable */;

               try {
                  var10 = true;
                  Locale initialLocale = Locale.getDefaultInputForSystem();
                  if (_context.selectInputMethod(initialLocale)) {
                     if (!initialLocale.equals(_context._inputMethod.getLocale())) {
                        Locale.setDefaultInputForSystem(_context._inputMethod.getLocale(), false);
                     }
                  } else {
                     int localeCode = initialLocale.getCode();
                     Locale l = Locale.get(localeCode & -65536);
                     if ((localeCode & 65535) == 0 || !_context.selectInputMethod(l)) {
                        l = Locale.get(1701707776);
                        if (!_context.selectInputMethod(l)) {
                           throw new Error("no input support can be provided for " + initialLocale.toString() + " locale");
                        }
                     }

                     System.err.println("WARNING: initial input locale " + initialLocale.toString() + " has changed to " + l.toString());
                     _context._iInitialised = true;
                     Locale.setDefaultInputForSystem(l, false);
                  }

                  _context._iInitialised = true;
                  var10 = false;
               } finally {
                  if (var10) {
                     _context._initializingThread = null;
                  }
               }

               _context._initializingThread = null;
            }
         }
      }

      return _context;
   }

   public boolean isInitialised() {
      return this._iInitialised;
   }

   public static InputContext getInstance() {
      return getInstance(true);
   }

   protected boolean changeIM(Locale locale, InputMethod im) {
      return false;
   }

   protected void notifyClientWindowChange() {
   }

   public void addSecureBuffer(ISecureInputMethodBuffer buffer) {
   }

   public void enableClientWindowNotification(InputMethod im, boolean state) {
   }

   public boolean addIMDescriptor(String imName, String className) {
      return this.addIMDescriptor(imName, className, false);
   }

   public boolean addIMDescriptor(String imName, String className, boolean isAuxiliary) {
      return false;
   }

   public Locale getLocale() {
      return this._inputMethod == null ? Locale.getDefaultInputForSystem() : this._inputMethod.getLocale();
   }

   public Locale getLastUsedLocale() {
      return this._lastUsedLocale;
   }

   public void setCompositionEnabled(boolean enable) {
      if (this._inputMethod != null) {
         this._inputMethod.setCompositionEnabled(enable);
      }
   }

   public boolean isCompositionEnabled() {
      return this._inputMethod != null ? this._inputMethod.isCompositionEnabled() : false;
   }

   public void reconvert() {
      if (this._inputMethod != null) {
         this._inputMethod.reconvert();
      }
   }

   public synchronized void dispatchEvent(Event event) {
      switch (event.getID()) {
         case 100:
         case 101:
            this.notifyClientWindowChange();
            return;
         case 1004:
            this.focusGained((FocusEvent)event);
            return;
         case 1005:
            this.focusLost((FocusEvent)event);
            return;
         default:
            if (this._inputMethod != null) {
               this._inputMethod.dispatchEvent(event);
            }
      }
   }

   public synchronized void endComposition() {
      if (this._lastIMEventResult == 1) {
         this._lastIMEventResult = 0;
         if (this._previousIM != null) {
            this._previousIM.endComposition();
         }

         this.cleanComponent((IComponent)_component.get());
      }

      if (this._inputMethod != null) {
         this._inputMethod.endComposition();
      }
   }

   private void cleanComponent(IComponent component) {
      if (component != null) {
         InputMethodEvent restartEvent = new InputMethodEvent(component, 1103, 0, new AttributedString(), 0, 0, 0, null, null);
         component.inputMethodTextChanged(restartEvent);
      }
   }

   private synchronized void reset(int type) {
      if (this._inputMethod != null) {
         this._inputMethod.reset(type);
         if (type == 1) {
            this.cleanComponent((IComponent)_component.get());
         }
      }
   }

   public void dispose() {
      if (this._inputMethod != null) {
         this._inputMethod.endComposition();
         this._inputMethod.hideWindows();
         this._inputMethod.deactivate(true);
      }

      this._inputMethod = null;
   }

   public synchronized void notifyAppSwitch(boolean foreground, int appID) {
      IComponent comp = (IComponent)_component.get();
      if (comp == null) {
         if (foreground) {
            this.reset(0);
         }
      } else {
         EventHandler eventHandler = EventHandler.getInstance();
         if (foreground) {
            IComponent fcomp = null;
            Field f = null;
            if (UiApplication.getUiApplication() != null) {
               Screen s = UiApplication.getUiApplication().getActiveScreen();
               if (s != null) {
                  f = s.getLeafFieldWithFocus();
                  if (f == comp) {
                     fcomp = comp;
                  } else if (f != null && comp != null && f.getInputMethodRequests() == comp.getInputMethodRequests()) {
                     fcomp = comp;
                  }
               }
            }

            eventHandler.focusGained(fcomp, (int)System.currentTimeMillis(), appID);
            return;
         }

         comp.actionPerformed(141, null);
         eventHandler.actionPerformed(1, null);
         UiApplication app = UiApplication.getUiApplication();
         if (app == null) {
            return;
         }

         Screen topAppScreen = app.getActiveScreen();
         if (topAppScreen instanceof TextInputDialog) {
            this.endComposition();
            if (this._inputMethod != null) {
               this._inputMethod.hideWindows();
            }

            eventHandler.focusLost(comp, (int)System.currentTimeMillis(), appID);
            return;
         }
      }
   }

   public synchronized Object getInputMethodControlObject() {
      if (this._inputMethod == null) {
         if (this._iInitialised) {
            this.selectInputMethod(Locale.getDefaultInputForSystem());
         } else {
            getInstance();
         }

         return this._inputMethod != null ? this._inputMethod.getControlObject() : null;
      } else {
         return this._inputMethod.getControlObject();
      }
   }

   public void enableLookup(boolean enable) {
   }

   public synchronized void setIMSwitchEnabled(boolean enable) {
      this._isIMSwitcherEnabled = enable;
   }

   public synchronized boolean isIMSwitchAllowed() {
      return this._imSwitcherAllowed;
   }

   private boolean isIMSwitchAllowedHelper() {
      return !this._isIMSwitcherEnabled ? false : this.isUnicodeInputAllowed();
   }

   private boolean isUnicodeInputAllowed() {
      if (this._inputMethod != null && this._isComponentAllowed) {
         if (this._cachedLocale != null) {
            return false;
         }

         IComponent c = this.getInputComponent();
         if (c == null) {
            return true;
         }

         if (c instanceof TextField) {
            return ((TextField)c).isUnicodeInputAllowed();
         }
      }

      return true;
   }

   public synchronized int getAppId() {
      return this._focusGainedAppId;
   }

   private void switchLocaleIfNeeded(IComponent comp, Locale switchLocale) {
      if (comp instanceof TextField) {
         TextField field = (TextField)comp;
         Locale currentLocale = switchLocale == null ? this.getLocale() : switchLocale;
         Locale intendedLocale = switchLocale == null ? currentLocale : switchLocale;
         if (!Locale.isLatinOneCharacterSetLocale(intendedLocale) && (!field.isUnicodeInputAllowed() || (field.getTextInputStyle() & 1073741824) != 0)) {
            Locale alternativeLocale = null;
            Locale l;
            if ((this.getActiveInputMethodID() & 20480) != 0) {
               l = Locale.get("en", "US", "Multitap");
               alternativeLocale = Locale.get("en", "US");
            } else {
               l = Locale.get("en", "US");
            }

            if (this._cachedLocale == null) {
               this._cachedLocale = currentLocale;
            }

            if (l != currentLocale && !this.selectInputMethod(l, null, 1) && alternativeLocale != null) {
               this.selectInputMethod(alternativeLocale, null, 1);
               return;
            }
         } else if (switchLocale != null) {
            this.selectInputMethod(this._cachedLocale, null, 2);
            this._cachedLocale = null;
         }
      }
   }

   private synchronized void focusGained(FocusEvent event) {
      IComponent comp = event.getSource();
      event.setSource(null);
      Locale switchLocale = null;
      if (this._focusGainedAppId == -1 || this._focusGainedAppId == event.getApplicationId()) {
         this.endComposition();
         if (this._focusGainedAppId == event.getApplicationId()) {
            if (this._cachedLocale != null && comp != _component.get()) {
               switchLocale = this._cachedLocale;
            }

            this.reset(0);
         }

         if (comp.getInputMethodRequests() != null && comp.isInputMethodEnabled()) {
            _component.set(comp);
            event.setSource(comp);
            this._focusGainedAppId = event.getApplicationId();
         } else {
            event.setSource((IComponent)_component.get());
            if (this._focusGainedAppId != -1) {
               this._focusGainedAppId = event.getApplicationId();
            }
         }
      } else if (comp.getInputMethodRequests() != null && comp.isInputMethodEnabled()) {
         _component.set(comp);
         event.setSource(comp);
         this._focusGainedAppId = event.getApplicationId();
         this.reset(1);
      } else {
         event.setSource((IComponent)_component.get());
      }

      this.switchLocaleIfNeeded(comp, switchLocale);
      comp = event.getSource();
      if (this._inputMethod != null && comp != null) {
         this.updateHanMask(comp, this.getLocale());
         this._inputMethod.dispatchEvent(event);
         if (comp.getInputMethodRequests() != null && comp.isInputMethodEnabled()) {
            this._isComponentAllowed = true;
            this._inputMethod.activate();
         }
      }

      this._imSwitcherAllowed = this.isIMSwitchAllowedHelper();
   }

   private void updateHanMask(IComponent comp, Locale locale) {
      if (comp != null && locale != null) {
         int hanMask = this.getHanMaskForLocale(locale);
         if (hanMask != 0) {
            Font curFont = FontRegistry.getDefaultFont();
            Font appFont = Font.getDefault();
            if (curFont != appFont) {
               if ((appFont.getStyle() & hanMask) != hanMask) {
                  Font.setDefaultFont(this.getFontForHanMask(appFont, hanMask));
               }

               curFont = appFont;
            }

            if (comp instanceof BasicEditField) {
               Font f = ((BasicEditField)comp).getFont();
               if (f != null && f != curFont && (f.getStyle() & hanMask) != hanMask) {
                  Font newFont = this.getFontForHanMask(f, hanMask);
                  Application app = Application.getApplication();
                  if (app == null || Monitor.monitorOwned(Application.getEventLock())) {
                     comp.setFont(newFont);
                     return;
                  }

                  app.invokeLater(new InputContext$1(this, comp, newFont));
               }
            }
         }
      }
   }

   private Font getFontForHanMask(Font origin, int mask) {
      int style = origin.getStyle() & -7169;
      return origin.derive(style | mask);
   }

   private int getHanMaskForLocale(Locale locale) {
      int lCode = locale.getCode();
      int lang = lCode & -65536;
      if (lang == Locale.get("ja").getCode()) {
         return 3072;
      } else if (lang == 2053636096) {
         String country = locale.getCountry();
         return country.length() != 0 && !country.equals("CN") ? 1024 : 2048;
      } else {
         return lang == Locale.get("ko").getCode() ? 4096 : 0;
      }
   }

   private synchronized void focusLost(FocusEvent event) {
      if (event.getApplicationId() == this._focusGainedAppId) {
         Object comp = _component.get();
         if (event.getSource() == comp) {
            if (this._cachedLocale != null) {
               this.selectInputMethod(this._cachedLocale, null, 2);
               this._cachedLocale = null;
            }

            this.endComposition();
            if (this._inputMethod != null) {
               this._inputMethod.dispatchEvent(event);
            }

            this._focusGainedAppId = -1;
            this._isComponentAllowed = false;
            return;
         }
      } else if (event.getSource() != null && this._inputMethod != null) {
         this.cleanComponent(event.getSource());
      }
   }

   public LingDataRegistry getLingDataRegistry() {
      return null;
   }

   public IMManager getInputMethodsManager() {
      return this._manager;
   }

   public int setListener(InputModeChangeListener listener) {
      return this._inputMethod != null ? this._inputMethod.setListener(listener) : 2;
   }

   public InputModeChangeListener getListener() {
      return this._inputMethod != null ? this._inputMethod.getListener() : null;
   }

   public CustomWordsRepository getRepository(int type) {
      if (type == 1 || type == 6) {
         return this._manager.getRepository(type);
      } else {
         return this._inputMethod != null ? this._inputMethod.getRepository(type) : null;
      }
   }

   public CustomDictionary getCustomDictionary(int type) {
      return this._inputMethod != null ? this._inputMethod.getCustomDictionary(type) : null;
   }

   public boolean hasSureType() {
      return (this._manager.getAvailableInputMethodIDs() & 4096) != 0;
   }

   public long getInputMethodIDForLocale(Locale locale) {
      return this._manager.getInputMethodIDForLocale(locale);
   }

   public boolean isSureType() {
      return (this.getActiveInputMethodID() & 4096) != 0;
   }

   public void setInvokableActionProducer(InvokableActionProducer producer) {
      this._invokableActionProducer = producer;
   }

   public InvokableAction[] getIMActions(Object comp) {
      return this._invokableActionProducer != null ? this._invokableActionProducer.getIMActions(comp) : null;
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _context = (InputContext)ar.getOrWaitFor(-141676089031756153L);
      if (_context == null) {
         _context = IMContext.getInstance0();
         ar.put(-141676089031756153L, _context);
      }
   }
}
