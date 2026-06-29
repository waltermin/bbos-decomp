package net.rim.device.apps.internal.ribbon.skin.svg.layout;

import java.util.Hashtable;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.ModelInteractorImpl;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.NodeImpl;
import net.rim.plazmic.internal.mediaengine.ui.LayoutManager;

class LayoutManagerFactory {
   public static LayoutManager createInstance(ModelInteractorImpl model, Hashtable params) {
      String value = (String)params.get("ids");
      if (value != null) {
         int delim = value.indexOf(59);
         int handle1 = model.getHandle(value.substring(0, delim));
         int handle2 = model.getHandle(value.substring(delim + 1));
         if (NodeImpl.getType(handle1, model) == 50 && NodeImpl.getType(handle2, model) == 50) {
            return new TwoStringLayout(model, params);
         }

         if (NodeImpl.getType(handle1, model) == 42 && NodeImpl.getType(handle2, model) == 50) {
            return new ImageStringLayout(model, params);
         }
      }

      return null;
   }
}
