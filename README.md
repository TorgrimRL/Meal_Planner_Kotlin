# Meal Planner (Kotlin)

Meal Planner er et kommandolinjebasert Kotlin-prosjekt utviklet for å bestå for få jobbet med databaser i Kotlin. Prosjektet har lært meg hvordan man kan bruke SQL og DAO-mønsteret i Kotlin for å håndtere datatilgang til en SQLite-database, samtidig man opprettholder en tydelig separasjon av ansvar mellom brukergrensesnitt (Menu), forretningslogikk (MealPlanner) og datatilgang (DAO).

## Designvalg

- **Separation of Concerns:**  
  Koden er delt opp i separate moduler:
  - **Menu:** Tar seg av all brukerinteraksjon (input, validering og utskrift).
  - **MealPlanner:** Inneholder den sentrale forretningslogikken og koordinerer operasjonene.
  - **DAO:** Ansvarlig for all kommunikasjon med databasen, slik at databaseoperasjoner isoleres fra den øvrige logikken.
  
- **Bruk av PreparedStatement og `use`-blokker:**  
  For å forhindre SQL-injeksjon og sikre korrekt ressursstyring, benyttes PreparedStatement med parameterplassholdere og Kotlin sin `use`-funksjon for automatisk lukking av ressurser.

- **Modularitet og vedlikehold:**  
  Ved å dele opp koden på denne måten blir prosjektet lettere å vedlikeholde, teste og videreutvikle. 


---

## Komme i gang

### Kloning

For å klone prosjektet, kjør følgende kommando i terminalen:
```
git clone https://github.com/TorgrimRL/Meal-Planner-Kotlin.git
```

### Bygging

Prosjektet bygges med Gradle Wrapper og er konfigurert for å kjøre med Java 21. I rotmappen, kjør:
```
./gradlew build
```
Dette kompilerer kildekoden og bygger en fat jar med alle runtime-avhengigheter. Fat jar-filen blir plassert i:
```
Meal Planner (Kotlin)/task/build/libs/
```
### Kjøring

Siden prosjektet er interaktivt (det venter på brukerinput), anbefales det å kjøre den genererte fat jar-filen direkte i stedet for via ./gradlew run.

Kjør fat jar-filen med:
```
java -jar "Meal Planner (Kotlin)/task/build/libs/Meal_Planner__Kotlin_-task.jar"
```

Når du kjører programmet, vil du få opp en melding som:
```
What would you like to do (add, show, plan, save, exit)?
```
og deretter kan du interaktivt gi input i terminalen.


## Mulige forbedringer

- **Selvskrevne tester:**  
  Selv om prosjektet er utformet for å bestå Hyperskills interne tester, vil implementering av enhetstester gjøre det enklere å vedlikeholde og videreutvikle prosjektet over tid.

- **Dynamisk utvidelse av måltider:**  
  I dag er strukturen statisk, men prosjektet kan forbedres ved å tillate dynamisk utvidelse av måltider og menyer, for eksempel ved å legge til nye kategorier.

- **Styrket inputvalidering:**  
  Videre utvikling kan fokusere på  inputvalidering for å sikre at data alltid er korrekt formatert, noe som reduserer risikoen for feil og øker brukervennligheten.

