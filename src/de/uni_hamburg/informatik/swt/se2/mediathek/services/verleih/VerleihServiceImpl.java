package de.uni_hamburg.informatik.swt.se2.mediathek.services.verleih;

import java.util.ArrayList;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.uni_hamburg.informatik.swt.se2.mediathek.fachwerte.Datum;
import de.uni_hamburg.informatik.swt.se2.mediathek.materialien.Kunde;
import de.uni_hamburg.informatik.swt.se2.mediathek.materialien.Verleihkarte;
import de.uni_hamburg.informatik.swt.se2.mediathek.materialien.Vormerkkarte;
import de.uni_hamburg.informatik.swt.se2.mediathek.materialien.medien.Medium;
import de.uni_hamburg.informatik.swt.se2.mediathek.services.AbstractObservableService;
import de.uni_hamburg.informatik.swt.se2.mediathek.services.kundenstamm.KundenstammService;
import de.uni_hamburg.informatik.swt.se2.mediathek.services.medienbestand.MedienbestandService;

/**
 * Diese Klasse implementiert das Interface VerleihService. Siehe dortiger
 * Kommentar.
 * 
 * @author SE2-Team
 * @version SoSe 2017
 */
public class VerleihServiceImpl extends AbstractObservableService implements VerleihService {
	/**
	 * Diese Map speichert für jedes eingefügte Medium die dazugehörige
	 * Verleihkarte. Ein Zugriff auf die Verleihkarte ist dadurch leicht über die
	 * Angabe des Mediums möglich. Beispiel: _verleihkarten.get(medium)
	 */
	private Map<Medium, Verleihkarte> _verleihkarten;

	/**
	 * Diese Map speichert für jedes eingefuegte Medium die dazugehörige
	 * Vormerkkarte. Ein Zugriff auf die Vormerkkarte ist dadurch leicht über die
	 * Angabe des Mediums möglich. Beispiel: _vormerkkarten.get(medium)
	 */
	private Map<Medium, Vormerkkarte> _vormerkkarten;

	/**
	 * Der Medienbestand.
	 */
	private MedienbestandService _medienbestand;

	/**
	 * Der Kundenstamm.
	 */
	private KundenstammService _kundenstamm;

	/**
	 * Der Protokollierer für die Verleihvorgänge.
	 */
	private VerleihProtokollierer _protokollierer;

	/**
	 * Konstruktor. Erzeugt einen neuen VerleihServiceImpl.
	 * 
	 * @param kundenstamm
	 *            Der KundenstammService.
	 * @param medienbestand
	 *            Der MedienbestandService.
	 * @param initialBestand
	 *            Der initiale Bestand.
	 * 
	 * @require kundenstamm != null
	 * @require medienbestand != null
	 * @require initialBestand != null
	 */
	public VerleihServiceImpl(KundenstammService kundenstamm, MedienbestandService medienbestand,
			List<Verleihkarte> initialVerleihBestand, List<Vormerkkarte> initialVormerkBestand) {
		assert kundenstamm != null : "Vorbedingung verletzt: kundenstamm  != null";
		assert medienbestand != null : "Vorbedingung verletzt: medienbestand  != null";
		assert initialVerleihBestand != null : "Vorbedingung verletzt: initialVerleihBestand  != null";
		assert initialVormerkBestand != null : "Vorbedingung verletzt: initialVormerkBestand  != null";
		
		_verleihkarten = erzeugeVerleihkartenBestand(initialVerleihBestand);
		_vormerkkarten = erzeugeVormerkkartenBestand(initialVormerkBestand);
		_kundenstamm = kundenstamm;
		_medienbestand = medienbestand;
		_protokollierer = new VerleihProtokollierer();
	}

	/**
	 * Erzeugt eine neue HashMap aus dem Initialbestand.
	 */
	private HashMap<Medium, Verleihkarte> erzeugeVerleihkartenBestand(List<Verleihkarte> initialBestand) {
		HashMap<Medium, Verleihkarte> result = new HashMap<Medium, Verleihkarte>();
		for (Verleihkarte verleihkarte : initialBestand) {
			result.put(verleihkarte.getMedium(), verleihkarte);
		}
		return result;
	}
	
	private HashMap<Medium, Vormerkkarte> erzeugeVormerkkartenBestand(List<Vormerkkarte> initialBestand) {
		HashMap<Medium, Vormerkkarte> result = new HashMap<Medium, Vormerkkarte>();
		for (Vormerkkarte vormerkkarte : initialBestand) {
			result.put(vormerkkarte.getMedium(), vormerkkarte);
		}
		return result;
	}

	@Override
	public List<Verleihkarte> getVerleihkarten() {
		return new ArrayList<Verleihkarte>(_verleihkarten.values());
	}

	@Override
	public boolean istVerliehen(Medium medium) {
		assert mediumImBestand(medium) : "Vorbedingung verletzt: mediumExistiert(medium)";
		return _verleihkarten.get(medium) != null;
	}

	@Override
	public boolean istVerleihenMoeglich(Kunde kunde, List<Medium> medien) {
		assert kundeImBestand(kunde) : "Vorbedingung verletzt: kundeImBestand(kunde)";
		assert medienImBestand(medien) : "Vorbedingung verletzt: medienImBestand(medien)";

		return sindAlleNichtVerliehen(medien);
	}

	@Override
	public void nimmZurueck(List<Medium> medien, Datum rueckgabeDatum) throws ProtokollierException {
		assert sindAlleVerliehen(medien) : "Vorbedingung verletzt: sindAlleVerliehen(medien)";
		assert rueckgabeDatum != null : "Vorbedingung verletzt: rueckgabeDatum != null";

		for (Medium medium : medien) {
			Verleihkarte verleihkarte = _verleihkarten.get(medium);
			_verleihkarten.remove(medium);
			_protokollierer.protokolliere(VerleihProtokollierer.EREIGNIS_RUECKGABE, verleihkarte);
		}

		informiereUeberAenderung();
	}

	@Override
	public boolean sindAlleNichtVerliehen(List<Medium> medien) {
		assert medienImBestand(medien) : "Vorbedingung verletzt: medienImBestand(medien)";
		boolean result = true;
		for (Medium medium : medien) {
			if (istVerliehen(medium)) {
				result = false;
			}
		}
		return result;
	}

	@Override
	public boolean sindAlleVerliehenAn(Kunde kunde, List<Medium> medien) {
		assert kundeImBestand(kunde) : "Vorbedingung verletzt: kundeImBestand(kunde)";
		assert medienImBestand(medien) : "Vorbedingung verletzt: medienImBestand(medien)";

		boolean result = true;
		for (Medium medium : medien) {
			if (!istVerliehenAn(kunde, medium)) {
				result = false;
			}
		}
		return result;
	}

	@Override
	public boolean istVerliehenAn(Kunde kunde, Medium medium) {
		assert kundeImBestand(kunde) : "Vorbedingung verletzt: kundeImBestand(kunde)";
		assert mediumImBestand(medium) : "Vorbedingung verletzt: mediumImBestand(medium)";

		return istVerliehen(medium) && getEntleiherFuer(medium).equals(kunde);
	}

	@Override
	public boolean sindAlleVerliehen(List<Medium> medien) {
		assert medienImBestand(medien) : "Vorbedingung verletzt: medienImBestand(medien)";

		boolean result = true;
		for (Medium medium : medien) {
			if (!istVerliehen(medium)) {
				result = false;
			}
		}
		return result;
	}

	@Override
	public void verleiheAn(Kunde kunde, List<Medium> medien, Datum ausleihDatum) throws ProtokollierException {
		assert kundeImBestand(kunde) : "Vorbedingung verletzt: kundeImBestand(kunde)";
		assert sindAlleNichtVerliehen(medien) : "Vorbedingung verletzt: sindAlleNichtVerliehen(medien) ";
		assert ausleihDatum != null : "Vorbedingung verletzt: ausleihDatum != null";
		assert istVerleihenMoeglich(kunde, medien) : "Vorbedingung verletzt:  istVerleihenMoeglich(kunde, medien)";

		for (Medium medium : medien) {
			Verleihkarte verleihkarte = new Verleihkarte(kunde, medium, ausleihDatum);

			_verleihkarten.put(medium, verleihkarte);
			_protokollierer.protokolliere(VerleihProtokollierer.EREIGNIS_AUSLEIHE, verleihkarte);
		}
		// Was passiert wenn das Protokollieren mitten in der Schleife
		// schief geht? informiereUeberAenderung in einen finally Block?
		informiereUeberAenderung();
	}

	@Override
	public boolean kundeImBestand(Kunde kunde) {
		return _kundenstamm.enthaeltKunden(kunde);
	}

	@Override
	public boolean mediumImBestand(Medium medium) {
		return _medienbestand.enthaeltMedium(medium);
	}

	@Override
	public boolean medienImBestand(List<Medium> medien) {
		assert medien != null : "Vorbedingung verletzt: medien != null";
		assert !medien.isEmpty() : "Vorbedingung verletzt: !medien.isEmpty()";

		boolean result = true;
		for (Medium medium : medien) {
			if (!mediumImBestand(medium)) {
				result = false;
				break;
			}
		}
		return result;
	}

	@Override
	public List<Medium> getAusgelieheneMedienFuer(Kunde kunde) {
		assert kundeImBestand(kunde) : "Vorbedingung verletzt: kundeImBestand(kunde)";
		List<Medium> result = new ArrayList<Medium>();
		for (Verleihkarte verleihkarte : _verleihkarten.values()) {
			if (verleihkarte.getEntleiher().equals(kunde)) {
				result.add(verleihkarte.getMedium());
			}
		}
		return result;
	}

	@Override
	public Kunde getEntleiherFuer(Medium medium) {
		assert istVerliehen(medium) : "Vorbedingung verletzt: istVerliehen(medium)";
		Verleihkarte verleihkarte = _verleihkarten.get(medium);
		return verleihkarte.getEntleiher();
	}

	@Override
	public Verleihkarte getVerleihkarteFuer(Medium medium) {
		assert istVerliehen(medium) : "Vorbedingung verletzt: istVerliehen(medium)";
		return _verleihkarten.get(medium);
	}

	@Override
	public List<Verleihkarte> getVerleihkartenFuer(Kunde kunde) {
		assert kundeImBestand(kunde) : "Vorbedingung verletzt: kundeImBestand(kunde)";
		List<Verleihkarte> result = new ArrayList<Verleihkarte>();
		for (Verleihkarte verleihkarte : _verleihkarten.values()) {
			if (verleihkarte.getEntleiher().equals(kunde)) {
				result.add(verleihkarte);
			}
		}
		return result;
	}
	
	// ###
	// Hier beginnen alle Vormerk-Sachen
	// ###
	
	/**
	 * Trägt den Kunden als Vormerker bei allen ausgewaehlten Medien ein.
	 * 
	 * @param Kunde kunde
	 * @param List<Medium> medien
	 * 
	 * @require kundeImBestand == true
     * @require istVormerkenMoeglich == true
	 */
	public void merkeVor(Kunde kunde, List<Medium> medien){
		assert kundeImBestand(kunde) : "Vorbedingung verletzt: kundeImBestand(kunde)";
		assert istVormerkenMoeglich(kunde, medien) : "Vorbedingung verletzt: istVerleihenMoeglich(kunde, medien)";

		for (Medium medium : medien) {
			if(_vormerkkarten.containsKey(medium)) {
				Vormerkkarte neueVormerkkarte = _vormerkkarten.get(medium);
				neueVormerkkarte.fuegeEinenVormerkerHinzu(kunde);
				System.out.println("Anzahl Vormerker nachm einfuegen: " + neueVormerkkarte.anzahlVormerker());
				_vormerkkarten.put(medium, neueVormerkkarte);
			}else {
				Vormerkkarte vormerkkarte = new Vormerkkarte(kunde, medium);
				_vormerkkarten.put(medium, vormerkkarte);
			}
			//_protokollierer.protokolliere(VerleihProtokollierer.EREIGNIS_AUSLEIHE, verleihkarte);
		}
		// Was passiert wenn das Protokollieren mitten in der Schleife
		// schief geht? informiereUeberAenderung in einen finally Block?
		informiereUeberAenderung();
	}
	
	public boolean istVorgemerkt(Medium medium) {
		assert mediumImBestand(medium) : "Vorbedingung verletzt: mediumExistiert(medium)";
		return _vormerkkarten.get(medium) != null;
	}
	
	public Vormerkkarte getVormerkkarteFuer(Medium medium) {
		if(_vormerkkarten.containsKey(medium)) {
			return _vormerkkarten.get(medium);
		}else {
			return null;
		}
	}
	
	public boolean istVormerkenMoeglich(Kunde kunde, List<Medium> medien) {
		assert kundeImBestand(kunde) : "Vorbedingung verletzt: kundeImBestand(kunde)";
		assert medienImBestand(medien) : "Vorbedingung verletzt: medienImBestand(medien)";

		boolean kundeHatAusgeliehen = kundeHatMedienAusgeliehen(medien, kunde);
		boolean kundeHatVorgemerkt = kundeHatSchonVorgemerktBei(medien, kunde);
		boolean vormerkPlaetzeFrei = sindVormerkPlaetzeFrei(medien);
		
		boolean vormerkenMoeglich = !kundeHatAusgeliehen && !kundeHatVorgemerkt && vormerkPlaetzeFrei;
		
		System.out.println("kundeHatAusgeliehen: " + kundeHatAusgeliehen);
		System.out.println("kundeHatVorgemerkt: " + kundeHatVorgemerkt);
		System.out.println("vormerkPlaetzeFrei: " + vormerkPlaetzeFrei);
		System.out.println("vormerkenMoeglich: " + vormerkenMoeglich);
		System.out.println("-------");
		
		return vormerkenMoeglich;
	}
	
	public boolean kundeIstBeiAllenErsterVormerker(List<Medium> medien, Kunde kunde) {
		for(Medium medium : medien) {
			if(_vormerkkarten.containsKey(medium)) {
				Vormerkkarte vormerkkarte = _vormerkkarten.get(medium);
				Kunde ersterVormerker = vormerkkarte.getErstenVormerkerAndKeep();
				if(ersterVormerker != null) {
					System.out.println("ersterVormerker: " + ersterVormerker.toString());
					System.out.println("kunde: " + kunde.toString());
					if(!ersterVormerker.equals(kunde)) {
						return false;
					}
				}else {
					System.out.println("Noch keine Vormerker");
				}
			}
		}
		return true;
	}
	
	// Private Vormerk-Methoden
	
	private boolean sindVormerkPlaetzeFrei(List<Medium> medien) {
		for(Medium medium : medien) {
			if(_vormerkkarten.containsKey(medium)) {
				Vormerkkarte vormerkkarte = _vormerkkarten.get(medium);
				if(vormerkkarte.anzahlVormerker() >= 3) {
					return false;
				}
			}
		}
		return true;
	}
	
	private boolean kundeHatSchonVorgemerktBei(List<Medium> medien, Kunde kunde) {
		for(Medium medium : medien) {
			if(_vormerkkarten.containsKey(medium)) {
				boolean state = _vormerkkarten.get(medium).hatKundeSchonVorgemerkt(kunde);
				if(state) {
					System.out.println("kundeHatSchonVorgemerktBei(): true");
					return true;
				}
			}
		}
		System.out.println("kundeHatSchonVorgemerktBei(): false");
		return false;
	}
	
	private boolean kundeHatMedienAusgeliehen(List<Medium> medien, Kunde kunde) {
		for(Medium medium : medien) {
			if(istVerliehenAn(kunde, medium)) {
				return true;
			}
		}
		return false;
	}
}
