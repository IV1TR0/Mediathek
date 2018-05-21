package de.uni_hamburg.informatik.swt.se2.mediathek.materialien;

import de.uni_hamburg.informatik.swt.se2.mediathek.materialien.medien.Medium;
import de.uni_hamburg.informatik.swt.se2.mediathek.materialien.Kunde;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public class Vormerkkarte {

	// Eigenschaften einer Vormerkkarte
	private final Medium _medium;
	private final ArrayBlockingQueue<Kunde> _vormerker = new ArrayBlockingQueue<Kunde>(3);

	/**
	 * Initialisert eine neue Vormerkkarte mit den gegebenen Daten.
	 * 
	 * @param Medium
	 *            medium Ein Medium.
	 * @param Kunde
	 *            vormerker Ein Kunde, der das Medium vormerken möchte.
	 * 
	 * @require medium != null
	 * @require vormerker != null
	 */
	public Vormerkkarte(Medium medium, Kunde vormerker) {
		_medium = medium;
		_vormerker.add(vormerker);
	}

	/**
	 * Fuegt mehrere Vormerker zur Vormerkkarte hinzu.
	 * 
	 * @param List<Kunde>
	 *            kunden
	 * @throws Exception
	 */
	public void fuegeVormerkerHinzu(List<Kunde> kunden) throws Exception {
		for (Kunde kunde : kunden) {
			if (!hatKundeSchonVorgemerkt(kunde)) {
				_vormerker.add(kunde);
			} else {
				throw new Exception("Kunde hat Medium schon vorgemerkt.");
			}
		}
	}

	/**
	 * Fuegt einen Vormerker zur Vormerkkarte hinzu.
	 * 
	 * @param Kunde
	 *            kunde
	 * @throws Exception
	 */
	public void fuegeEinenVormerkerHinzu(Kunde kunde) throws IllegalStateException {
		_vormerker.add(kunde);
	}

	/**
	 * Prüft, ob ein Kunde schon als Vormerker eingetragen ist.
	 * 
	 * @param Kunde
	 *            kunde
	 * @return boolean
	 */
	private boolean hatKundeSchonVorgemerkt(Kunde kunde) {
		return (_vormerker.contains(kunde) ? true : false);
	}

	/**
	 * Entfernt den ersten Vormerker.
	 */
	public void entferneErstenVormerker() {
		_vormerker.poll();
	}

	/**
	 * Gibt den ersten Vormerker in der Warteschlange zurück.
	 * 
	 * @return Kunde
	 */
	public Kunde getErstenVormerkerAndKeep() {
		return _vormerker.peek();
	}

	public Kunde getErstenVormerkerAndRemove() {
		return _vormerker.poll();
	}

	/**
	 * Gibt das Medium der Vormerkkarte zurück.
	 * 
	 * @return Medium
	 */
	public Medium getMedium() {
		return _medium;
	}

	/**
	 * Storniert eine Vormerkung. Zusatz: Baut die Möglichkeit zum Stornieren einer
	 * Vormerkung in die Mediathek ein.
	 * 
	 * @param Kunde
	 *            kunde
	 * @throws Exception
	 */
	public void storniereVormerkung(Kunde kunde) throws Exception {
		if (_vormerker.contains(kunde)) {
			int amountVormerker = _vormerker.size();
			for (int i = 0; i < amountVormerker; i++) {
				Kunde currentVormerker = _vormerker.poll();
				if (currentVormerker != kunde) {
					_vormerker.add(currentVormerker);
				}
			}
		} else {
			throw new Exception("Kunde hat Medium nicht vorgemerkt.");
		}
	}

	/**
	 * Gibt die Anzahl der Vormerker in der Warteschlange zurück.
	 * 
	 * @return int
	 */
	public int anzahlVormerker() {
		return _vormerker.size();
	}
}
