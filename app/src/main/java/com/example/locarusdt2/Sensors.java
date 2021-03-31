package com.example.locarusdt2;

public class Sensors {
    private String name;
    private String value;
    private String units;
    private String varName;
    private Integer ooType;
    private Long time;

    public Sensors(String name, String value, String units, String varName, Integer ooType, Long time) {
        this.name = name;
        this.value = value;
        this.units = units;
        this.varName = varName;
        this.ooType = ooType;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getVarName() {
        return varName;
    }

    public void setVarName(String varName) {
        this.varName = varName;
    }

    public Integer getOoType() {
        return ooType;
    }

    public void setOoType(Integer ooType) {
        this.ooType = ooType;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
