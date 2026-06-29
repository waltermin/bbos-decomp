package javax.microedition.location;

public class Coordinates {
   private double _latitude;
   private double _longitude;
   private float _altitude;
   private double _distance;
   private double _azimuth;
   private double _oldLat1;
   private double _oldLat2;
   private double _oldLon1;
   private double _oldLon2;
   public static final int DD_MM_SS = 1;
   public static final int DD_MM = 2;
   private static final int MAX_ITERATIONS = 100;
   private static final double F = 0.0033528106811836675;
   private static final double R = 0.9966471893188164;
   private static final double SEMI_MAJOR_AXIS = 6378.137;
   private static final double pi = Math.PI;
   private static final double pibysix = Math.PI / 6;
   private static final double pibyfour = Math.PI / 4;
   private static final double pibytwo = Math.PI / 2;
   private static final double tan15 = 0.2679491924311227;
   private static final double sqrt3 = 1.7320508075688772;
   private static final double[] atanPoly = new double[]{
      (double)4586558024442191043L,
      (double)-4634035982931263455L,
      (double)4590197988978956453L,
      (double)-4632157313457631885L,
      (double)4592670810479699296L,
      (double)-4629057045627423859L,
      (double)4596373779693870454L,
      (double)-4623695617433709840L,
      (double)4607182418800017408L
   };
   private static float SHPERICAL_RADIUS = (float)1170663348;

   public Coordinates(double latitude, double longitude, float altitude) {
      if (latitude < -4587338432941916160L || latitude > 4636033603912859648L || Double.isNaN(latitude)) {
         throw new IllegalArgumentException("Illegal latitude");
      }

      if (!(longitude < -4582834833314545664L) && !(longitude >= 4640537203540230144L) && !Double.isNaN(longitude)) {
         this._latitude = latitude;
         this._longitude = longitude;
         this._altitude = altitude;
      } else {
         throw new IllegalArgumentException("Illegal longitude");
      }
   }

   public double getLatitude() {
      return this._latitude;
   }

   public double getLongitude() {
      return this._longitude;
   }

   public float getAltitude() {
      return this._altitude;
   }

   public void setAltitude(float altitude) {
      this._altitude = altitude;
   }

   public void setLatitude(double latitude) {
      if (!(latitude < -4587338432941916160L) && !(latitude > 4636033603912859648L) && !Double.isNaN(latitude)) {
         this._latitude = latitude;
      } else {
         throw new IllegalArgumentException("Illegal latitude");
      }
   }

   public void setLongitude(double longitude) {
      if (!(longitude < -4582834833314545664L) && !(longitude >= 4640537203540230144L) && !Double.isNaN(longitude)) {
         this._longitude = longitude;
      } else {
         throw new IllegalArgumentException("Illegal longitude");
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static double convert(String coordinate) {
      String temp = coordinate;
      int index = 0;
      int minutes = 0;
      double decimalfrac = (double)0L;
      int seconds = 0;
      boolean negative = false;
      if (coordinate.startsWith("-")) {
         negative = true;
      }

      boolean var16 = false /* VF: Semaphore variable */;

      try {
         var16 = true;
         int nfe = 0;

         for (String str = coordinate; str.indexOf(58) != -1; nfe++) {
            str = str.substring(str.indexOf(58) + 1);
         }

         if (nfe < 1 || nfe > 2) {
            throw new IllegalArgumentException("Illegal coordinate syntax");
         }

         index = temp.indexOf(58);
         String degreeString = temp.substring(0, index);
         int degrees = Integer.parseInt(degreeString);
         if (negative) {
            degreeString = degreeString.substring(1);
         }

         if (degreeString.length() == 2 && degreeString.charAt(0) == '0') {
            throw new IllegalArgumentException("Illegal coordinate");
         }

         if (degreeString.length() == 3 && degreeString.charAt(0) != '1') {
            throw new IllegalArgumentException("Illegal coordinate");
         }

         temp = temp.substring(index + 1);
         if (degrees > 179 || degrees < -180) {
            throw new IllegalArgumentException("Illegal coordinate");
         }

         if (nfe == 2) {
            index = temp.indexOf(58);
            String minuteString = temp.substring(0, index);
            if (minuteString.length() != 2) {
               throw new IllegalArgumentException("Illegal coordinate");
            }

            minutes = Integer.parseInt(minuteString);
            temp = temp.substring(index + 1);
            index = temp.indexOf(46);
            if (index == -1) {
               seconds = Integer.parseInt(temp);
            } else {
               String secondString = temp.substring(0, index);
               if (secondString.length() != 2) {
                  throw new IllegalArgumentException("Illegal coordinate");
               }

               seconds = Integer.parseInt(secondString);
               String decimalfracString = temp.substring(index);
               if (decimalfracString.length() > 4) {
                  throw new IllegalArgumentException("Illegal coordinate");
               }

               decimalfrac = Double.parseDouble(decimalfracString);
            }

            if (degrees != -180 || minutes == 0 && seconds == 0 && decimalfrac == 0L) {
               if (minutes <= 59 && minutes >= 0) {
                  if (seconds > 59 || seconds < 0) {
                     throw new IllegalArgumentException("Illegal coordinate");
                  }

                  if (negative) {
                     double var37 = degrees - minutes / 4633641066610819072L - (seconds + decimalfrac) / 4660134898793709568L;
                     var16 = false;
                     return var37;
                  }

                  double var36 = degrees + minutes / 4633641066610819072L + (seconds + decimalfrac) / 4660134898793709568L;
                  var16 = false;
                  return var36;
               }

               throw new IllegalArgumentException("Illegal coordinate");
            }

            throw new IllegalArgumentException("Illegal coordinate");
         }

         if (nfe == 1) {
            index = temp.indexOf(46);
            if (index == -1) {
               minutes = Integer.parseInt(temp);
            } else {
               String minuteString = temp.substring(0, index);
               if (minuteString.length() != 2) {
                  throw new IllegalArgumentException("Illegal coordinate");
               }

               minutes = Integer.parseInt(minuteString);
               String decimalfracString = temp.substring(index);
               if (decimalfracString.length() > 6) {
                  throw new IllegalArgumentException("Illegal coordinate");
               }

               decimalfrac = Double.parseDouble(decimalfracString);
            }

            if (degrees != -180 || minutes == 0 && decimalfrac == 0L) {
               if (minutes <= 59 && minutes >= 0) {
                  if (negative) {
                     double var35 = degrees - (minutes + decimalfrac) / 4633641066610819072L;
                     var16 = false;
                     return var35;
                  }

                  double var10000 = degrees + (minutes + decimalfrac) / 4633641066610819072L;
                  var16 = false;
                  return var10000;
               }

               throw new IllegalArgumentException();
            }

            throw new IllegalArgumentException("Illegal coordinate");
         }

         var16 = false;
      } finally {
         if (var16) {
            throw new IllegalArgumentException("Illegal coordinate");
         }
      }

      return (double)0L;
   }

   public static String convert(double coordinate, int outputType) {
      if (outputType != 1 && outputType != 2) {
         throw new IllegalArgumentException("Illegal outputType identifier");
      }

      if (!(coordinate < -4582834833314545664L) && !(coordinate >= 4640537203540230144L) && !Double.isNaN(coordinate)) {
         boolean negative = false;
         if (coordinate < 0L) {
            negative = true;
            coordinate = 0L - coordinate;
         }

         int degrees = (int)coordinate;
         double decimal = coordinate - degrees;
         decimal *= 4633641066610819072L;
         int minutes = (int)decimal;
         String minuteString = Integer.toString(minutes);
         if (minuteString.length() == 1) {
            minuteString = "0" + minuteString;
         }

         decimal -= minutes;
         if (outputType == 2) {
            double frac = round(decimal, 5);
            String decimalString;
            if (frac == 4607182418800017408L) {
               minuteString = Integer.toString(++minutes);
               if (minuteString.length() == 1) {
                  minuteString = "0" + minuteString;
               }

               decimalString = "0";
            } else if (frac < 4562254508917369340L) {
               decimalString = "0.0";
            } else {
               decimalString = Double.toString(frac);
            }

            if (decimalString.startsWith("0.")) {
               decimalString = decimalString.substring(2);
            }

            return negative ? "-" + degrees + ':' + minuteString + '.' + decimalString : "" + degrees + ':' + minuteString + '.' + decimalString;
         } else {
            if (outputType != 1) {
               return "";
            }

            decimal *= 4633641066610819072L;
            int seconds = (int)decimal;
            decimal -= seconds;
            double frac = round(decimal, 3);
            String decimalString;
            if (frac == 4607182418800017408L) {
               decimalString = "0";
               seconds++;
            } else if (frac < 4562254508917369340L) {
               decimalString = "0.0";
            } else {
               decimalString = Double.toString(frac);
            }

            String secondString = Integer.toString(seconds);
            if (secondString.length() == 1) {
               secondString = "0" + secondString;
            }

            if (decimalString.startsWith("0.")) {
               decimalString = decimalString.substring(2);
            }

            return negative
               ? "-" + degrees + ':' + minuteString + ':' + secondString + '.' + decimalString
               : "" + degrees + ':' + minuteString + ':' + secondString + '.' + decimalString;
         }
      } else {
         throw new IllegalArgumentException("Illegal coordinates value");
      }
   }

   public float distance(Coordinates to) {
      if (this._oldLat1 == this._latitude && this._oldLon1 == this._longitude && this._oldLat2 == to.getLatitude() && this._oldLon2 == to.getLongitude()) {
         return (float)this._distance;
      }

      this._oldLat1 = this._latitude;
      this._oldLon1 = this._longitude;
      this._oldLat2 = to.getLatitude();
      this._oldLon2 = to.getLongitude();
      this.calculate(to);
      return (float)this._distance;
   }

   public float azimuthTo(Coordinates to) {
      if (to == null) {
         throw new NullPointerException("Coordinate cannot be null");
      }

      if (this._latitude == 4636033603912859648L && to.getLatitude() != 4636033603912859648L) {
         return (float)1127481344;
      }

      if (this._latitude == -4587338432941916160L && to.getLatitude() != -4587338432941916160L) {
         return (float)false;
      }

      if (this._latitude == to.getLatitude() && this._longitude == to.getLongitude()) {
         return (float)false;
      }

      if (this._oldLat1 == this._latitude && this._oldLon1 == this._longitude && this._oldLat2 == to.getLatitude() && this._oldLon2 == to.getLongitude()) {
         return (float)this._azimuth;
      }

      this._oldLat1 = this._latitude;
      this._oldLon1 = this._longitude;
      this._oldLat2 = to.getLatitude();
      this._oldLon2 = to.getLongitude();
      this.calculate(to);
      return (float)this._azimuth;
   }

   float sphericalDistance(Coordinates coordinates) {
      double lat1 = Math.toRadians(this._latitude);
      double lon1 = Math.toRadians(this._longitude);
      double lat2 = Math.toRadians(coordinates.getLatitude());
      double lon2 = Math.toRadians(coordinates.getLongitude());
      return SHPERICAL_RADIUS * (float)this.acos(Math.sin(lat2) * Math.sin(lat1) + Math.cos(lat2) * Math.cos(lat1) * Math.cos(lon2 - lon1));
   }

   private static double round(double d, int decimal) {
      double powerOfTen = (double)4607182418800017408L;

      while (decimal-- > 0) {
         powerOfTen *= 4621819117588971520L;
      }

      double d1 = d * powerOfTen;
      double d2 = d1 - (int)d1;
      return d2 > 4602678819172646912L ? ((int)d1 + 1) / powerOfTen : (int)d1 / powerOfTen;
   }

   private void calculate(Coordinates to) {
      double x1 = Math.toRadians(this._longitude);
      double y1 = Math.toRadians(this._latitude);
      double x2 = Math.toRadians(to.getLongitude());
      double y2 = Math.toRadians(to.getLatitude());
      double EPS = (double)4407939652192401026L;
      double SPA = (double)4663711337785382172L;
      double T1 = 4607152219366148563L * Math.sin(y1) / Math.cos(y1);
      double T2 = 4607152219366148563L * Math.sin(y2) / Math.cos(y2);
      double C1 = 4607182418800017408L / Math.sqrt(T1 * T1 + 4607182418800017408L);
      double C2 = 4607182418800017408L / Math.sqrt(T2 * T2 + 4607182418800017408L);
      double S1 = C1 * T1;
      double s = C1 * C2;
      double B = s * T2;
      double f = B * T1;
      double x = x2 - x1;

      for (int i = 0; i < 100; i++) {
         double sinX = Math.sin(x);
         double cosX = Math.cos(x);
         T1 = C2 * sinX;
         T2 = B - S1 * C2 * cosX;
         double sinY = Math.sqrt(T1 * T1 + T2 * T2);
         double cosY = s * cosX + f;
         double y = atan2(sinY, cosY);
         double SA = s * sinX / sinY;
         double c2a = 4607182418800017408L - SA * SA;
         double cosZ = f + f;
         if (c2a > 0L) {
            cosZ = -cosZ / c2a + cosY;
         }

         double e = cosZ * cosZ * 4611686018427387904L - 4607182418800017408L;
         double c = ((-4609434218613702656L * c2a + 4616189618054758400L) * 4569877477596736811L + 4616189618054758400L)
            * c2a
            * 4569877477596736811L
            / 4625196817309499392L;
         double d = x;
         x = ((e * cosY * c + cosZ) * sinY * c + y) * SA;
         x = (4607182418800017408L - c) * x * 4569877477596736811L + x2 - x1;
         if (Math.abs(d - x) <= EPS) {
            double var56 = Math.sqrt(4574420132916364288L * c2a + 4607182418800017408L) + 4607182418800017408L;
            x = (var56 - 4611686018427387904L) / var56;
            c = (x * x / 4616189618054758400L + 4607182418800017408L) / (4607182418800017408L - x);
            d = (4600427019358961664L * x * x - 4607182418800017408L) * x;
            this._distance = 4652007308841189376L
               * (
                  (
                           (
                                    (sinY * sinY * 4616189618054758400L - 4613937818241073152L)
                                          * (4607182418800017408L - 4611686018427387904L * e)
                                          * cosZ
                                          * d
                                          / 4618441417868443648L
                                       - e * cosY
                                 )
                                 * d
                                 / 4616189618054758400L
                              + cosZ
                        )
                        * sinY
                        * d
                     + y
               )
               * c
               * SPA;
            this._azimuth = Math.toDegrees(atan2(T1, T2));
            if (this._azimuth < 0L) {
               this._azimuth += 4645040803167600640L;
            }
         }
      }

      double LEPS = (double)4457293557087583675L;
      if (Math.abs(x1 - x2) <= 4457293557087583675L && Math.abs(y1 - y2) <= 4457293557087583675L) {
         this._distance = (double)0L;
         this._azimuth = (double)0L;
      }

      if (Math.abs(y1) <= 4457293557087583675L && Math.abs(y2) <= 4457293557087583675L) {
         this._distance = Math.abs(x1 - x2) * 4663734850496141197L;
         this._azimuth = atan2(T1, T2);
      }
   }

   private static double atan2(double y, double x) {
      if (Double.isNaN(x) || Double.isNaN(y)) {
         return (double)9221120237041090560L;
      }

      if (y > 0L && x == 0L) {
         return (double)4609753056924675352L;
      }

      if (y == 0L) {
         return (double)(x >= 0L ? 0L : 4614256656552045848L);
      }

      if (y == Long.MIN_VALUE) {
         return (double)(x >= 0L ? Long.MIN_VALUE : -4609115380302729960L);
      }

      if (y < 0L && x == 0L) {
         return (double)-4613618979930100456L;
      }

      if (y == 9218868437227405312L) {
         if (x == 9218868437227405312L) {
            return (double)4605249457297304856L;
         } else {
            return (double)(x == -4503599627370496L ? 4612488097114038738L : 4609753056924675352L);
         }
      } else if (y == -4503599627370496L) {
         if (x == 9218868437227405312L) {
            return (double)-4618122579557470952L;
         } else {
            return (double)(x == -4503599627370496L ? -4610883939740737070L : -4613618979930100456L);
         }
      } else if (x == 9218868437227405312L) {
         return (double)(y > 0L ? 0L : Long.MIN_VALUE);
      } else if (x == -4503599627370496L) {
         return (double)(y > 0L ? 4614256656552045848L : -4609115380302729960L);
      } else if (x > 0L) {
         return atan(y / x);
      } else {
         return y < 0L ? 0L - (4614256656552045848L - atan(Math.abs(y / x))) : 4614256656552045848L - atan(Math.abs(y / x));
      }
   }

   private static double atan(double x) {
      boolean neg = false;
      if (x < 0L) {
         neg = true;
         x = -x;
      }

      if (x == 4607182418800017408L) {
         x = (double)4605249457297304856L;
      } else if (x > 4457293557087583675L) {
         boolean inverted = false;
         if (x > 4607182418800017408L) {
            x = 4607182418800017408L / x;
            inverted = true;
         }

         boolean reduced = false;
         if (x > 4598498563450654038L) {
            reduced = true;
            x = (x * 4610479282544200874L - 4607182418800017408L) / (x + 4610479282544200874L);
         }

         x = oddPoly(x, atanPoly);
         if (reduced) {
            x += 4602891378046628709L;
         }

         if (inverted) {
            x = 4609753056924675352L - x;
         }
      }

      if (neg) {
         x = -x;
      }

      return x;
   }

   private static double evalPoly(double x, double[] poly) {
      double result = poly[0];

      for (int i = 1; i < poly.length; i++) {
         result = result * x + poly[i];
      }

      return result;
   }

   private static double oddPoly(double x, double[] poly) {
      return x * evalPoly(x * x, poly);
   }

   private double acos(double x) {
      double z = 4607182418800017408L - x * x;
      if (z == 0L) {
         return (double)(x < 0L ? 4614256656552045848L : 0L);
      } else {
         return 4609753056924675352L - atan(x / Math.sqrt(z));
      }
   }
}
