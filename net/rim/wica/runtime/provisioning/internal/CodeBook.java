package net.rim.wica.runtime.provisioning.internal;

public final class CodeBook {
   private static String[] wicletTags = new String[]{
      "wiclet",
      "desc",
      "dependency",
      "resource",
      "global",
      "enumeration",
      "value",
      "data",
      "field",
      "message",
      "mappedField",
      "alert",
      "style",
      "screen",
      "region",
      "repetition",
      "condition",
      "var",
      "onShow",
      "onInit",
      "onChange",
      "onFocusOut",
      "onClick",
      "menu",
      "menuItem",
      "separator",
      "label",
      "edit",
      "textarea",
      "image",
      "singleChoice",
      "multiChoice",
      "button",
      "checkbox",
      "script",
      "param",
      "notification"
   };
   private static String[] wicletAttribs = new String[]{
      "name",
      "uri",
      "entry",
      "vendor",
      "version",
      "size",
      "icon",
      "persistence",
      "messageDelivery",
      "type",
      "value",
      "url",
      "mimeType",
      "component",
      "array",
      "default",
      "persist",
      "prototype",
      "key",
      "script",
      "secure",
      "mapping",
      "beep",
      "ribbon",
      "dialogText",
      "font",
      "fgColor",
      "bgColor",
      "bold",
      "italic",
      "underline",
      "title",
      "dialog",
      "layout",
      "style",
      "bgImage",
      "collapsible",
      "refreshMsg",
      "placement",
      "params",
      "onTrue",
      "transition",
      "transaction",
      "inValue",
      "isWhitespace",
      "readOnly",
      "mandatory",
      "format",
      "visible",
      "visibleRows",
      "resource",
      "imageValue",
      "backgroundProcessing",
      "keepLast",
      "hoverIcon"
   };

   public final String[] getTags() {
      return wicletTags;
   }

   public final String[] getAttribs() {
      return wicletAttribs;
   }

   public final String[] getValues() {
      return null;
   }
}
