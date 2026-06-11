package com.example.pharmacy.Pojo;

public enum ProductType {
    MEDICINE("Лекарственный препарат"),
    PARAPHARMACY("Парафармацевтика"),
    COSMETIC("Косметика и уход"),
    HYGIENE("Гигиена и быт"),
    MEDICAL_DEVICE("Медизделие / прибор"),
    OTHER("Прочее");

    private final String label;

    ProductType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public boolean isMedicine() {
        return this == MEDICINE;
    }
}
