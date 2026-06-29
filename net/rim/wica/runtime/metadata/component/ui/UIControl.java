package net.rim.wica.runtime.metadata.component.ui;

public interface UIControl extends UIComponent {
   long getValueType();

   Object getValue();

   void setValue(Object var1, boolean var2);

   void initializeToEmpty(Object var1);

   void eventOccurred(int var1);

   boolean isReadOnly();

   boolean isMandatory();

   boolean isMandatorySatisfied();

   long getMappedValueType();

   Object getMappedValue();
}
