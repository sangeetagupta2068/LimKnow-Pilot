package com.pukhuriandbeels.limknowpilot.model;

import java.io.Serializable;

public class Animal implements Serializable {
    //declare fields
    private String animalName;
    private String animalCommonName;
    private String animalImageURL;
    private String animalARModelURL;
    private String animalImageCredits;
    private String animalThreat;
    private String animalWaterbodyAssociation;

    private float animalScale;

    private String associatedQuestion;
    private String optionOne;
    private String optionTwo;
    private String optionThree;

    //initialize fields
    public Animal(String animalName, String animalCommonName, String animalImageURL, String animalARModelURL, String animalImageCredits, String animalThreat, String animalWaterbodyAssociation, String associatedQuestion, String optionOne, String optionTwo, String optionThree) {
        this.animalName = animalName;
        this.animalCommonName = animalCommonName;
        this.animalImageURL = animalImageURL;
        this.animalARModelURL = animalARModelURL;
        this.animalImageCredits = animalImageCredits;
        this.animalThreat = animalThreat;
        this.animalWaterbodyAssociation = animalWaterbodyAssociation;

        this.animalScale = 0.5f;

        this.associatedQuestion = associatedQuestion;
        this.optionOne = optionOne;
        this.optionTwo = optionTwo;
        this.optionThree = optionThree;
    }

    public Animal() {
        this.animalScale = 0.05f;
    }

    //getters and setters
    public String getAnimalName() {
        return animalName;
    }

    public void setAnimalName(String animalName) {
        this.animalName = animalName;
    }

    public String getAnimalCommonName() {
        return animalCommonName;
    }

    public void setAnimalCommonName(String animalCommonName) {
        this.animalCommonName = animalCommonName;
    }

    public String getAnimalImageURL() {
        return animalImageURL;
    }

    public void setAnimalImageURL(String animalImageURL) {
        this.animalImageURL = animalImageURL;
    }

    public String getAnimalARModelURL() {
        return animalARModelURL;
    }

    public void setAnimalARModelURL(String animalARModelURL) {
        this.animalARModelURL = animalARModelURL;
    }

    public String getAnimalImageCredits() {
        return animalImageCredits;
    }

    public void setAnimalImageCredits(String animalImageCredits) {
        this.animalImageCredits = animalImageCredits;
    }

    public String getAnimalThreat() {
        return animalThreat;
    }

    public void setAnimalThreat(String animalThreat) {
        this.animalThreat = animalThreat;
    }

    public String getAnimalWaterbodyAssociation() {
        return animalWaterbodyAssociation;
    }

    public void setAnimalWaterbodyAssociation(String animalWaterbodyAssociation) {
        this.animalWaterbodyAssociation = animalWaterbodyAssociation;
    }

    public float getAnimalScale() {
        return animalScale;
    }

    public void setAnimalScale(float animalScale) {
        this.animalScale = animalScale;
    }

    public String getAssociatedQuestion() {
        return associatedQuestion;
    }

    public void setAssociatedQuestion(String associatedQuestion) {
        this.associatedQuestion = associatedQuestion;
    }

    public String getOptionOne() {
        return optionOne;
    }

    public void setOptionOne(String optionOne) {
        this.optionOne = optionOne;
    }

    public String getOptionTwo() {
        return optionTwo;
    }

    public void setOptionTwo(String optionTwo) {
        this.optionTwo = optionTwo;
    }

    public String getOptionThree() {
        return optionThree;
    }

    public void setOptionThree(String optionThree) {
        this.optionThree = optionThree;
    }
}
