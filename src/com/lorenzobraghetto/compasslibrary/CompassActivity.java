package com.lorenzobraghetto.compasslibrary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import rx.Subscription;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class CompassActivity extends Activity {

	protected TextView navType;
	protected TextView navAccuracy;
	protected TextView navSatellites;
	protected TextView navLocation;
	protected TextView distanceView;
	protected TextView headingView;
	protected CompassView compassView;
	protected TextView destinationTextView;
	protected TextView cacheInfoView;

	private static final String EXTRAS_COORDS = "coords";
	private static final String EXTRAS_CACHE_INFO = "cacheinfo";
	private static final List<IWaypoint> coordinates = new ArrayList<IWaypoint>();
	public static final int UPDATE_GEODIR = 1 << 3;

	/**
	 * Destination of the compass, or null (if the compass is used for a
	 * waypoint only).
	 */
	//Geocache cache = null;
	private Geopoint dstCoords = null;
	private float cacheHeading = 0;
	private String info = null;

	private Resources res;
	private boolean toasted = false;
	private Subscription geoDirHandlerSubSCription;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.compass_activity);

		navType = (TextView) findViewById(R.id.nav_type);
		navAccuracy = (TextView) findViewById(R.id.nav_accuracy);
		navSatellites = (TextView) findViewById(R.id.nav_satellites);
		navLocation = (TextView) findViewById(R.id.nav_location);
		distanceView = (TextView) findViewById(R.id.distance);
		headingView = (TextView) findViewById(R.id.heading);
		compassView = (CompassView) findViewById(R.id.rose);
		destinationTextView = (TextView) findViewById(R.id.destination);
		cacheInfoView = (TextView) findViewById(R.id.cacheinfo);

		// get parameters
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			dstCoords = extras.getParcelable(EXTRAS_COORDS);
			info = extras.getString(EXTRAS_CACHE_INFO);
		} else {
			//			Intent pointIntent = new Intent(this, NavigateAnyPointActivity.class);
			//		startActivity(pointIntent);

			//			finish();
			//		return;
		}
		res = getResources();

		// set header
		setTitle();
		setDestCoords();
		setCacheInfo();
	}

	@Override
	public void onResume() {
		super.onResume();
		geoDirHandlerSubSCription = geoDirHandler.start(UPDATE_GEODIR, this, getWindowManager());
	}

	@Override
	public void onDestroy() {
		compassView.destroyDrawingCache();
		geoDirHandlerSubSCription.unsubscribe();
		super.onDestroy();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		setContentView(R.layout.compass_activity);

		setTitle();
		setDestCoords();
		setCacheInfo();

		// Force a refresh of location and direction when data is available.
		final CompassLibraryApplication app = new CompassLibraryApplication(this);
		final IGeoData geo = app.currentGeo();
		if (geo != null) {
			geoDirHandler.updateGeoDir(geo, app.currentDirection());
		}
	}

	private void setTitle() {
		setTitle(res.getString(R.string.navigation));
	}

	private void setDestCoords() {
		if (dstCoords == null) {
			return;
		}

		destinationTextView.setText(dstCoords.toString());
	}

	private void setCacheInfo() {
		if (info == null) {
			cacheInfoView.setVisibility(View.GONE);
			return;
		}
		cacheInfoView.setVisibility(View.VISIBLE);
		cacheInfoView.setText(info);
	}

	private void updateDistanceInfo(final IGeoData geo) {
		if (geo.getCoords() == null || dstCoords == null) {
			return;
		}

		cacheHeading = geo.getCoords().bearingTo(dstCoords);
		float distance = geo.getCoords().distanceTo(dstCoords);
		distanceView.setText(Units.getDistanceFromKilometers(distance));
		headingView.setText(Math.round(cacheHeading) + "°");
	}

	private GeoDirHandler geoDirHandler = new GeoDirHandler() {
		@Override
		public void updateGeoDir(final IGeoData geo, final float dir) {
			try {
				if (geo.getCoords() != null) {
					if (geo.getSatellitesVisible() >= 0) {
						navSatellites.setText(res.getString(R.string.loc_sat) + ": " + geo.getSatellitesFixed() + "/" + geo.getSatellitesVisible());
					} else {
						navSatellites.setText("");
					}
					navType.setText(res.getString(geo.getLocationProvider().resourceId));

					if (geo.getAccuracy() >= 0) {
						navAccuracy.setText("±" + Units.getDistanceFromMeters(geo.getAccuracy()));
					} else {
						navAccuracy.setText(null);
					}

					navLocation.setText(geo.getCoords().toString());

					updateDistanceInfo(geo);
				} else {
					navType.setText(null);
					navAccuracy.setText(null);
					navLocation.setText(res.getString(R.string.loc_trying));
				}

				updateNorthHeading(DirectionProvider.getDirectionNow(dir, getWindowManager()));
			} catch (RuntimeException e) {
				Log.w("CompassLibrary", "Failed to LocationUpdater location.");
			}
		}
	};

	private void updateNorthHeading(final float northHeading) {
		if (compassView != null) {
			compassView.updateNorth(northHeading, cacheHeading);
		}
	}

	public static void startActivity(final Context context, final Geopoint coords, final Collection<IWaypoint> coordinatesWithType,
			final String info) {
		coordinates.clear();
		if (coordinatesWithType != null) {
			for (IWaypoint coordinate : coordinatesWithType) {
				if (coordinate != null) {
					coordinates.add(coordinate);
				}
			}
		}

		final Intent navigateIntent = new Intent(context, CompassActivity.class);
		navigateIntent.putExtra(EXTRAS_COORDS, coords);
		navigateIntent.putExtra(EXTRAS_CACHE_INFO, info);
		context.startActivity(navigateIntent);
	}

}
