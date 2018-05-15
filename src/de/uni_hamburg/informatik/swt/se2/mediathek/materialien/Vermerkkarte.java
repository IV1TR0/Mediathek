package de.uni_hamburg.informatik.swt.se2.mediathek.materialien;

import de.uni_hamburg.informatik.swt.se2.mediathek.materialien.medien.Medium;
import de.uni_hamburg.informatik.swt.se2.mediathek.materialien.Kunde;
import java.util.concurrent.ArrayBlockingQueue;

public class Vermerkkarte {

	// Eigenschaften einer Vermerkkarte
	private final Medium _medium;
	private final ArrayBlockingQueue<Kunde> _vermerker = new ArrayBlockingQueue<Kunde>(3);
	
	public Vermerkkarte(Medium medium, Kunde vermerker1, Kunde vermerker2, Kunde vermerker3) {
		_medium = medium;
		try {
			_vermerker.put(vermerker1);
			_vermerker.put(vermerker2);
			_vermerker.put(vermerker3);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
	}
	
	public void fuegeVermerkerHinzu(Kunde kunde) {
		try {
			_vermerker.put(kunde);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void entferneErstenVermerker() {
		_vermerker.poll();
	}
	
	public Kunde getErstenVermerker() {
		return _vermerker.peek();
	}
}
