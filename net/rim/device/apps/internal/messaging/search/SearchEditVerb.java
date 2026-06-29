package net.rim.device.apps.internal.messaging.search;

import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.registration.RecognizerRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.messagelist.ShowMessageApp;
import net.rim.device.apps.api.messaging.search.MessageSearch;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.utility.framework.SubmemberUtilities;
import net.rim.device.apps.internal.blackberryemail.folder.EmailHierarchy;
import net.rim.device.apps.internal.messaging.search.criteria.ServiceSearchModel;
import net.rim.device.apps.internal.messaging.search.criteria.ServiceSearchModelFactory;
import net.rim.device.apps.internal.messaging.search.criteria.TypeSearchModel;
import net.rim.device.apps.internal.messaging.search.criteria.TypeSearchModelFactory;

final class SearchEditVerb extends Verb {
   SearchEditVerb() {
      super(16861440, CommonResources.getResourceBundle(), 9136);
   }

   @Override
   public final Object invoke(Object context) {
      if (ContextObject.getFlag(context, 22)) {
         Object searchToEdit = ((SearchCollection)MessageSearch.getInstance().getCollection()).getLastFilter();
         if (!ContextObject.getFlag(context, 3)) {
            ShowMessageApp.showMessageApp(-1588283800, searchToEdit);
            return null;
         } else {
            ShowMessageApp.showMessageApp(2076204577, searchToEdit);
            return null;
         }
      } else {
         Object searchToEdit = SearchEditScreen.getDefaultSearch();
         Object selected = ContextObject.get(context, 250);
         ReadableList rl = (ReadableList)searchToEdit;
         TypeSearchModelFactory tsmf = TypeSearchModelFactory.getInstance();
         Object o = SubmemberUtilities.getFirstSubmember(rl, tsmf);
         if (o == null) {
            o = tsmf.createInstance(null);
            ((WritableSet)rl).add(o);
         }

         if (o instanceof TypeSearchModel) {
            TypeSearchModel tsm = (TypeSearchModel)o;
            long[] objectTypes = TypeSearchModel.getPossibleTypes();

            for (int i = 0; i < objectTypes.length; i++) {
               long type = objectTypes[i];
               Recognizer r = RecognizerRepository.getRecognizers(type);
               if (r != null && r.recognize(selected)) {
                  tsm.setIndex(type);
               }
            }
         }

         Long emailHierarchyLuid = (Long)ContextObject.get(context, -953487338188658393L);
         if (emailHierarchyLuid != null) {
            EmailHierarchy hierarchy = EmailHierarchy.getEmailHierarchy(emailHierarchyLuid);
            if (hierarchy != null) {
               ServiceSearchModelFactory ssmf = ServiceSearchModelFactory.getInstance();
               o = SubmemberUtilities.getFirstSubmember(rl, ssmf);
               if (o == null) {
                  o = ssmf.createInstance(null);
                  ((WritableSet)rl).add(o);
               }

               if (o instanceof ServiceSearchModel) {
                  ((ServiceSearchModel)o).setService(hierarchy.getServiceUserId(), hierarchy.getServiceUidHash());
               }
            }
         }

         ShowMessageApp.showMessageApp(2076204577, searchToEdit);
         return null;
      }
   }
}
