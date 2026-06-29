package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.servicebook.TransportRegistry;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.messagelist.ShowMessageApp;
import net.rim.device.apps.api.transmission.rim.sendmethods.SendMethod;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.utility.editor.EditorUsingRIMModelFactory;
import net.rim.device.apps.internal.blackberryemail.properties.TransitoryMessagePropertiesModel;

final class EmailSaveDraftVerb extends Verb {
   private EditorUsingRIMModelFactory _screen;

   EmailSaveDraftVerb(EditorUsingRIMModelFactory screen) {
      super(332368, CommonResources.getResourceBundle(), 9152);
      this._screen = screen;
   }

   @Override
   public final Object invoke(Object context) {
      EmailMessageModelImpl message = (EmailMessageModelImpl)this._screen.getModel(false);
      Object o = ContextObject.get(context, 32241034113959076L);
      if (!(o instanceof TransitoryMessagePropertiesModel)) {
         throw new RuntimeException("EmailSaveDraftVerb");
      }

      TransitoryMessagePropertiesModel tmpm = (TransitoryMessagePropertiesModel)o;
      ServiceRecord sr = null;
      SendMethod sm = tmpm.getSelectedSendMethod();
      if (sm != null) {
         sr = sm.getServiceRecord();
      }

      EmailTransport et = null;
      if (sr == null) {
         TransportRegistry tr = TransportRegistry.getInstance();
         et = (EmailTransport)tr.get("CMIME");
      } else {
         et = (EmailTransport)sr.getTransport();
      }

      et.saveMessage(message, sr, context);
      if (!ContextObject.getFlag(context, 121)) {
         ShowMessageApp.showMessageApp();
      }

      return new ContextObject(39);
   }
}
