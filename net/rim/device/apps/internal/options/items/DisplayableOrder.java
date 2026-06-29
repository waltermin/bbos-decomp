package net.rim.device.apps.internal.options.items;

final class DisplayableOrder {
   private int _order;
   private String _displayString;

   public DisplayableOrder(int order) {
      this._order = order;
   }

   public DisplayableOrder(int order, String displayString) {
      this(order);
      this._displayString = displayString;
   }

   public final int getOrder() {
      return this._order;
   }

   @Override
   public final String toString() {
      return this._displayString != null ? this._displayString : "Order by:";
   }
}
