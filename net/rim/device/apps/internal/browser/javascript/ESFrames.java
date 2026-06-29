package net.rim.device.apps.internal.browser.javascript;

import net.rim.device.apps.internal.browser.util.Frame;
import net.rim.ecmascript.runtime.GlobalObject;
import net.rim.ecmascript.runtime.RedirectedObject;
import net.rim.ecmascript.runtime.Value;

public final class ESFrames extends RedirectedObject {
   private ESWindow _window;
   private Frame _frame;

   public ESFrames(ESWindow window, Frame frame) {
      super(Names.WindowCollection, GlobalObject.getInstance().getObjectPrototype());
      this._window = window;
      this._frame = frame;
   }

   public final Frame getFrame() {
      return this._frame;
   }

   public final ESWindow getWindow() {
      return this._window;
   }

   @Override
   public final long requestFieldValue(String name) {
      return name == Names.length ? Value.makeIntegerValue(this._frame != null ? this._frame.length() : 0) : Value.DEFAULT;
   }

   @Override
   public final long requestElementValue(long element) {
      switch (Value.getType(element)) {
         case 0:
            if (this._frame != null) {
               Frame[] frames = this._frame.getChildren();
               if (frames != null) {
                  int index = Value.getIntegerValue(element);
                  if (index >= 0 && index < frames.length) {
                     return Value.makeObjectValue(frames[index].getESFrames().getWindow());
                  }
               }
            }
         default:
            return Value.UNDEFINED;
         case 5:
            return this.getChildFrame(Value.getStringValue(element));
      }
   }

   final long getChildFrame(String frameName) {
      if (this._frame != null && frameName != null) {
         Frame childFrame = this._frame.getChild(frameName);
         if (childFrame != null) {
            return Value.makeObjectValue(childFrame.getESFrames().getWindow());
         }
      }

      return Value.UNDEFINED;
   }
}
