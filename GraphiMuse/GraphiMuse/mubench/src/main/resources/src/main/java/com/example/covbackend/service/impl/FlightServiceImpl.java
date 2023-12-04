package com.example.covbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.covbackend.entity.Airport;
import com.example.covbackend.entity.Flight;
import com.example.covbackend.entity.Location;
import com.example.covbackend.mapper.FlightMapper;
import com.example.covbackend.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FlightServiceImpl extends ServiceImpl<FlightMapper, Flight> implements FlightService {

    @Autowired
    AirportServiceImpl airportServiceImpl;

    @Autowired
    CovDataServiceImpl covDataServiceImpl;

    @Autowired
    LocationServiceImpl locationServiceImpl;

    /**
     * 根据fromLocationId和toLocationId查询两个地方之间的航班，返回一个flight的list
     * 没有找到则返回一个空ArrayList
     * @param fromLocationId
     * @param toLocationId
     * @return
     */
    @Override
    public List<Map<String, Object>> getFlightsByCityAndKeywords(int fromLocationId, int toLocationId, String keywords) {
        List<String> fromAirports = airportServiceImpl.getAirPortsNameByLocationId(fromLocationId);
        List<String> toAirports = airportServiceImpl.getAirPortsNameByLocationId(toLocationId);
        if(keywords == null)
            keywords = "";
        QueryWrapper<Flight> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("from_airport", fromAirports).in("to_airport", toAirports).like("flight_no", keywords);
        List<Map<String, Object>> maps = listMaps(queryWrapper);
        if(maps == null)
            maps = new ArrayList<>();
        return maps;
    }

    /**
     * 根据fromCity和toCity的locationId获取航班和疫情信息
     * 格式{
     *     flights:[] //这是一个flight的list
     *     cityinfo:{
     *         城市名字: {疫情数据}
     *     }
     * }
     * @param fromCity
     * @param toCity
     * @return
     */
    @Override
    public Map<String, Object> getWarnningDataByCityName(int fromCity, int toCity, String keywords) {

        HashMap<String, Object> map = new HashMap<>();
        List<Map<String, Object>> flights = getFlightsByCityAndKeywords(fromCity, toCity, keywords);
        //放入航班信息
        map.put("flights", flights);
        HashMap<String, Object> covdata = new HashMap<>();

        //放入出发地和目的地的疫情信息
        Map<String, Object> fromCovData = covDataServiceImpl.getDataByLocationId(fromCity);
        Map<String, Object> toCovData = covDataServiceImpl.getDataByLocationId(toCity);
        covdata.put("from", fromCovData);
        covdata.put("to", toCovData);

        String fromrisk = fromCovData.get(("risk")).toString();
        String torisk = toCovData.get("risk").toString();

        for (Map<String, Object> flight : flights) {
            StringBuilder message = new StringBuilder();

            if(!fromrisk.equals("低"))
                message.append(fromCovData.get("name")).append("为").append(fromrisk).append("风险地区\n");
            if(!torisk.equals("低"))
                message.append(toCovData.get("name")).append("为").append(torisk).append("风险地区\n");
            //放入中转站的疫情信息
            if(flight.get("stop") != null){
                String stop = flight.get("stop").toString();
                int stopLocationId = locationServiceImpl.getLocationIdByName(stop);
                Map<String, Object> covData = covDataServiceImpl.getDataByLocationId(stopLocationId);

                String stoprisk = covData.get("risk").toString();
                if(!stoprisk.equals("低"))
                    message.append(covData.get("name")).append("为").append(stoprisk).append("风险地区\n");
                covdata.put(stop, covData);
            }
            if(message.toString().equals(""))
                message.append("无");
            flight.put("info", message.toString());
        }
        map.put("city_info", covdata);
        return map;
    }

    @Override
    public List<Map<String, Object>> getCityList(){
        //放入有机场的城市信息
        //所有机场
        List<Airport> list = airportServiceImpl.list();
        airportServiceImpl.list();
        List<Integer> idlist = new ArrayList<>();
        for (Airport airport : list) {
            if(!idlist.contains(airport.getLocationId()))
                idlist.add(airport.getLocationId());
        }
        List<Location> locations = locationServiceImpl.getLocationsBylocationIds(idlist);
        List<Map<String, Object>> citylist = new ArrayList<>();
        for (Location location : locations) {
            HashMap<String, Object> city = new HashMap<>();
            city.put("value", location.getId());
            city.put("label", location.getName());
            citylist.add(city);
        }

        return citylist;
    }

    /**
     * 把数据库中的某个字段的时期作为更新日期。
     * @return
     */
    @Override
    public Date getUpdateDate() {
        QueryWrapper<Flight> queryWrapper = new QueryWrapper<>();
        queryWrapper.last("limit 1");
        List<Flight> list = list(queryWrapper);
        if(list.size() > 0)
            return list.get(0).getDate();
        else
            return new Date();
    }

}
