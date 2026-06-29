package net.rim.device.apps.internal.messaging.search;

import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.FolderProvider;
import net.rim.device.apps.api.framework.registration.VerbFactory;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.ui.FolderList;
import net.rim.device.apps.api.messaging.ui.SelectFolderVerb;
import net.rim.device.apps.api.transmission.TransmissionService;
import net.rim.device.apps.api.transmission.TransmissionServiceManager;
import net.rim.device.apps.api.transmission.rim.RIMMessagingService;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.blackberryemail.folder.EmailFolder;
import net.rim.device.apps.internal.blackberryemail.folder.EmailHierarchy;
import net.rim.device.apps.internal.blackberryemail.folder.FolderUtil;
import net.rim.device.apps.internal.messaging.search.criteria.FolderSelectionField;

final class ViewFolder extends Verb implements VerbFactory {
   private Object _context;
   private static String CMIME = "CMIME";

   ViewFolder() {
      super(16861493, CommonResources.getResourceBundle(), 1351);
   }

   @Override
   public final Object invoke(Object subject) {
      Object selected = ContextObject.get(this._context, 250);
      FolderProvider fp = null;
      if (!(selected instanceof Object)) {
         selected = ContextObject.get(this._context, -2364888284414893834L);
         if (selected instanceof Object[]) {
            for (Object message : (Object[])selected) {
               if (message instanceof Object) {
                  Folder f = (Folder)message;
                  if (this.inDatabase(f)) {
                     break;
                  }
               }
            }
         }
      } else {
         fp = (FolderProvider)selected;
      }

      FolderList fl = null;
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      SelectFolderVerb selectVerb = (SelectFolderVerb)ar.get(3746287353255662597L);
      if (fp != null) {
         Folder f = FolderHierarchies.getFolder(fp.getFolderId());
         if (f != null && this.inDatabase(f)) {
            fl = FolderSelectionField.getSelectionList(f, selectVerb);
         }
      }

      if (fl == null) {
         Folder f = null;
         ServiceRecord serviceRecord = null;
         TransmissionService transmissionService = TransmissionServiceManager.get(8399767144006445082L);
         if (transmissionService instanceof Object) {
            serviceRecord = ((RIMMessagingService)transmissionService).getOutgoingServiceRecord();
            if (serviceRecord != null) {
               f = FolderUtil.getHierarchyByService(serviceRecord);
            }
         }

         if (f == null) {
            f = FolderUtil.getActiveFolderHierarchy(CMIME);
         }

         if (f != null) {
            EmailHierarchy eh = EmailHierarchy.getEmailHierarchyForFolder(f.getLUID());
            if (eh != null) {
               Folder inbox = EmailHierarchy.getEmailFolder(eh.getInboxFolder());
               if (inbox != null) {
                  f = inbox;
               }
            }
         }

         if (f == null) {
            Folder[] folders = FolderSelectionField.getSearchableHierarchies();
            if (folders.length > 0) {
               f = folders[0];
            }
         }

         if (f != null) {
            fl = FolderSelectionField.getSelectionList(f, selectVerb);
         }
      }

      if (fl != null) {
         fl.run();
         if (selectVerb.selectionMade()) {
            UiApplication.getUiApplication().invokeLater((Runnable)selectVerb);
         }
      }

      return null;
   }

   private final boolean inDatabase(Folder f) {
      if (!(f instanceof Object)) {
         return true;
      }

      EmailFolder ef = (EmailFolder)f;
      return ef.isInFolderDatabase();
   }

   @Override
   public final Verb[] getVerbs(Object context) {
      this._context = context;
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      return new Object[]{ar.get(7054367301267426154L)};
   }
}
