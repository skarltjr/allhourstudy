package com.allhour.allhourstudy.modules.zone;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StreamUtils;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ZoneService {
    private final ZoneRepository zoneRepository;
    private final ModelMapper modelMapper;

    @PostConstruct
    public void initZoneData() throws IOException {
        if (zoneRepository.count() == 0) {
            // 초기데이터 저장
            /*Resource resource = new ClassPathResource("zones_kr.csv");
            List<Zone> zoneList = Files.readAllLines(resource.getFile().toPath(), StandardCharsets.UTF_8).stream()
                    .map(line -> {
                        String[] split = line.split(",");
                        return Zone.builder().city(split[0]).localNameOfCity(split[1]).province(split[2]).build();
                    }).collect(Collectors.toList());
            zoneRepository.saveAll(zoneList);*/

            /**     inputstream을 통해 jar배포 시 file not found exception방지*/
            InputStream resourceAsStream = getClass().getResourceAsStream("/zones_kr.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(resourceAsStream));
            List<Zone> zoneList = reader.lines().map(line -> {
                String[] split = line.split(",");
                return Zone.builder().city(split[0]).localNameOfCity(split[1]).province(split[2]).build();
            }).collect(Collectors.toList());
            zoneRepository.saveAll(zoneList);
        }
    }

    public Zone findOrCreate(ZoneForm zoneForm) {
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        if (zone == null) {
            Zone newZone = modelMapper.map(zoneForm, Zone.class);
            return zoneRepository.save(newZone);
        }
        return zone;
    }
}
