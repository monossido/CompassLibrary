package com.lorenzobraghetto.compasslibrary;

import android.location.Location;

public interface IGeoData {

	public Location getLocation();

	public LocationProviderType getLocationProvider();

	public boolean isPseudoLocation();

	public Geopoint getCoords();

	public float getBearing();

	public float getSpeed();

	public float getAccuracy();

	public boolean getGpsEnabled();

	public int getSatellitesVisible();

	public int getSatellitesFixed();
}
