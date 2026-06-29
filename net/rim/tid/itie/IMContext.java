package net.rim.tid.itie;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.ui.XYRect;
import net.rim.tid.awt.Event;
import net.rim.tid.awt.event.InputMethodEvent;
import net.rim.tid.awt.im.InputContext;
import net.rim.tid.awt.im.InputMethodRequests;
import net.rim.tid.awt.im.spi.InputMethod;
import net.rim.tid.awt.im.spi.InputMethodContext;
import net.rim.tid.im.ISupplementaryInputData;
import net.rim.tid.im.SLControlObject;
import net.rim.tid.text.AttributedString;
import net.rim.tid.text.AttributedTextIterator;
import net.rim.tid.text.TextHitInfo;
import net.rim.tid.util.Utils;
import net.rim.vm.WeakReference;

public final class IMContext extends InputContext implements InputMethodContext {
   private WeakReference _event;
   private EventHandler _eventHandler;
   private LingDataRegistry _lingDataRegistry;
   private SecureBufferRegistry _secureBufferRegistry;
   private static IMContext _instance;

   final EventHandler getEventHandler() {
      return this._eventHandler;
   }

   final InputMethod getInputMethod() {
      return super._inputMethod;
   }

   @Override
   public final int dispatchInputMethodEvent(
      int id,
      int modifiers,
      AttributedString text,
      long attribTextMask,
      int committedCharacterCount,
      int convertedCharacterCount,
      TextHitInfo caret,
      TextHitInfo visiblePosition,
      ISupplementaryInputData supplementaryInputData
   ) {
      return this.dispatchInputMethodEvent(
         id, modifiers, text, attribTextMask, committedCharacterCount, convertedCharacterCount, caret, visiblePosition, supplementaryInputData, (byte)4
      );
   }

   @Override
   public final int dispatchInputMethodEvent(
      int id,
      int modifiers,
      AttributedString text,
      long attribTextMask,
      int committedCharacterCount,
      int convertedCharacterCount,
      TextHitInfo caret,
      TextHitInfo visiblePosition,
      ISupplementaryInputData supplementaryInputData,
      byte caretShape
   ) {
      IComponent component = this.getInputComponent();
      super._lastIMEventResult = 1;
      if (component != null) {
         InputMethodEvent event = this.getIMEvent(
            component,
            id,
            modifiers,
            text,
            attribTextMask,
            committedCharacterCount,
            convertedCharacterCount,
            caret,
            visiblePosition,
            supplementaryInputData,
            caretShape
         );
         switch (id) {
            case 1099:
            case 1103:
               break;
            case 1100:
            default:
               super._lastIMEventResult = component.inputMethodTextChanged(event);
               break;
            case 1101:
            case 1102:
            case 1104:
               super._lastIMEventResult = component.caretPositionChanged(event);
         }
      }

      return super._lastIMEventResult;
   }

   @Override
   public final void setIMCookieCache(Object cookie) {
      IComponent component = this.getInputComponent();
      if (component != null) {
         component.setIMCookieCache(cookie);
      }
   }

   @Override
   public final int getInsertPositionOffset() {
      InputMethodRequests r = this.getInputMethodRequests();
      return r != null ? r.getInsertPositionOffset() : -1;
   }

   @Override
   public final AttributedTextIterator getCommittedText(int beginIndex, int endIndex, String[] attributes) {
      return null;
   }

   @Override
   public final Object getIMCookieCache() {
      InputMethodRequests r = this.getInputMethodRequests();
      return r != null ? r.getIMCookieCache() : null;
   }

   @Override
   public final void getTextLocation(TextHitInfo offset, XYRect aResult) {
      InputMethodRequests requests = this.getInputMethodRequests();
      if (requests != null) {
         requests.getTextLocation(offset, aResult);
      }
   }

   @Override
   public final void setComposedText(int start, int end) {
      InputMethodRequests requests = this.getInputMethodRequests();
      if (requests != null) {
         requests.setComposedText(start, end);
      }
   }

   @Override
   public final AttributedString getAttributedText() {
      InputMethodRequests requests = this.getInputMethodRequests();
      return requests != null ? requests.getAttributedText() : null;
   }

   @Override
   public final int getComposedTextStart() {
      InputMethodRequests requests = this.getInputMethodRequests();
      return requests != null ? requests.getComposedTextStart() : -1;
   }

   @Override
   public final int getComposedTextEnd() {
      InputMethodRequests requests = this.getInputMethodRequests();
      return requests != null ? requests.getComposedTextEnd() : -1;
   }

   @Override
   public final int getLabelLength() {
      InputMethodRequests requests = this.getInputMethodRequests();
      return requests != null ? requests.getLabelLength() : -1;
   }

   @Override
   public final int getCaretPosition() {
      InputMethodRequests requests = this.getInputMethodRequests();
      return requests != null ? requests.getCaretPosition() : -1;
   }

   @Override
   public final int getAnchorPosition() {
      InputMethodRequests requests = this.getInputMethodRequests();
      return requests != null ? requests.getAnchorPosition() : -1;
   }

   @Override
   public final int getLatestCommittedTextStart() {
      InputMethodRequests requests = this.getInputMethodRequests();
      return requests != null ? requests.getLatestCommittedTextStart() : -1;
   }

   @Override
   public final int getLatestCommittedTextEnd() {
      InputMethodRequests requests = this.getInputMethodRequests();
      return requests != null ? requests.getLatestCommittedTextEnd() : -1;
   }

   @Override
   public final TextHitInfo getLocationOffset(int x, int y) {
      return null;
   }

   @Override
   public final AttributedTextIterator cancelLatestCommittedText() {
      return null;
   }

   @Override
   public final AttributedTextIterator getSelectedText() {
      return null;
   }

   @Override
   public final int getSelectionStart() {
      return -1;
   }

   @Override
   public final int getSelectionOffset() {
      return -1;
   }

   @Override
   public final int getSelectionEnd() {
      return -1;
   }

   @Override
   public final int getCommittedTextLength() {
      return -1;
   }

   @Override
   public final AttributedTextIterator getText(int start, int end, boolean makeUncommitted) {
      return null;
   }

   @Override
   public final void actionPerformed(int action, Object parameter) {
      IComponent component = this.getInputComponent();
      if (component != null) {
         component.actionPerformed(action, parameter);
      }
   }

   @Override
   public final int dispatchInputMethodEvent(
      int id,
      AttributedString text,
      long attribTextMask,
      int committedCharacterCount,
      int convertedCharacterCount,
      TextHitInfo caret,
      TextHitInfo visiblePosition
   ) {
      return this.dispatchInputMethodEvent(id, 0, text, attribTextMask, committedCharacterCount, convertedCharacterCount, caret, visiblePosition, null, (byte)4);
   }

   @Override
   public final InputMethod getInputMethod(Locale requestedLocale) {
      return requestedLocale != null ? super._manager.getInputMethod(requestedLocale, null) : null;
   }

   private final void handleException(Throwable th) {
      th.printStackTrace();
      Runnable _invoker = new IMContext$1(this);
      Application app = Application.getApplication();
      if (app != null) {
         app.invokeLater(_invoker);
      }

      try {
         String appName = null;
         if (app != null) {
            appName = app.toString() + " --> " + app.getClass().getName();
         }

         StringBuffer debugInfo = new StringBuffer();
         if (super._inputMethod != null) {
            System.err.println("WARNING: The instance of " + super._inputMethod.toString() + " has been removed. Application - " + appName);

            try {
               super._inputMethod.actionPerformed(this, 112, debugInfo);
            } catch (Throwable e) {
               e.printStackTrace();
            }
         }

         super._inputMethod = null;
         super._manager.dispose();
         IComponent component = this.getInputComponent();
         if (component != null) {
            System.err.println(component.getClass().getName());
            component.actionPerformed(112, debugInfo);
            System.err.println(debugInfo);
            InputMethodEvent event = this.getIMEvent(component, 1103, 0, new AttributedString(), 0, 0, 0, null, null, null, (byte)4);
            component.inputMethodTextChanged(event);
         }
      } catch (Throwable thr) {
         thr.printStackTrace();
      }

      Utils.reportException(th);
      Locale l = this.getLocale();
      this.selectInputMethod(l);
      RIMGlobalMessagePoster.postGlobalEvent(-3705893009697257465L);
   }

   @Override
   protected final synchronized boolean selectInputMethod(Locale locale, String imName, int state) {
      if (super.selectInputMethod(locale, imName, state)) {
         this.loadLingData(locale);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final void enableLookup(boolean enable) {
      if (super._inputMethod != null) {
         ((SLControlObject)super._inputMethod.getControlObject()).actionPerformed(enable ? 36 : 37, null);
      }
   }

   private final void loadLingData(Locale locale) {
      if (this._lingDataRegistry != null) {
         synchronized (this._lingDataRegistry) {
            int lCode = locale.getCode();
            LinguisticData data = this._lingDataRegistry.getLingData(lCode);
            this.processLingDataLoad(data, lCode);
            if (data == null) {
               int lang = lCode & -65536;
               if (lang != lCode) {
                  data = this._lingDataRegistry.getLingData(lang);
                  this.processLingDataLoad(data, lang);
               }

               if (data == null) {
                  data = this._lingDataRegistry.getLingData(0);
                  this.processLingDataLoad(data, 0);
               }
            }
         }
      }
   }

   private final void processLingDataLoad(LinguisticData data, int locale) {
      try {
         if (data != null) {
            LinguisticData first = data;
            LinguisticData last = data;

            do {
               if ((super._inputMethod.loadLinguisticData(data) & 2) != 0) {
                  if (data == first) {
                     last = data._next;
                     first = data._next;
                  } else {
                     last._next = data._next;
                  }
               } else {
                  last = data;
               }
            } while ((data = data._next) != null);

            this._lingDataRegistry.update(locale, first);
            return;
         }
      } catch (Throwable e) {
         Utils.reportException(e);
      }
   }

   @Override
   public final void addSecureBuffer(ISecureInputMethodBuffer buffer) {
      this._secureBufferRegistry.registerBuffer(buffer);
   }

   @Override
   protected final boolean changeIM(Locale locale, InputMethod im) {
      boolean ret = im.setLocale(locale);
      if (ret) {
         im.setInputMethodContext(this);
         im.endComposition();
      }

      return ret;
   }

   private final InputMethodEvent getIMEvent(
      IComponent component,
      int id,
      int modifiers,
      AttributedString text,
      long attribTextMask,
      int committedCharacterCount,
      int convertedCharacterCount,
      TextHitInfo caret,
      TextHitInfo visiblePosition,
      ISupplementaryInputData supplementaryInputData,
      byte caretShape
   ) {
      InputMethodEvent event = (InputMethodEvent)this._event.get();
      if (event == null) {
         event = new InputMethodEvent(component, id, modifiers, text, attribTextMask, committedCharacterCount, convertedCharacterCount, caret, visiblePosition);
         event.setCaretShape(caretShape);
         this._event.set(event);
      } else {
         event.init(component, id, modifiers, text, attribTextMask, committedCharacterCount, convertedCharacterCount, caret, visiblePosition, true, caretShape);
      }

      event.setSupplementaryInputData(supplementaryInputData);
      return event;
   }

   @Override
   public final void dispatchEvent(Event event) {
      try {
         super.dispatchEvent(event);
      } catch (Throwable e) {
         this.handleException(e);
      }
   }

   @Override
   protected final void notifyClientWindowChange() {
   }

   @Override
   public final void endComposition() {
      try {
         super.endComposition();
      } catch (Throwable e) {
         this.handleException(e);
      }
   }

   @Override
   public final synchronized boolean addIMDescriptor(String imName, String className, boolean isAuxiliary) {
      boolean ret = super._manager.addIMDescriptor(imName, className);
      if (isAuxiliary && ret && super._inputMethod != null) {
         this.loadLingData(super._inputMethod.getLocale());
      }

      return ret;
   }

   public static final IMContext getInstance0() {
      if (_instance == null) {
         _instance = new IMContext();
      }

      return _instance;
   }

   private IMContext() {
      super._manager = new IMManager();
      this._event = new WeakReference(null);
      this._lingDataRegistry = new LingDataRegistry(this);
      this._eventHandler = new EventHandler();
      this._secureBufferRegistry = new SecureBufferRegistry();
   }

   @Override
   public final LingDataRegistry getLingDataRegistry() {
      return this._lingDataRegistry;
   }

   private final InputMethodRequests getInputMethodRequests() {
      IComponent comp = this.getInputComponent();
      return comp != null ? comp.getInputMethodRequests() : null;
   }
}
