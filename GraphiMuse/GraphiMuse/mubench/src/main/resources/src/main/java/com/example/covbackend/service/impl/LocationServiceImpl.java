package com.example.covbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.covbackend.common.Result;
import com.example.covbackend.entity.Location;
import com.example.covbackend.entity.Station;
import com.example.covbackend.mapper.LocationMapper;
import com.example.covbackend.service.LocationService;
import com.example.covbackend.service.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LocationServiceImpl extends ServiceImpl<LocationMapper, Location> implements LocationService {

    private List<HashMap<String, Object>> stationCache;

    @Autowired
    StationService stationService;
    /**
     * 面向Controller接口，根据地名返回一个Location的Map
     * location{
     *     id,
     *     name, //已经做过修改
     *     type = enum{“省”, "市"},
     *     belong_id,
     *     risk
     * }
     * @param locationId
     * @return
     */
    @Override
    public Map<String, Object> getLocationDataByLocationId(int locationId) {
        QueryWrapper<Location> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", locationId);
        Map<String, Object> map = getMap(queryWrapper);
        if(map != null)
            format(map);
        else
            map = new HashMap<>();
        return map;
    }

    /**
     *
     * @param name
     * @return
     */
    @Override
    public Map<String, Object> getLocationDataByLocationName(String name) {
        Location location = getLocationByName(name, null);
        if(location == null)
            return new HashMap<>();
        return getLocationDataByLocationId(location.getId());
    }


    /**
     * 面向service层接口，根据location_id返回所有子一级location对象集合
      * @param belongId
     * @return
     */
    @Override
    public List<Location> getLocationsByBelongId(int belongId) {
        QueryWrapper<Location> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("belong_id", belongId);
        List<Location> list = list(queryWrapper);
        if(list == null)
            list = new ArrayList<>();
        for (Location location : list) {
            format(location);
        }
        return list;
    }

    @Override
    public List<Location> getLocationsBylocationIds(List<Integer> list) {
        QueryWrapper<Location> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", list);
        List<Location> locations = list(queryWrapper);
        if(locations == null)
            return new ArrayList<>();
        for (Location location : locations) {
            format(location);
        }
        return locations;
    }

    /**
     * 根据地方名字(不可为null)和类型(可为null)返回一个location。如果找到多个，返回第一个，如果没有找到，返回null
     * @param name
     * @param type
     * @return
     */
    @Override
    public Location getLocationByName(String name, String type) {
        //去掉末尾的市、县、区
        name = name.split("市")[0];
        name = name.split("区")[0];
        name = name.split("县")[0];
        QueryWrapper<Location> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("name", name);
        if(type != null)
            queryWrapper.eq("type", type);
        List<Location> list = list(queryWrapper);
        if(list.size() == 0)
            return null;
        return list.get(0);
    }

    /**
     * 根据地方的locationId返回一个location，如果没有找到则返回null
     * @param locationId
     * @return
     */
    @Override
    public Location getLocationById(int locationId) {
        QueryWrapper<Location> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", locationId).last("LIMIT 1");
        Location location = getOne(queryWrapper, false);
        if(location == null)
            return null;
        return location;
    }

    @Override
    public String getNameByLocationId(int locationId) {
        Location location = getLocationById(locationId);
        return format(location).getName();
    }

    @Override
    public int getLocationIdByName(String name){
        QueryWrapper<Location> queryWrapper = new QueryWrapper<>();
        queryWrapper.likeRight("name", name);
        Location location = new Location();
        try{
            location = getOne(queryWrapper);
        }catch (Exception e){
            System.out.println(e);
        }
        return location.getId();
    }

    //对数据库封装的location map进行格式修改
    private Map<String, Object> format(Map<String, Object> map){


        //修改name
        String name = map.get("name").toString().split("（")[0];
        String type = (String)map.get("type");
        int risk = Integer.parseInt(map.get("risk").toString());
        if(type.equals("1")){
            switch(name){
                case "北京":
                case "天津":
                case "重庆":
                case "上海":
                    name = name + "市";
                    break;
                case "西藏":
                case "内蒙古":
                    name = name + "自治区";
                    break;
                case "新疆":
                    name = "新疆维吾尔自治区";
                    break;
                case "宁夏":
                    name = "宁夏回族自治区";
                    break;
                case "广西":
                    name = "广西壮族自治区";
                    break;
                case "香港":
                    risk = 3;
                    break;
                case "澳门":
                    name = name + "特别行政区";
                    break;
                default:
                    name = name + "省";
            }
        } else if(type.equals("2")){
            switch(name){

            }
        } else if(type.equals("3")){
            name = name + "区";
        }
        map.put("name", name);
        //修改type
        switch (type){
            case "0":
                map.put("type", "国");
                break;
            case "1":
                map.put("type", "省");
                break;
            case "2":
                map.put("type", "市");
                break;
        }
        switch (risk){
            case 0:
                map.put("risk", "低");
                break;
            case 1:
                map.put("risk", "中");
                break;
            case 2:
                map.put("risk", "高");
                break;
            case 3:
                map.put("risk", "-");
        }

        return map;
    }
    //对数据库封装的location对象进行格式修改
    public Location format(Location location){
        //修改name
        String name = location.getName().toString().split("（")[0];
        location.setName(name);
        return location;
    }

    public List<Location> getAllCities(){
        QueryWrapper<Location> queryWrapper = new QueryWrapper<>();
        queryWrapper.notLike("name","%境外%")
                .notLike("name","%监狱%")
                .notLike("name","%外地%")
                .notLike("name","%区%区%区%")
                .and(QueryWrapper->QueryWrapper
                        .eq("type",2)
                        .or().like("name","北京%")
                        .or().like("name","天津%")
                        .or().like("name","重庆%")
                        .or().like("name","上海%"))
                .ne("belong_id",94)
                .ne("belong_id",115)
                .ne("belong_id",165)
                .ne("belong_id",340);
        return list(queryWrapper);
    }

    public void createStationCache(){
        List<Location> cityList = new ArrayList<>();
        try{
            cityList = getAllCities();
        }catch (Exception e){
            System.out.println(e);
        }
        List<HashMap<String, Object>> city_list = new ArrayList<>();
        for(int i=0 ; i<cityList.size(); i++){
            HashMap<String, Object> city = new HashMap<>();
            String name = format(cityList.get(i)).getName();
            city.put("value",name);
            city.put("label",name);
            List<HashMap<String,Object>> children = new ArrayList<>();
            int belong_id = cityList.get(i).getId();
            List<Station> stations = stationService.getStationByLocationId(belong_id);

            for(int j=0; j<stations.size(); j++){
                HashMap<String, Object> child = new HashMap<>();
                child.put("value",stations.get(j).getName());
                child.put("label",stations.get(j).getName());
                children.add(child);
            }
            city.put("children",children);
            city_list.add(city);
        }

        this.stationCache = city_list;
    }

    public void refreshCache(){
        this.createStationCache();
    }

    public List<HashMap<String,Object>> getCitiesAndStationsByCache(){
        if(this.stationCache==null) refreshCache();
        return this.stationCache;
    }

}
