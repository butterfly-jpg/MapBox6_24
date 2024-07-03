package com.mapbox.mapboxandroiddemo.examples.location;

import static com.mapbox.mapboxsdk.style.expressions.Expression.eq;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.interpolate;
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;
import static com.mapbox.mapboxsdk.style.expressions.Expression.stop;
import static com.mapbox.mapboxsdk.style.layers.Property.VISIBLE;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillExtrusionBase;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillExtrusionColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillExtrusionHeight;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillExtrusionOpacity;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxandroiddemo.R;

import com.mapbox.mapboxandroiddemo.examples.dds.AddRainFallStyleActivity;
import com.mapbox.mapboxandroiddemo.utils.Mercator;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.expressions.Expression;
import com.mapbox.mapboxsdk.style.layers.FillExtrusionLayer;
import com.mapbox.mapboxsdk.style.layers.FillLayer;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.sources.VectorSource;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import timber.log.Timber;


/**
 * Use the LocationComponent to easily add a device location "puck" to a Mapbox map.
 */
public class LocationComponentActivity extends AppCompatActivity implements
        OnMapReadyCallback {

  //教四楼层切换按钮
  FloatingActionButton floor1;
  FloatingActionButton floor2;
  FloatingActionButton floor3;
  FloatingActionButton floor4;

  //科研楼楼层切换按钮
  FloatingActionButton kyFloor1;
  FloatingActionButton kyFloor9;


  private PermissionsManager permissionsManager;
  private MapboxMap mapboxMap;
  private MapView mapView;
  private int count = 0;

  //教四楼层遍历数组
  private String[] floors = {"floor1", "floor2", "floor3", "floor4"};

  //科研楼楼层遍历数组
  private String[] kyFloors = {"kyFloor1", "kyFloor9"};
  private static final String SOURCE_ID = "SOURCE_ID";
  private static final String ICON_ID = "R.drawable.mapbox_marker_icon_default";
  private static final String LAYER_ID = "LAYER_ID";

  /**
   * 所需变量
   * @param loadedMapStyle
   */
  private static final String URL = "http://101.200.74.161/position/get/50"; // 服务器API地址

  JSONObject jsonObject = null;
  private double MercatorX;//网站上的x坐标
  private double MercatorY;//网站上的y坐标

  private double pointX;//纬度
  private double pointY;//经度

  double RefLat = 39.089751991900954;


  private Handler handler;
  private Runnable dataFetcherRunnable;
  private ArrayList<Feature> symbolLayerIconFeatureList = new ArrayList<>();
  private FeatureCollection featureCollection;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Mapbox access token is configured here. This needs to be called either in your application
    // object or in the same activity which contains the mapview.
    // Mapbox访问令牌已在此处配置。 您需要在您的应用程序对象或包含地图视图的相同活动中调用它
    Mapbox.getInstance(this, getString(R.string.access_token));

    // This contains the MapView in XML and needs to be called after the access token is configured.
    // XML中的MapView需要在访问令牌配置之后调用
    //setContentView(R.layout.activity_location_component);
    setContentView(R.layout.activity_indoor_3d_map);

    floor1 = (FloatingActionButton) findViewById(R.id.floor1);
    floor2 = (FloatingActionButton) findViewById(R.id.floor2);
    floor3 = (FloatingActionButton) findViewById(R.id.floor3);
    floor4 = (FloatingActionButton) findViewById(R.id.floor4);

    kyFloor1 = (FloatingActionButton) findViewById(R.id.kyFloor1);
    kyFloor9 = (FloatingActionButton) findViewById(R.id.kyFloor9);

    mapView = findViewById(R.id.mapView);
    mapView.onCreate(savedInstanceState);
    mapView.getMapAsync(this);

    handler = new Handler(Looper.getMainLooper());

  }


  @Override
  public void onMapReady(@NonNull final MapboxMap mapboxMap) {
    this.mapboxMap = mapboxMap;

//      getJsonData();
//      symbolLayerIconFeatureList.add(Feature.fromGeometry(
//              Point.fromLngLat(pointY, pointX)));
      //准确坐标——经度116.35260254690593, 纬度39.96247516951781

      mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/mapbox/cjf4m44iw0uza2spb3q0a7s41")
              , new Style.OnStyleLoaded() {
        @Override
        public void onStyleLoaded(@NonNull Style style) { //onStyleLoaded 方法在地图样式加载完成后执行。

          //创建GeoJSON源
          //科研楼按钮
          try {
            GeoJsonSource courseRouteGeoJson = new GeoJsonSource(
                    "ky_floor_one", new URI("asset://kyf1.geojson"));
            style.addSource(courseRouteGeoJson);
          } catch (URISyntaxException exception) {
            Timber.d(exception);
          }
          try {
            GeoJsonSource courseRouteGeoJson = new GeoJsonSource(
                    "ky_floor_nine", new URI("asset://kyf9.geojson"));
            style.addSource(courseRouteGeoJson);
          } catch (URISyntaxException exception) {
            Timber.d(exception);
          }


          //教四按钮
          try {
            GeoJsonSource courseRouteGeoJson = new GeoJsonSource(
                    "floor_one", new URI("asset://f1.json"));
            style.addSource(courseRouteGeoJson);
          } catch (URISyntaxException exception) {
            Timber.d(exception);
          }

          try {
            GeoJsonSource courseRouteGeoJson = new GeoJsonSource(
                    "floor_two", new URI("asset://f2.json"));
            style.addSource(courseRouteGeoJson);
          } catch (URISyntaxException exception) {
            Timber.d(exception);
          }

          try {
            GeoJsonSource courseRouteGeoJson = new GeoJsonSource(
                    "floor_three", new URI("asset://f3.json"));
            style.addSource(courseRouteGeoJson);
          } catch (URISyntaxException exception) {
            Timber.d(exception);
          }

          try {
            GeoJsonSource courseRouteGeoJson = new GeoJsonSource(
                    "floor_four", new URI("asset://f4.json"));
            style.addSource(courseRouteGeoJson);
          } catch (URISyntaxException exception) {
            Timber.d(exception);
          }

          //创建GeoJSON源
//          GeoJsonSource geoJsonSource =
//                    new GeoJsonSource("SOURCE_ID");


          //创建符号图层SymbolLayer
//          SymbolLayer symbolLayer = new SymbolLayer("LAYER_ID", "SOURCE_ID");
//          symbolLayer.withProperties(
//                  iconImage("mapbox_marker_icon_default.png"),
//                  iconAllowOverlap(true),
//                  iconIgnorePlacement(true)
//          );
//          style.addLayer(symbolLayer);

          //在dataFetcherRunnable中更新数据源
          dataFetcherRunnable = new Runnable() {
            @Override
            public void run() {
              //获取数据
              getJsonData();
              //更新地图上的点
              symbolLayerIconFeatureList.add(Feature.fromGeometry(
                      Point.fromLngLat(pointY, pointX)));
              //featureCollection = FeatureCollection.fromFeatures(symbolLayerIconFeatureList);

              mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/mapbox/cjf4m44iw0uza2spb3q0a7s41")
                      .withImage(ICON_ID, BitmapFactory.decodeResource(
                              LocationComponentActivity.this.getResources(), R.drawable.mapbox_marker_icon_default))
                      .withSource(new GeoJsonSource(SOURCE_ID,
                              FeatureCollection.fromFeatures(symbolLayerIconFeatureList)))
                      .withLayer(new SymbolLayer(LAYER_ID, SOURCE_ID)
                              .withProperties(
                                      iconImage(ICON_ID),
                                      iconAllowOverlap(true),
                                      iconIgnorePlacement(true)
                              )
                      ),new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
//                  GeoJsonSource geoJsonSource = style.getSourceAs("SOURCE_ID");
//                  if(geoJsonSource != null){
//                    geoJsonSource.setGeoJson(featureCollection);
//                  }
                }
              });

              //每隔2秒重复执行
              handler.postDelayed(this, 2000);
            }
          };
          handler.post(dataFetcherRunnable);



          //按钮的点击事件具体实现
          //科研楼楼层切换
          kyFloor1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
              for (String kyFloor : kyFloors) {
                style.removeLayer(kyFloor);
              }
              style.addLayer(new FillExtrusionLayer("kyFloor1", "ky_floor_one").withProperties(
                      fillExtrusionColor(get("color")),
                      fillExtrusionHeight(get("height")),
                      fillExtrusionBase(get("base_height")),
                      fillExtrusionOpacity(0.5f)
              ));
            }
          });
          kyFloor9.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
              for (String kyFloor : kyFloors) {
                style.removeLayer(kyFloor);
              }
              style.addLayer(new FillExtrusionLayer("kyFloor9", "ky_floor_nine").withProperties(
                      fillExtrusionColor(get("color")),
                      fillExtrusionHeight(get("height")),
                      fillExtrusionBase(get("base_height")),
                      fillExtrusionOpacity(0.5f)
              ));
            }
          });

          //一层点击切换
          floor1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
              for (String floor : floors) {
                style.removeLayer(floor);
              }
              style.addLayer(new FillExtrusionLayer("floor1", "floor_one").withProperties(
                      fillExtrusionColor(get("color")),
                      fillExtrusionHeight(get("height")),
                      fillExtrusionBase(get("base_height")),
                      fillExtrusionOpacity(0.5f)
              ));
            }
          });

          //二层点击切换
          floor2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              for (String floor : floors) {
                style.removeLayer(floor);
              }
              style.addLayer(new FillExtrusionLayer("floor2", "floor_two").withProperties(
                      fillExtrusionColor(get("color")),
                      fillExtrusionHeight(get("height")),
                      fillExtrusionBase(get("base_height")),
                      fillExtrusionOpacity(0.5f)
              ));
            }
          });

          //三层点击切换
          floor3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              for (String floor : floors) {
                style.removeLayer(floor);
              }
              style.addLayer(new FillExtrusionLayer("floor3", "floor_three").withProperties(
                      fillExtrusionColor(get("color")),
                      fillExtrusionHeight(get("height")),
                      fillExtrusionBase(get("base_height")),
                      fillExtrusionOpacity(0.5f)
              ));
            }
          });

          //四层点击切换
          floor4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              for (String floor : floors) {
                style.removeLayer(floor);
              }
              style.addLayer(new FillExtrusionLayer("floor4", "floor_four").withProperties(
                      fillExtrusionColor(get("color")),
                      fillExtrusionHeight(get("height")),
                      fillExtrusionBase(get("base_height")),
                      fillExtrusionOpacity(0.5f)
              ));
            }
          });

          //enableLocationComponent(style);

        }

      });

  }

  /**
   * (1)解析JSON数据
   * (2)获取坐标点
   * (3)在地图上绘制轨迹线
   *
   */
  private void getJsonData(){
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          OkHttpClient client = new OkHttpClient();
          Request request = new Request.Builder()
                  .url(URL)
                  .build();
          Response response = client.newCall(request).execute();

          if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
          }

          String responseBody = response.body().string();
          jsonObject = new JSONObject(responseBody);
          System.out.println(jsonObject.toString());

          JSONObject data = jsonObject.getJSONObject("data");


          MercatorX = data.getDouble("x");
          MercatorY = data.getDouble("y");
          //double[] pointXYM = Mercator.Lonlat2Mercator(116.35260254690593, 39.96247516951781, RefLat);//
          System.out.println("墨卡托坐标为" + "MercatorX:" + MercatorX +"----" + "MercatorY:" + MercatorY);
          //System.out.println("墨卡托坐标为" + "MercatorX:" + pointXYM[0] +"----" + "MercatorY:" + pointXYM[1]);//
          double[] pointXY = Mercator.mercator2LonLat(MercatorX, MercatorY, RefLat);
          pointX = pointXY[0];
          pointY = pointXY[1];

          System.out.println("经纬度坐标为" + "经度pointX:" + pointX +"----" + "纬度pointY:" + pointY);

          // 在这里处理JSONObject，例如将数据传递给UI更新等
        } catch (IOException | JSONException e) {
          // 处理网络错误，例如连接失败、超时、服务器错误等
          e.printStackTrace();
          // 可以在这里通知用户网络错误
        }
      }
    }).start();


  }

//  @SuppressWarnings( {"MissingPermission"})
//  private void enableLocationComponent(@NonNull Style loadedMapStyle){
//    // Check if permissions are enabled and if not request
//    if (PermissionsManager.areLocationPermissionsGranted(this)) {
//
//      // Get an instance of the component
//      LocationComponent locationComponent = mapboxMap.getLocationComponent();
//
//      // Activate with options
//      locationComponent.activateLocationComponent(
//              LocationComponentActivationOptions.builder(this, loadedMapStyle).build());
//
//      // Enable to make component visible
//      locationComponent.setLocationComponentEnabled(false);
//
//      // Set the component's camera mode
//      locationComponent.setCameraMode(CameraMode.TRACKING);
//
//      // Set the component's render mode
//      locationComponent.setRenderMode(RenderMode.COMPASS);
//
//
//    } else {
//      permissionsManager = new PermissionsManager(this);
//      permissionsManager.requestLocationPermissions(this);
//    }
//  }
//
//  @Override
//  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//    permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
//  }
//
//  @Override
//  public void onExplanationNeeded(List<String> permissionsToExplain) {
//    Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
//  }
//
//  @Override
//  public void onPermissionResult(boolean granted) {
//    if (granted) {
//      mapboxMap.getStyle(new Style.OnStyleLoaded() {
//        @Override
//        public void onStyleLoaded(@NonNull Style style) {
//          try {
//            enableLocationComponent(style);
//          } catch (Exception e) {
//            throw new RuntimeException(e);
//          }
//        }
//      });
//    } else {
//      Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
//      finish();
//    }
//  }

  @Override
  @SuppressWarnings( {"MissingPermission"})
  protected void onStart() {
    super.onStart();
    mapView.onStart();
  }

  @Override
  protected void onResume() {
    super.onResume();
    mapView.onResume();
  }

  @Override
  protected void onPause() {
    super.onPause();
    mapView.onPause();
  }

  @Override
  protected void onStop() {
    super.onStop();
    mapView.onStop();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    mapView.onSaveInstanceState(outState);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mapView.onDestroy();
    handler.removeCallbacks(dataFetcherRunnable);
  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
    mapView.onLowMemory();
  }
}