package ru.spbstu.shelepov.nim

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.Test
import ru.spbstu.shelepov.nim.ui.MenuFragment

class UITest {

    @get: Rule
    var mActivityRule: ActivityTestRule<MainActivity> = IntentsTestRule(
        MainActivity::class.java,false,true
    )

    @Test
    fun afterCreating() {
        onView(withId(R.id.computer_game_button))
            .check(matches(isEnabled()))
            .check(matches(isClickable()))

        onView(withId(R.id.score_button))
            .check(matches(isEnabled()))
            .check(matches(isFocusable()))

        onView(withId(R.id.hotseat_game_button))
            .check(matches(isEnabled()))
            .check(matches(isFocusable()))
    }

    @Test
    fun test1() {
        onView(withId(R.id.score_button)).perform(click())
        onView(withId(R.id.score_recycler))
            .check(matches(isDisplayed()))
    }

    @Test
    fun test2() {
        onView(withId(R.id.item_rules)).perform(click())
        onView(withId(R.id.rules_text))
            .check(matches(isDisplayed()))
    }
}