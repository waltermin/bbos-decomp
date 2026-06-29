package net.rim.device.apps.internal.sms;

import net.rim.device.api.lowmemory.LowMemoryManager;
import net.rim.device.api.notification.NotificationsManager;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.sms.resources.SMSResources;

public class SMSNotificationDialog extends PopupScreen {
   private long _notificationID;
   private ContextObject _notificationContext;
   private RichTextField _rtf;
   private RichTextField _titleField;
   private ButtonField _okButton;
   private ButtonField _saveButton;
   private SMSModel _model;
   private long _folderId;
   private DialogFieldManager _dfm;

   public SMSNotificationDialog(SMSModel model, long folderId, String text, ContextObject notificationContext, long notificationID) {
      super(new VerticalFieldManager());
      this._model = model;
      this._folderId = folderId;
      String title = this.makeTitle(model, notificationContext);
      this._titleField = new RichTextField(title, 1188950301626073088L);
      this.add(this._titleField);
      this.add(new SeparatorField());
      this._dfm = new DialogFieldManager();
      this._rtf = new RichTextField(text, 36028797018963968L);
      this._dfm.setMessage(this._rtf);
      Bitmap bitmap = Bitmap.getPredefinedBitmap(0);
      this._dfm.setIcon(new BitmapField(bitmap, 65568));
      this._okButton = new ButtonField(CommonResources.getString(117), 12884901888L);
      this._dfm.addCustomField(this._okButton);
      this.add(this._dfm);
      if (model != null && (RadioInfo.getSupportedWAFs() & 3) == 1) {
         this._saveButton = new ButtonField(SMSResources.getString(18), 12884901888L);
         this._dfm.addCustomField(this._saveButton);
      }

      this._notificationContext = notificationContext;
      this._notificationID = notificationID;
   }

   public void resetDialog(SMSModel model, long folderId, String text, ContextObject notificationContext, long notificationID) {
      this._model = model;
      this._folderId = folderId;
      String title = this.makeTitle(model, notificationContext);
      this._titleField.setText(title);
      this._rtf.setText(text);
      this._notificationContext = notificationContext;
      this._notificationID = notificationID;
   }

   private String makeTitle(SMSModel model, ContextObject notificationContext) {
      String title = SMSResources.getString(751);
      if (model != null) {
         RIMModel peerAddress = model._payload.getFirstAddress();
         if (peerAddress != null) {
            String displayString = this.getDisplayString(peerAddress, notificationContext);
            if (displayString != null && displayString.length() > 0) {
               StringBuffer sb = new StringBuffer(title);
               sb.append(' ');
               sb.append('(');
               sb.append(displayString);
               sb.append(')');
               title = sb.toString();
            }
         }
      }

      return title;
   }

   public DialogFieldManager getDialogFieldManager() {
      return this._dfm;
   }

   public void setText(String text) {
      this._rtf.setText(text);
   }

   public void show() {
      synchronized (Application.getApplication().getAppEventLock()) {
         Ui.getUiEngine().pushGlobalScreen(this, -2147483645, 2);
      }
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      switch (key) {
         case '\n':
         case ' ':
            return this.onWheelClick();
         case '\u001b':
            this.close();
            return true;
         default:
            return super.keyChar(key, status, time);
      }
   }

   @Override
   protected boolean invokeAction(int action) {
      return action == 1 ? this.onWheelClick() : super.invokeAction(action);
   }

   @Override
   protected boolean navigationClick(int status, int time) {
      return this.onWheelClick();
   }

   private boolean onWheelClick() {
      if (this.getLeafFieldWithFocus() == this._saveButton) {
         this.saveMessage(true);
      }

      this.close();
      return true;
   }

   public void saveMessage(boolean markAsOpened) {
      if (this._model != null && this._saveButton != null) {
         if (markAsOpened) {
            this._model.changeStatus(1, 0, 0, 0, true, true, true, null);
         } else {
            this._model.changeStatus(0, 1, 0, 0, true, true, true, null);
         }

         Storage.fileMessage(this._model, this._folderId);
         this._model = null;
         LowMemoryManager.poll();
      }
   }

   @Override
   public void close() {
      Ui.getUiEngine().popScreen(this);
      if (this._notificationContext != null) {
         long type = 7986617465467730856L;
         NotificationsManager.cancelImmediateEvent(type, this._notificationID, null, this._notificationContext);
      }
   }

   private String getDisplayString(RIMModel peerAddress, ContextObject notificationContext) {
      RIMModel actualModel = (RIMModel)AddressBookServices.reverseLookup(peerAddress);
      return actualModel != null ? actualModel.toString() : peerAddress.toString();
   }
}
