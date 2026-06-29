package net.rim.device.apps.api.utility.framework;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Vector;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;

public class FieldManagerEnumeration implements Enumeration {
   Vector _managers = new Vector();

   public FieldManagerEnumeration(Manager baseManager) {
      if (baseManager.getFieldCount() > 0) {
         this._managers.addElement(new FieldManagerEnumeration$ManagerStackEntry(baseManager));
      }
   }

   @Override
   public boolean hasMoreElements() {
      return this._managers.size() > 0;
   }

   @Override
   public Object nextElement() {
      for (int size = this._managers.size(); size > 0; size--) {
         FieldManagerEnumeration$ManagerStackEntry currentFrame = (FieldManagerEnumeration$ManagerStackEntry)this._managers.elementAt(size - 1);
         Field nextField = null;

         while (currentFrame._countWithinCurrentManager < currentFrame._manager.getFieldCount()) {
            nextField = currentFrame._manager.getField(currentFrame._countWithinCurrentManager++);
            if (!(nextField instanceof Manager)) {
               return nextField;
            }

            Manager manager = (Manager)nextField;
            if (manager.getFieldCount() > 0) {
               currentFrame = new FieldManagerEnumeration$ManagerStackEntry(manager);
               this._managers.addElement(currentFrame);
            }
         }

         this._managers.removeElementAt(size - 1);
      }

      throw new NoSuchElementException();
   }
}
