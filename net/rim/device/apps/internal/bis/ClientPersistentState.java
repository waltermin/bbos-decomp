package net.rim.device.apps.internal.bis;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.util.ContentProtectedHashtable;

public final class ClientPersistentState {
   protected ContentProtectedHashtable _content;
   private PersistentObject _store = PersistentStore.getPersistentObject(6644286283389573765L);
   private static final long STATE_GUID;
   private static final String KEY_LOCALE;
   private static final String KEY_USERNAME;
   private static final String KEY_PASSWORD;
   private static ClientPersistentState instance;

   private ClientPersistentState() {
      synchronized (this._store) {
         Object contents = this._store.getContents();
         if (contents != null && contents instanceof Object) {
            this._content = (ContentProtectedHashtable)contents;
         } else {
            this._content = (ContentProtectedHashtable)(new Object(true));
            this._store.setContents(this._content, 51);
            this._store.commit();
         }
      }
   }

   public static final void initialize() {
      instance = new ClientPersistentState();
   }

   public static final ClientPersistentState getInstance() {
      return instance;
   }

   protected final String getString(String key) {
      synchronized (this._store) {
         return (String)this._content.get(key);
      }
   }

   protected final void setString(String key, String value) {
      synchronized (this._store) {
         this._content.put(key, value);
         this._store.commit();
      }
   }

   public final String getUserName() {
      return this.getString("userName");
   }

   public final void setUserName(String userName) {
      this.setString("userName", userName);
   }

   public final String getPassword() {
      return this.getString("password");
   }

   public final void setPassword(String password) {
      this.setString("password", password);
   }

   public final Locale getLocale() {
      String localeCode = this.getString("locale");
      return localeCode != null && localeCode.length() > 0 ? Common.getLocale(localeCode) : null;
   }

   public final void setLocale(Locale locale) {
      if (locale != null) {
         String localeCode = Common.getLocaleCode(locale);
         this.setString("locale", localeCode);
      }
   }

   public final void clearCredentials() {
      synchronized (this._store) {
         this._content.remove("userName");
         this._content.remove("password");
         this._store.commit();
      }
   }
}
