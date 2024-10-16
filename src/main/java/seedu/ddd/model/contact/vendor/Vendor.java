package seedu.ddd.model.contact.vendor;

import java.util.Objects;
import java.util.Set;

import seedu.ddd.commons.util.CollectionUtil;
import seedu.ddd.commons.util.ToStringBuilder;
import seedu.ddd.model.contact.common.Address;
import seedu.ddd.model.contact.common.Contact;
import seedu.ddd.model.contact.common.Email;
import seedu.ddd.model.contact.common.Id;
import seedu.ddd.model.contact.common.Name;
import seedu.ddd.model.contact.common.Phone;
import seedu.ddd.model.tag.Tag;

/**
 * Represents a Vendor in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Vendor extends Contact {
    private Service service;
    /**
     * Constructs a {@code Vendor}.
     *
     * @param name    A valid name.
     * @param phone   A valid phone number.
     * @param email   A valid email address.
     * @param address A valid address.
     * @param service A valid service.
     * @param tags    A set of tags associated with the client.
     * @param id      A valid id.
     */
    public Vendor(Name name, Phone phone, Email email, Address address, Service service, Set<Tag> tags, Id id) {
        super(name, phone, email, address, tags, id);
        CollectionUtil.requireAllNonNull(service);
        this.service = service;
    }

    public Service getService() {
        return service;
    }

    /**
     * Returns true if both persons have the same name.
     * This defines a weaker notion of equality between two persons.
     */
    @Override
    public boolean isSameContact(Contact otherContact) {
        if (otherContact == this) {
            return true;
        }

        if (!(otherContact instanceof Vendor)) {
            return false;
        }

        return super.isSameContact(otherContact);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Vendor)) {
            return false;
        }

        Vendor otherVendor = (Vendor) other;
        return super.equals(otherVendor) && service.equals(otherVendor.service);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(this.getName(), this.getPhone(), this.getEmail(),
                this.getAddress(), this.service, this.getTags());
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("id", this.getId())
                .add("name", this.getName())
                .add("phone", this.getPhone())
                .add("email", this.getEmail())
                .add("address", this.getAddress())
                .add("service", this.service)
                .add("tags", this.getTags())
                .add("id", this.getId())
                .toString();
    }

}
