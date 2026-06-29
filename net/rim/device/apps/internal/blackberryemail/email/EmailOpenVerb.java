package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.ui.Font;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.messagelist.ShowMessageApp;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;
import net.rim.device.internal.i18n.CommonResource;

public final class EmailOpenVerb extends Verb {
   protected EmailMessageModelImpl _message;

   EmailOpenVerb(EmailMessageModelImpl message) {
      super(590080, CommonResource.getBundle(), 15);
      this._message = message;
   }

   @Override
   public final Object invoke(Object context) {
      ContextObject contextObject = ContextObject.clone(context);
      byte encoding = this._message.getEncoding();
      if (encoding != -1) {
         Font font = CMIMEUtilities.getSuggestedFontForEncoding(encoding);
         if (font != null) {
            ContextObject.put(contextObject, 77, font);
         }
      }

      if (this._message.getStatus() == Integer.MAX_VALUE) {
         EmailPayloadModel payload = (EmailPayloadModel)this._message.getPayload();
         if (ObjectGroup.isInGroup(payload)) {
            this._message.setPayload((EmailPayloadModel)ObjectGroup.expandGroup(payload));
         }

         contextObject.setFlag(0);
         EmailEditorScreen editor = new EmailEditorScreen(contextObject);
         editor.setModelAndSaveCopy(this._message);
         EventLogger.logEvent(-1237457833540244999L, 1162694480, 5);
         editor.setFocusToEditableBody(true);
         editor.go();
      } else {
         contextObject.setFlag(37);
         EmailViewerScreen viewer = new EmailViewerScreen(contextObject);
         viewer.setModel(this._message);
         if (this._message.inbound() && !contextObject.getFlag(64)) {
            viewer.markMessageOpened();
         }

         EventLogger.logEvent(-1237457833540244999L, 1162694480, 5);
         viewer.go();
      }

      return this.finalizeInvoke(contextObject);
   }

   protected final ContextObject finalizeInvoke(ContextObject contextObject) {
      if (contextObject.getFlag(64)) {
         ShowMessageApp.showMessageApp(-246332839, null);
         return new ContextObject(39);
      } else {
         return null;
      }
   }
}
