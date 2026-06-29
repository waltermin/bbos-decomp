package javax.microedition.content;

public interface ContentHandler {
   String ACTION_EDIT;
   String ACTION_EXECUTE;
   String ACTION_INSTALL;
   String ACTION_NEW;
   String ACTION_OPEN;
   String ACTION_PRINT;
   String ACTION_SAVE;
   String ACTION_SELECT;
   String ACTION_SEND;
   String ACTION_STOP;
   String UNIVERSAL_TYPE;

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
