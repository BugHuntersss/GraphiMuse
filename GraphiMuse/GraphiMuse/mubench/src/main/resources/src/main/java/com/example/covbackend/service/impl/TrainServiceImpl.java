package com.example.covbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.covbackend.entity.Station;
import com.example.covbackend.entity.Train;
import com.example.covbackend.mapper.TrainMapper;
import com.example.covbackend.service.CovDataService;
import com.example.covbackend.service.LocationService;
import com.example.covbackend.service.StationService;
import com.example.covbackend.service.TrainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TrainServiceImpl extends ServiceImpl<TrainMapper, Train> implements TrainService {
    @Autowired
    StationService stationService;

    @Autowired
    LocationService locationService;

    @Autowired
    CovDataService covDataService;


    public HashMap<String,Object> formatTrain(Train t, String from, String to) {
        HashMap<String, Object> train = new HashMap<>();
        StringBuilder info = new StringBuilder();
        String[] stops = t.getStops().split(",");
        List<String> stopList = new ArrayList<>();
        stopList.add(t.getFromStation());
        for (int j = 0; j < stops.length; j++) stopList.add(stops[j]);
        stopList.add(t.getToStation());
        int flag = 0;
        List<Map<String, Object>> station_data = new ArrayList<>();
        for (int j = 0; j < stopList.size(); j++) {
            Map<String, Object> location_data = new HashMap<>();
            if (stopList.get(j).equals(from)) train.put("from_index", j);
            if (stopList.get(j).equals(to)) train.put("to_index", j);

            Station station = new Station();

            station = stationService.getStationByName(stopList.get(j));
            if (station == null) {
                location_data.put("station", stopList.get(j));
                location_data.put("confirm", 0);
                location_data.put("now_confirm",0);
                location_data.put("confirm_add", 0);
                location_data.put("heal", 0);
                location_data.put("dead", 0);
                location_data.put("risk","低");
                station_data.add(location_data);
            } else {
                int location_id = station.getLocationId();
                if (location_id == -1) {
                    location_data.put("station", stopList.get(j));
                    location_data.put("confirm", 0);
                    location_data.put("now_confirm",0);
                    location_data.put("confirm_add", 0);
                    location_data.put("heal", 0);
                    location_data.put("dead", 0);
                    location_data.put("risk","低");
                } else {
                    location_data.putAll(covDataService.getDataByLocationIdWithCache(location_id));
                    location_data.put("station", stopList.get(j));
                }
                station_data.add(location_data);
                if(location_data.get("risk")!=null && location_data.get("risk").toString().equals("高")){
                    info.append(location_data.get("name").toString()+"是高风险地区\n");
                    flag=1;
                }
                if(location_data.get("risk")!=null && location_data.get("risk").toString().equals("中")){
                    info.append(location_data.get("name").toString()+"是中风险地区\n");
                    flag=1;
                }
            }
        }
        if(flag==0) info.append("该车次未经过风险地区,安全");
        train.put("station_data", station_data);
        train.put("train_no", t.getTrainNo());
        train.put("date", t.getDate());
        train.put("info",info.toString());
        return train;
    }

    public List<HashMap<String , Object>> getByFromAndTo(String from, String to){

        QueryWrapper<Train> queryWrapper = new QueryWrapper<>();
        String to_format = to+",";
        String from_format = from + ",";
        String stops = from + "," + "%" + to + ",";
        queryWrapper.eq("from_station",from).like("stops",to_format)
                    .or().eq("from_station",from).eq("to_station",to)
                    .or().like("stops",from_format).eq("to_station",to)
                    .or().like("stops",stops);

        List<Train> trainList = list(queryWrapper);

        List<HashMap<String,Object>> train_list = new ArrayList<>();
        for(int i=0; i<trainList.size(); i++){
            Train t = trainList.get(i);
            train_list.add(formatTrain(t,from,to));
        }

        return train_list;
    }

    public List<HashMap<String, Object>> getByKeyword(String keyword, String from, String to){
        QueryWrapper<Train> queryWrapper = new QueryWrapper<>();

        queryWrapper.like("train_no",keyword).
                and(QueryWrapper->QueryWrapper.eq("from_station",from).like("stops",to+",")
                .or().eq("from_station",from).eq("to_station",to)
                .or().like("stops",from+",").eq("to_station",to)
                .or().like("stops",from+",%"+to+","));

        List<Train> trainList = list(queryWrapper);

        List<HashMap<String,Object>> train_list = new ArrayList<>();
        for(int i=0; i<trainList.size(); i++){
            Train t = trainList.get(i);
            train_list.add(formatTrain(t,from,to));
        }

        return train_list;
    }


}
