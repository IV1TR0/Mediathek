package de.uni_hamburg.informatik.swt.se2.mediathek.materialien;

import de.uni_hamburg.informatik.swt.se2.mediathek.materialien.medien.Medium;
import de.uni_hamburg.informatik.swt.se2.mediathek.materialien.Kunde;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public class Vormerkkarte {

	// Eigenschaften einer Vermerkkarte
	private final Medium _medium;
	private final ArrayBlockingQueue<Kunde> _vormerker = new ArrayBlockingQueue<Kunde>(3);
	
	/**
     * Initialisert eine neue Vermerkkarte mit den gegebenen Daten.
     * 
     * @param medium Ein Medium.
     * @param vormerker1 Ein Kunde, der das Medium vormerken möchte.
     * @param vormerker2 Ein Kunde, der das Medium vormerken möchte.
     * @param vormerker3 Ein Kunde, der das Medium vormerken möchte.
     * 
     * 
     * @require medium != null
     * @require vormerker1 != null
     * @require vormerker2 != null
     * @require vormerker3 != null
     */
	public Vormerkkarte(Medium medium, Kunde vormerker1, Kunde vormerker2, Kunde vormerker3) {
		_medium = medium;
		try {
			_vormerker.put(vormerker1);
			_vormerker.put(vormerker2);
			_vormerker.put(vormerker3);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
	}
	
	public void fuegeVormerkerHinzu(List<Kunde> kunden) {
		for (Kunde kunde : kunden) {
			try {
				_vormerker.put(kunde);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void fuegeEinenVormerkerHinzu(Kunde kunde) {
		try {
			_vormerker.put(kunde);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void entferneErstenVormerker() {
		_vormerker.poll();
	}
	
	public Kunde getErstenVormerker() {
		return _vormerker.peek();
	}
}
