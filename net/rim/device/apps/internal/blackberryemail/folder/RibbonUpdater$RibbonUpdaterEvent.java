package net.rim.device.apps.internal.blackberryemail.folder;

import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.apps.api.ribbon.ApplicationEntryPoint;
import net.rim.device.apps.api.ribbon.RibbonLauncher;
import net.rim.device.apps.api.transmission.Parameters;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;

final class RibbonUpdater$RibbonUpdaterEvent implements Runnable {
   private int _type;
   private ServiceRecord _sr;
   private static final int ADD = 1;
   private static final int REMOVE = 2;
   private static final int REFRESH = 3;

   RibbonUpdater$RibbonUpdaterEvent(int type, ServiceRecord sr) {
      this._type = type;
      this._sr = sr;
   }

   private final void addRibbonEntryPoint() {
      RibbonLauncher ribbon = RibbonLauncher.getInstance();
      if (ribbon != null) {
         String id = ((StringBuffer)(new Object("net_rim_bb_messaging_app"))).append(this._sr.getId()).toString();
         if (ribbon.getRegisteredAction(id) != null) {
            ribbon.unregisterAction(id);
         }

         Parameters entryPointParameters = CMIMEUtilities.getEntryPointParameters(this._sr);
         if (entryPointParameters != null) {
            EmailHierarchy emailHierarchy = EmailHierarchy.getEmailHierarchy(this._sr, true);
            if (emailHierarchy != null) {
               int moduleHandle = CodeModuleManager.getModuleHandle("net_rim_bb_messaging_app");
               ApplicationDescriptor baseDescriptor = CodeModuleManager.getApplicationDescriptors(moduleHandle)[0];
               StringBuffer descriptorName = (StringBuffer)(new Object(baseDescriptor.getName()));
               long emailHierarchyLUID = emailHierarchy.getLUID();
               descriptorName.append(".service.");
               switch (this._sr.getServiceIdentifierType()) {
                  case 0:
                     break;
                  case 1:
                  default:
                     descriptorName.append("bes.");
                     break;
                  case 2:
                     descriptorName.append("bwc.");
                     break;
                  case 3:
                     descriptorName.append("bis.");
               }

               descriptorName.append(emailHierarchyLUID);
               String[] args = new Object[]{((StringBuffer)(new Object("service="))).append(emailHierarchyLUID).toString()};
               ApplicationDescriptor newDescriptor = (ApplicationDescriptor)(new Object(
                  baseDescriptor, descriptorName.toString(), args, baseDescriptor.getIcon(), baseDescriptor.getPosition(), null, 0
               ));
               ApplicationEntryPoint newEntryPoint = (ApplicationEntryPoint)(new Object(newDescriptor));
               RibbonUpdater.updateRibbonEntryPointDescription(ribbon, this._sr, emailHierarchy, newEntryPoint, null);
               ribbon.registerAction(id, newEntryPoint);
               String contentURL = CMIMEUtilities.getEntryPointContentURL(entryPointParameters);
               int iconVersion = CMIMEUtilities.getEntryPointIconVersion(entryPointParameters);
               if (contentURL != null) {
                  IconFetcher.getInstance().fetchIcons(this._sr.getUid(), id, contentURL, iconVersion);
               }
            }
         }
      }
   }

   private final void removeRibbonEntryPoint() {
      RibbonLauncher ribbon = RibbonLauncher.getInstance();
      if (ribbon != null) {
         ribbon.unregisterAction(((StringBuffer)(new Object("net_rim_bb_messaging_app"))).append(this._sr.getId()).toString());
         IconFetcher.deleteIcons(this._sr.getUid());
      }
   }

   private final void refreshRibbonEntryPoints() {
      ServiceRecord[] serviceRecords = ServiceBook.getSB().findRecordsByCid("CMIME");
      if (serviceRecords != null && serviceRecords.length > 0) {
         RibbonLauncher ribbon = RibbonLauncher.getInstance();
         if (ribbon != null) {
            for (int i = serviceRecords.length - 1; i >= 0; i--) {
               ServiceRecord sr = serviceRecords[i];
               EmailHierarchy emailHierarchy = EmailHierarchy.getEmailHierarchy(sr, false);
               if (emailHierarchy != null) {
                  RibbonUpdater.updateRibbonEntryPointDescription(ribbon, sr, emailHierarchy, null, null);
               }
            }
         }
      }
   }

   @Override
   public final void run() {
      switch (this._type) {
         case 1:
         default:
            this.addRibbonEntryPoint();
            return;
         case 2:
            this.removeRibbonEntryPoint();
            return;
         case 3:
            this.refreshRibbonEntryPoints();
         case 0:
      }
   }
}
