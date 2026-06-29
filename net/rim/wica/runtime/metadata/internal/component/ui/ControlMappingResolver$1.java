package net.rim.wica.runtime.metadata.internal.component.ui;

import net.rim.device.api.util.Comparator;

class ControlMappingResolver$1 implements Comparator {
   private final ControlMappingResolver this$0;

   ControlMappingResolver$1(ControlMappingResolver this$0) {
      this.this$0 = this$0;
   }

   @Override
   public int compare(Object obj1, Object obj2) {
      return ((UIControlImpl)obj1)._mapping.length - ((UIControlImpl)obj2)._mapping.length;
   }

   @Override
   public boolean equals(Object obj) {
      return this == obj;
   }
}
