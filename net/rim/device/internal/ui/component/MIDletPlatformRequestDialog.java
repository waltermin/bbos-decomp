package net.rim.device.internal.ui.component;

import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.proxy.Proxy;

public class MIDletPlatformRequestDialog extends PopupDialog implements GlobalEventListener, FieldChangeListener {
   private ButtonField _yes;
   private ButtonField _no;
   private boolean _isClosed;

   public boolean requestGranted() {
      BackgroundDialog.showOnProxy(this);
      return this.getCloseReason() != -1;
   }

   @Override
   public void fieldChanged(Field field, int context) {
      if (field == this._yes) {
         this.yes();
      } else {
         if (field == this._no) {
            this.no();
         }
      }
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -7131874474196788121L) {
         Proxy.getInstance().invokeLater(new MIDletPlatformRequestDialog$1(this));
      }
   }

   private synchronized boolean no() {
      if (!this._isClosed) {
         this._isClosed = true;
         this.close(-1);
      }

      return true;
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      if (key == 27) {
         return this._no != null ? this.no() : true;
      }

      if (key == '\n') {
         Field field = this.getLeafFieldWithFocus();
         if (field == this._yes) {
            return this.yes();
         }

         if (field == this._no) {
            return this.no();
         }
      }

      return super.keyChar(key, status, time);
   }

   @Override
   public void onUiEngineAttached(boolean attached) {
      if (attached) {
         super.onUiEngineAttached(attached);
         this._no.setFocus();
         Proxy.getInstance().addGlobalEventListener(this);
      } else {
         Proxy.getInstance().removeGlobalEventListener(this);
         super.onUiEngineAttached(attached);
      }
   }

   public MIDletPlatformRequestDialog(String message) {
      super(new DialogFieldManager(1153220571769602048L), 134217728);
      DialogFieldManager dfm = (DialogFieldManager)this.getDelegate();
      ImageField imgField = new ImageField();
      imgField.setImage(ThemeManager.getThemeAwareImage("dialog_question"));
      dfm.setIcon(imgField);
      if (message != null) {
         dfm.setMessage(new RichTextField(message));
      }

      this._yes = new ButtonField(CommonResource.getStringArray(10012)[0]);
      this._yes.setChangeListener(this);
      dfm.addButtonField(this._yes);
      this._no = new ButtonField(CommonResource.getStringArray(10012)[1]);
      this._no.setChangeListener(this);
      dfm.addButtonField(this._no);
      this.setCancelAllowed(true);
      this.setStatusPriority(-1073741824);
   }

   private synchronized boolean yes() {
      if (!this._isClosed) {
         this._isClosed = true;
         this.close(0);
      }

      return true;
   }
}
