package javax.microedition.content;

import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.util.DataBuffer;

final class ContentHandlerConverter {
   private static final int HANDLER_ID = 0;
   private static final int HANDLER_TYPES = 1;
   private static final int HANDLER_SUFFIXES = 2;
   private static final int HANDLER_ACTIONS = 3;
   private static final int HANDLER_ACTION_NAMES = 4;
   private static final int HANDLER_APP_NAME = 5;
   private static final int HANDLER_VERSION = 6;
   private static final int HANDLER_AUTHORITY = 7;
   private static final int HANDLER_CLASSNAME = 8;
   private static final int HANDLER_ACCESS_ALLOWED = 9;
   private static final int HANDLER_REGISTRATION = 10;

   private ContentHandlerConverter() {
   }

   static final ContentHandlerServerImpl convert(DataBuffer buffer) {
      try {
         ContentHandlerServerImpl server = new ContentHandlerServerImpl();
         String ID = null;
         String classname = null;
         String appName = null;
         String version = null;
         String authority = null;
         String[] types = null;
         String[] suffixes = null;
         String[] actions = null;
         String[] accessAllowed = null;
         ActionNameMap[] actionnames = null;
         int regType = -1;

         while (buffer.available() > 0) {
            switch (buffer.readByte()) {
               case -1:
                  return null;
               case 0:
               default:
                  ID = buffer.readUTF();
                  if (ID == null) {
                     return null;
                  }
                  break;
               case 1:
                  types = readStringArray(buffer);
                  break;
               case 2:
                  suffixes = readStringArray(buffer);
                  break;
               case 3:
                  actions = readStringArray(buffer);
                  break;
               case 4:
                  int numMaps = buffer.readByte();
                  actionnames = new ActionNameMap[numMaps];

                  for (int i = 0; i < numMaps; i++) {
                     String locale = buffer.readUTF();
                     int numActionNames = buffer.readByte();
                     String[] theActions = new String[numActionNames];
                     String[] theNames = new String[numActionNames];

                     for (int j = 0; j < numActionNames; j++) {
                        theActions[j] = buffer.readUTF();
                        theNames[j] = buffer.readUTF();
                     }

                     actionnames[i] = new ActionNameMap(theActions, theNames, locale);
                  }
                  break;
               case 5:
                  appName = buffer.readUTF();
                  break;
               case 6:
                  version = buffer.readUTF();
                  break;
               case 7:
                  authority = buffer.readUTF();
                  break;
               case 8:
                  classname = buffer.readUTF();
                  if (classname == null) {
                     return null;
                  }
                  break;
               case 9:
                  accessAllowed = readStringArray(buffer);
                  break;
               case 10:
                  regType = buffer.readByte();
            }
         }

         server.init(types, suffixes, actions, actionnames, ID, accessAllowed);
         server.setAppName(appName);
         server.setVersion(version);
         server.setAuthority(authority);
         server.setClassname(classname);
         server.setDynamic(regType == 0);
         Class c = Class.forName(classname);
         int moduleHandle = CodeModuleManager.getModuleHandleForClass(c);
         server.setApplicationDescriptor(ContentHandlerUtilities.findApplicationDescriptor(moduleHandle, classname));
         server.setModuleHandle(moduleHandle);
         return server;
      } catch (Exception e) {
         return null;
      }
   }

   private static final String[] readStringArray(DataBuffer buffer) {
      int size = buffer.readByte();
      String[] strs = new String[size];

      for (int i = 0; i < size; i++) {
         strs[i] = buffer.readUTF();
      }

      return strs;
   }

   static final DataBuffer convert(ContentHandlerServerImpl server) {
      try {
         DataBuffer buffer = new DataBuffer();
         buffer.writeByte(0);
         buffer.writeUTF(server.getID());
         buffer.writeByte(8);
         buffer.writeUTF(server.getClassname());
         writeString(buffer, 5, server.getAppName());
         writeString(buffer, 6, server.getVersion());
         writeString(buffer, 7, server.getAuthority());
         writeStringArray(buffer, 1, server.getTypes());
         writeStringArray(buffer, 2, server.getSuffixes());
         writeStringArray(buffer, 3, server.getActions());
         writeStringArray(buffer, 9, server.getAccessAllowed());
         ActionNameMap[] actionnames = server.getActionNameMaps();
         if (actionnames != null && actionnames.length != 0) {
            buffer.writeByte(4);
            buffer.writeByte(actionnames.length);

            for (int i = 0; i < actionnames.length; i++) {
               int size = actionnames[i].size();
               buffer.writeUTF(actionnames[i].getLocale());
               buffer.writeByte(size);

               for (int j = 0; j < size; j++) {
                  buffer.writeUTF(actionnames[i].getAction(j));
                  buffer.writeUTF(actionnames[i].getActionName(j));
               }
            }
         }

         buffer.writeByte(10);
         buffer.writeByte(server.isDynamic() ? 0 : 1);
         buffer.rewind();
         return buffer;
      } catch (Exception e) {
         return null;
      }
   }

   private static final void writeString(DataBuffer buffer, int param, String str) {
      if (str != null && str.length() != 0) {
         buffer.writeByte(param);
         buffer.writeUTF(str);
      }
   }

   private static final void writeStringArray(DataBuffer buffer, int param, String[] strs) {
      if (strs != null && strs.length != 0) {
         buffer.writeByte(param);
         buffer.writeByte(strs.length);

         for (int i = 0; i < strs.length; i++) {
            buffer.writeUTF(strs[i]);
         }
      }
   }
}
