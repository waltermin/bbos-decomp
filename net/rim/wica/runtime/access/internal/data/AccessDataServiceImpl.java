package net.rim.wica.runtime.access.internal.data;

import java.util.Enumeration;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.ToIntHashtable;
import net.rim.wica.common.builtindata.componentEnums.StdComponentEnumCollection;
import net.rim.wica.common.metadata.component.EnumCollection;
import net.rim.wica.runtime.access.data.AccessDataService;
import net.rim.wica.runtime.access.data.collections.StdCmpCollection;
import net.rim.wica.runtime.access.internal.data.collections.AddressCollection;
import net.rim.wica.runtime.access.internal.data.collections.AttendeeCollection;
import net.rim.wica.runtime.access.internal.data.collections.ContactCollection;
import net.rim.wica.runtime.access.internal.data.collections.DeviceInfoCollection;
import net.rim.wica.runtime.access.internal.data.collections.EmailCollection;
import net.rim.wica.runtime.access.internal.data.collections.EventCollection;
import net.rim.wica.runtime.access.internal.data.collections.RepeatRuleCollection;
import net.rim.wica.runtime.access.internal.data.collections.TasksCollection;
import net.rim.wica.runtime.metadata.Wiclet;
import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.metadata.internal.WicletEx;
import net.rim.wica.runtime.service.Startable;

public class AccessDataServiceImpl implements AccessDataService, Startable {
   private StdComponentEnumCollection _enumCollection = StdComponentEnumCollection.getInstance();
   private ToIntHashtable _definitionHandles;
   private IntHashtable _collections;
   private static final int ACCESS_NUM_COMP_DEFS_AND_ENUMS;

   @Override
   public void start() {
      this._definitionHandles = (ToIntHashtable)(new Object(21));
      getAllStdCompDefs(this._definitionHandles);
      this._collections = (IntHashtable)(new Object(12));
   }

   public static int getNumStdCompDefs() {
      return 14;
   }

   public static void getAllStdCompDefs(ToIntHashtable definitions) {
      if (definitions != null) {
         definitions.put("Contact", 2);
         definitions.put("Event", 3);
         definitions.put("Task", 5);
         definitions.put("DeviceInfo", 8);
         definitions.put("Message", 7);
         definitions.put("Attendee", 6);
         definitions.put("Address", 1);
         definitions.put("RepeatRule", 4);
         definitions.put("STATUS", 128);
         definitions.put("REPEAT_RULE_FREQUENCY", 129);
         definitions.put("PRIORITY", 130);
         definitions.put("FREEBUSY", 131);
         definitions.put("ATTENDEE_TYPE", 132);
         definitions.put("FOLDER", 133);
      }
   }

   @Override
   public void stop() {
   }

   @Override
   public int getDefType(int defID) {
      return defID - 128 >= 0 ? 5 : 6;
   }

   @Override
   public ToIntHashtable getAllDefs() {
      return this._definitionHandles;
   }

   @Override
   public DataCollection getDataCollection(int defHandle, Wiclet wiclet) {
      if (this._collections.containsKey(defHandle)) {
         return (DataCollection)this._collections.get(defHandle);
      }

      DataCollection collection = null;
      switch (defHandle) {
         case 0:
            break;
         case 1:
            collection = new AddressCollection((WicletEx)wiclet);
            break;
         case 2:
         default:
            collection = new ContactCollection((WicletEx)wiclet);
            break;
         case 3:
            collection = new EventCollection((WicletEx)wiclet);
            break;
         case 4:
            collection = new RepeatRuleCollection((WicletEx)wiclet);
            break;
         case 5:
            collection = new TasksCollection((WicletEx)wiclet);
            break;
         case 6:
            collection = new AttendeeCollection((WicletEx)wiclet);
            break;
         case 7:
            collection = new EmailCollection((WicletEx)wiclet);
            break;
         case 8:
            collection = new DeviceInfoCollection((WicletEx)wiclet);
      }

      if (collection != null) {
         this._collections.put(defHandle, collection);
      }

      return collection;
   }

   @Override
   public EnumCollection getEnumCollection() {
      return this._enumCollection;
   }

   @Override
   public void activate() {
      if (this._collections != null) {
         Enumeration e = this._collections.elements();

         while (e.hasMoreElements()) {
            Object dCollection = e.nextElement();
            if (dCollection instanceof StdCmpCollection) {
               ((StdCmpCollection)dCollection).initHandles();
            }
         }
      }
   }

   @Override
   public void deactivate() {
      if (this._collections != null) {
         Enumeration e = this._collections.elements();

         while (e.hasMoreElements()) {
            Object dCollection = e.nextElement();
            if (dCollection instanceof StdCmpCollection) {
               ((StdCmpCollection)dCollection).resetCache();
            }
         }
      }
   }

   @Override
   public void clearDataCollection(int defHandle) {
      this._collections.remove(defHandle);
   }

   @Override
   public void clearDataCollections() {
      this._collections.clear();
   }
}
