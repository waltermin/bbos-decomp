package net.rim.wica.common.builtindata.componentdefn;

public class BuiltinKeylessDataComponentDefinition extends DataComponentDefinition {
   @Override
   public boolean isPersistable() {
      return true;
   }

   @Override
   public boolean hasKey() {
      return false;
   }

   @Override
   public int[] getKeyFields() {
      return null;
   }
}
