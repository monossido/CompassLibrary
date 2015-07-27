package com.lorenzobraghetto.compasslibrary;

import java.util.Locale;

import org.apache.commons.lang3.tuple.ImmutablePair;

public class Units {

	public static ImmutablePair<Double, String> scaleDistance(final double distanceKilometers) {
		double distance;
		String units;

		if (distanceKilometers >= 1) {
			distance = distanceKilometers;
			units = "km";
		} else {
			distance = distanceKilometers * 1000;
			units = "m";
		}

		return new ImmutablePair<Double, String>(distance, units);
	}

	public static String getDistanceFromKilometers(final Float distanceKilometers) {
		if (distanceKilometers == null) {
			return "?";
		}

		final ImmutablePair<Double, String> scaled = scaleDistance(distanceKilometers);
		String formatString;
		if (scaled.left >= 100) {
			formatString = "%.0f";
		} else if (scaled.left >= 10) {
			formatString = "%.1f";
		} else {
			formatString = "%.2f";
		}

		return String.format(formatString + " %s", scaled.left, scaled.right);
	}

	public static String getDistanceFromMeters(float meters) {
		return getDistanceFromKilometers(meters / 1000f);
	}

	public static String getSpeed(final float kilometersPerHour) {

		return String.format(Locale.US, "%.0f km/h", kilometersPerHour);
	}
}
