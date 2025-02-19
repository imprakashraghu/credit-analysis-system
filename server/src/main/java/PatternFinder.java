import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternFinder {

    // Regular expression to find URLs
    private static final
    String urlRegexForA4HP = "https?://[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}(/[^\\\\s]*)?";
    private static final Pattern patternForHPA4 = Pattern.compile(urlRegexForA4HP, Pattern.CASE_INSENSITIVE);

    // Task6: Taking Email Addresses Out of Text
    public static List<String> extractEmails(String text) {
        // Create a list at the beginning to hold the retrieved email addresses.
        List<String> emails = new ArrayList<>();
        if (text == null) {
            System.out.println("Text input cannot be empty");
            return emails;
        }
        // Establish the regular expression for email addresses.
        String emailRegex = "[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}";
        // Construct a Pattern object by assembling the regular expression.
        Pattern emailPattern = Pattern.compile(emailRegex);
        // Construct a Matcher object and use the constructed pattern to search the text.
        Matcher matcher = emailPattern.matcher(text);

        // Extract email addresses from the text and gather them.
        while (matcher.find()) {
            // Compile a list for every matching email address.
            emails.add(matcher.group());
        }

        return emails;
    }

    /**
     * Used to extract url(s) from the given text
     * using regular expressions
     * @param txtStrForA4
     * @return
     */
    public static List<String> extractURLsForA4(String txtStrForA4) {
        List<String> URLsForA04 = new ArrayList<>();
        if (txtStrForA4.isEmpty()) {
            System.out.println("Text input cannot be empty");
            return URLsForA04;
        }
        Matcher matcherForA04HP = patternForHPA4.matcher(txtStrForA4);

        while (matcherForA04HP.find()) {
            URLsForA04.add(matcherForA04HP.group());
        }

        return URLsForA04;
    }

    /**
     * extracting content from csv file and the performing the
     * url finding and returning a list of strings
     * @param filepathForA4
     * @return
     */
    public static List<String> extractURLsFromCSV(String filepathForA4) {
        List<String> allUrls = new ArrayList<>();
        if (filepathForA4.isEmpty()) {
            System.out.println("File Path input cannot be empty");
            return allUrls;
        }
        try (BufferedReader readerFromBuffer04 = new BufferedReader(new FileReader(filepathForA4))) {
            String lineForA04HP;
            while ((lineForA04HP = readerFromBuffer04.readLine()) != null) {
                String[] wrdsHPForA04 = lineForA04HP.split(",");
                for(String wrd: wrdsHPForA04) {
                    allUrls.addAll(extractURLsForA4(wrd));
                }
            }
        } catch (IOException eerrExcepA4) {
            System.out.println(eerrExcepA4.getMessage());
        }

        return allUrls;
    }

    // Find Dates in Text
    public static List<String> findDates(List<String> texts) {
        String dateRegex = "\\b\\d{4}-\\d{2}-\\d{2}\\b";
        Pattern datePattern = Pattern.compile(dateRegex);
        List<String> foundDates = new ArrayList<>();

        for (String text : texts) {
            Matcher matcher = datePattern.matcher(text);
            while (matcher.find()) {
                foundDates.add(matcher.group());
            }
        }

        return foundDates;
    }

    /**
     * Used to extract phone numbers from the given text
     * @param text
     * @return
     */
    public static List<String> extractPhoneNumbers(String text) {
        List<String> phoneNumbers = new ArrayList<>();
        if (text.isEmpty()) {
            System.out.println("Phone Number input cannot be empty");
            return phoneNumbers;
        }
        // Regular expression to find phone numbers
        String phoneRegex = "(?:\\+\\d{1,3}\\s*)?(?:\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4})";

        Pattern pattern = Pattern.compile(phoneRegex);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            phoneNumbers.add(matcher.group());
        }

        return phoneNumbers;
    }
}
