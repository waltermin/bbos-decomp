package net.rim.wica.runtime.ui.internal.component;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.text.NumericTextFilter;
import net.rim.device.apps.api.ui.CommonResources;

public final class PagingController$WicletPopupDialog extends Dialog {
   private PagingController$CustomBasicEditField _editString;
   private ButtonField _buttonOk;
   private ButtonField _buttonCancel;
   private final PagingController this$0;

   public PagingController$WicletPopupDialog(PagingController this$0, String message, String buttonLabel, String format) {
      super(message, null, null, 0, null);
      this.this$0 = this$0;
      this.setEscapeEnabled(false);
      this._editString = new PagingController$CustomBasicEditField();
      this._editString.setFilter(new NumericTextFilter());
      this.add(this._editString);
      this._buttonOk = new ButtonField(buttonLabel);
      this._buttonOk.setChangeListener(this);
      this._buttonCancel = new ButtonField(CommonResources.getString(9042));
      this._buttonCancel.setChangeListener(this);
      HorizontalFieldManager hfm = new HorizontalFieldManager(12884901888L);
      hfm.add(this._buttonOk);
      hfm.add(this._buttonCancel);
      this.add(hfm);
      this._editString.setFocus();
   }

   public final String getString() {
      return this._editString.getText().trim();
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this._buttonCancel) {
         this.cancel();
      } else {
         if (field == this._buttonOk) {
            if (this.this$0.jumpToPage()) {
               this.close();
            }

            this._editString.clear(0);
            this._editString.setFocus();
         }
      }
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      boolean handled = super.keyChar(key, status, time);
      if (!handled) {
         if (key == 27) {
            this.cancel();
         }

         if (key == '\n') {
            if (this.this$0.jumpToPage()) {
               this.close();
            }

            this._editString.clear(0);
            this._editString.setFocus();
         }

         return true;
      } else {
         return handled;
      }
   }
}
