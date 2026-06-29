package net.rim.device.apps.internal.browser.ui;

final class SelectionPosition {
   int textPosition = -1;
   int selectedRegion = -1;

   final void reset() {
      this.textPosition = -1;
      this.selectedRegion = -1;
   }
}
