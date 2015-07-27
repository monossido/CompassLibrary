package com.lorenzobraghetto.compasslibrary;

public enum LocationProviderType {
	GPS(R.string.loc_gps),
	NETWORK(R.string.loc_net),
	LAST(R.string.loc_last);

	public final int resourceId;

	LocationProviderType(final int resourceId) {
		this.resourceId = resourceId;
	}
}
