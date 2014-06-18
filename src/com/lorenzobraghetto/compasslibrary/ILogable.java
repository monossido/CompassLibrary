package com.lorenzobraghetto.compasslibrary;

public interface ILogable {

	/**
	 * @return Geocode like GCxxxx
	 */
	public abstract String getGeocode();

	/**
	 * @return Name
	 */
	public abstract String getName();

}