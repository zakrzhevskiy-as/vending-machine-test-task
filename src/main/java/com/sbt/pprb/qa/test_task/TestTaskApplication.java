package com.sbt.pprb.qa.test_task;

import com.sbt.pprb.qa.test_task.model.Beverage;
import com.sbt.pprb.qa.test_task.model.BeverageType;
import com.sbt.pprb.qa.test_task.model.BeverageVolume;
import com.sbt.pprb.qa.test_task.repository.BeverageRepository;
import com.sbt.pprb.qa.test_task.repository.BeverageVolumeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Arrays;
import java.util.List;

@EnableJpaAuditing
@EnableJpaRepositories
@SpringBootApplication
public class TestTaskApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestTaskApplication.class, args);
    }

    @Bean
    public CommandLineRunner demoData(BeverageRepository beverageRepository, BeverageVolumeRepository volumeRepository) {
        return args -> {
            Beverage slurm = new Beverage();
            slurm.setAvailableVolume(5.0);
            slurm.setBeverageType(BeverageType.SLURM);

            Beverage nukaCola = new Beverage();
            nukaCola.setAvailableVolume(8.4);
            nukaCola.setBeverageType(BeverageType.NUKA_COLA);

            Beverage expresso = new Beverage();
            expresso.setAvailableVolume(3.0);
            expresso.setBeverageType(BeverageType.EXPRESSO);

            beverageRepository.saveAll(Arrays.asList(slurm, nukaCola, expresso));

            BeverageVolume volume1 = new BeverageVolume();
            volume1.setVolume(0.33);
            volume1.setPrice(35);
            volume1.setBeverage(slurm);

            BeverageVolume volume2 = new BeverageVolume();
            volume2.setVolume(0.5);
            volume2.setPrice(60);
            volume2.setBeverage(slurm);

            BeverageVolume volume3 = new BeverageVolume();
            volume3.setVolume(0.33);
            volume3.setPrice(40);
            volume3.setBeverage(nukaCola);

            BeverageVolume volume4 = new BeverageVolume();
            volume4.setVolume(0.5);
            volume4.setPrice(70);
            volume4.setBeverage(nukaCola);

            BeverageVolume volume5 = new BeverageVolume();
            volume5.setVolume(0.25);
            volume5.setPrice(30);
            volume5.setBeverage(expresso);

            BeverageVolume volume6 = new BeverageVolume();
            volume6.setVolume(0.4);
            volume6.setPrice(55);
            volume6.setBeverage(expresso);

            List<BeverageVolume> entities = Arrays.asList(volume1, volume2, volume3, volume4, volume5, volume6);
            volumeRepository.saveAll(entities);

        };
    }
}
