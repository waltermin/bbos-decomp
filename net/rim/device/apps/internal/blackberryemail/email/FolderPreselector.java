package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Persistable;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.internal.blackberryemail.folder.EmailFolder;
import net.rim.device.apps.internal.blackberryemail.folder.EmailHierarchy;
import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderModel;
import net.rim.device.apps.internal.blackberryemail.header.SubjectModel;
import net.rim.vm.Array;

public final class FolderPreselector implements Persistable {
   private long _hierarchyLUID;
   private IntToLongMRU _subjectCache;
   private IntToLongMRU _nameCache;
   private static final long KEY = -2903705266711841653L;
   private static final int SUBJECT_CACHE_SIZE = 1024;
   private static final int NAME_CACHE_SIZE = 1024;
   private static FolderPreselector[] _preselectors;

   private FolderPreselector(EmailHierarchy hierarchy) {
      this._hierarchyLUID = hierarchy.getLUID();
      this._subjectCache = new IntToLongMRU(1024);
      this._nameCache = new IntToLongMRU(1024);
   }

   public static final FolderPreselector getInstance(EmailHierarchy hierarchy) {
      long hierarchyLUID = hierarchy.getLUID();
      FolderPreselector selector;
      synchronized (_preselectors) {
         int count = _preselectors.length;

         for (int i = count - 1; i >= 0; i--) {
            if (hierarchyLUID == _preselectors[i]._hierarchyLUID) {
               return _preselectors[i];
            }
         }

         Array.resize(_preselectors, count + 1);
         selector = _preselectors[count] = new FolderPreselector(hierarchy);
      }

      PersistentObject.commit(_preselectors);
      return selector;
   }

   final EmailFolder getRecommendedFolder(EmailMessageModel message) {
      EmailHierarchy hierarchy = EmailHierarchy.getEmailHierarchy(this._hierarchyLUID);
      long folderLUID = 0;
      if (hierarchy == null) {
         return null;
      }

      int subjectKey = this.getSubjectKey(message);
      if (subjectKey != 0) {
         folderLUID = this._subjectCache.get(subjectKey);
      }

      if (folderLUID == 0) {
         int nameKey = this.getNameKey(message);
         if (nameKey != 0) {
            folderLUID = this._nameCache.get(nameKey);
         }
      }

      if (folderLUID != 0) {
         EmailFolder folder = (EmailFolder)hierarchy.getFolder(folderLUID);
         if (folder != null && folder.isInFolderDatabase()) {
            return folder;
         }
      }

      return null;
   }

   public final void setRecommendedFolder(EmailMessageModel message, long folderLUID) {
      int subjectKey = this.getSubjectKey(message);
      if (subjectKey != 0) {
         this._subjectCache.put(subjectKey, folderLUID);
      }

      int nameKey = this.getNameKey(message);
      if (nameKey != 0) {
         this._nameCache.put(nameKey, folderLUID);
      }

      PersistentObject.commit(this);
   }

   final void setRecommendedFolderQuickly(EmailMessageModel message, long folderLUID) {
      int subjectKey = this.getSubjectKey(message);
      if (subjectKey != 0) {
         this._subjectCache.quickPut(subjectKey, folderLUID);
      }

      int nameKey = this.getNameKey(message);
      if (nameKey != 0) {
         this._nameCache.quickPut(nameKey, folderLUID);
      }
   }

   final void cleanupRecommendedFolderQuickly() {
      this._subjectCache.quickCleanup();
      this._nameCache.quickCleanup();
      PersistentObject.commit(this);
   }

   private final int getSubjectKey(EmailMessageModel message) {
      String subject = null;

      for (int i = message.size() - 1; i >= 0; i--) {
         Object submember = message.getAt(i);
         if (submember instanceof SubjectModel) {
            subject = ((SubjectModel)submember).toString();
            i = subject.lastIndexOf(58);
            if (i != -1) {
               subject = subject.substring(i + 1);
            }

            subject = StringUtilities.toLowerCase(subject.trim(), 1701707776);
            if (subject.length() == 0) {
               return 0;
            }

            return subject.hashCode();
         }
      }

      return 0;
   }

   private final int getNameKey(EmailMessageModel message) {
      String name = null;
      int headerType = message.inbound() ? 3 : 0;

      for (int i = message.size() - 1; i >= 0; i--) {
         Object submember = message.getAt(i);
         if (submember instanceof EmailHeaderModel && ((EmailHeaderModel)submember).getHeaderType() == headerType) {
            name = submember.toString();
            name = StringUtilities.toLowerCase(name.trim(), 1701707776);
            if (name.length() == 0) {
               return 0;
            }

            return name.hashCode();
         }
      }

      return 0;
   }

   public static final void updateDefaultFolder(EmailMessageModel message, EmailFolder folder, boolean quick) {
      if (message != null && folder != null) {
         if (folder.isInFolderDatabase()) {
            switch (folder.getFolderType()) {
               case 0:
               case 3:
                  FolderPreselector fp = getInstance(folder.getEmailHierarchy());
                  long folderId = folder.getLUID();
                  if (quick) {
                     fp.setRecommendedFolderQuickly(message, folderId);
                     return;
                  }

                  fp.setRecommendedFolder(message, folderId);
                  break;
               case 1:
               case 2:
               case 4:
               default:
                  return;
            }
         }
      }
   }

   static {
      PersistentObject persistentObject = RIMPersistentStore.getPersistentObject(-2903705266711841653L);
      synchronized (persistentObject) {
         _preselectors = (FolderPreselector[])persistentObject.getContents();
         if (_preselectors == null) {
            _preselectors = new FolderPreselector[0];
            persistentObject.setContents(_preselectors, 51);
            persistentObject.commit();
         }
      }
   }
}
