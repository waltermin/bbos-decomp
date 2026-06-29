package net.rim.device.api.ui;

class Screen$ScreenFieldVisitor implements FieldVisitor {
   private int _type;
   private final Screen this$0;

   Screen$ScreenFieldVisitor(Screen _1, int type) {
      this.this$0 = _1;
      this._type = type;
   }

   @Override
   public boolean visit(Field hostField, int action) {
      if (action != 1) {
         return true;
      }

      switch (this._type) {
         case 0:
            this.this$0.assertLayoutComplete();
         default:
            return true;
      }
   }
}
