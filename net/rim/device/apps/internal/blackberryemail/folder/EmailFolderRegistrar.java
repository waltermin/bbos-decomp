package net.rim.device.apps.internal.blackberryemail.folder;

import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.messaging.messagelist.ShowMessageApp;

public class EmailFolderRegistrar implements GlobalEventListener {
   private boolean _ignoreRemove;

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if ((guid != 8288627527798139133L || object1 != null || !(object0 instanceof Object) || ((ServiceRecord)object0).getType() != 2)
         && guid != 2522898683889177438L) {
         if (guid != -4220058463650496006L && guid != 8288627527798139133L) {
            if (guid == 1348796660760556312L) {
               this._ignoreRemove = true;
               return;
            }

            if (guid == -583230596614878690L) {
               this._ignoreRemove = false;
               EmailHierarchy.purge();
               return;
            }

            if (guid == 4609271590317602928L) {
               RibbonUpdater.handleMessageListOptionsChanged();
               return;
            }

            if (guid == 2573494863350550132L) {
               RibbonUpdater.handleThemeChanged();
            }
         } else {
            ServiceRecord sr = ServiceBook.getSB().getRecordById(data0);
            if (sr != null) {
               if (StringUtilities.strEqualIgnoreCase(sr.getCid(), "CMIME", 1701707776)) {
                  EmailHierarchy emailHierarchy = EmailHierarchy.establishEmailHierarchy(sr.getUserId(), sr.getUidHash(), sr.getNameHash(), sr.getName());
                  if (guid == 8288627527798139133L) {
                     ShowMessageApp.postEvent(-8639396151207124460L, 0, 0, new Object(emailHierarchy.getLUID()), null);
                  }

                  RibbonUpdater.handleServiceRecordAdded(sr);
                  return;
               }

               if (StringUtilities.strEqualIgnoreCase(sr.getCid(), "MMS", 1701707776)) {
                  ShowMessageApp.postEvent(-8639396151207124460L, 0, 0, new Object(-4696470826620059293L), null);
                  return;
               }
            }
         }
      } else {
         ServiceRecord sr = (ServiceRecord)object0;
         if (sr != null) {
            if (StringUtilities.strEqualIgnoreCase(sr.getCid(), "CMIME", 1701707776)) {
               EmailHierarchy emailHierarchy = EmailHierarchy.getEmailHierarchy(sr, false);
               if (emailHierarchy != null) {
                  ShowMessageApp.postEvent(-3632668756051372542L, 0, 0, new Object(emailHierarchy.getLUID()), null);
               }

               RibbonUpdater.handleServiceRecordRemoved((ServiceRecord)object0);
               if (!this._ignoreRemove) {
                  EmailHierarchy.purge();
                  return;
               }
            } else if (StringUtilities.strEqualIgnoreCase(sr.getCid(), "MMS", 1701707776)) {
               ShowMessageApp.postEvent(-8639396151207124460L, 0, 0, new Object(-4696470826620059293L), null);
               return;
            }
         }
      }
   }
}
