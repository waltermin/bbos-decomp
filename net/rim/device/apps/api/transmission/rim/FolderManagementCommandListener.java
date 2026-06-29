package net.rim.device.apps.api.transmission.rim;

import net.rim.device.apps.api.transmission.rim.otasync.OTAFMConfiguration;

public interface FolderManagementCommandListener {
   void moveMessageCommand(int var1, int var2, int var3, Object var4);

   void deleteMessageCommand(int var1, int var2, Object var3);

   void messageStatusCommand(int var1, int var2, Object var3);

   void otafmConfigCommand(OTAFMConfiguration var1, Object var2);

   void otafmConfigAckCommand(OTAFMConfiguration var1, Object var2);

   void configurationRequestCommand(Object var1);

   void messageListRequestCommand(Object var1);

   void removeAllFoldersCommand(Object var1);

   void folderSyncCompleteCommand(Object var1);

   void createFolderCommand(int var1, int var2, String var3, int var4, int var5, Object var6);

   void deleteFolderCommand(int var1, int var2, Object var3);

   void modifyFolderCommand(int var1, int var2, String var3, int var4, int var5, Object var6);

   void messageListAckCommand(int var1, Object var2);

   void unknownCommand(int var1, Object var2);
}
