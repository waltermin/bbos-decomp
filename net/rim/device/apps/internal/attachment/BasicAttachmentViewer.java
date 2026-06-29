package net.rim.device.apps.internal.attachment;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.ActiveRichTextField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.registration.ModelViewListenerRegistry;
import net.rim.device.apps.api.ui.ExitVerb;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.api.utility.framework.ModelScreen;
import net.rim.device.apps.api.utility.framework.ModelScreen$NotificationRunnable;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.email.MorePartModel;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailMoreVerb;

public class BasicAttachmentViewer extends ModelScreen {
   protected EmailMessageModel _parentMessage;

   public BasicAttachmentViewer(long style, String title, Object context) {
      super(style, title, context);
      this.setLeaveScreenVerb(new ExitVerb(0, null));
      this._parentMessage = (EmailMessageModel)ContextObject.get(context, 246);
   }

   @Override
   public void setModel(Object model) {
      this.deleteAll();
      this.displayModel(model);
      super.setModel(model);
   }

   protected void displayModel(Object model) {
      AttachmentViewerModel attachmentViewerModel = (AttachmentViewerModel)model;
      Field field = new ActiveRichTextField(new String(attachmentViewerModel.getData()));
      field.setCookie(attachmentViewerModel);
      this.add(field);
      field.setFocus();
   }

   @Override
   protected void makeMenu(SystemEnabledMenu menu, int instance) {
      super.makeMenu(menu, instance);
      AttachmentViewerModel attachmentViewerModel = (AttachmentViewerModel)super._model;
      if (attachmentViewerModel.isMoreAvailable() && EmailMoreVerb.isMoreAllAllowed(this._parentMessage)) {
         int preferredConversion = attachmentViewerModel.getPreferredConversion();
         if (preferredConversion >= 0) {
            menu.add(new EmailMoreVerb(attachmentViewerModel, (byte)1, attachmentViewerModel._conversionsAvailable[preferredConversion]));
            menu.add(new EmailMoreVerb(attachmentViewerModel, (byte)2, attachmentViewerModel._conversionsAvailable[preferredConversion]));
         }
      }
   }

   @Override
   public void notifyOfOpenedModelChange(RIMModel oldModel, RIMModel newModel, Object moreContext) {
      if (this._parentMessage == oldModel && newModel instanceof EmailMessageModel) {
         if (super._application != Application.getApplication() || !Application.isEventDispatchThread()) {
            super._application.invokeLater(new ModelScreen$NotificationRunnable(this, oldModel, newModel, moreContext));
            return;
         }

         Field fieldWithFocus = this.getLeafFieldWithFocus();
         int oldOffset = 0;
         if (!(fieldWithFocus instanceof EditField)) {
            if (fieldWithFocus instanceof RichTextField) {
               oldOffset = ((RichTextField)fieldWithFocus).getCursorPosition();
            }
         } else {
            oldOffset = ((EditField)fieldWithFocus).getCursorPosition();
         }

         this._parentMessage = (EmailMessageModel)newModel;
         MorePartModel morePartModel = (MorePartModel)super._model;
         int morePartIdentifier = morePartModel.getMorePartID();
         morePartModel = EmailMoreVerb.findMorePartByIdentifier(this._parentMessage, morePartIdentifier);
         this.setModel(morePartModel);
         Field newFieldWithFocus = this.getLeafFieldWithFocus();
         if (newFieldWithFocus instanceof EditField) {
            ((EditField)newFieldWithFocus).setCursorPosition(oldOffset);
            return;
         }

         if (newFieldWithFocus instanceof RichTextField) {
            ((RichTextField)newFieldWithFocus).setCursorPosition(oldOffset);
         }
      }
   }

   @Override
   protected void onUiEngineAttached(boolean attached) {
      if (attached) {
         ModelViewListenerRegistry.notifyModelOpened(this, super._context);
      } else {
         ModelViewListenerRegistry.notifyModelClosed(this, super._context);
         this._parentMessage = null;
      }

      super.onUiEngineAttached(attached);
   }
}
