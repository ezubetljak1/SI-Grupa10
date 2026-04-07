# Lista nefunkcionalnih zahtjeva (NFR)

## Naziv projekta
**Sistem za AI asistirano prepoznavanje i obradu računa i ulaznih dokumenata**


Ovaj dokument definiše nefunkcionalne zahtjeve sistema. Zahtjevi su grupisani po kategorijama i sadrže mjerljive kriterije provjere.



## Tabela nefunkcionalnih zahtjeva

| ID | Kategorija | Opis zahtjeva | Kako se provjerava | Prioritet | Napomena |
|----|------------|---------------|--------------------|-----------|----------|
| NFR-01 | Sigurnost | Svi zahtjevi između klijenta i servera moraju biti šifrirani putem HTTPS/TLS 1.2+. Korisničke lozinke moraju biti hashirane. Sesije moraju isteći nakon 30 minuta neaktivnosti. | Penetracijski test i pregled konfiguracije servera; verifikacija TLS certifikata; provjera hash algoritma u kodu. | Visok | Obavezno zbog GDPR-a i osjetljivosti poslovnih podataka. |
| NFR-02 | Sigurnost | Pristup dokumentima mora biti ograničen isključivo na autorizovane korisnike. Sistem mora spriječiti neovlašteni pristup dokumentima ili podacima drugih korisnika/organizacija. | Testiranje pokušaja pristupa dokumentima bez odgovarajuće autorizacije; pregled implementacije kontrole pristupa. | Visok | Kritično za scenarije u budućnosti sa više firmi/korisnika. |
| NFR-03 | Privatnost podataka | Sistem mora biti usklađen sa GDPR regulativom: korisnici moraju biti obaviješteni o podacima koji se prikupljaju, podaci se ne smiju čuvati duže nego je neophodno, te mora postojati mehanizam za brisanje podataka na zahtjev. | Pregled politike privatnosti; provjera mehanizma brisanja podataka; audit loga pristupa. | Visok | Usklađenost sa lokalnim zakonima o zaštiti podataka je obavezna. |
| NFR-04 | Privatnost podataka | Originalni dokumenti (PDF, slike) i ekstraktovani podaci (računi, iznosi, dobavljači) moraju biti pohranjeni na siguran način. Pristup bazi podataka mora biti ograničen i logiran. | Pregled konfiguracije baze podataka; provjera da podaci nisu dostupni bez autentifikacije; pregled audit logova. | Visok | Dokumenti sadrže osjetljive poslovne i finansijske podatke. |
| NFR-05 | Performanse | Sistem mora završiti AI/OCR obradu i ekstrakciju podataka iz standardnog PDF dokumenta (do 5 stranica, do 5 MB) u roku od 60 sekundi u 90% slučajeva pri normalnom opterećenju. | Mjerenje vremena obrade za skup od minimalno 20 testnih dokumenata različitih formata i veličina. | Visok | 60 sekundi je razuman kompromis između tačnosti i brzine za OCR procesiranje. |
| NFR-06 | Performanse | Korisničko sučelje mora reagovati na akcije korisnika (klik, unos, navigacija) u roku od 2 sekunde, nezavisno o statusu pozadinske obrade dokumenta. | Manualno testiranje i mjerenje vremena odgovora UI-a; verifikacija da se pozadinska obrada ne blokira u UI niti. | Srednji | |
