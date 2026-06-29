package javax.microedition.lcdui;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYRect;

public class Displayable {
   private MIDPScreen _peer;

   Displayable(MIDPScreen peer) {
      this._peer = peer;
   }

   final MIDPScreen getPeer() {
      return this._peer;
   }

   final void setPeer(MIDPScreen peer) {
      this._peer = peer;
   }

   public String getTitle() {
      synchronized (Application.getEventLock()) {
         return this.getPeer().getTitle();
      }
   }

   public void setTitle(String s) {
      synchronized (Application.getEventLock()) {
         int oldHeight = this.getPeer().getDisplayableAreaExtent().height;
         this.getPeer().setTitle(s);
         int newHeight = this.getPeer().getDisplayableAreaExtent().height;
         if (oldHeight != newHeight) {
            this.sizeChanged(newHeight, this.getWidth());
         }
      }
   }

   public Ticker getTicker() {
      synchronized (Application.getEventLock()) {
         return this.getPeer().getTicker();
      }
   }

   public void setTicker(Ticker ticker) {
      synchronized (Application.getEventLock()) {
         int oldHeight = this.getPeer().getDisplayableAreaExtent().height;
         this.getPeer().setTicker(ticker);
         int newHeight = this.getPeer().getDisplayableAreaExtent().height;
         if (oldHeight != newHeight) {
            this.sizeChanged(newHeight, this.getWidth());
         }
      }
   }

   public boolean isShown() {
      synchronized (Application.getEventLock()) {
         UiApplication app = UiApplication.getUiApplication();
         return !app.isForeground() ? false : this.getPeer() == app.getActiveScreen();
      }
   }

   public void addCommand(Command cmd) {
      synchronized (Application.getEventLock()) {
         this.getPeer().addCommand(cmd);
      }
   }

   public void removeCommand(Command cmd) {
      synchronized (Application.getEventLock()) {
         this.getPeer().removeCommand(cmd);
      }
   }

   public void setCommandListener(CommandListener l) {
      synchronized (Application.getEventLock()) {
         this.getPeer().setCommandListener(l);
      }
   }

   public int getWidth() {
      MIDPScreen peer = this.getPeer();
      return peer != null ? peer.getWidth() : 0;
   }

   public int getHeight() {
      MIDPScreen peer = this.getPeer();
      if (peer != null) {
         XYRect displayableArea = peer.getDisplayableAreaExtent();
         if (displayableArea != null) {
            return displayableArea.height;
         }
      }

      return 0;
   }

   protected void sizeChanged(int w, int h) {
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      MMAPIConnectorImpl connector = (MMAPIConnectorImpl)ar.getOrWaitFor(-79929455318757284L);
      if (connector == null) {
         connector = new MMAPIConnectorImpl();
         ar.put(-79929455318757284L, connector);
      }
   }
}
