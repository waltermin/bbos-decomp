package net.rim.device.apps.internal.ribbon.skin.svg;

import java.util.TimeZone;
import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.RealtimeClockListener;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.cldc.util.GregorianCalendar;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.ModelInteractorImpl;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.PathNodeImpl;
import net.rim.plazmic.internal.mediaengine.ui.AbstractForeignObject;

class DayNightCurveForeignObject extends AbstractForeignObject implements GlobalEventListener, RealtimeClockListener {
   private long _nextShiftTime = Long.MIN_VALUE;
   private long _nextCalcTime = Long.MIN_VALUE;
   private int _curveMinX;
   private int _curveMaxX;
   private int _curveMinY;
   private int _curveMaxY;
   private int _curveMinXFP;
   private int _curveMaxXFP;
   private int _curveMinYFP;
   private int _curveMaxYFP;
   private int _mapLeftPix;
   private int _mapRightPix;
   private int _mapTopPix;
   private int _mapBottomPix;
   private int _mapLeftPixFP;
   private int _mapTopPixFP;
   private int _mapWidthPix;
   private int _mapHeightPix;
   private int _mapWidthPixFP;
   private int _mapHeightPixFP;
   private int _mapLeftDeg;
   private int _mapRightDeg;
   private int _mapTopDeg;
   private int _mapBottomDeg;
   private int _mapTopRadFP;
   private int _mapBottomRadFP;
   private int _mapLeftRadFP;
   private int _mapRightRadFP;
   private int _mapWidthRadFP;
   private int _mapHeightRadFP;
   private int _fullWidthPix;
   private int _numPts;
   protected ModelInteractorImpl _modelInteractor;
   private boolean _vertical;
   private int[] _xCoordsVertical = new int[4];
   private int[] _yCoordsVertical = new int[4];
   private int[] _xCoordsVerticalCur = new int[8];
   private int[] _yCoordsVerticalCur = new int[8];
   private int[] _xCoords;
   private int[] _yCoords;
   private int[] _xCoordsCur;
   private int[] _yCoordsCur;
   private int _handle1;
   int _C;
   private static GregorianCalendar _cal = (GregorianCalendar)(new Object());
   private static final int MAX_C;
   private static final int MIN_1_OVER_C;
   private static final int MAX_THETA_32 = Fixed32.tenThouToFP(4102);
   private static final int COS_PI_2__MAX_THETA_32 = Fixed32.Cos(102943 - MAX_THETA_32);
   private static final int SHIFT_INTERVAL;

   void setHandle2(int h) {
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 8877632280522743328L) {
         this.calculateCurve();
         this.positionCurve();
         this.setPath();
      }
   }

   @Override
   public void clockUpdated() {
      long curTime = System.currentTimeMillis();
      if (curTime > this._nextCalcTime) {
         this.calculateCurve();
         this.positionCurve();
         this.setPath();
         this._nextShiftTime = curTime + 600000;
         this._nextCalcTime = curTime + 86400000;
      } else {
         if (curTime > this._nextShiftTime) {
            this.positionCurve();
            this.setPath();
            this._nextShiftTime = curTime + 600000;
         }
      }
   }

   private void calculateVerticalCurve() {
      int xMin = this.radXToPixelX(-102943);
      int xMax = this.radXToPixelX(102943);
      this._xCoordsVertical[0] = xMin;
      this._xCoordsVertical[1] = xMin;
      this._xCoordsVertical[2] = xMax;
      this._xCoordsVertical[3] = xMax;
      this._yCoordsVertical[0] = this._yCoordsVerticalCur[0] = this._yCoordsVertical[3] = this._yCoordsVerticalCur[3] = this._yCoordsVerticalCur[4] = this._yCoordsVerticalCur[7] = this._curveMaxYFP;
      this._yCoordsVertical[1] = this._yCoordsVerticalCur[1] = this._yCoordsVertical[2] = this._yCoordsVerticalCur[2] = this._yCoordsVerticalCur[5] = this._yCoordsVerticalCur[6] = this._curveMinYFP;
   }

   private void initializeDimensions() {
      this._fullWidthPix = (this._mapRightPix - this._mapLeftPix) * 360 / (this._mapRightDeg - this._mapLeftDeg);
      this._numPts = this._curveMaxX - this._curveMinX;
      this._xCoords = new int[this._numPts + 2];
      this._yCoords = new int[this._numPts + 2];
      this._xCoordsCur = new int[this._numPts + 2];
      this._yCoordsCur = new int[this._numPts + 2];
      this._curveMinXFP = Fixed32.toFP(this._curveMinX);
      this._curveMaxXFP = Fixed32.toFP(this._curveMaxX);
      this._curveMinYFP = Fixed32.toFP(this._curveMinY);
      this._curveMaxYFP = Fixed32.toFP(this._curveMaxY);
      this._mapLeftPixFP = Fixed32.toFP(this._mapLeftPix);
      this._mapTopPixFP = Fixed32.toFP(this._mapTopPix);
      this._mapWidthPix = this._mapRightPix - this._mapLeftPix;
      this._mapHeightPix = this._mapBottomPix - this._mapTopPix;
      this._mapWidthPixFP = Fixed32.toFP(this._mapWidthPix);
      this._mapHeightPixFP = Fixed32.toFP(this._mapHeightPix);
      this._mapTopRadFP = Fixed32.degToRad(Fixed32.toFP(this._mapTopDeg));
      this._mapBottomRadFP = Fixed32.degToRad(Fixed32.toFP(this._mapBottomDeg));
      this._mapLeftRadFP = Fixed32.degToRad(Fixed32.toFP(this._mapLeftDeg));
      this._mapRightRadFP = Fixed32.degToRad(Fixed32.toFP(this._mapRightDeg));
      this._mapWidthRadFP = this._mapRightRadFP - this._mapLeftRadFP;
      this._mapHeightRadFP = this._mapTopRadFP - this._mapBottomRadFP;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void readParams(String query) {
      StringTokenizer tokenizer = (StringTokenizer)(new Object(query, '&'));

      while (tokenizer.hasMoreTokens()) {
         String nameValuePair = tokenizer.nextToken();
         int equalsIndex = nameValuePair.indexOf(61);
         String name;
         String value;
         if (equalsIndex < 0) {
            name = nameValuePair.trim();
            value = "";
         } else {
            name = nameValuePair.substring(0, equalsIndex).trim();
            value = nameValuePair.substring(equalsIndex + 1).trim();
         }

         boolean var9 = false /* VF: Semaphore variable */;

         try {
            var9 = true;
            if (name.equals("id")) {
               this._handle1 = this._modelInteractor.getHandle(value);
               var9 = false;
            } else if (name.equals("curveMinX")) {
               this._curveMinX = Integer.parseInt(value);
               var9 = false;
            } else if (name.equals("curveMaxX")) {
               this._curveMaxX = Integer.parseInt(value);
               var9 = false;
            } else if (name.equals("curveMinY")) {
               this._curveMinY = Integer.parseInt(value);
               var9 = false;
            } else if (name.equals("curveMaxY")) {
               this._curveMaxY = Integer.parseInt(value);
               var9 = false;
            } else if (name.equals("mapLeftPix")) {
               this._mapLeftPix = Integer.parseInt(value);
               var9 = false;
            } else if (name.equals("mapLeftDeg")) {
               this._mapLeftDeg = Integer.parseInt(value);
               var9 = false;
            } else if (name.equals("mapRightPix")) {
               this._mapRightPix = Integer.parseInt(value);
               var9 = false;
            } else if (name.equals("mapRightDeg")) {
               this._mapRightDeg = Integer.parseInt(value);
               var9 = false;
            } else if (name.equals("mapTopPix")) {
               this._mapTopPix = Integer.parseInt(value);
               var9 = false;
            } else if (name.equals("mapTopDeg")) {
               this._mapTopDeg = Integer.parseInt(value);
               var9 = false;
            } else if (name.equals("mapBottomPix")) {
               this._mapBottomPix = Integer.parseInt(value);
               var9 = false;
            } else if (!name.equals("mapBottomDeg")) {
               var9 = false;
            } else {
               this._mapBottomDeg = Integer.parseInt(value);
               var9 = false;
            }
         } finally {
            if (var9) {
               System.out.println(((StringBuffer)(new Object("Invalid number format parsing "))).append(name).append(" in DayNightCurve").toString());
               continue;
            }
         }
      }
   }

   DayNightCurveForeignObject(ModelInteractorImpl mi, String query) {
      this._modelInteractor = mi;
      this.readParams(query);
      this.initializeDimensions();
      this.calculateVerticalCurve();
      this.calculateCurve();
      this.positionCurve();
      this.setPath();
   }

   private void setPath() {
      if (this._vertical) {
         PathNodeImpl.setXCoordinates(this._xCoordsVerticalCur, this._handle1, this._modelInteractor);
         PathNodeImpl.setYCoordinates(this._yCoordsVerticalCur, this._handle1, this._modelInteractor);
      } else {
         PathNodeImpl.setXCoordinates(this._xCoordsCur, this._handle1, this._modelInteractor);
         PathNodeImpl.setYCoordinates(this._yCoordsCur, this._handle1, this._modelInteractor);
      }
   }

   private void shiftCurve(int[] y, int[] yCur, int xShift) {
      for (int i = 0; i < this._numPts - xShift; i++) {
         yCur[i] = y[i + xShift];
      }

      int skip = Fixed32.toRoundedInt(this.radXToPixelX(this.pixelXToRadX(Fixed32.toFP(this._numPts)) - 411774));

      for (int i = this._numPts - xShift; i < this._numPts; i++) {
         yCur[i] = y[i + xShift - this._numPts + skip];
      }
   }

   private void positionCurve() {
      _cal.setTimeLong(System.currentTimeMillis());
      int h = _cal.internalGet(11, true);
      int min = _cal.internalGet(12, true);
      int xShift = (60 * h + min) * this._fullWidthPix / 1440;
      if (!this._vertical) {
         this.shiftCurve(this._yCoords, this._yCoordsCur, xShift);
      } else {
         int shiftedLeft = Fixed32.toRoundedInt(this._xCoordsVertical[0]) - xShift;
         int shiftedRight = Fixed32.toRoundedInt(this._xCoordsVertical[2]) - xShift;
         boolean breakCurve = false;
         if (shiftedLeft < this._curveMinX) {
            if (shiftedLeft + this._fullWidthPix > this._curveMaxX) {
               shiftedLeft = this._curveMinX;
            } else if (shiftedRight + this._fullWidthPix <= this._curveMaxX) {
               shiftedLeft += this._fullWidthPix;
               shiftedRight += this._fullWidthPix;
            } else {
               breakCurve = true;
               shiftedLeft += this._fullWidthPix;
            }
         }

         if (!breakCurve) {
            this._xCoordsVerticalCur[0] = this._xCoordsVerticalCur[1] = Fixed32.toFP(shiftedLeft);
            this._xCoordsVerticalCur[2] = this._xCoordsVerticalCur[3] = Fixed32.toFP(shiftedRight);
            this._xCoordsVerticalCur[4] = this._xCoordsVerticalCur[5] = this._xCoordsVerticalCur[6] = this._xCoordsVerticalCur[7] = Fixed32.toFP(shiftedRight);
         } else {
            this._xCoordsVerticalCur[0] = this._xCoordsVerticalCur[1] = Fixed32.toFP(this._curveMinX);
            this._xCoordsVerticalCur[2] = this._xCoordsVerticalCur[3] = Fixed32.toFP(shiftedRight);
            this._xCoordsVerticalCur[4] = this._xCoordsVerticalCur[5] = Fixed32.toFP(shiftedLeft);
            this._xCoordsVerticalCur[6] = this._xCoordsVerticalCur[7] = Fixed32.toFP(this._curveMaxX);
         }
      }
   }

   private void calculateCurve() {
      _cal.setTimeLong(System.currentTimeMillis());
      int yearDate = _cal.internalGet(6, true);
      int DATE_NORTH_MAX = 174;
      int H_YEAR = 183;
      int diffFromMax = yearDate - DATE_NORTH_MAX;
      int ratio32 = Fixed32.div(Fixed32.toFP(diffFromMax), Fixed32.toFP(H_YEAR));
      int alpha32 = Fixed32.mul(ratio32, 205887);
      int cosAlpha32 = Fixed32.Cos(alpha32);
      int cosGamma32 = Fixed32.mul(cosAlpha32, COS_PI_2__MAX_THETA_32);
      int gamma32 = Fixed32.ArcCos(cosGamma32);
      int tan = Fixed32.Tan(102943 - gamma32);
      if (Fixed32.abs(tan) < 363) {
         this._C = 11863284;
         this._vertical = true;
      } else {
         this._C = Fixed32.div(65536, tan);
         this._vertical = false;

         for (int i = 0; i < this._numPts; i++) {
            int x = i + this._curveMinX;
            this._xCoordsCur[i] = this._xCoords[i] = Fixed32.toFP(x);
            int yCoord = Fixed32.toFP(this.curve(x));
            if (yCoord < this._curveMinYFP) {
               yCoord = this._curveMinYFP;
            } else if (yCoord > this._curveMaxYFP) {
               yCoord = this._curveMaxYFP;
            }

            this._yCoords[i] = yCoord;
         }

         boolean northDayLong;
         if (yearDate < DATE_NORTH_MAX + H_YEAR / 2 && yearDate > DATE_NORTH_MAX - H_YEAR / 2) {
            northDayLong = true;
         } else {
            northDayLong = false;
         }

         this._xCoordsCur[this._numPts] = this._xCoords[this._numPts] = this._curveMaxXFP;
         this._xCoordsCur[this._numPts + 1] = this._xCoords[this._numPts + 1] = this._curveMinXFP;
         if (northDayLong) {
            this._yCoordsCur[this._numPts] = this._yCoords[this._numPts] = this._curveMaxYFP;
            this._yCoordsCur[this._numPts + 1] = this._yCoords[this._numPts + 1] = this._curveMaxYFP;
         } else {
            this._yCoordsCur[this._numPts] = this._yCoords[this._numPts] = this._curveMinYFP;
            this._yCoordsCur[this._numPts + 1] = this._yCoords[this._numPts + 1] = this._curveMinYFP;
         }
      }
   }

   private int curve(int x) {
      x = Fixed32.toFP(x);
      int longtitude = this.pixelXToRadX(x);
      int cosL = Fixed32.Cos(longtitude);
      int c_cosL = Fixed32.mul(this._C, cosL);
      int c_cosLSq = Fixed32.mul(c_cosL, c_cosL);
      int sqrt = Fixed32.sqrt(c_cosLSq + 65536);
      int numerator = sqrt + c_cosL;
      int denominator = sqrt - c_cosL;
      int fraction = Fixed32.div(numerator, denominator);
      double fractionDouble = fraction / 4679240012837945344L;
      double latitudeDouble = 4602678819172646912L * MathUtilities.log(fractionDouble);
      int latitude = Fixed32.tenThouToFP((int)(latitudeDouble * 4666723172467343360L));
      int result32 = this._mapHeightPixFP
         + this._mapTopPixFP
         - Fixed32.mul(this._mapHeightPixFP, Fixed32.div(latitude - this._mapBottomRadFP, this._mapHeightRadFP));
      return Fixed32.toInt(result32);
   }

   private int pixelXToRadX(int x) {
      return Fixed32.div(Fixed32.mul(x - this._mapLeftPixFP, this._mapWidthRadFP), this._mapWidthPixFP) + this._mapLeftRadFP;
   }

   private int radXToPixelX(int radX) {
      return Fixed32.div(Fixed32.mul(radX - this._mapLeftRadFP, this._mapWidthPixFP), this._mapWidthRadFP) + this._mapLeftPixFP;
   }

   @Override
   public int getWidth() {
      return 0;
   }

   @Override
   public int getHeight() {
      return 0;
   }

   @Override
   public void draw(Object graphics, int x, int y) {
   }

   static {
      _cal.setTimeZone(TimeZone.getTimeZone(DateTimeUtilities.GMT));
   }
}
