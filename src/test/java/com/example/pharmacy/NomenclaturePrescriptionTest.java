package com.example.pharmacy;

import com.example.pharmacy.Pojo.Nomenclature;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NomenclaturePrescriptionTest {

    @Test
    void narcoticRequiresPrescription() {
        Nomenclature n = new Nomenclature();
        n.setNarcotic(true);
        assertTrue(n.requiresPrescription());
    }

    @Test
    void psychotropicRequiresPrescription() {
        Nomenclature n = new Nomenclature();
        n.setPsychotropic(true);
        assertTrue(n.requiresPrescription());
    }

    @Test
    void regularDrugWithoutFlags() {
        Nomenclature n = new Nomenclature();
        assertFalse(n.requiresPrescription());
    }
}
