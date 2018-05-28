package de.uni_hamburg.informatik.swt.se2.mediathek.materialien;

import static org.junit.Assert.assertEquals;

import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
//import static org.junit.jupiter.api.Assertions.*;

import org.junit.Test;

//import de.uni_hamburg.informatik.swt.se2.mediathek.fachwerte.Datum;
//import de.uni_hamburg.informatik.swt.se2.mediathek.fachwerte.Geldbetrag;
import de.uni_hamburg.informatik.swt.se2.mediathek.fachwerte.Kundennummer;
import de.uni_hamburg.informatik.swt.se2.mediathek.materialien.medien.CD;
import de.uni_hamburg.informatik.swt.se2.mediathek.materialien.medien.Medium;

public class VormerkkarteTest
{

    private Vormerkkarte _karte;
    private Kunde _kunde;
    private Medium _medium;

    public VormerkkarteTest()
    {
        _kunde = new Kunde(new Kundennummer(123456), "ich", "du");

        _medium = new CD("bar", "baz", "foo", 123);
        _karte = new Vormerkkarte(_kunde, _medium);
    }

    @Test
    public void testAnzahlVormerker()
    {
        int vormerker = _karte.anzahlVormerker();
        System.out.println("1. Anzahl Vormerker: " + vormerker);
        assertEquals(vormerker, _karte.anzahlVormerker());
        vormerker = _karte.anzahlVormerker();
        System.out.println("2. Anzahl Vormerker: " + vormerker);
        _karte.fuegeEinenVormerkerHinzu(
                new Kunde(new Kundennummer(654321), "ich", "du"));
        assertEquals(vormerker + 1, _karte.anzahlVormerker());
    }

    @Test
    public void testeKonstruktor() throws Exception
    {
        assertEquals(_kunde, _karte.getErstenVormerkerAndKeep());
        assertEquals(_medium, _karte.getMedium());
    }

    @Test
    public void testEquals()
    {
        Vormerkkarte karte1 = new Vormerkkarte(_kunde, _medium);

        assertTrue(_karte.equals(karte1));
        assertEquals(_karte.hashCode(), karte1.hashCode());

        Kunde kunde2 = new Kunde(new Kundennummer(654321), "ich", "du");
        CD medium2 = new CD("hallo", "welt", "foo", 321);
        Vormerkkarte karte2 = new Vormerkkarte(kunde2, medium2);

        assertFalse(_karte.equals(karte2));
        assertNotSame(_karte.hashCode(), karte2.hashCode());
    }

    @Test
    public void testfuegeEinenVormerkerHinzu()
    {
        Vormerkkarte karte2 = _karte;
        Kunde kunde2 = new Kunde(new Kundennummer(654321), "ich", "du");
        karte2.fuegeEinenVormerkerHinzu(kunde2);
        assertTrue(karte2.hatKundeSchonVorgemerkt(kunde2));
    }

    @Test
    public void testentferneErstenVormerker()
    {
    	Vormerkkarte karte2 = _karte;
    	karte2.entferneErstenVormerker();
        Kunde kunde2 = new Kunde(new Kundennummer(654321), "ich", "du");
        karte2.fuegeEinenVormerkerHinzu(kunde2);
        karte2.entferneErstenVormerker();
        assertFalse(karte2.hatKundeSchonVorgemerkt(kunde2));
        kunde2 = new Kunde(new Kundennummer(123457), "i", "bims");
        karte2.fuegeEinenVormerkerHinzu(kunde2);
        assertEquals(kunde2, karte2.getErstenVormerkerAndRemove());
        assertFalse(karte2.hatKundeSchonVorgemerkt(kunde2));
    }
    
    @Test
    public void testgetFormatiertenString()
    {
        System.out.println(_karte.getFormatiertenString());
    }

}
