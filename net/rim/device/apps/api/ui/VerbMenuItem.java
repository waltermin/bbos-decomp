package net.rim.device.apps.api.ui;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.apps.api.framework.verb.Verb;

public class VerbMenuItem extends MenuItem {
   private Verb _verb;
   private Object _context;
   private Object _result;
   private AppsMainScreen _screen;

   public VerbMenuItem(Verb verb, int priority) {
      super(null, 0, verb.getOrdering(), priority);
      this._verb = verb;
   }

   public VerbMenuItem(String text, int ordinal, int priority, Verb verb, Object context) {
      super(text, ordinal, priority);
      this._verb = verb;
      this._context = context;
   }

   public Object getResult() {
      return this._result;
   }

   public Verb getVerb() {
      return this._verb;
   }

   @Override
   public void run() {
      if (this._screen != null) {
         this._result = this._screen.invokeVerb(this._verb, this._context);
      } else {
         this._result = this._verb.invoke(this._context);
      }
   }

   public void setContext(Object context) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public void setNotify(AppsMainScreen screen) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public String toString() {
      String result = super.toString();
      if (result == null) {
         result = this._verb.toString();
      }

      return result;
   }

   public boolean temporaryRunHack() {
      return false;
   }
}
