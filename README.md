# ⏱️ Timer1000: Medlemsregistrering & Tidtagarur (JavaFX & JSON)

## 🌟 Projektöversikt

Timer1000 är en robust desktopapplikation byggd med JavaFX som kombinerar ett validerat medlemsregister med ett precist tidtagningssystem. Applikationen lagrar både medlemsdata och loggade tidsmätningar i lokala JSON-filer.

Projektet utvecklades med fokus på modern Java-utveckling (JDK 21+) och Apache Maven för beroendehantering, vilket förenklar installationen av externa bibliotek som Jackson för JSON-serialisering.

## Funktioner

- **Medlemsregistrering**: Formulär för inmatning av Förnamn, Efternamn, Telefonnummer och Adress
- **Validering**: Strikt validering av obligatoriska fält och format för Telefonnummer (endast siffror)
- **Högprecisionstidtagning**: Tidtagarur med hundradelssekunders precision (HH:MM:SS.HH)
- **Kopplad Loggning**: Sparar mätt tid mot en vald, sparad medlem
- **JSON Persistens**: Använder Jackson för att lagra all data (`members.json`, `timelogs.json`)
- **Dynamiskt UI**: Uppdatering av medlemslistan i realtid via en "Ladda om medlemmar"-knapp
- **Historik**: Visar alla sparade tidsloggar i en TableView

## 🛠️ Tekniker och Beroenden

- **Huvudspråk**: Java (Kräver JDK 21 eller senare)
- **Gränssnitt**: JavaFX (OpenJFX)
- **Byggverktyg**: Apache Maven
- **Datahantering**: Jackson (för JSON-serialisering/deserialisering)

### Maven Dependencies (pom.xml)

Projektet hanterar följande kritiska beroenden via Maven:

| Bibliotek | Syfte |
|-----------|-------|
| `org.openjfx:javafx-controls` | Huvudkomponenter för användargränssnittet (Knappar, Fält, VBox/GridPane) |
| `com.fasterxml.jackson.core:jackson-databind` | Huvudbiblioteket för att konvertera Java-objekt (Member, TimeLogEntry) till JSON och vice versa |
| `org.openjfx:javafx-maven-plugin` | Maven-plugin som möjliggör körning av JavaFX-applikationen |

## 🚀 Kom igång (Kör Applikationen)

### Förutsättningar

- **Java Development Kit (JDK)**: Version 21 eller senare
- **Apache Maven**: (Ofta inbyggt i moderna IDE:er som IntelliJ)
- **Git**: För att klona repot

### 1. Klona Repositoriet

git clone [DITT_GITHUB_REPO_URL]
cd Timer1000

text

### 2. Bygg och Ladda Beroenden

Öppna projektet i din IDE (IntelliJ rekommenderas). Maven kommer automatiskt att ladda ner alla nödvändiga bibliotek (JavaFX, Jackson) baserat på `pom.xml`.

### 3. Modulkonfiguration (module-info.java)

Eftersom projektet använder Java Module System, måste Jackson vara explicit deklarerat. Säkerställ att din `module-info.java` innehåller följande rad:

// I module-info.java
module org.example.timer1000 {
// ... befintliga JavaFX requires ...

text
requires com.fasterxml.jackson.databind; // <--- KRITISK FÖR JSON

// ... resten av filen ...
}

text

### 4. Kör Applikationen

Du kan starta applikationen direkt från din IDE eller via Maven-kommandot:

mvn javafx:run

text

## 📂 Kodstruktur och Designval

### 1. Datapersistens: JsonManager.java

Denna klass hanterar all interaktion med filsystemet och Jackson.

- **Strategi**: Bytet från databas till JSON valdes för att förenkla projektet. JSON ger enkel struktur för data utan behov av JDBC-drivrutiner eller SQL
- **Funktion**: `JsonManager` läser och skriver hela listor (`List<Member>`) till sina respektive filer (`members.json`, `timelogs.json`) vid varje sparhändelse
- **ID-hantering**: Eftersom JSON inte har automatisk inkrementering, hanterar `JsonManager.saveMember()` ID-tilldelningen genom att hitta det högsta befintliga ID:t och lägger till ett

### 2. Datamodeller (Member.java och TimeLogEntry.java)

| Klass | Fältdesign | JSON-kompatibilitet |
|-------|------------|---------------------|
| `Member` | Inkluderar `id`, `firstName`, `lastName`, `phone`, `adress` | Måste ha en default (no-argument) konstruktor samt getters och setters för att Jackson ska kunna serialisera/deserialisera objekten korrekt |
| `TimeLogEntry` | Inkluderar `memberId` (FK) och `durationSeconds` | Länkklass för att spara tidsdata mot den unika medlemsnyckeln |

### 3. Tidtagarlogik

- Tidtagningen drivs av `javafx.animation.Timeline` som uppdateras var 10:e millisekund (`Duration.millis(10)`)
- Tiden sparas internt som totala millisekunder (`totalMillis`) för att bibehålla precisionen under räkningen
- När tiden loggas (vid Stop), konverteras `totalMillis` till hela sekunder innan det sparas i `timelogs.json`, vilket håller datalagret rent och konsekvent

## 👩‍💻 Bidrag och Kontakt

**Utvecklare**: Alex Weström

Välkommen att testa och bidra till Timer1000!
