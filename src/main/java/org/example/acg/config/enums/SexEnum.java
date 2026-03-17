package org.example.acg.config.enums;

import lombok.Getter;

@Getter
public enum SexEnum {
    MALE("1", "男"),
    FEMALE("2", "女"),
    MALE_FEMALE("3", "男->女"),
    FEMALE_MALE("4", "女->男");

    private final String code;
    private final String value;

    SexEnum(String code, String value) {
        this.code = code;
        this.value = value;
    }


}
