package net.rim.wica.runtime.metadata.component.ui;

public interface UIContainer extends UIComponent {
   int LAYOUT_FLOW = 1;
   int LAYOUT_VERTICAL = 2;
   int LAYOUT_GRID = 3;
   int LAYOUT_EDGE = 4;

   UIComponent[] getChildren();

   int getLayout();
}
