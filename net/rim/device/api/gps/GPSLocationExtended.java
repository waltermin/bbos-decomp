package net.rim.device.api.gps;

public class GPSLocationExtended extends GPSLocationStandard {
   private int _heading;
   private int _velocity;
   private int _velocityUncertainty;
   private int _altitude;
   private int _altitudeUncertainty;
   private int _numSatViewed;
   private int _numSatTracked;
   private byte[] _satPRN;
   private byte[] _satSignalStrength;
   private static final int MAX_NUM_SATELLITES;

   public GPSLocationExtended() {
      this.reset();
   }

   @Override
   public void reset() {
      super.reset();
      this._heading = 0;
      this._velocity = 0;
      this._velocityUncertainty = 0;
      this._altitude = 0;
      this._altitudeUncertainty = 0;
      this._numSatViewed = 0;
      this._numSatTracked = 0;
      this._satPRN = new byte[12];
      this._satSignalStrength = new byte[12];
   }

   public float getHeading() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IllegalStateException: No common supertype for ternary expression
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.getExprType(FunctionExprent.java:224)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.checkExprTypeBounds(FunctionExprent.java:372)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.checkTypeExpr(VarTypeProcessor.java:156)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.checkTypeExprent(VarTypeProcessor.java:132)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.lambda$processVarTypes$2(VarTypeProcessor.java:125)
      //   at org.jetbrains.java.decompiler.modules.decompiler.flow.DirectGraph.iterateExprents(DirectGraph.java:114)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.processVarTypes(VarTypeProcessor.java:125)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.calculateVarTypes(VarTypeProcessor.java:44)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarVersionsProcessor.setVarVersions(VarVersionsProcessor.java:68)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarProcessor.setVarVersions(VarProcessor.java:47)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:241)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/api/gps/GPSLocationExtended._heading I
      // 04: bipush -1
      // 06: iand
      // 07: bipush -1
      // 09: if_icmpne 12
      // 0c: nop
      // 0d: ldc_w 2143289344
      // 10: nop
      // 11: freturn
      // 12: aload 0
      // 13: getfield net/rim/device/api/gps/GPSLocationExtended._heading I
      // 16: i2f
      // 17: nop
      // 18: ldc_w 1232348160
      // 1b: fdiv
      // 1c: nop
      // 1d: freturn
   }

   public float getVelocity() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IllegalStateException: No common supertype for ternary expression
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.getExprType(FunctionExprent.java:224)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.checkExprTypeBounds(FunctionExprent.java:372)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.checkTypeExpr(VarTypeProcessor.java:156)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.checkTypeExprent(VarTypeProcessor.java:132)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.lambda$processVarTypes$2(VarTypeProcessor.java:125)
      //   at org.jetbrains.java.decompiler.modules.decompiler.flow.DirectGraph.iterateExprents(DirectGraph.java:114)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.processVarTypes(VarTypeProcessor.java:125)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.calculateVarTypes(VarTypeProcessor.java:44)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarVersionsProcessor.setVarVersions(VarVersionsProcessor.java:68)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarProcessor.setVarVersions(VarProcessor.java:47)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:241)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/api/gps/GPSLocationExtended._velocity I
      // 04: ldc_w 65535
      // 07: iand
      // 08: ldc_w 65535
      // 0b: if_icmpne 14
      // 0e: nop
      // 0f: ldc_w 2143289344
      // 12: nop
      // 13: freturn
      // 14: aload 0
      // 15: getfield net/rim/device/api/gps/GPSLocationExtended._velocity I
      // 18: i2f
      // 19: nop
      // 1a: ldc_w 1120403456
      // 1d: fdiv
      // 1e: nop
      // 1f: freturn
   }

   public int getVelocityUncertainty() {
      return this._velocityUncertainty;
   }

   public int getAltitude() {
      int sign = (this._altitude & 32768) == 0 ? -1 : 1;
      int value = this._altitude & 32767;
      return sign * value;
   }

   public double getVerticalUncertainity() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IllegalStateException: No common supertype for ternary expression
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.getExprType(FunctionExprent.java:224)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.checkExprTypeBounds(FunctionExprent.java:372)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.checkTypeExpr(VarTypeProcessor.java:156)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.checkTypeExprent(VarTypeProcessor.java:132)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.lambda$processVarTypes$2(VarTypeProcessor.java:125)
      //   at org.jetbrains.java.decompiler.modules.decompiler.flow.DirectGraph.iterateExprents(DirectGraph.java:114)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.processVarTypes(VarTypeProcessor.java:125)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.calculateVarTypes(VarTypeProcessor.java:44)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarVersionsProcessor.setVarVersions(VarVersionsProcessor.java:68)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarProcessor.setVarVersions(VarProcessor.java:47)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:241)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/api/gps/GPSLocationExtended._altitudeUncertainty I
      // 04: bipush -1
      // 06: iand
      // 07: bipush -1
      // 09: if_icmpne 12
      // 0c: nop
      // 0d: ldc2_w 9221120237041090560
      // 10: nop
      // 11: dreturn
      // 12: aload 0
      // 13: getfield net/rim/device/api/gps/GPSLocationExtended._altitudeUncertainty I
      // 16: i2d
      // 17: nop
      // 18: ldc2_w 4636737291354636288
      // 1b: ddiv
      // 1c: nop
      // 1d: dreturn
   }

   public int getNumberOfSatellitesVisible() {
      return this._numSatViewed;
   }

   public int getNumberOfSatellitesTracked() {
      return this._numSatTracked;
   }

   public int getSatellitePRN(int index) {
      return this._satPRN[index] & 0xFF;
   }

   public int getSatelliteSignalStrength(int index) {
      return this._satSignalStrength[index] & 0xFF;
   }
}
