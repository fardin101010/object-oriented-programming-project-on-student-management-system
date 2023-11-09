package project1;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

class User {
    private String username;
    private String password;

    public User(String name, String pass) {
        this.username = name;
        this.password = pass;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String name) {
        this.username = name;
    }

    public void setPassword(String pass) {
        this.password = pass;
    }
}

class Student {
    private String username;
    private String batch;
    private ArrayList<String> subjects;

    public Student(String name, String batch, ArrayList<String> subjects) {
        this.username = name;
        this.batch = batch;
        this.subjects = subjects;
    }

    public String getUsername() {
        return username;
    }

    public String getBatch() {
        return batch;
    }

    public ArrayList<String> getSubjects() {
        return subjects;
    }
}

class UserManager {
    private final ArrayList<User> users = new ArrayList<>();
    private final Map<String, Student> students = new HashMap<>();
    private User loggedInUser; 
    private final String programFolder = "E://ourproject";
    private final User admin;

   public UserManager() {
    admin = new User("admin", "admin"); 
    users.add(admin); 
    loadUsers();
}


    private void loadUsers() {
        File[] userFolders = new File(programFolder).listFiles();
        if (userFolders != null) {
            for (File folder : userFolders) {
                String username = folder.getName();
                File passwordFile = new File(folder, "password.txt");
                if (passwordFile.exists()) {
                    try (Scanner passwordScanner = new Scanner(passwordFile)) {
                        String password = passwordScanner.nextLine();
                        User user = new User(username, password);
                        users.add(user);
                    } catch (IOException e) {
                        System.out.println("Failed to read the password file for student: " + username);
                    }
                }
                File studentInfoFile = new File(folder, "studentInfo.txt");
                if (studentInfoFile.exists()) {
                    try (Scanner studentInfoScanner = new Scanner(studentInfoFile)) {
                        String batch = studentInfoScanner.nextLine();
                        ArrayList<String> subjects = new ArrayList<>();
                        while (studentInfoScanner.hasNextLine()) {
                            subjects.add(studentInfoScanner.nextLine());
                        }
                        Student student = new Student(username, batch, subjects);
                        students.put(username, student);
                    } catch (IOException e) {
                        System.out.println("Failed to read student information for student: " + username);
                    }
                }
            }
        }
    }

    private void saveUser(User user) {
        String username = user.getUsername();
        String password = user.getPassword();

        File userFolder = new File(programFolder, username);
        userFolder.mkdirs();

        try (FileWriter passwordFile = new FileWriter(new File(userFolder, "password.txt"))) {
            passwordFile.write(password);
        } catch (IOException e) {
            System.out.println("Failed to create student folder or write the password file.");
            e.printStackTrace();
        }
    }

    private void saveStudentInfo(String username, String batch, ArrayList<String> subjects) {
        File userFolder = new File(programFolder, username);
        try (FileWriter studentInfoFile = new FileWriter(new File(userFolder, "studentInfo.txt"))) {
            studentInfoFile.write(batch + "\n");
            for (String subject : subjects) {
                studentInfoFile.write(subject + "\n");
            }
        } catch (IOException e) {
            System.out.println("Failed to write student information for student: " + username);
            e.printStackTrace();
        }
    }

   public void registerStudent(String username, String password, String batch, ArrayList<String> subjects) {
    if (loggedInUser != null && loggedInUser.getUsername().equals("admin")) {
        User newUser = new User(username, password);
        users.add(newUser);
        saveUser(newUser);

        Student newStudent = new Student(username, batch, subjects);
        students.put(username, newStudent);
        saveStudentInfo(username, batch, subjects);

        System.out.println("\t\tStudent Registered Successfully.....");
    } else {
        System.out.println("\t\tYou must log in as the admin to register a student.");
    }
}


    public User login(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                if (user.getUsername().equals(admin.getUsername())) {
                    System.out.println("\t\tAdmin Login Successful.");
                } else {
                    System.out.println("\t\tStudent Login Successful.");
                }
                loggedInUser = user; 
                return user;
            }
        }
        System.out.println("\t\tLogin Failed.");
        return null;
    }

    public void logout() {
        loggedInUser = null; 
        System.out.println("\t\tLogged out successfully.");
    }

    public void showStudents() {
        if (students.isEmpty()) {
            System.out.println("\t\tNo students registered.");
        } else {
            System.out.println("\t\tList of Registered Students:");
            for (Student student : students.values()) {
                System.out.println("\t\tUsername: " + student.getUsername());
                System.out.println("\t\tBatch: " + student.getBatch());
                System.out.println("\t\tSubjects: " + student.getSubjects());
                System.out.println();
            }
        }
    }

    public Student getStudent(String username) {
        return students.get(username);
    }

    public void searchStudent(String searchUsername) {
        Student student = students.get(searchUsername);
        if (student != null) {
            System.out.println("\t\tStudent Details:");
            System.out.println("\t\tUsername: " + student.getUsername());
            System.out.println("\t\tBatch: " + student.getBatch());
            System.out.println("\t\tSubjects: " + student.getSubjects());
        } else {
            System.out.println("\t\tStudent not found.");
        }
    }

   public void updateStudent(String username, String newBatch, ArrayList<String> newSubjects, User loggedInUser) {
    if (loggedInUser != null && loggedInUser.getUsername().equals("admin")) {
        Student studentToUpdate = students.get(username);
        if (studentToUpdate != null) {
            studentToUpdate = new Student(username, newBatch, newSubjects);
            students.put(username, studentToUpdate);
            saveStudentInfo(username, newBatch, newSubjects);
            System.out.println("\t\tStudent Updated Successfully.....");
        } else {
            System.out.println("\t\tStudent not found.");
        }
    } else {
        System.out.println("\t\tYou must log in as the admin to update the profile.");
    }
}



    public void deleteStudent(String deleteUsername, User loggedInUser) {
    if (loggedInUser != null && loggedInUser.getUsername().equals("admin")) {
        students.remove(deleteUsername);
        File userFolder = new File(programFolder, deleteUsername);
        if (userFolder.exists()) {
            deleteFolder(userFolder);
            System.out.println("\t\tStudent Deleted Successfully.....");
        } else {
            System.out.println("\t\tStudent folder not found.");
        }
    } else {
        System.out.println("\t\tYou must log in as the admin to delete the profile.");
    }
}


    private void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteFolder(file);
                } else {
                    file.delete();
                }
            }
        }
        folder.delete();
    }
}



public class Project1 {
    public static void main(String[] args) {
        UserManager userManager = new UserManager();
        int op;
        char choice;
        User loggedInUser = null; 
        do {
            System.out.println("\n\n\t\t1. Register Student");
            System.out.println("\t\t2. Login");
            System.out.println("\t\t3. Show Student List");
            System.out.println("\t\t4. Search Student");
            if (loggedInUser != null) {
                System.out.println("\t\t5. Update Student");
                System.out.println("\t\t6. Delete Student");
                System.out.println("\t\t7. Logout");
            }
            System.out.println("\t\t8. Exit");
            System.out.print("\t\tEnter Your Choice: ");

            Scanner scanner = new Scanner(System.in);
            op = scanner.nextInt();

            try {
                switch (op) {
                    case 1:
                        System.out.print("\t\tEnter Student Name: ");
                        String username = scanner.next();
                        System.out.print("\t\tEnter Password: ");
                        String password = scanner.next();
                        System.out.print("\t\tEnter Batch (e.g., 2018-19): ");
                        String batch = scanner.next();

                        ArrayList<String> subjects = new ArrayList<>();
                        for (int i = 0; i < 4; i++) {
                            System.out.print("\t\tEnter Subject " + (i + 1) + ": ");
                            String subject = scanner.next();
                            subjects.add(subject);
                        }

                        userManager.registerStudent(username, password, batch, subjects);
                        break;
                    case 2:
                        System.out.print("\t\tEnter Username: ");
                        String loginUsername = scanner.next();
                        System.out.print("\t\tEnter Password: ");
                        String loginPassword = scanner.next();
                        loggedInUser = userManager.login(loginUsername, loginPassword);
                        break;
                    case 3:
                        userManager.showStudents();
                        break;
                    case 4:
                        System.out.print("\t\tEnter Student Name to Search: ");
                        String searchUsername = scanner.next();
                        userManager.searchStudent(searchUsername);
                        break;
                    case 5:
    if (loggedInUser != null) {
        System.out.print("\t\tEnter Student Name to Update: ");
        String updateUsername = scanner.next();
        if (loggedInUser.getUsername().equals("admin")) {
            
            System.out.print("\t\tEnter New Batch: ");
            String newBatch = scanner.next();
            ArrayList<String> newSubjects = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                System.out.print("\t\tEnter New Subject " + (i + 1) + ": ");
                String newSubject = scanner.next();
                newSubjects.add(newSubject);
            }
            userManager.updateStudent(updateUsername, newBatch, newSubjects, loggedInUser);
        } else if (loggedInUser.getUsername().equals(updateUsername)) {
            
            System.out.print("\t\tEnter New Batch: ");
            String newBatch = scanner.next();
            ArrayList<String> newSubjects = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                System.out.print("\t\tEnter New Subject " + (i + 1) + ": ");
                String newSubject = scanner.next();
                newSubjects.add(newSubject);
            }
            userManager.updateStudent(updateUsername, newBatch, newSubjects, loggedInUser);
        } else {
            System.out.println("Permission denied. You can only update your own profile.");
        }
    } else {
        System.out.println("Please log in to perform this operation.");
    }
    break;

              case 6:
    if (loggedInUser != null) {
        if (loggedInUser.getUsername().equals("admin")) {
            System.out.print("\t\tEnter Student Name to Delete: ");
            String deleteUsername = scanner.next();
            userManager.deleteStudent(deleteUsername, loggedInUser);
        } else if (userManager.getStudent(loggedInUser.getUsername()) != null) {
            System.out.println("Permission denied. Only admin can delete student records.");
        }
    } else {
        System.out.println("Please log in to perform this operation.");
    }
    break;
       case 7:
                        if (loggedInUser != null) {
                            userManager.logout();
                            loggedInUser = null;
                        } else {
                            System.out.println("\t\tYou are not logged in.");
                        }
                        break;
                    case 8:
                        System.exit(0);
                    default:
                        System.out.println("Invalid choice. Please select a valid option.");
                        break;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            System.out.print("\t\tDo You Want To Continue [Yes/ No] ? : ");
            choice = scanner.next().charAt(0);
        } while (choice == 'y' || choice == 'Y');
    }

    
}