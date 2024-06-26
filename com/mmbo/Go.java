package com.mmbo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;

import java.awt.GridLayout;

public class Go {
    public static final boolean DEBUG_MODE = false;

    public static void main(String[] args) {
        File[] files;
        String filePath = "logOutput.txt";
        // Step 1: Choose a QAP input file
        JOptionPane.showMessageDialog(null,
                "This program is written for demonstrating the performance of the MBO algorithm." +
                        "\nEven though MBO algorithm can be applied to any combinatorial optimization problem, " +
                        "\nthis demo is specifically designed for solving QAP instances." +
                        "\nTherefore the input format should be in the form of QAPLIB standards." +
                        "\nWe also provided some sample QAPLIB instances within the package." +
                        "\nNow please specify a QAP input file.",
                "Multimeme Migrating Bird Optimization for QAP", JOptionPane.INFORMATION_MESSAGE);

        JFileChooser fileChooser = new JFileChooser(new File("./data_files/"));
        fileChooser.setMultiSelectionEnabled(true);
        int returnVal = fileChooser.showOpenDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            files = fileChooser.getSelectedFiles();

            // Step 2: Input parameters
            JPanel paramPanel = new JPanel(new GridLayout(0, 1));
            paramPanel.add(new JLabel("n - number of initial solutions(51):"));
            JTextField nField = new JTextField("51", 5);
            paramPanel.add(nField);
            paramPanel.add(new JLabel("\nk - number of neighbor solutions to be considered(3):"));
            JTextField kField = new JTextField("3", 5);
            paramPanel.add(kField);
            paramPanel.add(new JLabel("\nm - number of tours(10):"));
            JTextField mField = new JTextField("10", 5);
            paramPanel.add(mField);
            paramPanel
                    .add(new JLabel("\nx - number of neighbor solutions to be shared with the next solution(1):"));
            JTextField xField = new JTextField("1", 5);
            paramPanel.add(xField);

            int paramResult = JOptionPane.showConfirmDialog(null, paramPanel,
                    "Please enter the parameters of the algorithm", JOptionPane.OK_CANCEL_OPTION);

            if (paramResult == JOptionPane.OK_OPTION) {

                // Step 3: Run the algorithm
                for (int j = 0; j < files.length; j++) {
                    String file = files[j].getAbsolutePath();

                    try {
                        int numberOfInitialSolutions = Integer.parseInt(nField.getText());
                        int numberOfNeighborSolutions = Integer.parseInt(kField.getText());
                        int numberOfTours = Integer.parseInt(mField.getText());
                        int numberOfSharedWithNextSolution = Integer.parseInt(xField.getText());

                        // Create a FileOutputStream to write to the file
                        FileOutputStream fileOutputStream = new FileOutputStream(filePath);
                        // Create a PrintStream that writes to the FileOutputStream
                        //PrintStream printStream = new PrintStream(fileOutputStream);
                        // Save the current System.out
                        PrintStream originalSystemOut = System.out;
                        // Set System.out to the new PrintStream
                        // System.setOut(printStream);

                        // Regex deseni
                        String regexPattern = ".*/data_files/(.*?)\\.dat";

                        // Regex eşleştirmeyi gerçekleştir
                        Pattern pattern = Pattern.compile(regexPattern);
                        Matcher matcher = pattern.matcher(file);

                        // Eşleşme varsa yazdır
                        if (matcher.find()) {
                            // Kaynak dosyayı Path nesnesine dönüştür
                            Path sourcePath = new File("results.xlsx").toPath();

                            // Specify the file path where you want to save the Excel file
                            String resultsPathMBO = "results_" + matcher.group(1) + "_MBO.xlsx";
                            String resultsPathMMMBOv1 = "results_" + matcher.group(1) + "_MMMBOv1.xlsx";
                            String resultsPathMMMBOv2 = "results_" + matcher.group(1) + "_MMMBOv2.xlsx";

                            // Dosyayı kopyala
                            try {
                                Files.copy(sourcePath, new File(resultsPathMBO).toPath(),
                                        StandardCopyOption.REPLACE_EXISTING);
                                Files.copy(sourcePath, new File(resultsPathMMMBOv1).toPath(),
                                        StandardCopyOption.REPLACE_EXISTING);
                                Files.copy(sourcePath, new File(resultsPathMMMBOv2).toPath(),
                                        StandardCopyOption.REPLACE_EXISTING);
                                System.out.println("Dosyalar oluşturuldu ");
                            } catch (IOException e) {
                                e.printStackTrace();
                                fileOutputStream.close();
                                throw new RuntimeException("Dosya oluşturulamadı");
                            }

                            // BirdsAlgorithm instantiation here with the obtained parameters.
                            for (int i = 0; i < 31; i++) {
                                new BirdsAlgorithm(numberOfInitialSolutions, numberOfNeighborSolutions,
                                        numberOfTours, numberOfSharedWithNextSolution, AlgorithmMode.MBO, 1, 1, 1, file,
                                        resultsPathMBO);
                                new BirdsAlgorithm(numberOfInitialSolutions, numberOfNeighborSolutions,
                                        numberOfTours, numberOfSharedWithNextSolution, AlgorithmMode.MMMBOv1, 1, 1, 1,
                                        file, resultsPathMMMBOv1);
                                new BirdsAlgorithm(numberOfInitialSolutions, numberOfNeighborSolutions,
                                        numberOfTours, numberOfSharedWithNextSolution, AlgorithmMode.MMMBOv2, 1, 1, 1,
                                        file, resultsPathMMMBOv2);
                            }
                            // Restore the original System.out
                            System.setOut(originalSystemOut);
                            // Close the fileOutputStream
                            fileOutputStream.close();
                        } else {
                            fileOutputStream.close();
                            throw new RuntimeException(
                                    "Dosya yolu geçersiz: Dosya yolu bu formatta olmalıdır /xxxx.dat");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Wrong parameter format", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }
}
