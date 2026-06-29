package net.rim.device.apps.internal.phone.data;

import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.apps.api.framework.verb.Verb;

final class PhoneCallModelImpl$DoSomethingToThePhoneCallModelVerb extends Verb {
   private int _whatToDo;
   private final PhoneCallModelImpl this$0;
   public static final int OPEN = 0;
   public static final int MARK_OPENED = 1;
   public static final int MARK_UNOPENED = 2;
   public static final int FORWARD = 3;

   PhoneCallModelImpl$DoSomethingToThePhoneCallModelVerb(PhoneCallModelImpl _1, int whatToDo, int ordering) {
      super(ordering);
      this.this$0 = _1;
      this._whatToDo = whatToDo;
   }

   public PhoneCallModelImpl$DoSomethingToThePhoneCallModelVerb(PhoneCallModelImpl _1, int whatToDo, int ordering, ResourceBundleFamily rb, int rbKey) {
      super(ordering, rb, rbKey);
      this.this$0 = _1;
      this._whatToDo = whatToDo;
   }

   @Override
   public final Object invoke(Object parameter) {
      switch (this._whatToDo) {
         case 0:
         default:
            PhoneCallModelImpl.viewCallLog(this.this$0, parameter);
            return null;
         case 1:
            this.this$0.perform(5803508244060051872L, null);
            return null;
         case 2:
            this.this$0.perform(-8629311385729242560L, null);
            return null;
         case 3:
            this.this$0.forwardCallLog();
         case -1:
            return null;
      }
   }
}
