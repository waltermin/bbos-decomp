package net.rim.device.apps.internal.messaging.search.criteria;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.ui.Field;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.search.SearchCriterion;
import net.rim.device.apps.internal.blackberryemail.folder.EmailFolder;
import net.rim.device.apps.internal.blackberryemail.folder.EmailHierarchy;
import net.rim.device.apps.internal.messaging.search.resources.SearchResources;

public final class FolderModel implements PersistableRIMModel, SearchCriterion, FieldProvider, ConversionProvider {
   long _folderId = 0;

   public final Verb getDefaultVerb(Verb[] verbs, Object context) {
      return null;
   }

   public final void establishValue() {
      if (this._folderId != 0) {
         Folder f = FolderHierarchies.getFolder(this._folderId);
         if (f != null) {
            this._folderId = f.getLUID();
         }
      }
   }

   public final Verb[] getVerbs(Object context) {
      return null;
   }

   @Override
   public final int getOrder(Object context) {
      return 12300;
   }

   @Override
   public final int getType() {
      return 15;
   }

   @Override
   public final boolean convert(Object context, Object target) {
      if (ContextObject.getFlag(context, 22) && ContextObject.getFlag(context, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)target;
         long luid = this._folderId;
         if (luid != 0) {
            EmailFolder folder = EmailHierarchy.getEmailFolder(luid);
            if (folder != null) {
               EmailHierarchy hier = folder.getEmailHierarchy();
               byte[] cppFolder = new byte[12];
               int service_name_hash = hier.getServiceNameHash();
               int service_uid_hash = hier.getServiceUidHash();
               short folder_id = (short)folder.getFolderId();
               byte folder_type = (byte)folder.getFolderType();
               ConverterUtilities.injectCInt(cppFolder, 0, false, service_name_hash);
               ConverterUtilities.injectCInt(cppFolder, 4, false, service_uid_hash);
               ConverterUtilities.injectCShort(cppFolder, 8, false, folder_id);
               ConverterUtilities.injectCByte(cppFolder, 10, folder_type);
               syncBuffer.addBytes(13, cppFolder);
            }

            syncBuffer.addLong(8, luid);
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public final Field getField(Object context) {
      String folderName = null;
      Folder folder = null;
      if (this._folderId != 0) {
         folder = FolderHierarchies.getFolder(this._folderId);
         if (folder != null) {
            folderName = folder.getFriendlyName();
         } else {
            this._folderId = 0;
         }
      }

      return new FolderSelectionField(SearchResources.getString(37), folderName, folder);
   }

   @Override
   public final boolean grabDataFromField(Field field, Object context) {
      FolderSelectionField fsf = (FolderSelectionField)field;
      this._folderId = fsf.getFolderId();
      return true;
   }

   @Override
   public final boolean validate(Field field, Object context) {
      return true;
   }

   @Override
   public final Object getValue() {
      return new Long(this._folderId);
   }

   FolderModel() {
   }
}
