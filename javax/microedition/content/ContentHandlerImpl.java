package javax.microedition.content;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringUtilities;

class ContentHandlerImpl implements ContentHandler {
   protected String[] _types;
   protected String[] _suffixes;
   protected String[] _actions;
   protected ActionNameMap[] _actionnames;
   protected String _ID;
   protected String _appName;
   protected String _version;
   protected String _authority;
   protected String _classname;
   protected int _moduleHandle;
   protected boolean _dynamic;

   void setVersion(String version) {
      this._version = version;
   }

   String[] getTypes() {
      return this._types;
   }

   String[] getActions() {
      return this._actions;
   }

   String[] getSuffixes() {
      return this._suffixes;
   }

   boolean isDynamic() {
      return this._dynamic;
   }

   void setDynamic(boolean dynamic) {
      this._dynamic = dynamic;
   }

   String getClassname() {
      return this._classname;
   }

   ActionNameMap[] getActionNameMaps() {
      return this._actionnames;
   }

   void setAppName(String appName) {
      this._appName = appName;
   }

   void setModuleHandle(int handle) {
      this._moduleHandle = handle;
   }

   int getModuleHandle() {
      return this._moduleHandle;
   }

   void setClassname(String classname) {
      this._classname = classname;
   }

   void setAuthority(String authority) {
      this._authority = authority;
   }

   @Override
   public String getAppName() {
      return this._appName;
   }

   @Override
   public int getActionNameMapCount() {
      return this._actionnames.length;
   }

   @Override
   public ActionNameMap getActionNameMap(int index) {
      if (index >= 0 && index < this.getActionNameMapCount()) {
         return this._actionnames[index];
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   @Override
   public ActionNameMap getActionNameMap(String locale) {
      for (int i = 0; i < this._actionnames.length; i++) {
         if (this._actionnames[i].getLocale().equals(locale)) {
            return this._actionnames[i];
         }
      }

      int hashIndex = locale.lastIndexOf(45);
      return hashIndex == -1 ? null : this.getActionNameMap(locale.substring(0, hashIndex));
   }

   @Override
   public String getAuthority() {
      return this._authority;
   }

   @Override
   public String getID() {
      return this._ID;
   }

   @Override
   public String getSuffix(int index) {
      if (index >= 0 && index < this.getSuffixCount()) {
         return this._suffixes[index];
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   @Override
   public int getSuffixCount() {
      return this._suffixes.length;
   }

   @Override
   public ActionNameMap getActionNameMap() {
      return this.getActionNameMap(Locale.getCLDCLocaleString());
   }

   @Override
   public String getType(int index) {
      if (index >= 0 && index < this.getTypeCount()) {
         return this._types[index];
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   @Override
   public int getTypeCount() {
      return this._types.length;
   }

   @Override
   public int getActionCount() {
      return this._actions.length;
   }

   @Override
   public String getAction(int index) {
      if (index >= 0 && index < this.getActionCount()) {
         return this._actions[index];
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   @Override
   public String getVersion() {
      return this._version;
   }

   @Override
   public boolean hasAction(String action) {
      if (action == null) {
         throw new NullPointerException("action is null");
      } else {
         return Arrays.getIndex(this._actions, action) != -1;
      }
   }

   @Override
   public boolean hasSuffix(String suffix) {
      if (suffix == null) {
         throw new NullPointerException("suffix is null");
      }

      for (int i = 0; i < this._suffixes.length; i++) {
         if (StringUtilities.strEqualIgnoreCase(suffix, this._suffixes[i])) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean hasType(String type) {
      if (type == null) {
         throw new NullPointerException("type is null");
      }

      for (int i = 0; i < this._types.length; i++) {
         if (StringUtilities.strEqualIgnoreCase(type, this._types[i]) || this._types[i].equals("*")) {
            return true;
         }
      }

      return false;
   }

   ContentHandlerImpl(ContentHandlerImpl handler) {
      this._types = handler.getTypes();
      this._suffixes = handler.getSuffixes();
      this._actions = handler.getActions();
      this._actionnames = handler.getActionNameMaps();
      this._ID = handler.getID();
      this._appName = handler.getAppName();
      this._version = handler.getVersion();
      this._authority = handler.getAuthority();
      this._classname = handler.getClassname();
      this._moduleHandle = handler.getModuleHandle();
      this._dynamic = handler.isDynamic();
   }

   ContentHandlerImpl() {
   }

   @Override
   public String toString() {
      return this._ID;
   }
}
