package net.rim.plazmic.internal.contentpreview.dispatcher.message;

import net.rim.plazmic.internal.contentpreview.dispatcher.DispatcherEventHandler;

public class Model {
   public static final String rcsid = "$Id: //depot/dev/pbaldwin/advancedgraphics/src/net/rim/plazmic/internal/contentpreview/message/Model.java#1 $";

   public void toEvent(DispatcherEventHandler _1) {
      throw null;
   }

   @Override
   public String toString() {
      return ((StringBuffer)(new Object())).append(this.getClassName()).append(this.getProperties()).toString();
   }

   String getClassName() {
      throw null;
   }

   String getProperties() {
      throw null;
   }

   final String toPropertyString(String name, String value) {
      return ((StringBuffer)(new Object(" "))).append(value).toString();
   }
}
