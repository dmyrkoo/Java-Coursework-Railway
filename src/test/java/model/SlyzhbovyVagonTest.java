package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тести для класу SlyzhbovyVagon
 * Перевіряє функціональність службових вагонів
 */
class SlyzhbovyVagonTest {

    @Test
    void testKonstruktor() {
        // Перевірка створення службового вагону з усіма параметрами
        SlyzhbovyVagon vagon = new SlyzhbovyVagon(
                1, 6, 100, 5, "Restoran"
        );

        assertEquals(1, vagon.getId());
        assertEquals(6, vagon.getKomfortnist());
        assertEquals(100, vagon.getBagazhKilkist());
        assertEquals(5, vagon.getPersonalKilkist());
        assertEquals("Restoran", vagon.getTypPryznachennya());
    }

    @Test
    void testGetType() {
        // Перевірка повернення типу вагону
        SlyzhbovyVagon vagon = new SlyzhbovyVagon(
                1, 5, 80, 3, "Poshta"
        );
        assertEquals("Slyzhbovy", vagon.getType());
    }

    @Test
    void testGetPasazhyrskaMistkist() {
        // Перевірка повернення пасажирської місткості (має бути 0 для службових)
        SlyzhbovyVagon vagon = new SlyzhbovyVagon(
                1, 5, 80, 3, "Poshta"
        );
        assertEquals(0, vagon.getPasazhyrskaMistkist());
    }

    @Test
    void testToFileString() {
        // Перевірка форматування для збереження у файл
        SlyzhbovyVagon vagon = new SlyzhbovyVagon(
                7, 8, 120, 6, "Restoran"
        );
        String expected = "SLYZHBOVY;7;8;120;6;Restoran";
        assertEquals(expected, vagon.toFileString());
    }

    @Test
    void testToString() {
        // Перевірка рядкового представлення вагону
        SlyzhbovyVagon vagon = new SlyzhbovyVagon(
                2, 7, 90, 4, "Poshta"
        );
        String result = vagon.toString();
        assertTrue(result.contains("Vagon #2"));
        assertTrue(result.contains("Slyzhbovy"));
        assertTrue(result.contains("Poshta"));
        assertTrue(result.contains("4")); // кількість персоналу
    }

    @Test
    void testSetPersonalKilkist() {
        // Перевірка зміни кількості персоналу
        SlyzhbovyVagon vagon = new SlyzhbovyVagon(
                1, 5, 80, 3, "Poshta"
        );
        vagon.setPersonalKilkist(8);
        assertEquals(8, vagon.getPersonalKilkist());
    }

    @Test
    void testSetTypPryznachennya() {
        // Перевірка зміни типу призначення
        SlyzhbovyVagon vagon = new SlyzhbovyVagon(
                1, 5, 80, 3, "Poshta"
        );
        vagon.setTypPryznachennya("Restoran");
        assertEquals("Restoran", vagon.getTypPryznachennya());
    }

    @Test
    void testSetKomfortnist() {
        // Перевірка зміни комфортності (метод з батьківського класу)
        SlyzhbovyVagon vagon = new SlyzhbovyVagon(
                1, 5, 80, 3, "Poshta"
        );
        vagon.setKomfortnist(9);
        assertEquals(9, vagon.getKomfortnist());
    }

    @Test
    void testSetBagazhKilkist() {
        // Перевірка зміни кількості багажу (метод з батьківського класу)
        SlyzhbovyVagon vagon = new SlyzhbovyVagon(
                1, 5, 80, 3, "Poshta"
        );
        vagon.setBagazhKilkist(150);
        assertEquals(150, vagon.getBagazhKilkist());
    }

    @Test
    void testRizniTypyPryznachennya() {
        // Перевірка роботи з різними типами призначення
        SlyzhbovyVagon restoran = new SlyzhbovyVagon(
                1, 8, 200, 10, "Restoran"
        );
        assertEquals("Restoran", restoran.getTypPryznachennya());

        SlyzhbovyVagon poshta = new SlyzhbovyVagon(
                2, 6, 150, 4, "Poshta"
        );
        assertEquals("Poshta", poshta.getTypPryznachennya());

        SlyzhbovyVagon medychnyi = new SlyzhbovyVagon(
                3, 7, 100, 2, "Medychnyi"
        );
        assertEquals("Medychnyi", medychnyi.getTypPryznachennya());
    }
}
