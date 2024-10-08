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
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.util.LinkedList;

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
        courseStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status")); // Connect to the status method

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
    private void handleBackButton(ActionEvent event) throws IOException{

        FXMLLoader loader = new FXMLLoader(getClass().getResource("menu.fxml"));
        Parent menuParent = loader.load();

        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();

        Scene menuScene = new Scene(menuParent);
        stage.setScene(menuScene);
        stage.show();;
    }
    private void mergeCourses(Course course1, Course course2) {
        if (course2 == null) return;

        int totalStudents = course1.getNumStudents() + course2.getNumStudents();

        if (totalStudents <= 50) {
            course1.setNumStudents(totalStudents);

            mergeStudentLists(course1.getCode(), course2.getCode());

            courseList.deleteCourse(course2.getCode());

            updateCourseList();
        } else {
            showAlert("Merge Not Possible", "The total number of students exceeds 50. Merging not allowed.");
        }
    }

    private void mergeStudentLists(String course1Code, String course2Code) {
        String course1FilePath = course1Code + "_students.txt";
        String course2FilePath = course2Code + "_students.txt";

        try {
            LinkedList<String> studentsToMerge = new LinkedList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(course2FilePath))) {
                String student;
                while ((student = br.readLine()) != null) {
                    studentsToMerge.add(student);
                }
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(course1FilePath, true))) {
                for (String student : studentsToMerge) {
                    bw.write(student);
                    bw.newLine();
                }
            }

            new File(course2FilePath).delete();

        } catch (IOException e) {
            showAlert("Error", "An error occurred while merging student lists.");
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