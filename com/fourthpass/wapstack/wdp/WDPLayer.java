package com.fourthpass.wapstack.wdp;

import com.fourthpass.wapstack.IPacketTransiver;
import com.fourthpass.wapstack.IWapStackLayer;
import com.fourthpass.wapstack.bearer.IBearer;

public final class WDPLayer implements IWapStackLayer, IPacketTransiver {
   private IBearer _currentBearer;

   public final IWapStackLayer getSubmissionLayer() {
      return null;
   }

   public final void discard(WDPPacket packet) {
   }

   @Override
   public final void eventOccured(Object event) {
   }

   @Override
   public final void close() {
      this._currentBearer.closeConnection();
   }

   @Override
   public final int send(WDPPacket packet) {
      return this._currentBearer.send(packet);
   }

   @Override
   public final int receive(WDPPacket packet) {
      int recvCount = this._currentBearer.receive(packet);
      if (recvCount == -1) {
         return -1;
      } else {
         return recvCount == 0 ? 0 : recvCount;
      }
   }

   @Override
   public final void setUserLayer(IWapStackLayer userLayer) {
   }

   @Override
   public final boolean isSecure() {
      return false;
   }

   @Override
   public final boolean isClosed() {
      return this._currentBearer.isClosed();
   }

   @Override
   public final void setReceivingTimeout(int milliseconds) {
      this._currentBearer.setReceivingTimeout(milliseconds);
   }

   public WDPLayer(IBearer bearer) {
      this._currentBearer = bearer;
   }
}
