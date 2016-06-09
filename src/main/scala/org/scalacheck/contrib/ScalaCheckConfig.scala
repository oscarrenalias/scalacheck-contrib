package scala.org.scalacheck.contrib

import org.junit.runner.Description
import org.scalacheck.Test

trait ScalaCheckConfig {
  def withConfig(parameters: Test.Parameters, description: Description): Test.Parameters
}
