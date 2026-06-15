package com.example.pharmacy.Pojo;

public enum WriteOffReason {
    EXPIRED("Истёк срок годности"),
    DAMAGE("Повреждение"),
    BREAKAGE("Бой"),
    RETURN_SUPPLIER("Возврат поставщику"),
    INVENTORY("Инвентаризация"),
    OTHER("Прочее");

    private final String label;

    WriteOffReason(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
