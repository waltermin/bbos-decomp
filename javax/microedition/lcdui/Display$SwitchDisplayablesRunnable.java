package javax.microedition.lcdui;

final class Display$SwitchDisplayablesRunnable implements Runnable {
   private Displayable _oldD;
   private Displayable _newD;
   private final Display this$0;

   public final void setNewDisplayable(Displayable newD) {
      this._newD = newD;
   }

   @Override
   public final void run() {
      this.this$0._switchDisplayablesRunnable = null;
      this.this$0.switchDisplayables(this._oldD, this._newD);
   }

   public Display$SwitchDisplayablesRunnable(Display _1, Displayable oldD, Displayable newD) {
      this.this$0 = _1;
      this._oldD = oldD;
      this._newD = newD;
   }
}
