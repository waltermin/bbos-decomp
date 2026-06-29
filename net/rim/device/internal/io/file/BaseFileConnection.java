package net.rim.device.internal.io.file;

import net.rim.device.api.io.file.ExtendedFileConnection;

public interface BaseFileConnection extends ExtendedFileConnection {
   int SUPPORT_RENAME_EX = 1;

   void init(String var1, int var2, boolean var3);

   int getSupportFlags();

   void renameEx(String var1);
}
