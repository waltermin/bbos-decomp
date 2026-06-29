package net.rim.plazmic.internal.mediaengine.service.node;

import net.rim.plazmic.internal.mediaengine.ui.ForeignObject;

public interface ForeignObjectNode extends ViewportNode {
   int TYPE;

   ForeignObject getObject();

   void setObject(ForeignObject var1);

   String getExtension();

   void setExtension(String var1);
}
