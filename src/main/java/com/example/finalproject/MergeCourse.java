package com.example.finalproject;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.text.Text;

import java.io.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MergeCourse {
    @FXML
    private Text courseTitleText;
    @FXML
    private TableView<Course> courseTable;
    @FXML
    private TableColumn<Course, String> courseCodeColumn;
    @FXML
    private TableColumn<Course, Integer> numStudentsColumn;
    @FXML
    private TableColumn<Course, String> courseStatusColumn;

    private CourseList courseList;
    private String courseTitle;

    public MergeCourse() {
        courseList = new CourseList();
    }

    @FXML
    public void initialize() {
        courseCodeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        numStudentsColumn.setCellValueFactory(new PropertyValueFactory<>("numStudents"));
        courseStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadCourses();

        courseStatusColumn.setCellFactory(column -> new TableCell<Course, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    setStyle("Mergable".equals(status) ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
                }
            }
        });

        courseTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                onCourseCodeDoubleClicked();
            }
        });

        courseTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                updateCourseTitleText(newSelection);
            }
        });
    }

    public void loadCourses() {
        LinkedList<Course> filteredCourses = new LinkedList<>();
        for (Course course : courseList.getCourses()) {
            if (course.getTitle().equals(courseTitle)) {
                filteredCourses.add(course);
            }
        }
        courseTable.setItems(FXCollections.observableArrayList(filteredCourses));
    }

    public void setCourseTitle(String title) {
        this.courseTitle = title;
        loadCourses();

        Course matchingCourse = null;
        for (Course course : courseList.getCourses()) {
            if (course.getTitle().equals(title)) {
                matchingCourse = course;
                break;
            }
        }

        if (matchingCourse != null) {
            courseTitleText.setText("Courses for: " + title + " - " + matchingCourse.getDescription());
        } else {
            courseTitleText.setText("Courses for: " + title + " - Description not found");
        }
    }

    private void onCourseCodeDoubleClicked() {
        Course selectedCourse = courseTable.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {
            LinkedList<Course> mergeableCourses = getMergeableCourses(selectedCourse);
            showMergeableCoursesDialog(mergeableCourses, selectedCourse);
        }
    }

    private void updateCourseTitleText(Course course) {
        String titleText = course.getCode() + " - " + course.getTitle() + " (" + course.getNumStudents() + " Students)";
        courseTitleText.setText(titleText);
    }

    private LinkedList<Course> getMergeableCourses(Course course) {
        LinkedList<Course> mergeableCourses = new LinkedList<>();
        for (Course c : courseList.getCourses()) {
            if (!c.getCode().equals(course.getCode()) && c.getNumStudents() < 50 && c.getTitle().equals(course.getTitle())) {
                mergeableCourses.add(c);
            }
        }
        return mergeableCourses;
    }

    private void showMergeableCoursesDialog(LinkedList<Course> mergeableCourses, Course selectedCourse) {
        if (mergeableCourses.isEmpty()) {
            showAlert("No Mergeable Courses", "There are no courses available for merging with " + selectedCourse.getCode());
            return;
        }

        ListView<String> listView = new ListView<>();
        for (Course course : mergeableCourses) {
            listView.getItems().add(course.getCode());
        }

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Select a Course to Merge");
        dialog.setHeaderText("Select a mergeable course with " + selectedCourse.getCode());

        ButtonType mergeButtonType = new ButtonType("Merge", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(mergeButtonType, ButtonType.CANCEL);

        VBox dialogPane = new VBox(10);
        dialogPane.getChildren().add(listView);
        dialog.getDialogPane().setContent(dialogPane);

        dialog.setResultConverter(button -> {
            if (button == mergeButtonType) {
                return listView.getSelectionModel().getSelectedItem();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(courseCode -> {
            Course courseToMerge = mergeableCourses.stream()
                    .filter(c -> c.getCode().equals(courseCode))
                    .findFirst()
                    .orElse(null);
            mergeCourses(selectedCourse, courseToMerge);
        });
    }

    @FXML
    private Button backButton;

    @FXML
    private void handleBackButton(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("menu.fxml"));
        Parent menuParent = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        Scene menuScene = new Scene(menuParent);
        stage.setScene(menuScene);
        stage.show();
    }

    @FXML
    private void importStudents(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        // Open file chooser for two files
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog((Stage) ((Node) event.getSource()).getScene().getWindow());

        // Check if two files are selected
        if (selectedFiles == null || selectedFiles.size() != 2) {
            showAlert("File Selection Error", "Please select exactly 2 student list files.");
            return;
        }

        // Merge student lists from the selected files
        mergeImportedStudents(selectedFiles);
    }

    private void mergeImportedStudents(List<File> selectedFiles) {
        LinkedList<String> studentsToMerge = new LinkedList<>();
        long startTime = System.nanoTime();  // Start measuring time in nanoseconds
        double duration = 0;  // Declare duration as double

        try {
            for (File file : selectedFiles) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String student;

                while ((student = reader.readLine()) != null) {
                    if (!studentsToMerge.contains(student)) { // Prevent duplicates
                        studentsToMerge.add(student);
                    }
                }
                reader.close();
            }

            // Sort the merged student list using Collections.sort
            Collections.sort(studentsToMerge, (s1, s2) -> {
                String[] parts1 = s1.split(","); // Split first student
                String[] parts2 = s2.split(","); // Split second student

                // Check if both students have enough parts for last name access
                if (parts1.length < 3 || parts2.length < 3) {
                    return 0; // Keep original order if any student is malformed
                }

                String lastName1 = parts1[2]; // Extract last name from 3rd position
                String lastName2 = parts2[2];
                return lastName1.compareToIgnoreCase(lastName2);
            });

            long endTime = System.nanoTime();  // End measuring time in nanoseconds
            duration = (endTime - startTime) / 1_000_000.0;  // Convert to milliseconds as a double

            // Directly show new course dialog after reading and deduping
            showNewCourseDialog(studentsToMerge, duration);
        } catch (IOException e) {
            showAlert("Error", "An error occurred while importing student lists.");
            e.printStackTrace();
        }

        showAlert("Import Completed", String.format("Student lists imported successfully.\nCPU Time: %.3f milliseconds", duration));
    }



    private void showNewCourseDialog(LinkedList<String> studentsToMerge, double duration) {
        Dialog<Course> dialog = new Dialog<>();
        dialog.setTitle("New Course Details");
        dialog.setHeaderText(String.format("CPU Time: %.3f milliseconds\nEnter the new course code and description.", duration));

        // Create fields for course code and description
        TextField courseCodeField = new TextField();
        TextField courseDescriptionField = new TextField();

        VBox dialogPane = new VBox(10);
        dialogPane.getChildren().addAll(new Label("Course Code:"), courseCodeField, new Label("Course Description:"), courseDescriptionField);
        dialog.getDialogPane().setContent(dialogPane);

        ButtonType mergeButtonType = new ButtonType("Create Course", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(mergeButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == mergeButtonType) {
                return new Course(courseCodeField.getText(), courseTitle, studentsToMerge.size(), courseDescriptionField.getText());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(newCourse -> {
            // Add the new course to the course list
            courseList.addCourse(newCourse);
            updateCourseList();

            // Write the new student list to a file
            writeNewCourseFile(newCourse.getCode(), studentsToMerge);
            showAlert("Course Created", "New course created successfully with code: " + newCourse.getCode());
        });
    }



    private void writeNewCourseFile(String courseCode, LinkedList<String> students) {
        String filePath = courseCode + "_students.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String student : students) {
                writer.write(student);
                writer.newLine();
            }
        } catch (IOException e) {
            showAlert("Error", "An error occurred while creating the new student file.");
            e.printStackTrace();
        }

        // Update the courses.txt file
        updateCoursesFile(courseCode, students.size());
    }

    private void updateCoursesFile(String courseCode, int numStudents) {
        String filePath = "courses.txt"; // Path to your existing courses file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            // Check if the course code already exists
            if (!courseList.courseExists(courseCode)) {
                String courseEntry = courseCode + ", " + courseTitle + ", " + numStudents + ", Course Description";
                writer.write(courseEntry);
                writer.newLine();
            } else {
                System.out.println("Course with code " + courseCode + " already exists. Not added to file.");
            }
        } catch (IOException e) {
            showAlert("Error", "An error occurred while updating the courses file.");
            e.printStackTrace();
        }
    }


    private void mergeCourses(Course course1, Course course2) {
        if (course2 == null) return;

        // Start measuring CPU time
        long startTime = System.nanoTime();

        int totalStudents = course1.getNumStudents() + course2.getNumStudents();

        if (totalStudents <= 50) {
            // Merge student lists
            mergeStudentLists(course1.getCode(), course2.getCode());

            // Update the number of students in course1
            course1.setNumStudents(totalStudents);

            // Optionally delete the second course after merging
            courseList.deleteCourse(course2.getCode());
            updateCourseList();
        } else {
            showAlert("Merge Not Possible", "The total number of students exceeds 50. Merging not allowed.");
        }

        // End measuring CPU time
        long endTime = System.nanoTime();
        double duration = (endTime - startTime) / 1_000_000.0; // Convert to milliseconds

        // Show dialog with CPU time
        showAlert("Merge Completed", String.format("The courses were successfully merged.\nCPU Time: %.3f milliseconds", duration));
    }


    private void mergeStudentLists(String course1Code, String course2Code) {
        String course1FilePath = course1Code + "_students.txt";
        String course2FilePath = course2Code + "_students.txt";

        try {
            // Using BufferedReader for efficient reading
            BufferedReader br1 = new BufferedReader(new FileReader(course1FilePath));
            BufferedReader br2 = new BufferedReader(new FileReader(course2FilePath));

            LinkedList<String> studentsToMerge = new LinkedList<>();
            String student;

            // Read students from course 1
            while ((student = br1.readLine()) != null) {
                studentsToMerge.add(student);
            }

            // Read students from course 2
            while ((student = br2.readLine()) != null) {
                studentsToMerge.add(student);
            }

            br1.close();
            br2.close();

            // Sort the merged student list based on last names
            studentsToMerge.sort((s1, s2) -> {
                String lastName1 = s1.split(",")[2]; // Extract last name from 3rd position
                String lastName2 = s2.split(",")[2];
                return lastName1.compareToIgnoreCase(lastName2);
            });

            // Write sorted students back to course1 file
            BufferedWriter bw = new BufferedWriter(new FileWriter(course1FilePath));
            for (String mergedStudent : studentsToMerge) {
                bw.write(mergedStudent);
                bw.newLine();
            }
            bw.close();

            // Delete course2 student file after merging
            new File(course2FilePath).delete();

        } catch (IOException e) {
            showAlert("Error", "An error occurred while merging and sorting student lists.");
            e.printStackTrace();
        }
    }




    private void updateCourseList() {
        loadCourses();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
