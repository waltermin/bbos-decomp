package net.rim.device.apps.internal.help;

final class HistoryNode {
   private String _url;
   private int _scrollPosition;
   private int _focusPosition;

   public HistoryNode(String url, int scrollPosition, int focusPosition) {
      this._url = url;
      this._scrollPosition = scrollPosition;
      this._focusPosition = focusPosition;
   }

   public final String getURL() {
      return this._url;
   }

   public final int getScrollPosition() {
      return this._scrollPosition;
   }

   public final int getFocusPosition() {
      return this._focusPosition;
   }
}
