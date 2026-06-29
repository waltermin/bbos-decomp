package net.rim.wica.runtime.provisioning.internal.elements;

import net.rim.device.api.util.Comparator;

class MessageElement$MessageElementComparator implements Comparator {
   private MessageElement$MessageElementComparator() {
   }

   @Override
   public int compare(Object o1, Object o2) {
      if (o1 instanceof MessageElement && o2 instanceof MessageElement) {
         MessageElement m1 = (MessageElement)o1;
         MessageElement m2 = (MessageElement)o2;
         return m1.getName().compareTo(m2.getName());
      } else {
         throw new ClassCastException();
      }
   }

   MessageElement$MessageElementComparator(MessageElement$1 x0) {
      this();
   }
}
