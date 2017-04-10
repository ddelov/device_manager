package com.estafet.openshift.util;

/**
 * Created by Delcho Delov on 06.04.17.
 */
public class Utils {
		private Utils(){}
		public static boolean isEmpty(String val){
				return val==null || val.trim().isEmpty();
		}
		public static boolean areEquals(double a, double b, double delta) {
				if(a==b){
						return true;
				}
				if(a>b){
						return a-b < delta;
				}
				return b-a < delta;
		}

}
