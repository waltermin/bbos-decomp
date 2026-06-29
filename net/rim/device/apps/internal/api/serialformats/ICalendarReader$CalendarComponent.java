package net.rim.device.apps.internal.api.serialformats;

import net.rim.device.api.util.IntVector;

class ICalendarReader$CalendarComponent {
   private int tag;
   private IntVector properties;

   public ICalendarReader$CalendarComponent(int tag) {
      this.tag = tag;
      this.properties = new IntVector();
   }

   public int getTag() {
      return this.tag;
   }

   public void add(int property) {
      this.properties.addElement(property);
   }

   public boolean contains(int property) {
      return this.properties.contains(property);
   }
}
