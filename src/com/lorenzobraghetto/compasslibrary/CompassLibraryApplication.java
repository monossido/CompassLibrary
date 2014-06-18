package com.lorenzobraghetto.compasslibrary;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;

import rx.Observable;
import rx.functions.Action1;
import rx.observables.ConnectableObservable;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.ViewConfiguration;

public class CompassLibraryApplication {

	private boolean forceRelog = false; // c:geo needs to log into cache providers
	public boolean showLoginToast = true; //login toast shown just once.
	private boolean liveMapHintShownInThisSession = false; // livemap hint has been shown
	private static CompassLibraryApplication instance;
	private Observable<IGeoData> geoDataObservable;
	private Observable<Float> directionObservable;
	private volatile IGeoData currentGeo = null;
	private volatile float currentDirection = 0.0f;
	private Context context;

	static {
		final UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();

		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {

			@Override
			public void uncaughtException(Thread thread, Throwable ex) {
				Log.e("UncaughtException", ex.toString());
				Throwable exx = ex;
				while (exx.getCause() != null) {
					exx = exx.getCause();
				}
				if (exx.getClass().equals(OutOfMemoryError.class)) {
					try {
						Log.e("CACCIA", "OutOfMemory");
						android.os.Debug.dumpHprofData(Environment.getExternalStorageDirectory().getPath() + "/dump.hprof");
					} catch (IOException e) {
						Log.e("CACCIA", "Error writing dump", e);
					}
				}
				defaultHandler.uncaughtException(thread, ex);
			}
		});
	}

	public CompassLibraryApplication(Context context) {
		this.context = context;
		//if (Settings.isAlwaysShowOverlfowMenu()) {
		if (true) {
			try {
				ViewConfiguration config = ViewConfiguration.get(context);
				Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");

				if (menuKeyField != null) {
					menuKeyField.setAccessible(true);
					menuKeyField.setBoolean(config, false);
				}
			} catch (Exception ex) {
				// Ignore
			}
		}
	}

	public synchronized Observable<IGeoData> geoDataObservable() {
		if (geoDataObservable == null) {
			final ConnectableObservable<IGeoData> onDemand = GeoDataProvider.create(context).replay(1);
			onDemand.subscribe(new Action1<IGeoData>() {
				@Override
				public void call(final IGeoData geoData) {
					currentGeo = geoData;
				}
			});
			geoDataObservable = onDemand.refCount();
		}
		return geoDataObservable;
	}

	public synchronized Observable<Float> directionObservable() {
		if (directionObservable == null) {
			final ConnectableObservable<Float> onDemand = DirectionProvider.create(context).replay(1);
			onDemand.subscribe(new Action1<Float>() {
				@Override
				public void call(final Float direction) {
					currentDirection = direction;
				}
			});
			directionObservable = onDemand.refCount();
		}
		return directionObservable;
	}

	public IGeoData currentGeo() {
		return currentGeo != null ? currentGeo : geoDataObservable().toBlockingObservable().first();
	}

	public float currentDirection() {
		return currentDirection;
	}

	public boolean isLiveMapHintShownInThisSession() {
		return liveMapHintShownInThisSession;
	}

	public void setLiveMapHintShownInThisSession() {
		liveMapHintShownInThisSession = true;
	}

	/**
	 * Check if cgeo must relog even if already logged in.
	 * 
	 * @return <code>true</code> if it is necessary to relog
	 */
	public boolean mustRelog() {
		final boolean mustLogin = forceRelog;
		forceRelog = false;
		return mustLogin;
	}

	/**
	 * Force cgeo to relog when reaching the main activity.
	 */
	public void forceRelog() {
		forceRelog = true;
	}

}
