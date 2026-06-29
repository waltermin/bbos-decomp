package net.rim.blackberry.api.pdap;

import javax.microedition.pim.ToDo;
import net.rim.device.api.util.Comparator;

class ToDoListImpl$DueDateComparator implements Comparator {
   @Override
   public int compare(Object o1, Object o2) {
      if (o1 == null || o2 == null) {
         return 0;
      }

      if (!(o1 instanceof ToDo)) {
         throw new ClassCastException();
      }

      ToDo todo1 = (ToDo)o1;
      if (!(o2 instanceof ToDo)) {
         throw new ClassCastException();
      }

      ToDo todo2 = (ToDo)o2;
      long diff = todo1.getDate(103, 0) - todo2.getDate(103, 0);
      return (int)diff;
   }

   @Override
   public boolean equals(Object o) {
      return false;
   }
}
