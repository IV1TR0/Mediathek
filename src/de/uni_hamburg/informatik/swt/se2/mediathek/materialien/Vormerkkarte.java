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
     * @param vormerker Ein Kunde, der das Medium vormerken möchte.
     * 
     * @require medium != null
     * @require vormerker != null
     */
	public Vormerkkarte(Medium medium, Kunde vormerker) {
		_medium = medium;
		try {
			_vormerker.put(vormerker);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
	}
	
	public void fuegeVormerkerHinzu(List<Kunde> kunden) throws Exception{
		for (Kunde kunde : kunden) {
			if(!hatKundeSchonVorgemerkt(kunde)) {
				try {
					_vormerker.put(kunde);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				throw new Exception("Kunde hat Medium schon vorgemerkt.");
			}
		}
	}
	
	public void fuegeEinenVormerkerHinzu(Kunde kunde) throws Exception {
		if(!hatKundeSchonVorgemerkt(kunde)) {
			try {
				_vormerker.put(kunde);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}else {
			throw new Exception("Kunde hat Medium schon vorgemerkt.");
		}
	}
	
	private boolean hatKundeSchonVorgemerkt(Kunde kunde) {
		return (_vormerker.contains(kunde) ? true : false);
	}
	
	public void entferneErstenVormerker() {
		_vormerker.poll();
	}
	
	public Kunde getErstenVormerker() {
		return _vormerker.peek();
	}
	
	public Medium getMedium() {
		return _medium;
	}
	
	// Zusatz: Baut die Möglichkeit zum Stornieren einer Vormerkung in die Mediathek ein.
	public void storniereVormerkung(Kunde kunde) throws Exception {
		if(_vormerker.contains(kunde)) {
			int amountVormerker = _vormerker.size();
			for(int i = 0; i < amountVormerker; i++) {
				Kunde currentVormerker = _vormerker.poll();
				if(currentVormerker != kunde) {
					_vormerker.add(currentVormerker);
				}
			}
		}else {
			throw new Exception("Kunde hat Medium nicht vorgemerkt.");
		}
	}
	
	public int anzahlVormerker() {
		return _vormerker.size();
	}
}
