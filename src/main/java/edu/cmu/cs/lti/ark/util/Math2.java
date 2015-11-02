package edu.cmu.cs.lti.ark.util;

import static edu.cmu.cs.lti.ark.util.IntRanges.xrange;

/**
 * @author sthomson@cs.cmu.edu
 */
public class Math2 {
	public static double sum(double... values) {
		double result = 0;
		for (double val : values) {
			result += val;
		}
		return result;
	}

	/** Performs a dot product of the two dense vectors a and b. */
	public static double dotProduct(double[] a, double[] b) {
		double result = 0.0;
		for (int featureIdx : xrange(a.length)) {
			result += a[featureIdx] * b[featureIdx];
		}
		return result;
	}

	public static double[] softMax(double[] a) {
		double z = 0.0;
		for (double v : a) {
			z += Math.exp(v);
		}
		double [] ps = new double[a.length];
		for (int i : xrange(a.length)) {
			ps[i] = a[i] / z;
		}
		return ps;
	}
}
