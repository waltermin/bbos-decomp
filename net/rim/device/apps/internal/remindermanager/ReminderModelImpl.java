package net.rim.device.apps.internal.remindermanager;

import net.rim.device.api.ui.Field;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.DescriptionProvider;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.reminders.ReminderModel;
import net.rim.device.apps.api.sync.Checksumable;
import net.rim.device.apps.api.sync.Reconcilable;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.utility.general.Copyable;
import net.rim.device.apps.api.utility.props.LongProp;
import net.rim.device.internal.ui.IconCollection;
import net.rim.vm.Array;

class ReminderModelImpl implements ReminderModel, LongProp, Copyable, Reconcilable, Checksumable, FieldProvider, DescriptionProvider, PersistableRIMModel {
   long _reminderTime = -1;
   long _reminderFiredFor = Long.MIN_VALUE;
   int _state = 1;
   private static final int ORDER;
   private static final IconCollection STATUS_ICONS = IconCollection.get("net_rim_Reminder_Status", 1);
   private static ContextObject _checksumContext = (ContextObject)(new Object(60));

   @Override
   public boolean hasReminder() {
      return this._reminderTime != -1;
   }

   @Override
   public byte getType() {
      throw null;
   }

   @Override
   public int getState() {
      return this._state;
   }

   @Override
   public void setState(int state) {
      this._state = state;
   }

   @Override
   public long getTime() {
      return this._reminderTime;
   }

   @Override
   public void setTime(long time) {
      this._reminderTime = time;
   }

   @Override
   public long getReminderFiredFor() {
      return this._reminderFiredFor;
   }

   @Override
   public void setReminderFiredFor(long time) {
      this._reminderFiredFor = time;
   }

   @Override
   public void setLong(long l) {
      this._reminderTime = l;
   }

   @Override
   public long getLong() {
      return this._reminderTime;
   }

   @Override
   public Object copy() {
      throw null;
   }

   @Override
   public void reconcile(Object objectToReconcile, Object context) {
      if (objectToReconcile instanceof Object) {
         if (context != null) {
            ReminderModel reminderModelToReconcile = (ReminderModel)objectToReconcile;
            Object p1 = ContextObject.get(context, -442904859142728844L);
            Object p2 = ContextObject.get(context, 4930575420328309875L);
            Checksumable c1 = null;
            Checksumable c2 = null;
            if (p1 instanceof Object) {
               c1 = (Checksumable)p1;
            }

            if (p2 instanceof Object) {
               c2 = (Checksumable)p2;
            }

            if (c1 != null && c2 != null) {
               if (c1.getChecksum(_checksumContext) == c2.getChecksum(_checksumContext)) {
                  this._state = reminderModelToReconcile.getState();
                  this._reminderFiredFor = reminderModelToReconcile.getReminderFiredFor();
               }
            }
         }
      }
   }

   @Override
   public long getChecksum(Object context) {
      return this._reminderTime;
   }

   @Override
   public Field getField(Object _1) {
      throw null;
   }

   @Override
   public boolean grabDataFromField(Field _1, Object _2) {
      throw null;
   }

   @Override
   public boolean validate(Field field, Object context) {
      return true;
   }

   @Override
   public int getOrder(Object context) {
      return 1000;
   }

   @Override
   public String getStringForField(long field) {
      return CommonResources.getString(9052);
   }

   @Override
   public String getStringForField(long field, long data) {
      return this.getStringForField(field);
   }

   @Override
   public int getIconsForField(long field, IconCollection[] icons, int[][][] indices) {
      if (!this.hasReminder()) {
         return 0;
      }

      int numIcons = icons.length;
      Array.resize(icons, numIcons + 1);
      Array.resize(indices, numIcons + 1);
      icons[numIcons] = STATUS_ICONS;
      indices[numIcons] = (int[][])(new int[]{0, -804519911, 0, 0});
      return 1;
   }

   @Override
   public byte getProperties() {
      return 0;
   }
}
