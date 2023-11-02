package kkrasilnikovv.preprocessor.model;

public enum SectionType {
    TRIANGLE("Треугольное"),
    CIRCLE("Круглое"),
    RECTANGLE("Прямоугольное");

    private final String value;

    SectionType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}

