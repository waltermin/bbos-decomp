package net.rim.device.internal.io.store;

interface FSDescriptor {
   int ATTR_READABLE;
   int ATTR_WRITABLE;
   int ATTR_TEMP;
   int ATTR_HIDDEN;
   int ATTR_SYSTEM;
   int ATTR_ALIVE;
   int ATTR_CACHE;
   int ATTR_INDEXABLE;
   int ATTR_COMPRESSED;
   int ATTR_ENCRYPTED;
   int ATTR_ARCHIVE;
   int ATTR_NEVER_ARCHIVE;
   int ATTR_OTA_PLACEHOLDER;
   int ATTR_FAILED_RESTORE;
   int ATTR_DEVICE;
   int ATTR_FILE;
   int ATTR_FOLDER;
   int ATTR_MOUNT;
   int MAX_NAME_LENGTH;

   int getAttributes();

   String getAttribute(String var1);

   String getBindName();

   FolderImpl getFolder();

   String getName();

   void setName(String var1);

   boolean isAlive();

   void remove();

   void resurrect();

   String getJSRPath();

   void setAttributes(int var1, int var2);
}
