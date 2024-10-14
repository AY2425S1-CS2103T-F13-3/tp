package seedu.ddd.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.ddd.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.ddd.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.ddd.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.ddd.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.ddd.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.ddd.logic.parser.CliSyntax.PREFIX_SERVICE;
import static seedu.ddd.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.ddd.commons.core.index.Index;
import seedu.ddd.commons.util.CollectionUtil;
import seedu.ddd.commons.util.ToStringBuilder;
import seedu.ddd.logic.Messages;
import seedu.ddd.logic.commands.exceptions.CommandException;
import seedu.ddd.model.Model;
import seedu.ddd.model.contact.client.Client;
import seedu.ddd.model.contact.client.Date;
import seedu.ddd.model.contact.common.Address;
import seedu.ddd.model.contact.common.Contact;
import seedu.ddd.model.contact.common.Email;
import seedu.ddd.model.contact.common.Id;
import seedu.ddd.model.contact.common.Name;
import seedu.ddd.model.contact.common.Phone;
import seedu.ddd.model.contact.vendor.Service;
import seedu.ddd.model.contact.vendor.Vendor;
import seedu.ddd.model.tag.Tag;

/**
 * Edits the details of an existing contact in the address book.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the contact identified "
            + "by the index number used in the displayed contact list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_ADDRESS + "ADDRESS] "
            + "[" + PREFIX_DATE + "DATE (only for clients)] "
            + "[" + PREFIX_SERVICE + "SERVICE (only for vendors)] "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_PHONE + "91234567 "
            + PREFIX_EMAIL + "johndoe@example.com";

    public static final String MESSAGE_EDIT_CONTACT_SUCCESS = "Edited Contact: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_CONTACT = "This contact already exists in the address book.";
    public static final String MESSAGE_EDIT_INVALID_PARAMETER = "%1$s is not an editable parameter for this contact.";

    private final Index index;
    private final EditContactDescriptor editContactDescriptor;

    /**
     * @param index of the contact in the filtered contact list to edit
     * @param editContactDescriptor details to edit the contact with
     */
    public EditCommand(Index index, EditContactDescriptor editContactDescriptor) {
        requireNonNull(index);
        requireNonNull(editContactDescriptor);

        this.index = index;
        this.editContactDescriptor = editContactDescriptor.copy();
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Contact> lastShownList = model.getFilteredContactList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX);
        }

        Contact contactToEdit = lastShownList.get(index.getZeroBased());
        if (contactToEdit instanceof Client && editContactDescriptor instanceof EditVendorDescriptor) {
            throw new CommandException(String.format(MESSAGE_EDIT_INVALID_PARAMETER, PREFIX_SERVICE));
        }
        if (contactToEdit instanceof Vendor && editContactDescriptor instanceof EditClientDescriptor) {
            throw new CommandException(String.format(MESSAGE_EDIT_INVALID_PARAMETER, PREFIX_DATE));
        }
        Contact editedContact = createEditedContact(contactToEdit, editContactDescriptor);

        if (!contactToEdit.isSameContact(contactToEdit) && model.hasContact(editedContact)) {
            throw new CommandException(MESSAGE_DUPLICATE_CONTACT);
        }

        model.setContact(contactToEdit, editedContact);
        model.updateFilteredContactList(Model.PREDICATE_SHOW_ALL_CONTACTS);
        return new CommandResult(String.format(MESSAGE_EDIT_CONTACT_SUCCESS, Messages.format(editedContact)));
    }

    /**
     * Creates and returns a {@code Contact} with the details of {@code contactToEdit}
     * edited with {@code editContactDescriptor}.
     */
    private static Contact createEditedContact(Contact contactToEdit, EditContactDescriptor editContactDescriptor) {
        assert contactToEdit != null;
        assert contactToEdit instanceof Client || contactToEdit instanceof Vendor;

        return contactToEdit instanceof Client
            ? createEditedContact((Client) contactToEdit, editContactDescriptor)
            : createEditedContact((Vendor) contactToEdit, editContactDescriptor);
    }

    /**
     * Creates and returns a {@code Client} with the details of {@code contactToEdit}
     * edited with {@code editContactDescriptor}.
     */
    private static Client createEditedContact(Client contactToEdit, EditContactDescriptor editContactDescriptor) {
        assert contactToEdit != null;

        Name updatedName = editContactDescriptor.getName().orElse(contactToEdit.getName());
        Phone updatedPhone = editContactDescriptor.getPhone().orElse(contactToEdit.getPhone());
        Email updatedEmail = editContactDescriptor.getEmail().orElse(contactToEdit.getEmail());
        Address updatedAddress = editContactDescriptor.getAddress().orElse(contactToEdit.getAddress());
        Date updatedDate = editContactDescriptor instanceof EditClientDescriptor
            ? ((EditClientDescriptor) editContactDescriptor).getDate().orElse(contactToEdit.getDate())
            : contactToEdit.getDate();
        Set<Tag> updatedTags = editContactDescriptor.getTags().orElse(contactToEdit.getTags());
        Id updatedId = editContactDescriptor.getId().orElse(contactToEdit.getId());

        return new Client(updatedName, updatedPhone, updatedEmail, updatedAddress, updatedDate, updatedTags, updatedId);
    }

    /**
     * Creates and returns a {@code Vendor} with the details of {@code contactToEdit}
     * edited with {@code editContactDescriptor}.
     */
    private static Vendor createEditedContact(Vendor contactToEdit, EditContactDescriptor editContactDescriptor) {
        assert contactToEdit != null;

        Name updatedName = editContactDescriptor.getName().orElse(contactToEdit.getName());
        Phone updatedPhone = editContactDescriptor.getPhone().orElse(contactToEdit.getPhone());
        Email updatedEmail = editContactDescriptor.getEmail().orElse(contactToEdit.getEmail());
        Address updatedAddress = editContactDescriptor.getAddress().orElse(contactToEdit.getAddress());
        Service updatedService = editContactDescriptor instanceof EditVendorDescriptor
            ? ((EditVendorDescriptor) editContactDescriptor).getService().orElse(contactToEdit.getService())
            : contactToEdit.getService();
        Set<Tag> updatedTags = editContactDescriptor.getTags().orElse(contactToEdit.getTags());
        Id updatedId = editContactDescriptor.getId().orElse(contactToEdit.getId());

        return new Vendor(updatedName, updatedPhone, updatedEmail, updatedAddress, updatedService, updatedTags, updatedId);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand)) {
            return false;
        }

        EditCommand otherEditCommand = (EditCommand) other;
        return index.equals(otherEditCommand.index)
                && editContactDescriptor.equals(otherEditCommand.editContactDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("editContactDescriptor", editContactDescriptor)
                .toString();
    }

    /**
     * Stores the details of the contact to edit. Each non-empty field value will replace the
     * corresponding field value of the contact. This class contains fields that are common
     * to all types of contacts.
     */
    public static class EditContactDescriptor {
        protected Name name;
        protected Phone phone;
        protected Email email;
        protected Address address;
        protected Set<Tag> tags;
        protected Id id;

        public EditContactDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditContactDescriptor(EditContactDescriptor toCopy) {
            setName(toCopy.name);
            setPhone(toCopy.phone);
            setEmail(toCopy.email);
            setAddress(toCopy.address);
            setTags(toCopy.tags);
            setId(toCopy.id);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, phone, email, address, tags);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }

        public Optional<Phone> getPhone() {
            return Optional.ofNullable(phone);
        }

        public void setEmail(Email email) {
            this.email = email;
        }

        public Optional<Email> getEmail() {
            return Optional.ofNullable(email);
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public Optional<Address> getAddress() {
            return Optional.ofNullable(address);
        }

        /**
         * Sets {@code tags} to this object's {@code tags}.
         * A defensive copy of {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null) ? new HashSet<>(tags) : null;
        }

        /**
         * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Tag>> getTags() {
            return (tags != null) ? Optional.of(Collections.unmodifiableSet(tags)) : Optional.empty();
        }

        public void setId(Id id) {
            this.id = id;
        }

        public Optional<Id> getId() {
            return Optional.ofNullable(id);
        }

        /**
         * Creates a copy of this {@code EditContactDescriptor}.
         */
        public EditContactDescriptor copy() {
            return new EditContactDescriptor(this);
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditContactDescriptor)) {
                return false;
            }

            EditContactDescriptor otherEditContactDescriptor = (EditContactDescriptor) other;
            return Objects.equals(name, otherEditContactDescriptor.name)
                    && Objects.equals(phone, otherEditContactDescriptor.phone)
                    && Objects.equals(email, otherEditContactDescriptor.email)
                    && Objects.equals(address, otherEditContactDescriptor.address)
                    && Objects.equals(tags, otherEditContactDescriptor.tags)
                    && Objects.equals(id, otherEditContactDescriptor.id);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("name", name)
                    .add("phone", phone)
                    .add("email", email)
                    .add("address", address)
                    .add("tags", tags)
                    .toString();
        }
    }

    /**
     * Stores the details of the client to edit. This class extends {@code EditContactDescriptor}
     * and also contains fields exclusive to {@code Client} (i.e. {@code Client.date}).
     */
    public static class EditClientDescriptor extends EditContactDescriptor {
        private Date date;

        public EditClientDescriptor() {
            super();
        }

        public EditClientDescriptor(EditContactDescriptor toCopy) {
            super(toCopy);
        }

        /**
         * Returns true if at least one field is edited.
         */
        @Override
        public boolean isAnyFieldEdited() {
            return super.isAnyFieldEdited() || date != null;
        }

        public final void setDate(Date date) {
            this.date = date;
        }

        public Optional<Date> getDate() {
            return Optional.ofNullable(date);
        }

        @Override
        public EditContactDescriptor copy() {
            EditClientDescriptor copied = new EditClientDescriptor(this);
            copied.setDate(date);
            return copied;
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditClientDescriptor)) {
                return false;
            }

            EditClientDescriptor otherEditClientDescriptor = (EditClientDescriptor) other;
            return Objects.equals(name, otherEditClientDescriptor.name)
                    && Objects.equals(phone, otherEditClientDescriptor.phone)
                    && Objects.equals(email, otherEditClientDescriptor.email)
                    && Objects.equals(date, otherEditClientDescriptor.date)
                    && Objects.equals(address, otherEditClientDescriptor.address)
                    && Objects.equals(tags, otherEditClientDescriptor.tags);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("name", name)
                    .add("phone", phone)
                    .add("email", email)
                    .add("address", address)
                    .add("date", date)
                    .add("tags", tags)
                    .toString();
        }
    }

    /**
     * Stores the details of the vendor to edit. This class extends {@code EditContactDescriptor}
     * and also contains fields exclusive to {@code Vendor} (i.e. {@code Vendor.service}).
     */
    public static class EditVendorDescriptor extends EditContactDescriptor {
        private Service service;

        public EditVendorDescriptor() {
            super();
        }

        public EditVendorDescriptor(EditContactDescriptor toCopy) {
            super(toCopy);
        }

        /**
         * Returns true if at least one field is edited.
         */
        @Override
        public boolean isAnyFieldEdited() {
            return super.isAnyFieldEdited() || service != null;
        }

        public final void setService(Service service) {
            this.service = service;
        }

        public Optional<Service> getService() {
            return Optional.ofNullable(service);
        }

        @Override
        public EditContactDescriptor copy() {
            EditVendorDescriptor copied = new EditVendorDescriptor(this);
            copied.setService(service);
            return copied;
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditVendorDescriptor)) {
                return false;
            }

            EditVendorDescriptor otherEditVendorDescriptor = (EditVendorDescriptor) other;
            return Objects.equals(name, otherEditVendorDescriptor.name)
                    && Objects.equals(phone, otherEditVendorDescriptor.phone)
                    && Objects.equals(email, otherEditVendorDescriptor.email)
                    && Objects.equals(service, otherEditVendorDescriptor.service)
                    && Objects.equals(address, otherEditVendorDescriptor.address)
                    && Objects.equals(tags, otherEditVendorDescriptor.tags);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("name", name)
                    .add("phone", phone)
                    .add("email", email)
                    .add("address", address)
                    .add("service", service)
                    .add("tags", tags)
                    .toString();
        }
    }
}
