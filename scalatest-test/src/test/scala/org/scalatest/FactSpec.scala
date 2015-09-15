/*
 * Copyright 2001-2015 Artima, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.scalatest


import org.scalactic.PrettyMethods
import org.scalatest.exceptions.TestFailedException
import SharedHelpers.thisLineNumber
import Fact._
import prop.TableDrivenPropertyChecks._
import prop.TableFor1
import matchers.{ FailureMessage, NegatedFailureMessage, MidSentenceFailureMessage, MidSentenceNegatedFailureMessage }

/*
No(
  No(!Yes(3 equaled 3)) &&
  No(4 did not equal 3)
)

No(4 did not equal 3)
*/
class FactSpec extends FreeSpec with Matchers with PrettyMethods with ExpectationHavePropertyMatchers {

  "A Fact" - {
    // As if we said expectResult(3) { 1 + 1 }
    val noFact: Expectation = No("Expected 3, but got 2", "3 did not equal 2", "expected 3, but got 2", "3 did not equal 2")
    // As if we said expectResult(3) { 1 + 2 }
    val yesFact: Expectation = Yes("3 equaled 3", "3 equaled 3")
    "should have isYes and isNo methods" in {
      noFact.isYes shouldBe false
      noFact.isNo shouldBe true
      yesFact.isYes shouldBe true
      yesFact.isNo shouldBe false
    }
    "should have a toAssertion method that either returns Succeeded or throws TestFailedException with the correct error message and stack depth" in {
      yesFact.toAssertion shouldBe Succeeded
      val caught = the [TestFailedException] thrownBy noFact.toAssertion
      caught should have message "Expected 3, but got 2"
      caught.failedCodeLineNumber shouldEqual Some(thisLineNumber - 2)
      caught.failedCodeFileName shouldBe Some("FactSpec.scala")
    }
    "should offer a toBoolean method, even though it is redundant with isYes" in {
      noFact.toBoolean shouldBe false
      yesFact.toBoolean shouldBe true
    }
    "should construct localized strings from the raw strings and args" in {
      val fact = No("{0} did not equal {1}", "{0} equaled {1}", "{0} did not equal {1}", "{0} equaled {1}", Vector(1, 2), Vector(1, 2))
      fact.factMessage shouldBe ("1 did not equal 2")
      fact.simplifiedFactMessage shouldBe ("1 equaled 2")
      fact.midSentenceFactMessage shouldBe ("1 did not equal 2")
      fact.midSentenceSimplifiedFactMessage shouldBe ("1 equaled 2")
      fact.rawFactMessage shouldBe ("{0} did not equal {1}")
      fact.rawSimplifiedFactMessage shouldBe ("{0} equaled {1}")
      fact.rawMidSentenceFactMessage shouldBe ("{0} did not equal {1}")
      fact.rawMidSentenceSimplifiedFactMessage shouldBe ("{0} equaled {1}")
      fact.factMessageArgs shouldBe (Vector(1, 2))
      fact.simplifiedFactMessageArgs shouldBe (Vector(1, 2))
      fact.midSentenceFactMessageArgs shouldBe (Vector(1, 2))
      fact.midSentenceSimplifiedFactMessageArgs shouldBe (Vector(1, 2))
      fact.composite shouldBe (false)
    }

    "should use midSentenceFactMessageArgs to construct midSentenceFactMessage" in {
      val fact = No("{0} did not equal {1}", "{0} equaled {1}", "{0} did not equal {1}", "{0} equaled {1}", Vector.empty, Vector.empty, Vector(1, 2), Vector.empty)
      fact.midSentenceFactMessage should be ("1 did not equal 2")
    }

    "should use midSentenceSimplifiedFactMessageArgs to construct midSentenceSimplifiedFactMessage" in {
      val fact = No("{0} did not equal {1}", "{0} equaled {1}", "{0} did not equal {1}", "{0} equaled {1}", Vector.empty, Vector.empty, Vector.empty, Vector(1, 2))
      fact.midSentenceSimplifiedFactMessage should be ("1 equaled 2")
    }
    "when simplified with !" - {
      "should keep the same main message, but change Yes to No or No to Yes " in {
        !noFact should equal (Unary_!(noFact))
        val fact2 = No("Expected {0}, but got {1}", "{0} did not equal {1}", "expected {0}, but got {1}", "{0} did not equal {1}", Vector(3, 2), Vector(3, 2))
        fact2.factMessage shouldBe ("Expected 3, but got 2")
        fact2.simplifiedFactMessage shouldBe ("3 did not equal 2")
        fact2.midSentenceFactMessage shouldBe ("expected 3, but got 2")
        fact2.midSentenceSimplifiedFactMessage shouldBe ("3 did not equal 2")
        fact2.rawFactMessage shouldBe ("Expected {0}, but got {1}")
        fact2.rawSimplifiedFactMessage shouldBe ("{0} did not equal {1}")
        fact2.rawMidSentenceFactMessage shouldBe ("expected {0}, but got {1}")
        fact2.rawMidSentenceSimplifiedFactMessage shouldBe ("{0} did not equal {1}")
        fact2.factMessageArgs shouldBe (Vector(3, 2))
        fact2.simplifiedFactMessageArgs shouldBe (Vector(3, 2))
        fact2.midSentenceFactMessageArgs shouldBe (Vector(3, 2))
        fact2.midSentenceSimplifiedFactMessageArgs shouldBe (Vector(3, 2))
        fact2.composite shouldBe (false)
/*
        val fact2Simplified = !fact2
        fact2Simplified should equal (Unary_!(Yes("{0} did not equal null", "The reference equaled null", "{0} did not equal null", "the reference equaled null", Vector("howdy"), Vector.empty)))
        fact2Simplified.factMessage shouldBe ("The reference equaled null")
        fact2Simplified.simplifiedFactMessage shouldBe ("\"howdy\" did not equal null")
        fact2Simplified.midSentenceFactMessage shouldBe ("the reference equaled null")
        fact2Simplified.midSentenceSimplifiedFactMessage shouldBe ("\"howdy\" did not equal null")
        fact2Simplified.rawFactMessage shouldBe ("The reference equaled null")
        fact2Simplified.rawSimplifiedFactMessage shouldBe ("{0} did not equal null")
        fact2Simplified.rawMidSentenceFactMessage shouldBe ("the reference equaled null")
        fact2Simplified.rawMidSentenceSimplifiedFactMessage shouldBe ("{0} did not equal null")
        fact2Simplified.factMessageArgs shouldBe (Vector.empty)
        fact2Simplified.simplifiedFactMessageArgs shouldBe (Vector("howdy"))
        fact2Simplified.midSentenceFactMessageArgs shouldBe (Vector.empty)
        fact2Simplified.midSentenceSimplifiedFactMessageArgs shouldBe (Vector("howdy"))
        fact2Simplified.composite shouldBe (false)
*/
      }
      "should maintain the same composite state" in {
        !noFact should have (composite(false))
        
        val factCopy = noFact.asInstanceOf[No].copy(composite = true)
        !factCopy should have (composite(true))
      }
      "should return the original object when simplified yet again" in {
        val notYesFact = !yesFact
        !notYesFact should be theSameInstanceAs yesFact
      }
      "should return the opposite of underlying from its isYes method" in {
        val notYesFact = !yesFact
        notYesFact.isYes should equal (!(yesFact.isYes))
      }
      "should return the opposite of underlying from its isNo method" in {
        val notYesFact = !yesFact
        notYesFact.isNo should equal (!(yesFact.isNo))
      }
    }
  }
/*
  "The Fact obtained from and-ing two Facts" - {
    "should be lazy about constructing strings" - {
      "for No && No" in {
        val leftSideNo = No(Resources.rawWasNotGreaterThan, Resources.rawWasGreaterThan, Resources.rawWasNotGreaterThan, Resources.rawWasGreaterThan, Vector('a', 'b'),Vector('a', 'b'),Vector('a', 'b'),Vector('a', 'b'))
        val rightSideNo = No(Resources.rawWasNotGreaterThan, Resources.rawWasGreaterThan, Resources.rawWasNotGreaterThan, Resources.rawWasGreaterThan, Vector('a', 'd'),Vector('a', 'd'),Vector('a', 'd'),Vector('a', 'd'))
        val fact = leftSideNo && rightSideNo
        fact shouldBe a [No]
        fact.rawFactMessage should be (Resources.rawWasNotGreaterThan)
        fact.rawSimplifiedFactMessage should be (Resources.rawWasGreaterThan)
        fact.rawMidSentenceFactMessage should be (Resources.rawWasNotGreaterThan)
        fact.rawMidSentenceSimplifiedFactMessage should be (Resources.rawWasGreaterThan)
        fact.factMessage should be (Resources.wasNotGreaterThan('a'.pretty, 'b'.pretty))
        fact.simplifiedFactMessage should be (Resources.wasGreaterThan('a'.pretty, 'b'.pretty))
        fact.midSentenceFactMessage should be (Resources.wasNotGreaterThan('a'.pretty, 'b'.pretty))
        fact.midSentenceSimplifiedFactMessage should be (Resources.wasGreaterThan('a'.pretty, 'b'.pretty))
        fact.factMessageArgs should be (Vector('a', 'b'))
        fact.simplifiedFactMessageArgs should be (Vector('a', 'b'))
        fact.composite should be (false)
      }

      "for No && Yes" in {
        val leftSideNo = No(Resources.rawWasNotGreaterThan, Resources.rawWasGreaterThan, Resources.rawWasNotGreaterThan, Resources.rawWasGreaterThan, Vector('a', 'b'),Vector('a', 'b'),Vector('a', 'b'),Vector('a', 'b'))
        val rightSideYes = Yes(Resources.rawWasNotLessThan, Resources.rawWasLessThan, Resources.rawWasNotLessThan, Resources.rawWasLessThan, Vector('a', 'd'),Vector('a', 'd'),Vector('a', 'd'),Vector('a', 'd'))
        val fact = leftSideNo && rightSideYes
        fact shouldBe a [No]
        fact.rawFactMessage should be (Resources.rawWasNotGreaterThan)
        fact.rawSimplifiedFactMessage should be (Resources.rawWasGreaterThan)
        fact.rawMidSentenceFactMessage should be (Resources.rawWasNotGreaterThan)
        fact.rawMidSentenceSimplifiedFactMessage should be (Resources.rawWasGreaterThan)
        fact.factMessage should be ("No(" + Resources.wasNotGreaterThan('a'.pretty, 'b'.pretty) + ")")
        fact.simplifiedFactMessage should be ("No(" + Resources.wasGreaterThan('a'.pretty, 'b'.pretty) + ")")
        fact.midSentenceFactMessage should be ("No(" + Resources.wasNotGreaterThan('a'.pretty, 'b'.pretty) + ")")
        fact.midSentenceSimplifiedFactMessage should be ("No(" + Resources.wasGreaterThan('a'.pretty, 'b'.pretty) + ")")
        fact.factMessageArgs should be (Vector('a', 'b'))
        fact.simplifiedFactMessageArgs should be (Vector('a', 'b'))
        fact.composite should be (false)
      }

      "for Yes && No" in {
        val leftSideYes = Yes(Resources.rawWasNotGreaterThan, Resources.rawWasGreaterThan, Resources.rawWasNotGreaterThan, Resources.rawWasGreaterThan, Vector('c', 'b'),Vector('c', 'b'),Vector('c', 'b'),Vector('c', 'b'))
        val rightSideNo = No(Resources.rawWasNotGreaterThan, Resources.rawWasGreaterThan, Resources.rawWasNotGreaterThan, Resources.rawWasGreaterThan, Vector('c', 'd'),Vector('c', 'd'),Vector('c', 'd'),Vector('c', 'd'))
        val fact = leftSideYes && rightSideNo
        fact shouldBe a [Binary_&&]
        fact.isNo shouldBe true
        fact.rawFactMessage should be (Resources.rawCommaDoubleAmpersand)
        fact.rawSimplifiedFactMessage should be (Resources.rawCommaDoubleAmpersand)
        fact.rawMidSentenceFactMessage should be (Resources.rawCommaDoubleAmpersand)
        fact.rawMidSentenceSimplifiedFactMessage should be (Resources.rawCommaDoubleAmpersand)
        fact.factMessage should be ("No(" + Resources.commaDoubleAmpersand("Yes(" + Resources.wasGreaterThan('c'.pretty, 'b'.pretty) + ")", "No(" + Resources.wasNotGreaterThan('c'.pretty, 'd'.pretty) + ")") + ")")
        fact.simplifiedFactMessage should be ("No(" + Resources.commaDoubleAmpersand("Yes(" + Resources.wasGreaterThan('c'.pretty, 'b'.pretty) + ")", "No(" + Resources.wasGreaterThan('c'.pretty, 'd'.pretty) + ")") + ")")
        fact.midSentenceFactMessage should be ("No(" + Resources.commaDoubleAmpersand("Yes(" + Resources.wasGreaterThan('c'.pretty, 'b'.pretty) + ")", "No(" + Resources.wasNotGreaterThan('c'.pretty, 'd'.pretty) + ")") + ")")
        fact.midSentenceSimplifiedFactMessage should be ("No(" + Resources.commaDoubleAmpersand("Yes(" + Resources.wasGreaterThan('c'.pretty, 'b'.pretty) + ")", "No(" + Resources.wasGreaterThan('c'.pretty, 'd'.pretty) + ")") + ")")
        fact.factMessageArgs should be (Vector(SimplifiedFactMessage(leftSideYes), MidSentenceFactMessage(rightSideNo)))
        fact.simplifiedFactMessageArgs should be (Vector(SimplifiedFactMessage(leftSideYes), MidSentenceSimplifiedFactMessage(rightSideNo)))
        fact.midSentenceFactMessageArgs should be (Vector(MidSentenceSimplifiedFactMessage(leftSideYes), MidSentenceFactMessage(rightSideNo)))
        fact.midSentenceSimplifiedFactMessageArgs should be (Vector(MidSentenceSimplifiedFactMessage(leftSideYes), MidSentenceSimplifiedFactMessage(rightSideNo)))
        fact.composite should be (true)
      }

      "for Yes && Yes" in {
        val leftSideYes = Yes(Resources.rawWasNotGreaterThan, Resources.rawWasGreaterThan, Resources.rawWasNotGreaterThan, Resources.rawWasGreaterThan, Vector('e', 'b'),Vector('e', 'b'),Vector('e', 'b'),Vector('e', 'b'))
        val rightSideYes = Yes(Resources.rawWasNotGreaterThan, Resources.rawWasGreaterThan, Resources.rawWasNotGreaterThan, Resources.rawWasGreaterThan, Vector('e', 'd'),Vector('e', 'd'),Vector('e', 'd'),Vector('e', 'd'))
        val fact = leftSideYes && rightSideYes
        fact shouldBe a [Binary_&&]
        fact.isYes shouldBe true
        fact.rawFactMessage should be (Resources.rawCommaDoubleAmpersand)
        fact.rawSimplifiedFactMessage should be (Resources.rawCommaDoubleAmpersand)
        fact.rawMidSentenceFactMessage should be (Resources.rawCommaDoubleAmpersand)
        fact.rawMidSentenceSimplifiedFactMessage should be (Resources.rawCommaDoubleAmpersand)
        fact.factMessage should be ("Yes(" + Resources.commaDoubleAmpersand("Yes(" + Resources.wasGreaterThan('e'.pretty, 'b'.pretty) + ")", "Yes(" + Resources.wasNotGreaterThan('e'.pretty, 'd'.pretty) + ")") + ")")
        fact.simplifiedFactMessage should be ("Yes(" + Resources.commaDoubleAmpersand("Yes(" + Resources.wasGreaterThan('e'.pretty, 'b'.pretty) + ")", "Yes(" + Resources.wasGreaterThan('e'.pretty, 'd'.pretty) + ")") + ")")
        fact.midSentenceFactMessage should be ("Yes(" + Resources.commaDoubleAmpersand("Yes(" + Resources.wasGreaterThan('e'.pretty, 'b'.pretty) + ")", "Yes(" + Resources.wasNotGreaterThan('e'.pretty, 'd'.pretty) + ")") + ")")
        fact.midSentenceSimplifiedFactMessage should be ("Yes(" + Resources.commaDoubleAmpersand("Yes(" + Resources.wasGreaterThan('e'.pretty, 'b'.pretty) + ")", "Yes(" + Resources.wasGreaterThan('e'.pretty, 'd'.pretty) + ")") + ")")
        fact.factMessageArgs should be (Vector(SimplifiedFactMessage(leftSideYes), MidSentenceFactMessage(rightSideYes)))
        fact.simplifiedFactMessageArgs should be (Vector(SimplifiedFactMessage(leftSideYes), MidSentenceSimplifiedFactMessage(rightSideYes)))
        fact.midSentenceFactMessageArgs should be (Vector(MidSentenceSimplifiedFactMessage(leftSideYes), MidSentenceFactMessage(rightSideYes)))
        fact.midSentenceSimplifiedFactMessageArgs should be (Vector(MidSentenceSimplifiedFactMessage(leftSideYes), MidSentenceSimplifiedFactMessage(rightSideYes)))
        fact.composite should be (true)
      }
    }

    "should be parenthesize composite facts" - {
      "for non-composite && composite" in {
        val leftSideYes = Yes(Resources.rawWasNotGreaterThan, Resources.rawWasGreaterThan, Resources.rawWasNotGreaterThan, Resources.rawWasGreaterThan, Vector('e', 'b'),Vector('e', 'b'),Vector('e', 'b'),Vector('e', 'b'), false)
        val rightSideYes = Yes(Resources.rawWasNotGreaterThan, Resources.rawWasGreaterThan, Resources.rawWasNotGreaterThan, Resources.rawWasGreaterThan, Vector('e', 'd'),Vector('e', 'd'),Vector('e', 'd'),Vector('e', 'd'), true)
        val fact = leftSideYes && rightSideYes
        fact.rawFactMessage should be (Resources.rawCommaDoubleAmpersand)
        fact.rawSimplifiedFactMessage should be (Resources.rawCommaDoubleAmpersand)
        fact.rawMidSentenceFactMessage should be (Resources.rawCommaDoubleAmpersand)
        fact.rawMidSentenceSimplifiedFactMessage should be (Resources.rawCommaDoubleAmpersand)
        fact.composite should be (true)
      }

      "for composite && non-composite" in {
        val leftSideYes = Yes(Resources.rawWasNotGreaterThan, Resources.rawWasGreaterThan, Resources.rawWasNotGreaterThan, Resources.rawWasGreaterThan, Vector('e', 'b'),Vector('e', 'b'),Vector('e', 'b'),Vector('e', 'b'), true)
        val rightSideYes = Yes(Resources.rawWasNotGreaterThan, Resources.rawWasGreaterThan, Resources.rawWasNotGreaterThan, Resources.rawWasGreaterThan, Vector('e', 'd'),Vector('e', 'd'),Vector('e', 'd'),Vector('e', 'd'), false)
        val fact = leftSideYes && rightSideYes
        fact.rawFactMessage should be (Resources.rawCommaDoubleAmpersand)
        fact.rawSimplifiedFactMessage should be (Resources.rawCommaDoubleAmpersand)
        fact.rawMidSentenceFactMessage should be (Resources.rawCommaDoubleAmpersand)
        fact.rawMidSentenceSimplifiedFactMessage should be (Resources.rawCommaDoubleAmpersand)
        fact.composite should be (true)
      }

      "for composite && composite" in {
        val leftSideYes = Yes(Resources.rawWasNotGreaterThan, Resources.rawWasGreaterThan, Resources.rawWasNotGreaterThan, Resources.rawWasGreaterThan, Vector('e', 'b'),Vector('e', 'b'),Vector('e', 'b'),Vector('e', 'b'), true)
        val rightSideYes = Yes(Resources.rawWasNotGreaterThan, Resources.rawWasGreaterThan, Resources.rawWasNotGreaterThan, Resources.rawWasGreaterThan, Vector('e', 'd'),Vector('e', 'd'),Vector('e', 'd'),Vector('e', 'd'), true)
        val fact = leftSideYes && rightSideYes
        fact.rawFactMessage should be (Resources.rawCommaDoubleAmpersand)
        fact.rawSimplifiedFactMessage should be (Resources.rawCommaDoubleAmpersand)
        fact.rawMidSentenceFactMessage should be (Resources.rawCommaDoubleAmpersand)
        fact.rawMidSentenceSimplifiedFactMessage should be (Resources.rawCommaDoubleAmpersand)
        fact.composite should be (true)
      }
    }
  }
*/
/*
  "The Expectation obtained from or-ing two Expectations" - {
    "should be lazy about constructing strings" - {

/*
      "for No || No" in {
        val leftSideNo = No(Resources.rawWasNotGreaterThan, Resources.rawWasGreaterThan, Resources.rawWasNotGreaterThan, Resources.rawWasGreaterThan, Vector('a', 'b'),Vector('a', 'b'),Vector('a', 'b'),Vector('a', 'b'))
        val rightSideNo = No(Resources.rawWasNotGreaterThan, Resources.rawWasGreaterThan, Resources.rawWasNotGreaterThan, Resources.rawWasGreaterThan, Vector('a', 'd'),Vector('a', 'd'),Vector('a', 'd'),Vector('a', 'd'))
        val fact = leftSideNo || rightSideNo
        fact shouldBe a [Binary_||]
        fact.isNo shouldBe true
        fact.rawFactMessage should be (Resources.rawCommaDoublePipe)
        fact.rawSimplifiedFactMessage should be (Resources.rawCommaDoublePipe)
        fact.rawMidSentenceFactMessage should be (Resources.rawCommaDoublePipe)
        fact.rawMidSentenceSimplifiedFactMessage should be (Resources.rawCommaDoublePipe)
        fact.factMessage should be ("No(" + Resources.commaDoublePipe("No(" + Resources.wasNotGreaterThan('a'.pretty, 'b'.pretty) + ")", "No(" + Resources.wasNotGreaterThan('a'.pretty, 'd'.pretty) + ")") + ")")
        fact.simplifiedFactMessage should be ("No(" + Resources.commaDoublePipe("No(" + Resources.wasNotGreaterThan('a'.pretty, 'b'.pretty) + ")", "No(" + Resources.wasGreaterThan('a'.pretty, 'd'.pretty) + ")") + ")")
        /*fact.midSentenceFactMessage should be (Resources.commaDoublePipe(Resources.wasNotGreaterThan('a'.pretty, 'b'.pretty), Resources.wasNotGreaterThan('a'.pretty, 'd'.pretty)))
        fact.midSentenceSimplifiedFactMessage should be (Resources.commaDoublePipe(Resources.wasNotGreaterThan('a'.pretty, 'b'.pretty), Resources.wasGreaterThan('a'.pretty, 'd'.pretty)))
        fact.factMessageArgs should be (Vector(FactMessage(leftSideNo), MidSentenceFactMessage(rightSideNo)))
        fact.simplifiedFactMessageArgs should be (Vector(FactMessage(leftSideNo), MidSentenceSimplifiedFactMessage(rightSideNo)))
        fact.midSentenceFactMessageArgs should be (Vector(MidSentenceFactMessage(leftSideNo), MidSentenceFactMessage(rightSideNo)))
        fact.midSentenceSimplifiedFactMessageArgs should be (Vector(MidSentenceFactMessage(leftSideNo), MidSentenceSimplifiedFactMessage(rightSideNo)))
        fact.composite should be (true)*/
       }
*/

      /*"for No || Yes" in {
        val leftSideNo = No(Resources.rawWasNotGreaterThan, Resources.rawWasGreaterThan, Resources.rawWasNotGreaterThan, Resources.rawWasGreaterThan, Vector('a', 'b'),Vector('a', 'b'),Vector('a', 'b'),Vector('a', 'b'))
        val rightSideYes = Yes(Resources.rawWasNotLessThan, Resources.rawWasLessThan, Resources.rawWasNotLessThan, Resources.rawWasLessThan, Vector('a', 'd'),Vector('a', 'd'),Vector('a', 'd'),Vector('a', 'd'))
        val fact = leftSideNo || rightSideYes
        fact shouldBe a [Binary_||]
        fact.isYes shouldBe true
        fact.rawFactMessage should be (Resources.rawCommaAnd)
        fact.rawSimplifiedFactMessage should be (Resources.rawCommaAnd)
        fact.rawMidSentenceFactMessage should be (Resources.rawCommaAnd)
        fact.rawMidSentenceSimplifiedFactMessage should be (Resources.rawCommaAnd)
        fact.factMessage should be (Resources.commaAnd(Resources.wasNotGreaterThan('a'.pretty, 'b'.pretty), Resources.wasNotLessThan('a'.pretty, 'd'.pretty)))
        fact.simplifiedFactMessage should be (Resources.commaAnd(Resources.wasNotGreaterThan('a'.pretty, 'b'.pretty), Resources.wasLessThan('a'.pretty, 'd'.pretty)))
        fact.midSentenceFactMessage should be (Resources.commaAnd(Resources.wasNotGreaterThan('a'.pretty, 'b'.pretty), Resources.wasNotLessThan('a'.pretty, 'd'.pretty)))
        fact.midSentenceSimplifiedFactMessage should be (Resources.commaAnd(Resources.wasNotGreaterThan('a'.pretty, 'b'.pretty), Resources.wasLessThan('a'.pretty, 'd'.pretty)))
        fact.factMessageArgs should be (Vector(FailureMessage(leftSideNo), MidSentenceFailureMessage(rightSideYes)))
        fact.simplifiedFactMessageArgs should be (Vector(FailureMessage(leftSideNo), MidSentenceSimplifiedFailureMessage(rightSideYes)))
        fact.midSentenceFactMessageArgs should be (Vector(MidSentenceFailureMessage(leftSideNo), MidSentenceFailureMessage(rightSideYes)))
        fact.midSentenceSimplifiedFactMessageArgs should be (Vector(MidSentenceFailureMessage(leftSideNo), MidSentenceSimplifiedFailureMessage(rightSideYes)))
        fact.composite should be (true)
      }

      "for Yes || No" in {
        val leftSideYes = Yes(Resources.rawWasNotGreaterThan, Resources.rawWasGreaterThan, Resources.rawWasNotGreaterThan, Resources.rawWasGreaterThan, Vector('c', 'b'),Vector('c', 'b'),Vector('c', 'b'),Vector('c', 'b'))
        val rightSideNo = No(Resources.rawWasNotGreaterThan, Resources.rawWasGreaterThan, Resources.rawWasNotGreaterThan, Resources.rawWasGreaterThan, Vector('c', 'd'),Vector('c', 'd'),Vector('c', 'd'),Vector('c', 'd'))
        val fact = leftSideYes || rightSideNo
        fact shouldBe a [Yes]
        fact.rawFactMessage should be (Resources.rawWasNotGreaterThan)
        fact.rawSimplifiedFactMessage should be (Resources.rawWasGreaterThan)
        fact.rawMidSentenceFactMessage should be (Resources.rawWasNotGreaterThan)
        fact.rawMidSentenceSimplifiedFactMessage should be (Resources.rawWasGreaterThan)
        fact.factMessage should be (Resources.wasNotGreaterThan('c'.pretty, 'b'.pretty))
        fact.simplifiedFactMessage should be (Resources.wasGreaterThan('c'.pretty, 'b'.pretty))
        fact.midSentenceFactMessage should be (Resources.wasNotGreaterThan('c'.pretty, 'b'.pretty))
        fact.midSentenceSimplifiedFactMessage should be (Resources.wasGreaterThan('c'.pretty, 'b'.pretty))
        fact.factMessageArgs should be (Vector('c', 'b'))
        fact.simplifiedFactMessageArgs should be (Vector('c', 'b'))
        fact.composite should be (false)
      }

      "for Yes || Yes" in {
        val leftSideYes = Yes(Resources.rawWasNotGreaterThan, Resources.rawWasGreaterThan, Resources.rawWasNotGreaterThan, Resources.rawWasGreaterThan, Vector('e', 'b'),Vector('e', 'b'),Vector('e', 'b'),Vector('e', 'b'))
        val rightSideYes = Yes(Resources.rawWasNotGreaterThan, Resources.rawWasGreaterThan, Resources.rawWasNotGreaterThan, Resources.rawWasGreaterThan, Vector('e', 'd'),Vector('e', 'd'),Vector('e', 'd'),Vector('e', 'd'))
        val fact = leftSideYes || rightSideYes
        fact shouldBe a [Yes]
        fact.rawFactMessage should be (Resources.rawWasNotGreaterThan)
        fact.rawSimplifiedFactMessage should be (Resources.rawWasGreaterThan)
        fact.rawMidSentenceFactMessage should be (Resources.rawWasNotGreaterThan)
        fact.rawMidSentenceSimplifiedFactMessage should be (Resources.rawWasGreaterThan)
        fact.factMessage should be (Resources.wasNotGreaterThan('e'.pretty, 'b'.pretty))
        fact.simplifiedFactMessage should be (Resources.wasGreaterThan('e'.pretty, 'b'.pretty))
        fact.midSentenceFactMessage should be (Resources.wasNotGreaterThan('e'.pretty, 'b'.pretty))
        fact.midSentenceSimplifiedFactMessage should be (Resources.wasGreaterThan('e'.pretty, 'b'.pretty))
        fact.factMessageArgs should be (Vector('e', 'b'))
        fact.simplifiedFactMessageArgs should be (Vector('e', 'b'))
        fact.composite should be (false)
      }*/
    }

    /*"should be parenthesize composite facts" - {
      "for non-composite || composite" in {
        val leftSideNo = No(Resources.rawWasNotGreaterThan, Resources.rawWasGreaterThan, Resources.rawWasNotGreaterThan, Resources.rawWasGreaterThan, Vector('e', 'b'),Vector('e', 'b'),Vector('e', 'b'),Vector('e', 'b'), false)
        val rightSideNo = No(Resources.rawWasNotGreaterThan, Resources.rawWasGreaterThan, Resources.rawWasNotGreaterThan, Resources.rawWasGreaterThan, Vector('e', 'd'),Vector('e', 'd'),Vector('e', 'd'),Vector('e', 'd'), true)
        val fact = leftSideNo || rightSideNo
        fact.rawFactMessage should be (Resources.rawRightParensCommaAnd)
        fact.rawSimplifiedFactMessage should be (Resources.rawRightParensCommaAnd)
        fact.rawMidSentenceFactMessage should be (Resources.rawRightParensCommaAnd)
        fact.rawMidSentenceSimplifiedFactMessage should be (Resources.rawRightParensCommaAnd)
        fact.composite should be (true)
      }

      "for composite || non-composite" in {
        val leftSideNo = No(Resources.rawWasNotGreaterThan, Resources.rawWasGreaterThan, Resources.rawWasNotGreaterThan, Resources.rawWasGreaterThan, Vector('e', 'b'),Vector('e', 'b'),Vector('e', 'b'),Vector('e', 'b'), true)
        val rightSideNo = No(Resources.rawWasNotGreaterThan, Resources.rawWasGreaterThan, Resources.rawWasNotGreaterThan, Resources.rawWasGreaterThan, Vector('e', 'd'),Vector('e', 'd'),Vector('e', 'd'),Vector('e', 'd'), false)
        val fact = leftSideNo || rightSideNo
        fact.rawFactMessage should be (Resources.rawLeftParensCommaAnd)
        fact.rawSimplifiedFactMessage should be (Resources.rawLeftParensCommaAnd)
        fact.rawMidSentenceFactMessage should be (Resources.rawLeftParensCommaAnd)
        fact.rawMidSentenceSimplifiedFactMessage should be (Resources.rawLeftParensCommaAnd)
        fact.composite should be (true)
      }

      "for composite || composite" in {
        val leftSideNo = No(Resources.rawWasNotGreaterThan, Resources.rawWasGreaterThan, Resources.rawWasNotGreaterThan, Resources.rawWasGreaterThan, Vector('e', 'b'),Vector('e', 'b'),Vector('e', 'b'),Vector('e', 'b'), true)
        val rightSideNo = No(Resources.rawWasNotGreaterThan, Resources.rawWasGreaterThan, Resources.rawWasNotGreaterThan, Resources.rawWasGreaterThan, Vector('e', 'd'),Vector('e', 'd'),Vector('e', 'd'),Vector('e', 'd'), true)
        val fact = leftSideNo || rightSideNo
        fact.rawFactMessage should be (Resources.rawBothParensCommaAnd)
        fact.rawSimplifiedFactMessage should be (Resources.rawBothParensCommaAnd)
        fact.rawMidSentenceFactMessage should be (Resources.rawBothParensCommaAnd)
        fact.rawMidSentenceSimplifiedFactMessage should be (Resources.rawBothParensCommaAnd)
        fact.composite should be (true)
      }
    }*/
  }
*/
  /*"The Yes and No companion objects factory methods" - {
    "that takes two strings should work correctly" in {
      val fact = Yes("one", "two")
      fact should have (
        factMessage ("one"),
        simplifiedFactMessage ("two"),
        midSentenceFactMessage ("one"),
        midSentenceSimplifiedFactMessage ("two"),
        rawFactMessage ("one"),
        rawSimplifiedFactMessage ("two"),
        rawMidSentenceFactMessage ("one"),
        rawMidSentenceSimplifiedFactMessage ("two"),
        factMessageArgs(Vector.empty),
        simplifiedFactMessageArgs(Vector.empty),
        midSentenceFactMessageArgs(Vector.empty),
        midSentenceSimplifiedFactMessageArgs(Vector.empty),
        composite(false)
      )
      val ms = No("aaa", "bbb")
      ms should have (
        factMessage ("aaa"),
        simplifiedFactMessage ("bbb"),
        midSentenceFactMessage ("aaa"),
        midSentenceSimplifiedFactMessage ("bbb"),
        rawFactMessage ("aaa"),
        rawSimplifiedFactMessage ("bbb"),
        rawMidSentenceFactMessage ("aaa"),
        rawMidSentenceSimplifiedFactMessage ("bbb"),
        factMessageArgs(Vector.empty),
        simplifiedFactMessageArgs(Vector.empty),
        midSentenceFactMessageArgs(Vector.empty),
        midSentenceSimplifiedFactMessageArgs(Vector.empty),
        composite(false)
      )
    }
    "that takes four strings should work correctly" in {
      val fact = Yes("one", "two", "three", "four")
      fact should have (
        factMessage ("one"),
        simplifiedFactMessage ("two"),
        midSentenceFactMessage ("three"),
        midSentenceSimplifiedFactMessage ("four"),
        rawFactMessage ("one"),
        rawSimplifiedFactMessage ("two"),
        rawMidSentenceFactMessage ("three"),
        rawMidSentenceSimplifiedFactMessage ("four"),
        factMessageArgs(Vector.empty),
        simplifiedFactMessageArgs(Vector.empty),
        midSentenceFactMessageArgs(Vector.empty),
        midSentenceSimplifiedFactMessageArgs(Vector.empty),
        composite(false)
      )
      val ms = No("aaa", "bbb", "ccc", "ddd")
      ms should have (
        factMessage ("aaa"),
        simplifiedFactMessage ("bbb"),
        midSentenceFactMessage ("ccc"),
        midSentenceSimplifiedFactMessage ("ddd"),
        rawFactMessage ("aaa"),
        rawSimplifiedFactMessage ("bbb"),
        rawMidSentenceFactMessage ("ccc"),
        rawMidSentenceSimplifiedFactMessage ("ddd"),
        factMessageArgs(Vector.empty),
        simplifiedFactMessageArgs(Vector.empty),
        midSentenceFactMessageArgs(Vector.empty),
        midSentenceSimplifiedFactMessageArgs(Vector.empty),
        composite(false)
      )
    }
    "that takes four strings and two IndexedSeqs should work correctly" in {
      val fact = Yes("one", "two", "three", "four", Vector(42), Vector(42.0))
      fact should have (
        factMessage ("one"),
        simplifiedFactMessage ("two"),
        midSentenceFactMessage ("three"),
        midSentenceSimplifiedFactMessage ("four"),
        rawFactMessage ("one"),
        rawSimplifiedFactMessage ("two"),
        rawMidSentenceFactMessage ("three"),
        rawMidSentenceSimplifiedFactMessage ("four"),
        factMessageArgs(Vector(42)),
        simplifiedFactMessageArgs(Vector(42.0)),
        midSentenceFactMessageArgs(Vector(42)),
        midSentenceSimplifiedFactMessageArgs(Vector(42.0)),
        composite(false)
      )
      val ms = No("aaa", "bbb", "ccc", "ddd", Vector("ho", "he"), Vector("foo", "fie"))
      ms should have (
        factMessage ("aaa"),
        simplifiedFactMessage ("bbb"),
        midSentenceFactMessage ("ccc"),
        midSentenceSimplifiedFactMessage ("ddd"),
        rawFactMessage ("aaa"),
        rawSimplifiedFactMessage ("bbb"),
        rawMidSentenceFactMessage ("ccc"),
        rawMidSentenceSimplifiedFactMessage ("ddd"),
        factMessageArgs(Vector("ho", "he")),
        simplifiedFactMessageArgs(Vector("foo", "fie")),
        midSentenceFactMessageArgs(Vector("ho", "he")),
        midSentenceSimplifiedFactMessageArgs(Vector("foo", "fie")),
        composite(false)
      )
    }
    "that takes two strings and one IndexedSeq should work correctly" in {
      val fact = Yes("one", "two", Vector(42))
      fact should have (
        factMessage ("one"),
        simplifiedFactMessage ("two"),
        midSentenceFactMessage ("one"),
        midSentenceSimplifiedFactMessage ("two"),
        rawFactMessage ("one"),
        rawSimplifiedFactMessage ("two"),
        rawMidSentenceFactMessage ("one"),
        rawMidSentenceSimplifiedFactMessage ("two"),
        factMessageArgs(Vector(42)),
        simplifiedFactMessageArgs(Vector(42)),
        midSentenceFactMessageArgs(Vector(42)),
        midSentenceSimplifiedFactMessageArgs(Vector(42)),
        composite(false)
      )
      val ms = No("aaa", "bbb", Vector("ho", "he"))
      ms should have (
        factMessage ("aaa"),
        simplifiedFactMessage ("bbb"),
        midSentenceFactMessage ("aaa"),
        midSentenceSimplifiedFactMessage ("bbb"),
        rawFactMessage ("aaa"),
        rawSimplifiedFactMessage ("bbb"),
        rawMidSentenceFactMessage ("aaa"),
        rawMidSentenceSimplifiedFactMessage ("bbb"),
        factMessageArgs(Vector("ho", "he")),
        simplifiedFactMessageArgs(Vector("ho", "he")),
        midSentenceFactMessageArgs(Vector("ho", "he")),
        midSentenceSimplifiedFactMessageArgs(Vector("ho", "he")),
        composite(false)
      )
    }
    "that takes two strings and two IndexedSeqs should work correctly" in {
      val fact = Yes("one", "two", Vector(42), Vector(42.0))
      fact should have (
        factMessage ("one"),
        simplifiedFactMessage ("two"),
        midSentenceFactMessage ("one"),
        midSentenceSimplifiedFactMessage ("two"),
        rawFactMessage ("one"),
        rawSimplifiedFactMessage ("two"),
        rawMidSentenceFactMessage ("one"),
        rawMidSentenceSimplifiedFactMessage ("two"),
        factMessageArgs(Vector(42)),
        simplifiedFactMessageArgs(Vector(42.0)),
        midSentenceFactMessageArgs(Vector(42)),
        midSentenceSimplifiedFactMessageArgs(Vector(42.0)),
        composite(false)
      )
      val ms = No("aaa", "bbb", Vector("ho", "he"), Vector("foo", "fie"))
      ms should have (
        factMessage ("aaa"),
        simplifiedFactMessage ("bbb"),
        midSentenceFactMessage ("aaa"),
        midSentenceSimplifiedFactMessage ("bbb"),
        rawFactMessage ("aaa"),
        rawSimplifiedFactMessage ("bbb"),
        rawMidSentenceFactMessage ("aaa"),
        rawMidSentenceSimplifiedFactMessage ("bbb"),
        factMessageArgs(Vector("ho", "he")),
        simplifiedFactMessageArgs(Vector("foo", "fie")),
        midSentenceFactMessageArgs(Vector("ho", "he")),
        midSentenceSimplifiedFactMessageArgs(Vector("foo", "fie")),
        composite(false)
      )
    }
    "that takes four strings and four IndexedSeqs should work correctly" in {
      val fact = Yes("one", "two", "three", "four", Vector(1), Vector(2), Vector(3), Vector(4))
      fact should have (
        factMessage ("one"),
        simplifiedFactMessage ("two"),
        midSentenceFactMessage ("three"),
        midSentenceSimplifiedFactMessage ("four"),
        rawFactMessage ("one"),
        rawSimplifiedFactMessage ("two"),
        rawMidSentenceFactMessage ("three"),
        rawMidSentenceSimplifiedFactMessage ("four"),
        factMessageArgs(Vector(1)),
        simplifiedFactMessageArgs(Vector(2)),
        midSentenceFactMessageArgs(Vector(3)),
        midSentenceSimplifiedFactMessageArgs(Vector(4)),
        composite(false)
      )
      val ms = No("aaa", "bbb", "ccc", "ddd", Vector('A'), Vector('B'), Vector('C'), Vector('D'))
      ms should have (
        factMessage ("aaa"),
        simplifiedFactMessage ("bbb"),
        midSentenceFactMessage ("ccc"),
        midSentenceSimplifiedFactMessage ("ddd"),
        rawFactMessage ("aaa"),
        rawSimplifiedFactMessage ("bbb"),
        rawMidSentenceFactMessage ("ccc"),
        rawMidSentenceSimplifiedFactMessage ("ddd"),
        factMessageArgs(Vector('A')),
        simplifiedFactMessageArgs(Vector('B')),
        midSentenceFactMessageArgs(Vector('C')),
        midSentenceSimplifiedFactMessageArgs(Vector('D')),
        composite(false)
      )
    }
    "that takes four strings, four IndexedSeqs and composite should work correctly" in {
      val fact = Yes("one", "two", "three", "four", Vector(1), Vector(2), Vector(3), Vector(4), true)
          fact should have (
              factMessage ("one"),
              simplifiedFactMessage ("two"),
              midSentenceFactMessage ("three"),
              midSentenceSimplifiedFactMessage ("four"),
              rawFactMessage ("one"),
              rawSimplifiedFactMessage ("two"),
              rawMidSentenceFactMessage ("three"),
              rawMidSentenceSimplifiedFactMessage ("four"),
              factMessageArgs(Vector(1)),
              simplifiedFactMessageArgs(Vector(2)),
              midSentenceFactMessageArgs(Vector(3)),
              midSentenceSimplifiedFactMessageArgs(Vector(4)),
              composite(true)
              )
      val ms = No("aaa", "bbb", "ccc", "ddd", Vector('A'), Vector('B'), Vector('C'), Vector('D'), true)
      ms should have (
          factMessage ("aaa"),
          simplifiedFactMessage ("bbb"),
          midSentenceFactMessage ("ccc"),
          midSentenceSimplifiedFactMessage ("ddd"),
          rawFactMessage ("aaa"),
          rawSimplifiedFactMessage ("bbb"),
          rawMidSentenceFactMessage ("ccc"),
          rawMidSentenceSimplifiedFactMessage ("ddd"),
          factMessageArgs(Vector('A')),
          simplifiedFactMessageArgs(Vector('B')),
          midSentenceFactMessageArgs(Vector('C')),
          midSentenceSimplifiedFactMessageArgs(Vector('D')),
          composite(true)
          )
    }
  }*/

  def examples: TableFor1[Expectation] =
    Table(
      "fact",
      No("message", "simplified message"),
      Yes("message", "simplified message"),
      !(No("message", "simplified message")),
      !(Yes("message", "simplified message")),
      No("message", "simplified message") && Yes("message", "simplified message"),
      Yes("message", "simplified message") && Yes("message", "simplified message"),
      Yes("message", "simplified message") && No("message", "simplified message"),
      Yes("message", "simplified message") || No("message", "simplified message"),
      No("message", "simplified message") || Yes("message", "simplified message"),
      No("message", "simplified message") || No("message", "simplified message")
    )
}