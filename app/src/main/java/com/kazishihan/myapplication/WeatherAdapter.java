package com.kazishihan.myapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kazishihan.myapplication.Weather.WeatherResult;
import com.squareup.picasso.Picasso;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {


    Context context;
    WeatherResult weatherResult;


    public WeatherAdapter(Context context, WeatherResult weatherResult) {
        this.context = context;
        this.weatherResult = weatherResult;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(context).inflate(R.layout.weither_items,viewGroup,false);
        return  new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Picasso.get().load(new StringBuilder("https://openweathermap.org/img/w/")
                .append(weatherResult.getList().get(i).getWeather().get(0).getIcon())
                .append(".png").toString()).into(viewHolder.weitherIcon);

        viewHolder.weitherDate.setText("Date: "+weatherResult.getList().get(i).getDt_txt());
        viewHolder.weitherDescription.setText("Status: "+weatherResult.getList().get(i).getWeather().get(0).getDescription());
        viewHolder.weitherTemp.setText(("Temp :"+weatherResult.getList().get(i).getMain().getTemp()+" °C"));
        viewHolder.weitherWind.setText("Wind :"+weatherResult.getList().get(i).getWind().getSpeed()+" km/h");

    }

    @Override
    public int getItemCount() {
        return weatherResult.getList().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        private ImageView weitherIcon;
        private TextView weitherDate,weitherTemp,weitherWind,weitherHumidity,weitherDescription;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            weitherIcon = itemView.findViewById(R.id.weatherItemIvId);
            weitherDate = itemView.findViewById(R.id.dateWeitherItemTvId);
            weitherTemp = itemView.findViewById(R.id.tempWeitherItemTvId);
            weitherWind = itemView.findViewById(R.id.windWeitherItemTvId);
            weitherDescription = itemView.findViewById(R.id.weitherDiscriptionTvId);
        }
    }
}
