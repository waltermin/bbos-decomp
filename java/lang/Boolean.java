package java.lang;

public final class Boolean {
   private boolean value;
   public static final Boolean TRUE = new Boolean(true);
   public static final Boolean FALSE = new Boolean(false);

   public Boolean(boolean value) {
      this.value = value;
   }

   public final boolean booleanValue() {
      return this.value;
   }

   @Override
   public final String toString() {
      return this.value ? "true" : "false";
   }

   @Override
   public final int hashCode() {
      return this.value ? 1231 : 1237;
   }

   @Override
   public final boolean equals(Object obj) {
      return !(obj instanceof Boolean) ? false : ((Boolean)obj).value == this.value;
   }
}
