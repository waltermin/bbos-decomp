package net.rim.wica.runtime.metadata.component.ui;

public interface UIComponent {
   int INIT_EVENT;
   int SHOW_EVENT;
   int FOCUS_OUT_EVENT;
   int CHANGE_EVENT;
   int CLICK_EVENT;
   int FETCH_MORE_EVENT;
   int _EVENT_COUNT;

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
