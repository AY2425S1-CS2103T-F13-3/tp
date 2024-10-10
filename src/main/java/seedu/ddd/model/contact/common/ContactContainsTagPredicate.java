package seedu.ddd.model.contact.common;

import java.util.Set;
import java.util.function.Predicate;

import seedu.ddd.commons.util.ToStringBuilder;
import seedu.ddd.model.tag.Tag;

/**
 * Tests that any of a {@code Contact}'s {@code Tags} matches the given tag .
 */
public class ContactContainsTagPredicate implements Predicate<Contact> {
    private final Set<Tag> tagSet;

    public ContactContainsTagPredicate(Set<Tag> tagSet) {
        this.tagSet = tagSet;
    }

    @Override
    public boolean test(Contact contact) {
        return tagSet.stream()
                .anyMatch(tag -> contact.getTags().contains(tag));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ContactContainsTagPredicate)) {
            return false;
        }

        ContactContainsTagPredicate otherContactContainsTagPredicate = (ContactContainsTagPredicate) other;
        return tagSet.equals(otherContactContainsTagPredicate.tagSet);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("tag set", tagSet).toString();
    }
}
