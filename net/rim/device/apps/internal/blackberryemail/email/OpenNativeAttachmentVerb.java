package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.api.io.MIMETypeAssociations;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;

public class OpenNativeAttachmentVerb extends Verb {
   private AbstractEmailFileAttachment _attachment;

   OpenNativeAttachmentVerb(AbstractEmailFileAttachment attachment) {
      super(603904);
      this._attachment = attachment;
   }

   @Override
   public Object invoke(Object parameter) {
      AppsMainScreen viewScreen = new AppsMainScreen(281474976710656L);
      NativeAttachmentViewer attachmentViewerApp = new NativeAttachmentViewer(viewScreen, this._attachment);
      attachmentViewerApp.init();
      UiApplication.getUiApplication().pushScreen(viewScreen);
      return null;
   }

   @Override
   public String toString() {
      switch (MIMETypeAssociations.getMediaTypeFromMIMEType(this._attachment.getContentType())) {
         case 1:
            return EmailResources.getString(151);
         case 2:
         case 3:
         case 7:
            return EmailResources.getString(112);
         default:
            return EmailResources.getString(150);
      }
   }
}
