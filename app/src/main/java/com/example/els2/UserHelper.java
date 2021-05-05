package com.example.els2;

public class UserHelper {
    String firstname, lastname, birthdate, sex, bloodtype, weight, height, conditions, allergies, mednotes, number, state;
    boolean toggled;

    public UserHelper(String firstname, String lastname, String birthdate, String sex, String bloodtype, String weight, String height, String conditions, String allergies, String mednotes, String number, String state, boolean toggled) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.birthdate = birthdate;
        this.sex = sex;
        this.bloodtype = bloodtype;
        this.weight = weight;
        this.height = height;
        this.conditions = conditions;
        this.allergies = allergies;
        this.mednotes = mednotes;
        this.number = number;
        this.state = state;
        this.toggled = toggled;
    }

    public UserHelper() {
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBloodtype() {
        return bloodtype;
    }

    public void setBloodtype(String bloodtype) {
        this.bloodtype = bloodtype;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getConditions() {
        return conditions;
    }

    public void setConditions(String conditions) {
        this.conditions = conditions;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getMednotes() {
        return mednotes;
    }

    public void setMednotes(String mednotes) {
        this.mednotes = mednotes;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean isToggled() {
        return toggled;
    }

    public void setToggled(boolean toggled) {
        this.toggled = toggled;
    }
}
