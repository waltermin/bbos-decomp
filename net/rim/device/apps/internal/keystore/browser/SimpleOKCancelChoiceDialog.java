package net.rim.device.apps.internal.keystore.browser;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.component.PopupDialog;

public class SimpleOKCancelChoiceDialog extends PopupDialog implements FieldChangeListener {
   private DialogFieldManager _dfm;
   private int _selectedIndex;
   private ObjectChoiceField _choiceField;
   private ButtonField _okButton;
   private ButtonField _cancelButton;

   protected void select() {
      this._selectedIndex = this._choiceField.getSelectedIndex();
      this.close(0);
   }

   public int getSelectedIndex() {
      return this._selectedIndex;
   }

   @Override
   public void fieldChanged(Field field, int context) {
      if (field == this._okButton) {
         this.select();
      } else {
         if (field == this._cancelButton) {
            this.close(-1);
         }
      }
   }

   public SimpleOKCancelChoiceDialog(RichTextField message, String choiceLabel, Object[] choices, int defaultChoice, Bitmap bitmap, long style) {
      super(new DialogFieldManager(281474976710656L), style);
      if (choices == null) {
         throw new IllegalArgumentException();
      }

      this._dfm = (DialogFieldManager)this.getDelegate();
      this._dfm.setMessage(message);
      this._choiceField = new ObjectChoiceField(choiceLabel, choices, defaultChoice);
      this._dfm.addCustomField(this._choiceField);
      if (bitmap != null) {
         this._dfm.setIcon(new BitmapField(bitmap, 32));
      }

      this._okButton = new ButtonField(CommonResource.getString(100));
      this._okButton.setChangeListener(this);
      this._cancelButton = new ButtonField(CommonResource.getString(10005));
      this._cancelButton.setChangeListener(this);
      HorizontalFieldManager buttonManager = new HorizontalFieldManager(12884901888L);
      buttonManager.add(this._okButton);
      buttonManager.add(this._cancelButton);
      this._dfm.addCustomField(buttonManager);
      this.setCancelAllowed(true);
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      boolean handled = super.keyChar(key, status, time);
      if (!handled) {
         if (key == '\n') {
            this.select();
            handled = true;
         } else if (key == 27 && this.isCancelAllowed()) {
            this.close(-1);
            handled = true;
         }
      }

      return handled;
   }

   @Override
   public boolean onMenu(int instance) {
      Field field = this.getLeafFieldWithFocus();
      return field instanceof ObjectChoiceField ? super.onMenu(instance) : false;
   }

   public SimpleOKCancelChoiceDialog(String message, String choiceLabel, Object[] choices, int defaultChoice, Bitmap bitmap, long style) {
      this(new RichTextField(message, 67108864), choiceLabel, choices, defaultChoice, bitmap, style);
   }

   @Override
   protected void onUiEngineAttached(boolean attached) {
      if (attached) {
         this._choiceField.setFocus();
      }

      super.onUiEngineAttached(attached);
   }
}
