package seedu.ddd.testutil;

import static seedu.ddd.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.ddd.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.ddd.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.ddd.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.ddd.logic.parser.CliSyntax.PREFIX_SERVICE;
import static seedu.ddd.logic.parser.CliSyntax.PREFIX_TAG;

import seedu.ddd.logic.commands.AddCommand;
import seedu.ddd.model.contact.vendor.Vendor;

/**
 * A utility class for Vendor.
 */
public class VendorUtil {

    /**
     * Returns an add command string for adding the {@code contact}.
     */
    public static String getAddContactCommand(Vendor vendor) {
        return AddCommand.COMMAND_WORD + " vendor " + getVendorDetails(vendor);
    }

    /**
     * Returns the part of command string for the given {@code contact}'s details.
     */
    public static String getVendorDetails(Vendor vendor) {
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX_NAME + vendor.getName().fullName + " ");
        sb.append(PREFIX_PHONE + vendor.getPhone().value + " ");
        sb.append(PREFIX_EMAIL + vendor.getEmail().value + " ");
        sb.append(PREFIX_ADDRESS + vendor.getAddress().value + " ");
        sb.append(PREFIX_SERVICE + vendor.getService().value + " ");
        vendor.getTags().stream().forEach(
                s -> sb.append(PREFIX_TAG + s.tagName + " ")
        );
        return sb.toString();
    }

}
