package net.rim.device.apps.internal.calendar.meeting;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.container.VerticalFieldManager;

class MeetingInfoModel$NotifyVerticalFieldManager extends VerticalFieldManager {
   @Override
   public void add(Field field) {
      super.add(field);
      this.fieldChangeNotify(0);
   }

   @Override
   public void insert(Field field, int index) {
      super.insert(field, index);
      this.fieldChangeNotify(0);
   }

   @Override
   public void delete(Field field) {
      super.delete(field);
      this.fieldChangeNotify(0);
   }

   @Override
   public void deleteRange(int start, int count) {
      super.deleteRange(start, count);
      this.fieldChangeNotify(0);
   }
}
