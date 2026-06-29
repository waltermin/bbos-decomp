package net.rim.wica.runtime.metadata.component.ui;

public interface UIContainer extends UIComponent {
   int LAYOUT_FLOW;
   int LAYOUT_VERTICAL;
   int LAYOUT_GRID;
   int LAYOUT_EDGE;

   UIComponent[] getChildren();

   int getLayout();
}
