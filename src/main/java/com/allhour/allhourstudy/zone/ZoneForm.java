package com.allhour.allhourstudy.zone;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ZoneForm {


    private String zoneName;
    //Gimhae(김해시)/South Gyeongsang
    public String getCityName() {
        return zoneName.substring(0, zoneName.indexOf("("));
    } //Gimhae

    public String getProvinceName() {
        return zoneName.substring(zoneName.indexOf("/") + 1);
    } // South Gyeongsang

    public String getLocalNameOfCity() {
        return zoneName.substring(zoneName.indexOf("(") + 1, zoneName.indexOf(")"));
    } //(김해시)

    public Zone getZone() {
        return Zone.builder().city(this.getCityName())
                .localNameOfCity(this.getLocalNameOfCity())
                .province(this.getProvinceName()).build();
    }
}
