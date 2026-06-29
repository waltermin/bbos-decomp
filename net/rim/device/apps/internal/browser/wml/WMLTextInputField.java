package net.rim.device.apps.internal.browser.wml;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.browser.ui.InputFormatEditField;
import net.rim.device.apps.internal.browser.ui.TextInputField;

final class WMLTextInputField extends TextInputField implements WMLInputField {
   private Verb _autoExecVerb;
   private WMLContextManager _wmlContextManager;
   private WMLVariable _initialValue;
   private String _name;
   private boolean _emptyOk;
   private WMLBrowserField _browserField;
   private int _index;

   WMLTextInputField(
      WMLBrowserField browserField,
      int index,
      int size,
      WMLContextManager wmlContextManager,
      WMLVariable title,
      String name,
      WMLVariable initialValue,
      BasicEditField edit,
      boolean emptyOk,
      boolean wrap,
      long style
   ) {
      super(edit, size, wrap, style);
      this._browserField = browserField;
      this._index = index;
      this._initialValue = initialValue;
      this._wmlContextManager = wmlContextManager;
      this._name = name;
      this._emptyOk = emptyOk;
   }

   @Override
   public final void setAutoExecVerb(Verb verb) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public final void submit(WMLContext wmlContext) {
      if (wmlContext != null) {
         wmlContext.put(this._name, super._edit.getText());
      }
   }

   @Override
   public final boolean isModified() {
      return super._edit.isDirty();
   }

   @Override
   public final boolean validate() {
      if (!this._emptyOk && super._edit.getText().length() == 0) {
         return false;
      }

      if (super._edit instanceof InputFormatEditField) {
         ((InputFormatEditField)super._edit).validate(super._edit.getText(), false);
      }

      return true;
   }

   @Override
   public final void refresh() {
      String initialValue = null;
      if (this._initialValue != null) {
         initialValue = this._initialValue.getName();
      }

      String realInitValue = this._wmlContextManager.get(this._name);
      if (realInitValue == null || realInitValue.length() == 0) {
         if (initialValue == null || initialValue.length() == 0) {
            if (super._edit instanceof InputFormatEditField) {
               ((InputFormatEditField)super._edit).initialize();
               this._wmlContextManager.put(this._name, super._edit.getText());
            }

            return;
         }

         realInitValue = initialValue;
      }

      if (realInitValue.length() > super._edit.getMaxSize()) {
         realInitValue = realInitValue.substring(0, super._edit.getMaxSize());
      }

      boolean valid = false;
      if (!(super._edit instanceof InputFormatEditField)) {
         valid = true;
      } else {
         valid = ((InputFormatEditField)super._edit).validate(realInitValue, true);
      }

      if (valid) {
         synchronized (Application.getApplication().getAppEventLock()) {
            super._edit.setText(realInitValue);
         }

         this._wmlContextManager.put(this._name, realInitValue);
      } else {
         this._wmlContextManager.put(this._name, "");
      }
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key == '\n') {
         if (this._autoExecVerb != null) {
            this._autoExecVerb.invoke(null);
            return true;
         } else {
            this._browserField.jumpToNextInputField(this._index);
            return true;
         }
      } else if (key != '\b' && key != 127) {
         if ((key < ' ' || key > '~') && key < 160) {
            return super.keyChar(key, status, time);
         }

         super.keyChar(key, status, time);
         return true;
      } else {
         super.keyChar(key, status, time);
         return true;
      }
   }
}
