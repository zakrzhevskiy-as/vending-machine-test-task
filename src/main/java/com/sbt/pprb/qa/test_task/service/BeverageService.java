package com.sbt.pprb.qa.test_task.service;

import com.sbt.pprb.qa.test_task.model.dto.Beverage;
import com.sbt.pprb.qa.test_task.model.dto.BeverageVolume;
import com.sbt.pprb.qa.test_task.model.response.BeverageResponseResource;
import com.sbt.pprb.qa.test_task.model.response.BeverageVolumeResponseResource;
import com.sbt.pprb.qa.test_task.repository.BeverageVolumesRepository;
import com.sbt.pprb.qa.test_task.repository.BeveragesRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class BeverageService {

    private BeveragesRepository beveragesRepository;
    private BeverageVolumesRepository volumeRepository;

    public List<Beverage> getBeverages() {
        Sort sort = Sort.by(Sort.Direction.ASC, "created");
        return beveragesRepository.findAll(sort);
    }

    public List<BeverageResponseResource> getVolumes() {
        Sort sort = Sort.by(Sort.Direction.ASC, "created");
        List<BeverageVolume> beverageVolumes = volumeRepository.findAll(sort);
        Map<Beverage, List<BeverageVolume>> temp = new HashMap<>();

        for (BeverageVolume volume : beverageVolumes) {
            Beverage beverage = volume.getBeverage();
            if (temp.containsKey(beverage)) {
                List<BeverageVolume> volumes = temp.get(volume.getBeverage());
                volumes.add(volume);
                temp.put(beverage, volumes);
            } else {
                List<BeverageVolume> volumes = new ArrayList<>();
                volumes.add(volume);
                temp.put(beverage, volumes);
            }
        }

        List<BeverageResponseResource> responseResources = new ArrayList<>();
        temp.forEach((key, value) -> {
            BeverageResponseResource responseResource = new BeverageResponseResource();
            responseResource.setId(key.getId());
            responseResource.setBeverageType(key.getBeverageType());
            responseResource.setAvailableVolume(key.getAvailableVolume());

            List<BeverageVolumeResponseResource> beverageVolumesResources = value.stream().map(beverageVolume -> {
                BeverageVolumeResponseResource volumeResponseResource = new BeverageVolumeResponseResource();
                volumeResponseResource.setId(beverageVolume.getId());
                volumeResponseResource.setPrice(beverageVolume.getPrice());
                volumeResponseResource.setVolume(beverageVolume.getVolume());
                return volumeResponseResource;
            }).collect(Collectors.toList());

            responseResource.setBeverageVolumes(beverageVolumesResources);
            responseResources.add(responseResource);
        });

        return responseResources;
    }

    public void addVolume(Long beverageId, Double volume) {
        Beverage beverage = beveragesRepository.getById(beverageId);
        beverage.addAvailableVolume(volume);
        beveragesRepository.save(beverage);
    }
}
