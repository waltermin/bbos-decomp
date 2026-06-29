package net.rim.device.apps.internal.commonmodels.body;

import net.rim.device.apps.api.framework.model.PersistableRIMModel;

public interface BodyModel extends PersistableRIMModel {
   void setText(String var1);

   String getText();

   boolean isTextOpaque();

   void setTextEncoding(Object var1);

   Object getTextEncoding();
}
