package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailMoreVerb;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;

public class ViewWLANProfileMessageAttachmentVerb extends Verb {
   private WLANProfileMessageAttachmentModel _model;

   public ViewWLANProfileMessageAttachmentVerb(WLANProfileMessageAttachmentModel model) {
      super(603904, EmailResources.getResourceBundle(), 114);
      this._model = model;
   }

   @Override
   public Object invoke(Object parameter) {
      byte[] profileData = this.getProfileData(parameter);
      if (profileData == null) {
         String msg = EmailResources.getString(115);
         Dialog.alert(msg);
         return null;
      } else {
         this.invokeDisplaySummaryScreenVerb(profileData);
         return null;
      }
   }

   private void invokeDisplaySummaryScreenVerb(byte[] profileData) {
      if (profileData != null && profileData.length > 0) {
         ContextObject contextObject = new ContextObject();
         ContextObject.put(contextObject, 8849067667159082262L, profileData);
         Verb displaySummaryScreenVerb = this.getProfileSummaryVerb();
         if (displaySummaryScreenVerb != null) {
            displaySummaryScreenVerb.invoke(contextObject);
         }
      }
   }

   private Verb getProfileSummaryVerb() {
      VerbRepository repository = VerbRepository.getVerbRepository(-792109729318252673L);
      Verb[] verbs = repository.getVerbs(3533541249439435308L);
      return verbs[0];
   }

   private EmailMessageModel getParent(Object context) {
      ContextObject contextObject = ContextObject.castOrCreate(context);
      return (EmailMessageModel)contextObject.get(246);
   }

   private byte[] getProfileData(Object context) {
      if (this._model.getLengthOnDevice() < this._model.getAvailableLength()) {
         this.retrieveMoreDataFromServer(context);
      }

      return this._model.getData();
   }

   private void retrieveMoreDataFromServer(Object context) {
      EmailMessageModel parent = this.getParent(context);
      ContextObject moreContext = ContextObject.clone(context);
      moreContext.put(254, this._model);
      EmailMoreVerb emailMoreVerb = new EmailMoreVerb(parent, (byte)2);
      emailMoreVerb.invoke(moreContext);
   }
}
