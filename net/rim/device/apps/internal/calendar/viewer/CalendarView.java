package net.rim.device.apps.internal.calendar.viewer;

interface CalendarView {
   void initialize();

   void uninitialize();

   void display(int var1, boolean var2);

   void refresh();

   void remove();

   long getTimeWithFocus();

   void setTimeWithFocus(long var1);
}
