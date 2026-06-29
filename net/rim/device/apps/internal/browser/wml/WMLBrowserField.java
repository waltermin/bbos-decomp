package net.rim.device.apps.internal.browser.wml;

import java.util.Vector;
import net.rim.device.api.browser.field.BrowserContentChangedEvent;
import net.rim.device.api.browser.field.Destroyable;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.browser.core.IBrowserContext;
import net.rim.device.apps.internal.browser.ui.VerticalIndentFieldManager;

final class WMLBrowserField extends VerticalIndentFieldManager implements Destroyable {
   private Vector _textVariables;
   private Vector _inputFields;
   private WMLContextManager _contextManager;
   private String _currentCardId;
   private WMLVariable _cardTitle;
   private Verb _onEnterForward;
   private Verb _onEnterBackward;
   private WMLBrowserContent _browserContent;
   private int _destroyMethod;

   final void setTextVariables(Vector vars) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   final void setCurrentCardId(String currentCardId) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   final String getCurrentCardId() {
      return this._currentCardId;
   }

   final void setTitle(WMLVariable title) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   final int getInputFieldCount() {
      return this._inputFields == null ? 0 : this._inputFields.size();
   }

   public final String getTitle() {
      return this._cardTitle != null ? this._cardTitle.getName() : null;
   }

   public final boolean isModified() {
      if (this._inputFields == null) {
         return false;
      }

      int size = this._inputFields.size();

      for (int i = 0; i < size; i++) {
         if (((WMLInputField)this._inputFields.elementAt(i)).isModified()) {
            return true;
         }
      }

      return false;
   }

   final void setOnEnterForward(Verb verb) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   final void setOnEnterBackward(Verb verb) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   final Verb getOnEnterForward() {
      return this._onEnterForward;
   }

   final Verb getOnEnterBackward() {
      return this._onEnterBackward;
   }

   public final void jumpToNextInputField(int currentInputIndex) {
      if (this._inputFields != null && this._inputFields.size() > currentInputIndex + 1) {
         WMLInputField field = (WMLInputField)this._inputFields.elementAt(currentInputIndex + 1);
         if (field != null) {
            field.setFocus();
         }
      }
   }

   public final IBrowserContext getContext() {
      return this._contextManager != null ? this._contextManager.getContext() : null;
   }

   final void addInputField(WMLInputField input) {
      if (this._inputFields == null) {
         this._inputFields = new Vector();
      }

      this._inputFields.addElement(input);
   }

   public final boolean submit(boolean validate, IBrowserContext context) {
      if (this._inputFields == null) {
         return true;
      }

      WMLContext wmlContext = context == null ? this._contextManager.getContext() : (!(context instanceof WMLContext) ? null : (WMLContext)context);
      if (validate) {
         for (int i = 0; i < this._inputFields.size(); i++) {
            WMLInputField field = (WMLInputField)this._inputFields.elementAt(i);
            if (!field.validate()) {
               IncompleteInputWarningRunnable warning = new IncompleteInputWarningRunnable();
               Application.getApplication().invokeAndWait(warning);
               if (warning.editPage()) {
                  if (field instanceof WMLTextInputField) {
                     synchronized (Application.getEventLock()) {
                        ((WMLTextInputField)field).setFocus();
                        return false;
                     }
                  }

                  return false;
               }
               break;
            }
         }
      }

      for (int i = 0; i < this._inputFields.size(); i++) {
         ((WMLInputField)this._inputFields.elementAt(i)).submit(wmlContext);
      }

      return true;
   }

   final void refresh() {
      Screen screen = this.getScreen();
      Application application = null;
      if (screen != null && screen.isDisplayed() && (application = screen.getApplication()) != null) {
         synchronized (application.getAppEventLock()) {
            if (this._inputFields != null) {
               int size = this._inputFields.size();

               for (int i = 0; i < size; i++) {
                  WMLInputField field = (WMLInputField)this._inputFields.elementAt(i);
                  field.refresh();
               }
            }

            if (this._textVariables != null) {
               int size = this._textVariables.size();

               for (int i = 0; i < size; i++) {
                  WMLTextField field = (WMLTextField)this._textVariables.elementAt(i);
                  field.updateValues();
               }
            }

            this.invalidate();
         }

         if (this._browserContent.getRenderingApplication() != null) {
            BrowserContentChangedEvent event = new BrowserContentChangedEvent(this);
            this._browserContent.getRenderingApplication().eventOccurred(event);
         }
      }
   }

   @Override
   public final void destroy() {
      this._browserContent.destroyTimer();
   }

   @Override
   public final void setDestroyMethod(int method) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      super.makeMenu(menu, instance);
      this._browserContent.addBrowserContentMenuItems(menu);
   }

   @Override
   protected final void onUndisplay() {
      super.onUndisplay();
      this._browserContent.stopTimer();
      this._browserContent.haltScripts();
      if (this._destroyMethod == 0) {
         this.destroy();
      }
   }

   @Override
   protected final void onDisplay() {
      super.onDisplay();
      this._browserContent.startTimer();
      this.refresh();
   }

   WMLBrowserField(WMLBrowserContent browserContent, WMLContextManager contextManager, String url, WMLRenderer renderer) {
      super(1154328879490400256L);
      if (browserContent == null) {
         throw new RuntimeException("Browser Content cannot be null");
      }

      this._contextManager = contextManager;
      this._browserContent = browserContent;
   }
}
