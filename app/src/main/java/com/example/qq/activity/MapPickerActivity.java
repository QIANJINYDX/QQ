package com.example.qq.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import androidx.appcompat.app.AppCompatActivity;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.example.qq.Application.MyApplication;
import com.example.qq.R;
import com.example.qq.adapter.MapPickerAdapter;
import com.example.qq.util.BitmapLoader;
import com.example.qq.Service.LocationService;

import java.util.ArrayList;
import java.util.UUID;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.model.Conversation;


public class MapPickerActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private String TAG="????????????";
    private ListView list;
    private TextView status;
    private ProgressBar loading;
    private View defineMyLocationButton;
    private LinearLayout ll_title;

    //??????????????????
    private LocationService locationService;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    // ??????????????????????????????
    private LatLng mLoactionLatLng;
    private String mAddress;
    private String mStreet;
    private String mName;
    private String mCity;
    // ???????????????????????????
    private boolean isFirstLoc = true;
    // MapView???????????????????????????
    private Point mCenterPoint = null;
    // ????????????
    private GeoCoder mGeoCoder = null;
    // ????????????
    MapPickerAdapter mAdapter;
    ArrayList<PoiInfo> mInfoList;
    PoiInfo mCurentInfo;
    private View mPopupView;
    Conversation conv;
    protected int mWidth;
    protected int mHeight;
    protected float mDensity;
    protected int mDensityDpi;

    private double mLatitude;
    private double mLongitude;
    private LinearLayout linearLayout;
    private RelativeLayout relativeLayout;
    private boolean mSendLocation;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.setAgreePrivacy(getApplicationContext(),true);
        SDKInitializer.initialize(getApplicationContext());
        SDKInitializer.setCoordType(CoordType.BD09LL);
        LocationClient.setAgreePrivacy(true);
        setContentView(R.layout.activity_map_picker);
        try {
            locationService = new LocationService(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        locationService.registerListener(mListener);//???????????????onStart?????????
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mDensity = dm.density;
        mDensityDpi = dm.densityDpi;
        mWidth = dm.widthPixels;
        mHeight = dm.heightPixels;

        mPopupView = LayoutInflater.from(this).inflate(R.layout.location_popup_layout, null);

        linearLayout = (LinearLayout) findViewById(R.id.listNearbyHolder);
        relativeLayout = (RelativeLayout) findViewById(R.id.mapholder);
        defineMyLocationButton = findViewById(R.id.define_my_location);
        ll_title = findViewById(R.id.tl_title);

        initMap();
        initIntent();
    }
    //?????????????????????
    private void initIntent() {
        Intent intent = getIntent();
        mSendLocation = intent.getBooleanExtra("sendLocation", false);
        if (mSendLocation) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    MapPickerActivity.this.getResources().getDimensionPixelOffset(R.dimen.location));
            relativeLayout.setLayoutParams(params);
            defineMyLocationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    turnBack();
                }
            });
            TextView tv_title=ll_title.findViewById(R.id.tv_title);
            TextView tv_forward=ll_title.findViewById(R.id.tv_forward);
            tv_title.setText("????????????");
            tv_forward.setText("??????");
            tv_forward.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "????????????");
                    Log.d(TAG, String.valueOf(mLongitude));
                    Log.d(TAG, String.valueOf(mLatitude));
                    Log.d(TAG, String.valueOf(mCurentInfo.address));
                    if (mLoactionLatLng != null) {
                        int left = mWidth / 4;
                        int top = (int) (mHeight - 1.1 * mWidth);
                        Rect rect = new Rect(left, top, mWidth - left, mHeight - (int) (1.2 * top));
                        mBaiduMap.snapshotScope(rect, new BaiduMap.SnapshotReadyCallback() {
                            @Override
                            public void onSnapshotReady(Bitmap bitmap) {
                                if (null != bitmap) {
                                    String fileName = UUID.randomUUID().toString();
                                    String path = BitmapLoader.saveBitmapToLocal(bitmap, fileName);
                                    Intent intent = new Intent();
                                    intent.putExtra("latitude", mLatitude);
                                    intent.putExtra("longitude", mLongitude);
                                    intent.putExtra("mapview", mMapView.getMapLevel());
                                    intent.putExtra("address", mCurentInfo.address);
                                    intent.putExtra("path", path);
                                    setResult(MyApplication.RESULT_CODE_SEND_LOCATION, intent);
                                    finish();
                                } else {
                                    Toast.makeText(MapPickerActivity.this, "?????????????????????????????????",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });

        } else {//???????????????
            TextView tv_title=ll_title.findViewById(R.id.tv_title);
            TextView tv_forward=ll_title.findViewById(R.id.tv_forward);
            tv_title.setText(intent.getStringExtra("locDesc"));
            tv_forward.setText(intent.getStringExtra(""));
            locationService.unregisterListener(mListener);
            defineMyLocationButton.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            relativeLayout.setLayoutParams(params);
            findViewById(R.id.root).setBackgroundColor(Color.parseColor("#ffffff"));
            double latitude = intent.getDoubleExtra("latitude", 0);
            double longitude = intent.getDoubleExtra("longitude", 0);
            MyLocationData locationData = new MyLocationData.Builder()
                    .accuracy(100).direction(90.f).latitude(latitude).longitude(longitude).build();
            mBaiduMap.setMyLocationData(locationData);
            mBaiduMap.setMyLocationEnabled(true);
            LatLng ll = new LatLng(latitude, longitude);
            BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(R.drawable.oval);
            OverlayOptions options = new MarkerOptions().position(ll).icon(descriptor).zIndex(10);
            mBaiduMap.addOverlay(options);
            turnBack();
        }
    }
    //???????????????
    private void initMap() {
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mMapView.showZoomControls(false);
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(17.0f);
        mBaiduMap.setMapStatus(msu);
        mBaiduMap.setOnMapTouchListener(touchListener);
        // ?????????POI????????????
        mInfoList = new ArrayList<PoiInfo>();
        // ???????????????MapView????????????????????????????????????????????????
        mCenterPoint = mBaiduMap.getMapStatus().targetScreen;
        mLoactionLatLng = mBaiduMap.getMapStatus().target;
        // ??????
        mBaiduMap.setMyLocationEnabled(true);
        // ????????????logo ZoomControl
        int count = mMapView.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = mMapView.getChildAt(i);
            if (child instanceof ImageView || child instanceof ZoomControls) {
                child.setVisibility(View.INVISIBLE);
            }
        }
        // ???????????????
        //mMapView.showScaleControl(false);
        // ????????????
        mGeoCoder = GeoCoder.newInstance();
        mGeoCoder.setOnGetGeoCodeResultListener(GeoListener);
        list = (ListView) findViewById(R.id.list);
        list.setOnItemClickListener(this);
        list.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        loading = (ProgressBar) findViewById(R.id.loading);
        status = (TextView) findViewById(R.id.status);
        mAdapter = new MapPickerAdapter(MapPickerActivity.this, mInfoList);
        list.setAdapter(mAdapter);
    }
    //????????????????????????
    public void turnBack() {
        MyLocationData location = mBaiduMap.getLocationData();
        // ??????????????????
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(new LatLng(location.latitude, location.longitude));
        mBaiduMap.animateMapStatus(u);
        mBaiduMap.clear();
        // ???????????????????????????
        mGeoCoder.reverseGeoCode((new ReverseGeoCodeOption())
                .location(new LatLng(location.latitude, location.longitude)));
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        if (id == R.id.menu_send) {
            if (mLoactionLatLng != null) {
                int left = mWidth / 4;
                int top = (int) (mHeight - 1.1 * mWidth);
                Rect rect = new Rect(left, top, mWidth - left, mHeight - (int) (1.2 * top));
                mBaiduMap.snapshotScope(rect, new BaiduMap.SnapshotReadyCallback() {
                    @Override
                    public void onSnapshotReady(Bitmap bitmap) {
                        if (null != bitmap && null != conv) {
                            String fileName = UUID.randomUUID().toString();
                            String path = BitmapLoader.saveBitmapToLocal(bitmap, fileName);
                            Intent intent = new Intent();
                            intent.putExtra("latitude", mLatitude);
                            intent.putExtra("longitude", mLongitude);
                            intent.putExtra("mapview", mMapView.getMapLevel());
                            intent.putExtra("street", mStreet);
                            intent.putExtra("path", path);
                            setResult(MyApplication.RESULT_CODE_SEND_LOCATION, intent);
                            finish();
                        } else {
                            Toast.makeText(MapPickerActivity.this, "?????????????????????????????????",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mSendLocation) {
            getMenuInflater().inflate(R.menu.picker_map, menu);
            return true;
        } else {
            return false;
        }
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        // ?????????????????????position???item????????????
        mAdapter.setNotifyTip(position);
        mAdapter.notifyDataSetChanged();
        BitmapDescriptor mSelectIco = BitmapDescriptorFactory
                .fromResource(R.drawable.picker_map_geo_icon);
        mBaiduMap.clear();
        PoiInfo info = (PoiInfo) mAdapter.getItem(position);
        LatLng la = info.location;
        // ????????????
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(la);
        mBaiduMap.animateMapStatus(u);
        // ???????????????
        OverlayOptions ooA = new MarkerOptions().position(la)
                .icon(mSelectIco).anchor(0.5f, 0.5f);
        mBaiduMap.addOverlay(ooA);
        mLoactionLatLng = info.location;
        mAddress = info.address;
        mName = info.name;
        mCity = info.city;
        mLatitude = info.location.latitude;
        mLongitude = info.location.longitude;
        mStreet = info.name;//??????????????????????????????
    }

    /***
     * Stop location service
     */
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        locationService.stop(); // ??????????????????
        super.onStop();
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        // -----------location config ------------

        // ??????locationservice????????????????????????????????????1???location???????????????????????????????????????????????????activity?????????????????????????????????locationservice?????????

        // ????????????
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());

    }

    @Override
    protected void onResume() {
        super.onResume();
        //???activity??????onResume?????????mMapView. onResume ()?????????????????????????????????
        locationService.start();
        mMapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //???activity??????onDestroy?????????mMapView.onDestroy()?????????????????????????????????
        locationService.unregisterListener(mListener); // ???????????????
        locationService.stop();
        mMapView.onDestroy();
        mGeoCoder.destroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //???activity??????onPause?????????mMapView. onPause ()?????????????????????????????????
        locationService.stop();
        mMapView.onPause();
    }

    /*****
     * ???????????????????????????onReceiveLocation???????????????????????????????????????????????????????????????
     */
    private BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                //

                MyLocationData data = new MyLocationData.Builder()//
                        // .direction(mCurrentX)//
                        .accuracy(location.getRadius())//
                        .latitude(location.getLatitude())//
                        .longitude(location.getLongitude())//
                        .build();
                mBaiduMap.setMyLocationData(data);
                // ?????????????????????
                MyLocationConfiguration config = new MyLocationConfiguration(
                        MyLocationConfiguration.LocationMode.NORMAL, true, null);
                mBaiduMap.setMyLocationConfigeration(config);
                mAddress = location.getAddrStr();
                mName = location.getStreet();
                mCity = location.getCity();

                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                mLoactionLatLng = currentLatLng;
                // ?????????????????????
                if (isFirstLoc) {
                    isFirstLoc = false;
                    // ??????????????????
                    MapStatusUpdate u = MapStatusUpdateFactory
                            .newLatLng(currentLatLng);
                    mBaiduMap.animateMapStatus(u);
                    mGeoCoder.reverseGeoCode((new ReverseGeoCodeOption())
                            .location(currentLatLng));
                }
            }

        }

    };
    // ?????????????????????
    OnGetGeoCoderResultListener GeoListener = new OnGetGeoCoderResultListener() {
        public void onGetGeoCodeResult(GeoCodeResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                // ?????????????????????
            }
            // ????????????????????????
        }

        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                // ????????????????????????
                status.setText(R.string.picker_internalerror);
                status.setVisibility(View.VISIBLE);
            }
            // ??????????????????????????????
            else {
                status.setVisibility(View.GONE);
                // ??????????????????
                Log.d(TAG, String.valueOf(result.getPoiList()));
                mLoactionLatLng = result.getLocation();
                mAddress = result.getAddress();
                mName = result.getAddressDetail().street;
                mStreet = result.getAddressDetail().street;
                mCity = result.getAddressDetail().city;

                //????????????.????????????????????????????????????.??????????????????????????????listView???????????????
                mLatitude = result.getLocation().latitude;
                mLongitude = result.getLocation().longitude;

                mCurentInfo = new PoiInfo();
                mCurentInfo.address = result.getAddress();
                mCurentInfo.location = result.getLocation();
                mCurentInfo.name = "[????????????]";
                mInfoList.clear();
                mInfoList.add(mCurentInfo);
                // ????????????????????????
                if (result.getPoiList() != null) {
                    mInfoList.addAll(result.getPoiList());
                }
                mAdapter.setNotifyTip(0);
                // ???????????????????????????
                mAdapter.notifyDataSetChanged();
                loading.setVisibility(View.GONE);

            }
        }
    };
    // ???????????????????????????
    BaiduMap.OnMapTouchListener touchListener = new BaiduMap.OnMapTouchListener() {
        @Override
        public void onTouch(MotionEvent event) {
            // TODO Auto-generated method stub
            if (event.getAction() == MotionEvent.ACTION_UP) {

                if (mCenterPoint == null) {
                    return;
                }
                // ????????????MapView???????????????????????????????????????(??????????????????????????????????????????)
                mCenterPoint = mBaiduMap.getMapStatus().targetScreen;
                LatLng currentLatLng = mBaiduMap.getProjection().fromScreenLocation(mCenterPoint);
                // ???????????????????????????
                mGeoCoder.reverseGeoCode((new ReverseGeoCodeOption())
                        .location(currentLatLng));
                loading.setVisibility(View.VISIBLE);

            }
        }
    };


}
