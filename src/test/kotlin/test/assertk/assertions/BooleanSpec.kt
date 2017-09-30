package test.assertk.assertions

import assertk.assert
import assertk.assertions.*
import org.assertj.core.api.Assertions
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

class BooleanSpec : Spek({
    given("a Boolean") {
        on("isTrue()") {
            it("Given a true statement, test should pass") {
                assert(true).isTrue()
            }

            it("Given a false statement, test should fail") {
                Assertions.assertThatThrownBy {
                    assert(false).isTrue()
                }.hasMessage("expected to be true")
            }
        }

        on("isFalse()") {

            it("Given a false statement, test should pass") {
                assert(false).isFalse()
            }

            it("Given a true statement, test should fail") {
                Assertions.assertThatThrownBy {
                    assert(true).isFalse()
                }.hasMessage("expected to be false")
            }
        }
    }
})