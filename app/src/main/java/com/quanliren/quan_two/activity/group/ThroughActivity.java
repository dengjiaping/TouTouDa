package com.quanliren.quan_two.activity.group;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
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
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
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
        AMapLocationListener, AMap.OnCameraChangeListener, GeocodeSearch.OnGeocodeSearchListener {
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
            onCameraChangeFinish(null);
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
        tv_position.setVisibility(View.GONE);
    }

    private LatLng searchLL = null;

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        GeocodeSearch geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
        LatLng mTarget = amap.getCameraPosition().target;
        searchLL = mTarget;
        LatLonPoint lp = new LatLonPoint(mTarget.latitude, mTarget.longitude);
        RegeocodeQuery query = new RegeocodeQuery(lp, 200, GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query);
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        LatLng mTarget = amap.getCameraPosition().target;
        if (mTarget.longitude == searchLL.longitude && mTarget.latitude == searchLL.latitude) {
            if (rCode == 0) {
                if (result != null && result.getRegeocodeAddress() != null
                        && result.getRegeocodeAddress().getFormatAddress() != null) {
                    String addressName = result.getRegeocodeAddress().getFormatAddress();
                    tv_position.setVisibility(View.VISIBLE);
                    tv_position.setText(addressName);
                } else {
                    tv_position.setVisibility(View.GONE);
                }
            } else {
            }
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }
}
