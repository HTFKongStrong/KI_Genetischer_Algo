# KI_Genetischer_Algo
Erstellung eines Genetischen Algorithmus im Rahmen des Fachs Künstlicher Intelligenz


Kurze Beschreibung:

Eigens geschriebene Methoden des Genetischen Algorithmus befinden sich zur Übersichtlichkeit im package mulsp.solvers.gaMethods, da wir mehrere Methoden z.B für die Mutation und Selektion geschrieben haben. Im package mulsp.solvers.ga befindet sicher weiterhin die Klasse Individual, aber auch verschiedene Implementationen der Methode solve(). Jede Klasse in diesem package, außer Individual, stellt sozusagen eine Idee bzw. Optimierung durch das Aufrufen unterschiedlicher Methoden dar. So wurde z.B in der Klasse GaSolverSelektion, die Roulette-Selektion angewandt während bei den anderen Klassen die Wettkampf-Selektion eingesetzt wurde. Insgesamt wurden also verschiedene Möglichkeiten in der Selektion, Mutation und der Entscheidungsvariablen ausprobiert, um ein optimales Ergebnis zu schaffen.




Vom Bestergebnis: gesetzte Entscheidungsvariablen
- Gesetzte Werte:
  - Populationsgröße:250
  - Kreuzungswahrscheinlichkeit:0,7
  - Mutationswahrscheinlichkeit:0,3
  - AnzahlderElterndieindieneueGenerationübernommenwerden:10
  - Selektionsdruck/AnzahlderElterndiezurReproduktiongewähltwerdendürfen:10
- Benutzte Methoden
  - Template-Crossover
  - Wettkampf-Selektion
  - Swap-Mutation–mitstetigerMutationswahrscheinlichkeit
  - Delete-n-last-Schema
- Wurde mit dem GaSolverWettkampf erreicht
