package net.rim.device.internal.io.store;

interface FSDescriptor {
   int ATTR_READABLE = 1;
   int ATTR_WRITABLE = 2;
   int ATTR_TEMP = 4;
   int ATTR_HIDDEN = 8;
   int ATTR_SYSTEM = 16;
   int ATTR_ALIVE = 32;
   int ATTR_CACHE = 64;
   int ATTR_INDEXABLE = 128;
   int ATTR_COMPRESSED = 256;
   int ATTR_ENCRYPTED = 512;
   int ATTR_ARCHIVE = 1024;
   int ATTR_NEVER_ARCHIVE = 2048;
   int ATTR_OTA_PLACEHOLDER = 4096;
   int ATTR_FAILED_RESTORE = 8192;
   int ATTR_DEVICE = 134217728;
   int ATTR_FILE = 268435456;
   int ATTR_FOLDER = 536870912;
   int ATTR_MOUNT = -1073741824;
   int MAX_NAME_LENGTH = 255;

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
