package com.example.ivansv.weatherforecast;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Xml;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by ivansv on 24.12.2015.
 */
public class LoaderTask extends AsyncTask<Void, Void, Void> {
    private Activity activity;
    private AsyncTaskListener asyncTaskListener;
    private ArrayList<ForecastItem> forecastItems;
    private URL url;
    private InputStream inputStream;
    private HttpURLConnection urlConnection;
    private boolean isConnection = true;
    private Place place;
    public static final int ACCESS_LOCATION_PERMISSION = 1;
    public static final String API_KEY = "138b3901759ae9758770c6acef29b4a7";

    public static final String TAG_MAIN = "main";
    public static final String TAG_TEMP = "temp";
    public static final String TAG_HUMIDITY = "humidity";
    public static final String TAG_PRESSURE = "pressure";

    public static final String TAG_WIND = "wind";
    public static final String TAG_SPEED = "speed";

    public static final String TAG_CLOUDS = "clouds";
    public static final String TAG_ALL = "all";

    public static final String TAG_SNOW = "snow";
    public static final String TAG_3H = "3h";

    public static final String TAG_NAME = "name";

    public LoaderTask(Activity activity, AsyncTaskListener asyncTaskListener, ArrayList<ForecastItem> forecastItems, Place place) {
        this.activity = activity;
        this.asyncTaskListener = asyncTaskListener;
        this.forecastItems = forecastItems;
        this.place = place;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        getPlace(place);
        JSONObject jsonObject = getJSONFromUrl(place);
        getDataFromJson(jsonObject);
//        loadXml("http://informer.gismeteo.ru/xml/29634_1.xml");
        return null;
    }

    private void getDataFromJson(JSONObject jsonObject) {
        try {
            JSONObject main = jsonObject.getJSONObject(TAG_MAIN);
            String temperature = main.getString(TAG_TEMP);
            String humidity = main.getString(TAG_HUMIDITY);
            String pressure = main.getString(TAG_PRESSURE);

            JSONObject wind = jsonObject.getJSONObject(TAG_WIND);
            String speed = wind.getString(TAG_SPEED);

            JSONObject clouds = jsonObject.getJSONObject(TAG_CLOUDS);
            String all = clouds.getString(TAG_ALL);

//            JSONObject snow = jsonObject.getJSONObject(TAG_SNOW);
//            String threeHours = snow.getString(TAG_3H);

            ForecastItem forecastItem = new ForecastItem();
            forecastItem.setTemperature(temperature);
            forecastItem.setWetness(humidity);
            forecastItem.setPressure(pressure);
            forecastItem.setWindSpeed(speed);
            forecastItem.setCloudiness(all);
            forecastItem.setPrecipitation("0");
            place.setForecastItem(forecastItem);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getJSONFromUrl(Place place) {
        String jsonString = "";
        JSONObject jObj = null;
        try {
            url = new URL("http://api.openweathermap.org/data/2.5/weather?lat=" + String.valueOf(place.getLatitude())
                    + "&lon=" + String.valueOf(place.getLongitude()) + "&APPID=" + API_KEY);
            urlConnection = (HttpURLConnection) url.openConnection();
            inputStream = new BufferedInputStream(urlConnection.getInputStream());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            inputStream.close();
            urlConnection.disconnect();
            jsonString = sb.toString();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        try {
            jObj = new JSONObject(jsonString);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        return jObj;
    }

    private void getPlace(Place place) {

        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, ACCESS_LOCATION_PERMISSION);
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        place.setLatitude(location.getLatitude());
        place.setLongitude(location.getLongitude());


//        Geocoder geocoder = new Geocoder(activity);
//        try {
//            List<Address> addressList = geocoder.getFromLocation(place.getLatitude(), place.getLongitude(), 5);
//            if (addressList.size() > 0) {
//                Address address = addressList.get(0);
//                place.setName(address.getCountryName() + "\n"
//                        + address.getLocality() + "\n"
//                        + address.getThoroughfare() + "\n"
//                        + address.getSubThoroughfare());
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private void loadXml(String urlString) {
        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            inputStream = new BufferedInputStream(urlConnection.getInputStream());
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(inputStream, null);
            parseXml(parser);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            isConnection = false;
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
    }

    private void parseXml(XmlPullParser parser) {
        ForecastItem forecast = new ForecastItem();
        try {
            while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
                switch (parser.getEventType()) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        String name = parser.getName();
                        if (name.equals("FORECAST")) {
                            forecast.setDate(parser.getAttributeValue(0) + "." + parser.getAttributeValue(1));
                            String dayTime = null;
                            switch (parser.getAttributeValue(3)) {
                                case "03":
                                    dayTime = "ночь  ";
                                    break;
                                case "09":
                                    dayTime = "утро  ";
                                    break;
                                case "15":
                                    dayTime = "день  ";
                                    break;
                                case "21":
                                    dayTime = "вечер";
                                    break;
                            }
                            forecast.setDayTime(dayTime);
                        }
                        if (name.equals("PHENOMENA")) {
                            String cloudiness = null;
                            if (parser.getAttributeValue(0).equals("0")) {
                                cloudiness = "ясно";
                            }
                            if (parser.getAttributeValue(0).equals("1") || parser.getAttributeValue(0).equals("2") ||
                                    parser.getAttributeValue(0).equals("3")) {
                                cloudiness = "малооблачно";
                            }
                            if (parser.getAttributeValue(0).equals("4") || parser.getAttributeValue(0).equals("5") ||
                                    parser.getAttributeValue(0).equals("6") || parser.getAttributeValue(0).equals("7")) {
                                cloudiness = "облачно";
                            }
                            if (parser.getAttributeValue(0).equals("8") || parser.getAttributeValue(0).equals("9") ||
                                    parser.getAttributeValue(0).equals("10")) {
                                cloudiness = "пасмурно";
                            }
                            forecast.setCloudiness(cloudiness);
                            forecast.setPrecipitation(parser.getAttributeValue(1));
                        }
                        if (name.equals("PRESSURE")) {
                            forecast.setPressure(average(parser.getAttributeValue(0), parser.getAttributeValue(1), false));
                        }
                        if (name.equals("TEMPERATURE")) {
                            forecast.setTemperature(average(parser.getAttributeValue(0), parser.getAttributeValue(1), true));
                        }
                        if (name.equals("WIND")) {
                            forecast.setWindSpeed(average(parser.getAttributeValue(0), parser.getAttributeValue(1), false));
                            String windDirection = null;
                            switch (parser.getAttributeValue(2)) {
                                case "0":
                                    windDirection = "северный";
                                    break;
                                case "1":
                                    windDirection = "северо-восточный";
                                    break;
                                case "2":
                                    windDirection = "восточный";
                                    break;
                                case "3":
                                    windDirection = "юго-восточный";
                                    break;
                                case "4":
                                    windDirection = "южный";
                                    break;
                                case "5":
                                    windDirection = "юго-западный";
                                    break;
                                case "6":
                                    windDirection = "западный";
                                    break;
                                case "7":
                                    windDirection = "северо-западный";
                                    break;
                            }
                            forecast.setWindDirection(windDirection);
                        }
                        if (name.equals("RELWET")) {
                            forecast.setWetness(average(parser.getAttributeValue(0), parser.getAttributeValue(1), false));
                        }
                        if (name.equals("HEAT")) {
                            forecast.setRealFeel(average(parser.getAttributeValue(0), parser.getAttributeValue(1), true));
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals("FORECAST")) {
                            forecastItems.add(forecast);
                            forecast = new ForecastItem();
                        }
                        break;
                }
                parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String average(String s1, String s2, boolean isTemperature) {
        String s;
        int intS1 = Integer.parseInt(s1);
        int intS2 = Integer.parseInt(s2);
        int intS = (intS1 + intS2) / 2;
        if (isTemperature && intS > 0) {
            s = "+" + String.valueOf(intS);
        } else {
            s = String.valueOf(intS);
        }
        return s;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        asyncTaskListener.onAsyncTaskFinished();
//        if (!isConnection || inputStream == null || forecastItems.size() < 4) {
//            Toast.makeText(activity, "Нет соединения! Проверьте подключение к интеренту!", Toast.LENGTH_SHORT).show();
//        } else {
//            asyncTaskListener.onAsyncTaskFinished();
//        }
    }
}
