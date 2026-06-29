package org.w3c.dom.html2;

import org.w3c.dom.Node;

public interface HTMLOptionsCollection {
   int getLength();

   void setLength(int var1);

   Node item(int var1);

   Node namedItem(String var1);
}
