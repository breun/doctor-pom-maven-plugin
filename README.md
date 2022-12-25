# Doctor Pom Maven Plugin

Dr. Pom is an experienced [Maven](https://maven.apache.org) user,
and you can use his Maven plugin to detect errors and make sure that your project uses Dr. Pom's best practices.
By default the plugin will fail a build whenever it detects an issue,
but you can also configure the plugin to log warnings only.

## Requirements

* A Maven project which uses Java 8 or higher

## Getting started

Add the plugin in the build section of the root `pom.xml` file of your project to run the plugin's `find-issues` goal during the `validate` phase of your project.:

```
<properties>
  <doctor-pom-maven-plugin.version>1.0.0-SNAPSHOT<doctor-pom-maven-plugin.version>
</properties>

<build>
  <pluginManagement>
    <plugins>
      <plugin>
        <groupId>nl.breun.doctor_pom</groupId>
        <artifactId>doctor-pom-maven-plugin</artifactId>
        <version>${doctor-pom-maven-plugin.version}</version>
      </plugin>
    </plugins>
  </pluginManagement>
  <plugins>
    <plugin>
      <groupId>nl.breun.doctor_pom</groupId>
      <artifactId>doctor-pom-maven-plugin</artifactId>
      <executions>
        <execution>
          <id>find-issues</id>
          <phase>validate</phase>
          <goals>
            <goal>find-issues</goal>
          </goals>
        </execution>
      </executions>
    </plugin>
  </plugins>
</build>
```