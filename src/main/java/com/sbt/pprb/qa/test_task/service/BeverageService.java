package com.sbt.pprb.qa.test_task.service;

import com.sbt.pprb.qa.test_task.model.Beverage;
import com.sbt.pprb.qa.test_task.model.BeverageVolume;
import com.sbt.pprb.qa.test_task.repository.BeverageRepository;
import com.sbt.pprb.qa.test_task.repository.BeverageVolumeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class BeverageService {

    private BeverageRepository beverageRepository;
    private BeverageVolumeRepository volumeRepository;

    public List<Beverage> getBeverages() {
        return beverageRepository.findAll();
    }

    public Beverage getBeverage(Long id) {
        return beverageRepository.getById(id);
    }

    public Beverage createBeverage(Beverage beverage) {
        return beverageRepository.save(beverage);
    }

    public Beverage updateBeverage(Long id, Beverage beverage) {
        return beverageRepository.update(id, beverage.getAvailableVolume());
    }

    public List<BeverageVolume> getVolumes() {
        return volumeRepository.findAll();
    }

    public BeverageVolume getVolume(Long id) {
        return volumeRepository.getById(id);
    }

    public List<BeverageVolume> getBeverageVolumes(Long beverageId) {
        return volumeRepository.getAllByBeverageId(beverageId);
    }

    public BeverageVolume createVolume(Long beverageId, BeverageVolume beverageVolume) {
        Beverage beverage = beverageRepository.getById(beverageId);
        beverageVolume.setBeverage(beverage);
        return volumeRepository.save(beverageVolume);
    }

    public BeverageVolume updateVolume(Long id, BeverageVolume beverageVolume) {
        return volumeRepository.update(id, beverageVolume.getBeverage(),
                beverageVolume.getPrice(), beverageVolume.getVolume());
    }
}
