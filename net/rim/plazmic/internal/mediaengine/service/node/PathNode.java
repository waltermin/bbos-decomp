package net.rim.plazmic.internal.mediaengine.service.node;

public interface PathNode extends VisualNode {
   int END_POINT;
   int QUADRATIC_CONTROL_POINT;
   int CUBIC_CONTROL_POINT;
   int TYPE;

   int[] getXCoordinates();

   int[] getFinalXCoordinates();

   void setXCoordinates(int[] var1);

   int[] getYCoordinates();

   int[] getFinalYCoordinates();

   void setYCoordinates(int[] var1);

   int[] getOffsets();

   void setOffsets(int[] var1);

   byte[] getPointTypes();

   void setPointTypes(byte[] var1);
}
