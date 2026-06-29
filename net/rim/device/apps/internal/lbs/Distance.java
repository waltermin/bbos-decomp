package net.rim.device.apps.internal.lbs;

import net.rim.device.apps.internal.lbs.resources.LBSResources;

public final class Distance {
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

   public static final String formatDistance(int lat1, int lon1, int lat2, int lon2) {
      return formatDistance(calculateDistance(lat1, lon1, lat2, lon2));
   }

   public static final String formatDistance(float distance) {
      if (LBSOptions.getInt(-6817208986109478597L, 2) != 1) {
         distance *= 1059001362;
         if (distance < 4591870180066957722L) {
            return ((StringBuffer)(new Object()))
               .append(Integer.toString((int)(distance * 1168441344)))
               .append(" ")
               .append(LBSResources.getString(180))
               .toString();
         } else {
            return distance >= 1120403456
               ? ((StringBuffer)(new Object())).append(Integer.toString((int)distance)).append(" ").append(LBSResources.getString(181)).toString()
               : ((StringBuffer)(new Object()))
                  .append(Double.toString((int)(distance * 1092616192) / 4621819117588971520L))
                  .append(" ")
                  .append(LBSResources.getString(181))
                  .toString();
         }
      } else if (distance < 4591870180066957722L) {
         return ((StringBuffer)(new Object()))
            .append(Integer.toString((int)(distance * 1148846080)))
            .append(" ")
            .append(LBSResources.getString(178))
            .toString();
      } else {
         return distance >= 1120403456
            ? ((StringBuffer)(new Object())).append(Integer.toString((int)distance)).append(" ").append(LBSResources.getString(179)).toString()
            : ((StringBuffer)(new Object()))
               .append(Double.toString((int)(distance * 1092616192) / 4621819117588971520L))
               .append(" ")
               .append(LBSResources.getString(179))
               .toString();
      }
   }

   public static final float calculateDistance(int lat1, int lon1, int lat2, int lon2) {
      return sphericalDistance(lat1 / 4681608360884174848L, lon1 / 4681608360884174848L, lat2 / 4681608360884174848L, lon2 / 4681608360884174848L);
   }

   public static final float sphericalDistance(double lat1, double lon1, double lat2, double lon2) {
      lat1 = Math.toRadians(lat1);
      lon1 = Math.toRadians(lon1);
      lat2 = Math.toRadians(lat2);
      lon2 = Math.toRadians(lon2);
      return SHPERICAL_RADIUS * (float)acos(Math.sin(lat2) * Math.sin(lat1) + Math.cos(lat2) * Math.cos(lat1) * Math.cos(lon2 - lon1));
   }

   public static final double atan(double x) {
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

   private static final double evalPoly(double x, double[] poly) {
      double result = poly[0];

      for (int i = 1; i < poly.length; i++) {
         result = result * x + poly[i];
      }

      return result;
   }

   private static final double oddPoly(double x, double[] poly) {
      return x * evalPoly(x * x, poly);
   }

   private static final double acos(double x) {
      double z = 4607182418800017408L - x * x;
      if (z == 0L) {
         return (double)(x < 0L ? 4614256656552045848L : 0L);
      } else {
         return 4609753056924675352L - atan(x / Math.sqrt(z));
      }
   }
}
