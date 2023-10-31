package kkrasilnikovv.preprocessor.model;

public enum SectionType {
    TYPE_1("Треугольное"),
    TYPE_2("Type 2"),
    TYPE_3("Type 3");

    private final String value;

    SectionType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}

