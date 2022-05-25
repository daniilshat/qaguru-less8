package ru.daniilshat;

import com.codeborne.pdftest.PDF;
import com.codeborne.pdftest.matchers.ContainsExactText;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import static org.hamcrest.MatcherAssert.assertThat;

public class SelenideZipTest {

    ClassLoader cl = SelenideZipTest.class.getClassLoader();

    @Test
    void ParsingXlsZipTest() throws Exception {
        ZipFile zf = new ZipFile(new File("src/test/resources/file_example_XLS_10.zip"));
        try (ZipInputStream is = new ZipInputStream(cl.getResourceAsStream("file_example_XLS_10.zip"))) {
            ZipEntry entry;
            while ((entry = is.getNextEntry()) != null) {
                org.assertj.core.api.Assertions.assertThat(entry.getName()).isEqualTo("file_example_XLS_10.xls");
                try (InputStream inputStream = zf.getInputStream(entry)) {
                    XLS xls = new XLS(inputStream);
                    String value = xls.excel.getSheetAt(0).getRow(1).getCell(1).getStringCellValue();
                    org.assertj.core.api.Assertions.assertThat(value).contains("Dulce");
                }
            }
        }
    }

    @Test
    void zipParsingPDFTest() throws Exception {
        ZipFile zf = new ZipFile(new File("src/test/resources/test-pdf.zip"));
        try (ZipInputStream is = new ZipInputStream(cl.getResourceAsStream("test-pdf.zip"))) {
            ZipEntry entry;
            while ((entry = is.getNextEntry()) != null) {
                org.assertj.core.api.Assertions.assertThat(entry.getName()).isEqualTo("test-pdf.pdf");
                try (InputStream inputStream = zf.getInputStream(entry)) {
                    PDF pdf = new PDF(inputStream);
                    Assertions.assertThat(pdf.numberOfPages).isEqualTo(1);
                    assertThat(pdf, new ContainsExactText("PDF Test File"));
                }
            }
        }
    }

    @Test
    void zipParsingCsvTest() throws Exception {
        ZipFile zf = new ZipFile(new File("src/test/resources/wholesale-trade-survey-dec-2021-quarter-csv.zip"));
        try (ZipInputStream is = new ZipInputStream(cl.getResourceAsStream("wholesale-trade-survey-dec-2021-quarter-csv.zip"))) {
            ZipEntry entry;
            while ((entry = is.getNextEntry()) != null) {
                org.assertj.core.api.Assertions.assertThat(entry.getName()).isEqualTo("wholesale-trade-survey-dec-2021-quarter-csv.csv");
                try (InputStream inputStream = zf.getInputStream(entry)) {
                    try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                        List<String[]> content = reader.readAll();
                        org.assertj.core.api.Assertions.assertThat(content).contains(new String[] {
                                "Series_reference",
                                "Period",
                                "Data_value",
                                "Suppressed",
                                "STATUS",
                                "UNITS",
                                "Magnitude",
                                "Subject",
                                "Group",
                                "Series_title_1",
                                "Series_title_2",
                                "Series_title_3",
                                "Series_title_4",
                                "Series_title_5"});
                    }
                }
            }
        }
    }

}
