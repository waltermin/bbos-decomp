package net.rim.wica.runtime.ui;

import net.rim.wica.runtime.metadata.component.ui.UIComponent;

public interface View {
   UIComponent getModel();

   void setModel(UIComponent var1);

   void setVisibility(byte var1);

   void update(int var1);
}
