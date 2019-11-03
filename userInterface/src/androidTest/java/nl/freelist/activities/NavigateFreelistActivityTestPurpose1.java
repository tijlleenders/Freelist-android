package nl.freelist.activities;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import nl.freelist.freelist.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class NavigateFreelistActivityTestPurpose1 {

  @Rule
  public ActivityTestRule<NavigateFreelistActivity> mActivityTestRule = new ActivityTestRule<>(
      NavigateFreelistActivity.class);

  @Test
  public void navigateFreelistActivityTestPurpose1() {
    ViewInteraction floatingActionButton = onView(
        allOf(withId(R.id.button_add_entry),
            childAtPosition(
                childAtPosition(
                    withId(android.R.id.content),
                    0),
                1),
            isDisplayed()));
    floatingActionButton.perform(click());

    ViewInteraction appCompatEditText = onView(
        allOf(withId(R.id.edit_text_title),
            childAtPosition(
                childAtPosition(
                    withId(android.R.id.content),
                    0),
                1),
            isDisplayed()));
    appCompatEditText.perform(replaceText("Purpose 1"), closeSoftKeyboard());

    ViewInteraction appCompatEditText2 = onView(
        allOf(withId(R.id.edit_text_title), withText("Purpose 1"),
            childAtPosition(
                childAtPosition(
                    withId(android.R.id.content),
                    0),
                1),
            isDisplayed()));
    appCompatEditText2.perform(pressImeActionButton());

    ViewInteraction appCompatImageButton = onView(
        allOf(withContentDescription("Navigate up"),
            childAtPosition(
                allOf(withId(R.id.action_bar),
                    childAtPosition(
                        withId(R.id.action_bar_container),
                        0)),
                1),
            isDisplayed()));
    appCompatImageButton.perform(click());

    ViewInteraction textView = onView(
        allOf(withId(R.id.text_view_title), withText("Purpose 1"),
            childAtPosition(
                childAtPosition(
                    IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class),
                    0),
                0),
            isDisplayed()));
    textView.check(matches(withText("Purpose 1")));
  }

  private static Matcher<View> childAtPosition(
      final Matcher<View> parentMatcher, final int position) {

    return new TypeSafeMatcher<View>() {
      @Override
      public void describeTo(Description description) {
        description.appendText("Child at position " + position + " in parent ");
        parentMatcher.describeTo(description);
      }

      @Override
      public boolean matchesSafely(View view) {
        ViewParent parent = view.getParent();
        return parent instanceof ViewGroup && parentMatcher.matches(parent)
            && view.equals(((ViewGroup) parent).getChildAt(position));
      }
    };
  }
}
