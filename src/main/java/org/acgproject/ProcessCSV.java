package org.acgproject;

import org.apache.commons.csv.CSVRecord;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class ProcessCSV {

    public static String removeAcentos(String str) {
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }

}
