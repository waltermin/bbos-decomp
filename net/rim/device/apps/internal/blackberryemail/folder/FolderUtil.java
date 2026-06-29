package net.rim.device.apps.internal.blackberryemail.folder;

import java.util.Enumeration;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.internal.blackberryemail.ServiceRecordProvider;

public final class FolderUtil {
   public static final Folder getHierarchyByService(ServiceRecord sr) {
      return getHierarchyByService(sr.getCid(), sr.getUserId(), sr.getUidHash(), sr.getNameHash());
   }

   public static final Folder getHierarchyByService(String cid, int userId, int serviceUidHash, int serviceNameHash) {
      Enumeration e = FolderHierarchies.getFolderHierarchies();

      while (e.hasMoreElements()) {
         Folder f = (Folder)e.nextElement();
         if (f instanceof ServiceRecordProvider) {
            ServiceRecordProvider srp = (ServiceRecordProvider)f;
            if (StringUtilities.strEqualIgnoreCase(cid, srp.getServiceContentIdentifier(), 1701707776)) {
               if (userId != -1 && srp.getServiceUserId() != -1 && srp.getServiceUserId() == userId) {
                  return f;
               }

               if (srp.getServiceUidHash() == serviceUidHash && srp.getServiceNameHash() == serviceNameHash) {
                  return f;
               }
            }
         }
      }

      return null;
   }

   public static final boolean isServiceless(Folder f) {
      if (!(f instanceof ServiceRecordProvider)) {
         return true;
      }

      ServiceRecordProvider srp = (ServiceRecordProvider)f;
      return isServiceless(srp.getServiceUserId(), srp.getServiceUidHash(), srp.getServiceNameHash());
   }

   public static final boolean isServiceless(int serviceUserId, int serviceUIDHash, int serviceNameHash) {
      return serviceUserId == -1 && serviceUIDHash == -1 && serviceNameHash == -1;
   }

   public static final Folder getActiveFolderHierarchy(String cid) {
      Enumeration e = FolderHierarchies.getFolderHierarchies();
      Folder anonymous = null;

      while (e.hasMoreElements()) {
         Folder f = (Folder)e.nextElement();
         if (f instanceof ServiceRecordProvider) {
            ServiceRecordProvider srp = (ServiceRecordProvider)f;
            if (StringUtilities.strEqualIgnoreCase(cid, srp.getServiceContentIdentifier(), 1701707776)) {
               if (!isServiceless(f)) {
                  return f;
               }

               anonymous = f;
            }
         }
      }

      return anonymous;
   }
}
