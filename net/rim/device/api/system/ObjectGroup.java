package net.rim.device.api.system;

public final class ObjectGroup {
   private ObjectGroup() {
   }

   public static final void createGroup(Object obj) {
      if (!net.rim.vm.Memory.createGroup(obj)) {
         throw new ObjectGroupTooBigException();
      }
   }

   public static final void createGroupIgnoreTooBig(Object obj) {
      net.rim.vm.Memory.createGroup(obj);
   }

   public static final Object expandGroup(Object obj) {
      try {
         return net.rim.vm.Memory.expandGroup(obj);
      } catch (OutOfMemoryError e) {
         net.rim.vm.Memory.maximizeContiguousRAM();
         return net.rim.vm.Memory.expandGroup(obj);
      }
   }

   public static final boolean isInGroup(Object obj) {
      return net.rim.vm.Memory.isObjectInGroup(obj);
   }
}
