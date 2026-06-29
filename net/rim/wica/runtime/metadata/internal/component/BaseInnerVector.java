package net.rim.wica.runtime.metadata.internal.component;

public interface BaseInnerVector extends Cloneable {
   void clone(Object var1);

   Object getArrayRef();

   void setArrayRef(Object var1);

   void trimToSize();

   void removeAllElements();
}
