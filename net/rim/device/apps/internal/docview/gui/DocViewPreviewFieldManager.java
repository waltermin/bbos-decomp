package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntHashtable;

final class DocViewPreviewFieldManager {
   private static final long EMAILPREVIEWFIELD_OBJECT = 2289128795965653843L;
   private static final int MAX_ENTRIESCOUNT = 4;
   private static DocViewPreviewFieldManager _instance;

   public static final DocViewPreviewFieldManager getInstance() {
      if (_instance == null) {
         _instance = new DocViewPreviewFieldManager();
      }

      return _instance;
   }

   private DocViewPreviewFieldManager() {
   }

   public final synchronized Object getObject(int messageID, int key) {
      if (ApplicationRegistry.getApplicationRegistry().get(2289128795965653843L) != null) {
         IntHashtable mainHash = ApplicationRegistry.getApplicationRegistry().getIntHashtable(2289128795965653843L);
         if (mainHash.containsKey(messageID)) {
            IntHashtable secondaryHash = (IntHashtable)mainHash.get(messageID);
            if (secondaryHash.containsKey(key)) {
               DocViewPreviewFieldManager$CachedPreviewInfo previewInfo = (DocViewPreviewFieldManager$CachedPreviewInfo)secondaryHash.get(key);
               int procID = Application.getApplication().getProcessId();
               if (previewInfo._procID == procID) {
                  return previewInfo._obj;
               }
            }
         }
      }

      return null;
   }

   public final synchronized void putObject(int messageID, int key, Object value) {
      if (value != null && getFwdScreenCount(messageID) == 1) {
         IntHashtable mainHash = ApplicationRegistry.getApplicationRegistry().getIntHashtable(2289128795965653843L);
         IntHashtable hash = null;
         if (mainHash.containsKey(messageID)) {
            hash = (IntHashtable)mainHash.get(messageID);
         } else {
            if (mainHash.size() == 4) {
               IntEnumeration keys = mainHash.keys();
               mainHash.remove(keys.nextElement());
            }

            hash = (IntHashtable)(new Object());
            mainHash.put(messageID, hash);
         }

         int procID = Application.getApplication().getProcessId();
         hash.put(key, new DocViewPreviewFieldManager$CachedPreviewInfo(procID, value));
      }
   }

   public final synchronized void removeCachedObjects(int messageID) {
      if (ApplicationRegistry.getApplicationRegistry().get(2289128795965653843L) != null) {
         IntHashtable mainHash = ApplicationRegistry.getApplicationRegistry().getIntHashtable(2289128795965653843L);
         mainHash.remove(messageID);
      }
   }

   private static final int getFwdScreenCount(int messageID) {
      int count = 0;
      ForwardScreen[] fwdScreenInstances = DocViewDisplayScreenInstance.getForwardScreenInstances();
      if (fwdScreenInstances != null && fwdScreenInstances.length > 0) {
         for (int i = 0; i < fwdScreenInstances.length; i++) {
            if (fwdScreenInstances[i]._messageID == messageID) {
               count++;
            }
         }
      }

      return count;
   }
}
