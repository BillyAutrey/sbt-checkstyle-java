version := "0.1"
scalaVersion := "2.12.1"

val checkstyleTest = taskKey[Unit]("Executes checkstyle")
checkstyleTest := {
    val result = checkstyle.value
    assert (result == checkstyleVersion.value)
}