package net.rim.wica.common;

public class MessageDeliveryModes {
   public static final int DELIVERY_MODE_STANDARD;
   public static final int DELIVERY_MODE_RELIABLE;
   public static final int DELIVERY_MODE_BEST_EFFORT;

   public static String toString(int deliveryMode) {
      switch (deliveryMode) {
         case -1:
            return "Unknown Delivery Mode";
         case 0:
            return "DELIVERY_MODE_STANDARD";
         case 1:
            return "DELIVERY_MODE_RELIABLE";
         case 2:
         default:
            return "DELIVERY_MODE_BEST_EFFORT";
      }
   }
}
