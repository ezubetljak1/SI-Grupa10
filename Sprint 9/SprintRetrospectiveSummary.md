# Sprint Retrospective Summary – Sprint 8

## Šta je išlo dobro

- Timski rad: Svi članovi tima su pokazali dobru saradnju i komunikaciju te je svako uradio svoj dio posla. Ovo je
  direktno uticalo na realizaciju planiranih zadataka.
- Realizacija planiranih zadataka: Svi planirani zadaci su realizovani uspješno i na vrijeme.
- Isporuka dokumentacije: Tim je uspješno isporučio sve potrebne dokumente puput DecisionLog-a, AI Usage Log-a, Sprint
  Backlog-a i ostale potrebne dokumentacije.
- Povratne informacije Product Ownera: Product Owner je izrazio zadovoljstvo nakon demonstracije funkcionalnosti i
  artefakata koji su realizovani u sprintu 8.

## Šta nije išlo dobro

- Iako je tim i ranije radio peer review za svaki Pull Request, primijećeno je da su određeni sitni bugovi i nedovršeni detalji ipak povremeno prolazili do finalne verzije.
- Takvi problemi su se najčešće otkrivali neposredno prije deploymenta na server, što je stvaralo dodatni pritisak na osobu koja radi finalnu provjeru i deployment.
- Dio regresijskog testiranja je ranije zavisio od toga da svaki član tima lokalno pokrene potrebne build/test komande prije push-a ili otvaranja Pull Requesta, što nije uvijek bilo jednako konzistentno.

## Šta treba promijeniti

- Potrebno je zadržati postojeći dobar nivo timske komunikacije i organizacije, ali dodatno formalizovati QA proces kako bi se greške ranije otkrivale.
- Potrebno je da svaki Pull Request prođe kroz jasniju tehničku i funkcionalnu provjeru prije merge-a.
- Potrebno je automatizovati regresijsko testiranje što je više moguće, kako se kvalitet ne bi oslanjao isključivo na ručno pokretanje testova od strane pojedinačnih članova tima.
- Potrebno je jasno definisati odgovornost za QA review, kako bi se funkcionalnosti koje nisu u potpunosti završene ili imaju bugove vraćale autoru na doradu prije ulaska u develop granu.

## Koje konkretne akcije tim uvodi u narednom sprintu

- Tim uvodi CI pipeline koji se automatski pokreće na Pull Requestovima prema develop grani. Pipeline pokreće frontend i backend build, a u sklopu backend build-a izvršavaju se i postojeći integracijski testovi. Na ovaj način regresijsko testiranje postaje automatizovano i manje zavisno od ručnog lokalnog pokretanja testova.
- Uveden je CODEOWNERS fajl kako bi se jasno definisala osoba odgovorna za QA review.
- Develop grana je zaštićena tako da svaki Pull Request mora proći review od strane code owner-a prije merge-a.
- QA osoba vrši detaljniju provjeru funkcionalnosti, koda i potencijalnih regresija. Ukoliko se pronađe bug ili funkcionalnost nije u potpunosti dovršena, Pull Request se vraća autoru na doradu prije merge-a.
- Ako se nakon merge-a ipak pronađe bug, tim ga evidentira, prioritizira i rješava kroz dogovoreni proces, pri čemu QA osoba pomaže u reprodukciji problema i verifikaciji ispravke.
- Svaki član tima je dodatno motivisan da detaljno testira svoj dio koda prije otvaranja Pull Requesta, jer se greške sada ranije otkrivaju i vraćaju autoru na doradu.
- QA osoba je motivisana da detaljno provjeri svaki dio funkcionalnosti prije merge-a, jer se time smanjuje broj grešaka koje se otkrivaju neposredno prije deploymenta.
- Kao rezultat ovih promjena, tim očekuje kvalitetniji kod, stabilniji develop branch, manje hitnih ispravki pred deployment i bolju raspodjelu odgovornosti unutar tima.