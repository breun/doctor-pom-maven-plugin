package nl.breun.doctor_pom;

import java.io.File;
import java.util.List;

public class DoctorPomException extends RuntimeException {

    private File pomFile;

    private List<Issue> issues;

    public DoctorPomException(File pomFile, List<Issue> issues, String message) {
        super(message);
        this.pomFile = pomFile;
        this.issues = issues;
    }

    public File getPomFile() {
        return pomFile;
    }

    public List<Issue> getIssues() {
        return issues;
    }
}
