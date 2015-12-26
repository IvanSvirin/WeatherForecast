package com.example.ivansv.weatherforecast;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Xml;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by ivansv on 24.12.2015.
 */
public class LoaderTask extends AsyncTask<Void, Void, Void> {
    Activity activity;
    AsyncTaskListener asyncTaskListener;
    ArrayList<ForecastItem> forecastItems;
    static InputStream is = null;
    static URL url = null;
    static HttpURLConnection urlConnection;
    boolean connection = true;

    public LoaderTask(Activity activity, AsyncTaskListener asyncTaskListener, ArrayList<ForecastItem> forecastItems) {
        this.activity = activity;
        this.asyncTaskListener = asyncTaskListener;
        this.forecastItems = forecastItems;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        loadXml("http://informer.gismeteo.ru/xml/29634_1.xml");
        return null;
    }

    private void loadXml(String urlString) {
        try {
            url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            is = new BufferedInputStream(urlConnection.getInputStream());
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(is, null);
            parseXml(parser);
            is.close();
            urlConnection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            connection = false;
        } catch (XmlPullParserException e) {
            e.printStackTrace();
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
                                    dayTime = "ночь";
                                    break;
                                case "09":
                                    dayTime = "утро";
                                    break;
                                case "15":
                                    dayTime = "день";
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
                            forecast.setPressure(average(parser.getAttributeValue(0), parser.getAttributeValue(1)));
                        }
                        if (name.equals("TEMPERATURE")) {
                            forecast.setTemperature(average(parser.getAttributeValue(0), parser.getAttributeValue(1)));
                        }
                        if (name.equals("WIND")) {
                            forecast.setWindSpeed(average(parser.getAttributeValue(0), parser.getAttributeValue(1)));
                            forecast.setWindDirection(parser.getAttributeValue(2));
                        }
                        if (name.equals("RELWET")) {
                            forecast.setWetness(average(parser.getAttributeValue(0), parser.getAttributeValue(1)));
                        }
                        if (name.equals("HEAT")) {
                            forecast.setRealFeel(average(parser.getAttributeValue(0), parser.getAttributeValue(1)));
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

    String average(String s1, String s2) {
        String s = null;
        int intS1 = Integer.parseInt(s1);
        int intS2 = Integer.parseInt(s2);
        int intS = (intS1 + intS2) / 2;
        s = String.valueOf(intS);
        return s;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (!connection) {
            Toast.makeText(activity, "Нет соединения! Проверьте подключение к интеренту!", Toast.LENGTH_SHORT).show();
        } else {
            asyncTaskListener.onAsyncTaskFinished();
        }
    }
}
