package com.pukhuriandbeels.limknowpilot.model;

import java.io.Serializable;

public class Macrophyte implements Serializable {
    //declare fields
    private String macrophyteName;
    private String macrophyteType;
    private String commonName;
    private String macrophyteDescription;
    private String macrophyteImageURL;
    private String macrophyteImageCredit;
    private boolean invasiveSpecies;

    //initialize fields
    public Macrophyte(String macrophyteName, String macrophyteType, String commonName, String macrophyteDescription, String macrophyteImageURL, String macrophyteImageCredit, boolean invasiveSpecies) {
        this.macrophyteName = macrophyteName;
        this.macrophyteType = macrophyteType;
        this.commonName = commonName;
        this.macrophyteDescription = macrophyteDescription;
        this.macrophyteImageURL = macrophyteImageURL;
        this.macrophyteImageCredit = macrophyteImageCredit;
        this.invasiveSpecies = invasiveSpecies;
    }

    public Macrophyte() {
    }

    //getters and setters
    public String getMacrophyteName() {
        return macrophyteName;
    }

    public void setMacrophyteName(String macrophyteName) {
        this.macrophyteName = macrophyteName;
    }

    public String getMacrophyteType() {
        return macrophyteType;
    }

    public void setMacrophyteType(String macrophyteType) {
        this.macrophyteType = macrophyteType;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getMacrophyteDescription() {
        return macrophyteDescription;
    }

    public void setMacrophyteDescription(String macrophyteDescription) {
        this.macrophyteDescription = macrophyteDescription;
    }

    public String getMacrophyteImageURL() {
        return macrophyteImageURL;
    }

    public void setMacrophyteImageURL(String macrophyteImageURL) {
        this.macrophyteImageURL = macrophyteImageURL;
    }

    public String getMacrophyteImageCredit() {
        return macrophyteImageCredit;
    }

    public void setMacrophyteImageCredit(String macrophyteImageCredit) {
        this.macrophyteImageCredit = macrophyteImageCredit;
    }

    public boolean isInvasiveSpecies() {
        return invasiveSpecies;
    }

    public void setInvasiveSpecies(boolean invasiveSpecies) {
        this.invasiveSpecies = invasiveSpecies;
    }
}
