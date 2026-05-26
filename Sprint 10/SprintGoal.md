# Sprint Goal - Sprint 9

## Sprint cilj

Omogućiti kompletan tok obavještavanja i završne obrade dokumenata kroz uvođenje notification centra, automatizovanih
obavještenja i generisanja XML izlaza, kako bi korisnici mogli pravovremeno reagovati na promjene statusa dokumenta i
završiti obradu dokumenta spremnog za dalju poslovnu upotrebu.

## Ključne stavke koje tim želi završiti

- Implementacija obavještenja za:
    - dokumente koji čekaju odobravanje
    - odbijene ili vraćene dokumente
- Implementacija notification centra sa:
    - pregledom obavještenja
    - unread badge-om
    - označavanjem pročitanih obavještenja
- Implementacija email reminder sistema za nepročitana obavještenja
- Omogućavanje ručnog dodavanja OCR polja uz audit log evidenciju
- Implementacija XML mapping-a i generisanja XML izlaza
- Pregled i download generisanog XML-a
- Pohrana XML fajla uz dokument
- Uvođenje finalnog statusa dokumenta nakon uspješne obrade

## Rizici i zavisnosti

- Završetak XML funkcionalnosti zavisi od definisanih i stabilnih mapping pravila.
- Email reminder funkcionalnost zavisi od ispravne SMTP konfiguracije i scheduled job mehanizma.
- Ručno dodavanje polja može zahtijevati dodatnu validaciju canonical ključeva i usklađivanje sa postojećim OCR modelom.