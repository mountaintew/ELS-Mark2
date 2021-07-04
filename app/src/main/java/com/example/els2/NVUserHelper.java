package com.example.els2;

public class NVUserHelper {
    String ageCategory, isVaccinated, prevSymptoms, isFrontliner, relativeHadCvd, wilForVacc;

    public NVUserHelper(String ageCategory, String isVaccinated, String prevSymptoms, String isFrontliner, String relativeHadCvd, String wilForVacc) {
        this.ageCategory = ageCategory;
        this.isVaccinated = isVaccinated;
        this.prevSymptoms = prevSymptoms;
        this.isFrontliner = isFrontliner;
        this.relativeHadCvd = relativeHadCvd;
        this.wilForVacc = wilForVacc;
    }

    public NVUserHelper() {
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

    public String getPrevSymptoms() {
        return prevSymptoms;
    }

    public void setPrevSymptoms(String prevSymptoms) {
        this.prevSymptoms = prevSymptoms;
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

    public String getWilForVacc() {
        return wilForVacc;
    }

    public void setWilForVacc(String wilForVacc) {
        this.wilForVacc = wilForVacc;
    }
}
