package net.rim.device.api.ui;

public interface FieldVisitor {
   int PRE;
   int POST;
   int LEAF;

   boolean visit(Field var1, int var2);
}
