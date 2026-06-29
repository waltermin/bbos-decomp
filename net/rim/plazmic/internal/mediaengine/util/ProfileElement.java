package net.rim.plazmic.internal.mediaengine.util;

public class ProfileElement {
   protected String _name;

   ProfileElement(String name) {
      this._name = name;
      this.initialize();
   }

   public String getName() {
      return this._name;
   }

   public void reset() {
   }

   public void initialize() {
   }
}
