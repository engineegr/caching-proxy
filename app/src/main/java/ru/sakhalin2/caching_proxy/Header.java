package ru.sakhalin2.caching_proxy;

import java.util.Objects;

public record Header(String name, String value) {
    //  

    public static final Header DUMMY_HEADER = new Header("DUMMY_KEY", "DUMMY_VALUE");

    @Override
    public boolean equals(Object otherObject) {
        if (otherObject == null) {
            return false;
        }
        if (this == otherObject) {
            return true;
        }
        if (getClass() != otherObject.getClass()) {
            return false;
        }

        Header otherHeader = (Header) otherObject;
        return name.equals(otherHeader.name) && value.equals(otherHeader.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }

    @Override
    public String toString() {
        return Header.class.getPackageName()
                .concat(Header.class.getName())
                .concat("\n")
                .concat("name = [ ").concat(name).concat(" ] ")
                .concat("value = [ ").concat(value).concat(" ]");
    }
}
