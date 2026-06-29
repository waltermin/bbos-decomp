package net.rim.device.apps.internal.options;

class CustomDictUnitEditor$1 implements Runnable {
   private final CustomDictUnitEditor this$0;

   CustomDictUnitEditor$1(CustomDictUnitEditor _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      ((CustomDictUnitEditor$CustomDictEditorScreen)this.this$0._mainScreen).makeComposed();
   }
}
