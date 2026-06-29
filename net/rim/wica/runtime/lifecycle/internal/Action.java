package net.rim.wica.runtime.lifecycle.internal;

class Action {
   private String _description;

   protected Action(String description) {
      this._description = description;
   }

   public String getDescription() {
      return this._description;
   }

   public Object invoke(Object _1) {
      throw null;
   }

   @Override
   public String toString() {
      return this.getDescription();
   }
}
