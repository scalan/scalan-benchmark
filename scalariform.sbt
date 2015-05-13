import scalariform.formatter.preferences._

scalariformSettings

excludeFilter in ScalariformKeys.format <<= excludeFilter { _ || "*Impl.scala" }

ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(RewriteArrowSymbols, false)
  .setPreference(AlignParameters, true)
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(DoubleIndentClassDeclaration, true)
