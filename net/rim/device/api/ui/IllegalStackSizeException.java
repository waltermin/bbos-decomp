package net.rim.device.api.ui;

public class IllegalStackSizeException extends RuntimeException {
   public IllegalStackSizeException() {
      super("Unpaired push / pop of context stack.");
   }

   public IllegalStackSizeException(String message) {
      super(message);
   }

   public IllegalStackSizeException(String type, Class clazz, Graphics graphics, int depthExpected) {
      super(unpairedMessage(type, clazz, graphics, depthExpected));
   }

   private static String unpairedMessage(String type, Class clazz, Graphics graphics, int depthExpected) {
      XYRect clip = graphics.getClippingRect();
      return "Unpaired Graphics."
         + type
         + " in class "
         + clazz.getName()
         + " actual "
         + graphics.getContextStackSize()
         + "!= expected "
         + depthExpected
         + " clip ("
         + clip.x
         + ','
         + clip.y
         + ")+("
         + clip.width
         + ','
         + clip.height
         + ") @("
         + graphics.getTranslateX()
         + ','
         + graphics.getTranslateY();
   }
}
