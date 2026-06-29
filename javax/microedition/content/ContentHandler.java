package javax.microedition.content;

public interface ContentHandler {
   String ACTION_EDIT = "edit";
   String ACTION_EXECUTE = "execute";
   String ACTION_INSTALL = "install";
   String ACTION_NEW = "new";
   String ACTION_OPEN = "open";
   String ACTION_PRINT = "print";
   String ACTION_SAVE = "save";
   String ACTION_SELECT = "select";
   String ACTION_SEND = "send";
   String ACTION_STOP = "stop";
   String UNIVERSAL_TYPE = "*";

   String getID();

   String getType(int var1);

   int getTypeCount();

   boolean hasType(String var1);

   String getSuffix(int var1);

   int getSuffixCount();

   boolean hasSuffix(String var1);

   String getAction(int var1);

   int getActionCount();

   boolean hasAction(String var1);

   ActionNameMap getActionNameMap();

   ActionNameMap getActionNameMap(String var1);

   int getActionNameMapCount();

   ActionNameMap getActionNameMap(int var1);

   String getAppName();

   String getVersion();

   String getAuthority();
}
