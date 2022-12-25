package nl.breun.doctor_pom;

import java.util.Objects;

/**
 * An issue found in a Maven Project Object Model (POM).
 */
public class Issue {

    private final String message;

    public Issue(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Issue issue = (Issue) o;
        return message.equals(issue.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message);
    }

    @Override
    public String toString() {
        return "Issue{" +
                "message='" + message + '\'' +
                '}';
    }
}
