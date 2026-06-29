package net.rim.device.apps.internal.browser.wml;

final class OnEventVerb extends TaskContainer {
   private String _type;

   public OnEventVerb(String type) {
      super(340005);
      this._type = type;
   }

   public OnEventVerb(String type, Task task) {
      this(type);
      this.setTask(task);
   }

   public OnEventVerb(OnEventVerb onEventVerb) {
      super(onEventVerb);
      this._type = onEventVerb._type;
   }

   public final String getType() {
      return this._type;
   }

   public final TaskContainer clone() {
      return new OnEventVerb(this);
   }
}
