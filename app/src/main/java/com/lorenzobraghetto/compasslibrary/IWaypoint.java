package com.lorenzobraghetto.compasslibrary;

public interface IWaypoint extends ILogable, ICoordinates {

	/**
	 * Return an unique waypoint id.
	 * 
	 * @return a non-negative id if set, -1 if unset
	 */
	public abstract int getId();

	public abstract WaypointType getWaypointType();

	public abstract String getCoordType();

}
