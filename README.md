ScalaCheck-Contrib
==================
This repository contains additional features for ScalaCheck that are not part of the main repository. At the moment only a JUnit 4 runner is available.

JUnit 4 runner for ScalaCheck
-----------------------------

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

Getting Started
----------------
With Maven:

        <repository>
            <id>phunkphorce.github.com</id>
            <name>Maven2 repository</name>
            <url>http://phunkphorce.github.com/maven</url>
        </repository>
        ...
        <dependency>
            <groupId>net.renalias.scalacheck</groupId>
            <artifactId>scalacheck-contrib_2.9.0-1</artifactId>
            <version>20110916</version>
        </dependency>

With SBT (0.7.x):

```scala
    val repo = "phunkporce.github.com" at "http://phunkphorce.github.com/maven"
	val scalacheckContrib = "net.renalias.scalacheck" %% "scalacheck-contrib" % "20110916"
```

Please note that the library is currently only compield for Scala 2.9.0-1.

With the plain JAR file:

Get the JAR file from here: https://github.com/phunkphorce/scalacheck-contrib/raw/master/target/scalacheck-contrib_2.9.0-1-20110916.jar

License
-------
This code is copyrighted by Accenture, and is released under the Apache 2.0 License: http://www.apache.org/licenses/LICENSE-2.0.html.