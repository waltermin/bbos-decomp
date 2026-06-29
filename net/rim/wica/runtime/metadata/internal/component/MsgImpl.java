package net.rim.wica.runtime.metadata.internal.component;

import net.rim.wica.runtime.metadata.component.Msg;
import net.rim.wica.runtime.metadata.internal.WicletEx;
import net.rim.wica.runtime.metadata.internal.def.MsgDefAccess;
import net.rim.wica.runtime.metadata.internal.handler.MsgHandler;

public class MsgImpl extends ComponentImpl implements Msg {
   private MsgHandler _msgHandler;
   private String _msgName;
   private int _msgCode;
   private int _scriptId;
   static Class class$net$rim$wica$runtime$metadata$internal$handler$MsgHandler;

   public MsgImpl(int defId, WicletEx wiclet, MsgDefAccess defs) {
      super(defId, wiclet, defs);
      this.createDefaults();
      this._msgName = defs.getMsgName(defId);
      this._msgCode = defs.getMsgCode(defId);
      this._scriptId = defs.getScript(defId);
      this._msgHandler = (MsgHandler)wiclet.getRuntime()
         .getService(
            class$net$rim$wica$runtime$metadata$internal$handler$MsgHandler == null
               ? (class$net$rim$wica$runtime$metadata$internal$handler$MsgHandler = class$("net.rim.wica.runtime.metadata.internal.handler.MsgHandler"))
               : class$net$rim$wica$runtime$metadata$internal$handler$MsgHandler
         );
   }

   @Override
   public int getMsgCode() {
      return this._msgCode;
   }

   @Override
   public String getMsgName() {
      return this._msgName;
   }

   @Override
   public int getScript() {
      return this._scriptId;
   }

   @Override
   public int[] getFieldMapping(int fieldId) {
      return ((MsgDefAccess)super._defs).getFieldMapping(super._defId, fieldId);
   }

   @Override
   public boolean isSecure() {
      return ((MsgDefAccess)super._defs).isSecure(super._defId);
   }

   @Override
   public void send() {
      if (this._msgHandler != null) {
         this._msgHandler.postMsg(this, null);
      } else {
         throw new Object();
      }
   }

   @Override
   public void send(String destination) {
      if (this._msgHandler != null) {
         this._msgHandler.postMsg(this, destination);
      } else {
         throw new Object();
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new Object(x1.getMessage());
      }
   }
}
