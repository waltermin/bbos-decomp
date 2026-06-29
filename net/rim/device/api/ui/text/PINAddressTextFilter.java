package net.rim.device.api.ui.text;

class PINAddressTextFilter extends HexadecimalTextFilter {
   public PINAddressTextFilter() {
      this.setMaxLength(8);
   }

   @Override
   public long getPreferredInputStyle() {
      return 1224736768;
   }
}
