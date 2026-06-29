package net.rim.blackberry.api.pim;

import java.util.Date;

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

      return ((StringBuffer)(new Object())).append(uid).append('-').append(String.valueOf(((Date)(new Object())).getTime())).append("@rim.net").toString();
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
      return this._todo.getString(107, 0);
   }

   @Override
   public void setPriority(int value) {
      this._todo.setInt(105, 0, 0, value);
   }

   @Override
   public int getPriority() {
      return this._todo.getInt(105, 0);
   }

   @Override
   public void setDateTimeDue(int type, int paramType, Date dateTimeDueValue) {
      this._todo.addDate(103, 0, dateTimeDueValue.getTime());
   }

   @Override
   public Date getDateTimeDueValue() {
      return (Date)(this._todo.countValues(103) > 0 ? new Object(this._todo.getDate(103, 0)) : null);
   }
}
