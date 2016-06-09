package org.scalacheck.contrib

import org.junit.runner.Description
import org.scalacheck._
import org.scalacheck.{Test => SchkTest}
import org.junit.runner.notification.{Failure, RunNotifier}
import org.scalacheck.Prop.Result
import java.lang.{Boolean, Throwable}

import org.scalacheck.util.ConsoleReporter
import org.scalacheck.util.Pretty.Params

import scala.org.scalacheck.contrib.ScalaCheckConfig

/**
 * This a JUnit runner that allows to run ScalaCheck properties (created into an object that implements
 * Properties) as part of a JUnit test suite. Each property will be counted as a failure or passed test
 * by JUnit.
 *
 * Properties are written in the exact same way as pure ScalaCheck; the only aifference is that the test suite class
 * needs to be annotated with @RunWith[classOf[ScalaCheckJUnitPropertiesRunner]] so that JUnit knows how to run
 * the tests
 */
class ScalaCheckJUnitPropertiesRunner(suiteClass: java.lang.Class[Properties]) extends org.junit.runner.Runner {

	private val properties = suiteClass.newInstance

	lazy val getDescription = createDescription(properties)

	/**
	 * Create a description
	 */
	private def createDescription(props: Properties): Description = {
		val description = Description.createSuiteDescription(props.name)
		props.properties.foreach(p => Description.createTestDescription(p._2.getClass, p._1))
		description
	}

	// Our custom tes callback, used to keep JUnit's runner updated about test progress
	private[contrib] class CustomTestCallback(notifier:RunNotifier, desc: Description) extends Test.TestCallback {
		// TODO: is it even possible to obtain the correct stack trace? ScalaCheck doesn't throw Exceptions for property failures!
		def failure = new Failure(desc, new Throwable("ScalaCheck property did not hold true"))

		/** Called whenever a property has finished testing */
		override def onTestResult(name: String, res: Test.Result) = {
			res.status match {
				case Test.Passed => {} // Test passed, nothing to do
				case Test.Proved(_) => {} // Test passed, nothing to do
				case Test.Exhausted => notifier.fireTestIgnored(desc) // exhausted tests are marked as ignored in JUnit
				case _ => notifier.fireTestFailure(failure) // everything else is a failed test
			}
		}
	}

	// we'll use this one to report status to the console, and we'll chain it with our custom reporter
	val consoleReporter = new ConsoleReporter(1)

	def customConfig(parameters: Test.Parameters, description: Description): Test.Parameters =
		if(properties.isInstanceOf[ScalaCheckConfig])
			properties.asInstanceOf[ScalaCheckConfig]
				.withConfig(parameters, description)
		else parameters

	/**
	 * Run this <code>Suite</code> of tests, reporting results to the passed <code>RunNotifier</code>.
	 * This class's implementation of this method invokes <code>run</code> on an instance of the
	 * <code>suiteClass</code> <code>Class</code> passed to the primary constructor, passing
	 * in a <code>Reporter</code> that forwards to the  <code>RunNotifier</code> passed to this
	 * method as <code>notifier</code>.
	 *
	 * @param notifier the JUnit <code>RunNotifier</code> to which to report the results of executing
	 * this suite of tests
	 */
	def run(notifier: RunNotifier) {

		def ourConfig(parameters: Test.Parameters, descObj: Description, prop: Prop): Test.Parameters = {
			val testCallback = consoleReporter.chain(new CustomTestCallback(notifier, descObj))
			parameters.withTestCallback(Option(parameters.testCallback).fold(testCallback)(_.chain(testCallback)))
		}

		properties.properties.map({ propTuple =>
			propTuple match {
				case (desc, prop) => {
					val descObj = Description.createTestDescription(prop.getClass, desc)

					// TODO: is there a better way to do this? It seems that JUnit is not printing the actual name of the test case to the screen as it runs
					print("Running property: " + desc)

					notifier.fireTestStarted(descObj)
					SchkTest.check(prop)(parameters => ourConfig(customConfig(parameters, descObj), descObj, prop))
					notifier.fireTestFinished(descObj)
				}
			}
		})
	}

	/**
	 * Returns the number of tests that are expected to run when this ScalaTest <code>Suite</code>
	 * is run.
	 *
	 * @return the expected number of tests that will run when this suite is run
	 */
	override def testCount() = properties.properties.size
}