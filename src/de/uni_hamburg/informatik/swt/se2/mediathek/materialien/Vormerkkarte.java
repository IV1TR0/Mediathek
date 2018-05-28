package de.uni_hamburg.informatik.swt.se2.mediathek.materialien;

import de.uni_hamburg.informatik.swt.se2.mediathek.materialien.medien.Medium;
import de.uni_hamburg.informatik.swt.se2.mediathek.materialien.Kunde;

import java.util.concurrent.ArrayBlockingQueue;

public class Vormerkkarte {

	// Eigenschaften einer Vormerkkarte
	private final Medium _medium;
	private final ArrayBlockingQueue<Kunde> _vormerker;

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
	public Vormerkkarte(Kunde vormerker, Medium medium) {
		_medium = medium;
		_vormerker = new ArrayBlockingQueue<Kunde>(3);
		fuegeEinenVormerkerHinzu(vormerker);
	}

	/**
	 * Fuegt einen Vormerker zur Vormerkkarte hinzu.
	 * 
	 * @param Kunde
	 *            kunde
	 * @throws Exception
	 */
	public void fuegeEinenVormerkerHinzu(Kunde kunde) {
		_vormerker.add(kunde);
	}

	/**
	 * Prüft, ob ein Kunde schon als Vormerker eingetragen ist.
	 * 
	 * @param Kunde
	 *            kunde
	 * @return boolean
	 */
	public boolean hatKundeSchonVorgemerkt(Kunde kunde) {
		System.out.println("Kunde: " + kunde.getNachname());
		System.out.println("Anzahl Vormerker: " + _vormerker.size());
		System.out.println("hatKundeSchonVorgemerkt(): " + _vormerker.contains(kunde));
		return _vormerker.contains(kunde);
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
	
	public String getFormatiertenString()
	{
		ArrayBlockingQueue<Kunde> hilfsqueue = _vormerker;
		String hilfsString = "VORMERKUNG:\n  " + _medium.getFormatiertenString() + "  vorgemerkt von\n";
		while (hilfsqueue.peek() != null) {
			hilfsString += hilfsqueue.poll().getFormatiertenString() + "  ,\n";
		}
		return hilfsString;
	}
	
	@Override
    public String toString()
    {
        return getFormatiertenString();
    }
	
	@Override
    public boolean equals(Object obj)
    {
        boolean result = false;
        if (obj instanceof Vormerkkarte)
        {
            Vormerkkarte other = (Vormerkkarte) obj;

            if (other.getErstenVormerkerAndKeep().equals(getErstenVormerkerAndKeep())
            		&& other.anzahlVormerker() == anzahlVormerker())

                result = true;
        }
        return result;
    }
	
    @Override
    public int hashCode()
    {
        final int prime = 32;
        int result = 1;
        result = prime * result
                + ((getErstenVormerkerAndKeep() == null) ? 0 : getErstenVormerkerAndKeep().hashCode());
        result = prime * result + ((_medium == null) ? 0 : _medium.hashCode());
        result = prime * result + ((anzahlVormerker() == 0) ? 0 : anzahlVormerker());
        return result;
    }
}
