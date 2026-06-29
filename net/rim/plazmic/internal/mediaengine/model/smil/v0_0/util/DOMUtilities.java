package net.rim.plazmic.internal.mediaengine.model.smil.v0_0.util;

import org.w3c.dom.Element;

public class DOMUtilities {
   public static String getAttribute(Element element, String attribute) {
      return element.hasAttribute(attribute) ? element.getAttribute(attribute) : "";
   }
}
