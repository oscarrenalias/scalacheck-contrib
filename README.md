This repository contains additional features for ScalaCheck that are not part of the main repository. At the moment only a JUnit 4 runner is available.

== JUnit 4 runner for ScalaCheck ==

This is a simple JUnit 4 runner that allows "pure" ScalaCheck tests to be run as part of existing JUnit 4 suites. Properties that hold true will be reported as succesful test cases to JUnit, and properties that falsify will be shown as error test cases by JUnit.

The only change needed in our existing ScalaCheck code is to annotate the class with JUnit's @RunWith annotation, as shown in the example below. The rest of the code is exactly like plain ScalaCheck code, and the property suite can still be run using the Properties.check method, as usual (e.g. from Scala's REPL)

```scala
import org.scalacheck.Prop._
import org.junit.runner.RunWith

@RunWith(classOf[ScalaCheckJUnitPropertiesRunner])
class ScalaCheckTest extends Properties("My ScalaCheck test example") {
	property("first test") = forAll {
		...
	}
}
```

Run the test suite now with the "mvn test" command, which by default uses JUnit as the unit testing framework and JUnit will execute the test suite automatically using ScalaCheck for the property checks.