package org.scalacheck.contrib

import org.scalacheck.Prop._
import org.junit.runner.RunWith
import org.scalacheck.{Arbitrary, Gen, Properties}

/**
 * This is an example of a JUnit test suite implemented as a ScalaCheck Properties object, where all
 * unit test cases are properties that are evaluated as separate JUnit test cases
 *
 * The code is exactly the same as if the property was run using org.scalacheck.Test.check (and in
 * fact it can still be run like that) but using the @RunWith annotation with our custom runner
 * it can also be run as a JUnit suite so that it can easily be integrated with existing JUnit test suites
 */
@RunWith(classOf[ScalaCheckJUnitPropertiesRunner])
class ScalaCheckRunnerTest extends Properties("Rectangle property suite") {
	import RectangleGenerator._

	// This holds true and will be reported as a passed test by JUnit
	property("Test biggerThan") = forAll { (r1:Rectangle, r2:Rectangle) =>
		(r1 biggerThan r2) == (r1.area > r2.area)
	}

	// This does not hold true and will be reported as a test error by JUnit
	property("Failed test") = forAll {(a:Int) =>
		a == 1
	}

	// This holds true, and ScalaCheck will output the test data grouping to the console
	property("Test with collection of data") = forAll {(a:Int) =>
		(a > 0 && a <= 10) ==> collect(a) {
			2 * a == a + a
		}
	}
}

/**
 * Simple case class that will be used as the basis for our examples
 */
case class Rectangle(val width:Double, val height:Double) {
	// when the width is a multiple of 11, this will fail for the sake of having a bug in our code
	lazy val area =  if(width % 3 ==0) (width * 1.0001 * height) else (width * height)
	// valid version of the method above
	lazy val areaCorrect = (width * height)
	lazy val perimeter = (2*width) + (2*height)
	def biggerThan(r:Rectangle) = (area > r.area)
}

/**
 * Generator of case objects for the Rectangle class, as well as an arbitrary generator
 */
object RectangleGenerator {
	// generator for the Rectangle case class
	val rectangleGen: Gen[Rectangle] = for {
		height <- Gen.choose(0, 9999)
		width <- Gen.choose(0, 9999)
	} yield (Rectangle(width, height))

	// Arbitrary generator of rectangles
	implicit val arbRectangle: Arbitrary[Rectangle] = Arbitrary(rectangleGen)
}