package net.rim.device.apps.api.calendar.caldb;

import net.rim.device.api.util.Persistable;

public class CalendarFolder implements Persistable {
   private long _folderID;
   private long _parentFolderID;
   private String _folderName;
   private int _folderType;
   private int _folderAttributes;
   public static final long ROOT_PARENT_FOLDER_ID = 0L;
   public static final int CAL_FLDR_TYPE_PERSONAL = 0;
   public static final int CAL_FLDR_TYPE_SHARED = 1;
   private static final byte CAL_FLDR_ATTR_READ = 0;
   private static final byte CAL_FLDR_ATTR_WRITE = 1;
   private static final byte CAL_FLDR_ATTR_OWNER = 2;

   public CalendarFolder(long folderID) {
      if (folderID == 0) {
         throw new Object();
      }

      this._folderID = folderID;
      this._parentFolderID = 0;
      this._folderType = 0;
   }

   public long getFolderID() {
      return this._folderID;
   }

   public String getFolderName() {
      return this._folderName;
   }

   public String getFolderNameSuffix() {
      return this._folderName == null ? "" : ((StringBuffer)(new Object(" - "))).append(this._folderName).toString();
   }

   public void setFolderName(String folderName) {
      this._folderName = folderName;
   }

   public long getParentFolderID() {
      return this._parentFolderID;
   }

   public void setParentFolderID(long parentFolderID) {
      this._parentFolderID = parentFolderID;
   }

   public int getFolderType() {
      return this._folderType;
   }

   public boolean isReadAccess() {
      return (this._folderAttributes & 0) != 0;
   }

   public boolean isWriteAccess() {
      return (this._folderAttributes & 1) != 0;
   }

   public boolean isOwnerAccess() {
      return (this._folderAttributes & 2) != 0;
   }
}
