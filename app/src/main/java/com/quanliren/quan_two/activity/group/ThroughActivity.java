package com.quanliren.quan_two.activity.group;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapOptions;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.SupportMapFragment;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.activity.base.BaseActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

@EActivity
@OptionsMenu(R.menu.through_map_menu)
public class ThroughActivity extends BaseActivity implements LocationSource,
		AMapLocationListener,AMap.OnCameraChangeListener {
	public static final LatLng BEIJING = new LatLng(39.908691, 116.397506);// 北京市经纬度

	private static final String MAP_FRAGMENT_TAG = "map";
	AMap amap;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	private SupportMapFragment map;

    @ViewById
    TextView tv_position;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.through_map);

		CameraPosition LUJIAZUI = new CameraPosition.Builder().target(BEIJING)
				.zoom(15).bearing(0).tilt(0).build();
		AMapOptions aOptions = new AMapOptions();
		aOptions.camera(LUJIAZUI);
		if (map == null) {
			map = SupportMapFragment.newInstance(aOptions);
			FragmentTransaction fragmentTransaction = getSupportFragmentManager()
					.beginTransaction();
			fragmentTransaction.add(R.id.map, map, MAP_FRAGMENT_TAG);
			fragmentTransaction.commit();

		}

	}

	@AfterViews
	void initView() {

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (amap == null) {
			amap = map.getMap();// amap对象初始化成功
			setUpMap();
		}
		getSupportActionBar().setTitle("会员漫游");
	}

	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(this);
			mAMapLocationManager.requestLocationData(LocationProviderProxy.AMapNetwork, -1, 0, this);
		}
	}

	private void setUpMap() {
		MyLocationStyle myLocationStyle = new MyLocationStyle();
		myLocationStyle.myLocationIcon(BitmapDescriptorFactory
				.fromResource(R.drawable.location_marker));
		myLocationStyle.radiusFillColor(0x1902bce4);
		myLocationStyle.strokeColor(0x3302bce4);
		myLocationStyle.strokeWidth(1);
		amap.setMyLocationStyle(myLocationStyle);
		amap.setLocationSource(this);
		amap.getUiSettings().setMyLocationButtonEnabled(true);
		amap.setMyLocationEnabled(true);
        amap.setOnCameraChangeListener(this);

	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(AMapLocation aLocation) {
		// TODO Auto-generated method stub
		if (mListener != null && aLocation != null && aLocation.getAMapException().getErrorCode() == 0) {
			mListener.onLocationChanged(aLocation);
		}
		deactivate();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		deactivate();
	}

	/**
	 * 停止定位
	 */
	@Override
	public void deactivate() {
		mListener = null;
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destroy();
		}
		mAMapLocationManager = null;
	}

	@OptionsItem
	void ok() {
		LatLng mTarget = amap.getCameraPosition().target;
		ThroughListActivity_.intent(this).ll(mTarget).start();
	}

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {

    }
}
