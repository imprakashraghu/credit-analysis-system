import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataValidation {

    // Task 1: Email Validation
    public static boolean validateEmails(String email) {
        // Regex for validating email addresses
        String emailRegex = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";
        Pattern emailPattern = Pattern.compile(emailRegex);
        // Lists to store valid and invalid emails
        boolean isValid = false;

        if (email.isEmpty()) {
            System.out.println("Email cannot be empty");
            return isValid;
        }

        Matcher matcher = emailPattern.matcher(email);
        if (matcher.matches()) {
            isValid = true;
        }

        return isValid;
    }

    // Phone Number Validation
    public static boolean validatePhoneNumbers(String phoneNumber) {
        String phoneRegex = "(?:\\+\\d{1,3}\\s*)?(?:\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4})";
        Pattern phonePattern = Pattern.compile(phoneRegex);
        boolean isValid = false;

        if (phoneNumber.isEmpty()) {
            System.out.println("Phone number cannot be empty");
            return isValid;
        }

        Matcher matcher = phonePattern.matcher(phoneNumber);
        if (matcher.matches()) {
            isValid = true;
        }

        return isValid;
    }
}
