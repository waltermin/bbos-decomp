package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.api.collection.ReadableList;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.search.MessageSearch;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailMessageUtilities;
import net.rim.device.apps.internal.blackberryemail.header.SubjectModel;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;

public final class SearchSubjectVerb extends Verb {
   private RIMModel _model;

   SearchSubjectVerb(RIMModel model) {
      super(16861488);
      this._model = model;
   }

   @Override
   public final Object invoke(Object context) {
      ReadableList message = (ReadableList)this._model;
      String subject = "";

      for (int i = message.size() - 1; i >= 0; i--) {
         Object element = message.getAt(i);
         if (element instanceof SubjectModel) {
            subject = ((SubjectModel)element).toString();
            break;
         }
      }

      MessageSearch search = MessageSearch.getInstance();
      if (search != null) {
         search.subjectSearch(subject.substring(EmailMessageUtilities.getOriginalSubjectIndex(subject)), false, context);
      }

      return null;
   }

   @Override
   public final String toString() {
      return EmailResources.getString(22);
   }
}
