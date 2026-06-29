package net.rim.device.apps.internal.phone.api.ui.cdma;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.ObjectListField;
import net.rim.device.api.ui.container.MainScreen;

final class IOTAEditScreen extends MainScreen {
   private PersistentObject _persist;
   private Hashtable _storage;
   private ObjectListField _listField;
   private EditField _propField;
   private int _numOfItems;
   public static final String PHONE_BOOT_URL = "phone:boot.url";
   public static final String PHONE_BOOT_NAI_URL = "phone:boot.naiurl";
   public static final String PHONE_NAI_CURRENT = "phone:nai.current";
   public static final String BROWSER_DOMAIN_TRUSTED = "browser:domain.trusted";
   public static final String BROWSER_PROXY = "phone:proxy..0.address..0.wdp";
   public static final String PHONE_CDMA_PRL = "phone:cdma.prl";

   public IOTAEditScreen() {
      super(196608);
      this.setTitle("IOTA Utility Editor");
      this._persist = PersistentStore.getPersistentObject(-7688435971308426807L);
      this._storage = (Hashtable)this._persist.getContents();
      if (this._storage == null || this._storage.size() == 0) {
         this._storage = (Hashtable)(new Object());
         this._storage.put("phone:boot.url", new Object(Branding.getData(20992)));
         this._storage.put("phone:boot.naiurl", new Object(Branding.getData(20993)));
         this._storage.put("browser:domain.trusted", new Object(Branding.getData(20994)));
         this._storage.put("phone:proxy..0.address..0.wdp", new Object(Branding.getData(20995)));
         this._storage.put("phone:cdma.prl", new byte[0]);
         this._persist.setContents(this._storage, 51);
         this._persist.commit();
      }

      this.initFields();
   }

   public final void initFields() {
      this.deleteAll();
      this._propField = (EditField)(new Object("Current Value: ", ""));
      this._propField.setEditable(false);
      this._listField = (ObjectListField)(new Object(9007199254740992L));
      this.add(this._listField);
      this.add((Field)(new Object()));
      this.add(this._propField);
      this._numOfItems = 0;
      Enumeration e = this._storage.keys();
      this._numOfItems = 0;
      if (e != null) {
         synchronized (Application.getEventLock()) {
            while (e.hasMoreElements()) {
               String key = (String)e.nextElement();
               if (!key.equals("phone:cdma.prl")) {
                  this._listField.insert(this._numOfItems++, key);
               }
            }
         }
      }

      this.updateValue();
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (this._listField.getSelectedIndex() == -1) {
         return super.keyChar(key, status, time);
      }

      if (key == '\n') {
         String curObj = (String)this._listField.get(this._listField, this._listField.getSelectedIndex());
         Object curVal = this._storage.get(curObj);
         synchronized (Application.getEventLock()) {
            UiApplication.getUiApplication().pushScreen(new IOTAObjectEditScreen(this, curObj, curVal.toString()));
            return true;
         }
      } else if (key == 127) {
         if (!this._listField.isEmpty()) {
            String curObj = (String)this._listField.get(this._listField, this._listField.getSelectedIndex());
            this._listField.delete(this._listField.getSelectedIndex());
            this._storage.remove(curObj);
            this._persist.commit();
            synchronized (Application.getEventLock()) {
               this._propField.setText("");
               return true;
            }
         } else {
            return true;
         }
      } else {
         return super.keyChar(key, status, time);
      }
   }

   public final void updateValue() {
      synchronized (Application.getEventLock()) {
         if (!this._listField.isEmpty() && this._propField != null) {
            this._propField.setText((String)this._storage.get(this._listField.get(this._listField, this._listField.getSelectedIndex())));
         }
      }
   }

   @Override
   protected final boolean trackwheelRoll(int amount, int status, int time) {
      boolean result = super.trackwheelRoll(amount, status, time);
      if (this.getFieldWithFocus() == this._listField) {
         this.updateValue();
         result = true;
      }

      return result;
   }

   public final void removeOldValues(String obj) {
      this._storage.remove(obj);
      this._persist.commit();
      this.initFields();
   }

   public final void addNewValues(String obj, String val) {
      this._storage.put(obj, val);
      synchronized (Application.getEventLock()) {
         this._listField.insert(this._numOfItems++, obj);
      }

      this._persist.commit();
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      super.makeMenu(menu, instance);
      menu.add(new IOTAEditScreen$IOTAMenuItem(this, "Add", 0));
      if (this.getFieldWithFocus() == this._listField && this._listField.getSize() > 0) {
         menu.add(new IOTAEditScreen$IOTAMenuItem(this, "Delete", 1));
         menu.add(new IOTAEditScreen$IOTAMenuItem(this, "Edit", 2));
      }
   }
}
