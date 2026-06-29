package net.rim.device.api.ui;

public class DrawTextParam$AdvancedDrawTextParam {
   public int iStartOffset;
   public int iBaseLine;
   public int iSupplementaryRotation;
   public int iWordSpacing;
   public boolean iOverrideWordSpacing;
   public boolean iKern;
   public boolean iAllowStartOverlap;
   public static int ALPHABETIC_BASELINE = 0;
   public static int IDEOGRAPHIC_BASELINE = 1;
   public static int HANGING_BASELINE = 2;
   public static int MATHEMATICAL_BASELINE = 3;
   public static int CENTRAL_BASELINE = 4;
   public static int MIDDLE_BASELINE = 5;
   public static int TEXT_BEFORE_EDGE_BASELINE = 6;
   public static int TEXT_AFTER_EDGE_BASELINE = 7;

   public DrawTextParam$AdvancedDrawTextParam() {
      this.reset();
   }

   public void reset() {
      this.iStartOffset = 0;
      this.iBaseLine = ALPHABETIC_BASELINE;
      this.iSupplementaryRotation = 0;
      this.iWordSpacing = 0;
      this.iOverrideWordSpacing = false;
      this.iKern = true;
      this.iAllowStartOverlap = false;
   }
}
