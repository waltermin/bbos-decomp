package net.rim.device.internal.ui.component;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.internal.i18n.CommonResource;

public class SimpleOKCancelInputDialog extends SimpleInputDialog implements FieldChangeListener {
   private ButtonField _okButton = new ButtonField(CommonResource.getString(100), 65536);
   private ButtonField _cancelButton;
   private HorizontalFieldManager _buttonManager;

   public SimpleOKCancelInputDialog(int type, String prompt) {
      this(type, prompt, 0, 1000000, 0);
   }

   public SimpleOKCancelInputDialog(int type, String prompt, int minLength, int maxLength, long style) {
      super(type, prompt, minLength, maxLength, style);
      this._okButton.setChangeListener(this);
      this._cancelButton = new ButtonField(CommonResource.getString(10005), 65536);
      this._cancelButton.setChangeListener(this);
      this._buttonManager = new HorizontalFieldManager(12884901888L);
      this._buttonManager.add(this._okButton);
      this._buttonManager.add(this._cancelButton);
      DialogFieldManager dfm = (DialogFieldManager)this.getDelegate();
      dfm.addCustomField(this._buttonManager);
      this.setCancelAllowed(true);
   }

   protected Manager getButtonFieldManager() {
      return this._buttonManager;
   }

   @Override
   public void fieldChanged(Field field, int context) {
      if (field == this._okButton) {
         this.accept();
      } else {
         if (field == this._cancelButton) {
            this.cancel();
         }
      }
   }

   @Override
   public boolean navigationClick(int status, int time) {
      Field field = this.getDelegate().getLeafFieldWithFocus();
      return field == this._cancelButton ? this.cancel() : super.navigationClick(status, time);
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      if (key == '\n') {
         Field field = this.getDelegate().getLeafFieldWithFocus();
         if (field == this._cancelButton) {
            return this.cancel();
         }
      }

      return super.keyChar(key, status, time);
   }

   @Override
   public void setType(int type) {
      super.setType(type);
      if (this._buttonManager != null) {
         this.delete(this._buttonManager);
         this.add(this._buttonManager);
      }
   }
}
