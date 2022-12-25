package nl.breun.doctor_pom;

import java.util.List;

/**
 * An issue detector detects a particular kind of issue that can occur in a Maven Project Object Model.
 */
public interface IssueDetector {

    /**
     * Detects if a particular issue exists in a Maven Project Object Model (POM).
     * If so, a result is returned which indicates the detected issue instances.
     *
     * @param projectObjectModel a Project Object Model (POM)
     * @return the result of executing the detection logic, containing any issue instances that were detected in the Project Object Model (POM)
     */
    List<Issue> detectIssues(ProjectObjectModel projectObjectModel);
}
