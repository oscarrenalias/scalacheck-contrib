package org.scalacheck.contrib

import org.junit.runner.Description
import org.scalacheck._
import org.scalacheck.{Test=>SchkTest}
import org.scalacheck.Test.Params
import org.junit.runner.notification.{Failure, RunNotifier}
import java.lang.{Throwable, Boolean}

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

	/**
	 * Create a description
	 */
	lazy val getDescription = createDescription(properties)

	private def createDescription(props: Properties): Description = {
		val description = Description.createSuiteDescription(props.name)
		props.properties.foreach(p => Description.createTestDescription(p._2.getClass, p._1))
		description
	}

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
	private class CustomConsoleReporter extends ConsoleReporter(1)

	/**
	 * Checks a property and returns the boolean value wrapped in an Option
	 */
	private implicit def doCheckOption(p: Prop): Option[Boolean] = Option(SchkTest.check(Params(testCallback = new CustomConsoleReporter), p).passed)

	def run(notifier: RunNotifier) {
		properties.properties.map({ propTuple =>
			propTuple match {
				case (desc, prop) => {
					val descObj = Description.createTestDescription(prop.getClass, desc)

					notifier.fireTestStarted(descObj)

					// TODO: is there a better way to do this? It seems that JUnit is not printing the actual name of the test case to the screen as it runs
					print("Running property: " + desc)

					// log the failure only if the test failed, do nothing otherwise
					doCheckOption(prop) filter (_ == false) foreach (_ => notifier.fireTestFailure(new Failure(descObj, new Throwable("ScalaCheck property failed"))))
					// TODO: is it even possible to obtain the correct stack trace? ScalaCheck doesn't throw Exceptions for property failures!

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