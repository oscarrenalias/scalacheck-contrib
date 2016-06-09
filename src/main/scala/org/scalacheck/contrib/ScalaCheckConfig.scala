package scala.org.scalacheck.contrib

import org.scalacheck.Test

trait ScalaCheckConfig {
  def withConfig(parameters: Test.Parameters, description: String): Test.Parameters
}
