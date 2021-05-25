package sk.fri.uniza;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import sk.fri.uniza.api.WeatherStationService;
import sk.fri.uniza.model.WeatherData;

import java.util.List;

public class IotNode {
    private final Retrofit retrofit;
    private final WeatherStationService weatherStationService;

    public IotNode() {

        retrofit = new Retrofit.Builder()
                // Url adresa kde je umietnená WeatherStation služba
                .baseUrl("http://localhost:9000/")
                // Na konvertovanie JSON objektu na java POJO použijeme
                // Jackson knižnicu
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        // Vytvorenie inštancie komunikačného rozhrania
        weatherStationService = retrofit.create(WeatherStationService.class);

    }

    public WeatherStationService getWeatherStationService() {
        return weatherStationService;
    }

    private double calculateAverage(List<WeatherData> list){

        double average = 0.0;
        for (WeatherData i:list) {
            average += i.getAirTemperature();
        }

        average = average / list.size();
        return average;
    }

    public double getAverageTemperature(String station,String from, String to){


       try {
           Call<List<WeatherData>> historyWeather = weatherStationService.getHistoryWeather(station, from, to, List.of("airTemperature"));
               Response<List<WeatherData>> response = historyWeather.execute();

               if (response.isSuccessful()) { // Dotaz na server bol neúspešný
                   //Získanie údajov vo forme inštancie triedy WeatherData
                   List<WeatherData> data = response.body();
                   return  calculateAverage(data);
               }
       }
       catch (Exception e)
       {
           e.printStackTrace();
       }

       return  0.0;
    }

}
