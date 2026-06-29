package net.rim.device.internal.ui.security.component;

import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.ui.component.BackgroundDialog;
import net.rim.device.internal.ui.container.VerticalIndentFieldManager;

public class PermissionDialog extends VendorModuleStackDialog implements GlobalEventListener, FieldChangeListener {
   private boolean _isClosed;
   private CheckboxField _userOptionCheckbox;
   private ButtonField _allow;
   private ButtonField _deny;

   public boolean getUserOptionCheckBoxValue() {
      return this._userOptionCheckbox == null ? false : this._userOptionCheckbox.getChecked();
   }

   public boolean getPermission() {
      BackgroundDialog.showOnProxy(this);
      return this.getCloseReason() != -1;
   }

   @Override
   public void fieldChanged(Field field, int context) {
      if (field == this._allow) {
         this.allow();
      } else {
         if (field == this._deny) {
            this.deny();
         }
      }
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -7131874474196788121L) {
         Proxy.getInstance().invokeLater(new PermissionDialog$1(this));
      }
   }

   private synchronized boolean deny() {
      if (!this._isClosed) {
         this._isClosed = true;
         this.close(-1);
      }

      return true;
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      if (key == 27) {
         return this._deny != null ? this.deny() : true;
      }

      if (key == '\n') {
         Field field = this.getLeafFieldWithFocus();
         if (field == this._deny) {
            return this.deny();
         }

         if (field == this._allow) {
            return this.allow();
         }
      }

      return super.keyChar(key, status, time);
   }

   public PermissionDialog(String message, String checkBoxLabel, int[] stackHandles, int[] specialStackHandles) {
      super(new VerticalIndentFieldManager(1153220571769602048L), 134217728);
      VerticalIndentFieldManager vifm = (VerticalIndentFieldManager)this.getDelegate();
      if (message != null) {
         RichTextField label = new RichTextField(message);
         vifm.add(label);
      }

      if (checkBoxLabel != null) {
         this._userOptionCheckbox = new CheckboxField(checkBoxLabel, true, 1073741824);
         vifm.add(this._userOptionCheckbox);
      }

      HorizontalFieldManager buttonManager = new HorizontalFieldManager(12884901888L);
      this._allow = new ButtonField(CommonResource.getStringArray(10170)[0]);
      this._allow.setChangeListener(this);
      buttonManager.add(this._allow);
      this._deny = new ButtonField(CommonResource.getStringArray(10170)[1]);
      this._deny.setChangeListener(this);
      buttonManager.add(this._deny);
      vifm.add(buttonManager);
      VendorModuleStackDialog.populateVendorApplicationModulesStack(vifm, stackHandles, specialStackHandles);
      this.setCancelAllowed(true);
   }

   @Override
   public void onUiEngineAttached(boolean attached) {
      if (attached) {
         super.onUiEngineAttached(attached);
         if (this._userOptionCheckbox != null) {
            this._userOptionCheckbox.setFocus();
         } else {
            this._allow.setFocus();
         }

         Proxy.getInstance().addGlobalEventListener(this);
      } else {
         Proxy.getInstance().removeGlobalEventListener(this);
         super.onUiEngineAttached(attached);
      }
   }

   private synchronized boolean allow() {
      if (!this._isClosed) {
         this._isClosed = true;
         this.close(0);
      }

      return true;
   }

   static void access$000(PermissionDialog x0, int x1) {
      x0.close(x1);
   }
}
