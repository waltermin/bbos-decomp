package net.rim.wica.runtime.ui.internal.component;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.wica.runtime.metadata.component.ui.UIComponent;
import net.rim.wica.runtime.metadata.component.ui.UIContainer;
import net.rim.wica.runtime.metadata.component.ui.control.RepetitionControl;
import net.rim.wica.runtime.ui.View;

public final class ComponentHelper {
   public static final void setCookies(View view, UIComponent model, int row) {
      UIComponent parent = model.getParent();
      boolean isRepetition = false;

      while (parent != null) {
         if (parent instanceof RepetitionControl) {
            isRepetition = true;
            break;
         }

         parent = parent.getParent();
      }

      if (isRepetition) {
         int count = ((RepetitionControl)parent).getRepetitionCount();
         View[] views = new View[count > 0 ? count : 1];
         model.setView(views);
         views[row] = view;
      } else {
         model.setView(view);
      }
   }

   public static final void buildLayout(ScreenContext context, Manager parentLayout, UIContainer model, int parentRow, long style) {
      UIComponent[] children = model.getChildren();
      if (children != null) {
         int numChildren = children.length;
         UIComponent child = null;
         View view = null;

         for (int i = 0; i < numChildren; i++) {
            child = children[i];
            view = ComponentFactory.getView(context, child, parentRow, style);
            setCookies(view, child, parentRow);
            if (view instanceof Field) {
               parentLayout.add((Field)view);
            }
         }
      }
   }

   public static final void updateLayout(ScreenContext context, Manager parentLayout, UIContainer container, int row) {
      UIComponent[] children = container.getChildren();
      if (children != null) {
         int numChildren = children.length;
         UIComponent child = null;

         for (int i = 0; i < numChildren; i++) {
            child = children[i];
            ((View)child.getView()).update(row);
         }
      }
   }
}
