package net.rim.plazmic.internal.mediaengine.model.intarray.v1_2;

import net.rim.plazmic.internal.mediaengine.MediaServices;
import net.rim.plazmic.internal.mediaengine.service.ModelInteractor;
import net.rim.plazmic.mediaengine.MediaListener;

class ActionHandler implements MediaListener {
   ModelInteractor _model;
   MediaServices _services;

   protected void doHyperlink(int nodeHandle) {
      this._model.trigger(100, nodeHandle, null);
      AnimationModel data = (AnimationModel)this._services.getMedia();
      int hyperlinkURLIndex = data._nodes[nodeHandle + 10];
      this._model.notify(7, nodeHandle, data._hyperlinks[hyperlinkURLIndex]);
      this._model.trigger(101, nodeHandle, null);
   }

   protected void doCustomAction(int nodeHandle) {
      this._model.trigger(100, nodeHandle, null);
      AnimationModel data = (AnimationModel)this._services.getMedia();
      Object customNode = null;
      int customNodeHandle = data._nodes[nodeHandle + 12];
      if (customNodeHandle != -1) {
         int objectIndex = data._nodes[customNodeHandle + 9];
         customNode = objectIndex != -1 ? data._customObjects[objectIndex] : null;
      }

      String nodeID = data.getId(nodeHandle);
      if (nodeID == null) {
         throw new NullPointerException("No ID found for node handle");
      }

      if (!(customNode instanceof MediaListener)) {
         this._model.notify(data._nodes[nodeHandle + 10], nodeHandle, nodeID);
      } else {
         ((MediaListener)customNode).mediaEvent(this._model, data._nodes[nodeHandle + 10], nodeHandle, nodeID);
      }

      this._model.trigger(101, nodeHandle, null);
   }

   @Override
   public void mediaEvent(Object sender, int event, int eventParam, Object data) {
      switch (event) {
         case 206:
            this.doCustomAction(eventParam);
            return;
         case 209:
            this.doHyperlink(eventParam);
      }
   }

   public ActionHandler(MediaServices s) {
      this._services = s;
      this._model = (ModelInteractor)s.getService("ModelInteractor");
   }
}
