package net.rim.plazmic.internal.contentpreview.dispatcher.message;

import net.rim.plazmic.internal.contentpreview.dispatcher.DispatcherEventHandler;

public final class OpenSession extends Model {
   private String _device;
   private boolean _hidden;
   public static final String rcsid;

   public OpenSession(String device, boolean hidden) {
      this._device = device;
      this._hidden = hidden;
   }

   @Override
   public final void toEvent(DispatcherEventHandler handler) {
      handler.openSession(this._device, this._hidden);
   }

   @Override
   final String getClassName() {
      return "OpenSession";
   }

   @Override
   final String getProperties() {
      return ((StringBuffer)(new Object()))
         .append(this.toPropertyString("device", this._device))
         .append(this.toPropertyString("hidden", String.valueOf(this._hidden)))
         .toString();
   }
}
