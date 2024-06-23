package com.mapbox.mapboxandroiddemo.examples.location;

import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillExtrusionBase;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillExtrusionColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillExtrusionHeight;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillExtrusionOpacity;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxandroiddemo.R;

import com.mapbox.mapboxsdk.Mapbox;
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
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;


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
        OnMapReadyCallback, PermissionsListener {

  FloatingActionButton floor1;
  FloatingActionButton floor2;
  FloatingActionButton floor3;
  FloatingActionButton floor4;

  private PermissionsManager permissionsManager;
  private MapboxMap mapboxMap;
  private MapView mapView;
  private int count = 0;

  private String[] floors = {"floor1", "floor2", "floor3", "floor4"};

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

    floor1 = (FloatingActionButton)findViewById(R.id.floor1);
    floor2 = (FloatingActionButton)findViewById(R.id.floor2);
    floor3 = (FloatingActionButton)findViewById(R.id.floor3);
    floor4 = (FloatingActionButton)findViewById(R.id.floor4);

    mapView = findViewById(R.id.mapView);
    mapView.onCreate(savedInstanceState);
    mapView.getMapAsync(this);
  }

  @Override
  public void onMapReady(@NonNull final MapboxMap mapboxMap) {
    LocationComponentActivity.this.mapboxMap = mapboxMap;

    mapboxMap.setStyle(Style.MAPBOX_STREETS,
            new Style.OnStyleLoaded() {
              @Override
              public void onStyleLoaded(@NonNull Style style) {
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

                enableLocationComponent(style);
                getJsonData();
//                try {
//                  getLocationComponent(style);
//                } catch (Exception e) {
//                  throw new RuntimeException(e);
//                }
              }
            });
  }

//  private String urlPost = "http://101.200.74.161/update";
//  private String urlGet = "http://101.200.74.161/get/1";  //一号终端为4，二号终端为7
//  private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//  private volatile boolean Startpost = true;
//  private static final OkHttpClient client = new OkHttpClient();
//  public static Response post(String url, String json) throws IOException {
//    RequestBody body = RequestBody.create(JSON, json);
//    Request request = new Request.Builder()
//            .url(url)
//            .post(body)
//            .build();
//    Response response = client.newCall(request).execute();
//    return response;
//  }


  /**
   * 所需变量
   * @param loadedMapStyle
   */
  private static final String URL = "http://101.200.74.161/position/get/1"; // 服务器API地址
  JSONObject jsonObject = null;
  private double MercatorX;//网站上的x坐标
  private double MercatorY;//网站上的y坐标

  private double pointX;//纬度
  private double pointY;//经度

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

          System.out.println("墨卡托坐标为" + "MercatorX:" + MercatorX +"----" + "MercatorY:" + MercatorY);




          //绘制轨迹
          // 坐标点列表，这里是经纬度
          List<LatLng> points = new ArrayList<>();
          //points.add(new LatLng(y, x));

          drawPolyline(points);


          // 在这里处理JSONObject，例如将数据传递给UI更新等
        } catch (IOException e) {
          // 处理网络错误，例如连接失败、超时、服务器错误等
          e.printStackTrace();
          // 可以在这里通知用户网络错误
        } catch (JSONException e) {
          // 处理解析JSON错误，例如返回的不是有效的JSON格式
          e.printStackTrace();
          // 可以在这里记录日志或通知用户数据格式错误
        } finally {
          try {
            Thread.sleep(2000);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
        }
      }
    }).start();


  }

  private void drawPolyline(List<LatLng> points){
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
          // 当前线程是UI线程，可以安全地进行UI操作
          // 绘制轨迹的代码放在这里
          PolylineOptions polylineOptions = new PolylineOptions()
                  .addAll(points)
                  .color(Color.BLUE)
                  .width(10);
          mapboxMap.addPolyline(polylineOptions);
        } else {
          // 当前线程不是UI线程，需要切换到UI线程
          runOnUiThread(new Runnable() {
            @Override
            public void run() {
              // 在UI线程上执行绘制轨迹的代码
            }
          });
        }
      }
    });


  }

  @SuppressWarnings( {"MissingPermission"})
  private void enableLocationComponent(@NonNull Style loadedMapStyle){
    // Check if permissions are enabled and if not request
    if (PermissionsManager.areLocationPermissionsGranted(this)) {

      // Get an instance of the component
      LocationComponent locationComponent = mapboxMap.getLocationComponent();

      // Activate with options
      locationComponent.activateLocationComponent(
              LocationComponentActivationOptions.builder(this, loadedMapStyle).build());

      // Enable to make component visible
      locationComponent.setLocationComponentEnabled(true);

      // Set the component's camera mode
      locationComponent.setCameraMode(CameraMode.TRACKING);

      // Set the component's render mode
      locationComponent.setRenderMode(RenderMode.COMPASS);

//      OkHttpClient client = new OkHttpClient();
//      Request request = new Request.Builder()
//              .url(URL)
//              .build();
//      Response response = client.newCall(request).execute();
//      String responseBody = response.body().string();
//      jsonObject = new JSONObject(responseBody);

    } else {
      permissionsManager = new PermissionsManager(this);
      permissionsManager.requestLocationPermissions(this);
    }

    //System.out.println(jsonObject.toString());
  }







    //JSONObject json = new JSONObject();
//    while (true) {
//      try {
//        //获取室外的经纬度
//        //获取数据包
//        PackageInfo packageInfo;
//        packageInfo = DataBuffer.obtainPackageInfo();
//
//        //获取时间戳
//        Date d = new Date();
//        String dateString = "";
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd/hh/mm/ss/SSS");
//        dateString = formatter.format(d);
////                Log.d(TAG, dateString);
////                double[] endxy = {m.sqlX , m.sqlY };
////                double[] tempxy = GetEndPointByTrigonometric.getEndPointByTrigonometric(endxy);
//
//        //39.961895, 116.356236
//        if (Startpost) {
//          json.put("type", "@JHCOORD");
//          json.put("time", dateString);
////                    json.put("deviceID", position.getAutoinfo());
//          json.put("deviceID", 0);
//
//          json.put("dimension", 3);
//          json.put("mapID", 0);
////                    json.put("mapID",packageInfo.getBuildIdString());
//
//          json.put("x", packageInfo.getMercatorX());
////                    json.put("x",39.961895 );
//          json.put("y", packageInfo.getMercatorY());
////                    json.put("y", 116.356236);
//          json.put("z", 0);
////                    json.put("floor", packageInfo.getFloor());
//          json.put("floor", packageInfo.getFloor());
//          json.put("userID", 1);
//
//          post(urlPost, String.valueOf(json));
//          //Log.d("step_length", "" + step_length);
//
//          Thread.sleep(1500);
//        }
//
////        Response response = get(urlGet);
////        String responseData = response.body().string();
//////                Log.d(TAG, responseData);
////
////        user = parseJSONWithGSONObject(responseData);
//        } catch (Exception e) {
////        Log.d(TAG, "出错1");
//      }
//    }




  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }

  @Override
  public void onExplanationNeeded(List<String> permissionsToExplain) {
    Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
  }

  @Override
  public void onPermissionResult(boolean granted) {
    if (granted) {
      mapboxMap.getStyle(new Style.OnStyleLoaded() {
        @Override
        public void onStyleLoaded(@NonNull Style style) {
          try {
            enableLocationComponent(style);
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        }
      });
    } else {
      Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
      finish();
    }
  }

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
  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
    mapView.onLowMemory();
  }
}