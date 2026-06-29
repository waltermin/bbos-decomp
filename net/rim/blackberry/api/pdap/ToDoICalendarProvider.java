package net.rim.blackberry.api.pdap;

import java.util.Date;
import javax.microedition.pim.ToDo;

class ToDoICalendarProvider extends PIMICalendarProvider {
   private ToDo _todo;

   public ToDoICalendarProvider(ToDo todo) {
      this._todo = todo;
   }

   @Override
   public int getCalendarComponent() {
      return 82003356;
   }

   @Override
   protected String getUIDString() {
      String uid = null;
      if (this._todo.countValues(103) > 0) {
         uid = String.valueOf(this._todo.getDate(103, 0));
      } else {
         uid = "0";
      }

      return uid + '-' + new Date().getTime() + "@rim.net";
   }

   @Override
   public void setDescription(int type, String paramValue, String description) {
      this._todo.addString(104, 0, description);
   }

   @Override
   public String getDescriptionValue() {
      return this._todo.countValues(104) > 0 ? this._todo.getString(104, 0) : null;
   }

   @Override
   public void setSummary(int type, String paramValue, String value) {
      this._todo.addString(107, 0, value);
   }

   @Override
   public String getSummaryValue() {
      return this._todo.countValues(107) > 0 ? this._todo.getString(107, 0) : null;
   }

   @Override
   public void setPriority(int value) {
      this._todo.addInt(105, 0, value);
   }

   @Override
   public int getPriority() {
      return this._todo.countValues(105) > 0 ? this._todo.getInt(105, 0) : 0;
   }

   @Override
   public void setDateTimeDue(int type, int paramType, Date dateTimeDueValue) {
      this._todo.addDate(103, 0, dateTimeDueValue.getTime());
   }

   @Override
   public Date getDateTimeDueValue() {
      return this._todo.countValues(103) > 0 ? new Date(this._todo.getDate(103, 0)) : null;
   }
}
