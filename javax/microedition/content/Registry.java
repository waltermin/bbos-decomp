package javax.microedition.content;

public class Registry {
   Registry() {
   }

   public static Registry getRegistry(String classname) {
      return RegistryImpl.getRegistry(classname);
   }

   public static ContentHandlerServer getServer(String classname) {
      return RegistryImpl.getServer(classname);
   }

   public ContentHandlerServer register(
      String classname, String[] types, String[] suffixes, String[] actions, ActionNameMap[] actionnames, String ID, String[] accessAllowed
   ) {
      throw new SecurityException("Method should not be called");
   }

   public boolean unregister(String classname) {
      return false;
   }

   public String[] getTypes() {
      return null;
   }

   public String[] getIDs() {
      return null;
   }

   public String[] getActions() {
      return null;
   }

   public String[] getSuffixes() {
      return null;
   }

   public ContentHandler[] forType(String type) {
      return null;
   }

   public ContentHandler[] forAction(String action) {
      return null;
   }

   public ContentHandler[] forSuffix(String suffix) {
      return null;
   }

   public ContentHandler forID(String ID, boolean exact) {
      return null;
   }

   public ContentHandler[] findHandler(Invocation invocation) {
      throw new SecurityException("Method should not be called");
   }

   public boolean invoke(Invocation invocation, Invocation previous) {
      return false;
   }

   public boolean invoke(Invocation invocation) {
      return false;
   }

   public boolean reinvoke(Invocation invocation) {
      return false;
   }

   public Invocation getResponse(boolean wait) {
      return null;
   }

   public void cancelGetResponse() {
   }

   public void setListener(ResponseListener listener) {
   }

   public String getID() {
      return null;
   }

   static {
      ContentHandlerRegistrationHelperImpl.register();
   }
}
