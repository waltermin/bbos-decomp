package net.rim.wica.runtime.metadata.component.ui;

public interface UIComponent {
   int INIT_EVENT = Integer.MAX_VALUE;
   int SHOW_EVENT = 2147483646;
   int FOCUS_OUT_EVENT = 0;
   int CHANGE_EVENT = 1;
   int CLICK_EVENT = 2;
   int FETCH_MORE_EVENT = 3;
   int _EVENT_COUNT = 4;

   int getId();

   UIContainer getParent();

   ScreenModel getScreen();

   int getType();

   Object getView();

   void setView(Object var1);

   boolean hasPlacement();

   int getX();

   int getY();

   int getStyle();

   boolean isRepetition();

   boolean isVisible();

   void setVisible(boolean var1);
}
