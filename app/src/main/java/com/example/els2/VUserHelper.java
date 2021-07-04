package com.example.els2;

public class VUserHelper {
    String ageCategory, isVaccinated, doses, isFrontliner, relativeHadCvd;

    public VUserHelper(String ageCategory, String isVaccinated, String doses, String isFrontliner, String relativeHadCvd) {
        this.ageCategory = ageCategory;
        this.isVaccinated = isVaccinated;
        this.doses = doses;
        this.isFrontliner = isFrontliner;
        this.relativeHadCvd = relativeHadCvd;
    }

    public VUserHelper() {
    }

    public String getAgeCategory() {
        return ageCategory;
    }

    public void setAgeCategory(String ageCategory) {
        this.ageCategory = ageCategory;
    }

    public String getIsVaccinated() {
        return isVaccinated;
    }

    public void setIsVaccinated(String isVaccinated) {
        this.isVaccinated = isVaccinated;
    }

    public String getDoses() {
        return doses;
    }

    public void setDoses(String doses) {
        this.doses = doses;
    }

    public String getIsFrontliner() {
        return isFrontliner;
    }

    public void setIsFrontliner(String isFrontliner) {
        this.isFrontliner = isFrontliner;
    }

    public String getRelativeHadCvd() {
        return relativeHadCvd;
    }

    public void setRelativeHadCvd(String relativeHadCvd) {
        this.relativeHadCvd = relativeHadCvd;
    }
}
